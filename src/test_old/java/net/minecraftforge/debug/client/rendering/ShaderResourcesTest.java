/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.client.rendering;

import java.io.IOException;

import org.slf4j.Logger;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.logging.LogUtils;

import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftsingularity.api.distmarker.Dist;
import net.minecraftsingularity.client.event.RegisterShadersEvent;
import net.minecraftsingularity.eventbus.api.listener.SubscribeEvent;
import net.minecraftsingularity.fml.DistExecutor;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftsingularity.fml.loading.FMLEnvironment;

@Mod(ShaderResourcesTest.MODID)
public class ShaderResourcesTest
{
    private static Logger LOGGER;

    public static final String MODID = "shader_resources_test";
    private static final boolean ENABLE = false;

    public ShaderResourcesTest()
    {
        if (ENABLE)
        {
            LOGGER = LogUtils.getLogger();

            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,() -> ClientInit::new);
        }
    }

    private class ClientInit
    {
        public ClientInit()
        {
            final var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

            modEventBus.addListener(ClientInit::registerShaders);
        }

        public static void registerShaders(final RegisterShadersEvent event)
        {
            if (!ENABLE)
                return;

            try
            {
                event.registerShader(
                        new ShaderInstance(
                                event.getResourceProvider(),
                                new ResourceLocation(MODID, "vertex_cubemap"),
                                DefaultVertexFormat.POSITION),
                        shader ->
                        {
                            LOGGER.info("Completely loaded shader {} with no issues", shader.getName());
                        });

                LOGGER.info("Loaded registered shaders with no exceptions");
            }
            catch (IOException e)
            {
                LOGGER.error("Failed to load shaders with exception", e);
            }
        }
    }
}
