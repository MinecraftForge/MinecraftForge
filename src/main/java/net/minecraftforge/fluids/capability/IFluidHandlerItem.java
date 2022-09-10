/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fluids.capability;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

import org.jetbrains.annotations.NotNull;

/**
 * ItemStacks handled by an {@link IFluidHandler} may change, so this class allows
 * users of the fluid handler to get the container after it has been used.
 */
@AutoRegisterCapability
public interface IFluidHandlerItem extends IFluidHandler
{
    /**
     * Get the container currently acted on by this fluid handler.
     * The ItemStack may be different from its initial state, in the case of fluid containers that have different items
     * for their filled and empty states.
     * May be an empty item if the container was drained and is consumable.
     */
    @NotNull
    ItemStack getContainer();
}
