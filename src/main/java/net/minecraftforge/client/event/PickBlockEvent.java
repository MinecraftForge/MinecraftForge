package net.minecraftforge.client.event;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * This event is fired when a player attempts to pick a block, can be canceled or its result can be changed.
 */
@Cancelable
public class PickBlockEvent extends PlayerEvent
{
    private final IBlockState state;
    private final RayTraceResult target;
    private final World world;
    private final ItemStack originalResult;
    private ItemStack result;

    public PickBlockEvent(@Nonnull IBlockState state, @Nonnull RayTraceResult target, @Nonnull World world, @Nonnull EntityPlayer player, @Nonnull ItemStack originalResult)
    {
        super(player);
        this.state = state;
        this.target = target;
        this.world = world;
        this.originalResult = result = originalResult;
    }

    public @Nonnull IBlockState getTargtedBlockState()
    {
        return state;
    }

    public @Nonnull RayTraceResult getRayTraceResult()
    {
        return target;
    }

    public @Nonnull World getWorld()
    {
        return world;
    }

    public @Nonnull ItemStack getOriginalPickResult()
    {
        return originalResult;
    }

    public @Nonnull ItemStack getPickResult()
    {
        return result;
    }

    public void setPickResult(@Nonnull ItemStack result)
    {
        this.result = result;
    }

}
