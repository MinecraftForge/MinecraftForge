/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.CapabilitySystem;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Mod(CapabilitiesTest.MOD_ID)
public class CapabilitiesTest {
    public static final String MOD_ID = "captest";
    private static final ConcurrentHashMap<Class<?>, AtomicInteger> TRACK = new ConcurrentHashMap<>();
    private static final boolean ENABLED = true;

    public static void logAttachEvent(Event event) {
        TRACK.computeIfAbsent(event.getClass(), e -> new AtomicInteger()).getAndAdd(1);
    }

    public CapabilitiesTest() {
        if (ENABLED) { // Register our listeners if this test is enabled.
            CapabilitySystem.addListener(Item.class, this::AttachItemStack);
            CapabilitySystem.addListener(BlockEntity.class, this::AttachBlockEntity);
            CapabilitySystem.addListener(Level.class, this::AttachLevel);
            CapabilitySystem.addListener(LevelChunk.class, this::AttachLevelChunk);
            CapabilitySystem.addListener(Entity.class, this::AttachEntity);
        }
    }

    @SubscribeEvent
    public void AttachItemStack(AttachCapabilitiesEvent<ItemStack> event) {
        logAttachEvent(event);
    }

    @SubscribeEvent
    public void AttachBlockEntity(AttachCapabilitiesEvent<BlockEntity> event) {
        logAttachEvent(event);
    }

    @SubscribeEvent
    public void AttachLevel(AttachCapabilitiesEvent<Level> event) {
        logAttachEvent(event);
    }

    @SubscribeEvent
    public void AttachLevelChunk(AttachCapabilitiesEvent<LevelChunk> event) {
        logAttachEvent(event);
    }

    @SubscribeEvent
    public void AttachEntity(AttachCapabilitiesEvent<Entity> event) {
        logAttachEvent(event);
    }



    @Mod.EventBusSubscriber(value= Dist.CLIENT, modid = CapabilitiesTest.MOD_ID, bus= Mod.EventBusSubscriber.Bus.FORGE)
    public static class ClientEvents
    {

        private static final int tickRate = 100;
        private static int ticks = 0;
        @SubscribeEvent
        public static void clientTick(TickEvent.ClientTickEvent event)
        {
            if (!ENABLED) return;
            if (event.phase == TickEvent.Phase.END && Minecraft.getInstance().level != null)
            {
                ticks++;
                if (ticks % tickRate == 0) {
                    Minecraft minecraft = Minecraft.getInstance();
                    if (minecraft != null && minecraft.player != null) {
                        Player player = minecraft.player;

                        player.sendSystemMessage(Component.literal("Start of CapabilitiesTest results for the last %s ticks".formatted(tickRate)));

                        TRACK.forEach(((aClass, atomicInteger) -> {
                            player.sendSystemMessage(Component.literal("AttachEvent: %s heard %s times".formatted(
                                    aClass.getSimpleName(),
                                    atomicInteger.getAndSet(0)
                            )));
                        }));

                        player.sendSystemMessage(Component.literal("End of CapabilitiesTest results for the last %s ticks".formatted(tickRate)));
                    }
                }
            }
        }
    }
}
