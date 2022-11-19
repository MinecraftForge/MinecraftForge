/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.entity.living;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.SwapHandItemsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod("swap_hand_items_event_test")
public class SwapHandItemsEventTest {
    public static final boolean ENABLE = true;

    private static final Logger LOGGER = LogUtils.getLogger();

    public SwapHandItemsEventTest() {
        if (ENABLE) MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onSwapHandItems(final SwapHandItemsEvent event) {
        LOGGER.info("Main hand: {}", event.getMainHand());
        LOGGER.info("Off-hand: {}", event.getOffHand());
        if (event.getEntity().isSprinting())
            event.setCanceled(true);
    }
}
