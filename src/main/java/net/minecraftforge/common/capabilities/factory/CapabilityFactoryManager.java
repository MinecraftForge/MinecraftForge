package net.minecraftforge.common.capabilities.factory;

import net.minecraft.core.Direction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.RegisterCapabilityFactoriesEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class CapabilityFactoryManager<I, T> implements ICapabilityFactoryManager<I> {
    Map<Key, Instance<I>> factories = new HashMap<>();
    private Map<Function<I, LazyOptional<?>>, Instance<I>> instances = new HashMap<>();

    public CapabilityFactoryManager(T instance, Class<? extends T> iClass) {
        MinecraftForge.EVENT_BUS.post(new RegisterCapabilityFactoriesEvent<I, T>(instance, iClass, (f, cap, dirs) -> {
            register(f, cap, dirs.toArray(new Direction[0]));
        }));
    }

    private void register(Function<I, LazyOptional<?>> func, Capability<?> cap) {
        this.register(func, cap, new Direction[]{null});
    }

    private void register(Function<I, LazyOptional<?>> func, Capability<?> cap, Direction... dirs) {
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

    public ICapabilityProvider getWrapper(I instance) {
        return new CapabilityFactoryProvider<>(this, instance);
    }

    private record Key(Capability<?> cap, Direction side) {}
    private record Exposed(Capability<?> cap, Direction[] dirs) {}
    private record Instance<I>(Function<I, LazyOptional<?>> func, List<Exposed> exposed) {}

    private final static class CapabilityFactoryProvider<I> implements ICapabilityProvider {
        private final CapabilityFactoryManager<I, ?> manager;
        private final Map<CapabilityFactoryManager.Key, LazyOptional<?>> caps = new HashMap<>();
        private final I instance;

        CapabilityFactoryProvider(CapabilityFactoryManager<I, ?> manager, I instance) {
            this.manager = manager;
            this.instance = instance;
        }

        public LazyOptional<?> getCap(Capability<?> cap, Direction dir) {
            var key = new CapabilityFactoryManager.Key(cap, dir);
            var ret = caps.get(key);
            if (ret == null)
                ret = create(key);
            return ret;
        }

        private LazyOptional<?> create(CapabilityFactoryManager.Key key) {
            LazyOptional<?> ret = null;
            var inst = manager.factories.get(key);
            if (inst != null)
                ret = init(inst);
            else if (key.side() != null)
                ret = create(new CapabilityFactoryManager.Key(key.cap(), null));

            if (ret == null)
                ret = LazyOptional.empty();

            caps.put(key, ret);

            return ret;
        }

        private LazyOptional<?> init(CapabilityFactoryManager.Instance<I> inst) {
            var ret = inst.func().apply(instance);
            for (var exp : inst.exposed()) {
                for (var dir : exp.dirs())
                    caps.put(new CapabilityFactoryManager.Key(exp.cap(), dir), ret);
            }
            return ret;
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return getCap(cap, side).cast();
        }
    }
}