package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;

/**
 * This event is fired when the player is waking up.<br/>
 * This is merely for purposes of listening for this to happen.<br/>
 * There is nothing that can be manipulated with this event.
 */
public class PlayerWakeUpEvent extends PlayerEvent
{
    /**
     * Used for the 'wake up animation'.
     * This is false if the player is considered 'sleepy' and the overlay should slowly fade away.
     */
    public final boolean wakeImmediatly;

    /**
     * Indicates if the server should be notified of sleeping changes.
     * This will only be false if the server is considered 'up to date' already, because, for example, it initiated the call.
     */
    public final boolean updateWorld;

    /**
     * Indicates if the player's sleep was considered successful.
     * In vanilla, this is used to determine if the spawn chunk is to be set to the bed's position.
     */
    public final boolean setSpawn;

    public PlayerWakeUpEvent(EntityPlayer player, boolean wakeImmediatly, boolean updateWorld, boolean setSpawn)
    {
        super(player);
        this.wakeImmediatly = wakeImmediatly;
        this.updateWorld = updateWorld;
        this.setSpawn = setSpawn;
    }
}
