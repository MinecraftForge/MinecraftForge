package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is fired when a player attempts to use a Hoe on a block, it
 * can be canceled to completely prevent any further processing.
 *
 * You can also set the result to ALLOW to mark the event as processed
 * and damage the hoe.
 *
 * setResult(ALLOW) is the same as the old setHandled();
 */
@Cancelable
@Event.HasResult
public class UseHoeEvent extends PlayerEvent
{

    private final ItemStack current;
    private final World world;
    private final BlockPos pos;

    public UseHoeEvent(EntityPlayer player, ItemStack current, World world, BlockPos pos)
    {
        super(player);
        this.current = current;
        this.world = world;
        this.pos = pos;
    }

    public ItemStack getCurrent()
    {
        return current;
    }

    public World getWorld()
    {
        return world;
    }

    public BlockPos getPos()
    {
        return pos;
    }
}
