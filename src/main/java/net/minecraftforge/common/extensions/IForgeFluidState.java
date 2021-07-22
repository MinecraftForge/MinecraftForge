/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.tags.SetTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;

public interface IForgeFluidState
{
    private FluidState self()
    {
        return (FluidState)this;
    }

    /**
     * Called when the entity is inside this block, may be used to determined if the entity can breathing,
     * display material overlays, or if the entity can swim inside a block.
     *
     * @param world that is being tested.
     * @param pos position thats being tested.
     * @param entity that is being tested.
     * @param yToTest, primarily for testingHead, which sends the the eye level of the entity, other wise it sends a y that can be tested vs liquid height.
     * @param tag to test for.
     * @param testingHead when true, its testing the entities head for vision, breathing ect... otherwise its testing the body, for swimming and movement adjustment.
     */
    default boolean isEntityInside(LevelReader world, BlockPos pos, Entity entity, double yToTest, SetTag<Fluid> tag, boolean testingHead)
    {
//        return ifluidstate.isTagged(p_213290_1_) && d0 < (double)((float)blockpos.getY() + ifluidstate.getActualHeight(this.world, blockpos) + 0.11111111F);
        return self().getType().isEntityInside(self(), world, pos, entity, yToTest, tag, testingHead);
    }



    /**
     * Location sensitive version of getExplosionResistance
     *
     * @param world The current world
     * @param pos Block position in world
     * @param explosion The explosion
     * @return The amount of the explosion absorbed.
     */
    default float getExplosionResistance(BlockGetter world, BlockPos pos, Explosion explosion)
    {
        return self().getType().getExplosionResistance(self(), world, pos, explosion);
    }

    /**
     * Queries if this fluidstate should render in a given layer.
     * A custom {@link IBakedModel} can use {@link net.minecraftforge.client.MinecraftForgeClient#getRenderLayer()} to alter the model based on layer.
     */
    /* TODO: reimplement
    default boolean canRenderInLayer(BlockRenderLayer layer)
    {
        return getFluidState().getFluid().canRenderInLayer(getFluidState(), layer);
    }*/
}
