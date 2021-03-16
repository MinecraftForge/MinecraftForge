/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.fml.relauncher.libraries;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.minecraftforge.versions.mcp.MCPVersion;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import net.minecraftforge.fml.loading.FMLEnvironment;

public class LibraryManager
{
    private static final Logger LOGGER = LogManager.getLogger();

    public static final boolean DISABLE_EXTERNAL_MANIFEST = Boolean.parseBoolean(System.getProperty("forge.disable_external_manifest", "false"));
    public static final boolean ENABLE_AUTO_MOD_MOVEMENT = Boolean.parseBoolean(System.getProperty("forge.enable_auto_mod_movement", "false"));
    private static final String LIBRARY_DIRECTORY_OVERRIDE = System.getProperty("forge.lib_folder", null);
    private static final List<String> skipContainedDeps = Arrays.asList(System.getProperty("fml.skipContainedDeps","").split(",")); //TODO: Is this used by anyone in the real world? TODO: Remove in 1.13.
    private static final FilenameFilter MOD_FILENAME_FILTER  = (dir, name) -> name.endsWith(".jar") || name.endsWith(".zip"); //TODO: Disable support for zip in 1.13
    private static final Comparator<File> FILE_NAME_SORTER_INSENSITVE = (o1, o2) -> o1.getName().toLowerCase(Locale.ENGLISH).compareTo(o2.getName().toLowerCase(Locale.ENGLISH));

    public static final Attributes.Name MODSIDE = new Attributes.Name("ModSide");
    private static final Attributes.Name MODCONTAINSDEPS = new Attributes.Name("ContainedDeps");
    private static final Attributes.Name MAVEN_ARTIFACT = new Attributes.Name("Maven-Artifact");
    private static final Attributes.Name TIMESTAMP = new Attributes.Name("Timestamp");
    private static final Attributes.Name MD5 = new Attributes.Name("MD5");
    private static Repository libraries_dir = null;
    private static Set<File> processed = new HashSet<File>();

    public static void setup(File minecraftHome)
    {
        File libDir = findLibraryFolder(minecraftHome);
        LOGGER.debug("Determined Minecraft Libraries Root: {}", libDir);
        Repository old = Repository.replace(libDir, "libraries");
        if (old != null)
            LOGGER.debug("  Overwriting Previous: {}", old);
        libraries_dir = Repository.get("libraries");

        File mods = new File(minecraftHome, "mods");
        File mods_ver = new File(mods, MCPVersion.getMCVersion());

        ModList memory = null;
        if (!ENABLE_AUTO_MOD_MOVEMENT)
        {
            Repository repo = new LinkRepository(new File(mods, "memory_repo"));
            memory = new MemoryModList(repo);
            ModList.cache.put("MEMORY", memory);
            Repository.cache.put("MEMORY", repo);
        }

        for (File dir : new File[]{mods, mods_ver})
            cleanDirectory(dir, ENABLE_AUTO_MOD_MOVEMENT ? ModList.create(new File(dir, "mod_list.json"), minecraftHome) : memory, mods_ver, mods);

        for (ModList list : ModList.getKnownLists(minecraftHome))
        {
            Repository repo = list.getRepository() == null ? libraries_dir : list.getRepository();
            List<Artifact> artifacts = list.getArtifacts();
            // extractPacked adds artifacts to the list. As such, we can't use an Iterator to traverse it.
            for (int i = 0; i < artifacts.size(); i++)
            {
                Artifact artifact = artifacts.get(i);
                Artifact resolved = repo.resolve(artifact);
                if (resolved != null)
                {
                    File target = repo.getFile(resolved.getPath());
                    if (target.exists())
                        extractPacked(target, list, mods_ver, mods);
                }
            }
        }
    }

