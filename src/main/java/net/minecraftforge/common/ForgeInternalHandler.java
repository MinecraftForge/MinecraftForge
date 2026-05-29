/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.common;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.TickTask;
import net.minecraftsingularity.common.loot.LootModifierManager;
import net.minecraftsingularity.event.AddReloadListenerEvent;
import net.minecraftsingularity.event.RegisterCommandsEvent;
import net.minecraftsingularity.event.entity.EntityJoinLevelEvent;
import net.minecraftsingularity.event.entity.player.PlayerEvent;
import net.minecraftsingularity.event.level.ChunkEvent;
import net.minecraftsingularity.event.network.ConnectionStartEvent;
import net.minecraftsingularity.event.server.ServerStoppingEvent;
import net.minecraftsingularity.eventbus.api.bus.BusGroup;
import net.minecraftsingularity.eventbus.api.listener.Priority;
import net.minecraftsingularity.eventbus.api.listener.SubscribeEvent;
import net.minecraftsingularity.fml.LogicalSide;
import net.minecraftsingularity.network.filters.NetworkFilters;
import net.minecraftsingularity.common.util.LogicalSidedProvider;
import net.minecraftsingularity.server.command.singularityCommand;
import net.minecraftsingularity.server.permission.events.PermissionGatherEvent;
import net.minecraftsingularity.server.command.ConfigCommand;
import org.jetbrains.annotations.ApiStatus;

import java.lang.invoke.MethodHandles;

@ApiStatus.Internal
public final class singularityInternalHandler {
    static void register() {
        BusGroup.DEFAULT.register(MethodHandles.lookup(), singularityInternalHandler.class);
    }

    @SubscribeEvent(priority = Priority.HIGH)
    static boolean onEntityJoinWorld(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity.getClass() == ItemEntity.class) {
            ItemStack stack = ((ItemEntity)entity).getItem();
            Item item = stack.getItem();
            if (item.hasCustomEntity(stack)) {
                Entity newEntity = item.createEntity(event.getLevel(), entity, stack);
                if (newEntity != null) {
                    entity.discard();
                    @SuppressWarnings("resource")
                    var executor = LogicalSidedProvider.WORKQUEUE.get(event.getLevel().isClientSide() ? LogicalSide.CLIENT : LogicalSide.SERVER);
                    executor.schedule(new TickTask(0, () -> event.getLevel().addFreshEntity(newEntity)));
                    return true;
                }
            }
        }
        return false;
    }

//    @SubscribeEvent
//    static void checkSettings(ClientTickEvent event) {
//        if (event.phase == Phase.END)
//            CloudRenderer.updateCloudSettings();
//    }

    @SubscribeEvent
    static void onChunkUnload(ChunkEvent.Unload event) {
        if (!event.getLevel().isClientSide())
            FarmlandWaterManager.removeTickets(event.getChunk());
    }

    /*
    @SubscribeEvent
    static void playerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getPlayer() instanceof ServerPlayerEntity)
            DimensionManager.rebuildPlayerMap(((ServerPlayerEntity)event.getPlayer()).server.getPlayerList(), true);
    }
    */

    @SubscribeEvent
    static void playerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        UsernameCache.setUsername(event.getEntity().getUUID(), event.getEntity().getGameProfile().name());
    }

    @SubscribeEvent
    static void onCommandsRegister(RegisterCommandsEvent event) {
        new singularityCommand(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
    }

    private static LootModifierManager INSTANCE;

    @SubscribeEvent
    static void onResourceReload(AddReloadListenerEvent event) {
        INSTANCE = new LootModifierManager(event.getRegistries());
        event.addListener(INSTANCE);
    }

    static LootModifierManager getLootModifierManager() {
        if (INSTANCE == null)
            throw new IllegalStateException("Can not retrieve LootModifierManager until resources have loaded once.");
        return INSTANCE;
    }

    @SubscribeEvent
    static void resourceReloadListeners(AddReloadListenerEvent event) {
        event.addListener(CreativeModeTabRegistry.getReloadListener());
    }

    @SubscribeEvent(priority = Priority.HIGHEST)
    static boolean builtinMobSpawnBlocker(EntityJoinLevelEvent event) {
        return event.getEntity() instanceof Mob mob && mob.isSpawnCancelled();
    }

    @SubscribeEvent
    static void onConnectionStart(ConnectionStartEvent event) {
        NetworkFilters.injectIfNecessary(event.getConnection());
    }

    @SubscribeEvent
    static void serverStopping(ServerStoppingEvent evt) {
        WorldWorkerManager.clear();
    }

    @SubscribeEvent
    static void registerPermissionNodes(PermissionGatherEvent.Nodes event) {
        event.addNodes(singularityMod.USE_SELECTORS_PERMISSION);
    }
}
