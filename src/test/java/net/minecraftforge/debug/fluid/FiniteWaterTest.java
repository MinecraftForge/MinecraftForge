/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.fluid;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("finite_water_test")
@Mod.EventBusSubscriber()
public class FiniteWaterTest
{
    private static final boolean ENABLED = false;

    @SubscribeEvent
    public static void handleFiniteWaterSource(BlockEvent.CreateFluidSourceEvent event)
    {
        if (ENABLED)
        {
            BlockState state = event.getState();
            FluidState fluidState = state.getFluidState();
            if (fluidState.getType().isSame(Fluids.WATER))
            {
                event.setResult(Event.Result.DENY);
            }
            else if (fluidState.getType().isSame(Fluids.LAVA))
            {
                event.setResult(Event.Result.ALLOW);
            }
        }
    }
}
