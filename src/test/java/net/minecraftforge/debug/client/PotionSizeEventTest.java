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

package net.minecraftforge.debug.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.Objects;

/**
 * Test mod for {@link net.minecraftforge.client.event.ScreenEvent.PotionSizeEvent}. When enabled, this mod forces the
 * potion indicators in the {@linkplain net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen
 * inventory screen} to either render in compact mode when there are less than or equal to three active effects on the player,
 * or render in classic mode if there are more than three active effects.
 */
@Mod(PotionSizeEventTest.MODID)
public class PotionSizeEventTest
{
    static final String MODID = "potion_size_event_test";
    public static final boolean ENABLED = true;

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
        public static void onPotionSize(ScreenEvent.PotionSizeEvent event)
        {
            if (!ENABLED) return;

            final LocalPlayer player = Objects.requireNonNull(Minecraft.getInstance().player);

            if (player.getActiveEffects().size() <= 3)
            {
                event.setResult(Event.Result.ALLOW); // Force compact mode for 3 or less active effects
            } else
            {
                event.setResult(Event.Result.DENY); // Force classic mode for 4 or more active effects
            }
        }
    }
}
