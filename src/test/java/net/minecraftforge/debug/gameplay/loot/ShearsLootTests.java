/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.gameplay.loot;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTest;
import net.minecraftforge.gametest.GameTestNamespace;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.test.BaseTestMod;

@Mod(ShearsLootTests.MODID)
@GameTestNamespace("forge")
public class ShearsLootTests extends BaseTestMod {
    public static final String MODID = "shears_loot";

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final RegistryObject<Item> MODDED_SHEARS = ITEMS.register("modded_shears", () -> new ShearsItem(new Item.Properties()
            .component(DataComponents.TOOL, ShearsItem.createToolProperties())
            .setId(ITEMS.key("modded_shears"))
    ));

    public ShearsLootTests(FMLJavaModLoadingContext context) {
        super(context, false, true);
        this.testItem(lookup -> MODDED_SHEARS.get().getDefaultInstance());
    }

    @GameTest
    public static void test_shears_drop_blocks(GameTestHelper helper) {
        Test[] tests = {
            //leaves
            Test.of(Blocks.ACACIA_LEAVES),
            Test.of(Blocks.AZALEA_LEAVES),
            Test.of(Blocks.BIRCH_LEAVES),
            Test.of(Blocks.CHERRY_LEAVES),
            Test.of(Blocks.DARK_OAK_LEAVES),
            Test.of(Blocks.FLOWERING_AZALEA_LEAVES),
            Test.of(Blocks.JUNGLE_LEAVES),
            Test.of(Blocks.MANGROVE_LEAVES),
            Test.of(Blocks.OAK_LEAVES),
            Test.of(Blocks.PALE_OAK_LEAVES),
            Test.of(Blocks.SPRUCE_LEAVES),

            //misc
            Test.of(Blocks.COBWEB),
            Test.of(Blocks.DEAD_BUSH),
            Test.of(Blocks.FERN),
            Test.of(Blocks.HANGING_ROOTS),
            Test.of(Blocks.NETHER_SPROUTS),
            Test.of(Blocks.PALE_HANGING_MOSS),
            Test.of(Blocks.SEAGRASS),
            Test.of(Blocks.SHORT_GRASS),
            Test.of(Blocks.TWISTING_VINES),
            Test.of(Blocks.VINE),
            Test.of(Blocks.WEEPING_VINES),

            // Tall things that need surrounding blocks
            Test.tall(Blocks.LARGE_FERN, Items.FERN),
            Test.tall(Blocks.TALL_GRASS, Items.SHORT_GRASS),
            Test.tall(Blocks.TALL_SEAGRASS, Items.SEAGRASS),

            // Things that need a state different then default
            Test.of(Blocks.GLOW_LICHEN.defaultBlockState().setValue(BlockStateProperties.NORTH, true), Items.GLOW_LICHEN),
        };

        helper.makeFloor(); // Seagrass makes water
        var player = helper.makeMockServerPlayer(false); // Plants prevent loot for creative players
        var center = new BlockPos(1, 1, 1);

        player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(MODDED_SHEARS.get()));

        for (Test test : tests) {
            helper.setAndAssertBlock(center, test.state());
            if (test.below() != null)
                helper.setAndAssertBlock(center.below(), test.below());

            player.gameMode.destroyBlock(helper.absolutePos(center));

            helper.assertItemEntityPresent(test.item(), center, 1.0);

            // Cleanup after ourselves
            helper.setBlock(center, Blocks.AIR);
            helper.setBlock(center.below(), Blocks.AIR);

            helper.removeAllItemEntitiesInRange(center, 1.0);
        }

        helper.succeed();
    }


    private record Test(BlockState state, @Nullable BlockState below, Item item) {
        private static Test of(Block block) {
            return new Test(block.defaultBlockState(), null, block.asItem());
        }
        private static Test of(BlockState state, Item item) {
            return new Test(state, null, item);
        }
        private static Test tall(Block block, Item item) {
            return new Test(block.defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER), block.defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER), item);
        }
    }
}
