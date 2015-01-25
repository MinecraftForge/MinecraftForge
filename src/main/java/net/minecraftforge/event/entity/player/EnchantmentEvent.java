package net.minecraftforge.event.entity.player;

import java.util.List;

import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class EnchantmentEvent extends PlayerEvent
{
    public final List<EnchantmentData> enchantments;
    public ItemStack item;
    public final int levels;
	
    public EnchantmentEvent(EntityPlayer player, int levels, ItemStack item, List<EnchantmentData> enchantments)
    {
        super(player);
        this.levels = levels;
        this.item = item;
        this.enchantments = enchantments;
    }
}
