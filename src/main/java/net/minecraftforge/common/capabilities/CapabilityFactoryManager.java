package net.minecraftforge.common.capabilities;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class CapabilityFactoryManager {
    private static final CapabilityFactoryManager MANAGER = new CapabilityFactoryManager();

    public static CapabilityFactoryManager getInstance() {
        return MANAGER;
    }

    private final Map<Class<?>, Map<ResourceLocation, ICapabilityFactory<?>>> FACTORY = new HashMap<>();

    private CapabilityFactoryManager() {}

    public <G> void register(Class<G> gClass, ResourceLocation resourceLocation, ICapabilityFactory<G> factory) {
        FACTORY.computeIfAbsent(gClass, k -> new HashMap<>()).put(resourceLocation, factory);
    }

    private <G> void add(Map<ResourceLocation, ICapabilityFactory<G>> FACTORIES, Class<?> clz) {
        if (clz == Object.class) return;
        FACTORIES.putAll(cast(FACTORY.getOrDefault(clz, Map.of())));
        add(FACTORIES, clz.getSuperclass());
    }

    protected <G> Map<ResourceLocation, ICapabilityFactory<G>> build(Class<G> obj) {
        Map<ResourceLocation, ICapabilityFactory<G>> FACTORIES = new HashMap<>(cast(FACTORY.getOrDefault(obj, Map.of())));
        add(FACTORIES, obj.getSuperclass());
        return FACTORIES;
    }

    private <T> T cast(Object o) {
        return (T) o;
    }
}
