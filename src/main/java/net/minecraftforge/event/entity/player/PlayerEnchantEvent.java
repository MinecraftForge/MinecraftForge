package net.minecraftforge.event.entity.player;

import java.util.List;

import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
* This event is fired when the player chooses an enchantment to apply to the itemstack.<br/>
* This event allows access to the enchantment that would be placed and access to the itemstack that would be receiving the enchantment
*/
public class PlayerEnchantEvent extends PlayerEvent
{
    
    public List<EnchantmentData> enchantmentDataList;
    public ItemStack itemstack;

    public PlayerEnchantEvent(EntityPlayer player, List<EnchantmentData> enchantmentDataList, ItemStack itemstack)
    {
        super(player);
        this.enchantmentDataList = enchantmentDataList;
        this.itemstack = itemstack;
    }
}
