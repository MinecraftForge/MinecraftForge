package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.Cancelable;

/**
 * An event which is fired whenever a player looks at an Enderman. It holds the Enderman (entityLiving)
 * as well as the Player which looks at it )entityPlayer).
 * Cancel the event to prevent the Enderman from going aggressive against the player.
 * @author SanAndreas
 *
 */
@Cancelable
public class EnderFacingEvent extends LivingEvent {

    public final EntityPlayer entityPlayer;
    public EnderFacingEvent(EntityLiving entity, EntityPlayer player)
    {
        super(entity);
        this.entityPlayer = player;
    }

}
