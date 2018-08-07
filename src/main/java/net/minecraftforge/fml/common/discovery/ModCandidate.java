/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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
import java.util.Set;

import net.minecraftforge.fml.common.ModContainer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


public class ModCandidate
{
    private File classPathRoot;
    private File modContainer;
    private ContainerType sourceType;
    private boolean classpath;
    private boolean isMinecraft;
    private Set<String> foundClasses = Sets.newHashSet();
    private List<ModContainer> mods;
    private List<String> packages = Lists.newArrayList();
    private ASMDataTable table;

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
        this.table = table;
        this.mods = sourceType.findMods(this, table);
        return this.mods;
    }

    public void addClassEntry(String name)
    {
        String className = name.substring(0, name.lastIndexOf('.')); // strip the .class
        foundClasses.add(className);
        className = className.replace('/','.');
        int pkgIdx = className.lastIndexOf('.');
        if (pkgIdx > -1)
        {
            String pkg = className.substring(0,pkgIdx);
            packages.add(pkg);
            this.table.registerPackage(this,pkg);
        }
    }

    public boolean isClasspath()
    {
        return classpath;
    }
    public boolean isMinecraftJar()
    {
        return isMinecraft;
    }
    public Set<String> getClassList()
    {
        return foundClasses;
    }
    public List<ModContainer> getContainedMods()
    {
        return mods;
    }
    public List<String> getContainedPackages()
    {
        return packages;
    }
}
