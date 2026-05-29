/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.gui.overlay;

import com.mojang.logging.LogUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraftsingularity.client.event.singularityEventFactoryClient;
import net.minecraftsingularity.client.event.AddGuiOverlayLayersEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.*;
import java.util.function.BooleanSupplier;

/**
 * As vanilla has switched to a layered drawing system for overlays, this system replaces singularityGui and its associated headaches.
 * Vanilla will now have resource locations to represent its render layers which modders can order against.
 * This class is effectively a pseudo-registry for Layers. Add what you need during {@linkplain AddGuiOverlayLayersEvent}
 * Layers must be uniquely named per singularityLayeredDraw, but may be duplicated across different instances.
 * Any change which would result in a duplicate will not be applied.
 * All methods which return a {@linkplain singularityLayeredDraw} will return its caller's instance.
 * To select a specific instance, use {@linkplain singularityLayeredDraw#locateStack(Identifier)} before adding
 * or use the methods that include a stack identifier.
 */
@NullMarked
public final class singularityLayeredDraw implements singularityLayer {
    private final Map<Identifier, singularityLayer> namedLayers = new HashMap<>();
    private final Map<Identifier, Map.Entry<singularityLayeredDraw, BooleanSupplier>> subLayerStacks = new HashMap<>();
    private final List<Identifier> order = new LinkedList<>();
    private final List<singularityLayer> bakedLayers = new ArrayList<>();
    private final Identifier name;

    public static final Identifier  PRE_SLEEP_STACK = Identifier.withDefaultNamespace("pre_sleep_phase");
    public static final Identifier   CAMERA_OVERLAY = Identifier.withDefaultNamespace("camera_overlay");
    public static final Identifier        CROSSHAIR = Identifier.withDefaultNamespace("crosshair");
    public static final Identifier   CHANGE_STRATUM = Identifier.withDefaultNamespace("stratum_change");
    public static final Identifier HOTBAR_AND_DECOS = Identifier.withDefaultNamespace("hotbar");
    public static final Identifier   POTION_EFFECTS = Identifier.withDefaultNamespace("potion_effects");
    public static final Identifier     BOSS_OVERLAY = Identifier.withDefaultNamespace("boss_overlay");

    public static final Identifier POST_SLEEP_STACK = Identifier.withDefaultNamespace("post_sleep_phase");
    public static final Identifier     DEMO_OVERLAY = Identifier.withDefaultNamespace("demo");
    public static final Identifier    DEBUG_OVERLAY = Identifier.withDefaultNamespace("debug");
    public static final Identifier       SCOREBOARD = Identifier.withDefaultNamespace("scoreboard");
    public static final Identifier   HOTBAR_MESSAGE = Identifier.withDefaultNamespace("hotbar_message");
    public static final Identifier    TITLE_OVERLAY = Identifier.withDefaultNamespace("title");
    public static final Identifier     CHAT_OVERLAY = Identifier.withDefaultNamespace("chat_overlay");
    public static final Identifier         TAB_LIST = Identifier.withDefaultNamespace("tab_list");
    public static final Identifier SUBTITLE_OVERLAY = Identifier.withDefaultNamespace("subtitle");

    public static final Identifier     VANILLA_ROOT = Identifier.withDefaultNamespace("vanilla_root");
    public static final Identifier    SLEEP_OVERLAY = Identifier.withDefaultNamespace("sleep_overlay");

    private static final singularityLayeredDraw instance = new singularityLayeredDraw(VANILLA_ROOT);

    /**
     * Creates an empty draw list. Add entries with {@linkplain singularityLayeredDraw#add(Identifier, singularityLayer)}
     * @param name marker for which phase this is.
     */
    public singularityLayeredDraw(Identifier name) {
        this.name = name;
    }

    /**
     * Adds a full draw stack with its provided condition.
     * Use {@linkplain singularityLayeredDraw#putAbove} and {@linkplain singularityLayeredDraw#putBelow} for fine location adjustment.
     * @param name RL of the name to identify this stack with.
     * @param layeredDraw the draw stack
     * @param supplier condition for this stack to render
     * @return this
     */
    public singularityLayeredDraw add(Identifier name, singularityLayeredDraw layeredDraw, BooleanSupplier supplier) {
        if (isNameAvailable(name)) {
            subLayerStacks.put(name, Map.entry(layeredDraw, supplier));
            order.add(name);
        } else {
            nameTakenWarning(name);
        }
        return this;
    }

