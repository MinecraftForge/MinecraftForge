/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;

/**
 * Custom block activation handler.  This can be used to override normal block
 * activation behavior.
 * 
 * @author Mark Lewis
 * @see MinecraftForge#registerBlockActivationHandler(Block, IBlockActivationHandler)
 */
public interface IBlockActivationHandler {

    /**
     * Activates the given block.
     * 
     * @param block the Block type being activated
     * @param world the World that contains the activated Block
     * @param blockX the X coordinate of the Block being activated
     * @param blockY the Y coordinate of the Block being activated
     * @param blockZ the Z coordinate of the Block being activated
     * @param sideHit which side was hit. If its -1 then it went the full length of
     *        the ray trace. Bottom = 0, Top = 1, East = 2, West = 3, North = 4,
     *        South = 5.
     * @param player the Player who activated the Block
     * @return true if this handler activated the Block, false otherwise
     */
    public boolean activateBlock(Block block, World world, int blockX, int blockY, int blockZ, int sideHit, EntityPlayer player);
    
}
