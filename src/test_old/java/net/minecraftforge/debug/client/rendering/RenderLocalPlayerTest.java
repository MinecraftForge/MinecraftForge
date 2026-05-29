/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.client.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionResult;
import net.minecraftsingularity.api.distmarker.Dist;
import net.minecraftsingularity.event.entity.player.PlayerInteractEvent;
import net.minecraftsingularity.eventbus.api.listener.SubscribeEvent;
import net.minecraftsingularity.fml.common.Mod;

@Mod(RenderLocalPlayerTest.MODID)
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public final class RenderLocalPlayerTest {
    public static final String MODID = "render_local_player_test";
    static final boolean ENABLED = false;

    @SubscribeEvent
    public static boolean onItemRightClickEntity(final PlayerInteractEvent.EntityInteract event) {
        if (ENABLED && event.getItemStack().getItem() == Items.STICK) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.getCameraEntity() == mc.player) {
                mc.setCameraEntity(event.getTarget());

                event.setCancellationResult(InteractionResult.SUCCESS);
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static boolean onItemRightClick(final PlayerInteractEvent.RightClickItem event) {
        if (ENABLED && event.getItemStack().getItem() == Items.STICK) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.getCameraEntity() != mc.player) {
                mc.setCameraEntity(mc.player);

                event.setCancellationResult(InteractionResult.SUCCESS);
                return true;
            }
        }
        return false;
    }
}
