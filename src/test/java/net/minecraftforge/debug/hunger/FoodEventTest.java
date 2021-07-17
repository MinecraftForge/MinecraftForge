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

import net.minecraft.item.Items;
import net.minecraftforge.common.hunger.FoodValues;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.hunger.FoodEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Tests all of the {@link FoodEvent}s.
 * Changes made by this mod to test for:
 * - Apple's FoodValues are changed to 19 hunger and 1 saturation
 * - All other foods have their food values changed to be (20 - playerFoodLevel) / 8, with 1 saturation
 * - The player should not be allowed to increase their hunger or saturation if their hunger is already over halfway filled
 * - If the player just ate food that healed at least 1 hunger point, heal the player half a heart
 */
@Mod("food_event_test")
@Mod.EventBusSubscriber
public class FoodEventTest
{
    private static final boolean ENABLED = false;
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onGetFoodValues(FoodEvent.GetFoodValues event)
    {
        // Apples should now restore 19 hunger and provide 1 saturation.
        // All other foods will have their hunger points scaled based on the current player's food level.
        if (ENABLED && event.getPlayer() != null)
        {
            if (event.getItemStack().getItem() == Items.APPLE)
            {
                event.setFoodValues(new FoodValues(19, 1.0F));
            }
            else
            {
                event.setFoodValues(new FoodValues((20 - event.getPlayer().getFoodData().getFoodLevel()) / 8, 1.0F));
            }
        }
    }

    @SubscribeEvent
    public static void onFoodStatsAddition(FoodEvent.FoodStatsAddition event)
    {
        // If the player's hunger level is over halfway filled, cancel the food stats addition
        if (ENABLED && event.getPlayer().getFoodData().getFoodLevel() > ForgeEventFactory.getMaxHunger(event.getPlayer()) / 2)
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onFoodEaten(FoodEvent.FoodEaten event)
    {
        // Log what was eaten
        // If it added more than 1 hunger point, heal the player one half of a heart
        if (ENABLED)
        {
            LOGGER.info(event.getPlayer().getDisplayName().getString() + " ate " + event.getItemStack().toString());
            if (event.getHungerAdded() >= 1)
            {
                event.getPlayer().heal(1.0F);
            }
        }
    }
}
