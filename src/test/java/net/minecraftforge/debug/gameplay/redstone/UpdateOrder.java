/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.gameplay.redstone;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.test.BaseTestMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.gametest.GameTestHolder;

/**
 * Redstone update orders are important in many contraptions as there are pistons and other things that could conflict with each other.
 * These tests verify just that, two pistons pushing the same block.
 *
 * Partial verification/fix for https://github.com/MinecraftForge/MinecraftForge/issues/9973
 */
@Mod(UpdateOrder.MODID)
@GameTestHolder("forge." + UpdateOrder.MODID)
public class UpdateOrder extends BaseTestMod {
    public static final String MODID = "update_order";
    private static final int PISTON_DELAY = 4;

    public UpdateOrder(FMLJavaModLoadingContext context) {
        super(context);
    }

    @GameTest(template = "update_order:update_order", rotationSteps = 0)
    public static void south(GameTestHelper helper) {
        var chestPos = new BlockPos(0, 1, 0);
        helper.assertBlockPresent(Blocks.CHEST, chestPos);
        var chest = helper.<ChestBlockEntity>getBlockEntity(chestPos);
        chest.setItem(0, new ItemStack(Items.DIRT));

        helper.runAfterDelay(PISTON_DELAY, () -> {
            var expectedPos = new BlockPos(2, 1, 3);
            helper.assertBlockPresent(Blocks.WHITE_WOOL, expectedPos);
            helper.succeed();
        });
    }

    @GameTest(template = "update_order:update_order", rotationSteps = 1)
    public static void west(GameTestHelper helper) {
        var chestPos = new BlockPos(0, 1, 0);
        helper.assertBlockPresent(Blocks.CHEST, chestPos);
        var chest = helper.<ChestBlockEntity>getBlockEntity(chestPos);
        chest.setItem(0, new ItemStack(Items.DIRT));

        helper.runAfterDelay(PISTON_DELAY, () -> {
            var expectedPos = new BlockPos(2, 1, 3);
            helper.assertBlockPresent(Blocks.WHITE_WOOL, expectedPos);
            helper.succeed();
        });
    }

    @GameTest(template = "update_order:update_order", rotationSteps = 2)
    public static void north(GameTestHelper helper) {
        var chestPos = new BlockPos(0, 1, 0);
        helper.assertBlockPresent(Blocks.CHEST, chestPos);
        var chest = helper.<ChestBlockEntity>getBlockEntity(chestPos);
        chest.setItem(0, new ItemStack(Items.DIRT));

        helper.runAfterDelay(4, () -> {
            var expectedPos = new BlockPos(3, 1, 2);
            helper.assertBlockPresent(Blocks.WHITE_WOOL, expectedPos);
            helper.succeed();
        });
    }

    @GameTest(template = "update_order:update_order", rotationSteps = 3)
    public static void east(GameTestHelper helper) {
        var chestPos = new BlockPos(0, 1, 0);
        helper.assertBlockPresent(Blocks.CHEST, chestPos);
        var chest = helper.<ChestBlockEntity>getBlockEntity(chestPos);
        chest.setItem(0, new ItemStack(Items.DIRT));

        helper.runAfterDelay(PISTON_DELAY, () -> {
            var expectedPos = new BlockPos(2, 1, 3);
            helper.assertBlockPresent(Blocks.WHITE_WOOL, expectedPos);
            helper.succeed();
        });
    }
}