    private static File findLibraryFolder(File minecraftHome)
    {
        if (LIBRARY_DIRECTORY_OVERRIDE != null)
        {
            LOGGER.error("System variable set to override Library Directory: {}", LIBRARY_DIRECTORY_OVERRIDE);
            return new File(LIBRARY_DIRECTORY_OVERRIDE);
        }

        CodeSource source = ArtifactVersion.class.getProtectionDomain().getCodeSource();
        if (source == null)
        {
            LOGGER.error("Unable to determine codesource for {}. Using default libraries directory.", ArtifactVersion.class.getName());
            return new File(minecraftHome, "libraries");
        }

        try
        {
            File apache = new File(source.getLocation().toURI());
            if (apache.isFile())
                apache = apache.getParentFile(); //Get to a directory, this *should* always be the case...
            apache = apache.getParentFile(); //Skip the version folder. In case we ever update the version, I don't want to edit this code again.
            String comp = apache.getAbsolutePath().toLowerCase(Locale.ENGLISH).replace('\\', '/');
            if (!comp.endsWith("/"))
                comp += '/';

            if (!comp.endsWith("/org/apache/maven/maven-artifact/"))
            {
                LOGGER.error("Apache Maven library folder was not in the format expected. Using default libraries directory.");
                LOGGER.error("Full: {}", new File(source.getLocation().toURI()));
                LOGGER.error("Trimmed: {}", comp);
                return new File(minecraftHome, "libraries");
            }
            //     maven-artifact  /maven          /apache         /org            /libraries
            return apache          .getParentFile().getParentFile().getParentFile().getParentFile();
        }
        catch (URISyntaxException e)
        {
            LOGGER.error(LOGGER.getMessageFactory().newMessage("Unable to determine file for {}. Using default libraries directory.", ArtifactVersion.class.getName()), e);
        }

        return new File(minecraftHome, "libraries"); //Everything else failed, return the default.
    }

    private static void cleanDirectory(File dir, ModList modlist, File... modDirs)
    {
        if (!dir.exists())
            return;

        LOGGER.debug("Cleaning up mods folder: {}", dir);
        for (File file : dir.listFiles(f -> f.isFile() && f.getName().endsWith(".jar")))
        {
            Pair<Artifact, byte[]> ret = extractPacked(file, modlist, modDirs);
            if (ret != null)
            {
                Artifact artifact = ret.getLeft();
                Repository repo = modlist.getRepository() == null ? libraries_dir : modlist.getRepository();
                File moved = repo.archive(artifact, file, ret.getRight());
                processed.add(moved);
            }
        }

        try
        {
            if (modlist.changed())
                modlist.save();
        }
        catch (IOException e)
        {
            LOGGER.error(LOGGER.getMessageFactory().newMessage("Error updating modlist file {}", modlist.getName()), e);
        }
    }

