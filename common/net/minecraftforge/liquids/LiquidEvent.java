package net.minecraftforge.liquids;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event;

public class LiquidEvent extends Event {
    public final LiquidStack liquid;
    public final int x;
    public final int y;
    public final int z;
    public final World world;

    public LiquidEvent(LiquidStack liquid, World world, int x, int y, int z)
    {
        this.liquid = liquid;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Mods should fire this event when they move liquids around (pipe networks etc)
     *
     * @author cpw
     *
     */
    public static class LiquidMotionEvent extends LiquidEvent
    {
        public LiquidMotionEvent(LiquidStack liquid, World world, int x, int y, int z)
        {
            super(liquid, world, x, y, z);
        }
    }

    /**
     * Mods should fire this event when a liquid is {@link ILiquidTank#fill(LiquidStack, boolean)} their tank implementation.
     * {@link LiquidTank} does.
     *
     * @author cpw
     *
     */
    public static class LiquidFillingEvent extends LiquidEvent
    {
        public final ILiquidTank tank;

        public LiquidFillingEvent(LiquidStack liquid, World world, int x, int y, int z, ILiquidTank tank)
        {
            super(liquid, world, x, y, z);
            this.tank = tank;
        }
    }

    /**
     * Mods should fire this event when a liquid is {@link ILiquidTank#drain(int, boolean)} from their tank.
     * @author cpw
     *
     */
    public static class LiquidDrainingEvent extends LiquidEvent
    {
        public final ILiquidTank tank;

        public LiquidDrainingEvent(LiquidStack liquid, World world, int x, int y, int z, ILiquidTank tank)
        {
            super(liquid, world, x, y, z);
            this.tank = tank;
        }
    }


    /**
     * Mods should fire this event when a liquid "spills", for example, if a block containing liquid is broken.
     *
     * @author cpw
     *
     */
    public static class LiquidSpilledEvent extends LiquidEvent
    {
        public LiquidSpilledEvent(LiquidStack liquid, World world, int x, int y, int z)
        {
            super(liquid, world, x, y, z);
        }
    }

    /**
     * A handy shortcut for firing the various liquid events
     *
     * @param event
     */
    public static final void fireEvent(LiquidEvent event)
    {
        MinecraftForge.EVENT_BUS.post(event);
    }
}
