package net.minecraftforge.event.entity.player;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

/**
 * This event is fired when game checks, if sleeping player should be still considered "in bed".<br>
 * Failing this check will cause player to wake up.<br>
 *
 * This event has a result. {@link HasResult}<br>
 *
 * setResult(ALLOW) informs game that player is still "in bed"<br>
 * setResult(DEFAULT) causes game to check {@link Block#isBed(IBlockState, IBlockAccess, BlockPos, Entity)} instead
 */
@HasResult
public class SleepingLocationCheckEvent extends PlayerEvent
{

    private final BlockPos sleepingLocation;

    public SleepingLocationCheckEvent(EntityPlayer player, BlockPos sleepingLocation)
    {
        super(player);
        this.sleepingLocation = sleepingLocation;
    }

    public BlockPos getSleepingLocation()
    {
        return sleepingLocation;
    }
}
