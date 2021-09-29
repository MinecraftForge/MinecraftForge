package net.minecraftforge.fml;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.config.IConfigEvent;

import java.util.function.Supplier;

public interface IBindingsProvider {
    Supplier<IEventBus> getForgeBusSupplier();
    Supplier<I18NParser> getMessageParser();
    Supplier<IConfigEvent.ConfigConfig> getConfigConfiguration();
}
