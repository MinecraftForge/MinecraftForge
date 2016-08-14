package net.minecraftforge.common.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSavedData;

public class WorldCapabilityData extends WorldSavedData
{
    public static final String ID = "capabilities";

    private INBTSerializable<NBTTagCompound> serializable;
    private NBTTagCompound capNBT = null;

    public WorldCapabilityData(String name)
    {
        super(name);
    }

    public WorldCapabilityData(INBTSerializable<NBTTagCompound> serializable)
    {
        super(ID);
        this.serializable = serializable;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        this.capNBT = nbt;
        if (serializable != null)
        {
            serializable.deserializeNBT(this.capNBT);
            this.capNBT = null;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        if (serializable != null)
            nbt = serializable.serializeNBT();
        return nbt;
    }

    @Override
    public boolean isDirty()
    {
        return true;
    }

    public void setCapabilities(WorldProvider provider, INBTSerializable<NBTTagCompound> capabilities)
    {
        this.serializable = capabilities;
        if (this.capNBT != null && serializable != null)
        {
            serializable.deserializeNBT(this.capNBT);
            this.capNBT = null;
        }
    }
}