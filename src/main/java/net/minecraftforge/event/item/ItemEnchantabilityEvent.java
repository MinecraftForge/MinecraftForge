package net.minecraftforge.event.item;

import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.eventhandler.Event;

/**
 * Fired when getting the "enchantability" value of an ItemStack.
 *
 * stack contains the ItemStack in question.
 *
 * enchantability is the value supplied from the Item.getEnchantability() for the Item.
 */

public class ItemEnchantabilityEvent extends Event
{
    public final ItemStack stack;
    public int enchantability;
    
    public ItemEnchantabilityEvent(ItemStack stack, int enchantability)
    {
        this.stack = stack;
        this.enchantability = enchantability;
    }
}
