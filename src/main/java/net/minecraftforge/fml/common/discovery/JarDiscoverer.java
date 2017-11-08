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

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarFile;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.LoaderException;
import net.minecraftforge.fml.common.MetadataCollection;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ModContainerFactory;
import net.minecraftforge.fml.common.discovery.asm.ASMModParser;

import org.apache.commons.io.IOUtils;
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
        FMLLog.log.debug("Examining file {} for potential mods", candidate.getModContainer().getName());
        try (JarFile jar = new JarFile(candidate.getModContainer()))
        {
            ZipEntry modInfo = jar.getEntry("mcmod.info");
            MetadataCollection mc = null;
            if (modInfo != null)
            {
                FMLLog.log.trace("Located mcmod.info file in file {}", candidate.getModContainer().getName());
                try (InputStream inputStream = jar.getInputStream(modInfo))
                {
                    mc = MetadataCollection.from(inputStream, candidate.getModContainer().getName());
                }
            }
            else
            {
                FMLLog.log.debug("The mod container {} appears to be missing an mcmod.info file", candidate.getModContainer().getName());
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
                        try (InputStream inputStream = jar.getInputStream(ze))
                        {
                            modParser = new ASMModParser(inputStream);
                        }
                        candidate.addClassEntry(ze.getName());
                    }
                    catch (LoaderException e)
                    {
                        FMLLog.log.error("There was a problem reading the entry {} in the jar {} - probably a corrupt zip", candidate.getModContainer().getPath(), e);
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
                        container.setClassVersion(modParser.getClassVersion());
                    }
                }
            }
        }
        catch (Exception e)
        {
            FMLLog.log.warn("Zip file {} failed to read properly, it will be ignored", candidate.getModContainer().getName(), e);
        }
        return foundMods;
    }

}
