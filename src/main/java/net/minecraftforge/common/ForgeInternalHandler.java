package net.minecraftforge.common;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.entity.*;
import net.minecraftforge.event.world.WorldEvent;

public class ForgeInternalHandler
{
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        if (!event.world.isRemote)
        {
            ForgeChunkManager.loadEntity(event.entity);
        }

        Entity entity = event.entity;
        if (entity.getClass().equals(EntityItem.class))
        {
            ItemStack stack = entity.getDataWatcher().getWatchableObjectItemStack(10);

            if (stack == null)
            {
                //entity.setDead();
                //event.setCanceled(true);
                return;
            }

            Item item = stack.getItem();
            if (item == null)
            {
                FMLLog.warning("Attempted to add a EntityItem to the world with a invalid item at " +
                    "(%2.2f,  %2.2f, %2.2f), this is most likely a config issue between you and the server. Please double check your configs",
                    entity.posX, entity.posY, entity.posZ);
                entity.setDead();
                event.setCanceled(true);
                return;
            }

            if (item.hasCustomEntity(stack))
            {
                Entity newEntity = item.createEntity(event.world, entity, stack);
                if (newEntity != null)
                {
                    entity.setDead();
                    event.setCanceled(true);
                    event.world.spawnEntityInWorld(newEntity);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onDimensionLoad(WorldEvent.Load event)
    {
        ForgeChunkManager.loadWorld(event.world);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onDimensionSave(WorldEvent.Save event)
    {
    	ForgeChunkManager.saveWorld(event.world);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onDimensionUnload(WorldEvent.Unload event)
    {
        ForgeChunkManager.unloadWorld(event.world);
        if (event.world instanceof WorldServer)
            FakePlayerFactory.unloadWorld((WorldServer)event.world);
    }
}
