package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * LivingConversionEvent is triggered when an entity is trying
 * to replace itself with other entity
 *
 * This event is {@link Cancelable}
 * If cancelled, the replacement will not occur
 */
@Cancelable
public class LivingConversionEvent extends LivingEvent
{
    public LivingConversionEvent(LivingEntity entity)
    {
        super(entity);
    }

    public static class Pre extends LivingConversionEvent
    {
        private EntityType<? extends LivingEntity> outcome;

        public Pre(LivingEntity entity, EntityType<? extends LivingEntity> outcome)
        {
            super(entity);
            this.outcome = outcome;
        }

        public EntityType<? extends LivingEntity> getOutcome()
        {
            return outcome;
        }

        public void setOutcome(EntityType<? extends LivingEntity> outcome)
        {
            this.outcome = outcome;
        }
    }

    public static class Post extends LivingConversionEvent
    {
        private LivingEntity outcome;

        public Post(LivingEntity entity, LivingEntity outcome)
        {
            super(entity);
            this.outcome = outcome;
        }

        public LivingEntity getOutcome()
        {
            return outcome;
        }

        public void setOutcome(LivingEntity outcome)
        {
            this.outcome = outcome;
        }
    }
}
