/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client.rendering;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.LevelRendererHooks;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterLevelRendererHooksEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.client.model.StandaloneModelConfiguration;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.client.model.renderable.BakedRenderable;
import net.minecraftforge.client.model.renderable.IRenderable;
import net.minecraftforge.client.model.renderable.MultipartTransforms;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.Map;

@Mod(RenderableTest.MODID)
public class RenderableTest
{
    public static final String MODID = "renderable_test";

    public static final boolean ENABLED = true; // Renders at (0, 120, 0)
    public static final boolean USE_LEVEL_RENDERER_HOOKS = true; // True when using LevelRendererHooks. False when using RenderLevelLastEvent

    public RenderableTest()
    {
        if (ENABLED)
        {
            if (FMLEnvironment.dist == Dist.CLIENT)
            {
                Client.init();
            }
        }
    }

    private static class Client
    {
        private static ResourceLocation MODEL_LOC = new ResourceLocation("minecraft:block/blue_stained_glass");

        private static IRenderable<MultipartTransforms> renderable;
        private static IRenderable<IModelData> bakedRenderable;

        public static void init()
        {
            var bus = FMLJavaModLoadingContext.get().getModEventBus();
            bus.addListener(Client::registerModels);
            bus.addListener(Client::registerReloadListeners);
            if (USE_LEVEL_RENDERER_HOOKS)
            {
                // Registers level renderer hooks for each of phases, offsetting them on the x axis. AFTER_SKY is at (0, 120, 0)
                FMLJavaModLoadingContext.get().getModEventBus().addListener(Client::registerHooks);
            }
            else
                MinecraftForge.EVENT_BUS.addListener(Client::renderLast);
        }

        private static void registerModels(ModelRegistryEvent t)
        {
            ForgeModelBakery.addSpecialModel(MODEL_LOC);
        }

        public static void registerReloadListeners(RegisterClientReloadListenersEvent event)
        {
            event.registerReloadListener(new SimplePreparableReloadListener<OBJModel>()
            {
                @Override
                protected OBJModel prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller)
                {
                    var settings = new OBJModel.ModelSettings(
                            new ResourceLocation("new_model_loader_test:models/item/sugar_glider.obj"),
                            false,
                            true,
                            true,
                            false,
                            null
                    );
                    return OBJLoader.INSTANCE.loadModel(settings);
                }

                @Override
                protected void apply(OBJModel model, ResourceManager resourceManager, ProfilerFiller profilerFiller)
                {
                    var config = StandaloneModelConfiguration.create(Map.of(
                            "#qr", new ResourceLocation("minecraft:block/quartz_block_top")
                    ));
                    renderable = model.bakeRenderable(config);
                }
            });
        }

        public static void renderLast(RenderLevelLastEvent event)
        {
            Vec3 cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            renderHook(new LevelRendererHooks.RenderContext(event.getLevelRenderer(), event.getPoseStack(), event.getProjectionMatrix(), 0, event.getPartialTick(), cam.x, cam.y, cam.z), 0);
        }

        public static void registerHooks(RegisterLevelRendererHooksEvent event)
        {
            event.register(LevelRendererHooks.Phase.AFTER_SKY, context -> Client.renderHook(context, 0));
            event.register(LevelRendererHooks.Phase.AFTER_SOLID_BLOCKS, context -> Client.renderHook(context, 1));
            event.register(LevelRendererHooks.Phase.AFTER_ENTITIES, context -> Client.renderHook(context, 2));
            event.register(LevelRendererHooks.Phase.AFTER_TRANSLUCENT_BLOCKS, context -> Client.renderHook(context, 3));
            event.register(LevelRendererHooks.Phase.AFTER_PARTICLES, context -> Client.renderHook(context, 4));
            event.register(LevelRendererHooks.Phase.AFTER_CLOUDS, context -> Client.renderHook(context, 5));
            event.register(LevelRendererHooks.Phase.AFTER_WEATHER, context -> Client.renderHook(context, 6));
            event.register(LevelRendererHooks.Phase.LAST, context -> Client.renderHook(context, 7));
        }

        private static void renderHook(LevelRendererHooks.RenderContext context, int xOffset)
        {
            double x = context.camX(), y = context.camY(), z = context.camZ();
            if (!new BlockPos(0, y, 0).closerThan(new BlockPos(x, y, z), 200))
                return;

            PoseStack poseStack = context.poseStack();
            float partialTick = context.partialTick();

            if (bakedRenderable == null)
            {
                bakedRenderable = BakedRenderable.of(MODEL_LOC);
            }

            var bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

            double time = context.ticks() + partialTick;
            
            var map = ImmutableMap.<String, Matrix4f>builder();

            var left = new Matrix4f();
            left.setIdentity();
            left.multiply(Quaternion.fromYXZ(0, 0, (float)Math.sin(time * 0.4) * 0.1f));
            map.put("object_1", left);

            var right = new Matrix4f();
            right.setIdentity();
            right.multiply(Quaternion.fromYXZ(0, 0, -(float)Math.sin(time * 0.4) * 0.1f));
            map.put("object_9", right);

            var transforms = MultipartTransforms.of(map.build());

            poseStack.pushPose();
            poseStack.translate(0 - x + xOffset, 120 - y, 0 - z);
            renderable.render(poseStack, bufferSource, RenderType::entitySolid, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, partialTick, transforms);

            poseStack.translate(0, -1, 0);
            bakedRenderable.render(poseStack, bufferSource, RenderType::entityTranslucent, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, partialTick, EmptyModelData.INSTANCE);
            poseStack.popPose();

            bufferSource.endBatch();
        }
    }
}
