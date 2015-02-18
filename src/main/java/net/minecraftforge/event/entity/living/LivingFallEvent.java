package net.minecraftforge.event.entity.living;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.EntityLivingBase;

/**
 * LivingFallEvent is fired when an Entity is set to be falling.<br>
 * This event is fired whenever an Entity is set to fall in
 * EntityLivingBase#fall(float).<br>
 * <br>
 * This event is fired via the {@link ForgeHooks#onLivingFall(EntityLivingBase, float)}.<br>
 * <br>
 * {@link #distance} contains the distance the Entity is to fall. If this event is canceled, this value is set to 0.0F.
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the Entity does not fall.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class LivingFallEvent extends LivingEvent
{
    public float distance;
    public float damageMultiplier;
    public LivingFallEvent(EntityLivingBase entity, float distance, float damageMultiplier)
    {
        super(entity);
        this.distance = distance;
        this.damageMultiplier = damageMultiplier;
    }
}
