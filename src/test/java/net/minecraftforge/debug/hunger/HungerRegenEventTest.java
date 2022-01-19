/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.hunger;

import net.minecraftforge.event.hunger.HungerRegenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Tests all of the {@link HungerRegenEvent}s.
 * Changes made by this mod to test for:
 * - On peaceful difficulty, hunger should regen until it is over 10 points
 */
@Mod("hunger_regen_event_test")
@Mod.EventBusSubscriber
public class HungerRegenEventTest
{
    private static final boolean ENABLED = false;

    @SubscribeEvent
    public static void onPeacefulRegen(HungerRegenEvent.PeacefulRegen event)
    {
        // Peaceful regen of hunger will occur until it is over 10 hunger points filled
        if (ENABLED) event.setDeltaHunger(event.getPlayer().getFoodData().getFoodLevel() <= 10 ? 1 : 0);
    }
}
