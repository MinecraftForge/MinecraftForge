package net.minecraftforge.common.capabilities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public interface IAttachedCapabilityProvider<C, O extends ICapabilityProvider> extends INBTSerializable<CompoundTag>, Comparable<IAttachedCapabilityProvider<C, O>>
{

    CapabilityType<C> getType();

    ResourceLocation getId();

    @NotNull Capability<C> getCapability(@Nullable Direction direction);

    /**
     * Invalidates all contained caps, and prevents {@link #getCapability(Capability, Direction)} from returning a value.<br>
     * This is usually called when the object in question is removed/terminated.<br>
     * However there are be cases these 'invalid' caps need to be retrieved/copied.<br>
     * In that case, call {@link #reviveCaps()}, perform the needed operations, and then call {@link #invalidateCaps()} again.<br>
     * Be sure to make your invalidate callbacks recursion safe.
     */
    void invalidateCaps();

    /**
     * This function will allow {@link #getCapability(Capability, Direction)} to return values again.<br>
     * This can be used to copy caps from one removed provider to a new one.<br>
     * It is expected that all calls to this method are closely followed by a call to {@link #invalidateCaps()}
     */
    void reviveCaps();

    @Override
    default @Nullable CompoundTag serializeNBT() {
        return null;
    }

    @Override
    default void deserializeNBT(CompoundTag tag) {
    }

    default int compareTo(@Nullable IAttachedCapabilityProvider<C, O> other) {
        return 0;
    }

    default @Nullable IAttachedCapabilityProvider<C, O> copy(O copiedParent) {
        return null;
    }
}