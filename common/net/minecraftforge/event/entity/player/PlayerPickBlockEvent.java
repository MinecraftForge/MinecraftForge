package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class PlayerPickBlockEvent extends PlayerEvent
{

    public MovingObjectPosition target;

    public PlayerPickBlockEvent(EntityPlayer player, MovingObjectPosition target)
    {
        super(player);
        this.target = target;
    }

}
