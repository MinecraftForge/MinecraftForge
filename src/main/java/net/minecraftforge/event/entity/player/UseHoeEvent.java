package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
@Event.HasResult
public class UseHoeEvent extends PlayerEvent
{
    /**
     * This event is fired when a player attempts to use a Hoe on a block, it
     * can be canceled to completely prevent any further processing.
     *
     * You can also set the result to ALLOW to mark the event as processed
     * and damage the hoe.
     *
     * setResult(ALLOW) is the same as the old setHandeled();
     */

    public final ItemStack current;
    public final World world;
    public final BlockPos pos;

    public UseHoeEvent(EntityPlayer player, ItemStack current, World world, BlockPos pos)
    {
        super(player);
        this.current = current;
        this.world = world;
        this.pos = pos;
    }
}
