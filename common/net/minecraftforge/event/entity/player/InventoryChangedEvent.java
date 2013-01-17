package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class InventoryChangedEvent extends PlayerEvent {
	
	public final int slot;
	public final ItemStack itemstack;
	public final Boolean isArmor;
	
	public InventoryChangedEvent(EntityPlayer par1entityplayer, int par2, ItemStack par3ItemStack) {
		super(par1entityplayer);
		this.slot = par2;
		this.itemstack = par3ItemStack;
		this.isArmor = (par2 > 35 && par2 < 40);
	}

}
