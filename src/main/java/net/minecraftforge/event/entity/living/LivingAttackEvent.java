package net.minecraftforge.event.entity.living;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.EntityLivingBase;

/**
 * LivingAttackEvent is fired when a living Entity is attacked. <br>
 * This event is fired whenever an Entity is attacked in
 * EntityLivingBase#attackEntityFrom(DamageSource, float) and 
 * EntityPlayer#attackEntityFrom(DamageSource, float). <br>
 * <br>
 * This event is fired via the {@link ForgeHooks#onLivingAttack(EntityLivingBase, DamageSource, float)}.<br>
 * <br>
 * {@link #source} contains the DamageSource of the attack. <br>
 * {@link #amount} contains the amount of damage dealt to the entity. <br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the Entity does not take attack damage.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 *<br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class LivingAttackEvent extends LivingEvent
{
    public final DamageSource source;
    public final float ammount;
    public LivingAttackEvent(EntityLivingBase entity, DamageSource source, float ammount)
    {
        super(entity);
        this.source = source;
        this.ammount = ammount;
    }
}
