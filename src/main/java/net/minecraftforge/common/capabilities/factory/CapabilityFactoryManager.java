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

public class CapabilityFactoryManager<I> implements ICapabilityFactoryManager<I> {
    private final Map<Capability<?>, List<ICapabilityFunction<?, ?>>> factories = new HashMap<>();

    public <T> void register(Capability<T> cap, ICapabilityFunction<T, I> factory) {
        if (!factories.containsKey(cap)) {
            factories.put(cap, new ArrayList<>());
        }
        factories.get(cap).add(factory);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side, I instance) {
        if (!factories.containsKey(cap)) return LazyOptional.empty();

        for (ICapabilityFunction<?, ?> iFactory : factories.get(cap)) {
            @SuppressWarnings("unchecked") // Should be safely cast
            ICapabilityFunction<T, I> factory = (ICapabilityFunction<T, I>) iFactory;
            var lo = factory.apply(side, instance);
            if (lo.isPresent())
                return lo;
        }

        return LazyOptional.empty();
    }

    public interface ICapabilityFunction<T, I> {
        LazyOptional<T> apply(Direction direction, I instance);
    }
}
