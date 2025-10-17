/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

public class FramePassManager {
    private static final List<PassInfo> addedPasses = new ArrayList<>();

    protected static void addPass(ResourceLocation name, PassDefinition pass) {
        if (addedPasses.stream().noneMatch(info -> info.name.equals(name.toString()))) {
            addedPasses.add(new PassInfo(name.toString(), pass));
        } else {
            throw new IllegalArgumentException("Cannot create a frame pass with a duplicate name: " + name);
        }
    }

    // Note: Pass order is determined automatically within FrameGraphBuilder. It's unclear what must be done to guarantee ordering.
    @ApiStatus.Internal
    public static void insertForgePasses(FrameGraphBuilder graphBuilder, LevelTargetBundle bundle, LevelRenderState state) {
        for (PassInfo info : addedPasses) {
            FramePass pass = graphBuilder.addPass(info.name);
            PassDefinition forgePass = info.pass;
            forgePass.targets(bundle, pass);
            pass.executes(() -> forgePass.executes(state));
        }
    }

    public interface PassDefinition {
        /**
         * Use to define which targets your pass will bind against. Additionally, this method should be used for extracting
         * render states if necessary.
         * A FramePass must bind to at least ONE target. Otherwise, you get freaky issues.
         */
        void targets(LevelTargetBundle bundle, FramePass pass);

        /**
         * Use to define what your pass does during the render stage.
         */
        default void executes(LevelRenderState state) {executes();};
        default void executes(){};
    }

    private record PassInfo(String name, PassDefinition pass){}
}
