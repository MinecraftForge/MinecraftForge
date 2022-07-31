package net.minecraftforge.registries;

import net.minecraft.core.HolderSet;

/**
 * Interface for mods' custom holderset types
 */
public interface ICustomHolderSet<T> extends HolderSet<T>
{
    /**
     * {@return HolderSetType registered to {@link ForgeRegistries.HOLDER_SET_TYPES}}
     */
    public abstract HolderSetType type();
}
