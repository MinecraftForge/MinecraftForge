package net.minecraftforge.event.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * Called just before farmland would change to dirt.
 *
 * If the event is cancelled, the farmland (and plant) will remain intact.
 */
@Cancelable
public class FarmlandTrampleEvent extends BlockEvent
{
    public final EntityLivingBase entity;

    public FarmlandTrampleEvent(World world, BlockPos pos, IBlockState state, EntityLivingBase entity)
    {
        super(world, pos, state);
        this.entity = entity;
    }
}
