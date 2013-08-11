package net.minecraftforge.client.event;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.entity.player.PlayerEvent;

@Cancelable
public class PlayerPushOutOfBlocksEvent extends PlayerEvent
{
	public PlayerPushOutOfBlocksEvent(EntityPlayerSP player)
    {
        super(player);
    }
}
