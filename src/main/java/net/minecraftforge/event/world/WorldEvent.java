package net.minecraftforge.event.world;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

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
    private final World world;

    public WorldEvent(World world)
    {
        this.world = world;
    }

    public World getWorld()
    {
        return world;
    }

    /**
     * WorldEvent.Load is fired when Minecraft loads a world.<br>
     * This event is fired when a world is loaded in
     * WorldClient#WorldClient(NetHandlerPlayClient, WorldSettings, int, EnumDifficulty, Profiler),
     * MinecraftServer#loadAllWorlds(String, String, long, WorldType, String),
     * DimensionManager#initDimension(int),
     * and ForgeInternalHandler#onDimensionLoad(Load). <br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class Load extends WorldEvent
    {
        public Load(World world) { super(world); }
    }

    /**
     * WorldEvent.Unload is fired when Minecraft unloads a world.<br>
     * This event is fired when a world is unloaded in
     * Minecraft#loadWorld(WorldClient, String),
     * MinecraftServer#deleteWorldAndStopServer(),
     * MinecraftServer#stopServer(),
     * DimensionManager#unloadWorlds(Hashtable<Integer, long[]>),
     * ForgeInternalHandler#onDimensionUnload(Unload). <br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class Unload extends WorldEvent
    {
        public Unload(World world) { super(world); }
    }

    /**
     * WorldEvent.Save is fired when Minecraft saves a world.<br>
     * This event is fired when a world is saved in
     * WorldServer#saveAllChunks(boolean, IProgressUpdate),
     * ForgeInternalHandler#onDimensionSave(Save). <br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class Save extends WorldEvent
    {
        public Save(World world) { super(world); }
    }

    /**
     * Called by WorldServer to gather a list of all possible entities that can spawn at the specified location.
     * If an entry is added to the list, it needs to be a globally unique instance.
     * The event is called in WorldServer#getSpawnListEntryForTypeAt(EnumCreatureType, BlockPos) as well as
     * WorldServer#canCreatureTypeSpawnHere(EnumCreatureType creatureType, BiomeGenBase.SpawnListEntry spawnListEntry, BlockPos pos)
     * where the latter checks for identity, meaning both events must add the same instance.
     * Canceling the event will result in a empty list, meaning no entity will be spawned.
     */
    @Cancelable
    public static class PotentialSpawns extends WorldEvent
    {
        private final EnumCreatureType type;
        private final BlockPos pos;
        private final List<SpawnListEntry> list;

        public PotentialSpawns(World world, EnumCreatureType type, BlockPos pos, List<SpawnListEntry> oldList)
        {
            super(world);
            this.pos = pos;
            this.type = type;
            if (oldList != null)
            {
                this.list = new ArrayList<SpawnListEntry>(oldList);
            }
            else
            {
                this.list = new ArrayList<SpawnListEntry>();
            }
        }

        public EnumCreatureType getType()
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
    @Cancelable
    public static class CreateSpawnPosition extends WorldEvent
    {
        private final WorldSettings settings;
        public CreateSpawnPosition(World world, WorldSettings settings)
        {
            super(world);
            this.settings = settings;
        }

        public WorldSettings getSettings()
        {
            return settings;
        }
    }
}
