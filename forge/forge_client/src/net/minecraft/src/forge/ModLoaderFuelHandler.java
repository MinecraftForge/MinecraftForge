/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;

public class ModLoaderFuelHandler implements IFuelHandler
{
	@Override
	public int onUseFuel(ItemStack stack)
	{
		return ModLoader.addAllFuel(stack.itemID, stack.getItemDamage());
	}
	
	static
	{
		MinecraftForge.registerFuelHandler(new ModLoaderFuelHandler());
	}
}