    private static Pair<Artifact, byte[]> extractPacked(File file, ModList modlist, File... modDirs)
    {
        if (processed.contains(file))
        {
            LOGGER.debug("File already proccessed {}, Skipping", file.getAbsolutePath());
            return null;
        }
        JarFile jar = null;
        try
        {
            jar = new JarFile(file);
            LOGGER.debug("Examining file: {}", file.getName());
            processed.add(file);
            return extractPacked(jar, modlist, modDirs);
        }
        catch (IOException ioe)
        {
            LOGGER.error("Unable to read the jar file {} - ignoring", file.getName(), ioe);
        }
        finally
        {
            try {
                if (jar != null)
                    jar.close();
            } catch (IOException e) {}
        }
        return null;
    }
    private static Pair<Artifact, byte[]> extractPacked(JarFile jar, ModList modlist, File... modDirs) throws IOException
    {
        Attributes attrs;
        if (jar.getManifest() == null)
            return null;

        JarEntry manifest_entry = jar.getJarEntry(JarFile.MANIFEST_NAME);
        if (manifest_entry == null)
            manifest_entry = jar.stream().filter(e -> JarFile.MANIFEST_NAME.equals(e.getName().toUpperCase(Locale.ENGLISH))).findFirst().get(); //We know that getManifest returned non-null so we know there is *some* entry that matches the manifest file. So we dont need to empty check.

        attrs = jar.getManifest().getMainAttributes();

        String modSide = attrs.getValue(LibraryManager.MODSIDE);
        if (modSide != null && !"BOTH".equals(modSide) && !FMLEnvironment.dist.name().equals(modSide))
            return null;

        if (attrs.containsKey(MODCONTAINSDEPS))
        {
            for (String dep : attrs.getValue(MODCONTAINSDEPS).split(" "))
            {
                if (!dep.endsWith(".jar"))
                {
                    LOGGER.error("Contained Dep is not a jar file: {}", dep);
                    throw new IllegalStateException("Invalid contained dep, Must be jar: " + dep);
                }

                if (jar.getJarEntry(dep) == null && jar.getJarEntry("META-INF/libraries/" + dep) != null)
                    dep = "META-INF/libraries/" + dep;

                JarEntry depEntry = jar.getJarEntry(dep);
                if (depEntry == null)
                {
                    LOGGER.error("Contained Dep is not in the jar: {}", dep);
                    throw new IllegalStateException("Invalid contained dep, Missing from jar: " + dep);
                }

                String depEndName = new File(dep).getName(); // extract last part of name
                if (skipContainedDeps.contains(dep) || skipContainedDeps.contains(depEndName))
                {
                    LOGGER.error("Skipping dep at request: {}", dep);
                    continue;
                }

                Attributes meta = null;
                byte[] data = null;
                byte[] manifest_data = null;

                JarEntry metaEntry = jar.getJarEntry(dep + ".meta");
                if (metaEntry != null)
                {
                    manifest_data = readAll(jar.getInputStream(metaEntry));
                    meta = new Manifest(new ByteArrayInputStream(manifest_data)).getMainAttributes();
                }
                else
                {
                    data = readAll(jar.getInputStream(depEntry));
                    try (ZipInputStream zi = new ZipInputStream(new ByteArrayInputStream(data))) //We use zip input stream directly, as the current Oracle implementation of JarInputStream only works when the manifest is the First/Second entry in the jar...
                    {
                        ZipEntry ze = null;
                        while ((ze = zi.getNextEntry()) != null)
                        {
                            if (ze.getName().equalsIgnoreCase(JarFile.MANIFEST_NAME))
                            {
                                manifest_data = readAll(zi);
                                meta = new Manifest(new ByteArrayInputStream(manifest_data)).getMainAttributes();
                                break;
                            }
                        }
                    }
                }

                if (meta == null || !meta.containsKey(MAVEN_ARTIFACT)) //Ugh I really don't want to do backwards compatibility here, I want to force modders to provide information... TODO: Remove in 1.13?
                {
                    boolean found = false;
                    for (File dir : modDirs)
                    {
                        File target = new File(dir, depEndName);
                        if (target.exists())
                        {
                            LOGGER.debug("Found existing ContainDep extracted to {}, skipping extraction", target.getCanonicalPath());
                            found = true;
                        }
                    }
                    if (!found)
                    {
                        File target = new File(modDirs[0], depEndName);
                        LOGGER.debug("Extracting ContainedDep {} from {} to {}", dep, jar.getName(), target.getCanonicalPath());
                        try
                        {
                            Files.createParentDirs(target);
                            try
                            (
                                FileOutputStream out = new FileOutputStream(target);
                                InputStream in = data == null ? jar.getInputStream(depEntry) : new ByteArrayInputStream(data)
                            )
                            {
                                ByteStreams.copy(in, out);
                            }
                            LOGGER.debug("Extracted ContainedDep {} from {} to {}", dep, jar.getName(), target.getCanonicalPath());
                            extractPacked(target, modlist, modDirs);
                        }
                        catch (IOException e)
                        {
                            LOGGER.error("An error occurred extracting dependency", e);
                        }
                    }
                }
                else
                {
                    try
                    {
                        Artifact artifact = readArtifact(modlist.getRepository(), meta);
                        File target = artifact.getFile();
                        if (target.exists())
                        {
                            LOGGER.debug("Found existing ContainedDep {}({}) from {} extracted to {}, skipping extraction", dep, artifact.toString(), target.getCanonicalPath(), jar.getName());
                            if (!ENABLE_AUTO_MOD_MOVEMENT)
                            {
                                Pair<?, ?> child = extractPacked(target, modlist, modDirs); //If we're not building a real list we have to re-build the dep list every run. So search down.
                                if (child == null && metaEntry != null) //External meta with no internal name... If there is a internal name, we trust that that name is the correct one.
                                {
                                    modlist.add(artifact);
                                }
                            }
                        }
                        else
                        {
                            LOGGER.debug("Extracting ContainedDep {}({}) from {} to {}", dep, artifact.toString(), jar.getName(), target.getCanonicalPath());
                            Files.createParentDirs(target);
                            try
                            (
                                FileOutputStream out = new FileOutputStream(target);
                                InputStream in = data == null ? jar.getInputStream(depEntry) : new ByteArrayInputStream(data)
                            )
                            {
                                ByteStreams.copy(in, out);
                            }
                            LOGGER.debug("Extracted ContainedDep {}({}) from {} to {}", dep, artifact.toString(), jar.getName(), target.getCanonicalPath());

                            if (artifact.isSnapshot())
                            {
                                SnapshotJson json = SnapshotJson.create(artifact.getSnapshotMeta());
                                json.add(new SnapshotJson.Entry(artifact.getTimestamp(), meta.getValue(MD5)));
                                json.write(artifact.getSnapshotMeta());
                            }

                            if (!DISABLE_EXTERNAL_MANIFEST)
                            {
                                File meta_target = new File(target.getAbsolutePath() + ".meta");
                                Files.write(manifest_data, meta_target);
                            }
                            Pair<?, ?> child = extractPacked(target, modlist, modDirs);
                            if (child == null && metaEntry != null) //External meta with no internal name... If there is a internal name, we trust that that name is the correct one.
                            {
                                modlist.add(artifact);
                            }
                        }
                    }
                    catch (NumberFormatException nfe)
                    {
                        LOGGER.error(LOGGER.getMessageFactory().newMessage("An error occurred extracting dependency. Invalid Timestamp: {}", meta.getValue(TIMESTAMP)), nfe);
                    }
                    catch (IOException e)
                    {
                        LOGGER.error("An error occurred extracting dependency", e);
                    }
                }
            }
        }

        if (attrs.containsKey(MAVEN_ARTIFACT))
        {
            Artifact artifact = readArtifact(modlist.getRepository(), attrs);
            modlist.add(artifact);
            return Pair.of(artifact, readAll(jar.getInputStream(manifest_entry)));
        }
        return null;
    }

