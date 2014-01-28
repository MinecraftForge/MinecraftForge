package net.minecraft.world.storage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public interface IPlayerFileData
{
    // JAVADOC METHOD $$ func_75753_a
    void writePlayerData(EntityPlayer var1);

    // JAVADOC METHOD $$ func_75752_b
    NBTTagCompound readPlayerData(EntityPlayer var1);

    // JAVADOC METHOD $$ func_75754_f
    String[] getAvailablePlayerDat();
}