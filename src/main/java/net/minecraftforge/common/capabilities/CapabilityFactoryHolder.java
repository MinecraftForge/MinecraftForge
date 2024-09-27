package net.minecraftforge.common.capabilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.HashMap;
import java.util.Map;

public abstract class CapabilityFactoryHolder<T> {
    private final Map<ResourceLocation, ICapabilityFactory<T>> factories = new HashMap<>();
    private boolean built = false;

    protected <G> void build(G obj) {
        if (built) return;
        built = true;

        CapabilityFactoryManager.getInstance().build(cast(obj.getClass()), this).forEach((rl, f) -> {
            factories.put(rl, cast(f));
        });
    }


    private <G> G cast(Object o) {
        return (G) o;
    }

    public Map<ResourceLocation, ICapabilityProvider> getCaps(T obj) {
        Map<ResourceLocation, ICapabilityProvider> providerMap = new HashMap<>();
        factories.forEach((rl, f) -> {
            providerMap.put(rl, f.create(obj));
        });
        return providerMap;
    }

    public static class AsField<T> extends CapabilityFactoryHolder<T> {}
}
