package net.minecraftforge.event.entity.living;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.EntityLivingBase;

@Cancelable
public class LivingDeathEvent extends LivingEvent
{
    public final DamageSource source;
    public LivingDeathEvent(EntityLivingBase entity, DamageSource source)
    {
        super(entity);
        this.source = source;
    }

}
