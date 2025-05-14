/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.gui.overlay;

import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ForgeEventFactoryClient;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * As vanilla has switched to a layered drawing system for overlays, this system replaces ForgeGui and its associated headaches.
 * Vanilla will now have resource locations to represent its render layers which modders can order against.
 */
public final class ForgeLayeredDraw extends LayeredDraw {
    private final Deque<ResourceLocation> expected = new ArrayDeque<>();
    private int count = 0;
    private final Map<ResourceLocation, Layer> namedLayers = new HashMap<>();
    private final List<ResourceLocation> order = new LinkedList<>();

    /**
     * Creates a stack of pre-named layers for vanilla.
     * Not intended for modder use. Use {@link ForgeLayeredDraw(Supplier, String...)} instead.
     * @param layers
     */
    public ForgeLayeredDraw(String... layers) {
        for (String vanillaLayerName : layers) {
            expected.push(ResourceLocation.withDefaultNamespace(vanillaLayerName));
        }
    }

    /**
     * Creates a list of pre-defined names for layers to be added via {@link ForgeLayeredDraw#add(Layer)}
     * @param modIdProvider
     * @param layers
     */
    public ForgeLayeredDraw(Supplier<String> modIdProvider, String... layers) {
        for (String layer : layers) {
            expected.push(ResourceLocation.fromNamespaceAndPath(modIdProvider.get(), layer));
        }
    }

    /**
     * Adds a layer with an already known name provided by {@link ForgeLayeredDraw(Supplier, String...)}
     * The layer will be rendered last (on top) of already added layers.
     * Use {@link ForgeLayeredDraw#add(ResourceLocation, Layer)} for adding individual layers.
     * Only use this if you've constructed this list yourself with the associated constructor.
     * @param layer
     * @return this
     */
    @Override
    public LayeredDraw add(Layer layer) {
        return add(expected.isEmpty() ?
                ResourceLocation.fromNamespaceAndPath("unknown", String.valueOf(count++))
                : expected.pop(), layer);
    }

    /**
     * Add a layer to the layer list. This layer will be at the end of the list, which means
     * it will be rendered last (on top) of already added layers.
     * @param name
     * @param layer
     * @return this
     */
    public ForgeLayeredDraw add(ResourceLocation name, Layer layer) {
        namedLayers.put(name, layer);
        order.add(name);
        return this;
    }

    /**
     * Add a new layer that will only be rendered when the condition is met. The layer will be added
     * at the end of the list, which means it will render render last (on top) of already added layers
     * @param name
     * @param layer
     * @param condition
     * @return this
     */
    public ForgeLayeredDraw addWithCondition(ResourceLocation name, Layer layer, BooleanSupplier condition) {
        return add(name, layer).addConditionTo(name, condition);
    }

    /**
     * Add a condition to a pre-existing layer, its render order is not changed.
     * DOes nothing if the target is not present.
     * @param target
     * @param condition
     * @return this
     */
    public ForgeLayeredDraw addConditionTo(ResourceLocation target, BooleanSupplier condition) {
        if (namedLayers.containsKey(target)) {
            namedLayers.put(target, (guiGraphics, deltaTracker) -> {
                if (condition.getAsBoolean()) namedLayers.get(target).render(guiGraphics, deltaTracker);
            });
        }
        return this;
    }

    /**
     * Adds an overlay layer to be rendered above the other provided layer.
     * To render "above" another layer means thisLayer will be rendered after otherLayer
     * If the current stack does not contain otherLayer, no changes will be made.
     * @param thisLayer layer being added
     * @param otherLayer layer being ordered against
     * @param layer layer render code, see {@link Layer} and example usages in {@link net.minecraft.client.gui.Gui}
     * @return this
     */
    public ForgeLayeredDraw addAbove(ResourceLocation thisLayer, ResourceLocation otherLayer, Layer layer) {
        int loc = order.indexOf(otherLayer);
        if (loc != -1) {
            namedLayers.put(thisLayer, layer);
            order.add(loc+1, thisLayer);
        }
        return this;
    }

    /**
     * Adds an overlay layer to be rendered below the other provided layer.
     * To render "below" another layer means thisLayer will be rendered before otherLayer
     * If the current stack does not contain otherLayer, no changes will be made.
     * @param thisLayer layer being added
     * @param otherLayer layer being ordered against
     * @param layer layer render code, see {@link Layer} and example usages in {@link net.minecraft.client.gui.Gui}
     * @return this
     */
    public ForgeLayeredDraw addBelow(ResourceLocation thisLayer, ResourceLocation otherLayer, Layer layer) {
        int loc = order.indexOf(otherLayer);
        if (loc != -1) {
            namedLayers.put(thisLayer, layer);
            order.add(loc, thisLayer);
        }
        return this;
    }

    /**
     * Move the target layer immediately above the destination layer.
     * See {@link ForgeLayeredDraw#addAbove(ResourceLocation, ResourceLocation, Layer)}
     * for details on the definition of "above".
     * @param target
     * @param destination
     * @return this
     */
    public ForgeLayeredDraw moveAbove(ResourceLocation target, ResourceLocation destination) {
        int locTarget = order.indexOf(target);
        int locDestination = order.indexOf(destination);
        if (locTarget != -1 && locDestination != -1) {
            order.add(locDestination+1, order.remove(locTarget));
        }
        return this;
    }

    /**
     * Move the target layer immediately below the destination layer.
     * See {@link ForgeLayeredDraw#addBelow(ResourceLocation, ResourceLocation, Layer)}
     * for details on the definition of "below".
     * @param target
     * @param destination
     * @return this
     */
    public ForgeLayeredDraw moveBelow(ResourceLocation target, ResourceLocation destination) {
        int locTarget = order.indexOf(target);
        int locDestination = order.indexOf(destination);
        if (locTarget != -1 && locDestination != -1) {
            order.add(locDestination, order.remove(locTarget));
        }
        return this;
    }

    /**
     * Finalizes the order based on the order defined by the order field after
     * giving modders an opportunity to modify it.
     */
    public void finish() {
        ForgeEventFactoryClient.onComputeLayerOrder(this);
        if (!expected.isEmpty()) LogUtils.getLogger().warn("Found {} unbound layer names when constructing gui layer list.", expected.size());
        for (ResourceLocation resourceLocation : order) {
            super.add(namedLayers.get(resourceLocation));
        }
    }
}
