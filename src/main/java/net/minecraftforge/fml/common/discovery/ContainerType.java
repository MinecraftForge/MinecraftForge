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

import java.util.List;

import net.minecraftforge.fml.common.ModContainer;

import com.google.common.base.Throwables;

public enum ContainerType
{
    JAR(JarDiscoverer.class),
    DIR(DirectoryDiscoverer.class);

    private ITypeDiscoverer discoverer;

    private ContainerType(Class<? extends ITypeDiscoverer> discovererClass)
    {
        try
        {
            this.discoverer = discovererClass.newInstance();
        }
        catch (Exception e)
        {
            throw Throwables.propagate(e);
        }
    }

    public List<ModContainer> findMods(ModCandidate candidate, ASMDataTable table)
    {
        return discoverer.discover(candidate, table);
    }
}
