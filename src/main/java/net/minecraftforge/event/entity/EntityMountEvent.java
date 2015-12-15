package net.minecraftforge.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

/**
 * This event gets fired whenever a entity mounts/dismounts another entity.<br>
 * <b>entityBeingMounted can be null</b>, be sure to check for that.
 * <br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the entity does not mount/dismount the other entity.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 *<br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 * 
 */

@Cancelable
public class EntityMountEvent extends EntityEvent
{
    
    public final Entity entityMounting;
    public final Entity entityBeingMounted;
    public final World worldObj;
    
    private final boolean isMounting;

    public EntityMountEvent(Entity entityMounting, Entity entityBeingMounted, World entityWorld, boolean isMounting)
    {
        super(entityMounting);
        this.entityMounting = entityMounting;
        this.entityBeingMounted = entityBeingMounted;
        this.worldObj = entityWorld;
        this.isMounting = isMounting;
    }
    
    public boolean isMounting()
    {
        return isMounting;
    }
    
    public boolean isDismounting()
    {
        return !isMounting;
    }

}
