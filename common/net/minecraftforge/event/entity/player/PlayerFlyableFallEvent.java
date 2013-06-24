package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.Cancelable;

/**
 * Occurs when a player falls, but is able to fly.  Doesn't need to be cancelable, this is mainly for notification purposes.
 * @author Mithion
 *
 */
public class PlayerFlyableFallEvent extends PlayerEvent
{

    public float distance;

    public PlayerFlyableFallEvent(EntityPlayer player, float f)
    {
        super(player);
        this.distance = f;
    }

}
