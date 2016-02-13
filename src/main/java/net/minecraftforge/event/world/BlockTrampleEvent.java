package net.minecraftforge.event.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

/**
 * Called just before a block (usually farmland) would change to the {@link #target} state.<br>
 * If the event is cancelled, the block state will not be updated after the event.<br>
 * <br>
 * {@link #pos} The position of the block<br>
 * {@link #entity} The entity that trampled the block<br>
 * {@link #target} The state that the block will change to after the event<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * This event does not use {@link HasResult}.<br>
 */
@Cancelable
public class BlockTrampleEvent extends BlockEvent
{
    private final EntityLivingBase entity;
    private IBlockState target;

    public BlockTrampleEvent(World world, BlockPos pos, IBlockState origin, IBlockState target,EntityLivingBase entity)
    {
        super(world, pos, origin);
        this.entity = entity;
        this.target = target;
    }

    public EntityLivingBase getEntity()
    {
    	return this.entity;
    }

    public IBlockState getTargetBlockState()
    {
    	return this.target;
    }
    
    public void setTargetBlockState(IBlockState state)
    {
    	this.target = state;
    }
}
