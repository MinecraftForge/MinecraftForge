package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Occurs when a player falls, but is able to fly.  Doesn't need to be cancelable, this is mainly for notification purposes.
 * @author Mithion
 *
 */
public class PlayerFlyableFallEvent extends PlayerEvent
{
    public float distance;
    public float multipler;

    public PlayerFlyableFallEvent(EntityPlayer player, float distance, float multiplier)
    {
        super(player);
        this.distance = distance;
        this.multipler = multiplier;
    }
}
