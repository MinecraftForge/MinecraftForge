package net.minecraftforge.event.entity.living;

import net.minecraft.util.DamageSource;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class LivingHurtEvent extends LivingEvent
{
    public final DamageSource source;
    public int ammount;
    public LivingHurtEvent(EntityLiving entity, DamageSource source, int ammount)
    {
        super(entity);
        this.source = source;
        this.ammount = ammount;
    }

}
