package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

/**
 * This event is fired when game checks, if sleeping player should be still considered "in bed".<br>
 * Failing this check will cause player to wake up.<br>
 *
 * This event has a result. {@link HasResult}<br>
 *
 * setResult(ALLOW) informs game that player is still "in bed"<br>
 * setResult(DEFAULT) causes game to check {@link net.minecraft.block.Block#isBed(net.minecraft.world.IBlockAccess, BlockPos, net.minecraft.entity.Entity)} instead
 */
@HasResult
public class SleepingLocationCheckEvent extends PlayerEvent
{

    public final BlockPos sleepingLocation;

    public SleepingLocationCheckEvent(EntityPlayer player, BlockPos sleepingLocation)
    {
        super(player);
        this.sleepingLocation = sleepingLocation;
    }

}
