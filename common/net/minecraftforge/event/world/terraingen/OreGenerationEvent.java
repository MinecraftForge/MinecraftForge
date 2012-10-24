package net.minecraftforge.event.world.terraingen;

import java.util.Random;

import net.minecraft.src.World;
import net.minecraft.src.WorldGenerator;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.world.WorldEvent;

public abstract class OreGenerationEvent extends WorldEvent
{

    public final Random random;
    public final WorldGenerator worldGenerator;
    public final int worldX;
    public final int worldZ;
    public int veinsToGenerate;
    public int minY;
    public int maxY;
    
    private boolean handled = false;
   
    public OreGenerationEvent(World world, Random random, int worldX, int worldZ, int veinsToGenerate, WorldGenerator worldGenerator, int minY, int maxY)
    {
        super(world);

        this.random = random;
        this.worldX = worldX;
        this.worldZ = worldZ;
        this.worldGenerator = worldGenerator;
        this.veinsToGenerate = veinsToGenerate;
        this.minY = minY;
        this.maxY = maxY;
    }

    public boolean isHandled()
    {
        return handled;
    }
    
    public void setHandled()
    {
        handled = true;
    }

    @Cancelable
    public static class DirtGenerationEvent extends OreGenerationEvent
    {

        public DirtGenerationEvent(World world, Random random, int worldX, int worldZ, int veinsToGenerate, WorldGenerator worldGenerator, int minY, int maxY)
        {
            super(world, random, worldX, worldZ, veinsToGenerate, worldGenerator, minY, maxY);
        }

    }

    @Cancelable
    public static class GravelGenerationEvent extends OreGenerationEvent
    {

        public GravelGenerationEvent(World world, Random random, int worldX, int worldZ, int veinsToGenerate, WorldGenerator worldGenerator, int minY, int maxY)
        {
            super(world, random, worldX, worldZ, veinsToGenerate, worldGenerator, minY, maxY);
        }

    }

    @Cancelable
    public static class CoalGenerationEvent extends OreGenerationEvent
    {

        public CoalGenerationEvent(World world, Random random, int worldX, int worldZ, int veinsToGenerate, WorldGenerator worldGenerator, int minY, int maxY)
        {
            super(world, random, worldX, worldZ, veinsToGenerate, worldGenerator, minY, maxY);
        }

    }

    @Cancelable
    public static class IronGenerationEvent extends OreGenerationEvent
    {

        public IronGenerationEvent(World world, Random random, int worldX, int worldZ, int veinsToGenerate, WorldGenerator worldGenerator, int minY, int maxY)
        {
            super(world, random, worldX, worldZ, veinsToGenerate, worldGenerator, minY, maxY);
        }

    }

    @Cancelable
    public static class GoldGenerationEvent extends OreGenerationEvent
    {

        public GoldGenerationEvent(World world, Random random, int worldX, int worldZ, int veinsToGenerate, WorldGenerator worldGenerator, int minY, int maxY)
        {
            super(world, random, worldX, worldZ, veinsToGenerate, worldGenerator, minY, maxY);
        }

    }

    @Cancelable
    public static class RedstoneGenerationEvent extends OreGenerationEvent
    {

        public RedstoneGenerationEvent(World world, Random random, int worldX, int worldZ, int veinsToGenerate, WorldGenerator worldGenerator, int minY, int maxY)
        {
            super(world, random, worldX, worldZ, veinsToGenerate, worldGenerator, minY, maxY);
        }

    }

    @Cancelable
    public static class DiamondGenerationEvent extends OreGenerationEvent
    {

        public DiamondGenerationEvent(World world, Random random, int worldX, int worldZ, int veinsToGenerate, WorldGenerator worldGenerator, int minY, int maxY)
        {
            super(world, random, worldX, worldZ, veinsToGenerate, worldGenerator, minY, maxY);
        }

    }

    @Cancelable
    public static class LapisGenerationEvent extends OreGenerationEvent
    {

        public LapisGenerationEvent(World world, Random random, int worldX, int worldZ, int veinsToGenerate, WorldGenerator worldGenerator, int minY, int maxY)
        {
            super(world, random, worldX, worldZ, veinsToGenerate, worldGenerator, minY, maxY);
        }

    }
    
}
