/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.entity.living;

import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSwapHandItemsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("living_swap_hand_items_event_test")
public class LivingSwapHandItemsEventTest
{
    public static final boolean ENABLE = true;
    public static final Logger LOGGER = LogManager.getLogger();

    public LivingSwapHandItemsEventTest()
    {
        if (ENABLE)
            MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void livingSwapHandItems(LivingSwapHandItemsEvent event)
    {
        LOGGER.info("{} swapping hands from {} to {}", event.getEntity(), event.getOffHandItem(), event.getMainHandItem());
        if (event.getEntity().getMainHandItem().is(Items.DIAMOND))
            event.setCanceled(true);
    }
}
