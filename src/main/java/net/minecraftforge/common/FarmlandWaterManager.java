package net.minecraftforge.common;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.SimpleTicket;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class FarmlandWaterManager
{
    private static final Int2ObjectMap<Set<SimpleTicket<AxisAlignedBB>>> wateredAABBs = new Int2ObjectOpenHashMap<>();

    /**
     * An overload of {@link #addWateredRegion(World, AxisAlignedBB, int)} with a tickTimeout of 20 ticks.
     * <br>
     * If your ticket should stay valid for a longer time, either call {@link SimpleTicket#validate()},
     * or use the function with the int param if you know how long it is valid.
     */
    public static SimpleTicket<AxisAlignedBB> addWateredRegion(World world, AxisAlignedBB aabb)
    {
        return addWateredRegion(world, aabb, 20);
    }

    /**
     * Marks a region in the world as watered so blocks like Farmland know if there is a modded water source.
     * <br>
     * If you don't want to water the region anymore, call {@link SimpleTicket#invalidate()} so it doesn't have to wait until the tick timer runs out.
     * <br>
     * The AABB in the ticket is mutable, so if you need to update it, don't create a new ticket but update the existing one using {@link SimpleTicket#setTarget(Object)}.
     * @param world The world where the region should be marked
     * @param aabb The region where blocks should be watered
     * @param tickTimeout The total time in ticks this ticket should stay valid. The tick timer can be reset using {@link SimpleTicket#validate()}. Should be at least 1 tick
     * @return The ticket for your requested region.
     */
    public static SimpleTicket<AxisAlignedBB> addWateredRegion(World world, AxisAlignedBB aabb, int tickTimeout)
    {
        if (world.isRemote)
        {
            throw new IllegalArgumentException("Water region is only determined server-side");
        }
        Set<SimpleTicket<AxisAlignedBB>> tickets = wateredAABBs.computeIfAbsent(world.provider.getDimension(), id -> new HashSet<>());
        SimpleTicket<AxisAlignedBB> ticket = new SimpleTicket<>(aabb, tickets, tickTimeout);
        ticket.validate();
        return ticket;
    }

    /**
     * Tests if a block is in a region that is watered by blocks. This does not check vanilla water, see {@link net.minecraft.block.BlockFarmland#hasWater(World, BlockPos)}
     * @return true if there is a ticket with an AABB that includes your block
     */
    public static boolean hasBlockWaterTicket(World world, BlockPos pos)
    {
        if (world.isRemote)
        {
            throw new IllegalArgumentException("Water region is only determined server-side");
        }
        Set<SimpleTicket<AxisAlignedBB>> tickets = wateredAABBs.get(world.provider.getDimension());
        if (tickets == null)
        {
            return false;
        }

        Vec3d posAsVec3d = new Vec3d(pos);
        return tickets.stream().anyMatch(ticket -> ticket.getTarget().contains(posAsVec3d));
    }

    static void removeAllTickets(World world)
    {
        if (world.isRemote)
        {
            throw new IllegalArgumentException("Water region is only determined server-side!");
        }
        Set<SimpleTicket<AxisAlignedBB>> tickets = wateredAABBs.get(world.provider.getDimension());
        if (tickets != null)
        {
            SimpleTicket.invalidateAll(tickets);
        }
    }

    static void tick()
    {
        for (Set<SimpleTicket<AxisAlignedBB>> set : wateredAABBs.values())
        {
            Iterator<SimpleTicket<AxisAlignedBB>> ticketIterator = set.iterator();
            while (ticketIterator.hasNext())
            {
                SimpleTicket<AxisAlignedBB> next = ticketIterator.next();
                if (next.tick())
                {
                    ticketIterator.remove();
                }
            }
        }
    }
}
