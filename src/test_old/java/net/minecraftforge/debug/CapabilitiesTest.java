/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

@Mod(CapabilitiesTest.MODID)
public class CapabilitiesTest
{
    public static final String MODID = "capabilities_test";

    private static final boolean ENABLED = false;

    public static Capability<CapClass> INSTANCE = CapabilityManager.get(new CapabilityToken<>(){});

    private static ResourceLocation TEST_CAP_ID = new ResourceLocation("capabilities_test:test");

    private static final ConcurrentLinkedQueue<String> messages = new ConcurrentLinkedQueue<>();

    public CapabilitiesTest()
    {
        if (ENABLED)
        {
            new AttachTest<>(BlockEntity.class);
            new AttachTest<>(Entity.class);
            new AttachTest<>(ItemStack.class);
            new AttachTest<>(LevelChunk.class);
            new AttachTest<>(Level.class);
        }
    }

    public static class CapClass
    {
        private final Object o;

        public CapClass(Object o)
        {
            this.o = o;
        }
    }

    public static class AttachTest<T extends ICapabilityProviderImpl<T>>
    {
        private final Class<T> cls;

        public AttachTest(Class<T> cls)
        {
            this.cls = cls;

            MinecraftForge.EVENT_BUS.addGenericListener(cls, this::attach);
        }

        public void attach(AttachCapabilitiesEvent<T> event)
        {
            event.addCapability(TEST_CAP_ID, new ICapabilitySerializable<>()
            {
                final LazyOptional<CapClass> instance = LazyOptional.of(() -> new CapClass(this));

                @Override
                public Tag serializeNBT()
                {
                    return IntTag.valueOf(1);
                }

                @Override
                public void deserializeNBT(Tag nbt)
                {
                    if (nbt.getId() != Tag.TAG_INT)
                        throw new IllegalStateException("Unexpected tag type");
                    if (!(nbt instanceof IntTag it))
                        throw new IllegalStateException("Unexpected tag class");
                    if(it.getAsInt() != 1)
                        throw new IllegalStateException("Unexpected tag type");
                }

                @NotNull
                @Override
                public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
                {
                    if (cap == INSTANCE)
                        return instance.cast();
                    return LazyOptional.empty();
                }
            });

            messages.add(String.format(Locale.ENGLISH, "Attached capability to %s in %s", event.getObject().getClass(), EffectiveSide.get()));
        }
    }

    @Mod.EventBusSubscriber(value= Dist.CLIENT, modid = CapabilitiesTest.MODID, bus= Mod.EventBusSubscriber.Bus.FORGE)
    public static class ClientEvents
    {
        @SubscribeEvent
        public static void clientTick(TickEvent.ClientTickEvent event)
        {
            if (event.phase == TickEvent.Phase.END && Minecraft.getInstance().level != null)
            {
                while(messages.size() > 0)
                {
                    Minecraft.getInstance().getChatListener().handleSystemMessage(Component.literal(Objects.requireNonNull(messages.poll())), false);
                }
            }
        }
    }
}
