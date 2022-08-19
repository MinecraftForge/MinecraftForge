/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.energy;

import net.minecraftforge.common.capabilities.*;

@Deprecated(forRemoval = true, since = "1.19.2")
public class CapabilityEnergy
{
    /**
     * @deprecated Create your own reference using {@link CapabilityManager#get(CapabilityToken)}, or use {@link ForgeCapabilities#ENERGY}.
     */
    @Deprecated(forRemoval = true, since = "1.19.2")
    public static final Capability<IEnergyStorage> ENERGY = ForgeCapabilities.ENERGY;
    public static void register(RegisterCapabilitiesEvent event){}
}
