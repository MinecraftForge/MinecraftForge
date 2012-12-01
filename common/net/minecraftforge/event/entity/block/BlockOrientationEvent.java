package net.minecraftforge.event.entity.block;

import net.minecraft.src.EntityLiving;
import net.minecraft.src.World;
import net.minecraftforge.event.entity.EntityEvent;

public class BlockOrientationEvent extends BlockPlacementEvent
{

    public BlockOrientationEvent(World world, EntityLiving entity, int x, int y, int z)
    {
        super(world, entity, x, y, z);
    }

}
