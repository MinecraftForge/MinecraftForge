/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client.rendering;

import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Test protection of pre-render events against unbalanced {@link com.mojang.blaze3d.vertex.PoseStack} modifications.<br>
 *
 * <p>
 * The {@link com.mojang.blaze3d.vertex.PoseStack} is pushed and modified in {@link RenderLivingEvent.Pre} and popped in
 * {@link RenderLivingEvent.Post}. A second, lower priority {@link RenderLivingEvent.Pre} handler cancels the event,
 * causing an unbalanced modification of the {@link com.mojang.blaze3d.vertex.PoseStack} that would lead to a crash.
 * </p>
 *
 * <p>
 * The {@link com.mojang.blaze3d.vertex.PoseStack} modification is applied to pigs and crouching will cancel
 * the rendering of pigs.
 * </p>
 */
@Mod(RenderLivingEventTest.MOD_ID)
public class RenderLivingEventTest
{
    public static final String MOD_ID = "render_living_event_test";
    private static final boolean ENABLED = false;

    @Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    private static class ClientEvents
    {
        @SubscribeEvent
        public static void onLivingRenderPreChangePose(RenderLivingEvent.Pre<LivingEntity, EntityModel<LivingEntity>> event)
        {
            if (!ENABLED) return;

            if (event.getEntity() instanceof Pig)
            {
                event.getPoseStack().pushPose();
                event.getPoseStack().translate(.5, .5, .5);
                event.getPoseStack().mulPose(Axis.ZP.rotationDegrees(180));
                event.getPoseStack().translate(-.5, -.5, -.5);
            }
        }

        @SubscribeEvent
        public static void onLivingRenderPostChangePose(RenderLivingEvent.Post<LivingEntity, EntityModel<LivingEntity>> event)
        {
            if (!ENABLED) return;

            if (event.getEntity() instanceof Pig)
            {
                event.getPoseStack().popPose();
            }
        }

        @SubscribeEvent(priority = EventPriority.LOW)
        public static void onLivingRenderPreCancel(RenderLivingEvent.Pre<LivingEntity, EntityModel<LivingEntity>> event)
        {
            if (!ENABLED) return;

            if (event.getEntity() instanceof Pig && Minecraft.getInstance().player.isShiftKeyDown())
            {
                event.setCanceled(true);
            }
        }
    }
}
