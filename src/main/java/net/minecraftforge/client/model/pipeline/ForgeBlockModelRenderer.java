/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.pipeline;

import java.util.List;
import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.ForgeConfig;

public class ForgeBlockModelRenderer extends ModelBlockRenderer
{
    private final ThreadLocal<VertexLighterFlat> lighterFlat;
    private final ThreadLocal<VertexLighterSmoothAo> lighterSmooth;
    private final ThreadLocal<VertexBufferConsumer> consumerFlat = ThreadLocal.withInitial(VertexBufferConsumer::new);
    private final ThreadLocal<VertexBufferConsumer> consumerSmooth = ThreadLocal.withInitial(VertexBufferConsumer::new);

    public ForgeBlockModelRenderer(BlockColors colors)
    {
        super(colors);
        lighterFlat = ThreadLocal.withInitial(() -> new VertexLighterFlat(colors));
        lighterSmooth = ThreadLocal.withInitial(() -> new VertexLighterSmoothAo(colors));
    }

    @Override
    public boolean tesselateWithoutAO(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer buffer, boolean checkSides, Random rand, long seed, int packedOverlay, IModelData modelData)
    {
        if(ForgeConfig.CLIENT.experimentalForgeLightPipelineEnabled.get())
        {
            VertexBufferConsumer consumer = consumerFlat.get();
            consumer.setBuffer(buffer);

            VertexLighterFlat lighter = lighterFlat.get();
            lighter.setParent(consumer);
            lighter.setTransform(poseStack.last());

            return render(lighter, level, model, state, pos, poseStack, checkSides, rand, seed, modelData);
        }
        else
        {
            return super.tesselateWithoutAO(level, model, state, pos, poseStack, buffer, checkSides, rand, seed, packedOverlay, modelData);
        }
    }

    @Override
    public boolean tesselateWithAO(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer buffer, boolean checkSides, Random rand, long seed, int packedOverlay, IModelData modelData)
    {
        if(ForgeConfig.CLIENT.experimentalForgeLightPipelineEnabled.get())
        {
            VertexBufferConsumer consumer = consumerSmooth.get();
            consumer.setBuffer(buffer);

            VertexLighterSmoothAo lighter = lighterSmooth.get();
            lighter.setParent(consumer);
            lighter.setTransform(poseStack.last());

            return render(lighter, level, model, state, pos, poseStack, checkSides, rand, seed, modelData);
        }
        else
        {
            return super.tesselateWithAO(level, model, state, pos, poseStack, buffer, checkSides, rand, seed, packedOverlay, modelData);
        }
    }

    public static boolean render(VertexLighterFlat lighter, BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, boolean checkSides, Random rand, long seed, IModelData modelData)
    {
        lighter.setWorld(level);
        lighter.setState(state);
        lighter.setBlockPos(pos);
        boolean empty = true;
        rand.setSeed(seed);
        List<BakedQuad> quads = model.getQuads(state, null, rand, modelData);
        if(!quads.isEmpty())
        {
            lighter.updateBlockInfo();
            empty = false;
            for(BakedQuad quad : quads)
            {
                quad.pipe(lighter);
            }
        }
        for(Direction side : Direction.values())
        {
            rand.setSeed(seed);
            quads = model.getQuads(state, side, rand, modelData);
            if(!quads.isEmpty())
            {
                if(!checkSides || Block.shouldRenderFace(state, level, pos, side, pos.relative(side)))
                {
                    if(empty) lighter.updateBlockInfo();
                    empty = false;
                    for(BakedQuad quad : quads)
                    {
                        quad.pipe(lighter);
                    }
                }
            }
        }
        lighter.resetBlockInfo();
        return !empty;
    }
}
