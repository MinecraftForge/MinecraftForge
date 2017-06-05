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

package net.minecraftforge.fml.common.discovery;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.LoaderException;
import net.minecraftforge.fml.common.ModClassLoader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.FileListHelper;

import org.apache.logging.log4j.Level;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.ObjectArrays;

public class ModDiscoverer
{
    private static Pattern zipJar = Pattern.compile("(.+).(zip|jar)$");

    private List<ModCandidate> candidates = Lists.newArrayList();

    private ASMDataTable dataTable = new ASMDataTable();

    private List<File> nonModLibs = Lists.newArrayList();

    public void findClasspathMods(ModClassLoader modClassLoader)
    {
        List<String> knownLibraries = ImmutableList.<String>builder()
                // skip default libs
                .addAll(modClassLoader.getDefaultLibraries())
                // skip loaded coremods
                .addAll(CoreModManager.getIgnoredMods())
                // skip reparse coremods here
                .addAll(CoreModManager.getReparseableCoremods())
                .build();
        File[] minecraftSources = modClassLoader.getParentSources();
        if (minecraftSources.length == 1 && minecraftSources[0].isFile())
        {
            FMLLog.log.debug("Minecraft is a file at {}, loading", minecraftSources[0].getAbsolutePath());
            addCandidate(new ModCandidate(minecraftSources[0], minecraftSources[0], ContainerType.JAR, true, true));
        }
        else
        {
            int i = 0;
            for (File source : minecraftSources)
            {
                if (source.isFile())
                {
                    if (knownLibraries.contains(source.getName()) || modClassLoader.isDefaultLibrary(source))
                    {
                        FMLLog.log.trace("Skipping known library file {}", source.getAbsolutePath());
                    }
                    else
                    {
                        FMLLog.log.debug("Found a minecraft related file at {}, examining for mod candidates", source.getAbsolutePath());
                        addCandidate(new ModCandidate(source, source, ContainerType.JAR, i==0, true));
                    }
                }
                else if (minecraftSources[i].isDirectory())
                {
                    FMLLog.log.debug("Found a minecraft related directory at {}, examining for mod candidates", source.getAbsolutePath());
                    addCandidate(new ModCandidate(source, source, ContainerType.DIR, i==0, true));
                }
                i++;
            }
        }

    }

    public void findModDirMods(File modsDir)
    {
        findModDirMods(modsDir, new File[0]);
    }

    public void findModDirMods(File modsDir, File[] supplementalModFileCandidates)
    {
        File[] modList = FileListHelper.sortFileList(modsDir, null);
        modList = FileListHelper.sortFileList(ObjectArrays.concat(modList, supplementalModFileCandidates, File.class));
        for (File modFile : modList)
        {
            // skip loaded coremods
            if (CoreModManager.getIgnoredMods().contains(modFile.getName()))
            {
                FMLLog.log.trace("Skipping already parsed coremod or tweaker {}", modFile.getName());
            }
            else if (modFile.isDirectory())
            {
                FMLLog.log.debug("Found a candidate mod directory {}", modFile.getName());
                addCandidate(new ModCandidate(modFile, modFile, ContainerType.DIR));
            }
            else
            {
                Matcher matcher = zipJar.matcher(modFile.getName());

                if (matcher.matches())
                {
                    FMLLog.log.debug("Found a candidate zip or jar file {}", matcher.group(0));
                    addCandidate(new ModCandidate(modFile, modFile, ContainerType.JAR));
                }
                else
                {
                    FMLLog.log.debug("Ignoring unknown file {} in mods directory", modFile.getName());
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
                FMLLog.warn(le, "Identified a problem with the mod candidate {}, ignoring this source", candidate.getModContainer());
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

    private void addCandidate(ModCandidate candidate)
    {
        for (ModCandidate c : candidates)
        {
            if (c.getModContainer().equals(candidate.getModContainer()))
            {
                FMLLog.log.trace("  Skipping already in list {}", candidate.getModContainer());
                return;
            }
        }
        candidates.add(candidate);
    }
}
