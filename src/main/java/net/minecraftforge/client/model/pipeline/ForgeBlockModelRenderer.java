/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
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
    public boolean tesselateWithoutAO(BlockAndTintGetter world, BakedModel model, BlockState state, BlockPos pos, PoseStack matrixStack, VertexConsumer buffer, boolean checkSides, Random rand, long seed, int combinedOverlayIn, IModelData modelData)
    {
        if(ForgeConfig.CLIENT.experimentalForgeLightPipelineEnabled.get())
        {
            VertexBufferConsumer consumer = consumerFlat.get();
            consumer.setBuffer(buffer);

            VertexLighterFlat lighter = lighterFlat.get();
            lighter.setParent(consumer);
            lighter.setTransform(matrixStack.last());

            return render(lighter, world, model, state, pos, matrixStack, checkSides, rand, seed, modelData);
        }
        else
        {
            return super.tesselateWithoutAO(world, model, state, pos, matrixStack, buffer, checkSides, rand, seed, combinedOverlayIn, modelData);
        }
    }

    @Override
    public boolean tesselateWithAO(BlockAndTintGetter world, BakedModel model, BlockState state, BlockPos pos, PoseStack matrixStack, VertexConsumer buffer, boolean checkSides, Random rand, long seed, int combinedOverlayIn, IModelData modelData)
    {
        if(ForgeConfig.CLIENT.experimentalForgeLightPipelineEnabled.get())
        {
            VertexBufferConsumer consumer = consumerSmooth.get();
            consumer.setBuffer(buffer);

            VertexLighterSmoothAo lighter = lighterSmooth.get();
            lighter.setParent(consumer);
            lighter.setTransform(matrixStack.last());

            return render(lighter, world, model, state, pos, matrixStack, checkSides, rand, seed, modelData);
        }
        else
        {
            return super.tesselateWithAO(world, model, state, pos, matrixStack, buffer, checkSides, rand, seed, combinedOverlayIn, modelData);
        }
    }

    public static boolean render(VertexLighterFlat lighter, BlockAndTintGetter world, BakedModel model, BlockState state, BlockPos pos, PoseStack matrixStack, boolean checkSides, Random rand, long seed, IModelData modelData)
    {
        lighter.setWorld(world);
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
                if(!checkSides || Block.shouldRenderFace(state, world, pos, side, pos.relative(side)))
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
