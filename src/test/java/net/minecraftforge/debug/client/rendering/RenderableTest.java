/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client.rendering;

import com.google.common.collect.ImmutableMap;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
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

import java.util.Map;

@Mod(RenderableTest.MODID)
public class RenderableTest
{
    public static final String MODID = "renderable_test";

    public static final boolean ENABLED = false;

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
        private static ResourceLocation MODEL_LOC = new ResourceLocation("minecraft:block/diamond_block");

        private static IRenderable<CompositeRenderable.Transforms> renderable;
        private static IRenderable<ModelData> bakedRenderable;

        public static void init()
        {
            var bus = FMLJavaModLoadingContext.get().getModEventBus();
            bus.addListener(Client::registerModels);
            bus.addListener(Client::registerReloadListeners);
            MinecraftForge.EVENT_BUS.addListener(Client::renderLast);
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

        private static double time;

        public static void renderLast(RenderLevelLastEvent event)
        {
            if (bakedRenderable == null)
            {
                bakedRenderable = BakedModelRenderable.of(MODEL_LOC).withModelDataContext();
            }

            var poseStack = event.getPoseStack();
            var bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

            time += Minecraft.getInstance().getDeltaFrameTime();

            var map = ImmutableMap.<String, Matrix4f>builder();

            var left = new Matrix4f();
            left.setIdentity();
            left.multiply(Quaternion.fromYXZ(0, 0, (float)Math.sin(time * 0.05) * 0.1f));
            map.put("object_1", left);

            var right = new Matrix4f();
            right.setIdentity();
            right.multiply(Quaternion.fromYXZ(0, 0, -(float)Math.sin(time * 0.05) * 0.1f));
            map.put("object_9", right);

            var transforms = CompositeRenderable.Transforms.of(map.build());

            renderable.render(poseStack, bufferSource, RenderType::entitySolid, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, event.getPartialTick(), transforms);

            poseStack.pushPose();
            poseStack.translate(0,0.5f,0);
            bakedRenderable.render(poseStack, bufferSource, RenderType::entitySolid, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, event.getPartialTick(), ModelData.EMPTY);
            poseStack.popPose();

            bufferSource.endBatch();
        }
    }
}
