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

import net.minecraftforge.event.hunger.ExhaustionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Tests all of the {@link ExhaustionEvent}s.
 * Changes made by this mod to test for:
 * - Maximum exhaustion level a player can have increased from 4.0 -> 30.0
 * - Maximum exhaustion addition at one time increased from 40.0 -> 90.0
 * - Non-sprinting jumps should apply a random amount of exhaustion each time
 * - Sprinting jumps should apply 90 exhaustion each time
 * - All other exhausting actions should be 1.5x their normal rate
 * - Hunger loss should occur in peaceful difficulty
 */
@Mod("exhaustion_event_test")
@Mod.EventBusSubscriber
public class ExhaustionEventTest
{
    private static final boolean ENABLED = false;
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onGetMaxExhaustion(ExhaustionEvent.GetMaxExhaustion event)
    {
        // Increase the maximum exhaustion level to 30
        if (ENABLED) event.setMaxExhaustionLevel(30.0F);
    }

    @SubscribeEvent
    public static void onExhausted(ExhaustionEvent.Exhausted event)
    {
        // This will enable hunger loss in peaceful difficulty
        if (ENABLED)
        {
            if (event.getPlayer().getFoodData().getSaturationLevel() <= 0)
            {
                event.setDeltaHunger(-1);
            }
            LOGGER.info("onExhausted exhaustion=" + event.getCurrentExhaustionLevel());
        }
    }

    @SubscribeEvent
    public static void onExhaustionAdded(ExhaustionEvent.ExhaustionAdded event)
    {
        if (ENABLED)
        {
            // Randomize exhaustion for each normal jump
            if (event.getAction() == ExhaustionEvent.ExhaustingActions.NORMAL_JUMP)
            {
                event.setDeltaExhaustion((float)Math.random());
            }
            // Apply a large amount of exhaustion for each sprinting jump
            // Note: This is over the default addition cap of 40, but also over the modified cap of 90 below.
            //       So in theory, this should actually only do 90 units of exhaustion
            else if (event.getAction() == ExhaustionEvent.ExhaustingActions.SPRINTING_JUMP)
            {
                event.setDeltaExhaustion(100.0F);
            }
            // Otherwise, scale all exhaustion additions by 1.5x
            else
            {
                event.setDeltaExhaustion(event.getDeltaExhaustion() * 1.5F);
            }
        }
    }

    @SubscribeEvent
    public static void onGetExhaustionCap(ExhaustionEvent.GetExhaustionCap event)
    {
        // Increase the max exhaustion addition cap to 90
        if (ENABLED) event.setExhaustionLevelCap(90.0F);
    }
}
