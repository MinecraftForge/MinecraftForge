package net.minecraftforge.event.entity.living;

import net.minecraft.src.EntityLiving;
import net.minecraftforge.event.Cancelable;

/**
 * Called when an Entity shoots a bow.
 * If the event is cancelled the arrow-entity will be destroyed.
 */
@Cancelable
public class LivingHealEvent extends LivingEvent
{
    public enum HealReason
    {
        /**
         * When a player regains health from regenerating due to Peaceful mode (difficulty=0)
         */
        REGEN,
        /**
         * When a player regains health from regenerating due to their hunger being satisfied
         */
        SATIATED,
        /**
         * When a player regains health from eating consumables
         */
        EATING,
        /**
         * When an ender dragon regains health from an ender crystal
         */
        ENDER_CRYSTAL,
        /**
         * When a player is healed by a potion or spell
         */
        MAGIC,
        /**
         * When a player is healed over time by a potion or spell
         */
        MAGIC_REGEN,
        /**
         * Any other reason not covered by the reasons above
         */
        CUSTOM
    }

    public final int amount;
    public final HealReason reason;
    
    public LivingHealEvent(EntityLiving entity, int amount, HealReason reason)
    {
        super(entity);
        this.amount = amount;
        this.reason = reason;
    }

}
