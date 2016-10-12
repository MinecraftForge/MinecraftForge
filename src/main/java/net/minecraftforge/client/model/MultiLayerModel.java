package net.minecraftforge.client.model;

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
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
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

    @Override
    public IFlexibleBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        return new MultiLayerBakedModel(models, format, IPerspectiveAwareModel.MapWrapper.getTransforms(state));
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
        private final ImmutableMap<Optional<EnumWorldBlockLayer>, ModelResourceLocation> models;
        private final VertexFormat format;
        private final ImmutableMap<TransformType, TRSRTransformation> cameraTransforms;;
        private IBakedModel missing;
        private IBakedModel base;
        private ImmutableMap<EnumWorldBlockLayer, IBakedModel> bakedModels;
        private ImmutableMap<Optional<EnumFacing>, ImmutableList<BakedQuad>> quads;

        public MultiLayerBakedModel(ImmutableMap<Optional<EnumWorldBlockLayer>, ModelResourceLocation> models, VertexFormat format, ImmutableMap<TransformType, TRSRTransformation> cameraTransforms)
        {
            this.models = models;
            this.format = format;
            this.cameraTransforms = cameraTransforms;
        }

        private void compute()
        {
            if(base == null)
            {
                ModelManager manager = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager();
                missing = manager.getMissingModel();
                base = getModel(manager, Optional.<EnumWorldBlockLayer>absent());

                ImmutableMap.Builder<EnumWorldBlockLayer, IBakedModel> builder = ImmutableMap.builder();
                for(EnumWorldBlockLayer layer : EnumWorldBlockLayer.values())
                {
                    if(models.containsKey(Optional.of(layer)))
                    {
                        builder.put(layer, getModel(manager, Optional.of(layer)));
                    }
                }
                bakedModels = builder.build();

                ImmutableMap.Builder<Optional<EnumFacing>, ImmutableList<BakedQuad>> quadBuilder = ImmutableMap.builder();
                quadBuilder.put(Optional.<EnumFacing>absent(), buildQuads(Optional.<EnumFacing>absent()));
                for(EnumFacing side: EnumFacing.values())
                {
                    quadBuilder.put(Optional.of(side), buildQuads(Optional.of(side)));
                }
                quads = quadBuilder.build();
            }
        }

        private IBakedModel getModel(ModelManager manager, Optional<EnumWorldBlockLayer> layer)
        {
            ModelResourceLocation loc = models.get(layer);
            if(loc == null)
            {
                loc = new ModelResourceLocation("builtin/missing", "missing");
            }
            return manager.getModel(loc);
        }

        private ImmutableList<BakedQuad> buildQuads(Optional<EnumFacing> side)
        {
            ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
            for(IBakedModel model : bakedModels.values())
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
            compute();
            return quads.get(Optional.of(side));
        }

        @Override
        public List<BakedQuad> getGeneralQuads()
        {
            compute();
            return quads.get(Optional.absent());
        }

        @Override
        public boolean isAmbientOcclusion()
        {
            compute();
            return base.isAmbientOcclusion();
        }

        @Override
        public boolean isGui3d()
        {
            compute();
            return base.isGui3d();
        }

        @Override
        public boolean isBuiltInRenderer()
        {
            compute();
            return base.isBuiltInRenderer();
        }

        @Override
        public TextureAtlasSprite getParticleTexture()
        {
            compute();
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
            compute();
            EnumWorldBlockLayer layer = MinecraftForgeClient.getRenderLayer();
            if(!bakedModels.containsKey(layer))
            {
                return missing;
            }
            return bakedModels.get(layer);
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
