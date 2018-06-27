package net.minecraftforge.common.ticket;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import java.util.Objects;

public class AABBTicket extends SimpleTicket<Vec3d>
{
    @Nonnull
    private AxisAlignedBB axisAlignedBB;

    public AABBTicket(@Nonnull AxisAlignedBB axisAlignedBB)
    {
        setAABB(axisAlignedBB);
    }

    @Nonnull
    public AxisAlignedBB getAABB()
    {
        return this.axisAlignedBB;
    }

    public void setAABB(@Nonnull AxisAlignedBB newTarget)
    {
        this.axisAlignedBB = Objects.requireNonNull(newTarget);
    }

    @Override
    public boolean matches(Vec3d toMatch)
    {
        return this.axisAlignedBB.contains(toMatch);
    }
}
