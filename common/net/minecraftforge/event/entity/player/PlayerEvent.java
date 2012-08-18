package net.minecraftforge.event.entity.player;

import java.io.File;

import net.minecraft.src.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent;

public class PlayerEvent extends LivingEvent
{
    public final EntityPlayer entityPlayer;
    public PlayerEvent(EntityPlayer player)
    {
        super(player);
        entityPlayer = player;
    }
    
    public static class Load extends PlayerEvent
    {
    	public File saveDirectory;
        public Load(EntityPlayer player, File saveDirectory) { 
        	super(player); 
        	this.saveDirectory = saveDirectory;
    	}
    }
    
    public static class Save extends PlayerEvent
    {
    	public File saveDirectory;
        public Save(EntityPlayer player, File saveDirectory) { 
        	super(player); 
        	this.saveDirectory = saveDirectory;
    	}
    }
    
}