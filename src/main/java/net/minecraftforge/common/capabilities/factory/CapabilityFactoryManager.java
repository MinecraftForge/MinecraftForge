package net.minecraftforge.common.capabilities.factory;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class CapabilityFactoryManager<I> implements ICapabilityFactoryManager<I> {
    Map<Key, Instance<I>> factories = new HashMap<>();
    private Map<Function<I, LazyOptional<?>>, Instance<I>> instances = new HashMap<>();

    public void register(Function<I, LazyOptional<?>> func, Capability<?> cap) {
        this.register(func, cap, new Direction[]{null});
    }

    public void register(Function<I, LazyOptional<?>> func, Capability cap, Direction... dirs) {
        var old = instances.get(func);
        Instance<I> inst;
        if (old == null)
            inst = new Instance(func, List.of(new Exposed(cap, dirs)));
        else
            inst = new Instance(func, Stream.concat(old.exposed.stream(), Stream.of(new Exposed(cap, dirs))).toList());

        instances.put(func, inst);

        for (var exp : inst.exposed()) {
            for (var dir : exp.dirs())
                factories.put(new Key(exp.cap(), dir), inst);
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side, I instance) {
        return null;
    }

    public record Key(Capability<?> cap, Direction side) {}
    public record Exposed(Capability<?> cap, Direction[] dirs) {}
    public record Instance<I>(Function<I, LazyOptional<?>> func, List<Exposed> exposed) {}
}