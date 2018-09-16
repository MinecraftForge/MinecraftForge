/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fluids;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

public class FluidEvent extends net.minecraftforge.eventbus.api.Event
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
