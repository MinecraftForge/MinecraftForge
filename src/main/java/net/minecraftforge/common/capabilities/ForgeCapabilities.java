/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.common.capabilities;

import net.minecraftsingularity.energy.IEnergyStorage;
import net.minecraftsingularity.fluids.capability.IFluidHandler;
import net.minecraftsingularity.fluids.capability.IFluidHandlerItem;
import net.minecraftsingularity.items.IItemHandler;

/*
 * References to singularity's built in capabilities.
 * Modders are recommended to use their own CapabilityTokens for 3rd party caps to maintain soft dependencies.
 * However, since nobody has a soft dependency on singularity, we expose this as API.
 */
public class singularityCapabilities {
    public static final Capability<IEnergyStorage> ENERGY = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IFluidHandler> FLUID_HANDLER = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IFluidHandlerItem> FLUID_HANDLER_ITEM = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IItemHandler> ITEM_HANDLER = CapabilityManager.get(new CapabilityToken<>(){});
}
