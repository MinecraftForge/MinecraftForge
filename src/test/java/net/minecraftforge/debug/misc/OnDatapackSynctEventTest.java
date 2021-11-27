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

package net.minecraftforge.debug.misc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("send_datapacks_to_client")
@Mod.EventBusSubscriber
public class OnDatapackSynctEventTest
{
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    protected static void onSendDataToClient(final OnDatapackSyncEvent event)
    {
        // Fire for a specific player on login
        if (event.getPlayer() != null)
            syncData(event.getPlayer());
        // Fire for all players on /reload
        else
            event.getPlayerList().getPlayers().forEach(OnDatapackSynctEventTest::syncData);
    }

    private static void syncData(ServerPlayerEntity player)
    {
        LOGGER.info("Sending modded datapack data to {}", player.getName().getString());
    }
}
