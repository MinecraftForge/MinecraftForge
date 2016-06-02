package net.minecraftforge.event.entity.living;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.EntityLivingBase;

/**
 * LivingHurtEvent is fired when an Entity is set to be hurt. <br>
 * This event is fired whenever an Entity is hurt in 
 * {@link EntityLivingBase#damageEntity(DamageSource, float)} and
 * {@link EntityPlayer#damageEntity(DamageSource, float)}.<br>
 * <br>
 * This event is fired via the {@link net.minecraftforge.common.ForgeHooks#onLivingHurt(EntityLivingBase, DamageSource, float)}.<br>
 * <br>
 * {@link #source} contains the DamageSource that caused this Entity to be hurt. <br>
 * {@link #amount} contains the amount of damage dealt to the Entity that was hurt. <br>
 * <br>
 * This event is {@link net.minecraftforge.fml.common.eventhandler.Cancelable}.<br>
 * If this event is canceled, the Entity is not hurt.<br>
 * <br>
 * This event does not have a result. {@link net.minecraftforge.fml.common.eventhandler.Event.HasResult}<br>
 * <br>
 * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class LivingHurtEvent extends LivingEvent
{
    private final DamageSource source;
    private float amount;
    public LivingHurtEvent(EntityLivingBase entity, DamageSource source, float amount)
    {
        super(entity);
        this.source = source;
        this.amount = amount;
    }

    public DamageSource getSource() { return source; }

    public float getAmount() { return amount; }

    public void setAmount(float amount) { this.amount = amount; }
}
