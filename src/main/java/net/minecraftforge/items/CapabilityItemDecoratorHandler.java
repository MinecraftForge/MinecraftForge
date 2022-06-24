/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.items;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public class CapabilityItemDecoratorHandler
{
    public static final Capability<IItemDecoratorHandler> ITEM_DECORATOR_HANDLER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private static final ResourceLocation ID = new ResourceLocation("forge", "item_decorator");

    public static void register(RegisterCapabilitiesEvent event)
    {
        event.register(IItemDecoratorHandler.class);
    }

    public static LazyOptional<IItemDecoratorHandler> attach(AttachCapabilitiesEvent<ItemStack> event)
    {
        ICapabilityProvider provider = new ItemDecoratorProvider();
        event.addCapability(ID, provider);
        return provider.getCapability(CapabilityItemDecoratorHandler.ITEM_DECORATOR_HANDLER_CAPABILITY);
    }
}
