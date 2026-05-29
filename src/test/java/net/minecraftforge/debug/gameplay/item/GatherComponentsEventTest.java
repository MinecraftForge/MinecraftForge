/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.gameplay.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Items;
import net.minecraftsingularity.event.GatherComponentsEvent;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftsingularity.gametest.GameTest;
import net.minecraftsingularity.gametest.GameTestNamespace;
import net.minecraftsingularity.test.BaseTestMod;

@GameTestNamespace("singularity")
@Mod(GatherComponentsEventTest.MOD_ID)
public class GatherComponentsEventTest extends BaseTestMod {
    public static final String MOD_ID = "gather_components_test_event";

    public GatherComponentsEventTest(FMLJavaModLoadingContext context) {
        super(context, false, false);
        GatherComponentsEvent.Item.BUS.addListener(this::onItem);
    }

    public void onItem(GatherComponentsEvent.Item itemEvent) {
        if (!itemEvent.getOriginalComponentMap().has(DataComponents.FOOD) && itemEvent.getOwner() == Items.IRON_NUGGET) {
            itemEvent.register(DataComponents.FOOD, Foods.APPLE);
        }
    }

    @GameTest
    public static void is_food(GameTestHelper helper) {
        helper.runAfterDelay(5, () -> {
            helper.assertTrue(Items.IRON_NUGGET.components().has(DataComponents.FOOD), "Iron Nugget is not edible, failed to apply DataComponents.FOOD to it.");
            helper.assertFalse(Items.IRON_INGOT.components().has(DataComponents.FOOD), "Iron Ingot is edible, should not have DataComponents.FOOD");
            helper.succeed();
        });

    }
}
