package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.Event.HasResult;

/**
 * Event for when an Enderman stares, can be used to cancel attack/sound allowing for similar effect to pumpkin helmet
 * @author Pharabus
 * 
 */
@HasResult
public class EnderStareEvent extends LivingEvent
{

    public EnderStareEvent(EntityLivingBase entity)
    {
        super(entity);      
    }

}
