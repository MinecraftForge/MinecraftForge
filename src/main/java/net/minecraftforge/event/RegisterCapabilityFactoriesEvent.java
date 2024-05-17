package net.minecraftforge.event;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.GenericEvent;
import org.apache.logging.log4j.util.TriConsumer;

import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Function;

public class RegisterCapabilityFactoriesEvent<I, T> extends Event {

    private final TriConsumer<Function<I, LazyOptional<?>>, Capability<?>, List<Direction>> consumer;
    private final T instance;

    public RegisterCapabilityFactoriesEvent(T instance, Class<? extends T> iClass, TriConsumer<Function<I, LazyOptional<?>>, Capability<?>, List<Direction>> consumer) {
        this.instance = instance;
        this.consumer = consumer;
    }

    public void register(Function<I, LazyOptional<?>> func, Capability<?> cap) {
        this.register(func, cap, new Direction[]{null});
    }

    public void register(Function<I, LazyOptional<?>> func, Capability<?> cap, Direction... dirs) {
        consumer.accept(func, cap, List.of(dirs));
    }

    public T getInstance() {
        return instance;
    }
}
