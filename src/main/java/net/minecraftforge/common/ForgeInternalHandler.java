/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.common;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.loot.LootModifierManager;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.LevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fmllegacy.LogicalSidedProvider;
import net.minecraftforge.server.command.ForgeCommand;
import net.minecraftforge.server.command.ConfigCommand;

public class ForgeInternalHandler
{
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntityJoinLevel(EntityJoinLevelEvent event)
    {
        Entity entity = event.getEntity();
        if (entity.getClass().equals(ItemEntity.class))
        {
            ItemStack stack = ((ItemEntity)entity).getItem();
            Item item = stack.getItem();
            if (item.hasCustomEntity(stack))
            {
                Entity newEntity = item.createEntity(event.getLevel(), entity, stack);
                if (newEntity != null)
                {
                    entity.remove(false);
                    event.setCanceled(true);
                    BlockableEventLoop<Runnable> executor = LogicalSidedProvider.WORKQUEUE.get(event.getLevel().isClientSide ? LogicalSide.CLIENT : LogicalSide.SERVER);
                    executor.tell(new TickTask(0, () -> event.getLevel().addFreshEntity(newEntity)));
                }
            }
        }
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLevelUnload(LevelEvent.Unload event)
    {
        if (event.getLevel() instanceof ServerLevel)
            FakePlayerFactory.unloadWorld((ServerLevel) event.getLevel());
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent event)
    {
        WorldWorkerManager.tick(event.getTickPhase() == TickEvent.Phase.START);
    }

    @SubscribeEvent
    public void checkSettings(ClientTickEvent event)
    {
        //if (event.phase == Phase.END)
        //    CloudRenderer.updateCloudSettings();
    }

    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event)
    {
        if (!event.getLevel().isClientSide())
            FarmlandWaterManager.removeTickets(event.getChunk());
    }

    /*
    @SubscribeEvent
    public void playerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event)
    {
        if (event.getPlayer() instanceof ServerPlayerEntity)
            DimensionManager.rebuildPlayerMap(((ServerPlayerEntity)event.getPlayer()).server.getPlayerList(), true);
    }
    */

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        UsernameCache.setUsername(event.getEntity().getUUID(), event.getEntity().getGameProfile().getName());
    }

    @SubscribeEvent
    public synchronized void onTagsUpdated(TagsUpdatedEvent.VanillaTagTypes event)
    {
        ForgeHooks.updateBurns();
    }

    @SubscribeEvent
    public void onCommandsRegister(RegisterCommandsEvent event)
    {
        new ForgeCommand(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
    }
    
    private static LootModifierManager INSTANCE;

    @SubscribeEvent
    public void onAddReloadListener(AddReloadListenerEvent event)
    {
        INSTANCE = new LootModifierManager();
        event.addListener(INSTANCE);
    }

    static LootModifierManager getLootModifierManager()
    {
        if(INSTANCE == null)
            throw new IllegalStateException("Can not retrieve LootModifierManager until resources have loaded once.");
        return INSTANCE;
    }
}

