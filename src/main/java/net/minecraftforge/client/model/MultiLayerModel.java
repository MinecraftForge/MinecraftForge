package net.minecraftforge.client.model;

import java.util.Collection;
import java.util.List;

import javax.vecmath.Matrix4f;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.common.FMLLog;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class MultiLayerModel implements IModelCustomData
{
    public static final MultiLayerModel instance = new MultiLayerModel(ImmutableMap.<Optional<BlockRenderLayer>, ModelResourceLocation>of());

    private final ImmutableMap<Optional<BlockRenderLayer>, ModelResourceLocation> models;

    public MultiLayerModel(ImmutableMap<Optional<BlockRenderLayer>, ModelResourceLocation> models)
    {
        this.models = models;
    }

    @Override
    public Collection<ResourceLocation> getDependencies()
    {
        return ImmutableList.<ResourceLocation>copyOf(models.values());
    }

    @Override
    public Collection<ResourceLocation> getTextures()
    {
        return ImmutableList.of();
    }

    private static ImmutableMap<Optional<BlockRenderLayer>, IBakedModel> buildModels(ImmutableMap<Optional<BlockRenderLayer>, ModelResourceLocation> models, IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        ImmutableMap.Builder<Optional<BlockRenderLayer>, IBakedModel> builder = ImmutableMap.builder();
        for(Optional<BlockRenderLayer> key : models.keySet())
        {
            IModel model = ModelLoaderRegistry.getModel(models.get(key));
            builder.put(key, model.bake(new ModelStateComposition(state, model.getDefaultState()), format, bakedTextureGetter));
        }
        return builder.build();
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        IModel missing = ModelLoaderRegistry.getMissingModel();
        return new MultiLayerBakedModel(
            buildModels(models, state, format, bakedTextureGetter),
            missing.bake(missing.getDefaultState(), format, bakedTextureGetter),
            IPerspectiveAwareModel.MapWrapper.getTransforms(state)
        );
    }

    @Override
    public IModelState getDefaultState()
    {
        return TRSRTransformation.identity();
    }

    @Override
    public MultiLayerModel process(ImmutableMap<String, String> customData)
    {
        ImmutableMap.Builder<Optional<BlockRenderLayer>, ModelResourceLocation> builder = ImmutableMap.builder();
        for(String key : customData.keySet())
        {
            if("base".equals(key))
            {
                builder.put(Optional.<BlockRenderLayer>absent(), getLocation(customData.get(key)));
            }
            for(BlockRenderLayer layer : BlockRenderLayer.values())
            {
                if(layer.toString().equals(key))
                {
                    builder.put(Optional.of(layer), getLocation(customData.get(key)));
                }
            }
        }
        ImmutableMap<Optional<BlockRenderLayer>, ModelResourceLocation> models = builder.build();
        if(models.isEmpty()) return instance;
        return new MultiLayerModel(models);
    }

    private ModelResourceLocation getLocation(String json)
    {
        JsonElement e = new JsonParser().parse(json);
        if(e.isJsonPrimitive() && e.getAsJsonPrimitive().isString())
        {
            return new ModelResourceLocation(e.getAsString());
        }
        FMLLog.severe("Expect ModelResourceLocation, got: ", json);
        return new ModelResourceLocation("builtin/missing", "missing");
    }

    public static class MultiLayerBakedModel implements IPerspectiveAwareModel
    {
        private final ImmutableMap<Optional<BlockRenderLayer>, IBakedModel> models;
        private final ImmutableMap<TransformType, TRSRTransformation> cameraTransforms;;
        private final IBakedModel base;
        private final IBakedModel missing;
        private final ImmutableMap<Optional<EnumFacing>, ImmutableList<BakedQuad>> quads;

        public MultiLayerBakedModel(ImmutableMap<Optional<BlockRenderLayer>, IBakedModel> models, IBakedModel missing, ImmutableMap<TransformType, TRSRTransformation> cameraTransforms)
        {
            this.models = models;
            this.cameraTransforms = cameraTransforms;
            this.missing = missing;
            if(models.containsKey(Optional.absent()))
            {
                base = models.get(Optional.absent());
            }
            else
            {
                base = missing;
            }
            ImmutableMap.Builder<Optional<EnumFacing>, ImmutableList<BakedQuad>> quadBuilder = ImmutableMap.builder();
            quadBuilder.put(Optional.<EnumFacing>absent(), buildQuads(models, Optional.<EnumFacing>absent()));
            for(EnumFacing side: EnumFacing.values())
            {
                quadBuilder.put(Optional.of(side), buildQuads(models, Optional.of(side)));
            }
            quads = quadBuilder.build();
        }

        private static ImmutableList<BakedQuad> buildQuads(ImmutableMap<Optional<BlockRenderLayer>, IBakedModel> models, Optional<EnumFacing> side)
        {
            ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
            for(IBakedModel model : models.values())
            {
                builder.addAll(model.getQuads(null, side.orNull(), 0));
            }
            return builder.build();
        }

        @Override
        public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand)
        {
            IBakedModel model;
            BlockRenderLayer layer = MinecraftForgeClient.getRenderLayer();
            if(layer == null)
            {
                return quads.get(Optional.fromNullable(side));
            }
            else if(!models.containsKey(layer))
            {
                model = missing;
            }
            else
            {
                model = models.get(Optional.of(layer));
            }
            return model.getQuads(state, side, rand);
        }

        @Override
        public boolean isAmbientOcclusion()
        {
            return base.isAmbientOcclusion();
        }

        @Override
        public boolean isGui3d()
        {
            return base.isGui3d();
        }

        @Override
        public boolean isBuiltInRenderer()
        {
            return base.isBuiltInRenderer();
        }

        @Override
        public TextureAtlasSprite getParticleTexture()
        {
            return base.getParticleTexture();
        }

        @Override
        public ItemCameraTransforms getItemCameraTransforms()
        {
            return ItemCameraTransforms.DEFAULT;
        }

        @Override
        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType)
        {
            return IPerspectiveAwareModel.MapWrapper.handlePerspective(this, cameraTransforms, cameraTransformType);
        }

        @Override
        public ItemOverrideList getOverrides()
        {
            return ItemOverrideList.NONE;
        }
    }

    public static enum Loader implements ICustomModelLoader
    {
        instance;

        public void onResourceManagerReload(IResourceManager resourceManager) {}

        public boolean accepts(ResourceLocation modelLocation)
        {
            return modelLocation.getResourceDomain().equals("forge") && (
                modelLocation.getResourcePath().equals("multi-layer") ||
                modelLocation.getResourcePath().equals("models/block/multi-layer") ||
                modelLocation.getResourcePath().equals("models/item/multi-layer"));
        }

        public IModel loadModel(ResourceLocation modelLocation)
        {
            return MultiLayerModel.instance;
        }
    }
}
