/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.event.world;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

/**
 * ChunkWatchEvent is fired when an event involving a chunk being watched occurs.<br>
 * If a method utilizes this {@link Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * {@link #pos} contains the ChunkPos of the Chunk this event is affecting.<br>
 * {@link #world} contains the World of the Chunk this event is affecting.<br>
 * {@link #player} contains the EntityPlayer that is involved with this chunk being watched. <br>
 * <br>
 * The {@link #player}'s world may not be the same as the world of the chunk
 * when the player is teleporting to another dimension.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
public class ChunkWatchEvent extends Event
{
    private final ServerLevel world;
    private final ServerPlayer player;
    private final ChunkPos pos;

    public ChunkWatchEvent(ServerPlayer player, ChunkPos pos, ServerLevel world)
    {
        this.player = player;
        this.pos = pos;
        this.world = world;
    }

    public ServerPlayer getPlayer()
    {
        return this.player;
    }

    public ChunkPos getPos()
    {
        return this.pos;
    }

    public ServerLevel getWorld()
    {
        return this.world;
    }

    /**
     * ChunkWatchEvent.Watch is fired when an EntityPlayer begins watching a chunk.<br>
     * This event is fired when a chunk is added to the watched chunks of an EntityPlayer in
     * {@code net.minecraft.server.level.ChunkMap#updateChunkTracking(ServerPlayer, ChunkPos, Packet[], boolean, boolean)}. <br>
     * <br>
     * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class Watch extends ChunkWatchEvent
    {
        public Watch(ServerPlayer player, ChunkPos pos, ServerLevel world) {super(player, pos, world);}
    }

    /**
     * ChunkWatchEvent.UnWatch is fired when an EntityPlayer stops watching a chunk.<br>
     * This event is fired when a chunk is removed from the watched chunks of an EntityPlayer in
     * {@code net.minecraft.server.level.ChunkMap#updateChunkTracking(ServerPlayer, ChunkPos, Packet[], boolean, boolean)}. <br>
     * <br>
     * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class UnWatch extends ChunkWatchEvent
    {
        public UnWatch(ServerPlayer player, ChunkPos pos, ServerLevel world) {super(player, pos, world);}
    }
}
