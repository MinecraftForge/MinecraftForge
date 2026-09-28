/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.gameplay.loot;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.test.BaseTestMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.common.data.RegistryDataBuilder;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.gametest.GameTest;
import net.minecraftforge.gametest.GameTestNamespace;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Set;

@GameTestNamespace("forge")
@Mod(ConditionalLootPools.MODID)
public class ConditionalLootPools extends BaseTestMod {
    public static final String MODID = "conditional_loot_test";

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final RegistryObject<Block> TEST_BLOCK = BLOCKS.register("test", () -> new Block(name(MODID, "test", BlockBehaviour.Properties.of())));

    public ConditionalLootPools(FMLJavaModLoadingContext context) {
        super(context, false, true);
        GatherDataEvent.getBus(modBus).addListener(this::gatherData);
    }

    public void gatherData(GatherDataEvent event) {
        var registries = RegistryDataBuilder.of()
            .name(MODID)
            .reloadable(set -> set
                .add(Registries.LOOT_TABLE, new LootTableProvider(Set.of(), List.of(
                    new LootTableProvider.SubProviderEntry(BlockLoot::new, LootContextParamSets.BLOCK)
                )))
            );

        event.getGenerator().addProvider(event.includeServer(), registries.reloadableGenerator(event.getGenerator().getPackOutput()));
    }

    @GameTest
    public static void test_true_false(GameTestHelper helper) {
        var center = new BlockPos(1, 1, 1);
        helper.setBlock(center, TEST_BLOCK.get());
        helper.assertBlock(center, block -> block == TEST_BLOCK.get(), block -> Component.literal("Failed to set block, was " + block.getDescriptionId()));

        var player = helper.makeMockServerPlayerFull(GameType.SURVIVAL);
        player.gameMode.destroyBlock(helper.absolutePos(center));

        helper.assertItemEntityPresent(Items.GOLDEN_APPLE, center, 1.0);

        helper.succeed();
    }

    private static class BlockLoot extends BlockLootSubProvider implements IConditionBuilder {
        public BlockLoot(final LootTableSubProvider.Context output) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), output);
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
                        .setRolls(ContextIntProviders.exactly(1))
                        .add(LootItem.lootTableItem(Items.DIAMOND_BLOCK))
                )
                .withPool(
                    LootPool.lootPool()
                        .when(TRUE())
                        .setRolls(ContextIntProviders.exactly(1))
                        .add(LootItem.lootTableItem(Items.GOLDEN_APPLE))
                )
            );
        }
    }

}