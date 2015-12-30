package net.minecraftforge.event.terraingen;

import java.util.Random;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

/**
 * OreGenEvent is fired when an event involving ore generation occurs.<br>
 * If a method utilizes this {@link Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * {@link #world} contains the world this event is occurring in.<br>
 * {@link #rand} contains an instance of random that can be used in this event.<br>
 * {@link #pos} contains the coordinates of the chunk position currently being populated with ores.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#ORE_GEN_BUS}.<br>
 **/
public class OreGenEvent extends Event
{
    public final World world;
    public final Random rand;
    public final BlockPos pos;

    public OreGenEvent(World world, Random rand, BlockPos pos)
    {
        this.world = world;
        this.rand = rand;
        this.pos = pos;
    }

    /**
     * OreGenEvent.Pre is fired just before a chunk is populated with ores.<br>
     * This event is fired just before ore generation in
     * BiomeDecorator#generateOres().<br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#ORE_GEN_BUS}.<br>
     **/
    public static class Pre extends OreGenEvent
    {
        public Pre(World world, Random rand, BlockPos pos)
        {
            super(world, rand, pos);
        }
    }

    /**
     * OreGenEvent.Post is fired just after a chunk is populated with ores.<br>
     * This event is fired just after ore generation in
     * BiomeDecorator#generateOres().<br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#ORE_GEN_BUS}.<br>
     **/
    public static class Post extends OreGenEvent
    {
        public Post(World world, Random rand, BlockPos pos)
        {
            super(world, rand, pos);
        }
    }

    /**
     * GenerateMinable is fired when a mineable block is generated in a chunk.<br>
     * This event is fired just after ore generation in
     * BiomeDecorator#generateOres().<br>
     * <br>
     * {@link #type} contains the enum value for the Ore attempting to be generated.<br>
     * {@link #generator} contains the WorldGenerator generating this ore. <br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event has a result. {@link HasResult} <br>
     * This result determines whether the ore is allowed to be generated.<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#ORE_GEN_BUS}.<br>
     **/
    @HasResult
    public static class GenerateMinable extends OreGenEvent
    {
        public static enum EventType { COAL, DIAMOND, DIRT, GOLD, GRAVEL, IRON, LAPIS, REDSTONE, QUARTZ, DIORITE, GRANITE, ANDESITE, EMERALD, SILVERFISH, CUSTOM }

        public final EventType type;
        public final WorldGenerator generator;

        public GenerateMinable(World world, Random rand, WorldGenerator generator, BlockPos pos, EventType type)
        {
            super(world, rand, pos);
            this.generator = generator;
            this.type = type;
        }
    }
}
