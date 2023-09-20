/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.entity.player;

import com.mojang.logging.LogUtils;
import net.minecraftforge.event.entity.player.TradeWithVillagerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;


/**
 * This tests for {@link TradeWithVillagerEvent} and fires when
 * the player completes a trade with either a Villager or WanderingTrader(must inherit from AbstractVillager).
 * This test shows the player name involved along with the villager name and what the result of the trade is(Count and Item).
 */
@Mod("trade_with_villager_event_test")
@Mod.EventBusSubscriber()
public class TradeWithVillagerEventTest
{

    private static final boolean ENABLE = true;
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onPlayerVillagerTrade(TradeWithVillagerEvent event)
    {
        if (!ENABLE) return;
        LOGGER.info("Player {} traded with villager {} and exchanged for {} {}.",
                event.getEntity().getName().getString(), event.getAbstractVillager().getName().getString(),
                event.getMerchantOffer().getResult().getCount(),
                event.getMerchantOffer().getResult().getDisplayName().getString());
    }
}
