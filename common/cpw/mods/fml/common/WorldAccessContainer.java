package cpw.mods.fml.common;

import java.util.Map;

import net.minecraft.src.NBTBase;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.SaveHandler;
import net.minecraft.src.WorldInfo;

public interface WorldAccessContainer
{
    public NBTTagCompound getDataForWriting(SaveHandler handler, WorldInfo info);
    public void readData(SaveHandler handler, WorldInfo info, Map<String,NBTBase> propertyMap, NBTTagCompound tag);
}
