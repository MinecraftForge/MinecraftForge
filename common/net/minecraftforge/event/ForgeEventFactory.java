package net.minecraftforge.event;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.WeatherEvent;
import net.minecraftforge.event.world.WorldEvent;

import java.util.List;

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

    public static List getPotentialSpawns(WorldServer world, EnumCreatureType type, int x, int y, int z, List oldList)
    {
        WorldEvent.PotentialSpawns event = new WorldEvent.PotentialSpawns(world, type, x, y, z, oldList);
        if (MinecraftForge.EVENT_BUS.post(event))
        {
            return null;
        }
        return event.list;
    }

    public static boolean onLightningGenerated(World world, int x, int y, int z, WorldInfo info) {
        WeatherEvent.LightningGeneratedEvent event = new WeatherEvent.LightningGeneratedEvent(world, x, y, z, info);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult() != Result.DENY;
    }

    public static void onRainChanged(WorldInfo info) {
        World world = getWorldFromInfo(info);
        MinecraftForge.EVENT_BUS.post(new WeatherEvent.RainChangedEvent(world, info));

        if (info.isThundering()) {
            stormChanged(info, info.isRaining());
        }
    }

    public static void onThunderingChanged(WorldInfo info) {
        World world = getWorldFromInfo(info);
        MinecraftForge.EVENT_BUS.post(new WeatherEvent.ThunderingChangedEvent(world, info));

        if (info.isRaining()) {
            stormChanged(info, info.isThundering());
        }
    }

    private static void stormChanged(WorldInfo info, boolean started) {
        World world = getWorldFromInfo(info);
        MinecraftForge.EVENT_BUS.post(new WeatherEvent.StormChangedEvent(world, info, started));
    }

    private static World getWorldFromInfo(WorldInfo info) {
        return DimensionManager.getWorld(info.getDimension());
    }
}
