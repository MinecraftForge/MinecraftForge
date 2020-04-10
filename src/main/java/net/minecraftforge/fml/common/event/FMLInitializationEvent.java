/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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
