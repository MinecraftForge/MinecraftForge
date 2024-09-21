package net.minecraftforge.common.capabilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

import java.util.HashMap;
import java.util.Map;

public class RegisterCapabilityFactoryEvent extends Event {
    private final CapabilityFactoryHolder<?> holder;
    private final Map<ResourceLocation, ICapabilityFactory<?>> FACTORIES = new HashMap<>();

    public Object getObject() {
        return holder;
    }

    public RegisterCapabilityFactoryEvent(CapabilityFactoryHolder<?> holder) {
        this.holder = holder;
    }

    public void register(ResourceLocation resourceLocation, ICapabilityFactory<?> factory) {
        FACTORIES.put(resourceLocation, factory);
    }

    protected Map<ResourceLocation, ICapabilityFactory<?>> getFactories() {
        return FACTORIES;
    }
}
