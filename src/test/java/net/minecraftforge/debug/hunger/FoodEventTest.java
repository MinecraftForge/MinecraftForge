/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.hunger;

import net.minecraft.world.item.Items;
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
 * - Apple's FoodValues are changed to 19 nutrition and 36 saturation
 * - All other foods have their food values changed to be (20 - playerFoodLevel) / 8, with a 1.0 saturation modifier
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
        // All other foods will have their nutrition scaled based on the current player's food level.
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
    public static void onFoodStatsAddition(FoodEvent.FoodDataAddition event)
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
        // If it added more than 1 nutrition, heal the player one half of a heart
        if (ENABLED)
        {
            LOGGER.info(event.getPlayer().getDisplayName().getString() + " ate " + event.getItemStack().toString());
            if (event.getNutritionAdded() >= 1)
            {
                event.getPlayer().heal(1.0F);
            }
        }
    }
}
