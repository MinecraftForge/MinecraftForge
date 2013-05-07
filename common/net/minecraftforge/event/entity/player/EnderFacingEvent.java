package net.minecraftforge.event.entity.player;

import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.Cancelable;

/**
 * Event for when a Player looks at an Enderman.
 * Cancel the event to prevent the Enderman from going hostile against the Player.
 * @author SanAndreasP
 */
@Cancelable
public class EnderFacingEvent extends PlayerEvent
{
    public final EntityEnderman enderman;
    public EnderFacingEvent(EntityEnderman ender, EntityPlayer player)
    {
        super(player);
        this.enderman = ender;
    }
}
