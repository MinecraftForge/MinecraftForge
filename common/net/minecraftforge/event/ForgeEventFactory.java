package net.minecraftforge.event;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.living.LivingSpecialSpawnEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

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
        boolean result = MinecraftForge.EVENT_BUS.post(new LivingSpecialSpawnEvent(entity, world, x, y, z));
        LivingSpawnEvent.SpecialSpawn nEvent = new LivingSpawnEvent.SpecialSpawn(entity, world, x, y, z);

        if (result) //For the time being, copy the canceled state from the old legacy event
        {           // Remove when we remove LivingSpecialSpawnEvent.
            nEvent.setCanceled(true);
        }

        return MinecraftForge.EVENT_BUS.post(nEvent);
    }
}
