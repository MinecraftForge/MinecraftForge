/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("player_xp_event_test")
@Mod.EventBusSubscriber()
public class PlayerXpEventTest
{

    private static final boolean ENABLE = false;
    private static Logger logger = LogManager.getLogger(PlayerXpEventTest.class);

    @SubscribeEvent
    public static void onPlayerXpEvent(PlayerXpEvent event)
    {
        if (!ENABLE) return;
        logger.info("The PlayerXpEvent has been called!");
    }

    @SubscribeEvent
    public static void onPlayerPickupXpOld(PlayerPickupXpEvent event)
    {
        if (!ENABLE) return;
        logger.info("The deprecated PlayerPickupXpEvent has been called!");
    }

    @SubscribeEvent
    public static void onPlayerPickupXp(PlayerXpEvent.PickupXp event)
    {
        if (!ENABLE) return;
        logger.info("{} picked up an experience orb worth {}", event.getPlayer().getName().getString(), event.getOrb().getXpValue());
    }

    @SubscribeEvent
    public static void onPlayerXpChange(PlayerXpEvent.XpChange event)
    {
        if (!ENABLE) return;
        logger.info("{} has been given {} experience", event.getPlayer().getName().getString(), event.getAmount());
    }

    @SubscribeEvent
    public static void onPlayerLevelChange(PlayerXpEvent.LevelChange event)
    {
        if (!ENABLE) return;
        logger.info("{} has changed {} levels", event.getPlayer().getName().getString(), event.getLevels());
    }

}
