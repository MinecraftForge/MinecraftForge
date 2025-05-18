/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.gui.overlay;

import com.mojang.logging.LogUtils;
import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ForgeEventFactoryClient;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.BooleanSupplier;

/**
 * As vanilla has switched to a layered drawing system for overlays, this system replaces ForgeGui and its associated headaches.
 * Vanilla will now have resource locations to represent its render layers which modders can order against.
 * This class is effectively a mini-registry for layers. Add what you need during {@link net.minecraftforge.client.event.ModifyOverlayLayersEvent}
 */
@FieldsAreNonnullByDefault
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class ForgeLayeredDraw extends LayeredDraw {
    private final Deque<ResourceLocation> expected = new ArrayDeque<>();
    private int count = 0;
    private final Map<ResourceLocation, Layer> namedLayers = new HashMap<>();
    private final List<ResourceLocation> order = new LinkedList<>();
    private final ResourceLocation phase;
    private final boolean mayEdit;
    private boolean finalized = false;

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

    /**
     * Creates a stack of pre-named layers for vanilla.
     * Not intended for modder use. Use {@link ForgeLayeredDraw(ResourceLocation, Boolean, String...)} instead.
     * @param layers Head of varargs marks the phase, the remaining args mark pre-named layers.
     */
    @ApiStatus.Internal
    public ForgeLayeredDraw(ResourceLocation rl, String... layers) {
        phase = rl;
        mayEdit = true;
        for (String layer : layers) {
            expected.add(ResourceLocation.withDefaultNamespace(layer));
        }
    }

    /**
     * Creates an empty draw list. Add entries with {@link ForgeLayeredDraw#add(ResourceLocation, Layer)}
     * @param rl marker for which phase this is.
     * @param mayEdit whether other mods are allowed to insert layers into this list.
     */
    public ForgeLayeredDraw(ResourceLocation rl, boolean mayEdit) {
        phase = rl;
        this.mayEdit = mayEdit;
    }

    /**
     * Creates a list of pre-defined names for layers to be added via {@link ForgeLayeredDraw#add(Layer)}
     * @param rl Used as the marker for what specific LayeredDraw phase this is.
     * @param mayEdit Optionally fire ModifyOverlayLayers for this modded stack.
     * @param layers List of pre-named layers.
     */
    public ForgeLayeredDraw(ResourceLocation rl, boolean mayEdit, String... layers) {
        phase = rl;
        this.mayEdit = mayEdit;
        for (String layer : layers) {
            expected.add(ResourceLocation.fromNamespaceAndPath(rl.getPath(), layer));
        }
    }

    /**
     * Adds a layer with an already known name provided by {@link ForgeLayeredDraw(ResourceLocation, Boolean, String...)}
     * The layer will be rendered last (on top) of already added layers.
     * Use {@link ForgeLayeredDraw#add(ResourceLocation, Layer)} for adding individual layers.
     * @param layer layer render code, see {@link Layer} and example usages in {@link net.minecraft.client.gui.Gui}
     * @return this
     */
    @Override
    public LayeredDraw add(Layer layer) {
        return add(expected.isEmpty() ?
                ResourceLocation.fromNamespaceAndPath("unknown", String.valueOf(count++))
                : expected.remove(), layer);
    }

    /**
     * Add a layer to the layer list. This layer will be at the end of the list, which means
     * it will be rendered last (on top) of already added layers.
     * @param name RL for other mods to order against.
     * @param layer layer render code, see {@link Layer} and example usages in {@link net.minecraft.client.gui.Gui}
     * @return this
     */
    public ForgeLayeredDraw add(ResourceLocation name, Layer layer) {
        namedLayers.put(name, layer);
        order.add(name);
        return this;
    }

    /**
     * Adds an overlay layer to be rendered above the other provided layer.
     * To render "above" another layer means thisLayer will be rendered after otherLayer
     * If the current stack does not contain otherLayer, no changes will be made.
     * @param thisLayer name of the layer to be added
     * @param otherLayer name of the layer being ordered against
     * @param layer layer render code, see {@link Layer} and example usages in {@link net.minecraft.client.gui.Gui}
     * @return this
     */
    public ForgeLayeredDraw addAbove(ResourceLocation thisLayer, ResourceLocation otherLayer, Layer layer) {
        int loc = order.indexOf(otherLayer);
        if (loc != -1) {
            namedLayers.put(thisLayer, layer);
            order.add(loc+1, thisLayer);
        } else {
            warn(otherLayer);
        }
        return this;
    }

    /**
     * Adds an overlay layer to be rendered below the other provided layer.
     * To render "below" another layer means thisLayer will be rendered before otherLayer
     * If the current stack does not contain otherLayer, no changes will be made.
     * @param thisLayer name of the layer to be added
     * @param otherLayer name of the layer being ordered against
     * @param layer layer render code, see {@link Layer} and example usages in {@link net.minecraft.client.gui.Gui}
     * @return this
     */
    public ForgeLayeredDraw addBelow(ResourceLocation thisLayer, ResourceLocation otherLayer, Layer layer) {
        int loc = order.indexOf(otherLayer);
        if (loc != -1) {
            namedLayers.put(thisLayer, layer);
            order.add(loc, thisLayer);
        } else {
            warn(otherLayer);
        }
        return this;
    }

    /**
     * Convert this layered draw stack into a layer, it may now be treated as any other layer
     * following all conventions in the rest of this class. The internal order of the stack is preserved.
     * @return the resulting Layer. May now be treated as any other layer.
     */
    public Layer asLayer() {
        if (!finalized) LogUtils.getLogger().warn("{} was converted to a layer before its internal layer order was computed. If it isn't being added or rendered, this is why.", phase);
        return this::render;
    }

    /**
     * Add a new layer that will only be rendered when the condition is met. The layer will be added
     * at the end of the list, which means it will render last (on top) of already added layers
     * @param name name of the layer to be added
     * @param layer render code of the layer to be added.
     * @param condition coni
     * @return this
     */
    public ForgeLayeredDraw addWithCondition(ResourceLocation name, Layer layer, BooleanSupplier condition) {
        return add(name, layer).addConditionTo(name, condition);
    }

    /**
     * Add a condition to a pre-existing layer, its render order is not changed.
     * If the target is not present, no changes are made.
     * @param target
     * @param condition
     * @return this
     */
    public ForgeLayeredDraw addConditionTo(ResourceLocation target, BooleanSupplier condition) {
        if (namedLayers.containsKey(target)) {
            namedLayers.computeIfPresent(target, (k, res) -> (guiGraphics, deltaTracker) -> {
                if (condition.getAsBoolean()) res.render(guiGraphics, deltaTracker);
            });
        } else {
            warn(target);
        }
        return this;
    }

    public ResourceLocation getPhase() {
        return phase;
    }

    /**
     * Propagate the layer order down to the inner render list after providing modders an opportunity to alter the list as they wish.
     * Must be called at some point for layers to be rendered and ought to be (but does not have to be) called before
     * calling {@link ForgeLayeredDraw#asLayer()}
     */
    public ForgeLayeredDraw computeOrder() {
        if (mayEdit) ForgeEventFactoryClient.onComputeLayerOrder(this);
        if (!expected.isEmpty()) LogUtils.getLogger().warn("Found {} unbound layer names when computing layer order during phase {}.", expected.size(), phase);
        for (ResourceLocation resourceLocation : order) {
            super.add(namedLayers.get(resourceLocation));
        }
        finalized = true;
        return this;
    }

    private void warn(ResourceLocation layer) {
        LogUtils.getLogger().warn("Layer {} is not present in overlay phase {}", layer, phase);
    }
}
