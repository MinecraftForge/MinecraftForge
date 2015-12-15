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

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState.ModState;

import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
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
            Class<?>[] args = Lists.transform(Lists.newArrayList(arguments),new Function<Object, Class<?>>() {
                @Nullable
                @Override
                public Class<?> apply(@Nullable Object input) {
                    return input.getClass();
                }
            }).toArray(new Class[0]);
            try
            {
                Class<?> clz = Class.forName(className,true,Loader.instance().getModClassLoader());
                Constructor<?> ct = clz.getConstructor(args);
                return Optional.fromNullable(ct.newInstance(arguments));
            }
            catch (Exception e)
            {
                FMLLog.getLogger().log(Level.INFO, "An error occurred trying to build a soft depend proxy",e);
                return Optional.absent();
            }
        }
        return Optional.absent();
    }
}
