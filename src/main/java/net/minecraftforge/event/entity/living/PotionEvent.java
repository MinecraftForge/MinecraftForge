package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PotionEvent extends Event
{
    private EntityLivingBase entity;
    private PotionEffect existingEffect;
    private PotionEffect newEffect;

    public PotionEvent(EntityLivingBase entity, PotionEffect existingEffect, PotionEffect newEffect)
    {
        this.entity = entity;
        this.existingEffect = existingEffect;
        this.newEffect = newEffect;
    }

    @Cancelable
    public static class PotionAddedEvent extends PotionEvent
    {

        public PotionAddedEvent(EntityLivingBase entity, PotionEffect newEffect)
        {
            super(entity, null, newEffect);
        }
    }

    @Cancelable
    public static class PotionCombinedEvent extends PotionEvent
    {

        public PotionCombinedEvent(EntityLivingBase entity, PotionEffect existingEffect, PotionEffect newEffect)
        {
            super(entity, existingEffect, newEffect);
        }
    }

    public static class PotionRemovedEvent extends PotionEvent
    {

        public PotionRemovedEvent(EntityLivingBase entity, PotionEffect oldEffect)
        {
            super(entity, oldEffect, null);
        }
    }

    public EntityLivingBase getLivingEntity()
    {
        return entity;
    }

    public PotionEffect getExistingEffect()
    {
        return existingEffect;
    }

    public PotionEffect getNewEffect()
    {
        return newEffect;
    }
}
