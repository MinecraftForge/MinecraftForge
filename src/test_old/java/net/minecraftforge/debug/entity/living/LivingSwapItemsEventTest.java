/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.entity.living;

import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSwapItemsEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("living_swap_items_event_test")
public final class LivingSwapItemsEventTest {
    public static final boolean ENABLE = true;
    public static final Logger LOGGER = LogManager.getLogger();

    public LivingSwapItemsEventTest() {
        if (ENABLE)
            LivingSwapItemsEvent.Hands.BUS.addListener(this::livingSwapItems);
    }

    public boolean livingSwapItems(LivingSwapItemsEvent.Hands event) {
        LOGGER.info("{} swapping hands. To offhand: {}, to main hand: {}", event.getEntity(), event.getItemSwappedToOffHand(), event.getItemSwappedToMainHand());
        return event.getEntity().getMainHandItem().is(Items.DIAMOND);
    }
}
