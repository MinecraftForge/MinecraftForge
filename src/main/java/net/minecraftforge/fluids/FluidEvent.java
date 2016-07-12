
package net.minecraftforge.fluids;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public class FluidEvent extends Event
{
    private final FluidStack fluid;
    private final World world;
    private final BlockPos pos;

    public FluidEvent(FluidStack fluid, World world, BlockPos pos)
    {
        this.fluid = fluid;
        this.world = world;
        this.pos = pos;
    }

    public FluidStack getFluid()
    {
        return fluid;
    }

    public World getWorld()
    {
        return world;
    }

    public BlockPos getPos()
    {
        return pos;
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
        private final IFluidTank tank;
        private final int amount;

        public FluidFillingEvent(FluidStack fluid, World world, BlockPos pos, IFluidTank tank, int amount)
        {
            super(fluid, world, pos);
            this.tank = tank;
            this.amount = amount;
        }

        public IFluidTank getTank()
        {
            return tank;
        }

        public int getAmount()
        {
            return amount;
        }
    }

    /**
     * Mods should fire this event when a fluid is {@link IFluidTank#drain(int, boolean)} from their
     * tank.
     *
     */
    public static class FluidDrainingEvent extends FluidEvent
    {
        private final IFluidTank tank;
        private final int amount;

        public FluidDrainingEvent(FluidStack fluid, World world, BlockPos pos, IFluidTank tank, int amount)
        {
            super(fluid, world, pos);
            this.amount = amount;
            this.tank = tank;
        }

        public IFluidTank getTank()
        {
            return tank;
        }

        public int getAmount()
        {
            return amount;
        }
    }

    /**
     * Mods should fire this event when two different fluids are being {@link IFluidTank#fill(FluidStack, boolean)}
     * or {@link IFluidHandler#drain(ForgeDirection, FluidStack, boolean)}.
     * {@link FluidTank} and {@link TileFluidHandler} does this.
     *
     */
    @Cancelable
    public static class TankMixingEvent extends FluidEvent
    {
        public final IFluidTank tank;

        public TankMixingEvent(FluidStack source, World world, int x, int y, int z, IFluidTank tank)
        {
            super(source, world, x, y, z);
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
    public static final boolean fireEvent(FluidEvent event)
    {
        return MinecraftForge.EVENT_BUS.post(event);
    }
}
