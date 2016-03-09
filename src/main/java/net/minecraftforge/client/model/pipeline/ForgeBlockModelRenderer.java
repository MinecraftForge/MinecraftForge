package net.minecraftforge.client.model.pipeline;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeModContainer;

public class ForgeBlockModelRenderer extends BlockModelRenderer
{
    private final ThreadLocal<VertexLighterFlat> lighterFlat = new ThreadLocal<VertexLighterFlat>()
    {
        @Override
        protected VertexLighterFlat initialValue()
        {
            return new VertexLighterFlat(colors);
        }
    };

    private final ThreadLocal<VertexLighterSmoothAo> lighterSmooth = new ThreadLocal<VertexLighterSmoothAo>()
    {
        @Override
        protected VertexLighterSmoothAo initialValue()
        {
            return new VertexLighterSmoothAo(colors);
        }
    };

    private final ThreadLocal<VertexBufferConsumer> wrFlat = new ThreadLocal<VertexBufferConsumer>();
    private final ThreadLocal<VertexBufferConsumer> wrSmooth = new ThreadLocal<VertexBufferConsumer>();
    private final ThreadLocal<VertexBuffer> lastRendererFlat = new ThreadLocal<VertexBuffer>();
    private final ThreadLocal<VertexBuffer> lastRendererSmooth = new ThreadLocal<VertexBuffer>();

    private final BlockColors colors;

    public ForgeBlockModelRenderer(BlockColors colors)
    {
        // TODO Auto-generated constructor stub
        super(colors);
        this.colors = colors;
    }

    @Override
    public boolean func_187497_c(IBlockAccess world, IBakedModel model, IBlockState state, BlockPos pos, VertexBuffer buffer, boolean checkSides, long rand)
    {
        if(ForgeModContainer.forgeLightPipelineEnabled)
        {
            if(buffer != lastRendererFlat.get())
            {
                lastRendererFlat.set(buffer);
                VertexBufferConsumer newCons = new VertexBufferConsumer(buffer);
                wrFlat.set(newCons);
                lighterFlat.get().setParent(newCons);
            }
            wrFlat.get().setOffset(pos);
            return render(lighterFlat.get(), world, model, state, pos, buffer, checkSides, rand);
        }
        else
        {
            return super.func_187497_c(world, model, state, pos, buffer, checkSides, rand);
        }
    }

    @Override
    public boolean func_187498_b(IBlockAccess world, IBakedModel model, IBlockState state, BlockPos pos, VertexBuffer buffer, boolean checkSides, long rand)
    {
        if(ForgeModContainer.forgeLightPipelineEnabled)
        {
            if(buffer != lastRendererSmooth.get())
            {
                lastRendererSmooth.set(buffer);
                VertexBufferConsumer newCons = new VertexBufferConsumer(buffer);
                wrSmooth.set(newCons);
                lighterSmooth.get().setParent(newCons);
            }
            wrSmooth.get().setOffset(pos);
            return render(lighterSmooth.get(), world, model, state, pos, buffer, checkSides, rand);
        }
        else
        {
            return super.func_187498_b(world, model, state, pos, buffer, checkSides, rand);
        }
    }

    public static boolean render(VertexLighterFlat lighter, IBlockAccess world, IBakedModel model, IBlockState state, BlockPos pos, VertexBuffer wr, boolean checkSides, long rand)
    {
        lighter.setWorld(world);
        lighter.setState(state);
        lighter.setBlockPos(pos);
        boolean empty = true;
        List<BakedQuad> quads = model.func_188616_a(state, null, rand);
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
            quads = model.func_188616_a(state, side, rand);
            if(!quads.isEmpty())
            {
                if(!checkSides || state.func_185894_c(world, pos, side))
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
        return !empty;
    }
}
