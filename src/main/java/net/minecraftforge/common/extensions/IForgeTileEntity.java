package net.minecraftforge.common.extensions;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public interface IForgeTileEntity extends ICapabilitySerializable<NBTTagCompound>
{
    default TileEntity getTileEntity() { return (TileEntity) this; }
    
    @Override
    default void deserializeNBT(NBTTagCompound nbt)
    {
        getTileEntity().readFromNBT(nbt);
    }

    @Override
    default NBTTagCompound serializeNBT()
    {
        NBTTagCompound ret = new NBTTagCompound();
        getTileEntity().writeToNBT(ret);
        return ret;
    }
}