    /**
     * Add a layer to the layer list. This layer will be at the end of the list, which means
     * it will be rendered last (on top) of already added layers.
     * @param name RL for other mods to order against.
     * @param layer layer render code, see {@linkplain singularityLayer} and example usages in {@linkplain Gui}
     * @return this
     */
    public singularityLayeredDraw add(Identifier targetStack, Identifier name, singularityLayer layer) {
        locateStack(targetStack).ifPresentOrElse((stack) -> stack.add(name, layer), () -> stackNotPresentWarning(targetStack));
        return this;
    }

    /**
     * Helper method, assumes the intended draw stack instance is the caller.
     * Use any of the non-deprecated add methods if you want a specific instance,
     * or call {@linkplain singularityLayeredDraw#locateStack(Identifier)} to get a reference to an instance.
     * @param name RL of layer to add
     * @param layer layer render code, see {@linkplain singularityLayer} and example usages in {@linkplain Gui}
     * @return this
     */
    public singularityLayeredDraw add(Identifier name, singularityLayer layer) {
        if (isNameAvailable(name)) {
            namedLayers.put(name, layer);
            order.add(name);
        } else {
            nameTakenWarning(name);
        }
        return this;
    }

    /**
     * Use to specify where your custom draw stack should go. Can also be used to re-order layers.
     * Both target and destination must be in the same draw stack.
     * @param expectedStack Which singularityLayeredDraw the target is expected to be in. If you already have a reference to it, you can use {@linkplain singularityLayeredDraw#move(Identifier, Identifier, LayerOffset)}
     * @param target layer name to move
     * @param destination layer name to order against
     * @return this
     */
    public singularityLayeredDraw putAbove(Identifier expectedStack, Identifier target, Identifier destination) {
        locateStack(expectedStack).ifPresentOrElse((stack) -> stack.move(target, destination, LayerOffset.ABOVE), () -> stackNotPresentWarning(expectedStack));
        return this;
    }

    /**
     * Use to specify where your custom draw stack should go. Can also be used to re-order layers.
     * Both target and destination must be in the same draw stack.
     * @param expectedStack Which singularityLayeredDraw the target is expected to be in. If you already have a reference to it, you can use {@linkplain singularityLayeredDraw#move(Identifier, Identifier, LayerOffset)}
     * @param target layer name to move
     * @param destination layer name to order against
     * @return this
     */
    public singularityLayeredDraw putBelow(Identifier expectedStack, Identifier target, Identifier destination) {
        locateStack(expectedStack).ifPresentOrElse((stack) -> stack.move(target, destination, LayerOffset.BELOW), () -> stackNotPresentWarning(expectedStack));
        return this;
    }

    /**
     * Helper method, assumes the intended draw stack instance is the caller.
     * Moves pre-existing names around within the order.
     * @param target Layer being moved.
     * @param destination Layer being ordered against
     * @param offset Self-explanatory
     * @return this
     */
    public singularityLayeredDraw move(Identifier target, Identifier destination, LayerOffset offset) {
        if (!order.contains(target)) {
            layerNotPresentWarning(target);
            return this;
        }
        int loc = order.indexOf(destination);
        if (loc == -1) {
            layerNotPresentWarning(destination);
            return this;
        }
        order.remove(target);
        order.add(loc + (offset == LayerOffset.ABOVE ? 1 : 0), target);
        return this;
    }

    /**
     * Adds an overlay layer to be rendered above the other provided layer.
     * To render "above" another layer means thisLayer will be rendered after otherLayer
     * If the current stack does not contain otherLayer, no changes will be made.
     * @param newLayer name of the layer to be added
     * @param otherLayer name of the layer being ordered against
     * @param layer layer render code, see {@linkplain singularityLayer} and example usages in {@linkplain Gui}
     * @return this
     */
    public singularityLayeredDraw addAbove(Identifier expectedStack, Identifier newLayer, Identifier otherLayer, singularityLayer layer) {
        locateStack(expectedStack).ifPresentOrElse((stack) -> {
            if (!stack.isNameAvailable(otherLayer)) {
                stack.add(newLayer, layer).move(newLayer, otherLayer, LayerOffset.ABOVE);
            } else {
                layerNotPresentWarning(otherLayer);
            }
        }, () -> stackNotPresentWarning(expectedStack));
        return this;
    }

    /**
     * Helper method, assumes intended draw stack is the caller
     */
    public singularityLayeredDraw addAbove(Identifier newLayer, Identifier otherLayer, singularityLayer layer) {
        return addAbove(name, newLayer, otherLayer, layer);
    }

