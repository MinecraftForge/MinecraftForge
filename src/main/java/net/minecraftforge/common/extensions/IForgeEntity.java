package net.minecraftforge.common.extensions;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public interface IForgeEntity extends ICapabilitySerializable<NBTTagCompound>
{
    default Entity getEntity() { return (Entity) this; }

    default void deserializeNBT(NBTTagCompound nbt)
    {
        getEntity().readFromNBT(nbt);
    }

    default NBTTagCompound serializeNBT()
    {
        NBTTagCompound ret = new NBTTagCompound();
        String id = getEntity().getEntityString();
        if (id != null)
        {
            ret.setString("id", getEntity().getEntityString());
        }
        return getEntity().writeToNBT(ret);
    }
}
