package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class PlayerInsideOpaqueBlockEvent extends PlayerEvent
{
	public PlayerInsideOpaqueBlockEvent(EntityPlayer player)
    {
        super(player);
    }
}
