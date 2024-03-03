/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fluids.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class CapabilityFluidHandler
{
    public static Capability<IFluidHandler> FLUID_HANDLER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    public static Capability<IFluidHandlerItem> FLUID_HANDLER_ITEM_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public static void register(RegisterCapabilitiesEvent event)
    {
        event.register(IFluidHandler.class);

        event.register(IFluidHandlerItem.class);
    }
}
