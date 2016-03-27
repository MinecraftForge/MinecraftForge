package net.minecraftforge.event.world;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;


/**
 * Called when a fire block ticks. See {@link BlockFire#updateTick}.
 *<br>
 *<br>
 * If this event is cancelled, the fire block will not do anything on update.
 * <br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * 
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 * 
 * @author Artem226
 */
@Cancelable
public class FireTickEvent extends BlockEvent {
	
	private final Random rand;

	public FireTickEvent(World world, BlockPos pos, IBlockState state, Random random) {
		super(world, pos, state);
		rand = random;
	}

	public Random getRandom()
	{
		return rand;
	}
}
