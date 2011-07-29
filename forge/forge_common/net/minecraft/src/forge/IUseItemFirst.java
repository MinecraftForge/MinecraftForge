package net.minecraft.src.forge;
import net.minecraft.src.*;

public interface IUseItemFirst {
	public boolean onItemUseFirst(ItemStack ist,
			EntityPlayer player, World world,
			int i, int j, int k, int l);
}
