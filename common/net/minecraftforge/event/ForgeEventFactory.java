package net.minecraftforge.event;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.entity.EntityExplodeEvent;
import net.minecraftforge.event.entity.living.LivingPackSizeEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.AllowDespawn;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;

@SuppressWarnings("deprecation")
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

    public static Result canEntitySpawn(EntityLiving entity, World world, float x, float y, float z)
    {
        LivingSpawnEvent.CheckSpawn event = new LivingSpawnEvent.CheckSpawn(entity, world, x, y, z);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult();
    }

    public static boolean doSpecialSpawn(EntityLiving entity, World world, float x, float y, float z)
    {
        return MinecraftForge.EVENT_BUS.post(new LivingSpawnEvent.SpecialSpawn(entity, world, x, y, z));
    }

    public static Result canEntityDespawn(EntityLiving entity)
    {
        AllowDespawn event = new AllowDespawn(entity);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult();
    }
    
    public static List getPotentialSpawns(WorldServer world, EnumCreatureType type, int x, int y, int z, List oldList)
    {
        WorldEvent.PotentialSpawns event = new WorldEvent.PotentialSpawns(world, type, x, y, z, oldList);
        if (MinecraftForge.EVENT_BUS.post(event))
        {
            return null;
        }
        return event.list;
    }
    
    public static int getMaxSpawnPackSize(EntityLiving entity)
    {
        LivingPackSizeEvent maxCanSpawnEvent = new LivingPackSizeEvent(entity);
        MinecraftForge.EVENT_BUS.post(maxCanSpawnEvent);
        return maxCanSpawnEvent.getResult() == Result.ALLOW ? maxCanSpawnEvent.maxPackSize : entity.getMaxSpawnedInChunk();
    }

    public static String getPlayerDisplayName(EntityPlayer player, String username)
    {
        PlayerEvent.NameFormat event = new PlayerEvent.NameFormat(player, username);
        MinecraftForge.EVENT_BUS.post(event);
        return event.displayname;
    }

    public static float fireBlockHarvesting(ArrayList<ItemStack> drops, World world, Block block, int x, int y, int z, int meta, int fortune, float dropChance, boolean silkTouch, EntityPlayer player)
    {
        BlockEvent.HarvestDropsEvent event = new BlockEvent.HarvestDropsEvent(x, y, z, world, block, meta, fortune, dropChance, drops, player, silkTouch);
        MinecraftForge.EVENT_BUS.post(event);
        return event.dropChance;
    }
    
    public static boolean canEntityExplode(Explosion explosion, List list)
    {
        EntityExplodeEvent event = new EntityExplodeEvent(explosion, list);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled())
        {
            explosion.stopExplosion = true;
            return false;
        }
        return true;
    }
}
