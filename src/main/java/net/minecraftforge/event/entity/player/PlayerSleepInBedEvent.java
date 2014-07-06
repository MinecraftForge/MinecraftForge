package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.EnumStatus;

/**
 * PlayerSleepInBedEvent is fired when a player sleeps in a bed.
 * <br>
 * This event is fired whenever a player sleeps in a bed in
 * EntityPlayer#sleepInBedAt(int, int, int).<br>
 * <br>
 * {@link #result} contains whether the player is able to sleep. <br>
 * <br>
 * This event is not {@link Cancelable}.
 * <br>
 * This event does not have a result. {@link HasResult}
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class PlayerSleepInBedEvent extends PlayerEvent
{
    public EnumStatus result = null;
    public final int x;
    public final int y;
    public final int z;

    public PlayerSleepInBedEvent(EntityPlayer player, int x, int y, int z)
    {
        super(player);
        this.x = x;
        this.y = y;
        this.z = z;
    }

}
