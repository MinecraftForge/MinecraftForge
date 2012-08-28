package net.minecraftforge.event.entity.player;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraftforge.event.Cancelable;

/**
 * Activated when a Player triggers BlockChest.onBlockActivated. Coordinates match the triggering block,
 * Event only fires when opening the chest will succeed (eg it's not blocked). Passed IInventory is the
 * Chest Inventory that has been constructed. If this event is cancelled, no chest opening will occur.
 */
@Cancelable
public class ChestActivatedEvent extends PlayerEvent
{
    public final int xCoord;
    public final int yCoord;
    public final int zCoord;
    public final IInventory targetInventory;
    public ChestActivatedEvent(EntityPlayer player, IInventory target, int x, int y, int z)
    {
        super(player);
        this.targetInventory = target;
        xCoord = x;
        yCoord = y;
        zCoord = z;
    }
}
