package net.minecraftforge.client.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.*;
import java.util.function.Function;

public class SeparatePerspectiveModel implements IModelGeometry<SeparatePerspectiveModel>
{
    private final BlockModel baseModel;
    private final ImmutableMap<ItemCameraTransforms.TransformType, BlockModel> perspectives;

    public SeparatePerspectiveModel(BlockModel baseModel, ImmutableMap<ItemCameraTransforms.TransformType, BlockModel> perspectives)
    {
        this.baseModel = baseModel;
        this.perspectives = perspectives;
    }

    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, ISprite sprite, VertexFormat format, ItemOverrideList overrides)
    {
        return new BakedModel(
                owner.useSmoothLighting(), owner.isShadedInGui(),
                spriteGetter.apply(new ResourceLocation(owner.resolveTexture("particle"))), overrides,
                baseModel.bake(bakery, baseModel, spriteGetter, sprite, format),
                ImmutableMap.copyOf(Maps.transformValues(perspectives, value -> {
                    return value.bake(bakery, value, spriteGetter, sprite, format);
                }))
        );
    }

    @Override
    public Collection<ResourceLocation> getTextureDependencies(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors)
    {
        Set<ResourceLocation> textures = Sets.newHashSet();
        textures.addAll(baseModel.getTextures(modelGetter, missingTextureErrors));
        for(BlockModel model : perspectives.values())
            textures.addAll(model.getTextures(modelGetter, missingTextureErrors));
        return textures;
    }

    public static class BakedModel implements IBakedModel
    {
        private final boolean isAmbientOcclusion;
        private final boolean isGui3d;
        private final TextureAtlasSprite particle;
        private final ItemOverrideList overrides;
        private final IBakedModel baseModel;
        private final ImmutableMap<ItemCameraTransforms.TransformType, IBakedModel> perspectives;

        public BakedModel(boolean isAmbientOcclusion, boolean isGui3d, TextureAtlasSprite particle, ItemOverrideList overrides, IBakedModel baseModel, ImmutableMap<ItemCameraTransforms.TransformType, IBakedModel> perspectives)
        {
            this.isAmbientOcclusion = isAmbientOcclusion;
            this.isGui3d = isGui3d;
            this.particle = particle;
            this.overrides = overrides;
            this.baseModel = baseModel;
            this.perspectives = perspectives;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand)
        {
            return Collections.emptyList();
        }

        @Override
        public boolean isAmbientOcclusion()
        {
            return isAmbientOcclusion;
        }

        @Override
        public boolean isGui3d()
        {
            return isGui3d;
        }

        @Override
        public boolean isBuiltInRenderer()
        {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleTexture()
        {
            return particle;
        }

        @Override
        public ItemOverrideList getOverrides()
        {
            return overrides;
        }

        @Override
        public boolean doesHandlePerspectives()
        {
            return true;
        }

        @Override
        public ItemCameraTransforms getItemCameraTransforms()
        {
            return ItemCameraTransforms.DEFAULT;
        }

        @Override
        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType)
        {
            if (perspectives.containsKey(cameraTransformType))
            {
                IBakedModel p = perspectives.get(cameraTransformType);
                return p.handlePerspective(cameraTransformType);
            }
            return baseModel.handlePerspective(cameraTransformType);
        }
    }

    public static class Loader implements IModelLoader<SeparatePerspectiveModel>
    {
        public static final Loader INSTANCE = new Loader();

        private static final ImmutableMap<String, ItemCameraTransforms.TransformType> PERSPECTIVES = ImmutableMap.<String, ItemCameraTransforms.TransformType>builder()
                .put("none", ItemCameraTransforms.TransformType.NONE)
                .put("third_person_left_hand", ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND)
                .put("third_person_right_hand", ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND)
                .put("first_person_left_hand", ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND)
                .put("first_person_right_hand", ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND)
                .put("head", ItemCameraTransforms.TransformType.HEAD)
                .put("gui", ItemCameraTransforms.TransformType.GUI)
                .put("ground", ItemCameraTransforms.TransformType.GROUND)
                .put("fixed", ItemCameraTransforms.TransformType.FIXED)
                .build();

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager)
        {
            // Not used
        }

        @Override
        public SeparatePerspectiveModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents)
        {
            BlockModel baseModel = deserializationContext.deserialize(JSONUtils.getJsonObject(modelContents, "base"), BlockModel.class);

            JsonObject perspectiveData = JSONUtils.getJsonObject(modelContents, "perspectives");

            ImmutableMap.Builder<ItemCameraTransforms.TransformType, BlockModel> perspectives = ImmutableMap.builder();
            for(Map.Entry<String, ItemCameraTransforms.TransformType> perspective : PERSPECTIVES.entrySet())
            {
                if (perspectiveData.has(perspective.getKey()))
                {
                    BlockModel perspectiveModel = deserializationContext.deserialize(JSONUtils.getJsonObject(perspectiveData, perspective.getKey()), BlockModel.class);
                    perspectives.put(perspective.getValue(), perspectiveModel);
                }
            }

            return new SeparatePerspectiveModel(baseModel, perspectives.build());
        }
    }
}