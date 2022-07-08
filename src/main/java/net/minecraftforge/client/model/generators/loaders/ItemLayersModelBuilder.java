/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.generators.loaders;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.*;

public class ItemLayersModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T>
{
    public static <T extends ModelBuilder<T>> ItemLayersModelBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper)
    {
        return new ItemLayersModelBuilder<>(parent, existingFileHelper);
    }

    private final IntSet emissiveLayers = new IntOpenHashSet();
    private final Map<ResourceLocation, IntSet> renderTypes = new LinkedHashMap<>();
    private final IntSet layersWithRenderTypes = new IntOpenHashSet();

    protected ItemLayersModelBuilder(T parent, ExistingFileHelper existingFileHelper)
    {
        super(new ResourceLocation("forge:item_layers"), parent, existingFileHelper);
    }

    /**
     * Marks a set of layers to be rendered emissively.
     *
     * @param layers the layers that will render unlit
     * @return this builder
     * @throws NullPointerException     if {@code layers} is {@code null}
     * @throws IllegalArgumentException if {@code layers} is empty
     * @throws IllegalArgumentException if any entry in {@code layers} is smaller than 0
     */
    public ItemLayersModelBuilder<T> emissive(int... layers)
    {
        Preconditions.checkNotNull(layers, "Layers must not be null");
        Preconditions.checkArgument(layers.length > 0, "At least one layer must be specified");
        Preconditions.checkArgument(Arrays.stream(layers).allMatch(i -> i >= 0), "All layers must be >= 0");
        Arrays.stream(layers).forEach(emissiveLayers::add);
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
    public ItemLayersModelBuilder<T> renderType(String renderType, int... layers)
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
    public ItemLayersModelBuilder<T> renderType(ResourceLocation renderType, int... layers)
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

        JsonArray unlitLayers = new JsonArray();
        this.emissiveLayers.intStream().sorted().forEach(unlitLayers::add);
        json.add("emissive_layers", unlitLayers);

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
