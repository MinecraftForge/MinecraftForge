/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

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
    public static void insertForgePasses(FrameGraphBuilder graphBuilder, LevelTargetBundle bundle, DeltaTracker tracker) {
        for (PassInfo info : addedPasses) {
            FramePass pass = graphBuilder.addPass(info.name);
            PassDefinition forgePass = info.pass;
            forgePass.extracts(bundle, pass, tracker);
            pass.executes(forgePass::executes);
        }
    }



     /// ### A PassDefinition must satisfy 3 things.
     ///
     /// 1. The rendering order for the purpose of translucency sorting. Read further for specific details.
     /// 2. A render state definition supplier; "what" will be rendered.
     /// 3. A render state definition consumer; "how" it will be rendered.
     ///
     ///
     /// To satisfy #1, all FramePasses (and thus PassDefinitions) must bind against at least one target.
     /// The list of targets can be found in {@linkplain LevelTargetBundle}.
     /// HOWEVER!!! Only {@linkplain LevelTargetBundle#main} is used if the graphics mode is not set to Fabulous!
     /// So at minimum, all passes must guarantee a fallback binding to the main target, like the below.
     /// ```
     /// @Override
     /// void extracts(LevelTargetBundle bundle, FramePass pass, DeltaTracker deltaTracker) {
     ///     if (bundle.clouds != null) { // Perhaps we want to bind to clouds if Fabulous! is on.
     ///         bundle.clouds = pass.readsAndWrites(bundle.clouds);
     ///     }
     ///     // An else clause could be used, but is not mandatory. It is fine to bind to more than one target.
     ///     bundle.main = pass.readsAndWrites(bundle.main);
     /// }
     /// ```
     /// </pre>
     ///
     /// It is up to the user to decide how their pass should bind to targets, but it must always bind to at least one.
     /// Failure to satisfy this requirement will result in {@linkplain FrameGraphBuilder#resolvePassOrder} exploding.
     /// Custom render targets are possible but not documented, see the implementation of {@linkplain LevelTargetBundle}
     /// if you want to take a crack at it.
     ///
     /// Satisfying #2 is simple. Any state information you need to extract
     /// should be done during {@linkplain PassDefinition#extracts(LevelTargetBundle, FramePass, DeltaTracker)}.
     /// No actual rendering should be done at this time. This can happen before or after binding to a target.
     /// The specific implementation of your render state is up to you, it can even be done with some instance variables.
     ///
     /// Satisfying #3 is also simple, this is the actual rendering that will consume the state created
     /// during the extracts phase.
    @NullMarked
    public interface PassDefinition {
        /**
         * Use to define which targets your pass will bind against, see {@link FramePass#reads} and {@link FramePass#readsAndWrites}
         * A FramePass must bind to at least ONE target. Otherwise, you get freaky issues with >1 modded passes.
         * Additionally, this method should be used for extracting render states into instance variables if desired.
         */
        default void extracts(LevelTargetBundle bundle, FramePass pass, DeltaTracker tracker) {
            targets(bundle, pass);
        }

        /**
         * @deprecated Prefer {@link PassDefinition#extracts}
         */
        @Deprecated(forRemoval = true, since = "1.21.10")
        default void targets(LevelTargetBundle bundle, FramePass pass) {}

        /**
         * Use to define what your pass does during the render stage
         * @apiNote In 1.21.10+, LevelRenderState is used. Because that does not exist in 1.21.8, use the client level instead.
         * This method is informally deprecated due to rendering changes in versions 1.21.10+
         */
        default void executes(){};
    }

    private record PassInfo(String name, PassDefinition pass){}
}
