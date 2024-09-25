package net.minecraftforge.common.capabilities;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class CapabilityFactoryManager {
    private static final CapabilityFactoryManager MANAGER = new CapabilityFactoryManager();

    public static CapabilityFactoryManager getInstance() {
        return MANAGER;
    }

    private final Map<Class<?>, Map<ResourceLocation, ICapabilityFactory<?>>> factory = new HashMap<>();

    private CapabilityFactoryManager() {}

    public <G> void register(Class<G> gClass, ResourceLocation resourceLocation, ICapabilityFactory<G> factory) {
        this.factory.computeIfAbsent(gClass, k -> new HashMap<>()).put(resourceLocation, factory);
    }

    private <G> void add(Map<ResourceLocation, ICapabilityFactory<G>> factories, Class<?> clz) {
        if (clz == Object.class) return;
        factories.putAll(cast(factory.getOrDefault(clz, Map.of())));
        add(factories, clz.getSuperclass());
    }

    protected <G> Map<ResourceLocation, ICapabilityFactory<G>> build(Class<G> obj) {
        Map<ResourceLocation, ICapabilityFactory<G>> factories = new HashMap<>(cast(factory.getOrDefault(obj, Map.of())));
        add(factories, obj.getSuperclass());
        return factories;
    }

    private <T> T cast(Object o) {
        return (T) o;
    }
}
