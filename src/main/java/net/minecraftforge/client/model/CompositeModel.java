/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.common.util.ConcatenatedListView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * A model composed of several named children.
 * <p>
 * These respect component visibility as specified in {@link IGeometryBakingContext} and can additionally be provided
 * with an item-specific render ordering, for multi-pass arrangements.
 */
public class CompositeModel implements IUnbakedGeometry<CompositeModel>
{
    private static final Logger LOGGER = LogManager.getLogger();

    private final ImmutableMap<String, BlockModel> children;
    private final ImmutableList<String> itemPasses;
    private final boolean logWarning;

    public CompositeModel(ImmutableMap<String, BlockModel> children, ImmutableList<String> itemPasses)
    {
        this(children, itemPasses, false);
    }

    private CompositeModel(ImmutableMap<String, BlockModel> children, ImmutableList<String> itemPasses, boolean logWarning)
    {
        this.children = children;
        this.itemPasses = itemPasses;
        this.logWarning = logWarning;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation)
    {
        if (logWarning)
            LOGGER.warn("Model \"" + modelLocation + "\" is using the deprecated \"parts\" field in its composite model instead of \"children\". This field will be removed in 1.20.");

        Material particleLocation = context.getMaterial("particle");
        TextureAtlasSprite particle = spriteGetter.apply(particleLocation);

        var rootTransform = context.getRootTransform();
        if (!rootTransform.isIdentity())
            modelState = new SimpleModelState(modelState.getRotation().compose(rootTransform), modelState.isUvLocked());

        var bakedPartsBuilder = ImmutableMap.<String, BakedModel>builder();
        for (var entry : children.entrySet())
        {
            var name = entry.getKey();
            if (!context.isComponentVisible(name, true))
                continue;
            var model = entry.getValue();
            bakedPartsBuilder.put(name, model.bake(bakery, model, spriteGetter, modelState, modelLocation, true));
        }
        var bakedParts = bakedPartsBuilder.build();

        var itemPassesBuilder = ImmutableList.<BakedModel>builder();
        for (String name : this.itemPasses)
        {
            var model = bakedParts.get(name);
            if (model == null)
                throw new IllegalStateException("Specified \"" + name + "\" in \"item_render_order\", but that is not a child of this model.");
            itemPassesBuilder.add(model);
        }

        return new Baked(context.isGui3d(), context.useBlockLight(), context.useAmbientOcclusion(), particle, context.getTransforms(), overrides, bakedParts, itemPassesBuilder.build());
    }

    @Override
    public Collection<Material> getMaterials(IGeometryBakingContext context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
    {
        Set<Material> textures = new HashSet<>();
        if (context.hasMaterial("particle"))
            textures.add(context.getMaterial("particle"));
        for (BlockModel part : children.values())
            textures.addAll(part.getMaterials(modelGetter, missingTextureErrors));
        return textures;
    }

    @Override
    public Set<String> getConfigurableComponentNames()
    {
        return children.keySet();
    }

    public static class Baked implements IDynamicBakedModel
    {
        private final boolean isAmbientOcclusion;
        private final boolean isGui3d;
        private final boolean isSideLit;
        private final TextureAtlasSprite particle;
        private final ItemOverrides overrides;
        private final ItemTransforms transforms;
        private final ImmutableMap<String, BakedModel> children;
        private final ImmutableList<BakedModel> itemPasses;

        public Baked(boolean isGui3d, boolean isSideLit, boolean isAmbientOcclusion, TextureAtlasSprite particle, ItemTransforms transforms, ItemOverrides overrides, ImmutableMap<String, BakedModel> children, ImmutableList<BakedModel> itemPasses)
        {
            this.children = children;
            this.isAmbientOcclusion = isAmbientOcclusion;
            this.isGui3d = isGui3d;
            this.isSideLit = isSideLit;
            this.particle = particle;
            this.overrides = overrides;
            this.transforms = transforms;
            this.itemPasses = itemPasses;
        }

        @NotNull
        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType)
        {
            List<List<BakedQuad>> quadLists = new ArrayList<>();
            for (Map.Entry<String, BakedModel> entry : children.entrySet())
            {
                if (renderType == null || (state != null && entry.getValue().getRenderTypes(state, rand, data).contains(renderType)))
                {
                    quadLists.add(entry.getValue().getQuads(state, side, rand, CompositeModel.Data.resolve(data, entry.getKey()), renderType));
                }
            }
            return ConcatenatedListView.of(quadLists);
        }

        @Override
        public @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData)
        {
            var builder = Data.builder();
            for (var entry : children.entrySet())
                builder.with(entry.getKey(), entry.getValue().getModelData(level, pos, state, Data.resolve(modelData, entry.getKey())));
            return modelData.derive().with(Data.PROPERTY, builder.build()).build();
        }

