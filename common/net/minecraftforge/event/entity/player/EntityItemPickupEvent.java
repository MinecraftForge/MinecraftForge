package net.minecraftforge.event.entity.player;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class EntityItemPickupEvent extends PlayerEvent
{
    public final EntityItem item;
    private boolean handled = false;
    
    /**
     * Called when a Player "walks into" an Item Entity existing in the World. Setting this
     * event canceled will prevent any Interaction (like picking up) from happening.
     * Setting this event Handled will make the Item pop like on pickup, but disappear instead of being picked up.
     * @param player Player walking into the Item
     * @param item Item Entity being collided with
     */
    public EntityItemPickupEvent(EntityPlayer player, EntityItem item)
    {
        super(player);
        this.item = item;
    }
    
    public boolean isHandled()
    {
        return handled;
    }

    public void setHandled()
    {
        handled = true;
    }
}
