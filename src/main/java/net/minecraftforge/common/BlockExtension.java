package net.minecraftforge.common;

import net.minecraft.block.Block;

public class BlockExtension
{
    /**
     * The Block that this extension object is associated with
     */
    public final Block owner;
 
    /**
     * Determines wither or not a enderman can pickup and move this block.
     */
    public boolean carriable = false;
    
    public BlockExtension(Block owner)
    {
        if (MinecraftForge.getBlockExtension(owner, false) != null)
        {
            throw new IllegalArgumentException(owner + " already has an extension object allocated");
        }

        this.owner = owner;
    }
}
