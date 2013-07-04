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
@Deprecated //See new net.minecraftforge.fluids
public interface ILiquid {

    /**
     * The itemId of the liquid item
     * @return the itemId
     */
    public int stillLiquidId();

    /**
     * Is this liquid a metadata based liquid
     * @return if this is a metadata liquid
     */
    public boolean isMetaSensitive();

    /**
     * The item metadata of the liquid
     * @return the metadata of the liquid
     */
    public int stillLiquidMeta();
}
