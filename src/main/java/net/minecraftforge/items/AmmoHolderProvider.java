package net.minecraftforge.items;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AmmoHolderProvider implements ICapabilityProvider {
    private AmmoHolderHandler ammoHolder;
    private final LazyOptional<AmmoHolderHandler> optional = LazyOptional.of(() -> ammoHolder);

    public AmmoHolderProvider() { this.ammoHolder = new AmmoHolderHandler(); }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
    {
        if (cap == CapabilityAmmoHolder.AMMO_HOLDER_CAPABILITY)
            return optional.cast();
        return LazyOptional.empty();
    }
}
