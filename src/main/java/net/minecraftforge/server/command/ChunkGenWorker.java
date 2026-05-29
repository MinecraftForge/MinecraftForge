/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.server.command;

import java.util.ArrayDeque;
import java.util.Queue;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraftsingularity.common.WorldWorkerManager.IWorker;

public class ChunkGenWorker implements IWorker {
    private final CommandSourceStack listener;
    protected final BlockPos start;
    protected final int total;
    private final ServerLevel dim;
    private final Queue<BlockPos> queue;
    private final int notificationFrequency;
    private int lastNotification = 0;
    private long lastNotifcationTime = 0;
    private int genned = 0;

    public ChunkGenWorker(CommandSourceStack listener, BlockPos start, int total, ServerLevel dim, int interval) {
        this.listener = listener;
        this.start = start;
        this.total = total;
        this.dim  = dim;
        this.queue = buildQueue();
        this.notificationFrequency = interval != -1 ? interval : Math.max(total / 20, 100); //Every 5% or every 100, whichever is more.
        this.lastNotifcationTime = System.currentTimeMillis(); //We also notify at least once every 60 seconds, to show we haven't froze.
    }

    protected Queue<BlockPos> buildQueue() {
        Queue<BlockPos> ret = new ArrayDeque<BlockPos>();
        ret.add(start);

        //This *should* spiral outwards, starting on right side, down, left, up, right, but hey we'll see!
        int radius = 1;
        while (ret.size() < total) {
            for (int q = -radius + 1; q <= radius && ret.size() < total; q++)
                ret.add(start.offset(radius, 0, q));

            for (int q = radius - 1; q >= -radius && ret.size() < total; q--)
                ret.add(start.offset(q, 0, radius));

            for (int q = radius - 1; q >= -radius && ret.size() < total; q--)
                ret.add(start.offset(-radius, 0, q));

            for (int q = -radius + 1; q <= radius && ret.size() < total; q++)
                ret.add(start.offset(q, 0, -radius));

            radius++;
        }
        return ret;
    }

    public MutableComponent getStartMessage(CommandSourceStack sender) {
        return Component.translatable("commands.singularity.gen.start", total, start.getX(), start.getZ(), dim);
    }

    @Override
    public boolean hasWork() {
        return !queue.isEmpty();
    }

    @Override
    public boolean doWork() {
        /* TODO: Check how many things are pending save, and slow down world gen if to many
        AnvilChunkLoader loader = dim.getChunkProvider().chunkLoader instanceof AnvilChunkLoader ? (AnvilChunkLoader)world.getChunkProvider().chunkLoader : null;
        if (loader != null && loader.getPendingSaveCount() > 100) {
            if (lastNotifcationTime < System.currentTimeMillis() - 10*1000) {
                listener.sendFeedback(new TranslationTextComponent("commands.singularity.gen.progress", total - queue.size(), total), true);
                lastNotifcationTime = System.currentTimeMillis();
            }
            return false;
        }
        */

        BlockPos next = queue.poll();

        if (next != null) {
            if (++lastNotification >= notificationFrequency || lastNotifcationTime < System.currentTimeMillis() - 60*1000) {
                listener.sendSuccess(() -> Component.translatable("commands.singularity.gen.progress", total - queue.size(), total), true);
                lastNotification = 0;
                lastNotifcationTime = System.currentTimeMillis();
            }

            int x = next.getX();
            int z = next.getZ();

            if (!dim.hasChunk(x, z)) { //Chunk is unloaded
                ChunkAccess chunk = dim.getChunk(x, z, ChunkStatus.EMPTY, true);
                if (!chunk.getPersistedStatus().isOrAfter(ChunkStatus.FULL)) {
                    chunk = dim.getChunk(x, z, ChunkStatus.FULL);
                    genned++; //There isn't a way to check if the chunk is actually created just if it was loaded
                }
            }
        }

        if (queue.isEmpty()) {
            listener.sendSuccess(() -> Component.translatable("commands.singularity.gen.complete", genned, total, dim.dimension().identifier()), true);
            return false;
        }
        return true;
    }
}
