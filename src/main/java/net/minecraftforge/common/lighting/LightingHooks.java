/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.common.lighting;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.fml.common.FMLLog;

public class LightingHooks
{
    private static final EnumSkyBlock[] ENUM_SKY_BLOCK_VALUES = EnumSkyBlock.values();
    private static final AxisDirection[] ENUM_AXIS_DIRECTION_VALUES = AxisDirection.values();

    public static final int FLAG_COUNT = 32; //2 light types * 4 directions * 2 halves * (inwards + outwards)
    
    public static void relightSkylightColumn(final World world, final Chunk chunk, final int x, final int z, final int height1, final int height2)
    {
        final int yMin = Math.min(height1, height2);
        final int yMax = Math.max(height1, height2) - 1;

        final ExtendedBlockStorage[] sections = chunk.getBlockStorageArray();

        final int xBase = (chunk.x << 4) + x;
        final int zBase = (chunk.z << 4) + z;

        scheduleRelightChecksForColumn(world, EnumSkyBlock.SKY, xBase, zBase, yMin, yMax);

        if (sections[yMin >> 4] == Chunk.NULL_BLOCK_STORAGE && yMin > 0)
        {
            world.checkLightFor(EnumSkyBlock.SKY, new BlockPos(xBase, yMin - 1, zBase));
        }

        short emptySections = 0;
        
        for (int sec = yMax >> 4; sec >= yMin >> 4; --sec)
        {
            if (sections[sec] == Chunk.NULL_BLOCK_STORAGE)
            {
                emptySections |= 1 << sec;
            }
        }

        if (emptySections != 0)
        {
            for (final EnumFacing dir : EnumFacing.HORIZONTALS)
            {
                final int xOffset = dir.getFrontOffsetX();
                final int zOffset = dir.getFrontOffsetZ();

                final boolean neighborColumnExists =
                    (((x + xOffset) | (z + zOffset)) & 16) == 0 //Checks whether the position is at the specified border (the 16 bit is set for both 15+1 and 0-1)
                        || world.getChunkProvider().getLoadedChunk(chunk.x + xOffset, chunk.z + zOffset) != null;

                if (neighborColumnExists)
                {
                    for (int sec = yMax >> 4; sec >= yMin >> 4; --sec)
                    {
                        if ((emptySections & (1 << sec)) != 0)
                        {
                            scheduleRelightChecksForColumn(world, EnumSkyBlock.SKY, xBase + xOffset, zBase + zOffset, sec << 4, (sec << 4) + 15);
                        }
                    }
                }
                else
                {
                    flagChunkBoundaryForUpdate(chunk, emptySections, EnumSkyBlock.SKY, dir, getAxisDirection(dir, x, z), EnumBoundaryFacing.OUT);
                }
            }
        }
    }

    public static void scheduleRelightChecksForArea(final World world, final EnumSkyBlock lightType, final int xMin, final int yMin, final int zMin, final int xMax, final int yMax, final int zMax)
    {
        for (int x = xMin; x <= xMax; ++x)
        {
            for (int z = zMin; z <= zMax; ++z)
            {
                scheduleRelightChecksForColumn(world, lightType, x, z, yMin, yMax);
            }
        }
    }

    private static void scheduleRelightChecksForColumn(final World world, final EnumSkyBlock lightType, final int x, final int z, final int yMin, final int yMax)
    {
        for (int y = yMin; y <= yMax; ++y)
        {
            world.checkLightFor(lightType, new BlockPos(x, y, z));
        }
    }

    public enum EnumBoundaryFacing
    {
        IN, OUT;

        public EnumBoundaryFacing getOpposite()
        {
            return this == IN ? OUT : IN;
        }
    }

    public static void flagSecBoundaryForUpdate(final Chunk chunk, final BlockPos pos, final EnumSkyBlock lightType, final EnumFacing dir, final EnumBoundaryFacing boundaryFacing)
    {
        flagChunkBoundaryForUpdate(chunk, (short) (1 << (pos.getY() >> 4)), lightType, dir, getAxisDirection(dir, pos.getX(), pos.getZ()), boundaryFacing);
    }

