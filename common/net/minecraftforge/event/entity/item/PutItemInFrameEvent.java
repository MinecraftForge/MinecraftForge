package net.minecraftforge.event.entity.item;

import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.entity.EntityEvent;

/**
 * Called right before an item is placed in an item frame. Cancel to prevent further processing (prevent the player from putting the item in).
 */
@Cancelable
public class PutItemInFrameEvent extends EntityEvent
{

    public ItemStack item;
    public EntityPlayer player;

    /**
     * Creates a new event for putting an item in an item frame.
     *
     * @param frame The EntityItemFrame instance.
     * @param item The item stack that is about to be put in.
     * @param player The player who tried to put the item in.
     */
    public PutItemInFrameEvent(EntityItemFrame frame, ItemStack item, EntityPlayer player)
    {
        super(frame);
        this.item = item;
        this.player = player;
    }
}
