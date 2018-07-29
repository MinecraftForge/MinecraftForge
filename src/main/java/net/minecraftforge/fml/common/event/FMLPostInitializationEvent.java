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

package net.minecraftforge.fml.common.event;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState.ModState;

import java.lang.reflect.Constructor;

/**
 * Called after {@link FMLInitializationEvent} has been dispatched on every mod. This is the third and last
 * commonly called event during mod initialization.
 *
 * Recommended activities: interact with other mods to establish cross-mod behaviours.
 *
 * @see net.minecraftforge.fml.common.Mod.EventHandler for how to subscribe to this event
 * @author cpw
 */
public class FMLPostInitializationEvent extends FMLStateEvent
{
    public FMLPostInitializationEvent(Object... data)
    {
        super(data);
    }

    @Override
    public ModState getModState()
    {
        return ModState.POSTINITIALIZED;
    }

    /**
     * Build an object depending on if a specific target mod is loaded or not.
     *
     * Usually would be used to access an object from the other mod.
     *
     * @param modId The modId I conditionally want to build an object for
     * @param className The name of the class I wish to instantiate
     * @return An optional containing the object if possible, or null if not
     */
    public Optional<?> buildSoftDependProxy(String modId, String className, Object... arguments)
    {
        if (Loader.isModLoaded(modId))
        {
            Class<?>[] args = Arrays.stream(arguments).filter(Objects::nonNull).map(Object::getClass).toArray(Class<?>[]::new);
            try
            {
                Class<?> clz = Class.forName(className,true,Loader.instance().getModClassLoader());
                Constructor<?> ct = clz.getConstructor(args);
                return Optional.of(ct.newInstance(arguments));
            }
            catch (Exception e)
            {
                FMLLog.log.info("An error occurred trying to build a soft depend proxy", e);
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}
