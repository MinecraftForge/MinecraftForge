package net.minecraftforge.event;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.eventhandler.Event.Result;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.SaveHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingPackSizeEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.AllowDespawn;
import net.minecraftforge.event.entity.living.ZombieEvent.SummonAidEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;

public class ForgeEventFactory
{
    public static boolean doPlayerHarvestCheck(EntityPlayer player, Block block, boolean success)
    {
        PlayerEvent.HarvestCheck event = new PlayerEvent.HarvestCheck(player, block, success);
        MinecraftForge.EVENT_BUS.post(event);
        return event.success;
    }

    @Deprecated // Location version below
    public static float getBreakSpeed(EntityPlayer player, Block block, int metadata, float original)
    {
        return getBreakSpeed(player, block, metadata, original, 0, -1, 0);
    }

    public static float getBreakSpeed(EntityPlayer player, Block block, int metadata, float original, int x, int y, int z)
    {
        PlayerEvent.BreakSpeed event = new PlayerEvent.BreakSpeed(player, block, metadata, original, x, y, z);
        return (MinecraftForge.EVENT_BUS.post(event) ? -1 : event.newSpeed);
    }

    @Deprecated
    public static PlayerInteractEvent onPlayerInteract(EntityPlayer player, Action action, int x, int y, int z, int face)
    {
        return onPlayerInteract(player, action, x, y, z, face, null);
    }
    public static PlayerInteractEvent onPlayerInteract(EntityPlayer player, Action action, int x, int y, int z, int face, World world)
    {
        PlayerInteractEvent event = new PlayerInteractEvent(player, action, x, y, z, face, world);
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

    public static List<BiomeGenBase.SpawnListEntry> getPotentialSpawns(WorldServer world, EnumCreatureType type, int x, int y, int z, List<BiomeGenBase.SpawnListEntry> oldList)
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

    public static ItemTooltipEvent onItemTooltip(ItemStack itemStack, EntityPlayer entityPlayer, List<String> toolTip, boolean showAdvancedItemTooltips)
    {
        ItemTooltipEvent event = new ItemTooltipEvent(itemStack, entityPlayer, toolTip, showAdvancedItemTooltips);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static SummonAidEvent fireZombieSummonAid(EntityZombie zombie, World world, int x, int y, int z, EntityLivingBase attacker, double summonChance)
    {
        SummonAidEvent summonEvent = new SummonAidEvent(zombie, world, x, y, z, attacker, summonChance);
        MinecraftForge.EVENT_BUS.post(summonEvent);
        return summonEvent;
    }

    public static boolean onEntityStruckByLightning(Entity entity, EntityLightningBolt bolt)
    {
        return MinecraftForge.EVENT_BUS.post(new EntityStruckByLightningEvent(entity, bolt));
    }

    public static int onItemUseStart(EntityPlayer player, ItemStack item, int duration)
    {
        PlayerUseItemEvent event = new PlayerUseItemEvent.Start(player, item, duration);
        return MinecraftForge.EVENT_BUS.post(event) ? -1 : event.duration;
    }

    public static int onItemUseTick(EntityPlayer player, ItemStack item, int duration)
    {
        PlayerUseItemEvent event = new PlayerUseItemEvent.Tick(player, item, duration);
        return MinecraftForge.EVENT_BUS.post(event) ? -1 : event.duration;
    }

    public static boolean onUseItemStop(EntityPlayer player, ItemStack item, int duration)
    {
        return MinecraftForge.EVENT_BUS.post(new PlayerUseItemEvent.Stop(player, item, duration));
    }

    public static ItemStack onItemUseFinish(EntityPlayer player, ItemStack item, int duration, ItemStack result)
    {
        PlayerUseItemEvent.Finish event = new PlayerUseItemEvent.Finish(player, item, duration, result);
        MinecraftForge.EVENT_BUS.post(event);
        return event.result;
    }

    public static void onStartEntityTracking(Entity entity, EntityPlayer player)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerEvent.StartTracking(player, entity));
    }

    public static void onStopEntityTracking(Entity entity, EntityPlayer player)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerEvent.StopTracking(player, entity));
    }

    public static void firePlayerLoadingEvent(EntityPlayer player, File playerDirectory, String uuidString)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerEvent.LoadFromFile(player, playerDirectory, uuidString));
    }

    public static void firePlayerSavingEvent(EntityPlayer player, File playerDirectory, String uuidString)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerEvent.SaveToFile(player, playerDirectory, uuidString));
    }

    public static void firePlayerLoadingEvent(EntityPlayer player, IPlayerFileData playerFileData, String uuidString)
    {
        SaveHandler sh = (SaveHandler) playerFileData;
        File dir = ObfuscationReflectionHelper.getPrivateValue(SaveHandler.class, sh, "playersDirectory", "field_"+"75771_c");
        MinecraftForge.EVENT_BUS.post(new PlayerEvent.LoadFromFile(player, dir, uuidString));
    }
}
