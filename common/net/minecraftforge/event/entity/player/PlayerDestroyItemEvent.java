package net.minecraftforge.event.entity.player;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;

public class PlayerDestroyItemEvent extends PlayerEvent
{
    public final ItemStack original;
    /**
     * Called when a Player used up an Item's entire durability and it just broke.
     * @param player Player using the Item
     * @param original ItemStack that just broke
     */
    public PlayerDestroyItemEvent(EntityPlayer player, ItemStack original)
    {
        super(player);
        this.original = original;
    }

}
