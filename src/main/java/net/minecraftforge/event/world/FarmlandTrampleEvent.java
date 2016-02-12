package net.minecraftforge.event.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

/**
 * Called just before farmland would change to dirt.
 * If the event is cancelled, the farmland (and plant) will remain intact.
 * <br>
 * This event is {@link Cancelable}.<br>
 * This event does not use {@link HasResult}.<br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 * 
 * @param pos The position of the farmland block
 * @param entity The entity that trampled the farmland
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
