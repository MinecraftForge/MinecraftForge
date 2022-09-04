/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.world;

import java.util.function.Supplier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.MobSpawnSettings;
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
     * {@link ClientLevel#ClientLevel(ClientPacketListener, ClientLevel.ClientLevelData, ResourceKey, DimensionType, int, int, Supplier, LevelRenderer, boolean, long)},
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
    @Cancelable
    public static class PotentialSpawns extends WorldEvent
    {
        private final MobCategory mobcategory;
        private final BlockPos pos;
        private final List<MobSpawnSettings.SpawnerData> list;
        private final List<MobSpawnSettings.SpawnerData> view;

        public PotentialSpawns(LevelAccessor level, MobCategory category, BlockPos pos, WeightedRandomList<MobSpawnSettings.SpawnerData> oldList)
        {
            super(level);
            this.pos = pos;
            this.mobcategory = category;
            if (!oldList.isEmpty())
                this.list = new ArrayList<>(oldList.unwrap());
            else
                this.list = new ArrayList<>();

            this.view = Collections.unmodifiableList(list);
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
