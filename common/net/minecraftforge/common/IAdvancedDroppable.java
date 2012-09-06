package net.minecraftforge.common;

import net.minecraft.src.Entity;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public interface IAdvancedDroppable {

	public Entity createEntity(World world, Entity location, ItemStack itemstack);
}
