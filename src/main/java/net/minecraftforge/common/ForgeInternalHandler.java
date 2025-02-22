/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.TickTask;
import net.minecraftforge.common.loot.LootModifierManager;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.network.ConnectionStartEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.listener.Priority;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.filters.NetworkFilters;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.server.command.ForgeCommand;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.command.ConfigCommand;

import java.lang.invoke.MethodHandles;

public class ForgeInternalHandler {
    static void register() {
        BusGroup.DEFAULT.register(MethodHandles.lookup(), new ForgeInternalHandler());
    }

    @SubscribeEvent(priority = Priority.HIGH)
    public boolean onEntityJoinWorld(EntityJoinLevelEvent event) {
        Entity entity = event.entity();
        if (entity.getClass().equals(ItemEntity.class)) {
            ItemStack stack = ((ItemEntity)entity).getItem();
            Item item = stack.getItem();
            if (item.hasCustomEntity(stack)) {
                Entity newEntity = item.createEntity(event.level(), entity, stack);
                if (newEntity != null) {
                    entity.discard();
                    @SuppressWarnings("resource")
                    var executor = LogicalSidedProvider.WORKQUEUE.get(event.level().isClientSide ? LogicalSide.CLIENT : LogicalSide.SERVER);
                    executor.schedule(new TickTask(0, () -> event.level().addFreshEntity(newEntity)));
                    return true;
                }
            }
        }
        return false;
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Pre event) {
        WorldWorkerManager.tick(true);
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        WorldWorkerManager.tick(false);
    }

//    @SubscribeEvent
//    public void checkSettings(ClientTickEvent event) {
//        if (event.phase == Phase.END)
//            CloudRenderer.updateCloudSettings();
//    }

    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event) {
        if (!event.level().isClientSide())
            FarmlandWaterManager.removeTickets(event.chunk());
    }

    /*
    @SubscribeEvent
    public void playerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getPlayer() instanceof ServerPlayerEntity)
            DimensionManager.rebuildPlayerMap(((ServerPlayerEntity)event.getPlayer()).server.getPlayerList(), true);
    }
    */

    @SubscribeEvent
    public void playerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        UsernameCache.setUsername(event.entity().getUUID(), event.entity().getGameProfile().getName());
    }

    @SubscribeEvent
    public void onCommandsRegister(RegisterCommandsEvent event) {
        new ForgeCommand(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
    }

    private static LootModifierManager INSTANCE;

    @SubscribeEvent
    public void onResourceReload(AddReloadListenerEvent event) {
        INSTANCE = new LootModifierManager(event.getRegistries());
        event.addListener(INSTANCE);
    }

    static LootModifierManager getLootModifierManager() {
        if (INSTANCE == null)
            throw new IllegalStateException("Can not retrieve LootModifierManager until resources have loaded once.");
        return INSTANCE;
    }

    @SubscribeEvent
    public void resourceReloadListeners(AddReloadListenerEvent event) {
        event.addListener(CreativeModeTabRegistry.getReloadListener());
    }

    @SubscribeEvent(priority = Priority.HIGHEST)
    public boolean builtinMobSpawnBlocker(EntityJoinLevelEvent event) {
        return event.entity() instanceof Mob mob && mob.isSpawnCancelled();
    }

    @SubscribeEvent
    public void onConnectionStart(ConnectionStartEvent event) {
        NetworkFilters.injectIfNecessary(event.getConnection());
    }

    @SubscribeEvent
    public void serverStopping(ServerStoppingEvent evt) {
        WorldWorkerManager.clear();
    }

    @SubscribeEvent
    public void registerPermissionNodes(PermissionGatherEvent.Nodes event) {
        event.addNodes(ForgeMod.USE_SELECTORS_PERMISSION);
    }

}

