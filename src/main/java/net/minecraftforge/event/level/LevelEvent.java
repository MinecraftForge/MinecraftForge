/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraftforge.common.ForgeInternalHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import net.minecraftforge.fml.LogicalSide;

/**
 * This event is fired whenever an event involving a {@link LevelAccessor} occurs.
 * <p>
 * All children of this event are fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus}.
 */
public sealed interface LevelEvent
        permits LevelEvent.Load, LevelEvent.Unload, LevelEvent.Save, LevelEvent.CreateSpawnPosition,
                LevelEvent.PotentialSpawns, ChunkEvent, SaplingGrowTreeEvent, SleepFinishedTimeEvent {
    /**
     * {@return the level this event is affecting}
     */
    LevelAccessor level();

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
    record Load(LevelAccessor level) implements LevelEvent, RecordEvent {
        public static final EventBus<LevelEvent.Load> BUS = EventBus.create(LevelEvent.Load.class);
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
    record Unload(LevelAccessor level) implements LevelEvent, RecordEvent {
        public static final EventBus<LevelEvent.Unload> BUS = EventBus.create(LevelEvent.Unload.class);
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
    record Save(LevelAccessor level) implements LevelEvent, RecordEvent {
        public static final EventBus<LevelEvent.Save> BUS = EventBus.create(LevelEvent.Save.class);
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
    record CreateSpawnPosition(LevelAccessor level, ServerLevelData settings) implements Cancellable, LevelEvent, RecordEvent {
        public static final CancellableEventBus<CreateSpawnPosition> BUS = CancellableEventBus.create(LevelEvent.CreateSpawnPosition.class);
    }

    /**
     * Fired when building a list of all possible entities that can spawn at the specified location.
     *
     * <p>If an entry is added to the list, it needs to be a globally unique instance.</p>
     *
     * The event is called in {@link net.minecraft.world.level.NaturalSpawner#mobsAt(ServerLevel,
     * StructureManager, ChunkGenerator, MobCategory, RandomSource, BlockPos)}.</p>
     * 
     * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
     * Canceling the event will result in an empty list, meaning no entity will be spawned.</p>
     */
    final class PotentialSpawns extends MutableEvent implements Cancellable, LevelEvent {
        public static final CancellableEventBus<PotentialSpawns> BUS = CancellableEventBus.create(PotentialSpawns.class);

        private final LevelAccessor level;
        private final MobCategory mobcategory;
        private final BlockPos pos;
        private final List<MobSpawnSettings.SpawnerData> list;
        private final List<MobSpawnSettings.SpawnerData> view;

        public PotentialSpawns(LevelAccessor level, MobCategory category, BlockPos pos, WeightedRandomList<MobSpawnSettings.SpawnerData> oldList) {
            this.level = level;
            this.pos = pos;
            this.mobcategory = category;
            if (!oldList.isEmpty())
                this.list = new ArrayList<>(oldList.unwrap());
            else
                this.list = new ArrayList<>();

            this.view = Collections.unmodifiableList(list);
        }

        @Override
        public LevelAccessor level() {
            return level;
        }

        /**
         * {@return the category of the mobs in the spawn list.}
         */
        public MobCategory getMobCategory()
        {
            return mobcategory;
        }

        /**
         * {@return the block position where the chosen mob will be spawned.}
         */
        public BlockPos getPos()
        {
            return pos;
        }

        /**
         * {@return the list of mobs that can potentially be spawned.}
         */
        public List<MobSpawnSettings.SpawnerData> getSpawnerDataList()
        {
            return view;
        }

        /**
         * Appends a SpawnerData entry to the spawn list.
         *
         * @param data SpawnerData entry to be appended to the spawn list.
         */
        public void addSpawnerData(MobSpawnSettings.SpawnerData data)
        {
            list.add(data);
        }

        /**
         * Removes a SpawnerData entry from the spawn list.
         *
         * @param data SpawnerData entry to be removed from the spawn list.
         *
         * {@return {@code true} if the spawn list contained the specified element.}
         */
        public boolean removeSpawnerData(MobSpawnSettings.SpawnerData data)
        {
            return list.remove(data);
        }
    }
}
