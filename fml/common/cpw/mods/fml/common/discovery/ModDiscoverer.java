/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package cpw.mods.fml.common.discovery;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.LoaderException;
import cpw.mods.fml.common.ModClassLoader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.relauncher.CoreModManager;

public class ModDiscoverer
{
    private static Pattern zipJar = Pattern.compile("(.+).(zip|jar)$");

    private List<ModCandidate> candidates = Lists.newArrayList();

    private ASMDataTable dataTable = new ASMDataTable();

    private List<File> nonModLibs = Lists.newArrayList();

    public void findClasspathMods(ModClassLoader modClassLoader)
    {
        List<String> knownLibraries = ImmutableList.<String>builder()
                .addAll(modClassLoader.getDefaultLibraries())
                .addAll(CoreModManager.getLoadedCoremods())
                .build();
        File[] minecraftSources = modClassLoader.getParentSources();
        if (minecraftSources.length == 1 && minecraftSources[0].isFile())
        {
            FMLLog.fine("Minecraft is a file at %s, loading", minecraftSources[0].getAbsolutePath());
            candidates.add(new ModCandidate(minecraftSources[0], minecraftSources[0], ContainerType.JAR, true, true));
        }
        else
        {
            for (int i = 0; i < minecraftSources.length; i++)
            {
                if (minecraftSources[i].isFile())
                {
                    if (knownLibraries.contains(minecraftSources[i].getName()))
                    {
                        FMLLog.finer("Skipping known library file %s", minecraftSources[i].getAbsolutePath());
                    }
                    else
                    {
                        FMLLog.fine("Found a minecraft related file at %s, examining for mod candidates", minecraftSources[i].getAbsolutePath());
                        candidates.add(new ModCandidate(minecraftSources[i], minecraftSources[i], ContainerType.JAR, i==0, true));
                    }
                }
                else if (minecraftSources[i].isDirectory())
                {
                    FMLLog.fine("Found a minecraft related directory at %s, examining for mod candidates", minecraftSources[i].getAbsolutePath());
                    candidates.add(new ModCandidate(minecraftSources[i], minecraftSources[i], ContainerType.DIR, i==0, true));
                }
            }
        }

    }

    public void findModDirMods(File modsDir)
    {
        File[] modList = modsDir.listFiles();
        // Sort the files into alphabetical order first
        Arrays.sort(modList);

        for (File modFile : modList)
        {
            if (modFile.isDirectory())
            {
                FMLLog.fine("Found a candidate mod directory %s", modFile.getName());
                candidates.add(new ModCandidate(modFile, modFile, ContainerType.DIR));
            }
            else
            {
                Matcher matcher = zipJar.matcher(modFile.getName());

                if (matcher.matches())
                {
                    FMLLog.fine("Found a candidate zip or jar file %s", matcher.group(0));
                    candidates.add(new ModCandidate(modFile, modFile, ContainerType.JAR));
                }
                else
                {
                    FMLLog.fine("Ignoring unknown file %s in mods directory", modFile.getName());
                }
            }
        }
    }

    public List<ModContainer> identifyMods()
    {
        List<ModContainer> modList = Lists.newArrayList();

        for (ModCandidate candidate : candidates)
        {
            try
            {
                List<ModContainer> mods = candidate.explore(dataTable);
                if (mods.isEmpty() && !candidate.isClasspath())
                {
                    nonModLibs.add(candidate.getModContainer());
                }
                else
                {
                    modList.addAll(mods);
                }
            }
            catch (LoaderException le)
            {
                FMLLog.log(Level.WARNING, le, "Identified a problem with the mod candidate %s, ignoring this source", candidate.getModContainer());
            }
            catch (Throwable t)
            {
                Throwables.propagate(t);
            }
        }

        return modList;
    }

    public ASMDataTable getASMTable()
    {
        return dataTable;
    }

    public List<File> getNonModLibs()
    {
        return nonModLibs;
    }

}
