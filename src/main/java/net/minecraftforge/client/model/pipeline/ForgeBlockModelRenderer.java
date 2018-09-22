/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.ForgeMod;

public class ForgeBlockModelRenderer extends BlockModelRenderer
{
    private final ThreadLocal<VertexLighterFlat> lighterFlat;
    private final ThreadLocal<VertexLighterSmoothAo> lighterSmooth;
    private final ThreadLocal<VertexBufferConsumer> wrFlat = new ThreadLocal<>();
    private final ThreadLocal<VertexBufferConsumer> wrSmooth = new ThreadLocal<>();
    private final ThreadLocal<BufferBuilder> lastRendererFlat = new ThreadLocal<>();
    private final ThreadLocal<BufferBuilder> lastRendererSmooth = new ThreadLocal<>();

    public ForgeBlockModelRenderer(BlockColors colors)
    {
        super(colors);
        lighterFlat = ThreadLocal.withInitial(() -> new VertexLighterFlat(colors));
        lighterSmooth = ThreadLocal.withInitial(() -> new VertexLighterSmoothAo(colors));
    }

    @Override
    public boolean renderModelFlat(IWorldReader world, IBakedModel model, IBlockState state, BlockPos pos, BufferBuilder buffer, boolean checkSides, Random rand, long seed)
    {
        if(ForgeMod.forgeLightPipelineEnabled)
        {
            if(buffer != lastRendererFlat.get())
            {
                lastRendererFlat.set(buffer);
                VertexBufferConsumer newCons = new VertexBufferConsumer(buffer);
                wrFlat.set(newCons);
                lighterFlat.get().setParent(newCons);
            }
            wrFlat.get().setOffset(pos);
            return render(lighterFlat.get(), world, model, state, pos, buffer, checkSides, rand, seed);
        }
        else
        {
            return super.renderModelFlat(world, model, state, pos, buffer, checkSides, rand, seed);
        }
    }

    @Override
    public boolean renderModelSmooth(IWorldReader world, IBakedModel model, IBlockState state, BlockPos pos, BufferBuilder buffer, boolean checkSides, Random rand, long seed)
    {
        if(ForgeMod.forgeLightPipelineEnabled)
        {
            if(buffer != lastRendererSmooth.get())
            {
                lastRendererSmooth.set(buffer);
                VertexBufferConsumer newCons = new VertexBufferConsumer(buffer);
                wrSmooth.set(newCons);
                lighterSmooth.get().setParent(newCons);
            }
            wrSmooth.get().setOffset(pos);
            return render(lighterSmooth.get(), world, model, state, pos, buffer, checkSides, rand, seed);
        }
        else
        {
            return super.renderModelSmooth(world, model, state, pos, buffer, checkSides, rand, seed);
        }
    }

    public static boolean render(VertexLighterFlat lighter, IWorldReader world, IBakedModel model, IBlockState state, BlockPos pos, BufferBuilder wr, boolean checkSides, Random rand, long seed)
    {
        lighter.setWorld(world);
        lighter.setState(state);
        lighter.setBlockPos(pos);
        boolean empty = true;
        List<BakedQuad> quads = model.getQuads(state, null, rand);
        if(!quads.isEmpty())
        {
            lighter.updateBlockInfo();
            empty = false;
            for(BakedQuad quad : quads)
            {
                quad.pipe(lighter);
            }
        }
        for(EnumFacing side : EnumFacing.values())
        {
            quads = model.getQuads(state, side, rand);
            if(!quads.isEmpty())
            {
                if(!checkSides || Block.shouldSideBeRendered(state, world, pos, side))
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
