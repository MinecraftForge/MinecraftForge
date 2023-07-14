/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions.common;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.fml.LogicalSide;
import org.joml.Vector3d;

import java.util.function.Consumer;

/**
 * {@linkplain LogicalSide#CLIENT Client-only} extensions to {@link Block}.
 *
 * @see Block#initializeClient(Consumer)
 */
public interface IClientBlockExtensions
{
    IClientBlockExtensions DEFAULT = new IClientBlockExtensions() { };

    static IClientBlockExtensions of(BlockState state)
    {
        return of(state.getBlock());
    }

    static IClientBlockExtensions of(Block block)
    {
        return block.getRenderPropertiesInternal() instanceof IClientBlockExtensions e ? e : DEFAULT;
    }

    /**
     * Spawn a digging particle effect in the level, this is a wrapper
     * around EffectRenderer.addBlockHitEffects to allow the block more
     * control over the particles. Useful when you have entirely different
     * texture sheets for different sides/locations in the level.
     *
     * @param state   The current state
     * @param level   The current level
     * @param target  The target the player is looking at {x/y/z/side/sub}
     * @param manager A reference to the current particle manager.
     * @return True to prevent vanilla digging particles form spawning.
     */
    default boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager)
    {
        return false;
    }

    /**
     * Spawn particles for when the block is destroyed. Due to the nature
     * of how this is invoked, the x/y/z locations are not always guaranteed
     * to host your block. So be sure to do proper sanity checks before assuming
     * that the location is this block.
     *
     * @param Level   The current Level
     * @param pos     Position to spawn the particle
     * @param manager A reference to the current particle manager.
     * @return True to prevent vanilla break particles from spawning.
     */
    default boolean addDestroyEffects(BlockState state, Level Level, BlockPos pos, ParticleEngine manager)
    {
        return !state.shouldSpawnParticlesOnBreak();
    }

    /**
     * NOT CURRENTLY IMPLEMENTED
     * <p>
     * Use this to change the fog color used when the entity is "inside" a material.
     * Vec3d is used here as "r/g/b" 0 - 1 values.
     *
     * @param level         The level.
     * @param pos           The position at the entity viewport.
     * @param state         The state at the entity viewport.
     * @param entity        the entity
     * @param originalColor The current fog color, You are not expected to use this, Return as the default if applicable.
     * @return The new fog color.
     */
    default Vector3d getFogColor(BlockState state, LevelReader level, BlockPos pos, Entity entity, Vector3d originalColor, float partialTick)
    {
        FluidState fluidState = level.getFluidState(pos);
        if (fluidState.is(FluidTags.WATER))
        {
            float f12 = 0.0F;

            if (entity instanceof LivingEntity)
            {
                LivingEntity ent = (LivingEntity) entity;
                f12 = (float) EnchantmentHelper.getRespiration(ent) * 0.2F;

                if (ent.hasEffect(MobEffects.WATER_BREATHING))
                {
                    f12 = f12 * 0.3F + 0.6F;
                }
            }
            return new Vector3d(0.02F + f12, 0.02F + f12, 0.2F + f12);
        }
        else if (fluidState.is(FluidTags.LAVA))
        {
            return new Vector3d(0.6F, 0.1F, 0.0F);
        }
        return originalColor;
    }

    /**
     * Returns true if the breaking particles created from the {@link BlockState} passed should be tinted with biome colors. 
     * 
     * @param state The state of this block
     * @param level The level the particles are spawning in
     * @param pos The position of the block
     * @return {@code true} if the particles should be tinted.
     */
    default boolean areBreakingParticlesTinted(BlockState state, ClientLevel level, BlockPos pos)
    {
        return !state.is(Blocks.GRASS_BLOCK);
    }
}
