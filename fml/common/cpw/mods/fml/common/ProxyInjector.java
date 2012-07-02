/*
 * The FML Forge Mod Loader suite.
 * Copyright (C) 2012 cpw
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package cpw.mods.fml.common;

import java.lang.reflect.Field;

/**
 * @author cpw
 *
 */
public class ProxyInjector
{
    private String clientName;
    private String serverName;
    private String bukkitName;
    private Field target;

    public ProxyInjector(String clientName, String serverName, String bukkitName, Field target)
    {
        this.clientName = clientName;
        this.serverName = serverName;
        this.bukkitName = bukkitName;
        this.target = target;
    }

    public boolean isValidFor(Side type)
    {
        if (type == Side.CLIENT)
        {
            return !this.clientName.isEmpty();
        }
        else if (type == Side.SERVER)
        {
            return !this.serverName.isEmpty();
        }
        else if (type == Side.BUKKIT)
        {
            return !this.bukkitName.isEmpty();
        }
        return false;
    }

    public void inject(ModContainer mod, Side side)
    {
        String targetType = side == Side.CLIENT ? clientName : serverName;
        try
        {
            Object proxy=Class.forName(targetType, false, Loader.instance().getModClassLoader()).newInstance();
            if (target.getType().isAssignableFrom(proxy.getClass()))
            {
                target.set(mod.getMod(), proxy);
            } else {
                FMLCommonHandler.instance().getFMLLogger().severe(String.format("Attempted to load a proxy type %s into %s, but the types don't match", targetType, target.getName()));
                throw new LoaderException();
            }
        }
        catch (Exception e)
        {
            FMLCommonHandler.instance().getFMLLogger().severe(String.format("An error occured trying to load a proxy type %s into %s", targetType, target.getName()));
            FMLCommonHandler.instance().getFMLLogger().throwing("ProxyInjector", "inject", e);
            throw new LoaderException(e);
        }
    }
}
