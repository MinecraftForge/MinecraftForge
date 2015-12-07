package net.minecraftforge.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * This event gets fired whenever a entity dismounts another entity.<br>
 * <b>entityBeingMounted can be null</b>, be sure to check for that.
 * <br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the entity does not dismount the other entity.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 *<br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 * 
 */

@Cancelable
public class EntityDismountEvent extends EntityEvent
{

    public final Entity entityMounting;
    public final Entity entityBeingMounted;
    public final World worldObj;

    public EntityDismountEvent(Entity entityMounting, Entity entityBeingMounted, World entityWorld)
    {
        super(entityMounting);
        this.entityMounting = entityMounting;
        this.entityBeingMounted = entityBeingMounted;
        this.worldObj = entityWorld;
    }

}
