package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import javax.annotation.Nonnull;

/**
 * This event is called when a player clicks on an item in a container.
 * The event can be canceled, and no further processing will be done.
 *
 * This event is fired in {@link net.minecraft.inventory.Slot#canTakeStack(EntityPlayer)} and {@link net.minecraftforge.items.SlotItemHandler#canTakeStack(EntityPlayer)} when a player clicks on an item.
 */
@Cancelable
@Event.HasResult
public class CanTakeItemEvent extends PlayerEvent
{
    private final ItemStack itemStack;

    public CanTakeItemEvent(@Nonnull ItemStack itemStack, EntityPlayer entityPlayer) {
        super(entityPlayer);
        this.itemStack = itemStack;
    }

    /**
     * The {@link ItemStack} being moved.
     */
    @Nonnull
    public ItemStack getItemStack()
    {
        return itemStack;
    }
}
