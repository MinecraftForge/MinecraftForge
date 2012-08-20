package net.minecraftforge.event.entity.player;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class EntityInteractEvent extends PlayerEvent
{
    public final Entity target;
    /**
     * Called when a player Rightclicks on an Entity. Setting this event canceled will prevent
     * any interaction from happening.
     * @param player Player who right clicked the Entity
     * @param target Entity being right clicked
     */
    public EntityInteractEvent(EntityPlayer player, Entity target)
    {
        super(player);
        this.target = target;
    }
}
