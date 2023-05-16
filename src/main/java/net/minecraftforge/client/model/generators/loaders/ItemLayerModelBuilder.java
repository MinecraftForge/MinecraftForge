/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.generators.loaders;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.ForgeFaceData;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemLayerModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T>
{
    public static <T extends ModelBuilder<T>> ItemLayerModelBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper)
    {
        return new ItemLayerModelBuilder<>(parent, existingFileHelper);
    }

    private final Int2ObjectMap<ForgeFaceData> faceData = new Int2ObjectOpenHashMap<>();
    private final Map<ResourceLocation, IntSet> renderTypes = new LinkedHashMap<>();
    private final IntSet layersWithRenderTypes = new IntOpenHashSet();

    protected ItemLayerModelBuilder(T parent, ExistingFileHelper existingFileHelper)
    {
        super(new ResourceLocation("forge:item_layers"), parent, existingFileHelper);
    }

    /**
     * Marks a set of layers to be rendered emissively.
     *
     * @param blockLight The block light (0-15)
     * @param skyLight The sky light (0-15)
     * @param layers the layers that will render unlit
     * @return this builder
     * @throws NullPointerException     if {@code layers} is {@code null}
     * @throws IllegalArgumentException if {@code layers} is empty
     * @throws IllegalArgumentException if any entry in {@code layers} is smaller than 0
     */
    public ItemLayerModelBuilder<T> emissive(int blockLight, int skyLight, int... layers)
    {
        Preconditions.checkNotNull(layers, "Layers must not be null");
        Preconditions.checkArgument(layers.length > 0, "At least one layer must be specified");
        Preconditions.checkArgument(Arrays.stream(layers).allMatch(i -> i >= 0), "All layers must be >= 0");
        for(int i : layers)
        {
            faceData.compute(i, (key, value) -> {
                ForgeFaceData fallback = value == null ? ForgeFaceData.DEFAULT : value;
                return new ForgeFaceData(fallback.color(), blockLight, skyLight, fallback.ambientOcclusion());
            });
        }
        return this;
    }

    /**
     * Marks a set of layers to be rendered with a specific color.
     *
     * @param color The color, in ARGB.
     * @param layers the layers that will render with color
     * @return this builder
     * @throws NullPointerException     if {@code layers} is {@code null}
     * @throws IllegalArgumentException if {@code layers} is empty
     * @throws IllegalArgumentException if any entry in {@code layers} is smaller than 0
     */
    public ItemLayerModelBuilder<T> color(int color, int... layers)
    {
        Preconditions.checkNotNull(layers, "Layers must not be null");
        Preconditions.checkArgument(layers.length > 0, "At least one layer must be specified");
        Preconditions.checkArgument(Arrays.stream(layers).allMatch(i -> i >= 0), "All layers must be >= 0");
        for(int i : layers)
        {
            faceData.compute(i, (key, value) -> {
                ForgeFaceData fallback = value == null ? ForgeFaceData.DEFAULT : value;
                return new ForgeFaceData(color, fallback.blockLight(), fallback.skyLight(), fallback.ambientOcclusion());
            });
        }
        return this;
    }

    /**
     * Set the render type for a set of layers.
     *
     * @param renderType the render type. Must be registered via
     *                   {@link net.minecraftforge.client.event.RegisterNamedRenderTypesEvent}
     * @param layers     the layers that will use this render type
     * @return this builder
     * @throws NullPointerException     if {@code renderType} is {@code null}
     * @throws NullPointerException     if {@code layers} is {@code null}
     * @throws IllegalArgumentException if {@code layers} is empty
     * @throws IllegalArgumentException if any entry in {@code layers} is smaller than 0
     * @throws IllegalArgumentException if any entry in {@code layers} already has a render type
     */
    public ItemLayerModelBuilder<T> renderType(String renderType, int... layers)
    {
        Preconditions.checkNotNull(renderType, "Render type must not be null");
        ResourceLocation asLoc;
        if (renderType.contains(":"))
            asLoc = new ResourceLocation(renderType);
        else
            asLoc = new ResourceLocation(parent.getLocation().getNamespace(), renderType);
        return renderType(asLoc, layers);
    }

    /**
     * Set the render type for a set of layers.
     *
     * @param renderType the render type. Must be registered via
     *                   {@link net.minecraftforge.client.event.RegisterNamedRenderTypesEvent}
     * @param layers     the layers that will use this render type
     * @return this builder
     * @throws NullPointerException     if {@code renderType} is {@code null}
     * @throws NullPointerException     if {@code layers} is {@code null}
     * @throws IllegalArgumentException if {@code layers} is empty
     * @throws IllegalArgumentException if any entry in {@code layers} is smaller than 0
     * @throws IllegalArgumentException if any entry in {@code layers} already has a render type
     */
    public ItemLayerModelBuilder<T> renderType(ResourceLocation renderType, int... layers)
    {
        Preconditions.checkNotNull(renderType, "Render type must not be null");
        Preconditions.checkNotNull(layers, "Layers must not be null");
        Preconditions.checkArgument(layers.length > 0, "At least one layer must be specified");
        Preconditions.checkArgument(Arrays.stream(layers).allMatch(i -> i >= 0), "All layers must be >= 0");
        var alreadyAssigned = Arrays.stream(layers).filter(layersWithRenderTypes::contains).toArray();
        Preconditions.checkArgument(alreadyAssigned.length == 0, "Attempted to re-assign layer render types: " + Arrays.toString(alreadyAssigned));
        var renderTypeLayers = renderTypes.computeIfAbsent(renderType, $ -> new IntOpenHashSet());
        Arrays.stream(layers).forEach(layer -> {
            renderTypeLayers.add(layer);
            layersWithRenderTypes.add(layer);
        });
        return this;
    }

    @Override
    public JsonObject toJson(JsonObject json)
    {
        json = super.toJson(json);

        JsonObject forgeData = new JsonObject();
        JsonObject layerObj = new JsonObject();

        for(Int2ObjectMap.Entry<ForgeFaceData> entry : this.faceData.int2ObjectEntrySet())
        {
            layerObj.add(String.valueOf(entry.getIntKey()), ForgeFaceData.CODEC.encodeStart(JsonOps.INSTANCE, entry.getValue()).getOrThrow(false, s -> {}));
        }

        forgeData.add("layers", layerObj);
        json.add("forge_data", forgeData);

        JsonObject renderTypes = new JsonObject();
        this.renderTypes.forEach((renderType, layers) -> {
            JsonArray array = new JsonArray();
            layers.intStream().sorted().forEach(array::add);
            renderTypes.add(renderType.toString(), array);
        });
        json.add("render_types", renderTypes);

        return json;
    }
}
