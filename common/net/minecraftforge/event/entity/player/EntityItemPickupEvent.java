package net.minecraftforge.event.entity.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

@Cancelable
@Event.HasResult
public class EntityItemPickupEvent extends PlayerEvent
{
    /**
     * This event is called when a player collides with a EntityItem on the ground.
     * The event can be canceled, and no further processing will be done.
     * 
     *  You can set the result of this event to ALLOW which will trigger the 
     *  processing of achievements, FML's event, play the sound, and kill the 
     *  entity if all the items are picked up.
     *  
     *  setResult(ALLOW) is the same as the old setHandled()
     */

    public final EntityItem item;
    private boolean handled = false;

    public EntityItemPickupEvent(EntityPlayer player, EntityItem item)
    {
        super(player);
        this.item = item;
    }
}
