package net.minecraftforge.event.entity.player;

import net.minecraft.src.EntityPlayer;

public class PlayerJoinEvent extends PlayerEvent
{

    public PlayerJoinEvent(EntityPlayer player)
    {
        super(player);
    }
}
