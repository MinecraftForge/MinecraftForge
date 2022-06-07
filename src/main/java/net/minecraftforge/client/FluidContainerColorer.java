/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidUtil;
import org.jetbrains.annotations.NotNull;

public class FluidContainerColorer implements ItemColor
{
    @Override
    public int getColor(@NotNull ItemStack stack, int tintIndex)
    {
        if (tintIndex != 1) return 0xFFFFFFFF;
        return FluidUtil.getFluidContained(stack)
                .map(fstack -> fstack.getFluid().getAttributes().getColor(fstack))
                .orElse(0xFFFFFFFF);
    }
}
