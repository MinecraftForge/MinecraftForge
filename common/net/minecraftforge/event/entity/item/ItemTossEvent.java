package net.minecraftforge.event.entity.item;

import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.entity.EntityEvent;

/**
 * Event that is fired whenever a player tosses (Q) an item or drag-n-drops a
 * stack of items outside the inventory GUI screens. Cancelling the event will
 * stop the items from entering the world, but will not prevent them being
 * removed from the inventory - and thus removed from the system.
 * 
 * @author Grenadier
 */
@Cancelable
public class ItemTossEvent extends ItemSpawnEvent
{

    /**
     * The player tossing the item.
     */
    public final EntityPlayer Player;

    /**
     * Creates a new event for EntityItems tossed by a player.
     * 
     * @param eItem
     *            The EntityItem being tossed.
     * @param player
     *            The player tossing the item.
     */
    public ItemTossEvent(EntityItem eItem, EntityPlayer player)
    {
        super(eItem);
        this.Player = player;
    }
}