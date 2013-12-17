package net.minecraftforge.event.entity.living;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.EntityLivingBase;

@Cancelable
public class LivingFallEvent extends LivingEvent
{
    public float distance;
    public LivingFallEvent(EntityLivingBase entity, float distance)
    {
        super(entity);
        this.distance = distance;
    }
}
