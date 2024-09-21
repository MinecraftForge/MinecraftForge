package net.minecraftforge.common.capabilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;
import java.util.Map;

public class CapabilityFactoryHolder<T> {
    private Map<ResourceLocation, ICapabilityFactory<T>> FACTORIES = new HashMap<>();
    private boolean built = false;

    protected void build() {
        if (built) return;
        built = true;
        var event = new RegisterCapabilityFactoryEvent(this);
        MinecraftForge.EVENT_BUS.post(event);
        event.getFactories().forEach((rl, f) -> {
            FACTORIES.put(rl, cast(f));
        });
    }


    private <G> G cast(Object o) {
        return (G) o;
    }

    public Map<ResourceLocation, ICapabilityProvider> getCaps(T obj) {
        Map<ResourceLocation, ICapabilityProvider> providerMap = new HashMap<>();
        FACTORIES.forEach((rl, f) -> {
            providerMap.put(rl, f.create(obj));
        });
        return providerMap;
    }
}
