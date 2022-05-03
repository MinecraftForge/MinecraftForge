/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(RenderLocalPlayerTest.MODID)
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class RenderLocalPlayerTest
{
    public static final String MODID = "render_local_player_test";
    static final boolean ENABLED = false;

    @SubscribeEvent
    public static void onItemRightClickEntity(final PlayerInteractEvent.EntityInteract event)
    {
        if (ENABLED && event.getItemStack().getItem() == Items.STICK)
        {
            Minecraft mc = Minecraft.getInstance();
            if (mc.getCameraEntity() == mc.player)
            {
                mc.setCameraEntity(event.getTarget());

                event.setCancellationResult(ActionResultType.SUCCESS);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onItemRightClick(final PlayerInteractEvent.RightClickItem event)
    {
        if (ENABLED && event.getItemStack().getItem() == Items.STICK)
        {
            Minecraft mc = Minecraft.getInstance();
            if (mc.getCameraEntity() != mc.player)
            {
                mc.setCameraEntity(mc.player);

                event.setCancellationResult(ActionResultType.SUCCESS);
                event.setCanceled(true);
            }
        }
    }
}