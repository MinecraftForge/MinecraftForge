package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

@Cancelable
@Event.HasResult
public class LivingItemPickupEvent extends LivingEvent
{
    /**
     * This event is called when a living entity collides with a EntityItem on the ground.
     * The event can be canceled, and no further processing will be done.
     * 
     * setResult(ALLOW) is the same as the old setHandled()
     */
    public final EntityItem item;

    public LivingItemPickupEvent(EntityLivingBase entity, EntityItem item)
    {
        super(entity);
        this.item = item;
    }
}
