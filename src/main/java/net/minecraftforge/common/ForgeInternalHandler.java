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
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.listener.Priority;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.filters.NetworkFilters;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.server.command.ForgeCommand;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.command.ConfigCommand;
import org.jetbrains.annotations.ApiStatus;

import java.lang.invoke.MethodHandles;

@ApiStatus.Internal
public final class ForgeInternalHandler {
    static void register() {
        BusGroup.DEFAULT.register(MethodHandles.lookup(), ForgeInternalHandler.class);
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
                    var executor = LogicalSidedProvider.WORKQUEUE.get(event.getLevel().isClientSide ? LogicalSide.CLIENT : LogicalSide.SERVER);
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
        UsernameCache.setUsername(event.getEntity().getUUID(), event.getEntity().getGameProfile().getName());
    }

    @SubscribeEvent
    static void onCommandsRegister(RegisterCommandsEvent event) {
        new ForgeCommand(event.getDispatcher());
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
        event.addNodes(ForgeMod.USE_SELECTORS_PERMISSION);
    }
}
