package net.minecraftforge.common;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public interface IExtendedEntityProperties {
    public void SaveNBTData(NBTTagCompound compound);
    public void LoadFromNBT(NBTTagCompound compound);
    public void Init(Entity entity, World world);
}
