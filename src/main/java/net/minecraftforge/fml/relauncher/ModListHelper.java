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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.FMLLog;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import javax.annotation.Nullable;

public class ModListHelper {
    public static class JsonModList {
        public int version = 1;
        public String repositoryRoot;
        public List<String> modRef;
        public String parentList;
    }
    private static File mcDirectory;
    private static Set<File> visitedFiles = Sets.newHashSet();
    public static final Map<String,File> additionalMods = Maps.newLinkedHashMap();
    public static void parseModList(File minecraftDirectory)
    {
        FMLLog.log.debug("Attempting to load commandline specified mods, relative to {}", minecraftDirectory.getAbsolutePath());
        mcDirectory = minecraftDirectory;
        @SuppressWarnings("unchecked")
        Map<String,String> args = (Map<String, String>) Launch.blackboard.get("launchArgs");
        String listFile = args.get("--modListFile");
        if (listFile != null)
        {
            parseListFile(listFile);
        }
        String extraMods = args.get("--mods");
        if (extraMods != null)
        {
            String[] split = extraMods.split(",");
            for (String modFile : split)
            {
                tryAddFile(modFile, null, modFile);
            }
        }

        String[] extras = new String[]
        {
            "mods/mod_list.json",
            "mods/" + FMLInjectionData.mccversion + "/mod_list.json"
        };

        for (String extra : extras)
        {
            if ((new File(mcDirectory, extra)).exists())
                parseListFile(extra);
        }

    }
    private static void parseListFile(String listFile) {
        File f;
        try
        {
            f = parsePath(listFile, mcDirectory);
        } catch (IOException e2)
        {
            FMLLog.log.info(FMLLog.log.getMessageFactory().newMessage("Unable to canonicalize path {} relative to {}", listFile, mcDirectory.getAbsolutePath()), e2);
            return;
        }
        if (!f.exists())
        {
            FMLLog.log.info("Failed to find modList file {}", f.getAbsolutePath());
            return;
        }
        if (visitedFiles.contains(f))
        {
            FMLLog.log.fatal("There appears to be a loop in the modListFile hierarchy. You shouldn't do this!");
            throw new RuntimeException("Loop detected, impossible to load modlistfile");
        }
        String json;
        try {
            json = Files.asCharSource(f, StandardCharsets.UTF_8).read();
        } catch (IOException e1) {
            FMLLog.log.info(FMLLog.log.getMessageFactory().newMessage("Failed to read modList json file {}.", listFile), e1);
            return;
        }
        Gson gsonParser = new Gson();
        JsonModList modList;
        try {
            modList = gsonParser.fromJson(json, JsonModList.class);
        } catch (JsonSyntaxException e) {
            FMLLog.log.info(FMLLog.log.getMessageFactory().newMessage("Failed to parse modList json file {}.", listFile), e);
            return;
        }
        visitedFiles.add(f);
        // We visit parents before children, so the additionalMods list is sorted from parent to child
        if (modList.parentList != null)
        {
            parseListFile(modList.parentList);
        }
        File repoRoot = getRepoRoot(mcDirectory, modList);
        if (repoRoot == null) return;
        if (!repoRoot.exists())
        {
            FMLLog.log.info("Failed to find the specified repository root {}", modList.repositoryRoot);
            return;
        }

        for (String s : modList.modRef)
        {
            String[] parts = s.split(":");
            tryAddFile(convertArtifactToFileName(parts), repoRoot, convertArtifactToGenericName(parts));
        }
    }

    public static File getRepoRoot(File mcDirectory, JsonModList modList)
    {
        File repoRoot;
        try
        {
            // We need to be able to distinguish absolute from relative files for dependency extraction
            if (modList.version == 1)
            {
                repoRoot = new File(modList.repositoryRoot);
            }
            else
            {
                repoRoot = parsePath(modList.repositoryRoot, mcDirectory);
            }
            repoRoot = repoRoot.getCanonicalFile();
        }
        catch (IOException e)
        {
            FMLLog.log.info(FMLLog.log.getMessageFactory().newMessage("Unable to canonicalize path {} relative to {}", modList.repositoryRoot, mcDirectory.getAbsolutePath()), e);
            return null;
        }
        return repoRoot;
    }

    public static File parsePath(String path, File mcDirectory) throws IOException
    {
        if (path.startsWith("absolute:"))
            return new File(path.substring(9)).getCanonicalFile();
        else
            return new File(mcDirectory, path).getCanonicalFile();
    }

    private static void tryAddFile(String modFileName, @Nullable File repoRoot, String descriptor) {
        File modFile = repoRoot != null ? new File(repoRoot,modFileName) : new File(mcDirectory, modFileName);
        if (!modFile.exists())
        {
            FMLLog.log.info("Failed to find mod file {} ({})", descriptor, modFile.getAbsolutePath());
        }
        else
        {
            FMLLog.log.debug("Adding {} ({}) to the mod list", descriptor, modFile.getAbsolutePath());
            additionalMods.put(descriptor, modFile);
        }
    }

    public static String convertArtifactToFileName(String[] parts)
    {
        StringBuilder fileName = new StringBuilder();
        fileName.append(parts[0].replace('.', File.separatorChar));
        fileName.append(File.separatorChar);
        fileName.append(parts[1]).append(File.separatorChar);
        fileName.append(parts[2]).append(File.separatorChar);
        fileName.append(parts[1]).append('-').append(parts[2]);
        if (parts.length == 4)
        {
            fileName.append('-').append(parts[3]);
        }
        fileName.append(".jar");
        return fileName.toString();
    }

    private static String convertArtifactToGenericName(String[] parts)
    {
        StringBuilder genericName = new StringBuilder();
        genericName.append(parts[0]);
        genericName.append(":").append(parts[1]);
        if (parts.length == 4)
        {
            genericName.append(":").append(parts[3]);
        }
        return genericName.toString();
    }
}
