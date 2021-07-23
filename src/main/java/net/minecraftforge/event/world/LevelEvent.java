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

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * WorldEvent is fired when an event involving the world occurs.<br>
 * If a method utilizes this {@link Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * {@link #level} contains the World this event is occurring in.<br>
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
    public static class Load extends LevelEvent
    {
        public Load(LevelAccessor level)
        {
            super(level);
        }
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
    public static class Unload extends LevelEvent
    {
        public Unload(LevelAccessor level)
        {
            super(level);
        }
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
    public static class Save extends LevelEvent
    {
        public Save(LevelAccessor level)
        {
            super(level);
        }
    }

    /**
     * Called by WorldServer when it attempts to create a spawnpoint for a dimension.
     * Canceling the event will prevent the vanilla code from running.
     */
    @Cancelable
    public static class CreateSpawnPosition extends LevelEvent
    {
        private final ServerLevelData levelData;

        public CreateSpawnPosition(LevelAccessor level, ServerLevelData levelData)
        {
            super(level);
            this.levelData = levelData;
        }

        public ServerLevelData getLevelData()
        {
            return levelData;
        }
    }
}
