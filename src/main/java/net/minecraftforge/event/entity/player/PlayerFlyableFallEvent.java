package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Occurs when a player falls, but is able to fly.  Doesn't need to be cancelable, this is mainly for notification purposes.
 * @author Mithion
 *
 */
public class PlayerFlyableFallEvent extends PlayerEvent
{
    private float distance;
    private float multiplier;

    public PlayerFlyableFallEvent(EntityPlayer player, float distance, float multiplier)
    {
        super(player);
        this.distance = distance;
        this.multiplier = multiplier;
    }

    public float getDistance() { return distance;}
    public void setDistance(float distance) { this.distance = distance; }
    public float getMultiplier() { return multiplier; }
    public void setMultiplier(float multiplier) { this.multiplier = multiplier; }
}
