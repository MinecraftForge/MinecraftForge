package net.minecraftforge.event.smelting;

import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

public class ItemSmeltEvent extends Event
{
    private final World world;
    private final BlockPos pos;
    private ItemStack[] stacks;

    protected ItemSmeltEvent(World world, BlockPos pos, ItemStack[] stacks)
    {
        this.world = world;
        this.pos = pos;
        this.stacks = stacks;
    }

    public World getWorld()
    {
        return world;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public ItemStack getItem(int index)
    {
        if (index >= stacks.length) return null;
        return stacks[index];
    }

    public void setItem(int index, ItemStack stack)
    {
        if (index < stacks.length)
        {
            stacks[index] = stack;
        }
    }

    public int getLength()
    {
        return stacks.length;
    }

    /**
     * ItemSmeltEvent.Pre is fired before vanilla smelting takes place.
     * All changes made to the event's array will be made to the TileEntity if the event is canceled.
     * <br>
     * The event is fired during the TileEntityFurnace#update() method invocation.<br>
     * <br>
     * {@link #stacks} contains the itemstack array from the TileEntitySmelter holding all items in Smelter.<br>
     * <br>
     * {@link #cookTime} contains the integer from the TileEntitySmelter holding processing time in Smelter.<br>
     * <br>
     * {@link #totalCookTime} contains the integer from the TileEntitySmelter holding processing total time in Smelter.<br>
     * <br>
     * This event is {@link Cancelable}.<br>
     * If the event is not canceled, cookTime is added 1.
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    @Cancelable
    public static class Pre extends ItemSmeltEvent
    {
        private int cookTime;
        private int totalCookTime;

        public Pre(World world, BlockPos pos, ItemStack[] stacks, int cookTime, int totalCookTime)
        {
            super(world, pos, stacks);
            this.cookTime = cookTime;
            this.totalCookTime = totalCookTime;
        }

        public int getCookTime()
        {
            return cookTime;
        }

        public void setCookTime(int cookTime)
        {
            this.cookTime = cookTime;
        }

        public int getTotalCookTime()
        {
            return totalCookTime;
        }

        public void setTotalCookTime(int totalCookTime)
        {
            this.totalCookTime = totalCookTime;
        }
    }

    /**
     * ItemSmeltEvent.Post is fired when a item is smelted in the furnace.
     * <br>
     * The event is fired during the TileEntityFurnace#smeltItem() method invocation.<br>
     * <br>
     * {@link #stacks} contains the itemstack array from the TileEntitySmelter holding all items in Smelter.<br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class Post extends ItemSmeltEvent
    {
        public Post(World world, BlockPos pos, ItemStack[] stacks)
        {
            super(world, pos, stacks);
        }
    }
}