        @Override
        public boolean useAmbientOcclusion()
        {
            return isAmbientOcclusion;
        }

        @Override
        public boolean isGui3d()
        {
            return isGui3d;
        }

        @Override
        public boolean usesBlockLight()
        {
            return isSideLit;
        }

        @Override
        public boolean isCustomRenderer()
        {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleIcon()
        {
            return particle;
        }

        @Override
        public ItemOverrides getOverrides()
        {
            return overrides;
        }

        @Override
        public ItemTransforms getTransforms()
        {
            return transforms;
        }

        @Override
        public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data)
        {
            var sets = new ArrayList<ChunkRenderTypeSet>();
            for (Map.Entry<String, BakedModel> entry : children.entrySet())
                sets.add(entry.getValue().getRenderTypes(state, rand, CompositeModel.Data.resolve(data, entry.getKey())));
            return ChunkRenderTypeSet.union(sets);
        }

        @Override
        public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous)
        {
            return itemPasses;
        }

        @Nullable
        public BakedModel getPart(String name)
        {
            return children.get(name);
        }

        public static Builder builder(IGeometryBakingContext owner, TextureAtlasSprite particle, ItemOverrides overrides, ItemTransforms cameraTransforms)
        {
            return builder(owner.useAmbientOcclusion(), owner.isGui3d(), owner.useBlockLight(), particle, overrides, cameraTransforms);
        }

        public static Builder builder(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, TextureAtlasSprite particle, ItemOverrides overrides, ItemTransforms cameraTransforms)
        {
            return new Builder(isAmbientOcclusion, isGui3d, isSideLit, particle, overrides, cameraTransforms);
        }

        public static class Builder
        {
            private final boolean isAmbientOcclusion;
            private final boolean isGui3d;
            private final boolean isSideLit;
            private final List<BakedModel> children = new ArrayList<>();
            private final List<BakedQuad> quads = new ArrayList<>();
            private final ItemOverrides overrides;
            private final ItemTransforms transforms;
            private TextureAtlasSprite particle;
            private RenderTypeGroup lastRenderTypes = RenderTypeGroup.EMPTY;

            private Builder(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, TextureAtlasSprite particle, ItemOverrides overrides, ItemTransforms transforms)
            {
                this.isAmbientOcclusion = isAmbientOcclusion;
                this.isGui3d = isGui3d;
                this.isSideLit = isSideLit;
                this.particle = particle;
                this.overrides = overrides;
                this.transforms = transforms;
            }

            public void addLayer(BakedModel model)
            {
                flushQuads(null);
                children.add(model);
            }

            private void addLayer(RenderTypeGroup renderTypes, List<BakedQuad> quads)
            {
                var modelBuilder = IModelBuilder.of(isAmbientOcclusion, isSideLit, isGui3d, transforms, overrides, particle, renderTypes);
                quads.forEach(modelBuilder::addUnculledFace);
                children.add(modelBuilder.build());
            }

            private void flushQuads(RenderTypeGroup renderTypes)
            {
                if (!Objects.equals(renderTypes, lastRenderTypes))
                {
                    if (quads.size() > 0)
                    {
                        addLayer(lastRenderTypes, quads);
                        quads.clear();
                    }
                    lastRenderTypes = renderTypes;
                }
            }

            public Builder setParticle(TextureAtlasSprite particleSprite)
            {
                this.particle = particleSprite;
                return this;
            }

            public Builder addQuads(RenderTypeGroup renderTypes, BakedQuad... quadsToAdd)
            {
                flushQuads(renderTypes);
                Collections.addAll(quads, quadsToAdd);
                return this;
            }

            public Builder addQuads(RenderTypeGroup renderTypes, Collection<BakedQuad> quadsToAdd)
            {
                flushQuads(renderTypes);
                quads.addAll(quadsToAdd);
                return this;
            }

            public BakedModel build()
            {
                if (quads.size() > 0)
                {
                    addLayer(lastRenderTypes, quads);
                }
                var childrenBuilder = ImmutableMap.<String, BakedModel>builder();
                var itemPassesBuilder = ImmutableList.<BakedModel>builder();
                int i = 0;
                for (var model : this.children)
                {
                    childrenBuilder.put("model_" + (i++), model);
                    itemPassesBuilder.add(model);
                }
                return new Baked(isGui3d, isSideLit, isAmbientOcclusion, particle, transforms, overrides, childrenBuilder.build(), itemPassesBuilder.build());
            }
        }

    }

    /**
     * A model data container which stores data for child components.
     */
    public static class Data
    {
        public static final ModelProperty<Data> PROPERTY = new ModelProperty<>();

        private final Map<String, ModelData> partData;

        private Data(Map<String, ModelData> partData)
        {
            this.partData = partData;
        }

        @Nullable
        public ModelData get(String name)
        {
            return partData.get(name);
        }

        /**
         * Helper to get the data from a {@link ModelData} instance.
         *
         * @param modelData The object to get data from
         * @param name      The name of the part to get data for
         * @return The data for the part, or the one passed in if not found
         */
        public static ModelData resolve(ModelData modelData, String name)
        {
            var compositeData = modelData.get(PROPERTY);
            if (compositeData == null)
                return modelData;
            var partData = compositeData.get(name);
            return partData != null ? partData : modelData;
        }

        public static Builder builder()
        {
            return new Builder();
        }

        public static final class Builder
        {
            private final Map<String, ModelData> partData = new IdentityHashMap<>();

            public Builder with(String name, ModelData data)
            {
                partData.put(name, data);
                return this;
            }

            public Data build()
            {
                return new Data(partData);
            }
        }
    }

    public static final class Loader implements IGeometryLoader<CompositeModel>
    {
        public static final Loader INSTANCE = new Loader();

        private Loader()
        {
        }

        @Override
        public CompositeModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext)
        {
            List<String> itemPasses = new ArrayList<>();
            ImmutableMap.Builder<String, BlockModel> childrenBuilder = ImmutableMap.builder();
            readChildren(jsonObject, "children", deserializationContext, childrenBuilder, itemPasses, false);
            boolean logWarning = readChildren(jsonObject, "parts", deserializationContext, childrenBuilder, itemPasses, true);

            var children = childrenBuilder.build();
            if (children.isEmpty())
                throw new JsonParseException("Composite model requires a \"children\" element with at least one element.");

            if (jsonObject.has("item_render_order"))
            {
                itemPasses.clear();
                for (var element : jsonObject.getAsJsonArray("item_render_order"))
                {
                    var name = element.getAsString();
                    if (!children.containsKey(name))
                        throw new JsonParseException("Specified \"" + name + "\" in \"item_render_order\", but that is not a child of this model.");
                    itemPasses.add(name);
                }
            }

            return new CompositeModel(children, ImmutableList.copyOf(itemPasses), logWarning);
        }

        private boolean readChildren(JsonObject jsonObject, String name, JsonDeserializationContext deserializationContext, ImmutableMap.Builder<String, BlockModel> children, List<String> itemPasses, boolean logWarning)
        {
            if (!jsonObject.has(name))
                return false;
            var childrenJsonObject = jsonObject.getAsJsonObject(name);
            for (Map.Entry<String, JsonElement> entry : childrenJsonObject.entrySet())
            {
                children.put(entry.getKey(), deserializationContext.deserialize(entry.getValue(), BlockModel.class));
                itemPasses.add(entry.getKey()); // We can do this because GSON preserves ordering during deserialization
            }
            return logWarning;
        }
    }
}
