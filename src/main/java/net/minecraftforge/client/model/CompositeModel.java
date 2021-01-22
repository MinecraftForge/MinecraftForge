/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.client.model;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.*;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;
import net.minecraftforge.client.model.geometry.IMultipartModelGeometry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class CompositeModel implements IDynamicBakedModel
{
    private final ImmutableMap<String, IBakedModel> bakedParts;
    private final boolean isAmbientOcclusion;
    private final boolean isGui3d;
    private final boolean isSideLit;
    private final TextureAtlasSprite particle;
    private final ItemOverrideList overrides;
    private final IModelTransform transforms;

    public CompositeModel(boolean isGui3d, boolean isSideLit, boolean isAmbientOcclusion, TextureAtlasSprite particle, ImmutableMap<String, IBakedModel> bakedParts, IModelTransform combinedTransform, ItemOverrideList overrides)
    {
        this.bakedParts = bakedParts;
        this.isAmbientOcclusion = isAmbientOcclusion;
        this.isGui3d = isGui3d;
        this.isSideLit = isSideLit;
        this.particle = particle;
        this.overrides = overrides;
        this.transforms = combinedTransform;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData)
    {
        List<BakedQuad> quads = new ArrayList<>();
        for(Map.Entry<String, IBakedModel> entry : bakedParts.entrySet())
        {
            quads.addAll(entry.getValue().getQuads(state, side, rand, CompositeModelData.get(extraData, entry.getKey())));
        }
        return quads;
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull IBlockDisplayReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData)
    {
        CompositeModelData composite = new CompositeModelData();
        for(Map.Entry<String, IBakedModel> entry : bakedParts.entrySet())
        {
            composite.putSubmodelData(entry.getKey(), entry.getValue().getModelData(world, pos, state, ModelDataWrapper.wrap(tileData)));
        }
        return composite;
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
    public boolean isSideLit()
    {
        return isSideLit;
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
    public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat)
    {
        return PerspectiveMapWrapper.handlePerspective(this, transforms, cameraTransformType, mat);
    }

    @Nullable
    public IBakedModel getPart(String name)
    {
        return bakedParts.get(name);
    }

    private static class Submodel implements IModelGeometryPart
    {
        private final String name;
        private final BlockModel model;
        private final IModelTransform modelTransform;

        private Submodel(String name, BlockModel model, IModelTransform modelTransform)
        {
            this.name = name;
            this.model = model;
            this.modelTransform = modelTransform;
        }

        @Override
        public String name()
        {
            return name;
        }

        @Override
        public void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ResourceLocation modelLocation)
        {
            throw new UnsupportedOperationException("Attempted to call adQuads on a Submodel instance. Please don't.");
        }

        public IBakedModel bakeModel(ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ResourceLocation modelLocation)
        {
            return model.bakeModel(bakery, spriteGetter, new ModelTransformComposition(this.modelTransform, modelTransform,
                    this.modelTransform.isUvLock() || modelTransform.isUvLock()), modelLocation);
        }

        @Override
        public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
        {
            return model.getTextures(modelGetter, missingTextureErrors);
        }
    }

    public static class Geometry implements IMultipartModelGeometry<Geometry>
    {
        private final ImmutableMap<String, Submodel> parts;

        Geometry(ImmutableMap<String, Submodel> parts)
        {
            this.parts = parts;
        }

        @Override
        public Collection<? extends IModelGeometryPart> getParts()
        {
            return parts.values();
        }

        @Override
        public Optional<? extends IModelGeometryPart> getPart(String name)
        {
            return Optional.ofNullable(parts.get(name));
        }

        @Override
        public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation)
        {
            RenderMaterial particleLocation = owner.resolveTexture("particle");
            TextureAtlasSprite particle = spriteGetter.apply(particleLocation);

            ImmutableMap.Builder<String, IBakedModel> bakedParts = ImmutableMap.builder();
            for(Map.Entry<String, Submodel> part : parts.entrySet())
            {
                Submodel submodel = part.getValue();
                if (!owner.getPartVisibility(submodel))
                    continue;
                bakedParts.put(part.getKey(), submodel.bakeModel(bakery, spriteGetter, modelTransform, modelLocation));
            }
            return new CompositeModel(owner.isShadedInGui(), owner.isSideLit(), owner.useSmoothLighting(), particle, bakedParts.build(), owner.getCombinedTransform(), overrides);
        }

        @Override
        public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
        {
            Set<RenderMaterial> textures = new HashSet<>();
            for(Submodel part : parts.values())
            {
                textures.addAll(part.getTextures(owner, modelGetter, missingTextureErrors));
            }
            return textures;
        }
    }

    public static class Loader implements IModelLoader<Geometry>
    {
        public static final Loader INSTANCE = new Loader();

        private Loader() {}

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager)
        {

        }

        @Override
        public Geometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents)
        {
            if (!modelContents.has("parts"))
                throw new RuntimeException("Composite model requires a \"parts\" element.");
            ImmutableMap.Builder<String, Submodel> parts = ImmutableMap.builder();
            for(Map.Entry<String, JsonElement> part : modelContents.get("parts").getAsJsonObject().entrySet())
            {
                // TODO: Allow customizing state? If so, how?
                IModelTransform modelTransform = SimpleModelTransform.IDENTITY;
                parts.put(part.getKey(), new Submodel(
                        part.getKey(),
                        deserializationContext.deserialize(part.getValue(), BlockModel.class),
                        modelTransform));
            }
            return new Geometry(parts.build());
        }
    }

    /**
     * A model data container which stores data for child components.
     */
    public static class CompositeModelData extends ModelDataMap
    {
        public static final ModelProperty<CompositeModelData> SUBMODEL_DATA = new ModelProperty<>();

        /**
         * Helper to get the CompositeModelData from an unknown IModelData instance.
         * @param modelData The undetermined instance to get data from
         * @return An optional representing the composite data, if present.
         */
        public static Optional<CompositeModelData> get(IModelData modelData)
        {
            return Optional.ofNullable(modelData.getData(SUBMODEL_DATA));
        }

        /**
         * Helper to get child data from an unknown IModelData instance.
         * @param modelData The undetermined instance to get data from
         * @param name The name of the child part to get data for.
         * @return The data for the child, or empty if not available.
         */
        public static IModelData get(IModelData modelData, String name)
        {
            return get(modelData).map(data -> data.getSubmodelData(name))
                    .orElse(EmptyModelData.INSTANCE);
        }

        // Implementation

        private final Map<String, IModelData> parts = new HashMap<>();

        public IModelData getSubmodelData(String name)
        {
            if (parts.containsKey(name))
                return parts.get(name);
            return EmptyModelData.INSTANCE;
        }

        public void putSubmodelData(String name, IModelData data)
        {
            parts.put(name, data);
        }

        @Override
        public boolean hasProperty(ModelProperty<?> prop)
        {
            return prop == SUBMODEL_DATA ||super.hasProperty(prop);
        }

        @SuppressWarnings("unchecked")
        @Nullable
        @Override
        public <T> T getData(ModelProperty<T> prop)
        {
            if (prop == SUBMODEL_DATA)
                return (T)this;
            return super.getData(prop);
        }

        @SuppressWarnings("unchecked")
        @Nullable
        @Override
        public <T> T setData(ModelProperty<T> prop, T data)
        {
            if (prop == SUBMODEL_DATA)
                return (T)this;
            return super.setData(prop, data);
        }
    }

    /**
     * Wrapper for an IModelData instance which allows forwarding queries to the parent,
     * but stores any new/modified values itself, avoiding modifications to the parent.
     */
    private static class ModelDataWrapper extends ModelDataMap
    {
        private final IModelData parent;

        public static IModelData wrap(IModelData parent)
        {
            return new ModelDataWrapper(parent);
        }

        private ModelDataWrapper(IModelData parent)
        {
            this.parent = parent;
        }

        @Override
        public boolean hasProperty(ModelProperty<?> prop)
        {
            return super.hasProperty(prop) || parent.hasProperty(prop);
        }

        @Nullable
        @Override
        public <T> T getData(ModelProperty<T> prop)
        {
            return super.hasProperty(prop) ? super.getData(prop) : parent.getData(prop);
        }

        @Nullable
        @Override
        public <T> T setData(ModelProperty<T> prop, T data)
        {
            // We do not want to delegate setting to the parent
            return super.setData(prop, data);
        }
    }
}
