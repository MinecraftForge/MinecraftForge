
package net.minecraftforge.fluids;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public class FluidEvent extends Event
{
    public final FluidStack fluid;
    public final World world;
    public final BlockPos pos;

    public FluidEvent(FluidStack fluid, World world, BlockPos pos)
    {
        this.fluid = fluid;
        this.world = world;
        this.pos = pos;
    }

    /**
     * Mods should fire this event when they move fluids around.
     *
     */
    public static class FluidMotionEvent extends FluidEvent
    {
        public FluidMotionEvent(FluidStack fluid, World world, BlockPos pos)
        {
            super(fluid, world, pos);
        }
    }

    /**
     * Mods should fire this event when a fluid is {@link IFluidTank#fill(FluidStack, boolean)}
     * their tank implementation. {@link FluidTank} does.
     *
     */
    public static class FluidFillingEvent extends FluidEvent
    {
        public final IFluidTank tank;
        public final int amount;

        public FluidFillingEvent(FluidStack fluid, World world, BlockPos pos, IFluidTank tank, int amount)
        {
            super(fluid, world, pos);
            this.tank = tank;
            this.amount = amount;
        }
    }

    /**
     * Mods should fire this event when a fluid is {@link IFluidTank#drain(int, boolean)} from their
     * tank.
     *
     */
    public static class FluidDrainingEvent extends FluidEvent
    {
        public final IFluidTank tank;
        public final int amount;

        public FluidDrainingEvent(FluidStack fluid, World world, BlockPos pos, IFluidTank tank, int amount)
        {
            super(fluid, world, pos);
            this.amount = amount;
            this.tank = tank;
        }
    }

    /**
     * Mods should fire this event when a fluid "spills", for example, if a block containing fluid
     * is broken.
     *
     */
    public static class FluidSpilledEvent extends FluidEvent
    {
        public FluidSpilledEvent(FluidStack fluid, World world, BlockPos pos)
        {
            super(fluid, world, pos);
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
