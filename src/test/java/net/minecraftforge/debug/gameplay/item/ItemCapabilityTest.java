/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.gameplay.item;

import io.netty.buffer.Unpooled;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTest;
import net.minecraftforge.gametest.GameTestNamespace;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.test.BaseTestMod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@GameTestNamespace("forge")
@Mod(ItemCapabilityTest.MOD_ID)
public class ItemCapabilityTest extends BaseTestMod {
    public static final String MOD_ID = "item_caps";

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MOD_ID);
    public static final RegistryObject<DataComponentType<Integer>> STORAGE = DATA_COMPONENTS.register("energy_storage", () ->
            DataComponentType.<Integer>builder()
                    .persistent(ExtraCodecs.POSITIVE_INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
                    .build()
    );

    public ItemCapabilityTest(FMLJavaModLoadingContext context) {
        super(context, false, true);
    }

    private static void onEvent(AttachCapabilitiesEvent<ItemStack> event) {
        if (event.getObject().getItem() == Items.COPPER_INGOT) {
            event.addCapability(
                    ResourceLocation.fromNamespaceAndPath("forge", "test"),
                    new MyProvider(event.getObject())
            );
        }
    }

    @GameTest
    public static void item_capability(GameTestHelper helper) {
        helper.addEventListener(AttachCapabilitiesEvent.ItemStacks.BUS, ItemCapabilityTest::onEvent);

        ItemStack stack = new ItemStack(Items.COPPER_INGOT);
        AtomicReference<IEnergyStorage> storageAtomicReference = new AtomicReference<>();

        stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(storage -> {
            storage.receiveEnergy(1, false);
            storageAtomicReference.set(storage);
        });

        helper.assertTrue(
                storageAtomicReference.get() != null,
                "Unable to find ForgeCapabilities.ENERGY Capability"
        );

        var registry = RegistryFriendlyByteBuf.decorator(helper.getLevel().registryAccess()).apply(new FriendlyByteBuf(Unpooled.buffer()));

        ItemStack.STREAM_CODEC.encode(registry, stack);
        var stackOverWire = ItemStack.STREAM_CODEC.decode(registry);

        AtomicReference<IEnergyStorage> storage = new AtomicReference<>();
        stackOverWire.getCapability(ForgeCapabilities.ENERGY).ifPresent(storage::set);

        helper.assertValueEqual(
                storage.get() == null ? -1 : storage.get().getEnergyStored(),
                11,
                Component.literal("Value did not Sync to client")
        );

        helper.succeed();
    }

    public static final class MyEnergyStorage extends EnergyStorage {

        private final Consumer<EnergyStorage> consumer;

        public MyEnergyStorage(int capacity, Consumer<EnergyStorage> consumer) {
            super(capacity);
            this.consumer = consumer;
        }

        public MyEnergyStorage(int capacity, int maxTransfer, Consumer<EnergyStorage> consumer) {
            super(capacity, maxTransfer);
            this.consumer = consumer;
        }

        public MyEnergyStorage(int capacity, int maxReceive, int maxExtract, Consumer<EnergyStorage> consumer) {
            super(capacity, maxReceive, maxExtract);
            this.consumer = consumer;
        }

        public MyEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy, Consumer<EnergyStorage> consumer) {
            super(capacity, maxReceive, maxExtract, energy);
            this.consumer = consumer;
        }

        static <T, V> T doAndReturn(T value, V value2, Consumer<V> consumer) {
            consumer.accept(value2);
            return value;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return doAndReturn(super.receiveEnergy(maxReceive, simulate), this, consumer);
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return doAndReturn(super.extractEnergy(maxExtract, simulate), this, consumer);
        }
    }

    public static final class MyProvider implements ICapabilityProvider {
        private final EnergyStorage storage;
        private final LazyOptional<EnergyStorage> storageLazyOptional;

        public MyProvider(ItemStack stack) {
            this.storage = new MyEnergyStorage(1000, 10, 10, stack.getOrDefault(STORAGE.get(), 10), storage -> {
                stack.set(STORAGE.get(), storage.getEnergyStored());
            });
            this.storageLazyOptional = LazyOptional.of(() -> storage);
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if (cap == ForgeCapabilities.ENERGY) {
                return storageLazyOptional.cast();
            }
            return LazyOptional.empty();
        }
    }
}
