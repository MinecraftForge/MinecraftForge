
package net.minecraftforge.fluids;

import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event;

public class FluidEvent extends Event
{
    public final FluidStack fluid;
    public final int x;
    public final int y;
    public final int z;
    public final World world;

    public FluidEvent(FluidStack fluid, World world, int x, int y, int z)
    {
        this.fluid = fluid;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Mods should fire this event when they move fluids around.
     * 
     * @author cpw
     * 
     */
    public static class FluidMotionEvent extends FluidEvent
    {
        public FluidMotionEvent(FluidStack fluid, World world, int x, int y, int z)
        {
            super(fluid, world, x, y, z);
        }
    }

    /**
     * Mods should fire this event when a fluid is {@link IFluidTank#fill(FluidStack, boolean)}
     * their tank implementation. {@link FluidTank} does.
     * 
     * @author cpw
     * 
     */
    public static class FluidFillingEvent extends FluidEvent
    {
        public final IFluidTank tank;
        public FluidFillingEvent(FluidStack fluid, World world, int x, int y, int z, IFluidTank tank)
        {
            super(fluid, world, x, y, z);
            this.tank = tank;
        }
    }

    /**
     * Mods should fire this event when a fluid is {@link IFluidTank#drain(int, boolean)} from their
     * tank.
     * 
     * @author cpw
     * 
     */
    public static class FluidDrainingEvent extends FluidEvent
    {
        public final IFluidTank tank;
        public FluidDrainingEvent(FluidStack fluid, World world, int x, int y, int z, IFluidTank tank)
        {
            super(fluid, world, x, y, z);
            this.tank = tank;
        }
    }

    /**
     * Mods should fire this event when a fluid "spills", for example, if a block containing fluid
     * is broken.
     * 
     * @author cpw
     * 
     */
    public static class FluidSpilledEvent extends FluidEvent
    {
        public FluidSpilledEvent(FluidStack fluid, World world, int x, int y, int z)
        {
            super(fluid, world, x, y, z);
        }
    }

    /**
     * A handy shortcut for firing the various fluid events.
     * 
     * @param event
     */
    public static final void fireEvent(FluidEvent event)
    {
        MinecraftForge.EVENT_BUS.post(event);
    }
}
