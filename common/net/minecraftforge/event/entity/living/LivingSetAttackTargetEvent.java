package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityLiving;

public class LivingSetAttackTargetEvent extends LivingEvent
{

    public final EntityLiving target;
    public LivingSetAttackTargetEvent(EntityLiving entity, EntityLiving target)
    {
        super(entity);
        this.target = target;
    }

}
