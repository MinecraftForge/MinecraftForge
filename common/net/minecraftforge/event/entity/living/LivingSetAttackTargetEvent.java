package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityLivingBase;

public class LivingSetAttackTargetEvent extends LivingEvent
{

    public final EntityLivingBase target;
    public LivingSetAttackTargetEvent(EntityLivingBase entity, EntityLivingBase target)
    {
        super(entity);
        this.target = target;
    }

}
