/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.energy;

import net.minecraftforge.common.capabilities.*;

@Deprecated(forRemoval = true, since = "1.19.2")
public class CapabilityEnergy
{
    public static final Capability<IEnergyStorage> ENERGY = CapabilityManager.get(new CapabilityToken<>(){});

    public static void register(RegisterCapabilitiesEvent event){}
}
