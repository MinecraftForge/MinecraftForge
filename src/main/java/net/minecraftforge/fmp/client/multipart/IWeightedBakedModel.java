package net.minecraftforge.fmp.client.multipart;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

/**
 * Interface that represents a model whose sides have different sorting priorities. Lower weights get added after higher
 * weights, rendering on top of them and vice versa.
 */
public interface IWeightedBakedModel extends IBakedModel
{
    
    /**
     * Gets the weight of the quads on a specific side for the specified state.
     */
    public int getWeight(@Nullable IBlockState state, @Nullable EnumFacing side, long rand);

    public static class WeightedModelAdapter implements IWeightedBakedModel
    {
        
        private final IBakedModel model;
        private final IModelWeightProvider provider;

        private WeightedModelAdapter(IBakedModel model, IModelWeightProvider provider)
        {
            this.model = model;
            this.provider = provider;
        }

        @Override
        public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand)
        {
            return model.getQuads(state, side, rand);
        }

        @Override
        public boolean isAmbientOcclusion()
        {
            return model.isAmbientOcclusion();
        }

        @Override
        public boolean isGui3d()
        {
            return model.isGui3d();
        }

        @Override
        public boolean isBuiltInRenderer()
        {
            return model.isBuiltInRenderer();
        }

        @Override
        public TextureAtlasSprite getParticleTexture()
        {
            return model.getParticleTexture();
        }

        @Override
        public ItemCameraTransforms getItemCameraTransforms()
        {
            return model.getItemCameraTransforms();
        }

        @Override
        public ItemOverrideList getOverrides()
        {
            return model.getOverrides();
        }

        @Override
        public int getWeight(IBlockState state, EnumFacing side, long rand)
        {
            return provider.getWeight(state, side, rand);
        }

        public static final IWeightedBakedModel adapt(IBakedModel model, IModelWeightProvider provider)
        {
            return new WeightedModelAdapter(model, provider);
        }

    }

    public static interface IModelWeightProvider
    {
        
        /**
         * Gets the weight of the quads on a specific side for the specified state.
         */
        public int getWeight(@Nullable IBlockState state, @Nullable EnumFacing side, long rand);

    }

}
