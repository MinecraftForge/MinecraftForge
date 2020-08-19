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
