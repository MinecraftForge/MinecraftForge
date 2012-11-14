/**
 * Copyright (c) SpaceToad, 2011
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package net.minecraftforge.liquids;

/**
 * Liquids implement this interface
 *
 */
public interface ILiquid {

	/**
	 * The itemId of the liquid item
	 * @return
	 */
	public int stillLiquidId();

	/**
	 * Is this liquid a metadata based liquid
	 * @return
	 */
	public boolean isMetaSensitive();

	/**
	 * The item metadata of the liquid
	 * @return
	 */
	public int stillLiquidMeta();
}
