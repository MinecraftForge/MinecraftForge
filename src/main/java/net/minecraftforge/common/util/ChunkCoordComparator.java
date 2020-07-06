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

package net.minecraftforge.common.util;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.ChunkPos;

// Sorter to load nearby chunks first
public class ChunkCoordComparator implements java.util.Comparator<ChunkPos>
{
    private int x;
    private int z;

    public ChunkCoordComparator(ServerPlayerEntity entityplayer)
    {
        x = (int) entityplayer.getPosX() >> 4;
        z = (int) entityplayer.getPosZ() >> 4;
    }

    @Override
    public int compare(ChunkPos a, ChunkPos b)
    {
        if (a.equals(b))
        {
            return 0;
        }

        // Subtract current position to set center point
        int ax = a.x - this.x;
        int az = a.z - this.z;
        int bx = b.x - this.x;
        int bz = b.z - this.z;
        int result = ((ax - bx) * (ax + bx)) + ((az - bz) * (az + bz));

        if (result != 0)
        {
            return result;
        }

        if (ax < 0)
        {
            if (bx < 0)
            {
                return bz - az;
            }
            else
            {
                return -1;
            }
        }
        else
        {
            if (bx < 0)
            {
                return 1;
            }
            else
            {
                return az - bz;
            }
        }
    }
}
