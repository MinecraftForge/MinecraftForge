package net.minecraftforge.common.extensions;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.village.Village;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public interface IForgeVillage extends ICapabilitySerializable<NBTTagCompound>
{
    default Village getVillage()
    {
        return (Village) this;
    }
    
    @Override
    default void deserializeNBT(NBTTagCompound nbt)
    {
        getVillage().readVillageDataFromNBT(nbt);
    }

    @Override
    default NBTTagCompound serializeNBT()
    {
        NBTTagCompound ret = new NBTTagCompound();
        getVillage().writeVillageDataToNBT(ret);
        return ret;
    }
}
