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

package net.minecraftforge.fml.common.discovery;

import java.util.Collections;
import java.util.List;
import java.util.jar.JarFile;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.LoaderException;
import net.minecraftforge.fml.common.MetadataCollection;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ModContainerFactory;
import net.minecraftforge.fml.common.discovery.asm.ASMModParser;

import org.apache.logging.log4j.Level;

import java.util.regex.Matcher;
import java.util.zip.ZipEntry;

import com.google.common.collect.Lists;

public class JarDiscoverer implements ITypeDiscoverer
{
    @Override
    public List<ModContainer> discover(ModCandidate candidate, ASMDataTable table)
    {
        List<ModContainer> foundMods = Lists.newArrayList();
        FMLLog.fine("Examining file %s for potential mods", candidate.getModContainer().getName());
        JarFile jar = null;
        try
        {
            jar = new JarFile(candidate.getModContainer());

            ZipEntry modInfo = jar.getEntry("mcmod.info");
            MetadataCollection mc = null;
            if (modInfo != null)
            {
                FMLLog.finer("Located mcmod.info file in file %s", candidate.getModContainer().getName());
                mc = MetadataCollection.from(jar.getInputStream(modInfo), candidate.getModContainer().getName());
            }
            else
            {
                FMLLog.fine("The mod container %s appears to be missing an mcmod.info file", candidate.getModContainer().getName());
                mc = MetadataCollection.from(null, "");
            }
            for (ZipEntry ze : Collections.list(jar.entries()))
            {
                if (ze.getName()!=null && ze.getName().startsWith("__MACOSX"))
                {
                    continue;
                }
                Matcher match = classFile.matcher(ze.getName());
                if (match.matches())
                {
                    ASMModParser modParser;
                    try
                    {
                        modParser = new ASMModParser(jar.getInputStream(ze));
                        candidate.addClassEntry(ze.getName());
                    }
                    catch (LoaderException e)
                    {
                        FMLLog.log(Level.ERROR, e, "There was a problem reading the entry %s in the jar %s - probably a corrupt zip", ze.getName(), candidate.getModContainer().getPath());
                        jar.close();
                        throw e;
                    }
                    modParser.validate();
                    modParser.sendToTable(table, candidate);
                    ModContainer container = ModContainerFactory.instance().build(modParser, candidate.getModContainer(), candidate);
                    if (container!=null)
                    {
                        table.addContainer(container);
                        foundMods.add(container);
                        container.bindMetadata(mc);
                    }
                }
            }
        }
        catch (Exception e)
        {
            FMLLog.log(Level.WARN, e, "Zip file %s failed to read properly, it will be ignored", candidate.getModContainer().getName());
        }
        finally
        {
            if (jar != null)
            {
                try
                {
                    jar.close();
                }
                catch (Exception e)
                {
                }
            }
        }
        return foundMods;
    }

}
