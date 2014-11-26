package net.minecraftforge.event.entity;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;

/**
 * EntityStruckByLightningEvent is fired when an Entity is about to be struck by lightening.<br>
 * This event is fired whenever an EntityLightningBolt is updated to strike an Entity in
 * EntityLightningBolt#onUpdate() via {@link ForgeEventFactory#onEntityStruckByLightning(Entity, EntityLightningBolt)}.<br>
 * <br>
 * {@link #lightning} contains the instance of EntityLightningBolt attempting to strike an entity.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the Entity is not struck by the lightening.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
@Cancelable
public class EntityStruckByLightningEvent extends EntityEvent
{
    public final EntityLightningBolt lightning;

    public EntityStruckByLightningEvent(Entity entity, EntityLightningBolt lightning)
    {
        super(entity);
        this.lightning = lightning;
    }
}
