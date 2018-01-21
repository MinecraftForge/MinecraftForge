/*
 * Minecraft Forge
 * Copyright (c) 2017.
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
import net.minecraft.world.WorldProvider;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is fired when a world provider is created
 * (by {@link net.minecraftforge.common.DimensionManager#createProviderFor(int)}).
 *
 * You can use this event to replace the default provider implementation for a dimension.
 *
 * Note that, for compatibility, it is recommended that any replacement
 * should extend the original provider class used.
 */
public class WorldProviderEvent extends Event
{
    private final int dimension;
    private final DimensionType type;
    private WorldProvider provider;

    public WorldProviderEvent(int dimension, DimensionType type, WorldProvider provider)
    {
        this.dimension = dimension;
        this.type = type;
        this.provider = provider;
    }

    public int getDimension()
    {
        return dimension;
    }

    public DimensionType getType()
    {
        return type;
    }

    public WorldProvider getProvider()
    {
        return provider;
    }

    public void setProvider(WorldProvider provider)
    {
        this.provider = provider;
    }
}
