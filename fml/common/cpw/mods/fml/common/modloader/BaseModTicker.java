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

package cpw.mods.fml.common.modloader;

import java.util.EnumSet;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

/**
 * @author cpw
 *
 */
public class BaseModTicker implements ITickHandler
{

    private BaseMod mod;
    private EnumSet<TickType> ticks;
    private boolean clockTickTrigger;
    
    
    BaseModTicker(BaseMod mod)
    {
        this.mod = mod;
        this.ticks = EnumSet.of(TickType.WORLDLOAD);
    }
    
    BaseModTicker(EnumSet<TickType> ticks)
    {
        this.ticks = ticks;
    }
    
    @Override
    public void tickStart(EnumSet<TickType> types, Object... tickData)
    {
        tickBaseMod(types, false, tickData);
    }
    
    @Override
    public void tickEnd(EnumSet<TickType> types, Object... tickData)
    {
        tickBaseMod(ticks, true, tickData);
    }

    private void tickBaseMod(EnumSet<TickType> types, boolean end, Object... tickData)
    {
        if (end && (types.contains(TickType.GAME) && ticks.contains(TickType.GAME)) || (types.contains(TickType.WORLDLOAD) && ticks.contains(TickType.WORLDLOAD)))
        {
            clockTickTrigger =  true;
        }
        if (end && clockTickTrigger && types.contains(TickType.RENDER))
        {
            types.add(TickType.GAME);
            types.remove(TickType.RENDER);
            clockTickTrigger = false;
        }
        for (TickType type : types)
        {
            if (!ticks.contains(type))
            {
                continue;
            }
            boolean keepTicking=mod.doTickInGame(type, end, FMLCommonHandler.instance().getMinecraftInstance(), tickData);
            if (!keepTicking) {
                ticks.remove(type);
                ticks.removeAll(type.partnerTicks());
            }
        }
    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return ticks;
    }

    @Override
    public String getLabel()
    {
        return mod.getClass().getSimpleName();
    }

    /**
     * @param mod2
     */
    public void setMod(BaseMod mod)
    {
        this.mod = mod;
    }

}
