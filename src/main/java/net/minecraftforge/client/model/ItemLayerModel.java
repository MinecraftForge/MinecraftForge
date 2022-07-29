/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ForgeRenderTypes;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.client.model.geometry.UnbakedGeometryHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Forge reimplementation of vanilla's {@link ItemModelGenerator}, i.e. builtin/generated models with some tweaks:
 * - Represented as {@link IUnbakedGeometry} so it can be baked as usual instead of being special-cased
 * - Not limited to an arbitrary number of layers (5)
 * - Support for per-layer render types
 */
public class ItemLayerModel implements IUnbakedGeometry<ItemLayerModel>
{
    private static final Logger LOGGER = LogManager.getLogger();

    @Nullable
    private ImmutableList<Material> textures;
    private final IntSet emissiveLayers;
    private final Int2ObjectMap<ResourceLocation> renderTypeNames;
    private final boolean deprecatedLoader, logWarning;

    public ItemLayerModel(@Nullable ImmutableList<Material> textures, IntSet emissiveLayers, Int2ObjectMap<ResourceLocation> renderTypeNames)
    {
        this(textures, emissiveLayers, renderTypeNames, false, false);
    }

    private ItemLayerModel(@Nullable ImmutableList<Material> textures, IntSet emissiveLayers, Int2ObjectMap<ResourceLocation> renderTypeNames, boolean deprecatedLoader, boolean logWarning)
    {
        this.textures = textures;
        this.emissiveLayers = emissiveLayers;
        this.renderTypeNames = renderTypeNames;
        this.deprecatedLoader = deprecatedLoader;
        this.logWarning = logWarning;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation)
    {
        if (textures == null)
            throw new IllegalStateException("Textures have not been initialized. Either pass them in through the constructor or call getMaterials(...) first.");

        if (deprecatedLoader)
            LOGGER.warn("Model \"" + modelLocation + "\" is using the deprecated loader \"forge:item-layers\" instead of \"forge:item_layers\". This loader will be removed in 1.20.");
        if (logWarning)
            LOGGER.warn("Model \"" + modelLocation + "\" is using the deprecated \"fullbright_layers\" field in its item layer model instead of \"emissive_layers\". This field will be removed in 1.20.");

        TextureAtlasSprite particle = spriteGetter.apply(
                context.hasMaterial("particle") ? context.getMaterial("particle") : textures.get(0)
        );
        var rootTransform = context.getRootTransform();
        if (!rootTransform.isIdentity())
            modelState = new SimpleModelState(modelState.getRotation().compose(rootTransform), modelState.isUvLocked());

        var normalRenderTypes = new RenderTypeGroup(RenderType.translucent(), ForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get());
        CompositeModel.Baked.Builder builder = CompositeModel.Baked.builder(context, particle, overrides, context.getTransforms());
        for (int i = 0; i < textures.size(); i++)
        {
            TextureAtlasSprite sprite = spriteGetter.apply(textures.get(i));
            var unbaked = UnbakedGeometryHelper.createUnbakedItemElements(i, sprite);
            var quads = UnbakedGeometryHelper.bakeElements(unbaked, $ -> sprite, modelState, modelLocation);
            if (emissiveLayers.contains(i)) QuadTransformers.settingMaxEmissivity().processInPlace(quads);
            var renderTypeName = renderTypeNames.get(i);
            var renderTypes = renderTypeName != null ? context.getRenderType(renderTypeName) : null;
            builder.addQuads(renderTypes != null ? renderTypes : normalRenderTypes, quads);
        }

        return builder.build();
    }

    @Override
    public Collection<Material> getMaterials(IGeometryBakingContext context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
    {
        if (textures != null)
            return textures;

        ImmutableList.Builder<Material> builder = ImmutableList.builder();
        if (context.hasMaterial("particle"))
            builder.add(context.getMaterial("particle"));
        for (int i = 0; context.hasMaterial("layer" + i); i++)
        {
            builder.add(context.getMaterial("layer" + i));
        }
        return textures = builder.build();
    }

    public static final class Loader implements IGeometryLoader<ItemLayerModel>
    {
        public static final Loader INSTANCE = new Loader(false);
        @Deprecated(forRemoval = true, since = "1.19")
        public static final Loader INSTANCE_DEPRECATED = new Loader(true);

        private final boolean deprecated;

        private Loader(boolean deprecated)
        {
            this.deprecated = deprecated;
        }

        @Override
        public ItemLayerModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext)
        {
            var renderTypeNames = new Int2ObjectOpenHashMap<ResourceLocation>();
            if (jsonObject.has("render_types"))
            {
                var renderTypes = jsonObject.getAsJsonObject("render_types");
                for (Map.Entry<String, JsonElement> entry : renderTypes.entrySet())
                {
                    var renderType = new ResourceLocation(entry.getKey());
                    for (var layer : entry.getValue().getAsJsonArray())
                        if (renderTypeNames.put(layer.getAsInt(), renderType) != null)
                            throw new JsonParseException("Registered duplicate render type for layer " + layer);
                }
            }

            var emissiveLayers = new IntOpenHashSet();
            readUnlit(jsonObject, "emissive_layers", renderTypeNames, emissiveLayers, false);
            boolean logWarning = readUnlit(jsonObject, "fullbright_layers", renderTypeNames, emissiveLayers, true); // TODO: Deprecated name. To be removed in 1.20

            return new ItemLayerModel(null, emissiveLayers, renderTypeNames, deprecated, logWarning);
        }

        private boolean readUnlit(JsonObject jsonObject, String name, Int2ObjectOpenHashMap<ResourceLocation> renderTypeNames, IntOpenHashSet litLayers, boolean logWarning)
        {
            if (!jsonObject.has(name))
                return false;
            var fullbrightLayers = jsonObject.getAsJsonArray(name);
            var renderType = new ResourceLocation("forge", "item_unlit");
            for (var layer : fullbrightLayers)
            {
                litLayers.add(layer.getAsInt());
                renderTypeNames.putIfAbsent(layer.getAsInt(), renderType);
            }
            return logWarning && !fullbrightLayers.isEmpty();
        }
    }
}
