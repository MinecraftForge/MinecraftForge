package net.minecraftforge.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.util.INBTSerializable;

public class WorldCapabilityData extends WorldSavedData
{
    private INBTSerializable<NBTTagCompound> serializable;
    private NBTTagCompound data = null;
    private int dimension;

    public WorldCapabilityData(String name)
    {
        super(name);
    }

    public WorldCapabilityData(WorldProvider provider, INBTSerializable<NBTTagCompound> serializable)
    {
        super(fileNameForProvider(provider));
        this.serializable = serializable;
        this.dimension = provider.getDimension();
    }

	@Override
    public void readFromNBT(NBTTagCompound nbt)
    {
	    if(nbt.hasKey("ForgeCaps"))
	    {
	        this.data = nbt.getCompoundTag("ForgeCaps");
	        if(serializable != null)
	            serializable.deserializeNBT(this.data);
	    }
	}

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setTag("ForgeCaps", serializable.serializeNBT());
        return nbt;
    }

    @Override
    public boolean isDirty()
    {
        return true;
    }
    
    public void setCapabilities(WorldProvider provider, INBTSerializable<NBTTagCompound> capabilities)
    {
        this.dimension = provider.getDimension();
        this.serializable = capabilities;
        if(this.data != null)
            serializable.deserializeNBT(this.data);
    }

    public static String fileNameForProvider(WorldProvider provider)
    {
        return "capabilities" + provider.getDimensionType().getSuffix();
    }
}
