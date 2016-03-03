package net.minecraftforge.client.model.animation;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.Properties;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Generic TileEntitySpecialRenderer that works with the Forge model system and animations.
 */
public class AnimationTESR<T extends TileEntity & IAnimationProvider> extends FastTESR<T> implements IEventHandler<T>
{
    protected static BlockRendererDispatcher blockRenderer;

    public void renderTileEntityFast(T te, double x, double y, double z, float partialTick, int breakStage, VertexBuffer renderer)
    {
        if(blockRenderer == null) blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
        BlockPos pos = te.getPos();
        IBlockAccess world = MinecraftForgeClient.getRegionRenderCache(te.getWorld(), pos);
        IBlockState state = world.getBlockState(pos);
        if(state.getPropertyNames().contains(Properties.StaticProperty))
        {
            state = state.withProperty(Properties.StaticProperty, false);
        }
        if(state instanceof IExtendedBlockState)
        {
            IExtendedBlockState exState = (IExtendedBlockState)state;
            if(exState.getUnlistedNames().contains(Properties.AnimationProperty))
            {
                float time = Animation.getWorldTime(getWorld(), partialTick);
                Pair<IModelState, Iterable<Event>> pair = te.asm().apply(time);
                handleEvents(te, time, pair.getRight());

                // TODO: caching?
                IBakedModel model = blockRenderer.getBlockModelShapes().getModelForState(exState.getClean());
                exState = exState.withProperty(Properties.AnimationProperty, pair.getLeft());

                renderer.setTranslation(x - pos.getX(), y - pos.getY(), z - pos.getZ());

                blockRenderer.getBlockModelRenderer().renderModel(world, model, exState, pos, renderer, false);
            }
        }
    }

    public void handleEvents(T te, float time, Iterable<Event> pastEvents) {}
}