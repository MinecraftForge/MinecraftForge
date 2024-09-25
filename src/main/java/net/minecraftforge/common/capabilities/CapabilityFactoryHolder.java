package net.minecraftforge.common.capabilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;
import java.util.Map;

public class CapabilityFactoryHolder<T> {
    private Map<ResourceLocation, ICapabilityFactory<T>> FACTORIES = new HashMap<>();
    private boolean built = false;

    protected <G> void build(G obj) {
        if (built) return;
        built = true;

        CapabilityFactoryManager.getInstance().build((obj.getClass())).forEach((rl, f) -> {
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
