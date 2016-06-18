package net.minecraftforge.event.entity.living;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.EntityLivingBase;

/**
 * LivingHealEvent is fired when an Entity is set to be healed. <br>
 * This event is fired whenever an Entity is healed in {@link EntityLivingBase#heal(float)}<br>
 * <br>
 * This event is fired via the {@link ForgeEventFactory#onLivingHeal(EntityLivingBase, float)}.<br>
 * <br>
 * {@link #amount} contains the amount of healing done to the Entity that was healed. <br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the Entity is not healed.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class LivingHealEvent extends LivingEvent
{
    private float amount;
    public LivingHealEvent(EntityLivingBase entity, float amount)
    {
        super(entity);
        this.setAmount(amount);
    }

    public float getAmount()
    {
        return amount;
    }

    public void setAmount(float amount)
    {
        this.amount = amount;
    }
}