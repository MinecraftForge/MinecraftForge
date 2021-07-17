/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.debug.hunger;

import net.minecraftforge.event.hunger.StarvationEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Tests all of the {@link StarvationEvent}s.
 * Changes made by this mod to test for:
 * - Starvation should occur at all times, even when food level is greater than 0
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
