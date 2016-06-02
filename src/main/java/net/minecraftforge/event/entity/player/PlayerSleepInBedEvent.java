package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * PlayerSleepInBedEvent is fired when a player sleeps in a bed.
 * <br>
 * This event is fired whenever a player sleeps in a bed in
 * {@link EntityPlayer#trySleep(BlockPos)}.<br>
 * <br>
 * {@link #result} contains whether the player is able to sleep. <br>
 * <br>
 * This event is not {@link net.minecraftforge.fml.common.eventhandler.Cancelable}.
 * <br>
 * This event does not have a result. {@link net.minecraftforge.fml.common.eventhandler.Event.HasResult}
 * <br>
 * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.
 **/
public class PlayerSleepInBedEvent extends PlayerEvent
{
    private SleepResult result = null;
    private final BlockPos pos;

    public PlayerSleepInBedEvent(EntityPlayer player, BlockPos pos)
    {
        super(player);
        this.pos = pos;
    }

    public SleepResult getResultStatus()
    {
        return result;
    }

    public void setResult(SleepResult result)
    {
        this.result = result;
    }

    public BlockPos getPos()
    {
        return pos;
    }
}
