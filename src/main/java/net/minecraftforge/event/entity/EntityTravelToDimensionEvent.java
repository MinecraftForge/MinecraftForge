package net.minecraftforge.event.entity;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

/**
 * EntityTravelToDimensionEvent is fired before an Entity travels to a dimension.<br>
 * <br>
 * {@link #dimension} contains the id of the dimension the entity is traveling to.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the Entity does not travel to the dimension.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
@Cancelable
public class EntityTravelToDimensionEvent extends EntityEvent
{
    public final int dimension;

    public EntityTravelToDimensionEvent(Entity entity, int dimension)
    {
        super(entity);
        this.dimension = dimension;
    }
}