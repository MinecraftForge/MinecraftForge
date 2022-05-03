/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.ticket;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nonnull;

public class AABBTicket extends SimpleTicket<Vector3d>
{
    @Nonnull
    public final AxisAlignedBB axisAlignedBB;

    public AABBTicket(@Nonnull AxisAlignedBB axisAlignedBB)
    {
        this.axisAlignedBB = axisAlignedBB;
    }

    @Override
    public boolean matches(Vector3d toMatch)
    {
        return this.axisAlignedBB.contains(toMatch);
    }
}
