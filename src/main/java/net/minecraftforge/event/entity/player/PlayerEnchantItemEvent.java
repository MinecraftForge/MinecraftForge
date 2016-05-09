package net.minecraftforge.event.entity.player;

import java.util.List;

import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * Fired when an item is enchanted. Primarily triggered by enchanting at the
 * vanilla enchantment table, however it is also possible to be triggered by
 * other mods.
 */
@Cancelable
public class PlayerEnchantItemEvent extends PlayerEvent {

	/**
	 * The ItemStack being enchanted.
	 */
	private final ItemStack stack;

	/**
	 * The ItemStack in the Lapis Lazuli fuel slot.
	 */
	private final ItemStack fuel;
	
	/**
	 * The list of enchantments being added to the ItemStack.
	 */
	private final List<EnchantmentData> enchantments;

	/**
	 * The amount of levels being used to pay for the enchantments.
	 */
	private int levels;

	public PlayerEnchantItemEvent(EntityPlayer player, ItemStack stack, ItemStack fuel, int levels, List<EnchantmentData> enchantments) {
		super(player);
		this.stack = stack;
		this.fuel = fuel;
		this.levels = levels;
		this.enchantments = enchantments;
	}

	public ItemStack getItemStack() 
	{
		return stack;
	}
	
	public ItemStack getFuelStack()
	{
		return fuel;
	}

	public List<EnchantmentData> getEnchantments() 
	{
		return enchantments;
	}

	public int getLevels() 
	{
		return levels;
	}

	public void setLevels(int levels) 
	{
		this.levels = levels;
	}
}