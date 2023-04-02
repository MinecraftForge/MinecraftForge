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
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.client.model.geometry.StandaloneGeometryBakingContext;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.obj.ObjLoader;
import net.minecraftforge.client.model.obj.ObjModel;
import net.minecraftforge.client.model.renderable.BakedModelRenderable;
import net.minecraftforge.client.model.renderable.CompositeRenderable;
import net.minecraftforge.client.model.renderable.IRenderable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.joml.Matrix4f;

/**
 * This mod is testing the use of {@link RenderLevelStageEvent} and is a modifaction of a pre-existing test mod that used the old
 * RenderLevelLastEvent. To restore the old behavior, set {@link #USE_LEVEL_RENDERER_STAGE} to false.
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

    public static final boolean ENABLED = false; // Renders at (0, 120, 0)
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

        private static IRenderable<CompositeRenderable.Transforms> renderable;
        private static IRenderable<ModelData> bakedRenderable;

        public static void init()
        {
            var modBus = FMLJavaModLoadingContext.get().getModEventBus();
            var forgeBus = MinecraftForge.EVENT_BUS;
            modBus.addListener(Client::registerModels);
            modBus.addListener(Client::registerReloadListeners);
            modBus.addListener(Client::registerStage);
            forgeBus.addListener(Client::renderStage);
        }

        private static void registerModels(ModelEvent.RegisterAdditional event)
        {
            event.register(MODEL_LOC);
        }

        public static void registerReloadListeners(RegisterClientReloadListenersEvent event)
        {
            event.registerReloadListener(new SimplePreparableReloadListener<ObjModel>()
            {
                @Override
                protected ObjModel prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller)
                {
                    var settings = new ObjModel.ModelSettings(
                            new ResourceLocation("new_model_loader_test:models/item/sugar_glider.obj"),
                            false,
                            true,
                            true,
                            false,
                            null
                    );
                    return ObjLoader.INSTANCE.loadModel(settings);
                }

                @Override
                protected void apply(ObjModel model, ResourceManager resourceManager, ProfilerFiller profilerFiller)
                {
                    var config = StandaloneGeometryBakingContext.create(Map.of(
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
            if (!BlockPos.containing(0, y, 0).closerThan(BlockPos.containing(x, y, z), 100))
                return;

            var profiler = Minecraft.getInstance().getProfiler();
            profiler.push("renderable_test");
            if (bakedRenderable == null)
            {
                bakedRenderable = BakedModelRenderable.of(MODEL_LOC).withModelDataContext();
            }

            var bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

            double time = renderTick + partialTick;

            var map = ImmutableMap.<String, Matrix4f>builder();

            var left = new Matrix4f();
            left.rotation((float)Math.sin(time * 0.4) * 0.1f, 0, 0, 1);
            map.put("object_1", left);

            var right = new Matrix4f();
            right.rotation(-(float)Math.sin(time * 0.4) * 0.1f, 0, 0, 1);
            map.put("object_9", right);

            var transforms = CompositeRenderable.Transforms.of(map.build());

            poseStack.pushPose();
            poseStack.translate(0 - x + xOffset, 120 - y, 0 - z);
            renderable.render(poseStack, bufferSource, RenderType::entitySolid, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, partialTick, transforms);

            poseStack.translate(0, -1, 0);
            bakedRenderable.render(poseStack, bufferSource, texture -> getRenderType(stage, texture), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, partialTick, ModelData.EMPTY);
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