    public static void flagChunkBoundaryForUpdate(final Chunk chunk, final short sectionMask, final EnumSkyBlock lightType, final EnumFacing dir, final AxisDirection axisDirection, final EnumBoundaryFacing boundaryFacing)
    {
        initNeighborLightChecks(chunk);
        chunk.neighborLightChecks[getFlagIndex(lightType, dir, axisDirection, boundaryFacing)] |= sectionMask;
        chunk.markDirty();
    }

    public static int getFlagIndex(final EnumSkyBlock lightType, final int xOffset, final int zOffset, final AxisDirection axisDirection, final EnumBoundaryFacing boundaryFacing)
    {
        return (lightType == EnumSkyBlock.BLOCK ? 0 : 16) | ((xOffset + 1) << 2) | ((zOffset + 1) << 1) | (axisDirection.getOffset() + 1) | boundaryFacing.ordinal();
    }

    public static int getFlagIndex(final EnumSkyBlock lightType, final EnumFacing dir, final AxisDirection axisDirection, final EnumBoundaryFacing boundaryFacing)
    {
        return getFlagIndex(lightType, dir.getFrontOffsetX(), dir.getFrontOffsetZ(), axisDirection, boundaryFacing);
    }

    private static AxisDirection getAxisDirection(final EnumFacing dir, final int x, final int z)
    {
        return ((dir.getAxis() == Axis.X ? z : x) & 15) < 8 ? AxisDirection.NEGATIVE : AxisDirection.POSITIVE;
    }
    
    public static void scheduleRelightChecksForChunkBoundaries(final World world, final Chunk chunk)
    {
        for (final EnumFacing dir : EnumFacing.HORIZONTALS)
        {
            final int xOffset = dir.getFrontOffsetX();
            final int zOffset = dir.getFrontOffsetZ();

            final Chunk nChunk = world.getChunkProvider().getLoadedChunk(chunk.x + xOffset, chunk.z + zOffset);

            if (nChunk == null)
            {
                continue;
            }

            for (final EnumSkyBlock lightType : ENUM_SKY_BLOCK_VALUES)
            {
                for (final AxisDirection axisDir : ENUM_AXIS_DIRECTION_VALUES)
                {
                    //Merge flags upon loading of a chunk. This ensures that all flags are always already on the IN boundary below
                    mergeFlags(lightType, chunk, nChunk, dir, axisDir);
                    mergeFlags(lightType, nChunk, chunk, dir.getOpposite(), axisDir);

                    //Check everything that might have been canceled due to this chunk not being loaded.
                    //Also, pass in chunks if already known
                    //The boundary to the neighbor chunk (both ways)
                    scheduleRelightChecksForBoundary(world, chunk, nChunk, null, lightType, xOffset, zOffset, axisDir);
                    scheduleRelightChecksForBoundary(world, nChunk, chunk, null, lightType, -xOffset, -zOffset, axisDir);
                    //The boundary to the diagonal neighbor (since the checks in that chunk were aborted if this chunk wasn't loaded, see scheduleRelightChecksForBoundary)
                    scheduleRelightChecksForBoundary(world, nChunk, null, chunk, lightType, (zOffset != 0 ? axisDir.getOffset() : 0), (xOffset != 0 ? axisDir.getOffset() : 0), dir.getAxisDirection() == AxisDirection.POSITIVE ? AxisDirection.NEGATIVE : AxisDirection.POSITIVE);
                }
            }
        }
    }

    private static void mergeFlags(final EnumSkyBlock lightType, final Chunk inChunk, final Chunk outChunk, final EnumFacing dir, final AxisDirection axisDir)
    {
        if (outChunk.neighborLightChecks == null)
        {
            return;
        }

        initNeighborLightChecks(inChunk);
        
        final int inIndex = getFlagIndex(lightType, dir, axisDir, EnumBoundaryFacing.IN);
        final int outIndex = getFlagIndex(lightType, dir.getOpposite(), axisDir, EnumBoundaryFacing.OUT);

        inChunk.neighborLightChecks[inIndex] |= outChunk.neighborLightChecks[outIndex];
        //no need to call Chunk.setModified() since checks are not deleted from outChunk
    }

