package net.minecraftforge.event.entity.block;

import net.minecraft.src.EntityLiving;
import net.minecraft.src.World;
import net.minecraftforge.event.entity.living.LivingEvent;

public class BlockEvent extends LivingEvent
{
    public World world;
    public int blockX, blockY, blockZ;
    
    public BlockEvent(World world, EntityLiving entity, int x, int y, int z) {
        super(entity);
        this.world = world;
        this.blockX = x;
        this.blockY = y;
        this.blockZ = z;
    }
}
