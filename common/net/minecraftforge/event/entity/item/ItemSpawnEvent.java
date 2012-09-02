package net.minecraftforge.event.entity.item;

import net.minecraft.src.EntityItem;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.entity.EntityEvent;

/**
 * Event that is fired whenever the World spawns an ItemEntity. Cancelling this
 * event will prevent the EntityItem from being placed in the world.
 * 
 * @author Grenadier
 */
@Cancelable
public class ItemSpawnEvent extends ItemEvent
{

    /**
     * Creates a new event for a spawning EntityItem.
     * 
     * @param entityItem
     *            The EntityItem entering the world.
     */
    public ItemSpawnEvent(EntityItem entityItem)
    {
        super(entityItem);
    }
}