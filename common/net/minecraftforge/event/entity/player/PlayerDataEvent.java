package net.minecraftforge.event.entity.player;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NBTTagCompound;

public class PlayerDataEvent extends PlayerEvent
{

    public final NBTTagCompound playerData;
    
    public PlayerDataEvent(EntityPlayer player, NBTTagCompound playerData)
    {
        super(player);
        this.playerData = playerData;
    }
    
    public static class Load extends PlayerDataEvent
    {
        public Load(EntityPlayer player, NBTTagCompound playerData)
        {
            super(player, playerData);
        }
    }

    public static class Save extends PlayerDataEvent
    {
        public Save(EntityPlayer player, NBTTagCompound playerData)
        {
            super(player, playerData);
        }
    }
}