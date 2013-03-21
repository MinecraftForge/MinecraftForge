package net.minecraftforge.common;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public interface IExtendedEntityProperties {
    public void saveNBTData(NBTTagCompound compound);
    public void loadFromNBT(NBTTagCompound compound);
    public void init(Entity entity, World world);
}
