package net.minecraftforge.common.capabilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.eventbus.api.Event;

public final class CapabilityFactoryRegisterEvent extends Event {
    private final CapabilityFactoryManager MANAGER = CapabilityFactoryManager.getInstance();

    public CapabilityFactoryRegisterEvent() {}

    public <G> void register(Class<G> gClass, ResourceLocation resourceLocation, ICapabilityFactory<G> factory) {
        MANAGER.register(gClass, resourceLocation, factory);
    }
}
