package net.minecraftforge.common.capabilities.factory;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CapabilityFactoryProvider<I, G extends ICapabilityFactoryManager<I>> implements ICapabilityProvider {
    private final G owner;

    public CapabilityFactoryProvider(G owner) {
        this.owner = owner;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return owner == null ? LazyOptional.empty() : owner.getCapability(cap, side, (I) this);
    }
}
