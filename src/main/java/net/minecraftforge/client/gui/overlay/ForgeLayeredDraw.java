package net.minecraftforge.client.gui.overlay;

import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

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
     * @param layers
     */
    public ForgeLayeredDraw(String... layers) {
        for (String vanillaLayerName : layers) {
            expected.push(ResourceLocation.withDefaultNamespace(vanillaLayerName));
        }
    }

    /**
     * Creates a stack of pre-named layers for a given mod.
     * @param modIdProvider
     * @param layers
     */
    public ForgeLayeredDraw(Supplier<String> modIdProvider, String... layers) {
        for (String layer : layers) {
            expected.push(ResourceLocation.fromNamespaceAndPath(modIdProvider.get(), layer));
        }
    }

    @Override
    public LayeredDraw add(Layer layer) {
        return add(expected.isEmpty() ?
                ResourceLocation.fromNamespaceAndPath("unknown", String.valueOf(count++))
                : expected.pop(), layer);
    }

    /**
     * Adds a layer
     * @param name
     * @param layer
     * @return
     */
    public ForgeLayeredDraw add(ResourceLocation name, Layer layer) {
        namedLayers.put(name, layer);
        order.add(name);
        return this;
    }

    public ForgeLayeredDraw addCondition(ResourceLocation target, BooleanSupplier supplier) {
        return null;
    }

    /**
     * Adds an overlay layer to be rendered above the other provided layer.
     * To render "above" another layer means thisLayer will be rendered after otherLayer
     * @param thisLayer layer being added
     * @param otherLayer layer being ordered against
     * @param layer layer render code, see {@link Layer} and example usages in {@link net.minecraft.client.gui.Gui}
     * @return this
     */
    public ForgeLayeredDraw addAbove(ResourceLocation thisLayer, ResourceLocation otherLayer, Layer layer) {
        namedLayers.put(thisLayer, layer);
        int loc = order.indexOf(otherLayer);
        if (loc == -1) LogUtils.getLogger().warn("No such layer {}, skipping {}", otherLayer.toString(), thisLayer);
        else order.add(loc+1, thisLayer);
        return this;
    }

    /**
     * Adds an overlay layer to be rendered below the other provided layer.
     * To render "below" another layer means thisLayer will be rendered before otherLayer
     * @param thisLayer layer being added
     * @param otherLayer layer being ordered against
     * @param layer layer render code, see {@link Layer} and example usages in {@link net.minecraft.client.gui.Gui}
     * @return this
     */
    public LayeredDraw addBelow(ResourceLocation thisLayer, ResourceLocation otherLayer, Layer layer) {
        namedLayers.put(thisLayer, layer);
        int loc = order.indexOf(otherLayer);
        if (loc == -1) LogUtils.getLogger().warn("No such layer {}, skipping {}", otherLayer.toString(), thisLayer);
        else order.add(loc, thisLayer);
        return this;
    }

    public void finish() {
        // MinecraftForge.EVENT_BUS.post(new ModifyOverlayLayersEvent)
        if (!expected.isEmpty()) LogUtils.getLogger().warn("Found {} unbound layer names when constructing gui layer list.", expected.size());
        for (ResourceLocation resourceLocation : order) {
            super.add(namedLayers.get(resourceLocation));
        }
    }

}
