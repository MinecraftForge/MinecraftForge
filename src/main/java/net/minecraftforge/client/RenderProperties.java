/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidType;

public class RenderProperties
{
    public static EffectRenderer getEffectRenderer(MobEffectInstance effectInstance)
    {
        return getEffectRenderer(effectInstance.getEffect());
    }

    public static EffectRenderer getEffectRenderer(MobEffect effect)
    {
        return effect.getEffectRendererInternal() instanceof EffectRenderer r ? r : EffectRenderer.DUMMY;
    }

    public static IItemRenderProperties get(ItemStack stack)
    {
        return get(stack.getItem());
    }

    public static IItemRenderProperties get(Item item)
    {
        return item.getRenderPropertiesInternal() instanceof IItemRenderProperties props ? props : IItemRenderProperties.DUMMY;
    }

    public static IBlockRenderProperties get(BlockState state)
    {
        return get(state.getBlock());
    }

    public static IBlockRenderProperties get(Block block)
    {
        return block.getRenderPropertiesInternal() instanceof IBlockRenderProperties props ? props : IBlockRenderProperties.DUMMY;
    }

    public static IFluidTypeRenderProperties get(FluidState state)
    {
        return get(state.getFluidType());
    }

    public static IFluidTypeRenderProperties get(Fluid fluid)
    {
        return get(fluid.getFluidType());
    }

    public static IFluidTypeRenderProperties get(FluidType type)
    {
        return type.getRenderPropertiesInternal() instanceof IFluidTypeRenderProperties props ? props : IFluidTypeRenderProperties.DUMMY;
    }

    private RenderProperties()
    {
        // Not instantiable.
    }
}
