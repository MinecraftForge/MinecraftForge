/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.client.CloudRenderer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;

public class ForgeInternalHandler
{
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();
        if (entity.getClass().equals(ItemEntity.class))
        {
            ItemStack stack = ((ItemEntity)entity).getItem();
            Item item = stack.getItem();
            if (item.hasCustomEntity(stack))
            {
                Entity newEntity = item.createEntity(event.getWorld(), entity, stack);
                if (newEntity != null)
                {
                    entity.remove();
                    event.setCanceled(true);
                    event.getWorld().addEntity(newEntity);
                }
            }
        }
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onDimensionUnload(WorldEvent.Unload event)
    {
        if (event.getWorld() instanceof ServerWorld)
            FakePlayerFactory.unloadWorld((ServerWorld) event.getWorld());
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent event)
    {
        WorldWorkerManager.tick(event.phase == TickEvent.Phase.START);
    }

    @SubscribeEvent
    public void checkSettings(ClientTickEvent event)
    {
        if (event.phase == Phase.END)
            CloudRenderer.updateCloudSettings();
    }

    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event)
    {
        if (!event.getWorld().isRemote())
            FarmlandWaterManager.removeTickets(event.getChunk());
    }

    @SubscribeEvent
    public void playerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event)
    {
        if (event.getPlayer() instanceof ServerPlayerEntity)
            DimensionManager.rebuildPlayerMap(((ServerPlayerEntity)event.getPlayer()).server.getPlayerList(), true);
    }

    @SubscribeEvent
    public void playerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        UsernameCache.setUsername(event.getPlayer().getUniqueID(), event.getPlayer().getGameProfile().getName());
    }

    @SubscribeEvent
    public synchronized void tagsUpdated(TagsUpdatedEvent event)
    {
        ForgeHooks.updateBurns();
    }
}

