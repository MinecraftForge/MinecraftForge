package net.minecraftforge.event.entity.living;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.EntityLivingBase;

/**
 * LivingAttackEvent is fired when a living Entity is attacked. <br>
 * This event is fired whenever an Entity is attacked in
 * {@link EntityLivingBase#attackEntityFrom(DamageSource, float)} and
 * {@link EntityPlayer#attackEntityFrom(DamageSource, float)}. <br>
 * <br>
 * This event is fired via the {@link net.minecraftforge.common.ForgeHooks#onLivingAttack(EntityLivingBase, DamageSource, float)}.<br>
 * <br>
 * {@link #source} contains the DamageSource of the attack. <br>
 * {@link #amount} contains the amount of damage dealt to the entity. <br>
 * <br>
 * This event is {@link net.minecraftforge.fml.common.eventhandler.Cancelable}.<br>
 * If this event is canceled, the Entity does not take attack damage.<br>
 * <br>
 * This event does not have a result. {@link net.minecraftforge.fml.common.eventhandler.Event.HasResult}<br>
 *<br>
 * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class LivingAttackEvent extends LivingEvent
{
    private final DamageSource source;
    private final float amount;
    public LivingAttackEvent(EntityLivingBase entity, DamageSource source, float amount)
    {
        super(entity);
        this.source = source;
        this.amount = amount;
    }

    public DamageSource getSource() { return source; }
    public float getAmount() { return amount; }
}
