/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.level;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraftforge.common.ForgeInternalHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * LevelEvent is fired when an event involving the level occurs.<br>
 * If a method utilizes this {@link Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * {@link #getLevel()} contains the {@link Level} this event is occurring in.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
public class LevelEvent extends Event
{
    private final LevelAccessor level;

    public LevelEvent(LevelAccessor level)
    {
        this.level = level;
    }

    public LevelAccessor getLevel()
    {
        return level;
    }

    /**
     * LevelEvent.Load is fired when Minecraft loads a level.<br>
     * This event is fired when a level is loaded in
     * {@code ClientLevel#ClientLevel(ClientPacketListener, ClientLevel.ClientLevelData, ResourceKey, DimensionType, int, int, Supplier, LevelRenderer, boolean, long)},
     * {@code MinecraftServer#createLevels(ChunkProgressListener)}. <br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class Load extends LevelEvent
    {
        public Load(LevelAccessor level) { super(level); }
    }

    /**
     * LevelEvent.Unload is fired when Minecraft unloads a level.<br>
     * This event is fired when a level is unloaded in
     * {@link Minecraft#setLevel(ClientLevel)},
     * {@link MinecraftServer#stopServer()},
     * {@link Minecraft#clearLevel(Screen)}, and
     * {@link ForgeInternalHandler#onDimensionUnload(Unload)}. <br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class Unload extends LevelEvent
    {
        public Unload(LevelAccessor level) { super(level); }
    }

    /**
     * LevelEvent.Save is fired when Minecraft saves a level.<br>
     * This event is fired when a level is saved in
     * {@link ServerLevel#save(ProgressListener, boolean, boolean)}. <br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class Save extends LevelEvent
    {
        public Save(LevelAccessor level) { super(level); }
    }

    /**
     * Called by ServerLevel when it attempts to create a spawnpoint for a dimension.
     * Canceling the event will prevent the vanilla code from running.
     */
    @Cancelable
    public static class CreateSpawnPosition extends LevelEvent
    {
        private final ServerLevelData settings;
        public CreateSpawnPosition(LevelAccessor level, ServerLevelData settings)
        {
            super(level);
            this.settings = settings;
        }

        public ServerLevelData getSettings()
        {
            return settings;
        }
    }
}
