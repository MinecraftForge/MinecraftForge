package net.minecraftforge.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * EntityBoundingBoxChangeEvent is fired whenever something is trying to change the bounding box of an Entity. <br>
 * This event is fired whenever something is trying to change the bounding box of an Entity in
 * {@link Entity#setEntityBoundingBox(AxisAlignedBB)}<br>
 * <br>
 * {@link #oldBoundingBox} contains the old bounding box of the entity.<br>
 * <br>
 * <br>
 * {@link #newBoundingBox} contains the new bounding box of the entity. You can modify it to modify the resulting bounding box of the entity<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the Bounding Box wont get updated.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class EntityBoundingBoxChangeEvent extends EntityEvent {

    public final AxisAlignedBB oldBoundingBox;
    public AxisAlignedBB newBoundingBox;

    public EntityBoundingBoxChangeEvent(Entity entity, AxisAlignedBB oldBoundingBox, AxisAlignedBB newBoundingBox)
    {
        super(entity);
        this.oldBoundingBox = oldBoundingBox;
        this.newBoundingBox = newBoundingBox;
    }

}
