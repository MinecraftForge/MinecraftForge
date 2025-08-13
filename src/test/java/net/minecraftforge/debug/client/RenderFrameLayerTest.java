/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.debug.client;
 
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraftforge.client.FramePassManager;
import net.minecraftforge.client.event.AddFramePassEvent;
import net.minecraftforge.common.extensions.IForgeGameTestHelper;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTest;
import net.minecraftforge.gametest.GameTestNamespace;
import net.minecraftforge.test.BaseTestMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.gametest.framework.GameTestHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.framegraph.FramePass;


@GameTestNamespace("forge")
@Mod(RenderFrameLayerTest.MODID)
public class RenderFrameLayerTest extends BaseTestMod {
    public static final String MODID = "render_frame_layer_test";
    private static final IForgeGameTestHelper.BoolFlag passOne = new IForgeGameTestHelper.BoolFlag("pass_one_flag");
    private static final IForgeGameTestHelper.BoolFlag passTwo = new IForgeGameTestHelper.BoolFlag("pass_two_flag");
    public RenderFrameLayerTest(FMLJavaModLoadingContext context) {
        super(context, false, false);
        AddFramePassEvent.BUS.addListener(RenderFrameLayerTest::renderTest);
    }

    @GameTest
    public static void frame_passes_run(GameTestHelper helper) {
        passOne.set(false); passTwo.set(false); // pass#executes will reset to true if they run
        helper.runAfterDelay(4, () -> {
            helper.assertTrue(passOne.getBool(), "Pass One was not executed");
            helper.assertTrue(passTwo.getBool(), "Pass Two was not executed");
            helper.succeed();
        });
    }

    /**
     * If this is working, two white line box cubes will be rendered at ground level in a superflat world around (0,0)
     */
    public static void renderTest(AddFramePassEvent event) {
        var mainCamera = Minecraft.getInstance().gameRenderer.getMainCamera();
        FramePassManager.PassDefinition def = new FramePassManager.PassDefinition() {
            @Override
            public void targets(LevelTargetBundle bundle, FramePass pass) {
                bundle.main = pass.readsAndWrites(bundle.main);
            }

            @Override
            public void executes() {
                PoseStack ps = new PoseStack();
                passOne.set(true);
                ps.translate(mainCamera.getPosition().multiply(-1,-1,-1));
                ps.pushPose();
                var buffSource = Minecraft.getInstance().renderBuffers().bufferSource();
                var vc = buffSource.getBuffer(RenderType.lines());

                ShapeRenderer.renderLineBox(ps, vc, 0, -60, 0, 10, -50, 10, 1f, 1f, 1f, 1f);
                buffSource.endBatch();
                ps.popPose();
            }
        };
        event.addPass(rl(MODID), def);
        FramePassManager.PassDefinition def2 = new FramePassManager.PassDefinition() {
            @Override
            public void targets(LevelTargetBundle bundle, FramePass pass) {
                bundle.main = pass.readsAndWrites(bundle.main);
            }

            @Override
            public void executes() {
                PoseStack ps = new PoseStack();
                passTwo.set(true);
                ps.translate(mainCamera.getPosition().multiply(-1,-1,-1));
                ps.pushPose();
                var buffSource = Minecraft.getInstance().renderBuffers().bufferSource();
                var vc = buffSource.getBuffer(RenderType.lines());

                ShapeRenderer.renderLineBox(ps, vc, 1, -61, 1, 9, -49, 9, 1f, 1f, 1f, 1f);
                buffSource.endBatch();
                ps.popPose();
            }
        };
        event.addPass(rl(MODID+"_two"), def2);
    }
}