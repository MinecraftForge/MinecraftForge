/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;
import net.minecraftforge.client.model.geometry.IMultipartModelGeometry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class CompositeModel implements IDynamicBakedModel
{
    public static final ModelProperty<SubmodelModelData> SUBMODEL_DATA = new ModelProperty<>();

    private final ImmutableMap<String, IBakedModel> bakedParts;
    private final boolean isAmbientOcclusion;
    private final boolean isGui3d;
    private final TextureAtlasSprite particle;
    private final ItemOverrideList overrides;
    private final IModelTransform transforms;

    public CompositeModel(boolean isGui3d, boolean isAmbientOcclusion, TextureAtlasSprite particle, ImmutableMap<String, IBakedModel> bakedParts, IModelTransform combinedTransform, ItemOverrideList overrides)
    {
        this.bakedParts = bakedParts;
        this.isAmbientOcclusion = isAmbientOcclusion;
        this.isGui3d = isGui3d;
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
            // TODO: Some way to provide submodel data?
            quads.addAll(entry.getValue().getQuads(state, side, rand, getSubmodelData(extraData, entry.getKey())));
        }
        return quads;
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
    public boolean func_230044_c_()
    {
        // TODO: Forge: Auto-generated method stub
        return false;
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

    private IModelData getSubmodelData(IModelData extraData, String name)
    {
        SubmodelModelData data = extraData.getData(SUBMODEL_DATA);
        if (data == null)
            return EmptyModelData.INSTANCE;
        return data.getSubmodelData(name);
    }

    public static class SubmodelModelData
    {
        private final Map<String, IModelData> parts = new HashMap<>();

        public IModelData getSubmodelData(String name)
        {
            return parts.getOrDefault(name, EmptyModelData.INSTANCE);
        }

        public void putSubmodelData(String name, IModelData data)
        {
            parts.put(name, data);
        }
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
        public void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ResourceLocation modelLocation)
        {
            throw new UnsupportedOperationException("Attempted to call adQuads on a Submodel instance. Please don't.");
        }

        public IBakedModel bakeModel(ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ResourceLocation modelLocation)
        {
            return model.bakeModel(bakery, spriteGetter, new ModelTransformComposition(this.modelTransform, modelTransform,
                    this.modelTransform.isUvLock() || modelTransform.isUvLock()), modelLocation);
        }

        @Override
        public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
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
        public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation)
        {
            Material particleLocation = owner.resolveTexture("particle");
            TextureAtlasSprite particle = spriteGetter.apply(particleLocation);

            ImmutableMap.Builder<String, IBakedModel> bakedParts = ImmutableMap.builder();
            for(Map.Entry<String, Submodel> part : parts.entrySet())
            {
                Submodel submodel = part.getValue();
                if (!owner.getPartVisibility(submodel))
                    continue;
                bakedParts.put(part.getKey(), submodel.bakeModel(bakery, spriteGetter, modelTransform, modelLocation));
            }
            return new CompositeModel(owner.isShadedInGui(), owner.useSmoothLighting(), particle, bakedParts.build(), owner.getCombinedTransform(), overrides);
        }

        @Override
        public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
        {
            Set<Material> textures = new HashSet<>();
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
}
