/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

import java.util.Comparator;

// Sorter to load nearby chunks first

/// @deprecated This class is scheduled for removal in 26.4 due to a lack of adoption by mods, in order to ease porting.
///             Please reach out to the Forge team if you are relying on this and would like to see it remain in Forge.
///             We may keep it or add it back in a future version if there is enough demand.
@Deprecated(forRemoval = true, since = "26.4")
public class CenterChunkPosComparator implements Comparator<ChunkPos> {
    private final int x;
    private final int z;

    public CenterChunkPosComparator(ServerPlayer entityplayer) {
        x = (int)entityplayer.getX() >> 4;
        z = (int)entityplayer.getZ() >> 4;
    }

    @Override
    public int compare(ChunkPos a, ChunkPos b) {
        if (a.equals(b))
            return 0;

        // Subtract current position to set center point
        int ax = a.x() - this.x;
        int az = a.z() - this.z;
        int bx = b.x() - this.x;
        int bz = b.z() - this.z;
        int result = ((ax - bx) * (ax + bx)) + ((az - bz) * (az + bz));

        if (result != 0)
            return result;

        if (ax < 0) {
            if (bx < 0)
                return bz - az;
            else
                return -1;
        } else {
            if (bx < 0)
                return 1;
            else
                return az - bz;
        }
    }
}
