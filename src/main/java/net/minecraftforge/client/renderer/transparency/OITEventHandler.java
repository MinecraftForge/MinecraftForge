/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.renderer.transparency;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RegisterGlslPreprocessorsEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.event.RenderTargetEvent;
import net.minecraftforge.client.event.ShaderEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL30;

import java.io.IOException;

public class OITEventHandler
{
    @Mod.EventBusSubscriber(modid = "forge", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBus {

        @SubscribeEvent
        public static void onShaderRegistration(RegisterShadersEvent registerShadersEvent) throws IOException
        {
            registerShadersEvent.registerShader(new ShaderInstance(
                    registerShadersEvent.getResourceProvider(), new ResourceLocation("forge", "blit_screen_oit"), DefaultVertexFormat.POSITION_TEX_COLOR
            ), shader -> OITLevelRenderer.getInstance().setBlitToScreenShader(shader));
        }

        @SubscribeEvent
        public static void onShaderProgramCompile(RegisterGlslPreprocessorsEvent event) {
            if (event.getShaderInstance() != null) {
                event.newProcessor(new OITGlslPreprocessor(event.getShaderInstance()));
            }
        }

        @SubscribeEvent
        public static void onRenderTargetSetup(RenderTargetEvent.Create event) {
            OITLevelRenderer.getInstance().initialize(event.getWidth(), event.getHeight(), event.isOnOSX());
        }
    }

    @Mod.EventBusSubscriber(modid = "forge", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeBus {

        @SubscribeEvent
        public static void onRenderTargetResize(RenderTargetEvent.Resize event) {
            OITLevelRenderer.getInstance().getTransparentOITRenderTarget().resize(event.getWidth(), event.getHeight(), event.isOnOSX());
        }
    }
}
