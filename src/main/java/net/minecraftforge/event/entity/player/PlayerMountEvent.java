package net.minecraftforge.event.entity.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerMountEvent extends PlayerEvent{

    public final EntityPlayer player;
    public final Entity entity;
    
    public PlayerMountEvent(EntityPlayer player, Entity entity)
    {
        super(player);
        this.player = player;
        this.entity = entity;
    }

}
