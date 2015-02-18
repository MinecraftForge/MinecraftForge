package net.minecraftforge.event.entity.living;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.EntityLivingBase;

/**
 * LivingDeathEvent is fired when an Entity dies. <br>
 * This event is fired whenever an Entity dies in 
 * EntityLivingBase#onDeath(DamageSource), 
 * EntityPlayer#onDeath(DamageSource), and 
 * EntityPlayerMP#onDeath(DamageSource). <br>
 * <br>
 * This event is fired via the {@link ForgeHooks#onLivingDeath(EntityLivingBase, DamageSource)}.<br>
 * <br>
 * {@link #source} contains the DamageSource that caused the entity to die. <br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the Entity does not die.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class LivingDeathEvent extends LivingEvent
{
    public final DamageSource source;
    public LivingDeathEvent(EntityLivingBase entity, DamageSource source)
    {
        super(entity);
        this.source = source;
    }
}
