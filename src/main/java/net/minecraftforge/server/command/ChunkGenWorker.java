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
package net.minecraftforge.server.command;

import java.util.ArrayDeque;
import java.util.Queue;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.WorldWorkerManager.IWorker;

public class ChunkGenWorker implements IWorker
{
    private final ICommandSender listener;
    protected final BlockPos start;
    protected final int total;
    private final int dim;
    private final Queue<BlockPos> queue;
    private final int notificationFrequency;
    private int lastNotification = 0;
    private int genned = 0;
    private int oldUnloadDelay = -1;

    public ChunkGenWorker(ICommandSender listener, BlockPos start, int total, int dim, int interval)
    {
        this.listener = listener;
        this.start = start;
        this.total = total;
        this.dim  = dim;
        this.queue = buildQueue();
        this.notificationFrequency = interval != -1 ? interval : Math.max(total / 20, 100); //Every 5% or every 100, whichever is more.
    }

    protected Queue<BlockPos> buildQueue()
    {
        Queue<BlockPos> ret = new ArrayDeque<BlockPos>();
        ret.add(start);

        //This *should* spiral outwards, starting on right side, down, left, up, right, but hey we'll see!
        int radius = 1;
        while (ret.size() < total)
        {
            for (int q = -radius + 1; q <= radius && ret.size() < total; q++)
                ret.add(start.add(radius, 0, q));

            for (int q = radius - 1; q >= -radius && ret.size() < total; q--)
                ret.add(start.add(q, 0, radius));

            for (int q = radius - 1; q >= -radius && ret.size() < total; q--)
                ret.add(start.add(-radius, 0, q));

            for (int q = -radius + 1; q <= radius && ret.size() < total; q++)
                ret.add(start.add(q, 0, -radius));

            radius++;
        }
        return ret;
    }

    @Deprecated // TODO remove in 1.13
    public TextComponentTranslation getStartMessage()
    {
        return new TextComponentTranslation("commands.forge.gen.start", total, start.getX(), start.getZ(), dim);
    }
    
    public TextComponentBase getStartMessage(ICommandSender sender)
    {
        return TextComponentHelper.createComponentTranslation(sender, "commands.forge.gen.start", total, start.getX(), start.getZ(), dim);
    }

    @Override
    public boolean hasWork()
    {
        return queue.size() > 0;
    }

    @Override
    public void work()
    {
        BlockPos next = queue.poll();

        if (next != null)
        {
            WorldServer world = DimensionManager.getWorld(dim);
            if (world == null)
            {
                DimensionManager.initDimension(dim);
                world = DimensionManager.getWorld(dim);
                if (world == null)
                {
                    listener.sendMessage(TextComponentHelper.createComponentTranslation(listener, "commands.forge.gen.dim_fail", dim));
                    queue.clear();
                    return;
                }
            }
            //While we work we don't want to cause world load spam so pause unloading worlds.
            if (oldUnloadDelay == -1)
            {
                oldUnloadDelay = ForgeModContainer.dimensionUnloadQueueDelay;
                ForgeModContainer.dimensionUnloadQueueDelay = Integer.MAX_VALUE;
            }

            if (++lastNotification >= notificationFrequency)
            {
                listener.sendMessage(TextComponentHelper.createComponentTranslation(listener, "commands.forge.gen.progress", total - queue.size(), total));
                lastNotification = 0;
            }

            int x = next.getX();
            int z = next.getZ();

            Chunk target = world.getChunkFromChunkCoords(x, z);
            Chunk[] chunks = { target };

            if (!target.isTerrainPopulated())
            {
                // In order for a chunk to populate, The chunks around its bottom right corner need to be loaded.
                // So lets load those chunks, but this needs to be done in a certain order to make this trigger.
                // So this does load more chunks then it should, and is a hack, but lets go!.
                chunks = new Chunk[] {
                    target,
                    world.getChunkFromChunkCoords(x + 1, z),
                    world.getChunkFromChunkCoords(x + 1, z + 1),
                    world.getChunkFromChunkCoords(x,     z + 1),
                };
                genned++;
            }

            for (Chunk chunk : chunks) //Now lets unload them. Note: Saving is done off thread so there may be cache hits, but this should still unload everything.
            {
                PlayerChunkMapEntry watchers = world.getPlayerChunkMap().getEntry(chunk.x, chunk.z);
                if (watchers == null) //If there are no players watching this, this will be null, so we can unload.
                    world.getChunkProvider().queueUnload(chunk);
            }
        }

        if (queue.size() == 0)
        {
            listener.sendMessage(TextComponentHelper.createComponentTranslation(listener, "commands.forge.gen.complete", genned, total, dim));
            ForgeModContainer.dimensionUnloadQueueDelay = oldUnloadDelay;
        }
    }
}
