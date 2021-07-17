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

import net.minecraftforge.event.hunger.HealthRegenEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Tests all of the {@link HealthRegenEvent}s.
 * Changes made by this mod to test for:
 * - Normal health regen (unsaturated)
 *   - Should occur no matter the food level or the state of the "naturalRegeneration" gamerule
 *   - Tick period reduced from 80 -> 10
 *   - Should always heal one full heart at a time
 *   - Exhaustion added halved from 6.0 -> 3.0
 * - Health regeneration in peaceful difficulty should not occur
 * - Saturated health regen
 *   - Should be allowed under the default vanilla logic
 *   - Tick period doubled from 10 -> 20
 *   - Should always heal half a heart at a time
 *   - Exhaustion added held constant at 1.0
 */
@Mod("health_regen_event_test")
@Mod.EventBusSubscriber
public class HealthRegenEventTest
{
    private static final boolean ENABLED = false;

    @SubscribeEvent
    public static void onAllowHealthRegen(HealthRegenEvent.AllowRegen event)
    {
        // Allow health regen at all times, unless the player is already fully healed. Meaning:
        // - ignore food level
        // - ignore naturalRegeneration gamerule
        if (ENABLED && event.getPlayer().isHurt()) event.setResult(Event.Result.ALLOW);
    }

    @SubscribeEvent
    public static void onGetRegenTickPeriod(HealthRegenEvent.GetRegenTickPeriod event)
    {
        // Reduce health regen tick period to 10 ticks
        if (ENABLED) event.setRegenTickPeriod(10);
    }

    @SubscribeEvent
    public static void onRegen(HealthRegenEvent.Regen event)
    {
        // Heal a whole heart at a time, and halve the amount of exhaustion added
        if (ENABLED)
        {
            event.setDeltaHealth(2.0F);
            event.setDeltaExhaustion(3.0F);
        }
    }

    @SubscribeEvent
    public static void onPeacefulRegen(HealthRegenEvent.PeacefulRegen event)
    {
        // Disable health regen in peaceful
        if (ENABLED) event.setDeltaHealth(0.0F);
    }

    @SubscribeEvent
    public static void onAllowSaturatedRegen(HealthRegenEvent.AllowSaturatedRegen event)
    {
        // Use the default vanilla logic
        if (ENABLED) event.setResult(Event.Result.DEFAULT);
    }

    @SubscribeEvent
    public static void onGetSaturatedRegenTickPeriod(HealthRegenEvent.GetSaturatedRegenTickPeriod event)
    {
        // Double the normal saturated regen tick period
        if (ENABLED) event.setRegenTickPeriod(20);
    }

    @SubscribeEvent
    public static void onSaturatedRegen(HealthRegenEvent.SaturatedRegen event)
    {
        // Always heal half a heart, and only add 1 exhaustion each time
        if (ENABLED)
        {
            event.setDeltaHealth(1);
            event.setDeltaExhaustion(1.0F);
        }
    }
}
