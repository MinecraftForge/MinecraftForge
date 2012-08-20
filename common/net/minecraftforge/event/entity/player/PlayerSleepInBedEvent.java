package net.minecraftforge.event.entity.player;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumStatus;

public class PlayerSleepInBedEvent extends PlayerEvent
{
    public EnumStatus result = null;
    public final int x;
    public final int y;
    public final int z;

    /**
     * Called when a Player attempts to Sleep in a bed. Bed and Sleep viability have been checked at this point.
     * Setting the result EnumStatus to anything but null will push your result into the Minecraft checking code.
     * @param player Player attempting to use a Bed
     * @param x Bed Coordinate
     * @param y Bed Coordinate
     * @param z Bed Coordinate
     */
    public PlayerSleepInBedEvent(EntityPlayer player, int x, int y, int z)
    {
        super(player);
        this.x = x;
        this.y = y;
        this.z = z;
    }

}
