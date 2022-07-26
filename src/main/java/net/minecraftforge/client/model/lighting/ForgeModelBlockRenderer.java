/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.lighting;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.ForgeConfig;

import java.util.List;

/**
 * Wrapper around {@link ModelBlockRenderer} to allow rendering blocks via Forge's lighting pipeline.
 */
public class ForgeModelBlockRenderer extends ModelBlockRenderer
{
    private static final Direction[] SIDES = Direction.values();

    private final ThreadLocal<QuadLighter> flatLighter, smoothLighter;

    public ForgeModelBlockRenderer(BlockColors colors)
    {
        super(colors);
        this.flatLighter = ThreadLocal.withInitial(() -> new FlatQuadLighter(colors));
        this.smoothLighter = ThreadLocal.withInitial(() -> new SmoothQuadLighter(colors));
    }

    @Override
    public void tesselateWithoutAO(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer vertexConsumer, boolean checkSides, RandomSource rand, long seed, int packedOverlay, ModelData modelData, RenderType renderType)
    {
        if (ForgeConfig.CLIENT.experimentalForgeLightPipelineEnabled.get())
        {
            render(vertexConsumer, flatLighter.get(), level, model, state, pos, poseStack, checkSides, rand, seed, packedOverlay, modelData, renderType);
        }
        else
        {
            super.tesselateWithoutAO(level, model, state, pos, poseStack, vertexConsumer, checkSides, rand, seed, packedOverlay, modelData, renderType);
        }
    }

    @Override
    public void tesselateWithAO(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer vertexConsumer, boolean checkSides, RandomSource rand, long seed, int packedOverlay, ModelData modelData, RenderType renderType)
    {
        if (ForgeConfig.CLIENT.experimentalForgeLightPipelineEnabled.get())
        {
            render(vertexConsumer, smoothLighter.get(), level, model, state, pos, poseStack, checkSides, rand, seed, packedOverlay, modelData, renderType);
        }
        else
        {
            super.tesselateWithAO(level, model, state, pos, poseStack, vertexConsumer, checkSides, rand, seed, packedOverlay, modelData, renderType);
        }
    }

    public static boolean render(VertexConsumer vertexConsumer, QuadLighter lighter, BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, boolean checkSides, RandomSource rand, long seed, int packedOverlay, ModelData modelData, RenderType renderType)
    {
        var pose = poseStack.last();
        var empty = true;

        rand.setSeed(seed);
        List<BakedQuad> quads = model.getQuads(state, null, rand, modelData, renderType);
        if (!quads.isEmpty())
        {
            empty = false;
            lighter.setup(level, pos, state);
            for (BakedQuad quad : quads)
            {
                lighter.process(vertexConsumer, pose, quad, packedOverlay);
            }
        }

        for (Direction side : SIDES)
        {
            if (checkSides && !Block.shouldRenderFace(state, level, pos, side, pos.relative(side)))
            {
                continue;
            }
            rand.setSeed(seed);
            quads = model.getQuads(state, side, rand, modelData, renderType);
            if (!quads.isEmpty())
            {
                if (empty)
                {
                    empty = false;
                    lighter.setup(level, pos, state);
                }
                for (BakedQuad quad : quads)
                {
                    lighter.process(vertexConsumer, pose, quad, packedOverlay);
                }
            }
        }
        lighter.reset();
        return !empty;
    }
}
