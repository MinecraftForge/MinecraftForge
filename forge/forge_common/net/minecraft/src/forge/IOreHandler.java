/**
 * This software is provided under the terms of the Minecraft Forge Public 
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.ItemStack;

/** The current list of known classes.
 * oreTin, oreCopper, oreSilver
 * ingotTin, ingotCopper, ingotSilver, ingotBrass, ingotBronze
 * dyeBlue
 * gemRuby, gemEmerald, gemSapphire
 * woodRubber
 */

public interface IOreHandler {
	/** Called when a new ore is registered with the ore dictionary.
	 * @param oreClass The string class of the ore.
	 * @param ore The ItemStack for the ore.
	 */
	public void registerOre(String oreClass, ItemStack ore);
}
