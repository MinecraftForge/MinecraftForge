/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.ticket;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;

public class AABBTicket extends SimpleTicket<Vec3>
{
    @Nonnull
    public final AABB axisAlignedBB;

    public AABBTicket(@Nonnull AABB axisAlignedBB)
    {
        this.axisAlignedBB = axisAlignedBB;
    }

    @Override
    public boolean matches(Vec3 toMatch)
    {
        return this.axisAlignedBB.contains(toMatch);
    }
}
