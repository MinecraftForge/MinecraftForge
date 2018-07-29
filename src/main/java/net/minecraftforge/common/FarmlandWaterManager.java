/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ticket.AABBTicket;
import net.minecraftforge.common.ticket.SimpleTicket;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class FarmlandWaterManager
{
    private static final Int2ObjectMap<Set<SimpleTicket<Vec3d>>> customWaterHandler = new Int2ObjectOpenHashMap<>();

    /**
     * Adds a custom ticket.
     * Use {@link #addAABBTicket(World, AxisAlignedBB)} if you just need a ticket that can water a certain area.
     * <br>
     * If you don't want to water the region anymore, call {@link SimpleTicket#invalidate()}. Also call this
     * when the region this is unloaded (e.g. your TE is unloaded or the block is removed), and validate once it is loaded
     * @param world The world where the region should be marked. Only server-side worlds are allowed
     * @param ticket Your ticket you want to have registered
     * @return The ticket for your requested region.
     */
    public static<T extends SimpleTicket<Vec3d>> T addCustomTicket(World world, T ticket)
    {
        Preconditions.checkArgument(!world.isRemote, "Water region is only determined server-side");
        Set<SimpleTicket<Vec3d>> tickets = customWaterHandler.computeIfAbsent(world.provider.getDimension(), id -> Collections.newSetFromMap(new WeakHashMap<>()));
        ticket.setBackend(tickets);
        ticket.validate();
        return ticket;
    }

    /**
     * Convenience method to add a ticket that is backed by an AABB.
     * <br>
     * If you don't want to water the region anymore, call {@link SimpleTicket#invalidate()}. Also call this
     * when the region this is unloaded (e.g. your TE is unloaded or the block is removed), and validate once it is loaded
     * <br>
     * The AABB in the ticket is mutable, so if you need to update it, don't create a new ticket but update the existing one using {@link AABBTicket#setAABB(AxisAlignedBB)}.
     * @param world The world where the region should be marked. Only server-side worlds are allowed
     * @param aabb The region where blocks should be watered
     * @return The ticket for your requested region.
     */
    public static AABBTicket addAABBTicket(World world, AxisAlignedBB aabb)
    {
        return addCustomTicket(world, new AABBTicket(aabb));
    }

    /**
     * Tests if a block is in a region that is watered by blocks. This does not check vanilla water, see {@link net.minecraft.block.BlockFarmland#hasWater(World, BlockPos)}
     * @return true if there is a ticket with an AABB that includes your block
     */
    public static boolean hasBlockWaterTicket(World world, BlockPos pos)
    {
        Preconditions.checkArgument(!world.isRemote, "Water region is only determined server-side");
        Set<SimpleTicket<Vec3d>> tickets = customWaterHandler.get(world.provider.getDimension());
        if (tickets == null)
        {
            return false;
        }

        Vec3d posAsVec3d = new Vec3d(pos);
        return tickets.stream().anyMatch(ticket -> ticket.matches(posAsVec3d));
    }

    static void removeAllTickets(World world)
    {
        Preconditions.checkArgument(!world.isRemote, "Water region is only determined server-side");
        Set<SimpleTicket<Vec3d>> tickets = customWaterHandler.get(world.provider.getDimension());
        if (tickets != null)
        {
            for (SimpleTicket<Vec3d> ticket : tickets)
            {
                ticket.invalidate();
            }
        }
    }
}
