package net.minecraftforge.client.model;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.vecmath.Matrix4f;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.common.FMLLog;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class MultiLayerModel implements IModelCustomData<MultiLayerModel>
{
    public static final MultiLayerModel instance = new MultiLayerModel(ImmutableMap.<Optional<EnumWorldBlockLayer>, ModelResourceLocation>of());

    private final ImmutableMap<Optional<EnumWorldBlockLayer>, ModelResourceLocation> models;

    public MultiLayerModel(ImmutableMap<Optional<EnumWorldBlockLayer>, ModelResourceLocation> models)
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

    private static ImmutableMap<Optional<EnumWorldBlockLayer>, IFlexibleBakedModel> buildModels(ImmutableMap<Optional<EnumWorldBlockLayer>, ModelResourceLocation> models, IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        ImmutableMap.Builder<Optional<EnumWorldBlockLayer>, IFlexibleBakedModel> builder = ImmutableMap.builder();
        for(Optional<EnumWorldBlockLayer> key : models.keySet())
        {
            IModel model;
            try
            {
                model = ModelLoaderRegistry.getModel(models.get(key));
            }
            catch (IOException e)
            {
                FMLLog.log(Level.ERROR, e, "Couldn't load MultiLayerModel dependency: %s", models.get(key));
                model = ModelLoaderRegistry.getMissingModel();
            }
            builder.put(key, model.bake(new ModelStateComposition(state, model.getDefaultState()), format, bakedTextureGetter));
        }
        return builder.build();
    }

    @Override
    public IFlexibleBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        IModel missing = ModelLoaderRegistry.getMissingModel();
        return new MultiLayerBakedModel(
            buildModels(models, state, format, bakedTextureGetter),
            missing.bake(missing.getDefaultState(), format, bakedTextureGetter),
            format,
            IPerspectiveAwareModel.MapWrapper.getTransforms(state)
        );
    }

    @Override
    public IModelState getDefaultState()
    {
        return TRSRTransformation.identity();
    }

    @Override
    public IModel process(ImmutableMap<String, String> customData)
    {
        ImmutableMap.Builder<Optional<EnumWorldBlockLayer>, ModelResourceLocation> builder = ImmutableMap.builder();
        for(String key : customData.keySet())
        {
            if("base".equals(key))
            {
                builder.put(Optional.<EnumWorldBlockLayer>absent(), getLocation(customData.get(key)));
            }
            for(EnumWorldBlockLayer layer : EnumWorldBlockLayer.values())
            {
                if(layer.toString().equals(key))
                {
                    builder.put(Optional.of(layer), getLocation(customData.get(key)));
                }
            }
        }
        ImmutableMap<Optional<EnumWorldBlockLayer>, ModelResourceLocation> models = builder.build();
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

    public static class MultiLayerBakedModel implements IFlexibleBakedModel, ISmartBlockModel, IPerspectiveAwareModel
    {
        private final ImmutableMap<Optional<EnumWorldBlockLayer>, IFlexibleBakedModel> models;
        private final VertexFormat format;
        private final ImmutableMap<TransformType, TRSRTransformation> cameraTransforms;;
        private final IFlexibleBakedModel base;
        private final IFlexibleBakedModel missing;
        private final ImmutableMap<Optional<EnumFacing>, ImmutableList<BakedQuad>> quads;

        private static final Function<ResourceLocation, TextureAtlasSprite> defaultTextureGetter = new Function<ResourceLocation, TextureAtlasSprite>()
        {
            public TextureAtlasSprite apply(ResourceLocation location)
            {
                return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
            }
        };

        @Deprecated // remove 1.9
        public MultiLayerBakedModel(ImmutableMap<Optional<EnumWorldBlockLayer>, ModelResourceLocation> models, VertexFormat format, ImmutableMap<TransformType, TRSRTransformation> cameraTransforms)
        {
            this(
                buildModels(models, TRSRTransformation.identity(), format, defaultTextureGetter),
                ModelLoaderRegistry.getMissingModel().bake(ModelLoaderRegistry.getMissingModel().getDefaultState(), format, defaultTextureGetter),
                format,
                cameraTransforms
            );
        }

        public MultiLayerBakedModel(ImmutableMap<Optional<EnumWorldBlockLayer>, IFlexibleBakedModel> models, IFlexibleBakedModel missing, VertexFormat format, ImmutableMap<TransformType, TRSRTransformation> cameraTransforms)
        {
            this.models = models;
            this.format = format;
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

        private static ImmutableList<BakedQuad> buildQuads(ImmutableMap<Optional<EnumWorldBlockLayer>, IFlexibleBakedModel> models, Optional<EnumFacing> side)
        {
            ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
            for(IBakedModel model : models.values())
            {
                if(side.isPresent())
                {
                    builder.addAll(model.getFaceQuads(side.get()));
                }
                else
                {
                    builder.addAll(model.getGeneralQuads());
                }
            }
            return builder.build();
        }

        @Override
        public List<BakedQuad> getFaceQuads(EnumFacing side)
        {
            return quads.get(Optional.of(side));
        }

        @Override
        public List<BakedQuad> getGeneralQuads()
        {
            return quads.get(Optional.absent());
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
        public IBakedModel handleBlockState(IBlockState state)
        {
            Optional<EnumWorldBlockLayer> layer = Optional.of(MinecraftForgeClient.getRenderLayer());
            if(!models.containsKey(layer))
            {
                return missing;
            }
            return models.get(layer);
        }

        @Override
        public VertexFormat getFormat()
        {
            return format;
        }

        @Override
        public Pair<? extends IFlexibleBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType)
        {
            return IPerspectiveAwareModel.MapWrapper.handlePerspective(this, cameraTransforms, cameraTransformType);
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
