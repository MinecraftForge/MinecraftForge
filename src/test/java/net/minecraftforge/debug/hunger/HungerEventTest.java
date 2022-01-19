/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.hunger;

import net.minecraftforge.event.hunger.HungerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Tests all of the {@link HungerEvent}s.
 * Changes made by this mod to test for:
 * - Maximum hunger is increased from 20 -> 60.
 *   - It should look visually the same on the HUD, but you should notice it visually changes less often.
 *   - You can also do a `/hunger list` if you have the HungerCommandTest mod enabled.
 */
@Mod("hunger_event_test")
@Mod.EventBusSubscriber
public class HungerEventTest
{
    private static final boolean ENABLED = false;

    @SubscribeEvent
    public static void onGetMaxHunger(HungerEvent.GetMaxHunger event)
    {
        // Increase maximum hunger to 60
        if (ENABLED) event.setMaxHunger(60);
    }
}
