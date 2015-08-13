package net.minecraftforge.event.terraingen;

import java.util.Random;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

/**
 * PopulateChunkEvent is fired when an event involving chunk terrain feature population occurs.<br>
 * If a method utilizes this {@link Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * {@link #world} contains the world this event is occurring in.<br>
 * {@link #rand} contains an instance of random that can be used in this event.<br>
 * {@link #chunkX} contains the x-coordinate of the chunk currently being populated with a terrain feature.<br>
 * {@link #chunkZ} contains the z-coordinate of the chunk currently being populated with ores.<br>
 * {@link #hasVillageGenerated} contains the boolean value stating if the chunk already has a village spawned in it.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}, except {@link Populate}, which fires on the {@link MinecraftForge#TERRAIN_GEN_BUS}.<br>
 **/
public class PopulateChunkEvent extends ChunkProviderEvent
{
    public final World world;
    public final Random rand;
    public final int chunkX;
    public final int chunkZ;
    public final boolean hasVillageGenerated;

    public PopulateChunkEvent(IChunkProvider chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated)
    {
        super(chunkProvider);
        this.world = world;
        this.rand = rand;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.hasVillageGenerated = hasVillageGenerated;
    }

    /**
     * PopulateChunkEvent.Pre is fired just before a chunk is populated a terrain feature.<br>
     * This event is fired just before terrain feature generation in
     * ChunkProviderEnd#populate(IChunkProvider, int, int),
     * ChunkProviderGenerate#populate(IChunkProvider, int, int),
     * and ChunkProviderHell#populate(IChunkProvider, int, int). <br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class Pre extends PopulateChunkEvent
    {
        public Pre(IChunkProvider chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated)
        {
            super(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated);
        }
    }

    /**
     * PopulateChunkEvent.Post is fired just after a chunk is populated with a terrain feature.<br>
     * This event is fired just after terrain feature generation in
     * ChunkProviderEnd#populate(IChunkProvider, int, int),
     * ChunkProviderGenerate#populate(IChunkProvider, int, int),
     * and ChunkProviderHell#populate(IChunkProvider, int, int). <br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class Post extends PopulateChunkEvent
    {
        public Post(IChunkProvider chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated)
        {
            super(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated);
        }
    }

    /**
     * PopulateChunkEvent.Populate is fired when a chunk is populated with a terrain feature.<br>
     * This event is fired during terrain feature generation in
     * ChunkProviderEnd#populate(IChunkProvider, int, int),
     * ChunkProviderGenerate#populate(IChunkProvider, int, int),
     * and ChunkProviderHell#populate(IChunkProvider, int, int). <br>
     * <br>
     * {@link #type} contains the enum value for the terrain feature being generated. <br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event has a result. {@link HasResult} <br>
     * This result determines if the chunk is populated with the terrain feature. <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    @HasResult
    public static class Populate extends PopulateChunkEvent
    {
        /** Use CUSTOM to filter custom event types
         */
        public static enum EventType { DUNGEON, FIRE, GLOWSTONE, ICE, LAKE, LAVA, NETHER_LAVA, NETHER_LAVA2, ANIMALS, CUSTOM }

        public final EventType type;

        public Populate(IChunkProvider chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated, EventType type)
        {
            super(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated);
            this.type = type;
        }
    }
}
