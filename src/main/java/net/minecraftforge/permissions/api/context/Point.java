package net.minecraftforge.permissions.api.context;

import net.minecraft.dispenser.ILocation;
import net.minecraftforge.permissions.api.context.IContext.ILocationContext;

public class Point implements ILocationContext
{
    private final double x, y, z;
    private final int dim;

    public Point(double x, double y, double z, int dim)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dim = dim;
    }

    public Point(ILocation loc)
    {
        x = loc.getX();
        y = loc.getY();
        z = loc.getZ();
        this.dim = loc.getWorld().provider.dimensionId;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public double getZ()
    {
        return z;
    }

    public int getDimensionId()
    {
        return dim;
    }
}