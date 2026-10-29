/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.ticket;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/// @deprecated This class is scheduled for removal in 26.4 due to a lack of adoption by mods, in order to ease porting.
///             Please reach out to the Forge team if you are relying on this and would like to see it remain in Forge.
///             We may keep it or add it back in a future version if there is enough demand.
@Deprecated(forRemoval = true, since = "26.4")
public class AABBTicket extends SimpleTicket<Vec3>
{
    @NotNull
    public final AABB axisAlignedBB;

    public AABBTicket(@NotNull AABB axisAlignedBB)
    {
        this.axisAlignedBB = axisAlignedBB;
    }

    @Override
    public boolean matches(Vec3 toMatch)
    {
        return this.axisAlignedBB.contains(toMatch);
    }
}
