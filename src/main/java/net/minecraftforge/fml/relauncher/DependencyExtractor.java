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

import LZMA.LzmaException;
import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.versioning.ComparableVersion;
import net.minecraftforge.fml.relauncher.ModListHelper.JsonModList;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

public class DependencyExtractor
{
    public static final String EXTRACTED_MODS_LIST = "extracted_mods.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Attributes.Name MOD_CONTAINS_DEPS = new Attributes.Name("ContainedDeps");
    private static final String MAVEN_ARTIFACT_NAME = "Maven-Artifact";
    private static final Attributes.Name MAVEN_ARTIFACT = new Attributes.Name(MAVEN_ARTIFACT_NAME);
    private static final Attributes.Name MOD_SIDE = CoreModManager.MODSIDE;
    private static final String MOD_SIDE_NAME = MOD_SIDE.toString();
    private static final List<String> skipContainedDeps = Arrays.asList(System.getProperty("fml.skipContainedDeps", "").split(","));
    /**
     * It's a little weird, but it should yield the best performance
     * The map is used like a set (similar to how HashSet is really just a wrapped HashMap)
     */
    public static final Map<Artifact, Artifact> extractedArtifacts = new HashMap<>();
    public static int extractedDeps = 0;

    public static void inspect(File mcDir, File baseModsDir, File versionedModsDir, String repositoryRoot)
    {
        FMLLog.log.debug("Inspecting mod directory for dependency extraction candidates");
        File desiredModListFile = new File(versionedModsDir, EXTRACTED_MODS_LIST);
        JsonModList modList = prepareModList(mcDir.toPath(), repositoryRoot, desiredModListFile);
        File repository = ModListHelper.getRepoRoot(mcDir, modList);
        // Completely wipe the list of extracted artifacts, always override the list
        extractedArtifacts.clear();
        extractedDeps = 0;
        Map<Artifact, Artifact> oldArtifacts = modList.modRef.stream().map(Artifact::new).collect(Collectors.toMap(Function.identity(), Function.identity()));

        for (File mod : CoreModManager.listFiles((d, name) -> name.endsWith(".jar"), baseModsDir, versionedModsDir))
        {
            try (JarFile jar = new JarFile(mod))
            {
                if (jar.getManifest() == null)
                {
                    // Not an extraction candidate
                    continue;
                }
                extractContainedDepJars(jar, baseModsDir, versionedModsDir, repository);
            }
            catch (IOException ioe)
            {
                FMLLog.log.error("Unable to read the jar file {} - ignoring", mod.getName(), ioe);
            }
        }

        // If any dependencies were extracted or the count in artifacts changed compared to the previous state, save the list file
        if (extractedDeps > 0 || oldArtifacts.size() != extractedArtifacts.size())
        {
            FMLLog.log.debug("Finished extracting dependencies, at least {} dependencies were added or updated", extractedDeps);
            FMLLog.log.debug("Since dependencies were extracted, the list of extracted mods has to be written to disk again");
            modList.modRef = extractedArtifacts.values().stream().map(Artifact::toGradleNotation).collect(Collectors.toList());
            try
            {
                versionedModsDir.mkdirs();
                Files.write(GSON.toJson(modList), desiredModListFile, Charsets.UTF_8);
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

    public static JsonModList prepareModList(Path mcDir, @Nullable String repositoryRoot, File desiredModListFile)
    {
        JsonModList list = new JsonModList();
        // Default option, when all else fails
        list.repositoryRoot = "./libraries/";
        // This is mainly for testing purposes
        if (repositoryRoot != null)
        {
            list.repositoryRoot = repositoryRoot;
        }
        else
        {
            String lzmaLocation = LzmaException.class.getResource("").toString();
            int idx = lzmaLocation.indexOf('!');
            // Only get the location from actual JAR files
            if (lzmaLocation.startsWith("jar:file:") && idx != -1)
            {
                try
                {
                    String fileName = URLDecoder.decode(lzmaLocation.substring("jar:file:".length(), idx), "UTF-8");
                    File lzmaFile = new File(fileName).getAbsoluteFile();
                    // This is somewhat unreliable, but we should never be in an environment without Maven structure in the libs folder
                    // Basically searches for the artifact part of "lzma:lzma:{version}"
                    // Limit the depth in order to not search indefinitely, maximum depth of 3 because that'd result in the Gradle Maven Cache base directory in a dev environment
                    int depth = 0;
                    while (!lzmaFile.getName().equals("lzma") && depth < 3)
                    {
                        lzmaFile = lzmaFile.getParentFile();
                        depth++;
                    }
                    // Double parent because we go artifact -> group -> libraries
                    Path libsDir = lzmaFile.getParentFile().getParentFile().toPath();
                    if (libsDir.startsWith(mcDir))
                    {
                        list.repositoryRoot = mcDir.relativize(libsDir).toString();
                    }
                    else
                    {
                        list.repositoryRoot = "absolute:" + libsDir.toAbsolutePath().toString();
                    }
                }
                catch (UnsupportedEncodingException e)
                {
                    FMLLog.log.error("Failed to decode JAR file URL", e);
                }
            }
        }
        list.modRef = new ArrayList<>();
        // Treat path as relative to the MC dir
        list.version = 2;
        if (!desiredModListFile.exists())
        {
            return list;
        }
        String json;
        try
        {
            json = Files.asCharSource(desiredModListFile, Charsets.UTF_8).read();
            list = GSON.fromJson(json, JsonModList.class);
        }
        catch (IOException e)
        {
            FMLLog.log.warn("Failed to read extracted mod list json file {}, overriding with empty one", desiredModListFile, e);
        }
        catch (JsonSyntaxException e)
        {
            FMLLog.log.warn("Failed to parse existing extracted mod list json file {}, overriding with empty one", desiredModListFile, e);
        }
        return list;
    }

    private static void extractContainedDepJars(JarFile jar, File baseModsDir, File versionedModsDir, File repository) throws IOException
    {
        Attributes manifest = jar.getManifest().getMainAttributes();
        String modSide = manifest.containsKey(MOD_SIDE) ? manifest.getValue(MOD_SIDE) : "BOTH";
        // Skip extraction if the JAR isn't intended for this side
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
            File targetFile = determineTargetFile(jar, baseModsDir, versionedModsDir, repository, dep, depEndName);
            if (targetFile == null) continue;

            FMLLog.log.debug("Extracting ContainedDep {} from {} to {}", dep, jar.getName(), targetFile.getCanonicalPath());
            try
            {
                Files.createParentDirs(targetFile);
                try (FileOutputStream targetOutputStream = new FileOutputStream(targetFile);
                     InputStream jarInputStream = jar.getInputStream(jarEntry))
                {
                    ByteStreams.copy(jarInputStream, targetOutputStream);
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

    @Nullable
    public static File determineTargetFile(JarFile jar, File baseModsDir, File versionedModsDir, File repository, String dep, String depEndName) throws IOException
    {
        File versionedTarget = new File(versionedModsDir, depEndName);
        File baseDirTarget = new File(baseModsDir, depEndName);
        if (versionedTarget.exists())
        {
            FMLLog.log.debug("Found existing ContainsDep extracted to {}, skipping extraction", versionedTarget.getCanonicalPath());
            return null;
        }
        else if (baseDirTarget.exists())
        {
            FMLLog.log.debug("Found ContainsDep in main mods directory at {}, skipping extraction", baseDirTarget.getCanonicalPath());
            return null;
        }
        File targetFile = versionedTarget;
        String artifactData = null;
        String modSide = "BOTH";
        JarEntry metaEntry = jar.getJarEntry(dep + ".meta");
        // Prioritize a separate metadata file, which basically gets interpreted like an external manifest, useful for binaries you have no control over
        if (metaEntry != null)
        {
            try (InputStream metaStream = jar.getInputStream(metaEntry))
            {
                Properties metadata = new Properties();
                metadata.load(metaStream);
                if (metadata.containsKey(MAVEN_ARTIFACT_NAME))
                {
                    artifactData = metadata.getProperty(MAVEN_ARTIFACT_NAME);
                }
                if (metadata.containsKey(MOD_SIDE_NAME))
                {
                    modSide = metadata.getProperty(MOD_SIDE_NAME);
                }
            }
            catch (IOException e)
            {
                FMLLog.log.warn("Could not load metadata file for ContainedDep {} in {}, please report to the mod author!", dep, jar.getName());
            }
        }
        // Hypothetically, we might not always be dealing with JARs, so better make sure
        if (depEndName.endsWith(".jar"))
        {
            try (JarInputStream jarInputStream = new JarInputStream(jar.getInputStream(jar.getEntry(dep)));)
            {
                // Unfortunately we need to open the jar stream twice
                Manifest manifest = jarInputStream.getManifest();
                if (manifest != null)
                {
                    Attributes attributes = manifest.getMainAttributes();
                    if (artifactData == null && attributes.containsKey(MAVEN_ARTIFACT))
                    {
                        artifactData = attributes.getValue(MAVEN_ARTIFACT);
                    }
                    if (modSide.equals("BOTH") && attributes.containsKey(MOD_SIDE))
                    {
                        modSide = attributes.getValue(MOD_SIDE);
                    }
                }
            }
        }
        // Skip extraction if the file is not for this side
        if (!("BOTH".equals(modSide) || FMLLaunchHandler.side.name().equals(modSide))) return null;
        if (artifactData != null)
        {
            Artifact artifact = new Artifact(artifactData);
            if (addArtifact(artifact, repository))
            {
                FMLLog.log.debug("Found artifact {} in {}, extracting to repository at {}", artifact, jar.getName(), repository.getCanonicalPath());
                targetFile = artifact.toFile(repository);
            }
            else
            {
                return null;
            }
        }
        else
        {
            FMLLog.log.warn("Could not find artifact information inside ContainedDep {} in {}, please report to the mod author!", dep, jar.getName());
        }
        return targetFile;
    }

    private static boolean addArtifact(Artifact artifact, File repository)
    {
        Artifact existing = extractedArtifacts.get(artifact);
        if (existing != null)
        {
            if (existing.getParsedVersion().compareTo(artifact.getParsedVersion()) < 0)
            {
                FMLLog.log.debug("Found extracted artifact {} in repository already which has an older version, replacing it", existing);
                existing.version = artifact.version;
            }
            else if (existing.toFile(repository).exists())
            {
                // Don't need to extract if newer or equal artifact was already extracted
                FMLLog.log.debug("Found extracted artifact {} in repository already with equal or newer version, skipping extraction", existing);
                return false;
            }
        }
        else
        {
            extractedArtifacts.put(artifact, artifact);
        }
        return true;
    }

    public static final class Artifact
    {
        final String[] parts;
        final String group;
        final String name;
        String version;
        final String classifier;

        public Artifact(String raw)
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

        public String toGradleNotation()
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

        public String toFileName()
        {
            return ModListHelper.convertArtifactToFileName(parts);
        }

        public File toFile(File relativeTo)
        {
            return new File(relativeTo, toFileName());
        }

        public ComparableVersion getParsedVersion()
        {
            return new ComparableVersion(version);
        }

        public boolean equalsAll(Artifact artifact)
        {
            return Objects.equals(group, artifact.group) &&
                Objects.equals(name, artifact.name) &&
                Objects.equals(version, artifact.version) &&
                Objects.equals(classifier, artifact.classifier);
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
