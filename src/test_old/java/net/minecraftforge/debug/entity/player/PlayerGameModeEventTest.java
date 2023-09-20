/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.entity.player;

import net.minecraft.world.level.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerChangeGameTypeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("player_game_mode_event_test")
@Mod.EventBusSubscriber()
public class PlayerGameModeEventTest
{
    private static final boolean ENABLE = false;
    private static final Logger LOGGER = LogManager.getLogger(PlayerGameModeEventTest.class);

    @SubscribeEvent
    public static void onPlayerChangeGameModeEvent(PlayerEvent.PlayerChangeGameModeEvent event)
    {
        if (!ENABLE) return;
        LOGGER.info("{} changed game mode. Current GameType: {}. New Game Type: {}", event.getEntity(), event.getCurrentGameMode(), event.getNewGameMode());
        // prevent changing to SURVIVAL
        if (event.getNewGameMode() == GameType.SURVIVAL)
        {
            event.setCanceled(true);
        }
        else if (event.getNewGameMode() == GameType.SPECTATOR)
        {
            // when changing to SPECTATOR, change to SURVIVAL instead
            event.setNewGameMode(GameType.SURVIVAL);
        }
    }

    @Mod.EventBusSubscriber(modid="player_game_mode_event_test", value=Dist.CLIENT, bus=Mod.EventBusSubscriber.Bus.FORGE)
    public static class PlayerGameModeEventTestClientForgeEvents
    {
        @SubscribeEvent
        public static void onClientPlayerChangeGameModeEvent(ClientPlayerChangeGameTypeEvent event)
        {
            if (!ENABLE) return;
            LOGGER.info("Client notified of changed game mode from '{}'. Current GameType: {}. New Game Type: {}", event.getInfo().getProfile(), event.getCurrentGameType(), event.getNewGameType());
        }
    }
}
