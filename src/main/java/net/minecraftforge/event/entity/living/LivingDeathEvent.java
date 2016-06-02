package net.minecraftforge.event.entity.living;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.EntityLivingBase;

/**
 * LivingDeathEvent is fired when an Entity dies. <br>
 * This event is fired whenever an Entity dies in 
 * {@link EntityLivingBase#onDeath(DamageSource)},
 * {@link EntityPlayer#onDeath(DamageSource)}, and
 * {@link EntityPlayerMP#onDeath(DamageSource)}. <br>
 * <br>
 * This event is fired via the {@link net.minecraftforge.common.ForgeHooks#onLivingDeath(EntityLivingBase, DamageSource)}.<br>
 * <br>
 * {@link #source} contains the DamageSource that caused the entity to die. <br>
 * <br>
 * This event is {@link net.minecraftforge.fml.common.eventhandler.Cancelable}.<br>
 * If this event is canceled, the Entity does not die.<br>
 * <br>
 * This event does not have a result. {@link net.minecraftforge.fml.common.eventhandler.Event.HasResult}<br>
 * <br>
 * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class LivingDeathEvent extends LivingEvent
{
    private final DamageSource source;
    public LivingDeathEvent(EntityLivingBase entity, DamageSource source)
    {
        super(entity);
        this.source = source;
    }

    public DamageSource getSource()
    {
        return source;
    }
}
