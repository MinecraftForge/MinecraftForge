package net.minecraftforge.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.ResourceLocation;

public class SkyLayerGroup {
    private Map<ResourceLocation, SkyLayer> layers = new HashMap<>();

    SkyLayerGroup() { }

    /**
     * Gets sub-layer in this layer group.
     * @param id the id for the sub-layer
     * @return the sub-layer with the specified id
     * */
    public SkyLayer subLayer(ResourceLocation id)
    {
        if(!layers.containsKey(id))
            layers.put(id, new SkyLayer(id));
        return layers.get(id);
    }

    /**
     * @return all sub-layers of this layer group
     * */
    public Collection<SkyLayer> subLayers()
    {
        return layers.values();
    }
}
