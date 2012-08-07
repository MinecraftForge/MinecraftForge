package net.minecraftforge.event.entity.living;

import net.minecraft.src.DamageSource;
import net.minecraft.src.EntityLiving;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class LivingAttackEvent extends LivingEvent
{
    public final DamageSource source;
    public final int ammount;
    public LivingAttackEvent(EntityLiving entity, DamageSource source, int ammount)
    {
        super(entity);
        this.source = source;
        this.ammount = ammount;
    }
}