    private static void scheduleRelightChecksForBoundary(final World world, final Chunk chunk, Chunk nChunk, Chunk sChunk, final EnumSkyBlock lightType, final int xOffset, final int zOffset, final AxisDirection axisDir)
    {
        if (chunk.neighborLightChecks == null)
        {
            return;
        }

        final int flagIndex = getFlagIndex(lightType, xOffset, zOffset, axisDir, EnumBoundaryFacing.IN); //OUT checks from neighbor are already merged

        final int flags = chunk.neighborLightChecks[flagIndex];

        if (flags == 0)
        {
            return;
        }

        if (nChunk == null)
        {
            nChunk = world.getChunkProvider().getLoadedChunk(chunk.x + xOffset, chunk.z + zOffset);

            if (nChunk == null)
            {
                return;
            }
        }

        if (sChunk == null)
        {
            sChunk = world.getChunkProvider().getLoadedChunk(chunk.x + (zOffset != 0 ? axisDir.getOffset() : 0), chunk.z + (xOffset != 0 ? axisDir.getOffset() : 0));

            if (sChunk == null)
            {
                return; //Cancel, since the checks in the corner columns require the corner column of sChunk
            }
        }

        final int reverseIndex = getFlagIndex(lightType, -xOffset, -zOffset, axisDir, EnumBoundaryFacing.OUT);

        chunk.neighborLightChecks[flagIndex] = 0;

        if (nChunk.neighborLightChecks != null)
        {
            nChunk.neighborLightChecks[reverseIndex] = 0; //Clear only now that it's clear that the checks are processed
        }
        
        chunk.markDirty();
        nChunk.markDirty();

        //Get the area to check
        //Start in the corner...
        int xMin = chunk.x << 4;
        int zMin = chunk.z << 4;

        //move to other side of chunk if the direction is positive
        if ((xOffset | zOffset) > 0)
        {
            xMin += 15 * xOffset;
            zMin += 15 * zOffset;
        }

        //shift to other half if necessary (shift perpendicular to dir)
        if (axisDir == AxisDirection.POSITIVE)
        {
            xMin += 8 * (zOffset & 1); //x & 1 is same as abs(x) for x=-1,0,1
            zMin += 8 * (xOffset & 1);
        }

        //get maximal values (shift perpendicular to dir)
        final int xMax = xMin + 7 * (zOffset & 1);
        final int zMax = zMin + 7 * (xOffset & 1);

        for (int y = 0; y < 16; ++y)
        {
            if ((flags & (1 << y)) != 0)
            {
                scheduleRelightChecksForArea(world, lightType, xMin, y << 4, zMin, xMax, (y << 4) + 15, zMax);
            }
        }
    }

    public static void initNeighborLightChecks(final Chunk chunk)
    {
        if (chunk.neighborLightChecks == null)
        {
            chunk.neighborLightChecks = new short[FLAG_COUNT];
        }
    }
    
    public static final String neighborLightChecksKey = "NeighborLightChecks";

    public static void writeNeighborLightChecksToNBT(final Chunk chunk, final NBTTagCompound nbt)
    {
        if (chunk.neighborLightChecks == null)
        {
            return;
        }
        
        boolean empty = true;
        final NBTTagList list = new NBTTagList();

        for (final short flags : chunk.neighborLightChecks)
        {
            list.appendTag(new NBTTagShort(flags));

            if (flags != 0)
            {
                empty = false;
            }
        }

        if (!empty)
        {
            nbt.setTag(neighborLightChecksKey, list);
        }
    }

    public static void readNeighborLightChecksFromNBT(final Chunk chunk, final NBTTagCompound nbt)
    {
        if (nbt.hasKey(neighborLightChecksKey, 9))
        {
            final NBTTagList list = nbt.getTagList(neighborLightChecksKey, 2);

            if (list.tagCount() == FLAG_COUNT)
            {
                initNeighborLightChecks(chunk);

                for (int i = 0; i < FLAG_COUNT; ++i)
                {
                    chunk.neighborLightChecks[i] = ((NBTTagShort) list.get(i)).getShort();
                }
            }
            else
            {
                FMLLog.warning("Chunk field %s had invalid length, ignoring it (chunk coordinates: %s %s)", neighborLightChecksKey, chunk.x, chunk.z);
            }
        }
    }
}
