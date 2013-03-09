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
import java.util.List;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.discovery.asm.ASMModParser;


public class ModCandidate
{
    private File classPathRoot;
    private File modContainer;
    private ContainerType sourceType;
    private boolean classpath;
    private List<String> baseModTypes = Lists.newArrayList();
    private boolean isMinecraft;
    private List<ASMModParser> baseModCandidateTypes = Lists.newArrayListWithCapacity(1);

    public ModCandidate(File classPathRoot, File modContainer, ContainerType sourceType)
    {
        this(classPathRoot, modContainer, sourceType, false, false);
    }
    public ModCandidate(File classPathRoot, File modContainer, ContainerType sourceType, boolean isMinecraft, boolean classpath)
    {
        this.classPathRoot = classPathRoot;
        this.modContainer = modContainer;
        this.sourceType = sourceType;
        this.isMinecraft = isMinecraft;
        this.classpath = classpath;
    }

    public File getClassPathRoot()
    {
        return classPathRoot;
    }

    public File getModContainer()
    {
        return modContainer;
    }

    public ContainerType getSourceType()
    {
        return sourceType;
    }
    public List<ModContainer> explore(ASMDataTable table)
    {
        List<ModContainer> mods = sourceType.findMods(this, table);
        if (!baseModCandidateTypes.isEmpty())
        {
            FMLLog.info("Attempting to reparse the mod container %s", getModContainer().getName());
            return sourceType.findMods(this, table);
        }
        else
        {
            return mods;
        }
    }

    public boolean isClasspath()
    {
        return classpath;
    }
    public void rememberBaseModType(String className)
    {
        baseModTypes.add(className);
    }
    public List<String> getRememberedBaseMods()
    {
        return baseModTypes;
    }
    public boolean isMinecraftJar()
    {
        return isMinecraft;
    }
    public void rememberModCandidateType(ASMModParser modParser)
    {
        baseModCandidateTypes.add(modParser);
    }
}
