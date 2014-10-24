package net.minecraftforge.event.entity.player;

import java.util.List;

import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
* This event is fired when the player chooses an enchantment to apply to the stack.
* This event allows access to the enchantment being placed and access to the stack being enchantment, and the player doing the enchanting.
* If you want to cancel the event, not enchant, set the list to null.
*/
public class PlayerEnchantEvent extends PlayerEvent
{
    
    public List enchantmentDataList;
    public ItemStack stack;

    public PlayerEnchantEvent(EntityPlayer player, List enchantmentDataList, ItemStack stack)
    {
        super(player);
        this.enchantmentDataList = enchantmentDataList;
        this.stack = stack;
    }
}
