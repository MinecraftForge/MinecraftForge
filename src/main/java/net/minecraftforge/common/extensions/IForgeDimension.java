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

package net.minecraftforge.common.extensions;

import javax.annotation.Nullable;

import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.NetherDimension;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IRenderHandler;

public interface IForgeDimension
{
    default Dimension getDimension()
    {
        return (Dimension) this;
    }

    /**
     * Called from {@link World#initCapabilities()}, to gather capabilities for this
     * world. It's safe to access world here since this is called after world is
     * registered.
     *
     * On server, called directly after mapStorage and world data such as Scoreboard
     * and VillageCollection are initialized. On client, called when world is
     * constructed, just before world load event is called. Note that this method is
     * always called before the world load event.
     * 
     * @return initial holder for capabilities on the world
     */
    default net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities()
    {
        return null;
    }

    /**
     * The dimension's movement factor. Whenever a player or entity changes
     * dimension from world A to world B, their coordinates are multiplied by
     * worldA.provider.getMovementFactor() / worldB.provider.getMovementFactor()
     * Example: Overworld factor is 1, nether factor is 8. Traveling from overworld
     * to nether multiplies coordinates by 1/8.
     * 
     * @return The movement factor
     */
    default double getMovementFactor()
    {
        if (getDimension() instanceof NetherDimension)
        {
            return 8.0;
        }
        return 1.0;
    }

    /**
     * Sets the providers current dimension ID, used in default getSaveFolder()
     * Added to allow default providers to be registered for multiple dimensions.
     * This is to denote the exact dimension ID opposed to the 'type' in WorldType
     *
     * @param id Dimension ID
     */
    void setId(int id);

    int getId();

    @OnlyIn(Dist.CLIENT)
    @Nullable
    IRenderHandler getSkyRenderer();

    @OnlyIn(Dist.CLIENT)
    void setSkyRenderer(IRenderHandler skyRenderer);

    @OnlyIn(Dist.CLIENT)
    @Nullable
    IRenderHandler getCloudRenderer();

    @OnlyIn(Dist.CLIENT)
    void setCloudRenderer(IRenderHandler renderer);

    @OnlyIn(Dist.CLIENT)
    @Nullable
    IRenderHandler getWeatherRenderer();

    @OnlyIn(Dist.CLIENT)
    void setWeatherRenderer(IRenderHandler renderer);

    void resetRainAndThunder();
    
    default boolean canDoLightning(net.minecraft.world.chunk.Chunk chunk)
    {
        return true;
    }

    default boolean canDoRainSnowIce(net.minecraft.world.chunk.Chunk chunk)
    {
        return true;
    }
}
