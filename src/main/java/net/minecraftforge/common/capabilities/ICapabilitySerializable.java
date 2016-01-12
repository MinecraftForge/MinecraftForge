package net.minecraftforge.common.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraftforge.common.util.INBTSerializable;

//Just a mix of the two, useful in patches to lower the size.
public interface ICapabilitySerializable<T extends NBTBase> extends ICapabilityProvider, INBTSerializable<T>{}
