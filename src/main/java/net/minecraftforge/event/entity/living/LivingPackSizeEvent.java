package net.minecraftforge.event.entity.living;

import net.minecraftforge.fml.common.eventhandler.Event.HasResult;
import net.minecraft.entity.EntityLiving;

@HasResult
public class LivingPackSizeEvent extends LivingEvent
{
    /**
     * This event is fired when the spawning system determines the
     * maximum amount of the selected entity that can spawn at the same 
     * time.
     *  
     * If you set the result to 'ALLOW', it means that you want to return
     * the value of maxPackSize as the maximum pack size for current entity.
     */
    public int maxPackSize;
    
    public LivingPackSizeEvent(EntityLiving entity)
    {
        super(entity);
    }
}
