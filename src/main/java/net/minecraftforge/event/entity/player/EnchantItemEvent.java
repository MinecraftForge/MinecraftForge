package net.minecraftforge.event.entity.player;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Fired when the player enchants an item with an enchanting table.
 * This only fires server-side.
 * It does not fire on /enchant. Use command events for that.
 */
@Cancelable 
public class EnchantItemEvent extends PlayerEvent
{
    /**
     * This is the ItemStack the player enchanted. Do not modify it, so other mods may access it. 
     */
    public final ItemStack input;
    /**
     * This is the ItemStack that resulted from enchanting. You may change it.
     */
    public ItemStack output;
    /**
     * This is the slot from which the enchantment resulted. 0 is top, 1 is middle, and 2 is bottom.
     */
    public final int slot;
    /**
     * This is the number of levels the player spent to enchant the item. (Or didn't spend, if in Creative.)
     */
    public final int level;
    
    public EnchantItemEvent(EntityPlayer player, ItemStack input, ItemStack output, int slot, int level)
    {
        super(player);
        this.input = input;
        this.output = output;
        this.slot = slot;
        this.level = level;
    }
}

