/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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
