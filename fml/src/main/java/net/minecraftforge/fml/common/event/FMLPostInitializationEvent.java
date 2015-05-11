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

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState.ModState;

import com.google.common.base.Throwables;

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

    public Object buildSoftDependProxy(String modId, String className)
    {
        if (Loader.isModLoaded(modId))
        {
            try
            {
                Class<?> clz = Class.forName(className,true,Loader.instance().getModClassLoader());
                return clz.newInstance();
            }
            catch (Exception e)
            {
                Throwables.propagateIfPossible(e);
                return null;
            }
        }
        return null;
    }
}
