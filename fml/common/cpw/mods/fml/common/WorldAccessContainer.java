package cpw.mods.fml.common;

import java.util.Map;

import net.minecraft.nbt.*;
import net.minecraft.world.storage.*;


public interface WorldAccessContainer
{
    public NBTTagCompound getDataForWriting(SaveHandler handler, WorldInfo info);
    public void readData(SaveHandler handler, WorldInfo info, Map<String,NBTBase> propertyMap, NBTTagCompound tag);
}
