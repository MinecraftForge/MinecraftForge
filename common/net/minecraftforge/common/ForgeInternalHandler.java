package net.minecraftforge.common;

import java.util.UUID;

import net.minecraft.src.*;
import net.minecraftforge.event.*;
import net.minecraftforge.event.entity.*;
import net.minecraftforge.event.world.WorldEvent;

public class ForgeInternalHandler
{
    @ForgeSubscribe(priority = EventPriority.HIGHEST)
    public void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        if (!event.world.isRemote)
        {
            if (event.entity.getPersistentID() == null)
            {
                event.entity.generatePersistentID();
            }
            else
            {
                ForgeChunkManager.loadEntity(event.entity);
            }
        }
        Entity entity = event.entity;
        if (entity.getClass().equals(EntityItem.class))
        {
            ItemStack item = ((EntityItem)entity).item;
            if (item != null && item.getItem().hasCustomEntity(item))
            {
                Entity newEntity = item.getItem().createEntity(event.world, entity, item);
                if (newEntity != null)
                {
                    entity.setDead();
                    event.setCanceled(true);
                    event.world.spawnEntityInWorld(newEntity);
                }
            }
        }
    }

    @ForgeSubscribe(priority = EventPriority.HIGHEST)
    public void onDimensionLoad(WorldEvent.Load event)
    {
        ForgeChunkManager.loadWorld(event.world);
    }

    @ForgeSubscribe(priority = EventPriority.HIGHEST)
    public void onDimensionSave(WorldEvent.Save event)
    {
    	ForgeChunkManager.saveWorld(event.world);
    }
}
