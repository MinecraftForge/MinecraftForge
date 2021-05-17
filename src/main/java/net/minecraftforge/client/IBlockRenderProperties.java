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

package net.minecraftforge.client;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public interface IBlockRenderProperties
{
    IBlockRenderProperties DUMMY = new IBlockRenderProperties()
    {
    };

    /**
     * Spawn a digging particle effect in the world, this is a wrapper
     * around EffectRenderer.addBlockHitEffects to allow the block more
     * control over the particles. Useful when you have entirely different
     * texture sheets for different sides/locations in the world.
     *
     * @param state   The current state
     * @param world   The current world
     * @param target  The target the player is looking at {x/y/z/side/sub}
     * @param manager A reference to the current particle manager.
     * @return True to prevent vanilla digging particles form spawning.
     */
    default boolean addHitEffects(BlockState state, World world, RayTraceResult target, ParticleManager manager)
    {
        return false;
    }

    /**
     * Spawn particles for when the block is destroyed. Due to the nature
     * of how this is invoked, the x/y/z locations are not always guaranteed
     * to host your block. So be sure to do proper sanity checks before assuming
     * that the location is this block.
     *
     * @param world   The current world
     * @param pos     Position to spawn the particle
     * @param manager A reference to the current particle manager.
     * @return True to prevent vanilla break particles from spawning.
     */
    default boolean addDestroyEffects(BlockState state, World world, BlockPos pos, ParticleManager manager)
    {
        return false;
    }

    /**
     * NOT CURRENTLY IMPLEMENTED
     * <p>
     * Use this to change the fog color used when the entity is "inside" a material.
     * Vec3d is used here as "r/g/b" 0 - 1 values.
     *
     * @param world         The world.
     * @param pos           The position at the entity viewport.
     * @param state         The state at the entity viewport.
     * @param entity        the entity
     * @param originalColor The current fog color, You are not expected to use this, Return as the default if applicable.
     * @return The new fog color.
     */
    default Vector3d getFogColor(BlockState state, IWorldReader world, BlockPos pos, Entity entity, Vector3d originalColor, float partialTicks)
    {
        if (state.getMaterial() == Material.WATER)
        {
            float f12 = 0.0F;

            if (entity instanceof LivingEntity)
            {
                LivingEntity ent = (LivingEntity) entity;
                f12 = (float) EnchantmentHelper.getRespiration(ent) * 0.2F;

                if (ent.hasEffect(Effects.WATER_BREATHING))
                {
                    f12 = f12 * 0.3F + 0.6F;
                }
            }
            return new Vector3d(0.02F + f12, 0.02F + f12, 0.2F + f12);
        }
        else if (state.getMaterial() == Material.LAVA)
        {
            return new Vector3d(0.6F, 0.1F, 0.0F);
        }
        return originalColor;
    }
}
