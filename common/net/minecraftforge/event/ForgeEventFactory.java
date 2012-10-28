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
    
    public static enum ChunkPopulateEnum {DUNGEON, FIRE, GLOWSTONE, ICE, LAKE, LAVA, NETHER_LAVA}
    
    public static boolean doPopulate(IChunkProvider chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated, ChunkPopulateEnum type)
    {
        PopulateChunkEvent event;
        switch (type)
        {
        case DUNGEON:
            event = new PopulateChunkEvent.Dungeon(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated);
        case FIRE:
            event = new PopulateChunkEvent.Fire(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated);
        case GLOWSTONE:
            event = new PopulateChunkEvent.GlowStone(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated);
        case ICE:
            event = new PopulateChunkEvent.Dungeon(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated);
        case LAKE:
            event = new PopulateChunkEvent.Lake(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated, Block.waterStill);
        case LAVA:
            event = new PopulateChunkEvent.Lake(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated, Block.lavaStill);
        default:
            event = new PopulateChunkEvent.Lake(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated, Block.lavaMoving);
        }
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult() != Result.DENY;
    }
    
    public static enum BiomeDecorateEnum { BIG_SHROOM, CACTUS, CLAY, DEAD_BUSH, LILYPAD, FLOWERS, GRASS, LAKE, PUMPKIN, REED, SAND, SAND_PASS2, SHROOM, TREE }
    
    public static boolean doDecorate(World world, Random rand, int chunkX, int chunkZ, BiomeDecorateEnum type)
    {
        DecorateBiomeEvent event;
        switch (type)
        {
        case SHROOM:
            event = new DecorateBiomeEvent.Mushrooms(world, rand, chunkX, chunkZ);
        case SAND:
            event = new DecorateBiomeEvent.Sand(world, rand, chunkX, chunkZ);
        default:
//            event = new DecorateBiomeEvent.GenRedMushroom(world, rand, chunkX, chunkZ);
            event = null;
        }
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult() != Result.DENY;
    }
}
