package net.minecraftforge.event.entity.living;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * The ShieldBlockEvent is fired when an entity successfully blocks with a shield.<br>
 * Cancelling this event will have the same impact as if the shield was not eligible to block.<br>
 * The damage blocked cannot be set lower than zero or greater than the original value.<br>
 * Note: The shield item stack "should" be available from {@link LivingEntity#getUseItem()}
 * at least for players.
 */
@Cancelable
public class ShieldBlockEvent extends LivingEvent
{
    protected final DamageSource source;
    protected final float originalBlocked;
    protected float dmgBlocked;
    protected boolean shieldTakesDamage = true;

    public ShieldBlockEvent(LivingEntity blocker, DamageSource source, float blocked)
    {
        super(blocker);
        this.source = source;
        this.originalBlocked = blocked;
        this.dmgBlocked = blocked;
    }

    /**
     * @return The damage source.
     */
    public DamageSource getSource()
    {
        return this.source;
    }

    /**
     * @return The original amount of damage blocked, which is the same as the original
     * incoming damage value.
     */
    public float getOriginalBlocked()
    {
        return this.originalBlocked;
    }

    /**
     * @return The current amount of damage blocked, as a result of this event.
     */
    public float getBlocked()
    {
        return this.dmgBlocked;
    }

    /**
     * Controls if {@link LivingEntity#hurtCurrentlyUsedShield} is called.
     * @return If the shield item will take durability damage or not.
     */
    public boolean shieldTakesDamage()
    {
        return this.shieldTakesDamage;
    }

    /**
     * Set how much damage is blocked by this action.<br>
     * Note that initially the blocked amount is the entire attack.<br>
     */
    public void setBlocked(float blocked)
    {
        this.dmgBlocked = Mth.clamp(blocked, 0, this.originalBlocked);
    }

    /**
     * Set if the shield will take durability damage or not.
     */
    public void setTakesDamage(boolean damage)
    {
        this.shieldTakesDamage = damage;
    }

}
