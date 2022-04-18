/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

// Sorter to load nearby chunks first
public class CenterChunkPosComparator implements java.util.Comparator<ChunkPos>
{
    private int x;
    private int z;

    public CenterChunkPosComparator(ServerPlayer entityplayer)
    {
        x = (int) entityplayer.getX() >> 4;
        z = (int) entityplayer.getZ() >> 4;
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
