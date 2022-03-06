/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.entity.player;

import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("player_name_event_test")
@Mod.EventBusSubscriber()
public class PlayerNameEventTest
{
    private static final boolean ENABLE = false;

    @SubscribeEvent
    public static void onPlayerNameEvent(PlayerEvent.NameFormat event)
    {
        if (!ENABLE) return;
        event.setDisplayname(new TextComponent("Test Name"));
    }
}
