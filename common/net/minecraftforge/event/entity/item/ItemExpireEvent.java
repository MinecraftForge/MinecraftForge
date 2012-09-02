package net.minecraftforge.event.entity.item;

import net.minecraft.src.EntityItem;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.entity.EntityEvent;

/**
 * Event that is fired when an EntityItem's age has reached its maximum
 * lifespan. Cancelling this event will prevent the EntityItem from being
 * flagged as dead, thus staying it's removal from the world. However, should
 * you cancel this event, be sure to change either the age, lifespan, or both of
 * the EntityItem, or the event will simply fire again on the next update cycle.
 * 
 * @author Grenadier
 */
@Cancelable
public class ItemExpireEvent extends ItemEvent
{

    /**
     * Creates a new event for an expiring EntityItem.
     * 
     * @param entityItem
     *            The EntityItem being deleted.
     */
    public ItemExpireEvent(EntityItem entityItem)
    {
        super(entityItem);
    }
}