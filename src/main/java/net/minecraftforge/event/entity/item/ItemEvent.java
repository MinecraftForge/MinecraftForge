package net.minecraftforge.event.entity.item;

import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.event.entity.EntityEvent;

/**
 * Base class for all EntityItem events. Contains a reference to the
 * EntityItem of interest. For most EntityItem events, there's little to no
 * additional useful data from the firing method that isn't already contained
 * within the EntityItem instance.
 */
public class ItemEvent extends EntityEvent
{
    /**
     * The relevant EntityItem for this event, already cast for you.
     */
    public final EntityItem entityItem;

    /**
     * Creates a new event for an EntityItem.
     * 
     * @param itemEntity The EntityItem for this event
     */
    public ItemEvent(EntityItem itemEntity)
    {
        super(itemEntity);
        this.entityItem = itemEntity;
    }
}