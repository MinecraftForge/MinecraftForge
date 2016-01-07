package net.minecraftforge.event.entity.player;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

@Cancelable
@Event.HasResult
public class BonemealEvent extends PlayerEvent
{
    /**
     * This event is called when a player attempts to use Bonemeal on a block.
     * It can be canceled to completely prevent any further processing.
     *
     * You can also set the result to ALLOW to mark the event as processed
     * and use up a bonemeal from the stack but do no further processing.
     *
     * setResult(ALLOW) is the same as the old setHandeled()
     */

    public final World world;
    public final BlockPos pos;
    public final IBlockState block;

    public BonemealEvent(EntityPlayer player, World world, BlockPos pos, IBlockState block)
    {
        super(player);
        this.world = world;
        this.pos = pos;
        this.block = block;
    }
}
