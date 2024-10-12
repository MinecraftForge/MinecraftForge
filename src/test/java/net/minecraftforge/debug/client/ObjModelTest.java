/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Tests loading and rendering an obj-model using the using Forge's {@link net.minecraftforge.client.model.obj.ObjLoader}.
 * When enabled, this will render a pyramid loaded from an obj model at coordinates 0,0,0.
 */
@Mod(ObjModelTest.MODID)
public class ObjModelTest {
    private static final boolean ENABLED = true;

    public static final String MODID = "obj_model_test";
    private static final PoseStack POSE_STACK = new PoseStack();
    private static final ModelResourceLocation PYRAMID_MODEL_LOCATION = new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(MODID, "pyramid"), "");
    private static BakedModel pyramidModel;

    public ObjModelTest(FMLJavaModLoadingContext ctx) {
        if (ENABLED) {
            ctx.getModEventBus().addListener(ObjModelTest::registerObjModel);
            ctx.getModEventBus().addListener(ObjModelTest::collectObjModel);
            MinecraftForge.EVENT_BUS.addListener(ObjModelTest::renderModel);
        }
    }

    public static void registerObjModel(ModelEvent.RegisterAdditional e) {
        e.register(PYRAMID_MODEL_LOCATION);
    }

    public static void collectObjModel(ModelEvent.BakingCompleted e) {
        pyramidModel = e.getModels().get(PYRAMID_MODEL_LOCATION);
    }

    public static void renderModel(RenderLevelStageEvent e) {
        if (e.getStage() == RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) {
            POSE_STACK.setIdentity();
            Vec3 camera = e.getCamera().getPosition();
            //noinspection removal
            POSE_STACK.mulPose(e.getProjectionMatrix());
            POSE_STACK.translate(0.5 - camera.x, 0.5 - camera.y, 0.5 - camera.z);
            MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            // Render the obj model
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                POSE_STACK.last(),
                bufferSource.getBuffer(RenderType.solid()),
                null,
                pyramidModel,
                1,
                1,
                1,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                ModelData.EMPTY,
                RenderType.solid()
            );
            bufferSource.endBatch();
        }
    }
}
