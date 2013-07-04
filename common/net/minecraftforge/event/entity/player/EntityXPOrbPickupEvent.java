package net.minecraftforge.event.entity.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

@Cancelable
public class EntityXPOrbPickupEvent extends PlayerEvent
{
    /**
     * This event is called when a player collides with a EntityXPOrb on the ground.
     * The event can be canceled, and no further processing will be done.
     */

    public final EntityXPOrb xpOrb;
    public int xpValue;
    public int xpCooldown;

    public EntityXPOrbPickupEvent(EntityPlayer player, EntityXPOrb xpOrb, int xpValue, int xpCooldown)
    {
        super(player);
        this.xpOrb = xpOrb;
        this.xpValue = xpValue;
        this.xpCooldown = xpCooldown;
    }
}
