/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.hunger;

import net.minecraftforge.event.hunger.StarvationEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Tests all of the {@link StarvationEvent}s.
 * Changes made by this mod to test for:
 * - Starvation should occur at all times, even when nutrition is greater than 0
 * - Starvation tick period doubled from 80 -> 160
 * - Starvation damage will always be half a heart. Even when half a heart is left.
 */
@Mod("starvation_event_test")
@Mod.EventBusSubscriber
public class StarvationEventTest
{
    private static final boolean ENABLED = false;

    @SubscribeEvent
    public static void onAllowStarvation(StarvationEvent.AllowStarvation event)
    {
        // Starvation should occur at all times
        if (ENABLED) event.setResult(Event.Result.ALLOW);
    }

    @SubscribeEvent
    public static void onGetStarveTickPeriod(StarvationEvent.GetStarveTickPeriod event)
    {
        // Double the default starvation tick period
        if (ENABLED) event.setStarveTickPeriod(160);
    }

    @SubscribeEvent
    public static void onStarve(StarvationEvent.Starve event)
    {
        // Always deal half a heart of damage
        if (ENABLED) event.setStarveDamage(1);
    }
}
