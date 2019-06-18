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

package net.minecraftforge.common;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.common.ticket.AABBTicket;
import net.minecraftforge.common.ticket.ChunkTicketManager;
import net.minecraftforge.common.ticket.MultiTicketManager;
import net.minecraftforge.common.ticket.SimpleTicket;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FarmlandWaterManager
{
    private static final Int2ObjectMap<Map<ChunkPos, ChunkTicketManager<Vec3d>>> customWaterHandler = new Int2ObjectOpenHashMap<>();

    /**
     * Adds a custom ticket.
     * Use {@link #addAABBTicket(World, AxisAlignedBB)} if you just need a ticket that can water a certain area.
     * <br>
     * If you don't want to water the region anymore, call {@link SimpleTicket#invalidate()}. Also call this
     * when the region this is unloaded (e.g. your TE is unloaded or the block is removed), and validate once it is loaded
     * @param world The world where the region should be marked. Only server-side worlds are allowed
     * @param ticket Your ticket you want to have registered
     * @param chunkPoses The chunkPoses where the ticket is located
     * @return The ticket for your requested region.
     */
    @SuppressWarnings("unchecked")
    public static<T extends SimpleTicket<Vec3d>> T addCustomTicket(World world, T ticket, ChunkPos... chunkPoses)
    {
        Preconditions.checkArgument(!world.isRemote, "Water region is only determined server-side");
        Preconditions.checkArgument(chunkPoses.length > 0, "Need at least one chunk pos");
        Map<ChunkPos, ChunkTicketManager<Vec3d>> ticketMap = customWaterHandler.computeIfAbsent(world.getDimension().getType().getId(), id -> new HashMap<>());
        if (chunkPoses.length == 1)
        {
            ticket.setBackend(ticketMap.computeIfAbsent(chunkPoses[0], ChunkTicketManager::new));
        }
        else
        {
            ChunkTicketManager<Vec3d>[] tickets = new ChunkTicketManager[chunkPoses.length];
            for (int i = 0; i < chunkPoses.length; i++)
                tickets[i] = ticketMap.computeIfAbsent(chunkPoses[i], ChunkTicketManager::new);
            ticket.setBackend(new MultiTicketManager<>(tickets));
        }
        ticket.validate();
        return ticket;
    }

    /**
     * Convenience method to add a ticket that is backed by an AABB.
     * <br>
     * If you don't want to water the region anymore, call {@link SimpleTicket#invalidate()}. Also call this
     * when the region this is unloaded (e.g. your TE is unloaded or the block is removed), and validate once it is loaded
     * <br>
     * The AABB in the ticket is immutable
     * @param world The world where the region should be marked. Only server-side worlds are allowed
     * @param aabb The region where blocks should be watered
     * @return The ticket for your requested region.
     */
    public static AABBTicket addAABBTicket(World world, AxisAlignedBB aabb)
    {
        //First calculate all chunks the aabb is in
        ChunkPos leftUp = new ChunkPos(((int) aabb.minX) >> 4, ((int) aabb.minZ) >> 4);
        ChunkPos rightDown = new ChunkPos(((int) aabb.maxX) >> 4, ((int) aabb.maxZ) >> 4);
        Set<ChunkPos> posSet = new HashSet<>();
        for (int x = leftUp.x; x <= rightDown.x; x++)
        {
            for (int z = leftUp.z; z <= rightDown.z; z++)
            {
                posSet.add(new ChunkPos(x, z));
            }
        }
        return addCustomTicket(world, new AABBTicket(aabb), posSet.toArray(new ChunkPos[0]));
    }

    /**
     * Tests if a block is in a region that is watered by blocks. This does not check vanilla water, see {@link net.minecraft.block.BlockFarmland#hasWater(World, BlockPos)}
     * @return true if there is a ticket with an AABB that includes your block
     */
    public static boolean hasBlockWaterTicket(IWorldReader world, BlockPos pos)
    {
        ChunkTicketManager<Vec3d> ticketManager = getTicketManager(new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4), world);
        if (ticketManager != null)
        {
            Vec3d posAsVec3d = new Vec3d(pos);
            for (SimpleTicket<Vec3d> ticket : ticketManager.getTickets()) {
                if (ticket.matches(posAsVec3d))
                    return true;
            }
        }
        return false;
    }

    static void removeTickets(IChunk chunk)
    {
        ChunkTicketManager<Vec3d> ticketManager = getTicketManager(chunk.getPos(), chunk.getWorldForge());
        if (ticketManager != null)
        {
            for (SimpleTicket<Vec3d> ticket : ticketManager.getTickets())
            {
                ticket.invalidate();
            }
        }
    }

    private static ChunkTicketManager<Vec3d> getTicketManager(ChunkPos pos, IWorldReader world) {
        Preconditions.checkArgument(!world.isRemote(), "Water region is only determined server-side");
        Map<ChunkPos, ChunkTicketManager<Vec3d>> ticketMap = customWaterHandler.get(world.getDimension().getType().getId());
        if (ticketMap == null)
        {
            return null;
        }
        return ticketMap.get(pos);
    }
}
