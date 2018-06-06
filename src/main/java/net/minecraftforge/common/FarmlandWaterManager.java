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
     * Marks a region in the world as watered so blocks like Farmland know if there is a modded water source.
     * <br>
     * If you don't want to water the region anymore, call {@link SimpleTicket#invalidate()}. Also call this
     * when the region this is unloaded (e.g. your TE is unloaded), and validate once it is loaded
     * <br>
     * The AABB in the ticket is mutable, so if you need to update it, don't create a new ticket but update the existing one using {@link SimpleTicket#setTarget(Object)}.
     * @param world The world where the region should be marked. Only server-side worlds are allowed
     * @param aabb The region where blocks should be watered
     * @return The ticket for your requested region.
     */
    public static SimpleTicket<AxisAlignedBB> addWateredRegion(World world, AxisAlignedBB aabb)
    {
        if (world.isRemote)
        {
            throw new IllegalArgumentException("Water region is only determined server-side");
        }
        Set<SimpleTicket<AxisAlignedBB>> tickets = wateredAABBs.computeIfAbsent(world.provider.getDimension(), id -> new HashSet<>());
        SimpleTicket<AxisAlignedBB> ticket = new SimpleTicket<>(aabb, tickets);
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
}
