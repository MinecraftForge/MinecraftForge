/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.IItemHandler;

import static net.minecraftforge.common.capabilities.CapabilityManager.get;

/*
 * References to  Forge's built in capabilities.
 * Modders are recommended to use their own CapabilityTokens for 3rd party caps to maintain soft dependencies.
 * However, since nobody has a soft dependency on Forge, we expose this as API.
 */
public class ForgeCapabilities
{
    public static final Capability<IEnergyStorage> ENERGY = get(new CapabilityToken<>(){});
    public static final Capability<IFluidHandler> FLUID_HANDLER = get(new CapabilityToken<>(){});
    public static final Capability<IFluidHandlerItem> FLUID_HANDLER_ITEM = get(new CapabilityToken<>(){});
    public static final Capability<IItemHandler> ITEM_HANDLER = get(new CapabilityToken<>(){});
}
