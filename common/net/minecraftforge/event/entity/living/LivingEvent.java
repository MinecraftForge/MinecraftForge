package net.minecraftforge.event.entity.living;

import net.minecraft.src.EntityLiving;
import net.minecraftforge.event.entity.EntityEvent;

public class LivingEvent extends EntityEvent
{
    public final EntityLiving entityLiving;
    public LivingEvent(EntityLiving entity)
    {
        super(entity);
        entityLiving = entity;
    }
}
