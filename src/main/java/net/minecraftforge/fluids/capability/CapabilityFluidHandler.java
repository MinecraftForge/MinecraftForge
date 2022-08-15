/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fluids.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

@Deprecated(forRemoval = true, since = "1.19.2")
public class CapabilityFluidHandler
{
    public static Capability<IFluidHandler> FLUID_HANDLER_CAPABILITY = ForgeCapabilities.FLUID_HANDLER;
    public static Capability<IFluidHandlerItem> FLUID_HANDLER_ITEM_CAPABILITY = ForgeCapabilities.FLUID_HANDLER_ITEM;

    public static void register(RegisterCapabilitiesEvent event){}
}
