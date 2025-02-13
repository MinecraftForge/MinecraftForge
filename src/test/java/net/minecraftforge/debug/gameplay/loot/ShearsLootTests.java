/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.gameplay.loot;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.test.BaseTestMod;

@Mod(ShearsLootTests.MODID)
@GameTestHolder("forge." + ShearsLootTests.MODID)
public class ShearsLootTests extends BaseTestMod {
    public static final String MODID = "shears_loot_test";

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final RegistryObject<Item> MODDED_SHEARS = ITEMS.register("modded_shears", ModdedShearsItem::new);

    private static final Block[] SHEARS_DROP_BLOCKS = {
            //leaves
            Blocks.ACACIA_LEAVES,
            Blocks.AZALEA_LEAVES,
            Blocks.BIRCH_LEAVES,
            Blocks.CHERRY_LEAVES,
            Blocks.DARK_OAK_LEAVES,
            Blocks.FLOWERING_AZALEA_LEAVES,
            Blocks.JUNGLE_LEAVES,
            Blocks.MANGROVE_LEAVES,
            Blocks.OAK_LEAVES,
            Blocks.PALE_OAK_LEAVES,
            Blocks.SPRUCE_LEAVES,
            //misc
            Blocks.COBWEB,
            Blocks.DEAD_BUSH,
            Blocks.FERN,
            Blocks.HANGING_ROOTS,
            Blocks.NETHER_SPROUTS,
            Blocks.PALE_HANGING_MOSS,
            Blocks.SEAGRASS,
            Blocks.SHORT_GRASS,
            Blocks.TWISTING_VINES,
            Blocks.VINE,
            Blocks.WEEPING_VINES
    };

    public ShearsLootTests(FMLJavaModLoadingContext context) {
        super(context);
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void test_shears_drop_blocks(GameTestHelper helper) {
        var player = helper.makeMockServerPlayer();
        var top = new BlockPos(1, 0, 1);
        var bottom = new BlockPos(1, 2, 1);
        var center = new BlockPos(1, 1, 1);

        player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(MODDED_SHEARS.get()));
        helper.setBlock(top, Blocks.DIRT);
        helper.assertBlock(top, b -> b == Blocks.DIRT, "Failed to set top dirt block");
        helper.setBlock(bottom, Blocks.DIRT);
        helper.assertBlock(bottom, b -> b == Blocks.DIRT, "Failed to set bottom dirt block");

        for (Block block : SHEARS_DROP_BLOCKS) {
            helper.setBlock(center, block);
            helper.assertBlock(center, b -> b == block, "Failed to set block: " + block);

            player.gameMode.destroyBlock(helper.absolutePos(center));

            helper.assertItemEntityPresent(block.asItem(), center, 1.0);
        }

        helper.succeed();
    }

    private static final class ModdedShearsItem extends ShearsItem {
        public ModdedShearsItem() {
            super(new Item.Properties().setId(ITEMS.key("modded_shears")).component(DataComponents.TOOL, ShearsItem.createToolProperties()));
        }
    }

}