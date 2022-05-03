/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidUtil;

public class FluidContainerColorer implements IItemColor
{
    @Override
    public int getColor(@Nonnull ItemStack stack, int tintIndex)
    {
        if (tintIndex != 1) return 0xFFFFFFFF;
        return FluidUtil.getFluidContained(stack)
                .map(fstack -> fstack.getFluid().getAttributes().getColor(fstack))
                .orElse(0xFFFFFFFF);
    }
}