    /**
     * Adds an overlay layer to be rendered below the other provided layer.
     * To render "below" another layer means thisLayer will be rendered before otherLayer
     * If the current stack does not contain otherLayer, no changes will be made.
     * @param newLayer name of the layer to be added
     * @param otherLayer name of the layer being ordered against
     * @param layer layer render code, see {@linkplain singularityLayer} and example usages in {@linkplain Gui}
     * @return this
     */
    public singularityLayeredDraw addBelow(Identifier expectedStack, Identifier newLayer, Identifier otherLayer, singularityLayer layer) {
        locateStack(expectedStack).ifPresentOrElse((stack) -> {
            if (!stack.isNameAvailable(otherLayer)) {
                stack.add(newLayer, layer).move(newLayer, otherLayer, LayerOffset.BELOW);
            } else {
                layerNotPresentWarning(otherLayer);
            }
        }, () -> stackNotPresentWarning(expectedStack));
        return this;
    }

    /**
     * Helper method, assumes intended draw stack is the caller
     */
    public singularityLayeredDraw addBelow(Identifier newLayer, Identifier otherLayer, singularityLayer layer) {
        return addBelow(name, newLayer, otherLayer, layer);
    }

    /**
     * Add a new layer that will only be rendered when the condition is met. The layer will be added
     * at the end of the list, which means it will render last (on top) of already added layers
     * @param name name of the layer to be added
     * @param layer render code of layer being added
     * @param condition supplier for the condition
     * @return this
     */
    public singularityLayeredDraw addWithCondition(Identifier targetStack, Identifier name, singularityLayer layer, BooleanSupplier condition) {
        locateStack(targetStack).ifPresentOrElse((stack) -> stack.addWithCondition(name, layer, condition), () -> stackNotPresentWarning(targetStack));
        return this;
    }

    /**
     * Assumes the correct stack is the caller of this method, otherwise functions the same as other method.
     * @param name name of layer to be added
     * @param layer render code of layer being added
     * @param condition supplier for the condition
     * @return this
     */
    public singularityLayeredDraw addWithCondition(Identifier name, singularityLayer layer, BooleanSupplier condition) {
        add(name, layer).addConditionTo(name, condition);
        return this;
    }

    /**
     * Add a condition to a pre-existing layer in the specified stack, its render order is not changed.
     * If the target is not present, no changes are made.
     * @param targetStack name of the draw stack the target is expected to be in
     * @param targetLayer name of layer to add a condition to
     * @param condition supplier for the condition
     * @return this
     */
    public singularityLayeredDraw addConditionTo(Identifier targetStack, Identifier targetLayer, BooleanSupplier condition) {
        locateStack(targetStack).ifPresentOrElse((stack) -> stack.addConditionTo(targetLayer, condition), () -> stackNotPresentWarning(targetStack));
        return this;
    }

    /**
     * Assumes the correct stack is the caller of this method.
     * Use {@linkplain singularityLayeredDraw#addConditionTo(Identifier, Identifier, BooleanSupplier)}
     * if you do not have a reference to the draw stack you want.
     * @param targetLayer name of layer to add condition to
     * @param condition supplier for the condition
     * @return this
     */
    public singularityLayeredDraw addConditionTo(Identifier targetLayer, BooleanSupplier condition) {
        var result = namedLayers.computeIfPresent(targetLayer,
                (name, layer) -> (guiGraphics, deltaTracker) -> {
                    if (condition.getAsBoolean()) {
                        layer.extract(guiGraphics, deltaTracker);
                    }
                });
        if (result == null) {
            layerNotPresentWarning(targetLayer);
        }
        return this;
    }

    /**
     * Propagate the layer order down to the inner render list after providing modders an opportunity to alter the list as they wish.
     * @apiNote Modders should <emph>NEVER</emph> be calling this method.
     * @return this
     */
    @ApiStatus.Internal
    public singularityLayeredDraw resolveLayers() {
        if (!order.isEmpty()) {
            singularityEventFactoryClient.onComputeLayerOrder(this);
        }
        resolveNested();
        order.clear();
        return this;
    }

    /**
     * Resolve the layer order per stack, recursively as needed.
     * Parent layer stack {@linkplain singularityLayeredDraw#VANILLA_ROOT} holds the results.
     */
    private void resolveNested() {
        for (Identifier layerName : order) {
            if (subLayerStacks.containsKey(layerName)) {
                var entry = subLayerStacks.get(layerName);
                entry.getKey().resolveNested();
                bakedLayers.add((gg, tr) -> {
                    if (entry.getValue().getAsBoolean()) entry.getKey().extract(gg,tr);
                });
            } else {
                bakedLayers.add(namedLayers.get(layerName));
            }
        }
    }

