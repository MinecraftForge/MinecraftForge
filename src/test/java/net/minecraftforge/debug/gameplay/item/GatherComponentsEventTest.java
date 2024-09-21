/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.gameplay.item;

import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.RegisterCapabilityFactoryEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.event.GatherComponentsEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.test.BaseTestMod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@GameTestHolder("forge." + GatherComponentsEventTest.MOD_ID)
@Mod(GatherComponentsEventTest.MOD_ID)
public class GatherComponentsEventTest extends BaseTestMod {
    public static final String MOD_ID = "gather_components_test_event";

    public GatherComponentsEventTest() {
        IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.addListener(this::onItem);
        bus.addListener(this::onEvent);
        bus.addListener(this::onInteract);
    }

    public void onItem(GatherComponentsEvent.Item itemEvent) {
        if (!itemEvent.getOriginalComponentMap().has(DataComponents.FOOD) && itemEvent.getOwner() == Items.IRON_NUGGET) {
            itemEvent.register(DataComponents.FOOD, new FoodProperties(
                    2,
                    7,
                    true,
                    3,
                    Optional.empty(),
                    List.of()
            ));
        }
    }

    public void onEvent(RegisterCapabilityFactoryEvent event) {
        if (event.getObject() == EntityType.PIG) {
            event.register(ResourceLocation.fromNamespaceAndPath("mc", "test"), (pig) -> {
                Pig pig1 = (Pig) pig;

                return new ICapabilityProvider() {
                    LazyOptional<EnergyStorage> STORAGE = LazyOptional.of(() -> new EnergyStorage(1000));


                    @Override
                    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                        if (cap == ForgeCapabilities.ENERGY)
                            return STORAGE.cast();
                        return LazyOptional.empty();
                    }
                };
            });
        }
    }

    public void onInteract(AttackEntityEvent event) {
        var target = event.getTarget();
        var plr = event.getEntity();

        if (target.getCapability(ForgeCapabilities.ENERGY).isPresent()) {
            plr.sendSystemMessage(Component.literal("HAS ENERGY!"));
        }
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void onTestForFood(GameTestHelper helper) {
        helper.assertTrue(Items.IRON_NUGGET.components().has(DataComponents.FOOD), "Iron Nugget is not edible, failed to apply DataComponents.FOOD to it.");
        helper.assertFalse(Items.IRON_INGOT.components().has(DataComponents.FOOD), "Iron Ingot is edible, should not have DataComponents.FOOD");
        helper.succeed();
    }
}
