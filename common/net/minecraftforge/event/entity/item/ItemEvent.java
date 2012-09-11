package net.minecraftforge.event.entity.item;

import net.minecraft.src.EntityItem;
import net.minecraftforge.event.entity.EntityEvent;

/**
 * Abstract base class for all EntityItem events. Contains a reference to the
 * EntityItem of interest. For most EntityItem events, there's little to no
 * additional useful data from the firing method that isn't already contained
 * within the EntityItem instance.
 * 
 * @author Grenadier
 */
public abstract class ItemEvent extends EntityEvent
{
    /**
     * The relevant EntityItem for this event, already cast for you.
     */
    public final EntityItem EntityItem;

    /**
     * Creates a new event for an EntityItem.
     * 
     * @param eItem
     */
    public ItemEvent(EntityItem eItem)
    {
        super(eItem);
        this.EntityItem = eItem;
    }
}