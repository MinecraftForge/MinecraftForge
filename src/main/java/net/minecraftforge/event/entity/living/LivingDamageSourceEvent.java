package net.minecraftforge.event.entity.living;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * LivingDamageSourceEvent is fired when an Entity is attacked. <br>
 * At this point, {@link LivingAttackEvent} has not been cancelled. <br>
 * This event is fired when an Entity is starting to be hurt in
 * {@link LivingEntity#hurt(DamageSource, float)} and
 * {@link Player#hurt(DamageSource, float)}. <br>
 * <br>
 * This event is fired via the {@link ForgeHooks#onLivingDamageSource(LivingEntity, DamageSource, float)}. <br>
 * <br>
 * {@link #source} contains the DamageSource from the attack. <br>
 * {@link #amount} contains the damage that will be dealt to the entity. <br>
 * <br>
 * This event is {@link Cancelable}. <br>
 * If this event is cancelled, the Entity is not hurt. <br>
 * <br>
 * This event does not have a result. {@link Result}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
@Cancelable
public class LivingDamageSourceEvent extends LivingEvent
{
    private DamageSource source;
    private final float amount; // Damage amount gets changed in other events
    public LivingDamageSourceEvent(LivingEntity entity, DamageSource source, float amount)
    {
        super(entity);
        this.source = source;
        this.amount = amount;
    }
    
    public DamageSource getSource() { return this.source; }
    
    public void setSource(DamageSource source) { this.source = source; }
    
    public float getAmount() { return this.amount; }
    
}
