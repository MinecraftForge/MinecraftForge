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

package net.minecraftforge.debug.entity.player;

import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerChangeGameModeEvent;
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
        LOGGER.info("{} changed game mode. Current GameType: {}. New Game Type: {}", event.getPlayer(), event.getCurrentGameMode(), event.getNewGameMode());
        if (event.getNewGameMode() == GameType.SURVIVAL)
            event.setCanceled(true);
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT)
    private static class ClientEvents
    {
        @SubscribeEvent
        public static void onClientPlayerChangeGameModeEvent(ClientPlayerChangeGameModeEvent event)
        {
            if (!ENABLE) return;
            LOGGER.info("Client notified of changed game mode from '{}'. Current GameType: {}. New Game Type: {}", event.getInfo().getGameProfile(), event.getCurrentGameMode(), event.getNewGameMode());
        }
    }
}
