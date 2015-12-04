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

import net.minecraftforge.fml.common.LoaderState.ModState;

/**
 * Called after {@link FMLPreInitializationEvent} and before {@link FMLPostInitializationEvent} during mod
 * startup.
 *
 * This is the second of three commonly called events during mod initialization.
 *
 * Recommended activities: Register your recipes and Ore Dictionary entries in the
 * {@link net.minecraftforge.fml.common.registry.GameRegistry} and {@link net.minecraftforge.oredict.OreDictionary}
 * Dispatch requests through {@link FMLInterModComms} to other mods, to tell them what you wish them to do.
 *
 * @see net.minecraftforge.fml.common.Mod.EventHandler for how to subscribe to this event
 * @author cpw
 */
public class FMLInitializationEvent extends FMLStateEvent
{

    public FMLInitializationEvent(Object... data)
    {
        super(data);
    }
    
    @Override
    public ModState getModState()
    {
        return ModState.INITIALIZED;
    }

}
