/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.client;

import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

public class FramePassManager {
    private static final List<String> addedPassNames = new ArrayList<>();
    private static final List<ForgePassDefinition> addedPasses = new ArrayList<>();

    public static void addPass(ResourceLocation name, ForgePassDefinition pass) {
        if (!addedPassNames.contains(name.toString())) {
            addedPassNames.add(name.toString());
            addedPasses.add(pass);
        } else {
            throw new IllegalArgumentException("Cannot create a frame pass with a duplicate name: " + name);
        }
    }

    // Note: Pass order is determined automatically within FrameGraphBuilder. It's unclear what must be done to guarantee ordering.
    @ApiStatus.Internal
    public static void insertForgePasses(FrameGraphBuilder graphBuilder, LevelTargetBundle bundle) {
        // Assume our state is valid (i == j) once rendering had begun. No sane way to recover if it does become invalid anyway.
        for (int i = 0; i < addedPassNames.size(); i++) {
            FramePass pass = graphBuilder.addPass(addedPassNames.get(i));
            ForgePassDefinition forgePass = addedPasses.get(i);
            forgePass.targets(bundle, pass);
            pass.executes(forgePass::executes);
        }
    }

    public static void checkManagerState() {
        if (addedPasses.size() != addedPassNames.size()) {
            // This should never, ever happen, otherwise something has gone seriously wrong.
            throw new IllegalStateException("Number of named passes differs from actual added passes in FramePassManager. Something has gone terribly wrong.");
        }
    }

    public interface ForgePassDefinition {
        void targets(LevelTargetBundle bundle, FramePass pass);
        void executes();
    }
}
