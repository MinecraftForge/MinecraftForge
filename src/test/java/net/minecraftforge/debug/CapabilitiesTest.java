/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
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

    public static CapabilityType<CapClass> INSTANCE = CapabilityManager.get(new ResourceLocation(MODID, "test_cap"));

    private static ResourceLocation PROVIDER_ID = new ResourceLocation("capabilities_test:test");

    private static final ConcurrentLinkedQueue<String> messages = new ConcurrentLinkedQueue<>();

    public CapabilitiesTest()
    {
        if (ENABLED)
        {
            new AttachTest<>(AttachCapabilitiesEvent.BlockEntities.class);
            new AttachTest<>(AttachCapabilitiesEvent.ItemStacks.class);
            new AttachTest<>(AttachCapabilitiesEvent.Entities.class);
            new AttachTest<>(AttachCapabilitiesEvent.Levels.class);
            new AttachTest<>(AttachCapabilitiesEvent.Chunks.class);
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

    public static class AttachTest<K extends ICapabilityProvider, T extends AttachCapabilitiesEvent<K>>
    {
        public AttachTest(Class<T> cls)
        {
            MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, cls, this::attach);
        }

        public void attach(T event)
        {
        	//TODO: FIXME, will NOT work for ItemStacks or Players!
            event.addCapability(new IAttachedCapabilityProvider<CapClass, K>()
            {
                final Capability<CapClass> instance = Capability.of(() -> new CapClass(this));

                @Override
                public CompoundTag serializeNBT()
                {
                	CompoundTag tag = new CompoundTag();
                    tag.putInt("test", 1);
                    return tag;
                }

                @Override
                public void deserializeNBT(CompoundTag nbt)
                {
                    if (!nbt.contains("test", Tag.TAG_INT))
                        throw new IllegalStateException("Unexpected tag type");
                    if(nbt.getInt("test") != 1)
                        throw new IllegalStateException("Unexpected tag data");
                }

				@Override
				public CapabilityType<CapClass> getType() {
					return INSTANCE;
				}

				@Override
				public ResourceLocation getId() {
					return PROVIDER_ID;
				}

				@Override
				public @NotNull Capability<CapClass> getCapability(@Nullable Direction direction) {
					return this.instance.cast();
				}

				@Override
				public void invalidateCaps() {
					this.instance.invalidate();
				}

				@Override
				public void reviveCaps() {
					this.instance.revive();
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
                    final ChatType system = Minecraft.getInstance().level.registryAccess().registryOrThrow(Registry.CHAT_TYPE_REGISTRY).getOrThrow(ChatType.SYSTEM);
                    Minecraft.getInstance().gui.handleSystemChat(system, Component.literal(Objects.requireNonNull(messages.poll())));
                }
            }
        }
    }
}
