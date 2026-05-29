/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftsingularity.api.distmarker.Dist;
import net.minecraftsingularity.client.event.RegisterKeyMappingsEvent;
import net.minecraftsingularity.common.MinecraftForge;
import net.minecraftsingularity.event.TickEvent;
import net.minecraftsingularity.eventbus.api.listener.SubscribeEvent;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.fml.event.lifecycle.FMLConstructModEvent;

@Mod("keymapping_test")
public class KeyMappingTest
{
    @Mod.EventBusSubscriber(modid = "keymapping_test", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientStuff {
        // these are two separate keys to stand in for keys added by different
        // mods that each do something similar with a held item from the
        // respective mod, so the user wants them on the same physical key.
        static KeyMapping stickKey = new KeyMapping("stick_key", InputConstants.KEY_BACKSLASH, KeyMapping.CATEGORY_MISC);
        static KeyMapping rockKey = new KeyMapping("rock_key", InputConstants.KEY_BACKSLASH, KeyMapping.CATEGORY_MISC);

        @SubscribeEvent
        public static void initKeys(RegisterKeyMappingsEvent event) {
            event.register(stickKey);
            event.register(rockKey);
        }
        @SubscribeEvent
        public static void tick(TickEvent.ClientTickEvent.Post event) {
            if(stickKey.consumeClick()) {
                Player player = Minecraft.getInstance().player;
                if(player != null && player.getMainHandItem().is(Items.STICK))
                    player.sendSystemMessage(Component.literal("stick found!"));
            }
            if(rockKey.consumeClick()) {
                Player player = Minecraft.getInstance().player;
                if(player != null && player.getMainHandItem().is(Items.COBBLESTONE))
                    player.sendSystemMessage(Component.literal("rock found!"));
            }
        }
    }
}
