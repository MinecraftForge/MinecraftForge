package net.minecraftforge.event.entity.player;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class AttackEntityEvent extends PlayerEvent
{
    public final Entity target;
    /**
     * Called when a Player Leftclicks an Entity that is within reach distance.
     * Setting this event canceled will prevent the target from taking any damage or suffering onHit effects.
     * @param player Player who clicked
     * @param target Entity being clicked
     */
    public AttackEntityEvent(EntityPlayer player, Entity target)
    {
        super(player);
        this.target = target;
    }
}
