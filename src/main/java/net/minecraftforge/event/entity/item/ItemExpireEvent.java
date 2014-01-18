package net.minecraftforge.event.entity.item;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.item.EntityItem;

/**
 * Event that is fired when an EntityItem's age has reached its maximum
 * lifespan. Canceling this event will prevent the EntityItem from being
 * flagged as dead, thus staying it's removal from the world. If canceled
 * it will add more time to the entitie's life equal to extraLife.
 */
@Cancelable
public class ItemExpireEvent extends ItemEvent
{

    public int extraLife;

    /**
     * Creates a new event for an expiring EntityItem.
     * 
     * @param entityItem The EntityItem being deleted.
     * @param extraLife The amount of time to be added to this entities lifespan if the event is canceled.
     */
    public ItemExpireEvent(EntityItem entityItem, int extraLife)
    {
        super(entityItem);
        this.extraLife = extraLife;
    }
}