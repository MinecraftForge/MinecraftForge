/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilitySystem;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Mod(CapabilitiesTest.MOD_ID)
public class CapabilitiesTest
{
    public static final String MOD_ID = "captest";
    private static final ConcurrentHashMap<Class<?>, AtomicInteger> TRACK = new ConcurrentHashMap<>();
    private static final boolean ENABLED = true;

    public static void logAttachEvent(AttachCapabilitiesEvent<?> event) {
        TRACK.computeIfAbsent(event.getType(), e -> new AtomicInteger()).getAndAdd(1);
    }

    public static void onEvent(AttachCapabilitiesEvent<Item> event, ItemStack stack) {}

    public CapabilitiesTest() {
        if (ENABLED) { // Register our listeners if this test is enabled.

            CapabilitySystem.addListener(ItemStack.class, this::Attach);

            CapabilitySystem.addListener(BlockEntity.class, this::Attach);

            CapabilitySystem.addListener(Level.class, this::Attach);

            CapabilitySystem.addListener(LevelChunk.class, this::Attach);

            CapabilitySystem.addListener(Entity.class, this::Attach);


            CapabilitySystem.addWrappedListener(ItemStack.class, BlockItem.class, ItemStack::getItem,  (event, item) -> {
                TRACK.computeIfAbsent(item.getClass(), (a) -> new AtomicInteger()).addAndGet(1);
            });

            class test implements ICapabilitySerializable<CompoundTag> {
                final IItemHandler handler = new ItemStackHandler(1);
                final LazyOptional<IItemHandler> handlerLazyOptional = LazyOptional.of(() -> handler);
                @Override
                public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                    if (cap == ForgeCapabilities.ITEM_HANDLER)
                        return handlerLazyOptional.cast();
                    return LazyOptional.empty();
                }

                @Override
                public CompoundTag serializeNBT() {
                    CompoundTag tag = new CompoundTag();
                    tag.putString("result", "mamngorage");
                    return tag;
                }

                @Override
                public void deserializeNBT(CompoundTag nbt) {

                }
            }


            CapabilitySystem.addListener(ServerPlayer.class, this::Attach);
            CapabilitySystem.addListener(LocalPlayer.class, this::Attach);
        }
    }

    @SubscribeEvent
    public void Attach(AttachCapabilitiesEvent<?> event) {
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
                            if (aClass.getName().length() <= 6)
                                player.sendSystemMessage(Component.literal("Weird Class Detected: %s".formatted(aClass.getName())));
                        }));

                        player.sendSystemMessage(Component.literal("End of CapabilitiesTest results for the last %s ticks".formatted(tickRate)));
                    }
                }
            }
        }
    }
}
