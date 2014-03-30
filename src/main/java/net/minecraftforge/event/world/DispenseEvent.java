package net.minecraftforge.event.world;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

public class DispenseEvent extends Event
{

    public final int x;
    public final int y;
    public final int z;
    public final World world;
    public final ItemStack itemstack;

    public DispenseEvent(int x, int y, int z, World world,ItemStack itemstack)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.itemstack = itemstack;
    }

    /**
     * Fired before a Dispenser or Dropper are triggered.
     * 
     * Gives you the Coordinates of the block,the world,and the itemstack it is about to dispene
     *  
     * It can be canceled to completely prevent the Dispenser or Dropper from firing.
     * 
     * 
     * @author chibill
     */

    @Cancelable
    public static class PreDispenserFireEvent extends DispenseEvent
    {

        public PreDispenserFireEvent(int x, int y, int z, World world, ItemStack itemstack)
        {
            super(x, y, z, world, itemstack);
        }
    }

    /**
     * Fired after a Dispenser or Dropper are triggered.
     * 
     * Gives you the Coordinates of the block,the world,and the ItemStack that was fired from the dispenser.
     * 
     * NEVER change the itemstack in during this event as it won't do anything and could cause problems.
     * 
     * @author chibill
     */

    public static class PostDispenserFireEvent extends DispenseEvent
    {

        public PostDispenserFireEvent(int x, int y, int z, World world, ItemStack itemstack)
        {
            super(x, y, z, world, itemstack);
        }
    }
}
