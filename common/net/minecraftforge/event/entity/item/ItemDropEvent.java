package net.minecraftforge.event.entity.item;

import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.entity.EntityEvent;

/**
 * Event that is fired for each item that a player (unwillingly) drops into the
 * world upon death. Cancelling this event will prevent the EntityItem from
 * spawning in the world, but the items are removed from the system if this
 * happens.
 * 
 * @author Grenadier
 */
@Cancelable
public class ItemDropEvent extends ItemSpawnEvent
{

    /**
     * The dying player. Pretty sure there's nothing to be done for him at this
     * point.
     */
    public final EntityPlayer Player;

    /**
     * Creates a new event for EntityItems dropped by a dying player.
     * 
     * @param eItem
     *            The EntityItem being dropped.
     * @param player
     *            The dying player.
     */
    public ItemDropEvent(EntityItem eItem, EntityPlayer player)
    {
        super(eItem);
        this.Player = player;
    }
}