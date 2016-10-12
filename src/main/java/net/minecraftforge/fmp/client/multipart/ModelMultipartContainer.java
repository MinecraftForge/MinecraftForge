package net.minecraftforge.fmp.client.multipart;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Predicate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fmp.block.BlockCoverable;
import net.minecraftforge.fmp.block.BlockMultipartContainer;
import net.minecraftforge.fmp.multipart.PartState;

@SuppressWarnings("deprecation")
public class ModelMultipartContainer implements IBakedModel
{
    
    final IBakedModel model;
    private final Predicate<BlockRenderLayer> layerFilter;

    public ModelMultipartContainer(IBakedModel model, Predicate<BlockRenderLayer> layerFilter)
    {
        this.model = model;
        this.layerFilter = layerFilter;
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand)
    {
        if (state == null)
        {
            if (model != null)
            {
                return model.getQuads(state, side, rand);
            }
            return Collections.emptyList();
        }
        BlockRenderLayer layer = MinecraftForgeClient.getRenderLayer();
        if (!(state instanceof IExtendedBlockState)
                || !((IExtendedBlockState) state).getUnlistedProperties().containsKey(BlockMultipartContainer.PROPERTY_MULTIPART_CONTAINER))
        {
            if (model != null && layerFilter.apply(layer))
            {
                return model.getQuads(state, side, rand);
            }
            return Collections.emptyList();
        }
        List<PartState> partStates = ((IExtendedBlockState) state).getValue(BlockMultipartContainer.PROPERTY_MULTIPART_CONTAINER);

        if (partStates == null)
        {
            if (model != null)
            {
                model.getQuads(state, side, rand);
            }
            return Collections.emptyList();
        }

        List<BakedQuad> quads = new LinkedList<BakedQuad>();
        List<Pair<PartState, IBakedModel>> models = new LinkedList<Pair<PartState, IBakedModel>>();

        if (model != null && layerFilter.apply(layer))
        {
            quads.addAll(model.getQuads(state, side, rand));
        }

        for (PartState partState : partStates)
        {
            if (!partState.renderLayers.contains(MinecraftForgeClient.getRenderLayer()))
            {
                continue;
            }

            ModelResourceLocation modelLocation = new ModelResourceLocation(partState.modelPath,
                    MultipartStateMapper.instance.getPropertyString(partState.state.getProperties()));
            IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager()
                    .getModel(modelLocation);
            if (model != null)
            {
                models.add(Pair.of(partState, model));
            }
        }

        Collections.sort(models, new WeightComparator(side, rand));

        for (Pair<PartState, IBakedModel> p : models)
        {
            if (p.getKey().tintProvider == null)
            {
                quads.addAll(p.getValue().getQuads(p.getKey().extendedState, side, rand));
            }
            else
            {
                for (BakedQuad quad : p.getValue().getQuads(p.getKey().extendedState, side, rand))
                {
                    if (quad.hasTintIndex())
                    {
                        int color = p.getKey().tintProvider.colorMultiplier(p.getKey().extendedState, null, null, quad.getTintIndex());
                        float b = (float)(color & 0xFF) / 0xFF;
                        float g = (float)((color >>> 8) & 0xFF) / 0xFF;
                        float r = (float)((color >>> 16) & 0xFF) / 0xFF;
                        UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(quad.getFormat());
                        builder.setApplyDiffuseLighting(quad.shouldApplyDiffuseLighting());
                        builder.setQuadOrientation(quad.getFace());
                        builder.setTexture(quad.getSprite());
                        LightUtil.ItemConsumer consumer = new LightUtil.ItemConsumer(builder);
                        consumer.setAuxColor(r, g, b, 1);
                        quad.pipe(consumer);
                        quads.add(builder.build());
                    }
                    else
                    {
                        quads.add(quad);
                    }
                }
            }
        }

        return quads;
    }

    @Override
    public boolean isAmbientOcclusion()
    {
        return model != null ? model.isAmbientOcclusion() : true;
    }

    @Override
    public boolean isGui3d()
    {
        return model != null ? model.isGui3d() : false;
    }

    @Override
    public boolean isBuiltInRenderer()
    {
        return model != null ? model.isBuiltInRenderer() : false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture()
    {
        return model != null ? model.getParticleTexture()
                : Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/stone");
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms()
    {
        return model != null ? model.getItemCameraTransforms() : ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides()
    {
        return ItemOverrideList.NONE;
    }

    public static ModelMultipartContainer fromBlock(IBakedModel model, final BlockCoverable block)
    {
        
        return new ModelMultipartContainer(model, new Predicate<BlockRenderLayer>()
        {
            @Override
            public boolean apply(BlockRenderLayer layer)
            {
                return block.canRenderInLayerDefault(layer);
            }
        });
    }

    public static ModelMultipartContainer fromBlockState(IBakedModel model, final IBlockState state)
    {
        
        return new ModelMultipartContainer(model, new Predicate<BlockRenderLayer>()
        {
            @Override
            public boolean apply(BlockRenderLayer layer)
            {
                return ((BlockCoverable) state.getBlock()).canRenderInLayerDefault(state, layer);
            }
        });
    }

    private final class WeightComparator implements Comparator<Pair<PartState, IBakedModel>>
    {
        
        private final EnumFacing side;
        private final long rand;

        public WeightComparator(EnumFacing side, long rand)
        {
            this.side = side;
            this.rand = rand;
        }

        @Override
        public int compare(Pair<PartState, IBakedModel> a, Pair<PartState, IBakedModel> b)
        {
            int wa = a.getValue() instanceof IWeightedBakedModel
                    ? ((IWeightedBakedModel) a.getValue()).getWeight(a.getKey().extendedState, side, rand) : 0;
            int wb = b.getValue() instanceof IWeightedBakedModel
                    ? ((IWeightedBakedModel) b.getValue()).getWeight(b.getKey().extendedState, side, rand) : 0;
            return Integer.compare(wb, wa);
        }
    }

}