    private static Artifact readArtifact(Repository repo, Attributes meta)
    {
        String timestamp = meta.getValue(TIMESTAMP);
        if (timestamp != null)
            timestamp = SnapshotJson.TIMESTAMP.format(new Date(Long.parseLong(timestamp)));

        return new Artifact(repo, meta.getValue(MAVEN_ARTIFACT), timestamp);
    }

    private static byte[] readAll(InputStream in) throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int read = -1;
        byte[] data = new byte[1024 * 16];

        while ((read = in.read(data, 0, data.length)) != -1)
            out.write(data, 0, read);

        out.flush();
        return out.toByteArray();
    }

    public static List<Artifact> flattenLists(File mcDir)
    {
        List<Artifact> merged = new ArrayList<>();
        for (ModList list : ModList.getBasicLists(mcDir))
        {
            for (Artifact art : list.flatten())
            {
                Optional<Artifact> old = merged.stream().filter(art::matchesID).findFirst();
                if (!old.isPresent())
                {
                    merged.add(art);
                }
                else if (old.get().getVersion().compareTo(art.getVersion()) < 0)
                {
                    merged.add(merged.indexOf(old.get()), art);
                    merged.remove(old.get());
                }
            }
        }
        return merged;
    }

    public static List<File> gatherLegacyCanidates(File mcDir)
    {
        List<File> list = new ArrayList<>();

        Map<String,String> args = Collections.emptyMap(); // TODO Launch args - do we need this? (Map<String, String>)Launcher.INSTANCE.blackboard().get("launchArgs");
        String extraMods = args.get("--mods");
        if (extraMods != null)
        {
            LOGGER.info("Found mods from the command line:");
            for (String mod : extraMods.split(","))
            {
                File file = new File(mcDir, mod);
                if (!file.exists())
                {
                    LOGGER.info("  Failed to find mod file {} ({})", mod, file.getAbsolutePath());
                }
                else if (!list.contains(file))
                {
                    LOGGER.debug("  Adding {} ({}) to the mod list", mod, file.getAbsolutePath());
                    list.add(file);
                }
                else if (!list.contains(file))
                {
                    LOGGER.debug("  Duplicte command line mod detected {} ({})", mod, file.getAbsolutePath());
                }
            }
        }

        for (String dir : new String[]{"mods", "mods" + File.separatorChar + MCPVersion.getMCVersion()})
        {
            File base = new File(mcDir, dir);
            if (!base.isDirectory() || !base.exists())
                continue;

            LOGGER.info("Searching {} for mods", base.getAbsolutePath());
            for (File f : base.listFiles(MOD_FILENAME_FILTER))
            {
                if (!list.contains(f))
                {
                    LOGGER.debug("  Adding {} to the mod list", f.getName());
                    list.add(f);
                }
            }
        }

        ModList memory = ModList.cache.get("MEMORY");
        if (!ENABLE_AUTO_MOD_MOVEMENT && memory != null && memory.getRepository() != null)
            memory.getRepository().filterLegacy(list);

        list.sort(FILE_NAME_SORTER_INSENSITVE);
        return list;
    }

    public static Repository getDefaultRepo()
    {
        return libraries_dir;
    }
}
