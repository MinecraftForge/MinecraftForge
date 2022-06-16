/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fluids.capability;

import net.minecraft.world.item.Item;
import net.minecraftforge.common.capabilities.AttachCapabilitiesEvent.ItemStacks;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

/**
 * A simple fluid container, to replace the functionality of the old FluidContainerRegistry and IFluidContainerItem.
 * This fluid container may be set so that is can only completely filled or empty. (binary)
 * It may also be set so that it gets consumed when it is drained. (consumable)
 */
public class ItemFluidContainer extends Item
{
    protected final int capacity;

    /**
     * @param capacity   The maximum capacity of this fluid container.
     */
    public ItemFluidContainer(Item.Properties properties, int capacity)
    {
        super(properties);
        this.capacity = capacity;
    }

    @Override
    public void attachBuiltinCaps(ItemStacks event) {
    	event.addCapability(new FluidHandlerItemStack(event.getObject(), capacity));
    }
}
