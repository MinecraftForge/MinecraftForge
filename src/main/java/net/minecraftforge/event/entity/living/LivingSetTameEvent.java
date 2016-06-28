package net.minecraftforge.event.entity.living;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.passive.EntityTameable;

@Cancelable
public class LivingSetTameEvent extends LivingEvent
{
    public final EntityTameable entityTameable;
    public boolean tame;
    public LivingSetTameEvent(EntityTameable entity, boolean isTame)
    {
        super(entity);
        entityTameable = entity;
        tame = isTame;
    }
}
