package net.minecraftforge.fml.util;

import net.minecraftforge.eventbus.api.IEventBus;

import java.util.function.Supplier;

public interface IEventBusSupplier extends Supplier<IEventBus> {
    IEventBus getEventBus();

    default IEventBus get() {
        return getEventBus();
    }
}
