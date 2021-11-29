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

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraftforge.common.ForgeInternalHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * WorldEvent is fired when an event involving the world occurs.<br>
 * If a method utilizes this {@link Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * {@link #world} contains the World this event is occurring in.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
public class WorldEvent extends Event
{
    private final LevelAccessor world;

    public WorldEvent(LevelAccessor world)
    {
        this.world = world;
    }

    public LevelAccessor getWorld()
    {
        return world;
    }

    /**
     * WorldEvent.Load is fired when Minecraft loads a world.<br>
     * This event is fired when a world is loaded in
     * {@link ClientLevel#ClientLevel(ClientPacketListener, ClientLevel.ClientLevelData, ResourceKey, DimensionType, int, Supplier, LevelRenderer, boolean, long)},
     * {@code MinecraftServer#createLevels(ChunkProgressListener)}. <br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class Load extends WorldEvent
    {
        public Load(LevelAccessor world) { super(world); }
    }

    /**
     * WorldEvent.Unload is fired when Minecraft unloads a world.<br>
     * This event is fired when a world is unloaded in
     * {@link Minecraft#setLevel(ClientLevel)},
     * {@link MinecraftServer#stopServer()},,
     * {@link Minecraft#clearLevel(Screen)}
     * {@link ForgeInternalHandler#onDimensionUnload(Unload)}. <br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class Unload extends WorldEvent
    {
        public Unload(LevelAccessor world) { super(world); }
    }

    /**
     * WorldEvent.Save is fired when Minecraft saves a world.<br>
     * This event is fired when a world is saved in
     * {@link ServerLevel#save(ProgressListener, boolean, boolean)}. <br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class Save extends WorldEvent
    {
        public Save(LevelAccessor world) { super(world); }
    }

    /**
     * Called by WorldServer when it attempts to create a spawnpoint for a dimension.
     * Canceling the event will prevent the vanilla code from running.
     */
    @net.minecraftforge.eventbus.api.Cancelable
    public static class CreateSpawnPosition extends WorldEvent
    {
        private final ServerLevelData settings;
        public CreateSpawnPosition(LevelAccessor world, ServerLevelData settings)
        {
            super(world);
            this.settings = settings;
        }

        public ServerLevelData getSettings()
        {
            return settings;
        }
    }
}
