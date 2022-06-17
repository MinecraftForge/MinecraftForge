/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.items;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface IItemHandlerModifiable extends IItemHandler
{
    /**
     * Overrides the stack in the given slot. This method is used by the
     * standard Forge helper methods and classes. It is not intended for
     * general use by other mods, and the handler may throw an error if it
     * is called unexpectedly.
     *
     * @param slot  Slot to modify
     * @param stack ItemStack to set slot to (may be empty).
     * @throws RuntimeException if the handler is called in a way that the handler
     * was not expecting.
     **/
    void setStackInSlot(int slot, @NotNull ItemStack stack);
}
