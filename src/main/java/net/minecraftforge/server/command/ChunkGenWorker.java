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

package net.minecraftforge.server.command;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

import net.minecraft.command.CommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.SessionLockException;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.WorldWorkerManager.IWorker;

public class ChunkGenWorker implements IWorker
{
    private final CommandSource listener;
    protected final BlockPos start;
    protected final int total;
    private final DimensionType dim;
    private final Queue<BlockPos> queue;
    private final int notificationFrequency;
    private int lastNotification = 0;
    private long lastNotifcationTime = 0;
    private int genned = 0;
    private Boolean keepingLoaded;

    public ChunkGenWorker(CommandSource listener, BlockPos start, int total, DimensionType dim, int interval)
    {
        this.listener = listener;
        this.start = start;
        this.total = total;
        this.dim  = dim;
        this.queue = buildQueue();
        this.notificationFrequency = interval != -1 ? interval : Math.max(total / 20, 100); //Every 5% or every 100, whichever is more.
        this.lastNotifcationTime = System.currentTimeMillis(); //We also notify at least once every 60 seconds, to show we haven't froze.
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

    public TextComponent getStartMessage(CommandSource sender)
    {
        return new TranslationTextComponent("commands.forge.gen.start", total, start.getX(), start.getZ(), dim);
    }

    @Override
    public boolean hasWork()
    {
        return queue.size() > 0;
    }

    @Override
    public boolean doWork()
    {
        ServerWorld world = DimensionManager.getWorld(listener.getServer(), dim, false, false);
        if (world == null)
        {
            world = DimensionManager.initWorld(listener.getServer(), dim);
            if (world == null)
            {
                listener.sendFeedback(new TranslationTextComponent("commands.forge.gen.dim_fail", dim), true);
                queue.clear();
                return false;
            }
        }

        /* TODO: Check how many things are pending save, and slow down world gen if to many
        AnvilChunkLoader loader = world.getChunkProvider().chunkLoader instanceof AnvilChunkLoader ? (AnvilChunkLoader)world.getChunkProvider().chunkLoader : null;
        if (loader != null && loader.getPendingSaveCount() > 100)
        {

            if (lastNotifcationTime < System.currentTimeMillis() - 10*1000)
            {
                listener.sendFeedback(new TranslationTextComponent("commands.forge.gen.progress", total - queue.size(), total), true);
                lastNotifcationTime = System.currentTimeMillis();
            }
            return false;
        }
        */

        BlockPos next = queue.poll();

        if (next != null)
        {
            // While we work we don't want to cause world load spam so pause unloading the world.
            if (keepingLoaded == null)
            {
                keepingLoaded = DimensionManager.keepLoaded(dim, true);
            }

            if (++lastNotification >= notificationFrequency || lastNotifcationTime < System.currentTimeMillis() - 60*1000)
            {
                listener.sendFeedback(new TranslationTextComponent("commands.forge.gen.progress", total - queue.size(), total), true);
                lastNotification = 0;
                lastNotifcationTime = System.currentTimeMillis();
            }

            int x = next.getX();
            int z = next.getZ();

            IChunk chunk = world.getChunk(x, z, ChunkStatus.FULL, false);
            /* TODO: Mark things to unload
            PlayerChunkMapEntry watchers = world.getPlayerChunkMap().getEntry(chunk.x, chunk.z);
            if (watchers == null) //If there are no players watching this, this will be null, so we can unload.
                world.getChunkProvider().queueUnload(chunk);
            */
        }

        if (queue.size() == 0)
        {
            listener.sendFeedback(new TranslationTextComponent("commands.forge.gen.complete", genned, total, dim), true);
            if (keepingLoaded != null && !keepingLoaded)
            {
                DimensionManager.keepLoaded(dim, false);
            }
            return false;
        }
        return true;
    }
}
