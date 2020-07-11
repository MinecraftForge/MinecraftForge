/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityClassification;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.storage.IServerWorldInfo;
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
    private final IWorld world;

    public WorldEvent(IWorld world)
    {
        this.world = world;
    }

    public IWorld getWorld()
    {
        return world;
    }

    /**
     * WorldEvent.Load is fired when Minecraft loads a world.<br>
     * This event is fired when a world is loaded in
     * {@link WorldClient#WorldClient(NetHandlerPlayClient, WorldSettings, int, EnumDifficulty, Profiler)},
     * {@link MinecraftServer#loadAllWorlds(String, String, long, WorldType, String)},
     * {@link IntegratedServer#loadAllWorlds(String, String, long, WorldType, String)}
     * {@link DimensionManager#initDimension(int)},
     * and {@link ForgeInternalHandler#onDimensionLoad(Load)}. <br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class Load extends WorldEvent
    {
        public Load(IWorld world) { super(world); }
    }

    /**
     * WorldEvent.Unload is fired when Minecraft unloads a world.<br>
     * This event is fired when a world is unloaded in
     * {@link Minecraft#loadWorld(WorldClient, String)},
     * {@link MinecraftServer#stopServer()},
     * {@link DimensionManager#unloadWorlds()},
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
        public Unload(IWorld world) { super(world); }
    }

    /**
     * WorldEvent.Save is fired when Minecraft saves a world.<br>
     * This event is fired when a world is saved in
     * {@link WorldServer#saveAllChunks(boolean, IProgressUpdate)},
     * {@link ForgeInternalHandler#onDimensionSave(Save)}. <br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class Save extends WorldEvent
    {
        public Save(IWorld world) { super(world); }
    }

    /**
     * Called by WorldServer to gather a list of all possible entities that can spawn at the specified location.
     * If an entry is added to the list, it needs to be a globally unique instance.
     * The event is called in {@link WorldServer#getSpawnListEntryForTypeAt(EnumCreatureType, BlockPos)} as well as
     * {@link WorldServer#canCreatureTypeSpawnHere(EnumCreatureType, SpawnListEntry, BlockPos)}
     * where the latter checks for identity, meaning both events must add the same instance.
     * Canceling the event will result in a empty list, meaning no entity will be spawned.
     */
    @net.minecraftforge.eventbus.api.Cancelable
    public static class PotentialSpawns extends WorldEvent
    {
        private final EntityClassification type;
        private final BlockPos pos;
        private final List<SpawnListEntry> list;

        public PotentialSpawns(IWorld world, EntityClassification type, BlockPos pos, List<SpawnListEntry> oldList)
        {
            super(world);
            this.pos = pos;
            this.type = type;
            if (oldList != null)
                this.list = new ArrayList<SpawnListEntry>(oldList);
            else
                this.list = new ArrayList<SpawnListEntry>();
        }

        public EntityClassification getType()
        {
            return type;
        }

        public BlockPos getPos()
        {
            return pos;
        }

        public List<SpawnListEntry> getList()
        {
            return list;
        }
    }

    /**
     * Called by WorldServer when it attempts to create a spawnpoint for a dimension.
     * Canceling the event will prevent the vanilla code from running.
     */
    @net.minecraftforge.eventbus.api.Cancelable
    public static class CreateSpawnPosition extends WorldEvent
    {
        private final IServerWorldInfo settings;
        public CreateSpawnPosition(IWorld world, IServerWorldInfo settings)
        {
            super(world);
            this.settings = settings;
        }

        public IServerWorldInfo getSettings()
        {
            return settings;
        }
    }
}
