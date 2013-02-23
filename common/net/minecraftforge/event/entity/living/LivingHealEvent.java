package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityLiving;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class LivingHealEvent extends LivingEvent {

    public final int oldHealth;
    public int amount;
    
    public LivingHealEvent(EntityLiving entity, int amount)
    {
        super(entity);
        this.oldHealth = entity.getHealth();
        this.amount = amount;
    }

}