    /**
     * Attempt to locate a particular draw stack. Search starts at caller's instance. For global search call on VANILLA_ROOT
     * Entries which don't extend singularityLayeredDraw may not have the proper fields to support internal list adjustment, so they are skipped.
     * @param targetStack Name of singularityLayeredDraw to find
     * @return Filled Optional if target exists, empty otherwise.
     */
    public Optional<singularityLayeredDraw> locateStack(Identifier targetStack) {
        if (!name.equals(targetStack)) {
            for (Map.Entry<singularityLayeredDraw, BooleanSupplier> value : subLayerStacks.values()) {
                if (value.getKey() instanceof singularityLayeredDraw searchable) {
                    var res = searchable.locateStack(targetStack);
                    if(res.isPresent()) return res;
                }
            }
            return Optional.empty();
        } else {
            return Optional.of(this);
        }
    }

    @Nullable
    public singularityLayeredDraw getChild(Identifier childName) {
        return locateStack(childName).orElse(null);
    }

    @Nullable
    public singularityLayer getLayer(Identifier layerName) {
        return namedLayers.get(layerName);
    }

    public Identifier getName() {
        return name;
    }

    private void stackNotPresentWarning(Identifier stackName) {
        LogUtils.getLogger().warn("Target stack {} was not present anywhere. Is your Identifier correct?", stackName);
    }

    private void layerNotPresentWarning(Identifier layer) {
        LogUtils.getLogger().warn("Expected layer {} was not found in stack {}, no layer modifications have been made.", layer, name);
    }

    private void nameTakenWarning(Identifier layer) {
        LogUtils.getLogger().warn("Name {} was already present in {} and cannot be re-used.", layer, name);
    }

    private boolean isNameAvailable(Identifier name) {
        return !namedLayers.containsKey(name) && !subLayerStacks.containsKey(name);
    }

    public enum LayerOffset {
        ABOVE,
        BELOW
    }

    @ApiStatus.Internal
    public static void extractRenderState(GuiGraphicsExtractor gg, DeltaTracker dt) {
        instance.extract(gg, dt);
    }

    @ApiStatus.Internal
    public void extract(GuiGraphicsExtractor gg, DeltaTracker dt) {
        for (singularityLayer bakedLayer : bakedLayers) {
            bakedLayer.extract(gg, dt);
        }
    }

    @ApiStatus.Internal
    public static void init(Gui gui, Minecraft minecraft) {
        var preSleepDraw = new singularityLayeredDraw(PRE_SLEEP_STACK)
            .add(CAMERA_OVERLAY, gui::extractCameraOverlays)
            .add(CROSSHAIR, gui::extractCrosshair)
            .add(CHANGE_STRATUM, (gg, dt) -> gg.nextStratum())
            .add(HOTBAR_AND_DECOS, gui::extractHotbarAndDecorations)
            .add(POTION_EFFECTS, gui::extractEffects)
            .add(BOSS_OVERLAY, gui::extractBossOverlay);
        var postSleepDraw = new singularityLayeredDraw(POST_SLEEP_STACK)
            .add(DEMO_OVERLAY, gui::extractDemoOverlay)
            .add(SCOREBOARD, gui::extractScoreboardSidebar)
            .add(HOTBAR_MESSAGE, gui::extractOverlayMessage)
            .add(TITLE_OVERLAY, gui::extractTitle)
            .add(CHAT_OVERLAY, gui::extractChat)
            .add(TAB_LIST, gui::extractTabList)
            .add(SUBTITLE_OVERLAY, (gfx, delta) -> gui.extractSubtitleOverlay(gfx, minecraft.screen != null && minecraft.screen.isInGameUi()));
        instance
            .add(PRE_SLEEP_STACK, preSleepDraw, () -> !minecraft.options.hideGui)
            .add(SLEEP_OVERLAY, gui::extractSleepOverlay)
            .add(POST_SLEEP_STACK, postSleepDraw, () -> !minecraft.options.hideGui)
            .add(SUBTITLE_OVERLAY, (gfx, delta) -> {
                if (minecraft.options.hideGui && minecraft.screen != null && minecraft.screen.isInGameUi())
                    gui.extractSubtitleOverlay(gfx,  true);
            });
        instance.resolveLayers();
    }
}
