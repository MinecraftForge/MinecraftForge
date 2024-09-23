package net.minecraftforge.common.capabilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.GenericEvent;

import java.util.HashMap;
import java.util.Map;

public class RegisterCapabilityFactoryEvent<G> extends GenericEvent<G> {
    private final CapabilityFactoryHolder<?> holder;
    private final Map<ResourceLocation, ICapabilityFactory<?>> FACTORIES = new HashMap<>();
    private final G obj;

    public RegisterCapabilityFactoryEvent(CapabilityFactoryHolder<?> holder, G obj) {
        super((Class<G>) obj.getClass());
        this.holder = holder;
        this.obj = obj;
    }

    public G getObject() {
        return obj;
    }

    public void register(ResourceLocation resourceLocation, ICapabilityFactory<G> factory) {
        FACTORIES.put(resourceLocation, factory);
    }

    protected Map<ResourceLocation, ICapabilityFactory<?>> getFactories() {
        return FACTORIES;
    }
}
