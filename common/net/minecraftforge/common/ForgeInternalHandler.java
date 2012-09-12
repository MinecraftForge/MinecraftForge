package net.minecraftforge.common;

import net.minecraft.src.*;
import net.minecraftforge.event.*;
import net.minecraftforge.event.entity.*;

public class ForgeInternalHandler
{
    @ForgeSubscribe(priority = EventPriority.HIGHEST)
    public void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        Entity entity = event.entity;
        if (entity instanceof EntityItem)
        {
            ItemStack item = ((EntityItem)entity).item;
            if (item.getItem().hasCustomEntity(item))
            {
                Entity newEntity = item.getItem().createEntity(event.world, entity, item);
                if (newEntity != null)
                {
                    entity.setDead();
                    event.setCanceled(true);
                    event.world.spawnEntityInWorld(entity);
                }
            }
        }
    }
}
