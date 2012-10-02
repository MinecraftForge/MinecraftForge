package net.minecraftforge.event.block;

import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.World;

public class BlockOrientationEvent extends BlockEvent
{
    public World world;
    public Entity entity;
    public int blockX, blockY, blockZ;
    
    public BlockOrientationEvent(Block block, World world, Entity entity, int x, int y, int z)
    {
        super(block);
        this.world = world;
        this.entity = entity;
        this.blockX = x;
        this.blockY = y;
        this.blockZ = z;
    }

}
