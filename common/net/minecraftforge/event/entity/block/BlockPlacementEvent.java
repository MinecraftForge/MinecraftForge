package net.minecraftforge.event.entity.block;

import net.minecraft.src.EntityLiving;
import net.minecraft.src.World;

public class BlockPlacementEvent extends BlockEvent
{

    public BlockPlacementEvent(World world, EntityLiving entity, int x, int y, int z)
    {
        super(world, entity, x, y, z);
    }

}
