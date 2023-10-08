/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.gameplay.loot;

import net.minecraft.core.BlockPos;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.test.BaseTestMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Set;

@Mod(ConditionalLootPools.MODID)
@GameTestHolder("forge." + ConditionalLootPools.MODID)
public class ConditionalLootPools extends BaseTestMod {
    public static final String MODID = "conditional_loot_test";

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final RegistryObject<Block> TEST_BLOCK = BLOCKS.register("test", () -> new Block(BlockBehaviour.Properties.of()));

    @SubscribeEvent
    public void runData(GatherDataEvent event) {
        event.getGenerator().addProvider(event.includeServer(), new LootProvider(event.getGenerator().getPackOutput()));
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void test_true_false(GameTestHelper helper) {
        var center = new BlockPos(1, 1, 1);
        helper.setBlock(center, TEST_BLOCK.get());
        helper.assertBlock(center, block -> block == TEST_BLOCK.get(), "Failed to set block");

        var player = helper.makeMockServerPlayer();
        player.gameMode.destroyBlock(helper.absolutePos(center));

        helper.assertItemEntityPresent(Items.GOLDEN_APPLE, center, 1.0);

        helper.succeed();
    }

    private static class LootProvider extends LootTableProvider {
        public LootProvider(PackOutput out) {
            super(out, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(BlockLoot::new, LootContextParamSets.BLOCK)
            ));
        }
    }

    private static class BlockLoot extends BlockLootSubProvider implements IConditionBuilder {
        public BlockLoot() {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags());
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
           return List.of(TEST_BLOCK.get());
        }

        @Override
        protected void generate() {
            this.add(TEST_BLOCK.get(), LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .when(FALSE())
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(Items.DIAMOND_BLOCK))
                )
                .withPool(
                    LootPool.lootPool()
                        .when(TRUE())
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(Items.GOLDEN_APPLE))
                )
            );
        }
    }

}