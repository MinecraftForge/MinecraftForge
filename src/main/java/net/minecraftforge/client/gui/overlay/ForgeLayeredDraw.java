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
 * Layers must be uniquely named per ForgeLayeredDraw, but may be duplicated across different instances.
 * Any change which would result in a duplicate will not be applied.
 * All methods which return a {@linkplain ForgeLayeredDraw} will return its caller's instance.
 * To select a specific instance, use {@linkplain ForgeLayeredDraw#locateStack(ResourceLocation)} before adding
 * or use the methods that include a stack identifier.
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
    private final ResourceLocation name;

    public static final ResourceLocation  PRE_SLEEP_STACK = ResourceLocation.withDefaultNamespace("pre_sleep_phase");
    public static final ResourceLocation   CAMERA_OVERLAY = ResourceLocation.withDefaultNamespace("camera_overlay");
    public static final ResourceLocation        CROSSHAIR = ResourceLocation.withDefaultNamespace("crosshair");
    public static final ResourceLocation           HOTBAR = ResourceLocation.withDefaultNamespace("hotbar");
    public static final ResourceLocation       EXPERIENCE = ResourceLocation.withDefaultNamespace("experience");
    public static final ResourceLocation   POTION_EFFECTS = ResourceLocation.withDefaultNamespace("potion_effects");
    public static final ResourceLocation     BOSS_OVERLAY = ResourceLocation.withDefaultNamespace("boss_overlay");

    public static final ResourceLocation POST_SLEEP_STACK = ResourceLocation.withDefaultNamespace("post_sleep_phase");
    public static final ResourceLocation     DEMO_OVERLAY = ResourceLocation.withDefaultNamespace("demo");
    public static final ResourceLocation    DEBUG_OVERLAY = ResourceLocation.withDefaultNamespace("debug");
    public static final ResourceLocation       SCOREBOARD = ResourceLocation.withDefaultNamespace("scoreboard");
    public static final ResourceLocation   HOTBAR_MESSAGE = ResourceLocation.withDefaultNamespace("hotbar_message");
    public static final ResourceLocation    TITLE_OVERLAY = ResourceLocation.withDefaultNamespace("title");
    public static final ResourceLocation     CHAT_OVERLAY = ResourceLocation.withDefaultNamespace("chat_overlay");
    public static final ResourceLocation         TAB_LIST = ResourceLocation.withDefaultNamespace("tab_list");
    public static final ResourceLocation SUBTITLE_OVERLAY = ResourceLocation.withDefaultNamespace("subtitle");

    public static final ResourceLocation     VANILLA_ROOT = ResourceLocation.withDefaultNamespace("vanilla_root");
    public static final ResourceLocation    SLEEP_OVERLAY = ResourceLocation.withDefaultNamespace("sleep_overlay");


    public static final ImmutableList<ResourceLocation> PRE_LIST = ImmutableList.of(CAMERA_OVERLAY, CROSSHAIR, HOTBAR, EXPERIENCE, POTION_EFFECTS, BOSS_OVERLAY);
    public static final ImmutableList<ResourceLocation> POST_LIST = ImmutableList.of(DEMO_OVERLAY, DEBUG_OVERLAY, SCOREBOARD, HOTBAR_MESSAGE, TITLE_OVERLAY, CHAT_OVERLAY, TAB_LIST, SUBTITLE_OVERLAY);
    public static final ImmutableList<ResourceLocation> COMBINE_LIST = ImmutableList.of(PRE_SLEEP_STACK, SLEEP_OVERLAY, POST_SLEEP_STACK);

    /**
     * Creates a stack of pre-named layers for vanilla.
     * Not intended for modder use. Use {@linkplain ForgeLayeredDraw(ResourceLocation, Boolean, String...)} instead.
     * @param name Stack identifier
     * @param layers locations of layers to add
     */
    @ApiStatus.Internal
    public ForgeLayeredDraw(ResourceLocation name, List<ResourceLocation> layers) {
        this.name = name;
        expectedNames.addAll(layers);
    }

    /**
     * Creates an empty draw list. Add entries with {@linkplain ForgeLayeredDraw#add(ResourceLocation, Layer)}
     * @param name marker for which phase this is.
     */
    public ForgeLayeredDraw(ResourceLocation name) {
        this.name = name;
    }

    /**
     * Adds a full, pre-named draw stack.
     * @param layeredDraw layer stack to be added.
     * @param booleanSupplier requirement for it to render.
     * @deprecated Modders should be using {@linkplain ForgeLayeredDraw#add(ResourceLocation, LayeredDraw, BooleanSupplier)}
     * @return this
     */
    @Deprecated
    @Override
    public ForgeLayeredDraw add(LayeredDraw layeredDraw, BooleanSupplier booleanSupplier) {
        return add(getNextPreName(), layeredDraw, booleanSupplier);
    }

    /**
     * Adds a full draw stack with its provided condition.
     * Use {@linkplain ForgeLayeredDraw#putAbove} and {@linkplain ForgeLayeredDraw#putBelow} for fine location adjustment.
     * @param name RL of the name to identify this stack with.
     * @param layeredDraw the draw stack
     * @param supplier condition for this stack to render
     * @return this
     */
    public ForgeLayeredDraw add(ResourceLocation name, LayeredDraw layeredDraw, BooleanSupplier supplier) {
        if (isNameAvailable(name)) {
            subLayerStacks.put(name, Map.entry(layeredDraw, supplier));
            order.add(name);
        } else {
            nameTakenWarning(name);
        }
        return this;
    }

    /**
     * Adds a layer with an already known name provided by {@linkplain ForgeLayeredDraw(ResourceLocation, List)}
     * The layer will be rendered last (on top) of already added layers.
     * @param layer layer render code, see {@linkplain Layer} and example usages in {@linkplain Gui}
     * @deprecated Use {@linkplain ForgeLayeredDraw#add(ResourceLocation, Layer)} for adding individual layers.
     * @return this
     */
    @Deprecated
    @Override
    public LayeredDraw add(Layer layer) {
        add(getNextPreName(), layer);
        return this;
    }

    /**
     * Add a layer to the layer list. This layer will be at the end of the list, which means
     * it will be rendered last (on top) of already added layers.
     * @param name RL for other mods to order against.
     * @param layer layer render code, see {@linkplain Layer} and example usages in {@linkplain Gui}
     * @return this
     */
    public ForgeLayeredDraw add(ResourceLocation targetStack, ResourceLocation name, Layer layer) {
        locateStack(targetStack).ifPresentOrElse((stack) -> stack.add(name, layer), () -> stackNotPresentWarning(targetStack));
        return this;
    }

    /**
     * Helper method, assumes the intended draw stack instance is the caller.
     * Use any of the non-deprecated add methods if you want a specific instance,
     * or call {@linkplain ForgeLayeredDraw#locateStack(ResourceLocation)} to get a reference to an instance.
     * @param name RL of layer to add
     * @param layer layer render code, see {@linkplain Layer} and example usages in {@linkplain Gui}
     * @return this
     */
    public ForgeLayeredDraw add(ResourceLocation name, Layer layer) {
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
     * @param expectedStack Which ForgeLayeredDraw the target is expected to be in. If you already have a reference to it, you can use {@linkplain ForgeLayeredDraw#move(ResourceLocation, ResourceLocation, LayerOffset)}
     * @param target layer name to move
     * @param destination layer name to order against
     * @return this
     */
    public ForgeLayeredDraw putAbove(ResourceLocation expectedStack, ResourceLocation target, ResourceLocation destination) {
        locateStack(expectedStack).ifPresentOrElse((stack) -> stack.move(target, destination, LayerOffset.ABOVE), () -> stackNotPresentWarning(expectedStack));
        return this;
    }

    /**
     * Use to specify where your custom draw stack should go. Can also be used to re-order layers.
     * Both target and destination must be in the same draw stack.
     * @param expectedStack Which ForgeLayeredDraw the target is expected to be in. If you already have a reference to it, you can use {@linkplain ForgeLayeredDraw#move(ResourceLocation, ResourceLocation, LayerOffset)}
     * @param target layer name to move
     * @param destination layer name to order against
     * @return this
     */
    public ForgeLayeredDraw putBelow(ResourceLocation expectedStack, ResourceLocation target, ResourceLocation destination) {
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
    public ForgeLayeredDraw move(ResourceLocation target, ResourceLocation destination, LayerOffset offset) {
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
     * @param layer layer render code, see {@linkplain Layer} and example usages in {@linkplain Gui}
     * @return this
     */
    public ForgeLayeredDraw addAbove(ResourceLocation expectedStack, ResourceLocation newLayer, ResourceLocation otherLayer, Layer layer) {
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
    public ForgeLayeredDraw addAbove(ResourceLocation newLayer, ResourceLocation otherLayer, Layer layer) {
        return addAbove(name, newLayer, otherLayer, layer);
    }

    /**
     * Adds an overlay layer to be rendered below the other provided layer.
     * To render "below" another layer means thisLayer will be rendered before otherLayer
     * If the current stack does not contain otherLayer, no changes will be made.
     * @param newLayer name of the layer to be added
     * @param otherLayer name of the layer being ordered against
     * @param layer layer render code, see {@linkplain Layer} and example usages in {@linkplain Gui}
     * @return this
     */
    public ForgeLayeredDraw addBelow(ResourceLocation expectedStack, ResourceLocation newLayer, ResourceLocation otherLayer, Layer layer) {
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
    public ForgeLayeredDraw addBelow(ResourceLocation newLayer, ResourceLocation otherLayer, Layer layer) {
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
    public ForgeLayeredDraw addWithCondition(ResourceLocation targetStack, ResourceLocation name, Layer layer, BooleanSupplier condition) {
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
    public ForgeLayeredDraw addWithCondition(ResourceLocation name, Layer layer, BooleanSupplier condition) {
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
    public ForgeLayeredDraw addConditionTo(ResourceLocation targetStack, ResourceLocation targetLayer, BooleanSupplier condition) {
        locateStack(targetStack).ifPresentOrElse((stack) -> stack.addConditionTo(targetLayer, condition), () -> stackNotPresentWarning(targetStack));
        return this;
    }

    /**
     * Assumes the correct stack is the caller of this method.
     * Use {@linkplain ForgeLayeredDraw#addConditionTo(ResourceLocation, ResourceLocation, BooleanSupplier)}
     * if you do not have a reference to the draw stack you want.
     * @param targetLayer name of layer to add condition to
     * @param condition supplier for the condition
     * @return this
     */
    public ForgeLayeredDraw addConditionTo(ResourceLocation targetLayer, BooleanSupplier condition) {
        var result = namedLayers.computeIfPresent(targetLayer,
                (name, layer) -> (guiGraphics, deltaTracker) -> {
                    if (condition.getAsBoolean()) {
                        layer.render(guiGraphics, deltaTracker);
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
     * Resolve the layer order per stack, recursively as needed.
     * Parent layer stack {@linkplain ForgeLayeredDraw#VANILLA_ROOT} holds the results.
     */
    private void resolveNested() {
        for (ResourceLocation layerName : order) {
            if (subLayerStacks.containsKey(layerName)) {
                var entry = subLayerStacks.get(layerName);
                if (entry.getKey() instanceof ForgeLayeredDraw resolveable) {
                    resolveable.resolveNested();
                    // Although unlikely, a LayeredDraw could be in the list, and we can't call resolveNested on one.
                }
                super.add((gg, tr) -> {
                    if (entry.getValue().getAsBoolean()) entry.getKey().render(gg,tr);
                });
            } else {
                super.add(namedLayers.get(layerName));
            }
        }
    }

    /**
     * Attempt to locate a particular draw stack. Search starts at caller's instance. For global search call on VANILLA_ROOT
     * i.e. {@linkplain net.minecraftforge.client.event.AddGuiOverlayLayersEvent#getLayeredDraw()}
     * Entries which don't extend ForgeLayeredDraw may not have the proper fields to support internal list adjustment, so they are skipped.
     * @param targetStack Name of ForgeLayeredDraw to find
     * @return Filled Optional if target exists, empty otherwise.
     */
    public Optional<ForgeLayeredDraw> locateStack(ResourceLocation targetStack) {
        if (!name.equals(targetStack)) {
            for (Map.Entry<LayeredDraw, BooleanSupplier> value : subLayerStacks.values()) {
                if (value.getKey() instanceof ForgeLayeredDraw searchable) {
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
    public LayeredDraw getChild(ResourceLocation childName) {
        return locateStack(childName).orElse(null);
    }

    @Nullable
    public Layer getLayer(ResourceLocation layerName) {
        return namedLayers.get(layerName);
    }

    private ResourceLocation getNextPreName() {
        return expectedNames.isEmpty() ? ResourceLocation.fromNamespaceAndPath("unknown", "layer_" + unknown++) : expectedNames.removeFirst();
    }

    public ResourceLocation getName() {
        return name;
    }

    private void stackNotPresentWarning(ResourceLocation stackName) {
        LogUtils.getLogger().warn("Target stack {} was not present anywhere. Is your ResourceLocation correct?", stackName);
    }

    private void layerNotPresentWarning(ResourceLocation layer) {
        LogUtils.getLogger().warn("Expected layer {} was not found in stack {}, no layer modifications have been made.", layer, name);
    }

    private void nameTakenWarning(ResourceLocation layer) {
        LogUtils.getLogger().warn("Name {} was already present in {} and cannot be re-used.", layer, name);
    }

    private boolean isNameAvailable(ResourceLocation name) {
        return !namedLayers.containsKey(name) && !subLayerStacks.containsKey(name);
    }

    public enum LayerOffset {
        ABOVE,
        BELOW
    }
}
