/*
 * Minecraft Forge
 * Copyright (c) 2016-2022.
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

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("player_login_event_test")
@Mod.EventBusSubscriber()
public class PlayerLoginEventTest
{
    private static final Logger LOGGER = LogManager.getLogger(PlayerLoginEventTest.class);
    private static final boolean KICK_ENABLED = false;
    private static final boolean ENABLED = true;

    @SubscribeEvent
    public static void onPrePlayerLoginEvent(PlayerEvent.PrePlayerLoginEvent event)
    {
        if (ENABLED)
        {
            LOGGER.info("{} logged in with connection {}.", event.getPlayer().getName(), event.getConnection());
            if (KICK_ENABLED)
            {
                event.setDenyMessage(new TextComponent("This is a test disconnect\nTesting the new line").withStyle(ChatFormatting.BLUE));
                event.setResult(Event.Result.DENY);
            }
        }
    }
}
