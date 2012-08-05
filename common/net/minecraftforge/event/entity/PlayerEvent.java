package net.minecraftforge.event.entity;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;

public class PlayerEvent extends EntityEvent
{
    private final EntityPlayer player;
    public PlayerEvent(EntityPlayer player)
    {
        super(player);
        this.player = player;
    }

    public EntityPlayer getPlayer()
    {
        return player;
    }
}
