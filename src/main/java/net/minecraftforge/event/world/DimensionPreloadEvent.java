/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.event.world;

import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.common.MinecraftForge;

/**
 * Event is fired when DimensionManager starts to initiate a new dimension
 * with {@link net.minecraftforge.common.DimensionManager#initDimension(int)},<br>
 * event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 * <br>
 * This event can be used to override instantiated dimensions <br>
 * by using {@link #setAlternativeDimension }
 */

public class DimensionPreloadEvent extends Event
{

    private final int dimensionId;
    private final DimensionType type;
    private WorldServer alternativeDimension;

    public DimensionPreloadEvent(int dimensionId, DimensionType type)
    {
        this.dimensionId = dimensionId;
        this.type = type;
    }

    public int getDimensionId()
    {
        return this.dimensionId;
    }

    public DimensionType getDimensionType()
    {
        return this.type;
    }

    public WorldServer getAlternativeDimension()
    {
        return alternativeDimension;
    }

    public void setAlternativeDimension(WorldServer alternativeDimension)
    {
        this.alternativeDimension = alternativeDimension;
    }
}
