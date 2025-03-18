/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.debug.client;
 
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.AddFramePassEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.test.BaseTestMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.framegraph.FramePass;


@Mod(RenderFrameLayerTest.MODID)
@GameTestHolder("forge." + RenderFrameLayerTest.MODID)
public class RenderFrameLayerTest extends BaseTestMod {
    public static final String MODID = "render_frame_layer_test";
    private static final boolean ENABLED = false;

    public RenderFrameLayerTest(FMLJavaModLoadingContext context) {
        super(context, ENABLED);
        if (ENABLED) {
            MinecraftForge.EVENT_BUS.addListener(RenderFrameLayerTest::renderTest);
        }
        
    }
    
    private static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void test_test(GameTestHelper helper) {
        helper.succeed();
    }
    
    @SubscribeEvent
    public static void renderTest(AddFramePassEvent event) {
        FramePass pass = event.createPass(rl(MODID));
        event.bundle.main = pass.readsAndWrites(event.bundle.main);
        
        var pl = Minecraft.getInstance().gameRenderer.getMainCamera();
        pass.executes(() -> {
            PoseStack ps = new PoseStack();
            ps.translate(pl.getPosition().multiply(-1,-1,-1));
            ps.pushPose();
            var buffSource = Minecraft.getInstance().renderBuffers().bufferSource();
            var vc = buffSource.getBuffer(RenderType.lines());
            
            ShapeRenderer.renderLineBox(ps, vc, 0, -60, 0, 10, -50, 10, 1f, 1f, 1f, 1f);
            buffSource.endBatch();
            ps.popPose();
        });
        pass = event.createPass(rl(MODID+"2"));
        event.bundle.main = pass.readsAndWrites(event.bundle.main);
        
        pass.executes(() -> {
            PoseStack ps = new PoseStack();
            ps.translate(pl.getPosition().multiply(-1,-1,-1));
            ps.pushPose();
            var buffSource = Minecraft.getInstance().renderBuffers().bufferSource();
            var vc = buffSource.getBuffer(RenderType.lines());
            
            ShapeRenderer.renderLineBox(ps, vc, 1, -61, 1, 9, -49, 9, 1f, 1f, 1f, 1f);
            buffSource.endBatch();
            ps.popPose();
        });
    }
}