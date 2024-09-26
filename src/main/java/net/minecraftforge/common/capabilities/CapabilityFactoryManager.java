package net.minecraftforge.common.capabilities;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;
import java.util.Map;

public final class CapabilityFactoryManager {
    private static final CapabilityFactoryManager MANAGER = new CapabilityFactoryManager();
    private static boolean init = false;

    public static void init() {
        if (init) return;
        init = true;

        var event = new CapabilityFactoryRegisterEvent();
        MinecraftForge.EVENT_BUS.post(event);
        event.factory.forEach((k, m) -> {
            MANAGER.factory.put(k, ImmutableMap.copyOf(m));
        });
    }

    static CapabilityFactoryManager getInstance() {
        return MANAGER;
    }

    private final Map<Class<?>, Map<ResourceLocation, ICapabilityFactory<?>>> factory = new HashMap<>();

    private CapabilityFactoryManager() {}

    <G> void register(Class<G> gClass, ResourceLocation resourceLocation, ICapabilityFactory<G> factory) {
        this.factory.computeIfAbsent(gClass, k -> new HashMap<>()).put(resourceLocation, factory);
    }

    private <G> void add(Map<ResourceLocation, ICapabilityFactory<G>> factories, Class<?> clz) {
        if (clz == Object.class) return;
        var map = factory.get(clz);
        if (map != null) factories.putAll(cast(map));
        add(factories, clz.getSuperclass());
    }

    <G> Map<ResourceLocation, ICapabilityFactory<G>> build(Class<G> obj) {
        Map<ResourceLocation, ICapabilityFactory<G>> factories = new HashMap<>(cast(factory.getOrDefault(obj, Map.of())));
        add(factories, obj.getSuperclass());
        return factories;
    }

    private <T> T cast(Object o) {
        return (T) o;
    }
}
