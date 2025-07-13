/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.gui.overlay;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ForgeEventFactoryClient;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.BooleanSupplier;

/**
 * As vanilla has switched to a layered drawing system for overlays, this system replaces ForgeGui and its associated headaches.
 * Vanilla will now have resource locations to represent its render layers which modders can order against.
 * This class is effectively a pseudo-registry for Layers. Add what you need during {@linkplain AddGuiOverlayLayersEvent}
 * After being resolved, it is too late to order against vanilla layers. Do it during the event.
 * Layer and LayeredDraws are expected to be uniquely named.
 * Changes will not be made if a Layer/LayeredDraw addition would result in a duplicate.
 */
@FieldsAreNonnullByDefault
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class ForgeLayeredDraw extends LayeredDraw {
    private static int unknown = 0;
    private final Map<ResourceLocation, Layer> namedLayers = new HashMap<>();
    private final Map<ResourceLocation, Map.Entry<LayeredDraw, BooleanSupplier>> subLayerStacks = new HashMap<>();
    private final List<ResourceLocation> order = new LinkedList<>();
    private final List<ResourceLocation> expectedNames = new ArrayList<>();
    private final ResourceLocation phase;

    public static final ResourceLocation  PRE_SLEEP_PHASE = ResourceLocation.withDefaultNamespace("pre_sleep_phase");
    public static final ResourceLocation   CAMERA_OVERLAY = ResourceLocation.withDefaultNamespace("camera_overlay");
    public static final ResourceLocation        CROSSHAIR = ResourceLocation.withDefaultNamespace("crosshair");
    public static final ResourceLocation           HOTBAR = ResourceLocation.withDefaultNamespace("hotbar");
    public static final ResourceLocation       EXPERIENCE = ResourceLocation.withDefaultNamespace("experience");
    public static final ResourceLocation   POTION_EFFECTS = ResourceLocation.withDefaultNamespace("potion_effects");
    public static final ResourceLocation     BOSS_OVERLAY = ResourceLocation.withDefaultNamespace("boss_overlay");

    public static final ResourceLocation POST_SLEEP_PHASE = ResourceLocation.withDefaultNamespace("post_sleep_phase");
    public static final ResourceLocation     DEMO_OVERLAY = ResourceLocation.withDefaultNamespace("demo");
    public static final ResourceLocation    DEBUG_OVERLAY = ResourceLocation.withDefaultNamespace("debug");
    public static final ResourceLocation       SCOREBOARD = ResourceLocation.withDefaultNamespace("scoreboard");
    public static final ResourceLocation   HOTBAR_MESSAGE = ResourceLocation.withDefaultNamespace("hotbar_message");
    public static final ResourceLocation    TITLE_OVERLAY = ResourceLocation.withDefaultNamespace("title");
    public static final ResourceLocation     CHAT_OVERLAY = ResourceLocation.withDefaultNamespace("chat_overlay");
    public static final ResourceLocation         TAB_LIST = ResourceLocation.withDefaultNamespace("tab_list");
    public static final ResourceLocation SUBTITLE_OVERLAY = ResourceLocation.withDefaultNamespace("subtitle");

    public static final ResourceLocation    COMBINE_PHASE = ResourceLocation.withDefaultNamespace("combine_phase");
    public static final ResourceLocation    SLEEP_OVERLAY = ResourceLocation.withDefaultNamespace("sleep_overlay");


    public static final ImmutableList<ResourceLocation> PRE_LIST = ImmutableList.of(CAMERA_OVERLAY, CROSSHAIR, HOTBAR, EXPERIENCE, POTION_EFFECTS, BOSS_OVERLAY);
    public static final ImmutableList<ResourceLocation> POST_LIST = ImmutableList.of(DEMO_OVERLAY, DEBUG_OVERLAY, SCOREBOARD, HOTBAR_MESSAGE, TITLE_OVERLAY, CHAT_OVERLAY, TAB_LIST, SUBTITLE_OVERLAY);
    public static final ImmutableList<ResourceLocation> COMBINE_LIST = ImmutableList.of(PRE_SLEEP_PHASE, SLEEP_OVERLAY, POST_SLEEP_PHASE);

    /**
     * Creates a stack of pre-named layers for vanilla.
     * Not intended for modder use. Use {@linkplain ForgeLayeredDraw(ResourceLocation, Boolean, String...)} instead.
     * @param phase Phase indicator
     * @param layers locations of layers to add
     */
    @ApiStatus.Internal
    public ForgeLayeredDraw(ResourceLocation phase, List<ResourceLocation> layers) {
        this.phase = phase;
        expectedNames.addAll(layers);
    }

    /**
     * Creates an empty draw list. Add entries with {@linkplain ForgeLayeredDraw#add(ResourceLocation, Layer)}
     * @param phase marker for which phase this is.
     */
    public ForgeLayeredDraw(ResourceLocation phase) {
        this.phase = phase;
    }

    /**
     * Adds a full, pre-named draw stack.
     * Modders should be using {@linkplain ForgeLayeredDraw#add(ResourceLocation, ForgeLayeredDraw, BooleanSupplier)}
     * @param layeredDraw layer stack to be added.
     * @param booleanSupplier requirement for it to render.
     * @return this
     */
    @ApiStatus.Internal
    @Override
    public ForgeLayeredDraw add(LayeredDraw layeredDraw, BooleanSupplier booleanSupplier) {
        ResourceLocation name = getName();
        order.add(name);
        subLayerStacks.put(name, Map.entry(layeredDraw, booleanSupplier));
        return this;
    }

    /**
     * Adds a full draw stack with its provided condition.
     * @param name RL of the name to identify this stack with.
     * @param layeredDraw the draw stack
     * @param supplier condition for this stack to render
     * @return this
     */
    public ForgeLayeredDraw add(ResourceLocation name, ForgeLayeredDraw layeredDraw, BooleanSupplier supplier) {
        if (findLayer(name) == null) {
            subLayerStacks.put(name, Map.entry(layeredDraw, supplier));
            order.add(name);
        } else {
            layerAlreadyPresentWarning(name);
        }
        return this;
    }

    /**
     * Adds a layer with an already known name provided by {@linkplain ForgeLayeredDraw(ResourceLocation, List)}
     * The layer will be rendered last (on top) of already added layers.
     * Use {@linkplain ForgeLayeredDraw#add(ResourceLocation, Layer)} for adding individual layers.
     * @param layer layer render code, see {@linkplain Layer} and example usages in {@linkplain Gui}
     * @return this
     */
    @ApiStatus.Internal
    @Override
    public LayeredDraw add(Layer layer) {
        ResourceLocation name = getName();
        order.add(name);
        namedLayers.put(name, layer);
        return this;
    }

    /**
     * Add a layer to the layer list. This layer will be at the end of the list, which means
     * it will be rendered last (on top) of already added layers.
     * @param name RL for other mods to order against.
     * @param layer layer render code, see {@linkplain Layer} and example usages in {@linkplain Gui}
     * @return this
     */
    public ForgeLayeredDraw add(ResourceLocation name, Layer layer) {
        if (findLayer(name) == null) {
            namedLayers.put(name, layer);
            order.add(name);
        } else {
            layerAlreadyPresentWarning(name);
        }
        return this;
    }

    /**
     * Use to specify where your custom draw stack should go. Can also be used to re-order layers.
     * @param target layer name to move
     * @param destination layer name to order against
     * @return this
     */
    public ForgeLayeredDraw putAbove(ResourceLocation target, ResourceLocation destination) {
        var destLayer = findLayer(destination);
        if (destLayer != null) {
            destLayer.order.remove(target); // Prevent duplicates.
            int loc = destLayer.order.indexOf(destination);
            order.add(loc+1, target);
        } else {
            LogUtils.getLogger().warn("{} is not an available entry. Cannot put {} above {}", target, target, destination);
        }
        return this;
    }

    /**
     * Use to specify where your custom draw stack should go. Can also be used to re-order layers.
     * @param target layer name to move
     * @param destination layer name to order against
     * @return this
     */
    public ForgeLayeredDraw putBelow(ResourceLocation target, ResourceLocation destination) {
        var stack = findLayer(target);
        if (stack != null) {
            stack.order.remove(target); // Prevent duplicates.
            int loc = stack.order.indexOf(destination);
            order.add(loc, target);
        } else {
            LogUtils.getLogger().warn("{} is not an available entry. Cannot put {} below {}", target, target, destination);
        }
        return this;
    }

    /**
     * Adds an overlay layer to be rendered above the other provided layer.
     * To render "above" another layer means thisLayer will be rendered after otherLayer
     * If the current stack does not contain otherLayer, no changes will be made.
     * @param thisLayer name of the layer to be added
     * @param otherLayer name of the layer being ordered against
     * @param layer layer render code, see {@linkplain Layer} and example usages in {@linkplain Gui}
     * @return this
     */
    public ForgeLayeredDraw addAbove(ResourceLocation thisLayer, ResourceLocation otherLayer, Layer layer) {
        ForgeLayeredDraw stack = findLayer(otherLayer);
        if (stack != null) {
            stack.namedLayers.put(thisLayer, layer);
            stack.order.add(stack.order.indexOf(otherLayer)+1, thisLayer);
        } else {
            layerNotPresentWarning(otherLayer);
        }
        return this;
    }

    /**
     * Adds an overlay layer to be rendered below the other provided layer.
     * To render "below" another layer means thisLayer will be rendered before otherLayer
     * If the current stack does not contain otherLayer, no changes will be made.
     * @param thisLayer name of the layer to be added
     * @param otherLayer name of the layer being ordered against
     * @param layer layer render code, see {@linkplain Layer} and example usages in {@linkplain Gui}
     * @return this
     */
    public ForgeLayeredDraw addBelow(ResourceLocation thisLayer, ResourceLocation otherLayer, Layer layer) {
        ForgeLayeredDraw stack = findLayer(otherLayer);
        if (stack != null) {
            stack.namedLayers.put(thisLayer, layer);
            stack.order.add(stack.order.indexOf(otherLayer), thisLayer);
        } else {
            layerNotPresentWarning(otherLayer);
        }
        return this;
    }

    /**
     * Add a new layer that will only be rendered when the condition is met. The layer will be added
     * at the end of the list, which means it will render last (on top) of already added layers
     * @param name name of the layer to be added
     * @param layer render code of the layer to be added.
     * @param condition supplier for the condition
     * @return this
     */
    public ForgeLayeredDraw addWithCondition(ResourceLocation name, Layer layer, BooleanSupplier condition) {
        return add(name, layer).addConditionTo(name, condition);
    }

    /**
     * Add a condition to a pre-existing layer, its render order is not changed.
     * If the target is not present, no changes are made.
     * @param target name of layer to add a condition to
     * @param condition supplier for the condition
     * @return this
     */
    public ForgeLayeredDraw addConditionTo(ResourceLocation target, BooleanSupplier condition) {
        var stack = findLayer(target);
        if (stack == null) {
            layerNotPresentWarning(target);
        } else {
            stack.namedLayers.computeIfPresent(target,
                    (name, layer) -> (guiGraphics, deltaTracker) -> {
                        if (condition.getAsBoolean()) {
                            layer.render(guiGraphics, deltaTracker);
                        }
                    });
        }
        return this;
    }

    /**
     * @return phase name of this ForgeLayeredDraw instance
     */
    public ResourceLocation getPhase() {
        return phase;
    }

    /**
     * Propagate the layer order down to the inner render list after providing modders an opportunity to alter the list as they wish.
     * @apiNote Modders should <emph>NEVER</emph> be calling this method.
     * @return this
     */
    @ApiStatus.Internal
    public ForgeLayeredDraw resolveLayers() {
        if (!order.isEmpty()) {
            ForgeEventFactoryClient.onComputeLayerOrder(this);
        }
        if (namedLayers.size() + subLayerStacks.size() < order.size()) {
            LogUtils.getLogger().warn("Found {} unbound pre-defined layer names when resolving gui overlay order. This is not an error, but potentially indicates a mod directly modifying Gui instead of using this api.", order.size() - namedLayers.size());
        }
        resolveNested();
        order.clear();
        return this;
    }

    /**
     * Resolve the layer order per stack, recursively per each sub stack.
     * Parent layer stack {@linkplain ForgeLayeredDraw#COMBINE_PHASE} holds the results.
     */
    private void resolveNested() {
        for (ResourceLocation layerName : order) {
            if (subLayerStacks.containsKey(layerName)) {
                var entry = subLayerStacks.get(layerName);
                ((ForgeLayeredDraw) entry.getKey()).resolveNested();
                super.add((gg, tr) -> {
                    if (entry.getValue().getAsBoolean()) entry.getKey().render(gg,tr);
                });
            } else {
                super.add(namedLayers.get(layerName));
            }
        }
    }

    private void layerNotPresentWarning(ResourceLocation layer) {
        LogUtils.getLogger().warn("Could not find layer {}, no layer modifications have been made.", layer);
    }

    private void layerAlreadyPresentWarning(ResourceLocation layer) {
        LogUtils.getLogger().warn("Layer {} was already present and cannot be overwritten. Consider using addConditionTo to cancel the layer and order after it.", layer);
    }

    /**
     * Locate which stack the target is in.
     * @param target targetted layer
     * @return the stack which contains the layer, or null if not present.
     */
    @Nullable
    private ForgeLayeredDraw findLayer(ResourceLocation target) {
        if (!namedLayers.containsKey(target) && !subLayerStacks.containsKey(target)) {
            for (Map.Entry<LayeredDraw, BooleanSupplier> value : subLayerStacks.values()) {
                var res = ((ForgeLayeredDraw) value.getKey()).findLayer(target);
                if (res != null) return res;
            }
            return null;
        } else {
            return this;
        }
    }

    private ResourceLocation getName() {
        return expectedNames.isEmpty() ? ResourceLocation.fromNamespaceAndPath("unknown", "layer_" + unknown++) : expectedNames.removeFirst();
    }
}
