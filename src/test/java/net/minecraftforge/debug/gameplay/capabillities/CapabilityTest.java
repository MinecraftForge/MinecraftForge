/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.gameplay.capabillities;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityFactoryRegisterEvent;
import net.minecraftforge.common.capabilities.CapabilityProviderHolder;
import net.minecraftforge.common.capabilities.CapabilityProviderWithFactory;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.test.BaseTestMod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraftforge.debug.gameplay.capabillities.CapabilityTest.MOD_ID;

@Mod(MOD_ID)
public class CapabilityTest extends BaseTestMod {
    public static final String MOD_ID = "capability_test";
    public static class SelfMarkEnergyStorage extends EnergyStorage {
        private final ItemStack stack;

        public SelfMarkEnergyStorage(ItemStack stack) {
            super(1000, 10, 10, 10);
            this.stack = stack;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (!simulate) {
                var a = super.receiveEnergy(maxReceive, simulate);
                return a;
            }
            return super.receiveEnergy(maxReceive, simulate);
        }


    }

    public CapabilityTest() {
        IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.addListener(this::onInteract);
        bus.addListener(this::onRegister);
        bus.addListener(this::onToolTip);
        bus.addListener(this::tick);
    }

    public void onRegister(CapabilityFactoryRegisterEvent event) {

        Supplier<CapabilityProviderHolder<MyProvider>> cap = () -> new CapabilityProviderHolder<>(
                new MyProvider(),
                e -> {}
        );

        Function<ItemStack, CapabilityProviderHolder<MyProviderItem>> cap2 = i -> new CapabilityProviderHolder<>(
                new MyProviderItem(i),
                e -> {}
        );

        event.register(Entity.class, ResourceLocation.fromNamespaceAndPath("mc", "test2"), obj -> cap.get());
        event.register(Items.GLASS_PANE, ResourceLocation.fromNamespaceAndPath("mc", "test3"), cap2::apply);
    }

    public void tick(TickEvent.PlayerTickEvent event) {
        if (event.player.level().isClientSide) return;
        event.player.getItemInHand(InteractionHand.MAIN_HAND)
                .getCapability(ForgeCapabilities.ENERGY)
                .ifPresent(e ->
                        e.receiveEnergy(1, false)
                );

    }

    public void onToolTip(ItemTooltipEvent event) {
        var item = event.getItemStack();
        item.getCapability(ForgeCapabilities.ENERGY).ifPresent(e -> {
            event.getToolTip()
                    .add(
                            Component.literal("Energy: %s / %s".formatted(e.getEnergyStored(), e.getMaxEnergyStored()))
                    );
        });
    }

    public void onInteract(AttackEntityEvent event) {
        if (event.getTarget().level().isClientSide()) return; // check on server!
        var target = event.getTarget();
        var plr = event.getEntity();

        if (target.getCapability(ForgeCapabilities.ENERGY).isPresent()) {
            plr.sendSystemMessage(Component.literal("HAS ENERGY!"));
        }
        if (target.getCapability(ForgeCapabilities.FLUID_HANDLER).isPresent()) {
            plr.sendSystemMessage(Component.literal("HAS FLUID!"));
        }
    }


    public static class MyProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final EnergyStorage STORAGE = new EnergyStorage(1000, 10, 10, 10);
        private final LazyOptional<EnergyStorage> STORAGE_LAZY = LazyOptional.of(() -> STORAGE);

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if (cap == ForgeCapabilities.ENERGY)
                return STORAGE_LAZY.cast();
            return LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider registryAccess) {
            CompoundTag tag = new CompoundTag();
            tag.put("energy", STORAGE.serializeNBT(registryAccess));
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider registryAccess, CompoundTag nbt) {
            STORAGE.deserializeNBT(registryAccess, nbt.get("energy"));
        }
    }

    public static class MyProviderItem implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final EnergyStorage STORAGE ;
        private final LazyOptional<EnergyStorage> STORAGE_LAZY;

        public MyProviderItem(ItemStack stack) {
            this.STORAGE = new SelfMarkEnergyStorage(stack);
            this.STORAGE_LAZY = LazyOptional.of(() -> STORAGE);
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if (cap == ForgeCapabilities.ENERGY)
                return STORAGE_LAZY.cast();
            return LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider registryAccess) {
            CompoundTag tag = new CompoundTag();
            tag.put("energy", STORAGE.serializeNBT(registryAccess));
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider registryAccess, CompoundTag nbt) {
            STORAGE.deserializeNBT(registryAccess, nbt.get("energy"));
        }
    }
}
