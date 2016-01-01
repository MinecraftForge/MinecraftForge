package net.minecraftforge.client.model.animation;

import java.util.concurrent.TimeUnit;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.Properties;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Generic TileEntitySpecialRenderer that works with the Forge model system and animations.
 */
public class AnimationTESR<T extends TileEntity & IAnimationProvider> extends FastTESR<T> implements IEventHandler<T>
{
    protected static BlockRendererDispatcher blockRenderer;

    protected static final LoadingCache<Pair<IExtendedBlockState, IModelState>, IBakedModel> modelCache = CacheBuilder.newBuilder().maximumSize(10).expireAfterWrite(100, TimeUnit.MILLISECONDS).build(new CacheLoader<Pair<IExtendedBlockState, IModelState>, IBakedModel>()
    {
        public IBakedModel load(Pair<IExtendedBlockState, IModelState> key) throws Exception
        {
            IBakedModel model = blockRenderer.getBlockModelShapes().getModelForState(key.getLeft().getClean());
            if(model instanceof ISmartBlockModel)
            {
                model = ((ISmartBlockModel)model).handleBlockState(key.getLeft().withProperty(Properties.AnimationProperty, key.getRight()));
            }
            return model;
        }
    });

    protected static IBakedModel getModel(IExtendedBlockState state, IModelState modelState)
    {
        return modelCache.getUnchecked(Pair.of(state, modelState));
    }

    public void renderTileEntityFast(T te, double x, double y, double z, float partialTick, int breakStage, WorldRenderer renderer)
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

                IBakedModel model = getModel(exState, pair.getLeft());

                renderer.setTranslation(x - pos.getX(), y - pos.getY(), z - pos.getZ());

                blockRenderer.getBlockModelRenderer().renderModel(world, model, state, pos, renderer, false);
            }
        }
    }

    public void handleEvents(T te, float time, Iterable<Event> pastEvents) {}
}