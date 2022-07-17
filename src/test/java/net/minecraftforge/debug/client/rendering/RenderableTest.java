/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client.rendering;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import net.minecraftforge.client.ForgeRenderTypes;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
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

/**
 * This mod is testing the use of {@link RenderLevelStageEvent} and is a modifaction of a pre-existing test mod that used the old
 * {@link RenderLevelLastEvent}. To restore the old behavior, set {@link #USE_LEVEL_RENDERER_STAGE} to false.
 * 
 * When you enter a world, there should be 6 sugar gliders rendering at (0, 120, 0) that test the various stages in {@link RenderLevelStageEvent}.
 * From left to right (with the sugar gliders facing you) they represent {@link Stage#AFTER_SKY}, {@link Stage#AFTER_SOLID_BLOCKS},
 * {@link Stage#AFTER_TRANSLUCENT_BLOCKS}, {@link Stage#AFTER_TRIPWIRE_BLOCKS}, {@link Stage#AFTER_PARTICLES}, and {@link Stage#AFTER_WEATHER}. 
 * Due to how weather modifies the projection matrix, it's sugar glider will be positioned weirdly. Below each sugar gliders is a render of
 * blue stained glass to test translucency with fabulous graphics. {@link Stage#AFTER_PARTICLES} will render the stained glass using
 * {@link ForgeRenderTypes#TRANSLUCENT_ON_PARTICLES_TARGET}.
 */
@Mod(RenderableTest.MODID)
public class RenderableTest
{
    public static final String MODID = "renderable_test";
    private static final Logger LOGGER = LogManager.getLogger();

    public static final boolean ENABLED = true; // Renders at (0, 120, 0)
    public static final boolean USE_LEVEL_RENDERER_STAGE = true; // True when using RenderLevelStageEvent. False when using RenderLevelLastEvent

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
            var modBus = FMLJavaModLoadingContext.get().getModEventBus();
            var forgeBus = MinecraftForge.EVENT_BUS;
            modBus.addListener(Client::registerModels);
            modBus.addListener(Client::registerReloadListeners);
            if (USE_LEVEL_RENDERER_STAGE)
            {
                modBus.addListener(Client::registerStage);
                forgeBus.addListener(Client::renderStage);
            }
            else
                forgeBus.addListener(Client::renderLast);
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

        public static void registerStage(RenderLevelStageEvent.RegisterStageEvent event)
        {
            var stage = event.register(new ResourceLocation(MODID, "test_stage"), null);
            LOGGER.info("Registered RenderLevelStageEvent.Stage: {}", stage);
        }

        public static void renderLast(RenderLevelLastEvent event)
        {
            Vec3 cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            render(Stage.AFTER_SKY, event.getPoseStack(), 0, event.getPartialTick(), cam.x, cam.y, cam.z, 0);
        }

        private static void renderStage(RenderLevelStageEvent event)
        {
            int xOffset = -1;
            var stage = event.getStage();
            if (stage == Stage.AFTER_SKY)
                xOffset = 0;
            else if (stage == Stage.AFTER_SOLID_BLOCKS)
                xOffset = 1;
            else if (stage == Stage.AFTER_TRANSLUCENT_BLOCKS)
                xOffset = 2;
            else if (stage == Stage.AFTER_TRIPWIRE_BLOCKS)
                xOffset = 3;
            else if (stage == Stage.AFTER_PARTICLES)
                xOffset = 4;
            else if (stage == Stage.AFTER_WEATHER)
                xOffset = 5;

            Vec3 cam = event.getCamera().getPosition();
            if (xOffset > -1)
                render(stage, event.getPoseStack(), event.getRenderTick(), event.getPartialTick(), cam.x, cam.y, cam.z, xOffset);
        }

        private static void render(Stage stage, PoseStack poseStack, int renderTick, float partialTick, double camX, double camY, double camZ, int xOffset)
        {
            double x = camX, y = camY, z = camZ;
            if (!new BlockPos(0, y, 0).closerThan(new BlockPos(x, y, z), 100))
                return;

            var profiler = Minecraft.getInstance().getProfiler();
            profiler.push("renderable_test");
            if (bakedRenderable == null)
            {
                bakedRenderable = BakedRenderable.of(MODEL_LOC);
            }

            var bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

            double time = renderTick + partialTick;

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
            bakedRenderable.render(poseStack, bufferSource, texture -> getRenderType(stage, texture), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, partialTick, EmptyModelData.INSTANCE);
            poseStack.popPose();

            bufferSource.endBatch();
            profiler.pop();
        }

        private static RenderType getRenderType(Stage stage, ResourceLocation texture)
        {
            if (stage == Stage.AFTER_PARTICLES)
                return ForgeRenderTypes.TRANSLUCENT_ON_PARTICLES_TARGET.get();
            return RenderType.entityTranslucent(texture);
        }
    }
}
