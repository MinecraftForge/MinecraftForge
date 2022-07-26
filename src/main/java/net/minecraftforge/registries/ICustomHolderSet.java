package net.minecraftforge.registries;

import net.minecraft.core.HolderSet;

public interface ICustomHolderSet<T> extends HolderSet<T>
{
    public abstract HolderSetType type();
}
