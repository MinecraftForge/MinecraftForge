/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.gameplay.item;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.golem.CopperGolem;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTest;
import net.minecraftforge.gametest.GameTestNamespace;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.test.BaseTestMod;

@Mod(ShearsBehaviorTest.MOD_ID)
@GameTestNamespace("forge")
public class ShearsBehaviorTest extends BaseTestMod {
    public static final String MOD_ID = "shears_behavior";

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final RegistryObject<Item> CUSTOM_SHEARS_ITEM = ITEMS.register("custom_shears_item", () -> new ShearsItem(
            new Item.Properties().stacksTo(1).setId(ITEMS.key("custom_shears_item"))
    ));
    private static final RegistryObject<Item> CUSTOM_SHEARS_HARVEST_ITEM = ITEMS.register("custom_shears_harvest_item", () -> new ShearsHarvestItem(
            new Item.Properties().stacksTo(1).setId(ITEMS.key("custom_shears_harvest_item"))
    ));

    public ShearsBehaviorTest(FMLJavaModLoadingContext context) {
        super(context, false, true);
        this.testItem(_ -> CUSTOM_SHEARS_ITEM.get().getDefaultInstance());
        this.testItem(_ -> CUSTOM_SHEARS_HARVEST_ITEM.get().getDefaultInstance());
    }

    @GameTest
    public static void custom_shears_unleash_entity(GameTestHelper helper) {
        helper.makeFloor();

        // setup: fence + knot + leash cow
        var fencePos = new BlockPos(1, 1, 1);
        var cowPos = new BlockPos(0, 1, 1);
        helper.setBlock(fencePos, Blocks.OAK_FENCE);
        var cow = helper.spawnWithNoFreeWill(EntityTypes.COW, cowPos);
        var knot = LeashFenceKnotEntity.getOrCreateKnot(helper.getLevel(), helper.absolutePos(fencePos));
        cow.setLeashedTo(knot, true);
        helper.assertTrue(cow.isLeashed(), "Cow should start leashed");

        // act: interact with cow using custom shears
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var shears = CUSTOM_SHEARS_HARVEST_ITEM.get().getDefaultInstance();
        player.setItemInHand(InteractionHand.MAIN_HAND, shears);
        var result = player.interactOn(cow, InteractionHand.MAIN_HAND, Vec3.ZERO);

        // assert
        helper.assertTrue(result.consumesAction(), "Using custom shears on leashed cow should result in consume action");
        helper.assertTrue(!cow.isLeashed(), "Cow should be unleashed by custom shears");
        helper.assertItemEntityPresent(Items.LEAD);

        helper.succeed();
    }


    @GameTest
    public static void custom_shears_unleash_knot(GameTestHelper helper) {
        helper.makeFloor();

        // setup: fence + knot + leash cow
        var fencePos = new BlockPos(1, 1, 1);
        var cowPos = new BlockPos(0, 1, 1);
        helper.setBlock(fencePos, Blocks.OAK_FENCE);
        var cow = helper.spawnWithNoFreeWill(EntityTypes.COW, cowPos);
        var knot = LeashFenceKnotEntity.getOrCreateKnot(helper.getLevel(), helper.absolutePos(fencePos));
        cow.setLeashedTo(knot, true);
        helper.assertTrue(cow.isLeashed(), "Cow should start leashed");

        // act: interact with knot using custom shears
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var shears = CUSTOM_SHEARS_HARVEST_ITEM.get().getDefaultInstance();
        player.setItemInHand(InteractionHand.MAIN_HAND, shears);
        var result = player.interactOn(knot, InteractionHand.MAIN_HAND, Vec3.ZERO);

        // assert
        helper.assertTrue(result.consumesAction(), "Using custom shears on leash knot should result in consume action");
        helper.assertTrue(!cow.isLeashed(), "Cow should be unleashed by sheared knot");
        helper.assertItemEntityPresent(Items.LEAD);

        helper.succeed();
    }

    @GameTest
    public static void custom_shears_shear_copper_golem_poppy(GameTestHelper helper) {
        helper.makeFloor();

        // setup: copper golem with poppy
        var golemPos = new BlockPos(1, 1, 1);
        var golem = helper.spawnWithNoFreeWill(EntityTypes.COPPER_GOLEM, golemPos);
        golem.setItemSlot(CopperGolem.EQUIPMENT_SLOT_ANTENNA, new ItemStack(Items.POPPY));
        helper.assertTrue(golem.readyForShearing(), "Golem should start shearable (has poppy)");

        // act: interact with copper golem using custom shears
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var shears = CUSTOM_SHEARS_ITEM.get().getDefaultInstance();
        player.setItemInHand(InteractionHand.MAIN_HAND, shears);
        var result = player.interactOn(golem, InteractionHand.MAIN_HAND, Vec3.ZERO);

        // assert
        helper.assertTrue(result.consumesAction(), "Using custom shears on copper golem should result in consume action");
        helper.assertTrue(golem.getItemBySlot(CopperGolem.EQUIPMENT_SLOT_ANTENNA).isEmpty(), "Copper golem poppy should be sheared off with custom shears");
        helper.assertItemEntityPresent(Items.POPPY);

        helper.succeed();
    }

    private static final class ShearsHarvestItem extends Item {
        ShearsHarvestItem(Item.Properties properties) {
            super(properties);
        }

        @Override
        public boolean canPerformAction(ItemStack stack, ToolAction action) {
            return action == ToolActions.SHEARS_HARVEST;
        }
    }

}
