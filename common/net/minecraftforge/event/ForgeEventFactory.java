package net.minecraftforge.event;
import java.util.Random;

import net.minecraft.src.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.*;

public class ForgeEventFactory
{
    public static byte getBiomeSize(WorldType worldType, byte original)
    {
        WorldTypeEvent.BiomeSize event = new WorldTypeEvent.BiomeSize(worldType, original);
        MinecraftForge.EVENT_BUS.post(event);
        return event.newSize;
    }

    public static GenLayer[] getBiomeGenerators(WorldType worldType, long seed, GenLayer[] original)
    {
        WorldTypeEvent.InitBiomeGens event = new WorldTypeEvent.InitBiomeGens(worldType, seed, original);
        MinecraftForge.EVENT_BUS.post(event);
        return event.newBiomeGens;
    }
    
    public static NoiseGeneratorOctaves[] getNoiseGenerators(World world, Random rand, NoiseGeneratorOctaves[] original)
    {
        WorldEvent.InitNoiseGens event = new WorldEvent.InitNoiseGens(world, rand, original);
        MinecraftForge.EVENT_BUS.post(event);
        return event.newNoiseGens;
    }

    public static boolean doPlayerHarvestCheck(EntityPlayer player, Block block, boolean success)
    {
        PlayerEvent.HarvestCheck event = new PlayerEvent.HarvestCheck(player, block, success);
        MinecraftForge.EVENT_BUS.post(event);
        return event.success;
    }

    public static float getBreakSpeed(EntityPlayer player, Block block, int metadata, float original)
    {
        PlayerEvent.BreakSpeed event = new PlayerEvent.BreakSpeed(player, block, metadata, original);
        return (MinecraftForge.EVENT_BUS.post(event) ? -1 : event.newSpeed);
    }

    public static PlayerInteractEvent onPlayerInteract(EntityPlayer player, Action action, int x, int y, int z, int face)
    {
        PlayerInteractEvent event = new PlayerInteractEvent(player, action, x, y, z, face);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static void onPlayerDestroyItem(EntityPlayer player, ItemStack stack)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, stack));
    }

    public static enum MapGenEnum { RAVINE, CAVE, NETHER_CAVE }
    
    public static MapGenBase GetMapGen(MapGenBase original, MapGenEnum type)
    {
        MapGenBase retVal;
        switch (type)
        {
        case CAVE:
            InitMapGenEvent.Cave eventCave = new InitMapGenEvent.Cave(original);
            MinecraftForge.EVENT_BUS.post(eventCave);
            retVal = eventCave.newGen;
        case NETHER_CAVE:
            InitMapGenEvent.NetherCave eventNetherCave = new InitMapGenEvent.NetherCave(original);
            MinecraftForge.EVENT_BUS.post(eventNetherCave);
            retVal = eventNetherCave.newGen;
        default:
            InitMapGenEvent.Ravine eventRavine = new InitMapGenEvent.Ravine(original);
            MinecraftForge.EVENT_BUS.post(eventRavine);
            retVal = eventRavine.newGen;
        }
        return retVal;
    }

    public static MapGenStronghold GetMapGen(MapGenStronghold original)
    {
        InitMapGenEvent.Stronghold event = new InitMapGenEvent.Stronghold(original);
        MinecraftForge.EVENT_BUS.post(event);
        return event.newGen;
    }

    public static MapGenVillage GetMapGen(MapGenVillage original)
    {
        InitMapGenEvent.Village event = new InitMapGenEvent.Village(original);
        MinecraftForge.EVENT_BUS.post(event);
        return event.newGen;
    }

    public static MapGenMineshaft GetMapGen(MapGenMineshaft original)
    {
        InitMapGenEvent.Mineshaft event = new InitMapGenEvent.Mineshaft(original);
        MinecraftForge.EVENT_BUS.post(event);
        return event.newGen;
    }

    public static MapGenScatteredFeature GetMapGen(MapGenScatteredFeature original)
    {
        InitMapGenEvent.ScatteredFeature event = new InitMapGenEvent.ScatteredFeature(original);
        MinecraftForge.EVENT_BUS.post(event);
        return event.newGen;
    }

    public static MapGenNetherBridge GetMapGen(MapGenNetherBridge original)
    {
        InitMapGenEvent.NetherBridge event = new InitMapGenEvent.NetherBridge(original);
        MinecraftForge.EVENT_BUS.post(event);
        return event.newGen;
    }
    
    public static enum DoGenEnum {DUNGEON, FIRE, GLOWSTONE, ICE, LAKE, LAVA, NETHER_LAVA}
    
    public static boolean doPopulate(IChunkProvider chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated, DoGenEnum type)
    {
        PopulateChunkEvent event;
        switch (type)
        {
        case DUNGEON:
            event = new PopulateChunkEvent.GenDungeon(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated);
        case FIRE:
            event = new PopulateChunkEvent.GenFire(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated);
        case GLOWSTONE:
            event = new PopulateChunkEvent.GenGlowStone(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated);
        case ICE:
            event = new PopulateChunkEvent.GenDungeon(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated);
        case LAKE:
            event = new PopulateChunkEvent.GenLake(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated, Block.waterStill);
        case LAVA:
            event = new PopulateChunkEvent.GenLake(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated, Block.lavaStill);
        default:
            event = new PopulateChunkEvent.GenLake(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated, Block.lavaMoving);
        }
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult() == Result.DEFAULT;
    }
}
