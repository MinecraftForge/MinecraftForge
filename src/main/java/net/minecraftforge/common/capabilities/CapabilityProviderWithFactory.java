package net.minecraftforge.common.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CapabilityProviderWithFactory<B extends ICapabilityProviderImpl<B>> implements ICapabilityProviderImpl<B> {
    private final CapabilityDispatcher capabilities;
    private boolean valid = true;

    public CapabilityProviderWithFactory(CapabilityFactoryHolder<B> holder) {
        holder.build();
        this.capabilities = new CapabilityDispatcher(
                holder.getCaps(cast()),
                List.of() // Maybe not needed?
        );
    }

    private B cast() {
        return (B) this;
    }

    protected final @Nullable CompoundTag serializeCaps(HolderLookup.Provider registryAccess) {

        return null;
    }

    protected final void deserializeCaps(HolderLookup.Provider registryAccess, CompoundTag tag) {

    }


    @Override
    public void invalidateCaps() {
        valid = false;
        capabilities.invalidate();
    }

    @Override
    public void reviveCaps() {
        valid = true;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        final CapabilityDispatcher disp = capabilities;
        return !valid || disp == null ? LazyOptional.empty() : disp.getCapability(cap, side);
    }
}
