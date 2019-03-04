/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.common.util.math;

import com.google.common.base.MoreObjects;

public final class Dimension
{
    public final int width;
    public final int height;

    public Dimension(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Dimension)
        {
            Dimension other = (Dimension)obj;
            return (width == other.width) && (height == other.height);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        int hash = 17;
        hash = hash * 31 + width;
        hash = hash * 31 + height;
        return hash;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
            .add("width", width)
            .add("height", height)
            .toString();
    }
}
