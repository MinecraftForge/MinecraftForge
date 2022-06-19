/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.DataSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.AttachCapabilitiesEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityType;
import net.minecraftforge.common.capabilities.IAttachedCapabilityProvider.ICompleteCapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.thread.EffectiveSide;

@Mod(CapabilitiesTest.MODID)
public class CapabilitiesTest
{
    public static final String MODID = "capabilities_test";

    private static final boolean ENABLED = true;

    public static CapabilityType<DataSlot> INSTANCE = CapabilityManager.get(new ResourceLocation(MODID, "test_cap"));

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
            MinecraftForge.EVENT_BUS.<RightClickBlock>addListener(e -> {
            	if(e.getHand() == InteractionHand.MAIN_HAND) e.getItemStack().getCapability(INSTANCE).ifPresent(t -> t.set(t.get() + 1));
            });
        }
    }

    static class Provider<O extends ICapabilityProvider> implements ICompleteCapabilityProvider<DataSlot, O>
    {
    	private DataSlot data = DataSlot.standalone();
    	
        private Capability<DataSlot> instance = Capability.of(() -> data);

        @Override
        public CompoundTag serializeNBT()
        {
            CompoundTag tag = new CompoundTag();
            tag.putInt("test", this.data.get());
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt)
        {
            if (!nbt.contains("test", Tag.TAG_INT))
                throw new IllegalStateException("Unexpected tag type");
            if(nbt.getInt("test") < 0)
                throw new IllegalStateException("Unexpected tag data");
            this.data.set(nbt.getInt("test"));
        }

        @Override
        public CapabilityType<DataSlot> getType() {
            return INSTANCE;
        }

        @Override
        public ResourceLocation getId() {
            return PROVIDER_ID;
        }

        @Override
        public @NotNull Capability<DataSlot> getCapability(@Nullable Direction direction) {
            return this.instance.cast();
        }

        @Override
        public void invalidateCaps() {
            this.instance.invalidate();
        }

        @Override
        public void reviveCaps() {
            this.instance = Capability.of(() -> data);
        }

        @Override
        public boolean isEquivalentTo(@Nullable IComparableCapabilityProvider<DataSlot, O> other)
        {
            return other != null && ((Provider<?>) other).data.get() == this.data.get();
        }

        @Override
        public @Nullable ICopyableCapabilityProvider<DataSlot, O> copy(O copiedParent)
        {
            Provider<O> p = new Provider<>();
            p.data.set(this.data.get());
            return p;
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
            event.addCapability(new Provider<>());

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

        @SubscribeEvent
        public static void tooltip(ItemTooltipEvent e) {
        	e.getItemStack().getCapability(CapabilitiesTest.INSTANCE).ifPresent(t -> {
        		e.getToolTip().add(Component.literal("Cap Data:" + t.get()));
        	});
        }
    }
}
