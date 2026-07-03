/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraftforge.client.FramePassManager;
import net.minecraftforge.client.event.AddFramePassEvent;
import net.minecraftforge.common.extensions.IForgeGameTestHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTest;
import net.minecraftforge.gametest.GameTestNamespace;
import net.minecraftforge.test.BaseTestMod;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.AABB;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.framegraph.FramePass;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

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
    @NullMarked
    public static void renderTest(AddFramePassEvent event) {
        FramePassManager.PassDefinition def = new FramePassManager.PassDefinition() {
            @Override
            public void extracts(LevelTargetBundle bundle, FramePass pass, DeltaTracker dt) {
                bundle.main = pass.readsAndWrites(bundle.main);
            }

            @Override
            public void executes(LevelRenderState state) {
                PoseStack ps = new PoseStack();
                passOne.set(true);
                ps.translate(state.cameraRenderState.pos.multiply(-1,-1,-1));
                ps.pushPose();
                Gizmos.cuboid(
                    new AABB(0, -60, 0, 10, -50, 10),
                    GizmoStyle.stroke(ARGB.colorFromFloat(1.0F, 0.9F, 0.9F, 0.9F)),
                    true
                );
                ps.popPose();
            }
        };
        event.addPass(rl(MODID), def);
        FramePassManager.PassDefinition def2 = new FramePassManager.PassDefinition() {
            @Override
            public void extracts(@NotNull LevelTargetBundle bundle, FramePass pass, DeltaTracker dt) {
                bundle.main = pass.readsAndWrites(bundle.main);
            }

            @Override
            public void executes(LevelRenderState state) {
                PoseStack ps = new PoseStack();
                passTwo.set(true);
                ps.translate(state.cameraRenderState.pos.multiply(-1,-1,-1));
                ps.pushPose();
                Gizmos.cuboid(
                    new AABB(0, -60, 0, 10, -50, 10).contract(1, 1, 1),
                    GizmoStyle.stroke(ARGB.colorFromFloat(1.0F, 0.9F, 0.9F, 0.9F)),
                    true
                );
                ps.popPose();
            }
        };
        event.addPass(rl(MODID+"_two"), def2);
    }
}