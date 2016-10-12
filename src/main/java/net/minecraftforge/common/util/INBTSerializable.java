package net.minecraftforge.common.util;

import net.minecraft.nbt.NBTBase;

/**
 * An interface designed to unify various things in the Minecraft
 * code base that can be serialized to and from a NBT tag.
 */
public interface INBTSerializable<T extends NBTBase>
{
    T serializeNBT();
    void deserializeNBT(T nbt);
}
