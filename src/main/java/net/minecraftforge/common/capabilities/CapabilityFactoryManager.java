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
        event.factory_holder.forEach((k, m) -> {
            MANAGER.factory_holder.put(k, ImmutableMap.copyOf(m));
        });
    }

    static CapabilityFactoryManager getInstance() {
        return MANAGER;
    }

    private final Map<Class<?>, Map<ResourceLocation, ICapabilityFactory<?, ?>>> factory = new HashMap<>();
    private final Map<CapabilityFactoryHolder<?>, Map<ResourceLocation, ICapabilityFactory<?, ?>>> factory_holder = new HashMap<>();

    private CapabilityFactoryManager() {}

    private <G> void add(Map<ResourceLocation, ICapabilityFactory<G, ?>> factories, Class<?> clz) {
        if (clz == Object.class) return;
        var map = factory.get(clz);
        if (map != null) factories.putAll(cast(map));
        add(factories, clz.getSuperclass());
    }

    private <G> G cast(Object o) {
        return (G) o;
    }

    <G> Map<ResourceLocation, ICapabilityFactory<G, ?>> build(Class<G> obj, CapabilityFactoryHolder<G> holder) {
        Map<ResourceLocation, ICapabilityFactory<G, ?>> factories = new HashMap<>(cast(factory_holder.getOrDefault(holder, Map.of())));
        var map = factory.get(obj);
        if (map != null)
            map.forEach((k, v) -> {
                factories.put(k, cast(v));
            });
        add(factories, obj.getSuperclass());
        return factories;
    }

}
