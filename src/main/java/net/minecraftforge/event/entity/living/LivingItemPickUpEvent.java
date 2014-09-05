package net.minecraftforge.event.entity.living;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;

@Cancelable
@Event.HasResult
public class LivingItemPickUpEvent extends LivingEvent
{
    /**
     * This event is called when a living entity collides with a EntityItem on the ground.
     * The event can be canceled, and no further processing will be done.
     * 
     *  You can set the result of this event to ALLOW which will trigger
     *  FML's event, play the sound, and kill the 
     *  entity if all the items are picked up.
     *  
     *  setResult(ALLOW) is the same as the old setHandled()
     */
    
    public EntityItem itemEntity;
    
    public LivingItemPickUpEvent(EntityLivingBase pEntity, EntityItem itemEntity)
    {
        super(pEntity);
        this.itemEntity = itemEntity;
    }
}
