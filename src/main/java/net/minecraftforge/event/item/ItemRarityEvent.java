package net.minecraftforge.event.item;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.eventhandler.Event;

/**
 * Fired when getting the "rarity" value of an ItemStack.
 *
 * stack contains the ItemStack in question.
 *
 * rarity is the value supplied from the Item.getRarity() for the Item.
 */

public class ItemRarityEvent extends Event
{
    public final ItemStack stack;
    public EnumRarity rarity;
    
    public ItemRarityEvent(ItemStack item, EnumRarity rarity)
    {
        this.stack = item;
        this.rarity = rarity;
    }
}
