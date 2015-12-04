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

package net.minecraftforge.fml.common.event;

import net.minecraftforge.fml.common.ModClassLoader;
import net.minecraftforge.fml.common.LoaderState.ModState;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

import com.google.common.collect.ListMultimap;

/**
 * An internal FML event used to signal the construction of mods. Should not be used by mods.
 */
public class FMLConstructionEvent extends FMLStateEvent
{
    private ModClassLoader modClassLoader;
    private ASMDataTable asmData;
    private ListMultimap<String,String> reverseDependencies;

    @SuppressWarnings("unchecked")
    public FMLConstructionEvent(Object... eventData)
    {
        this.modClassLoader = (ModClassLoader)eventData[0];
        this.asmData = (ASMDataTable) eventData[1];
        this.reverseDependencies = (ListMultimap<String, String>) eventData[2];
    }

    public ModClassLoader getModClassLoader()
    {
        return modClassLoader;
    }

    @Override
    public ModState getModState()
    {
        return ModState.CONSTRUCTED;
    }

    public ASMDataTable getASMHarvestedData()
    {
        return asmData;
    }

    public ListMultimap<String, String> getReverseDependencies()
    {
        return reverseDependencies;
    }
}
