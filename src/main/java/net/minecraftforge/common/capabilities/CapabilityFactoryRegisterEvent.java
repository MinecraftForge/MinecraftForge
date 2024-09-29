package net.minecraftforge.common.capabilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

import java.util.HashMap;
import java.util.Map;

public class CapabilityFactoryRegisterEvent extends Event {
    final Map<Class<?>, Map<ResourceLocation, ICapabilityFactory<?, ?>>> factory = new HashMap<>();
    final Map<CapabilityFactoryHolder<?>, Map<ResourceLocation, ICapabilityFactory<?, ?>>> factory_holder = new HashMap<>();

    public CapabilityFactoryRegisterEvent() {}

    public <G> void register(Class<G> gClass, ResourceLocation resourceLocation, ICapabilityFactory<G, ?> factory) {
        if (this.factory.computeIfAbsent(gClass, k -> new HashMap<>()).put(resourceLocation, factory) != null) {
            throw new IllegalStateException("Duplicate CapabilityFactory registered for class %s with id of %s".formatted(gClass, resourceLocation));
        }
    }

    public <G> void register(CapabilityFactoryHolder<G> holder, ResourceLocation resourceLocation, ICapabilityFactory<G, ?> factory) {
        if (this.factory_holder.computeIfAbsent(holder, k -> new HashMap<>()).put(resourceLocation, factory) != null) {
            throw new IllegalStateException("Duplicate CapabilityFactory registered for holder %s with id of %s".formatted(holder, resourceLocation));
        }
    }

}
