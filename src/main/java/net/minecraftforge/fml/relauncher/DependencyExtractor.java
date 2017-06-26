/*
 * Minecraft Forge
 * Copyright (c) 2016.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.relauncher;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.ModListHelper.JsonModList;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

public class DependencyExtractor
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Attributes.Name MOD_CONTAINS_DEPS = new Attributes.Name("ContainedDeps");
    private static final Attributes.Name MAVEN_ARTIFACT = new Attributes.Name("MavenArtifact");
    private static final List<String> skipContainedDeps = Arrays.asList(System.getProperty("fml.skipContainedDeps", "").split(","));
    private static int extractedDeps = 0;

    public static void inspect(File baseModsDir, File versionedModsDir)
    {
        FMLLog.log.debug("Inspecting mod directory for dependency extraction candidates");
        File modListFile = new File(baseModsDir, "mod_list.json");
        JsonModList modList = prepareModList(modListFile);
        File repository = new File(modList.repositoryRoot);
        // It looks a little weird, but it should yield the best performance
        Map<Artifact, Artifact> artifacts = modList.modRef.stream().map(Artifact::new).collect(Collectors.toMap(a -> a, a -> a));
        extractedDeps = 0;

        for (File mod : CoreModManager.listFiles((d, name) -> name.endsWith(".jar"), baseModsDir, versionedModsDir))
        {
            JarFile jar = null;
            try
            {
                jar = new JarFile(mod);
                if (jar.getManifest() == null)
                {
                    // Not an extraction candidate
                    continue;
                }
                extractContainedDepJars(jar, baseModsDir, versionedModsDir, repository, artifacts);
            }
            catch (IOException ioe)
            {
                FMLLog.log.error("Unable to read the jar file {} - ignoring", mod.getName(), ioe);
            }
            finally
            {
                if (jar != null)
                {
                    try
                    {
                        jar.close();
                    }
                    catch (IOException e)
                    {
                        // Noise
                    }
                }
            }
        }

        if (extractedDeps > 0)
        {
            FMLLog.log.debug("Finished extracting dependencies, at least {} dependencies were added or updated", extractedDeps);
            FMLLog.log.debug("Since dependencies were extracted, the mod list has to be written to disk again");
            modList.modRef = artifacts.values().stream().map(Artifact::toGradleNotation).collect(Collectors.toList());
            try
            {
                Files.write(GSON.toJson(modList), modListFile, Charsets.UTF_8);
                FMLLog.log.debug("Successfully updated mod list");
            }
            catch (IOException e)
            {
                FMLLog.log.error("Failed to write updated mod list", e);
            }
        }
        else
        {
            FMLLog.log.debug("No or only erroring dependencies to be extracted found");
        }
    }

    private static JsonModList prepareModList(File modListFile)
    {
        JsonModList list = new JsonModList();
        list.repositoryRoot = "./modrepo";
        list.modRef = new ArrayList<>();
        if (!modListFile.exists())
        {
            return list;
        }
        String json;
        try
        {
            json = Files.asCharSource(modListFile, Charsets.UTF_8).read();
            list = GSON.fromJson(json, JsonModList.class);
        }
        catch (IOException e)
        {
            FMLLog.log.warn(FMLLog.log.getMessageFactory().newMessage("Failed to read mod list json file {}, overriding with empty one", modListFile), e);
        }
        catch (JsonSyntaxException e)
        {
            FMLLog.log.warn(FMLLog.log.getMessageFactory().newMessage("Failed to parse existing mod list json file {}, overriding with empty one", modListFile), e);
        }
        return list;
    }

    private static void extractContainedDepJars(JarFile jar, File baseModsDir, File versionedModsDir, File repository, Map<Artifact, Artifact> artifacts) throws IOException
    {
        Attributes manifest = jar.getManifest().getMainAttributes();
        String modSide = manifest.containsKey(CoreModManager.MODSIDE) ? manifest.getValue(CoreModManager.MODSIDE) : "BOTH";
        if (!("BOTH".equals(modSide) || FMLLaunchHandler.side.name().equals(modSide))) return;
        if (!manifest.containsKey(MOD_CONTAINS_DEPS)) return;

        String deps = manifest.getValue(MOD_CONTAINS_DEPS);
        String[] depList = deps.split(" ");
        for (String dep : depList)
        {
            String depEndName = new File(dep).getName(); // extract last part of name
            if (skipContainedDeps.contains(dep) || skipContainedDeps.contains(depEndName))
            {
                FMLLog.log.error("Skipping dep at request: {}", dep);
                continue;
            }
            final JarEntry jarEntry = jar.getJarEntry(dep);
            if (jarEntry == null)
            {
                FMLLog.log.error("Found invalid ContainsDeps declaration {} in {}", dep, jar.getName());
                continue;
            }
            File versionedTarget = new File(versionedModsDir, depEndName);
            File baseDirTarget = new File(baseModsDir, depEndName);
            if (versionedTarget.exists())
            {
                FMLLog.log.debug("Found existing ContainsDep extracted to {}, skipping extraction", versionedTarget.getCanonicalPath());
                continue;
            }
            else if (baseDirTarget.exists())
            {
                FMLLog.log.debug("Found ContainsDep in main mods directory at {}, skipping extraction", baseDirTarget.getCanonicalPath());
                continue;
            }
            File targetFile = versionedTarget;
            boolean shouldExtract = true;
            // Hypothetically, contained deps might be some other file than a jar
            if (depEndName.endsWith(".jar"))
            {
                JarInputStream jarInputStream = null;
                try
                {
                    // Unfortunately we need to open the jar stream twice
                    jarInputStream = new JarInputStream(jar.getInputStream(jarEntry));
                    Manifest containedManifest = jarInputStream.getManifest();
                    if (containedManifest != null && containedManifest.getMainAttributes().containsKey(MAVEN_ARTIFACT))
                    {
                        Artifact artifact = new Artifact(containedManifest.getMainAttributes().getValue(MAVEN_ARTIFACT));
                        if (addArtifact(artifacts, artifact, repository))
                        shouldExtract = addArtifact(artifacts, artifact, repository);
                        if (shouldExtract)
                        {
                            FMLLog.log.debug("Found artifact {} in {}, extracting to repository at {}", artifact, jar.getName(), repository.getCanonicalPath());
                            targetFile = artifact.toFile(repository);
                        }
                    }
                    else
                    {
                        FMLLog.log.warn("Could not find artifact information inside ContainedDep {} in {}, please report to the mod author!", dep, jar.getName());
                    }
                }
                finally
                {
                    IOUtils.closeQuietly(jarInputStream);
                }
            }
            if (!shouldExtract)
            {
                continue;
            }

            FMLLog.log.debug("Extracting ContainedDep {} from {} to {}", dep, jar.getName(), targetFile.getCanonicalPath());
            try
            {
                Files.createParentDirs(targetFile);
                FileOutputStream targetOutputStream = null;
                InputStream jarInputStream = null;
                try
                {
                    targetOutputStream = new FileOutputStream(targetFile);
                    jarInputStream = jar.getInputStream(jarEntry);
                    ByteStreams.copy(jarInputStream, targetOutputStream);
                }
                finally
                {
                    IOUtils.closeQuietly(targetOutputStream);
                    IOUtils.closeQuietly(jarInputStream);
                }
                extractedDeps++;
                FMLLog.log.debug("Extracted ContainedDep {} from {} to {}", dep, jar.getName(), targetFile.getCanonicalPath());
            }
            catch (IOException e)
            {
                FMLLog.log.error("An error occurred extracting dependency", e);
            }
        }
    }

    private static boolean addArtifact(Map<Artifact, Artifact> artifacts, Artifact artifact, File repository)
    {
        Artifact existing = artifacts.get(artifact);
        if (existing != null)
        {
            if (existing.version.equals(artifact.version))
            {
                if (!artifact.toFile(repository).exists())
                {
                    return true;
                }
                // Don't need to extract existing artifacts
                FMLLog.log.debug("Found extracted artifact {} in repository already, skipping extraction", artifact);
                return false;
            }
            FMLLog.log.warn("Replacing existing mod list entry {} with differing extracted version {}", existing, artifact.version);
            existing.version = artifact.version;
        }
        else
        {
            artifacts.put(artifact, artifact);
        }
        return true;
    }

    private static final class Artifact
    {
        final String[] parts;
        final String group;
        final String name;
        String version;
        final String classifier;

        Artifact(String raw)
        {
            parts = raw.split(":");
            group = parts[0];
            name = parts[1];
            version = parts[2];
            if (parts.length == 4)
            {
                classifier = parts[3];
            }
            else
            {
                classifier = null;
            }
        }

        String toGradleNotation()
        {
            StringBuilder builder = new StringBuilder();
            builder.append(group);
            builder.append(':');
            builder.append(name);
            builder.append(':');
            builder.append(version);
            if (classifier != null)
            {
                builder.append(':');
                builder.append(classifier);
            }
            return builder.toString();
        }

        String toFileName()
        {
            return ModListHelper.convertArtifactToFileName(parts);
        }

        File toFile(File relativeTo)
        {
            return new File(relativeTo, toFileName());
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(group, name, classifier);
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Artifact artifact = (Artifact) o;
            return Objects.equals(group, artifact.group) &&
                Objects.equals(name, artifact.name) &&
                Objects.equals(classifier, artifact.classifier);
        }

        @Override
        public String toString()
        {
            return toGradleNotation();
        }
    }
}
