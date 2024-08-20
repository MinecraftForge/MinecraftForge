package net.minecraftforge.fml.common;

import net.minecraftforge.eventbus.api.IEventBus;

public final class FMLJavaModContext {
    private final IEventBus modBus;

    public FMLJavaModContext(IEventBus modBus) {
        this.modBus = modBus;
    }

    public IEventBus getModBus() {
        return modBus;
    }
}
