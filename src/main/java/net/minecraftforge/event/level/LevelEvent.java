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
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraftforge.common.ForgeInternalHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;

/**
 * This event is fired whenever an event involving a {@link LevelAccessor} occurs.
 * <p>
 * All children of this event are fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus}.
 */
public class LevelEvent extends Event
{
    private final LevelAccessor level;

    public LevelEvent(LevelAccessor level)
    {
        this.level = level;
    }

    /**
     * {@return the level this event is affecting}
     */
    public LevelAccessor getLevel()
    {
        return level;
    }

    /**
     * This event is fired whenever a level loads.
     * This event is fired whenever a level loads in ClientLevel's constructor and
     * {@literal MinecraftServer#createLevels(ChunkProgressListener)}.
     * <p>
     * This event is not {@linkplain Cancelable cancellable} and does not {@linkplain HasResult have a result}.
     * <p>
     * This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus}
     * on both logical sides.
     **/
    public static class Load extends LevelEvent
    {
        public Load(LevelAccessor level) { super(level); }
    }

    /**
     * This event is fired whenever a level unloads.
     * This event is fired whenever a level unloads in
     * {@link Minecraft#setLevel(ClientLevel)},
     * {@link MinecraftServer#stopServer()},
     * {@link Minecraft#clearLevel(Screen)}, and
     * {@link ForgeInternalHandler#onDimensionUnload(Unload)}.
     * <p>
     * This event is not {@linkplain Cancelable cancellable} and does not {@linkplain HasResult have a result}.
     * <p>
     * This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus}
     * on both logical sides.
     **/
    public static class Unload extends LevelEvent
    {
        public Unload(LevelAccessor level) { super(level); }
    }

    /**
     * This event fires whenever a level is saved.
     * This event is fired when a level is saved in
     * {@link ServerLevel#save(ProgressListener, boolean, boolean)}.
     * <p>
     * This event is not {@linkplain Cancelable cancellable} and does not {@linkplain HasResult have a result}.
     * <p>
     * This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus}
     * only on the {@linkplain LogicalSide#SERVER logical server}.
     **/
    public static class Save extends LevelEvent
    {
        public Save(LevelAccessor level) { super(level); }
    }

    /**
     * This event fires whenever a {@link ServerLevel} is initialized for the first time
     * and a spawn position needs to be chosen.
     * <p>
     * This event is {@linkplain Cancelable cancellable} and does not {@linkplain HasResult have a result}.
     * If the event is canceled, the vanilla logic to choose a spawn position will be skipped.
     * <p>
     * This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus}
     * only on the {@linkplain LogicalSide#SERVER logical server}.
     *
     * @see ServerLevelData#isInitialized()
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
