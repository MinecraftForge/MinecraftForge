/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.Objects;

/**
 * Test mod for {@link ScreenEvent.RenderInventoryMobEffects}. When enabled, this mod forces the
 * potion indicators in the {@linkplain net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen
 * inventory screen} to either render in compact mode when there are less than or equal to three active effects on the player,
 * or render in classic mode if there are more than three active effects.
 */
@Mod(PotionSizeEventTest.MODID)
public class PotionSizeEventTest
{
    static final String MODID = "potion_size_event_test";
    public static final boolean ENABLED = false;

    public PotionSizeEventTest()
    {
        if (ENABLED && FMLEnvironment.dist == Dist.CLIENT)
        {
            MinecraftForge.EVENT_BUS.register(ClientEventHandler.class);
        }
    }

    static final class ClientEventHandler
    {
        @SubscribeEvent
        public static void onPotionSize(ScreenEvent.RenderInventoryMobEffects event)
        {
            if (!ENABLED) return;

            final LocalPlayer player = Objects.requireNonNull(Minecraft.getInstance().player);

            if (player.getActiveEffects().size() <= 3)
            {
                event.setCompact(true); // Force compact mode for 3 or less active effects
            }
            else
            {
                event.setCompact(false); // Force classic mode for 4 or more active effects
            }
            if (player.hasEffect(MobEffects.MOVEMENT_SLOWDOWN))
            {
                event.addHorizontalOffset(20); // Move the effect rendering to the right when slowness is enabled
            }
        }
    }
}
