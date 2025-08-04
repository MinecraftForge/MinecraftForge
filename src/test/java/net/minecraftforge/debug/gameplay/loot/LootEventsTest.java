/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.gameplay.loot;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTest;
import net.minecraftforge.gametest.GameTestNamespace;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.test.BaseTestMod;

@Mod(LootEventsTest.MODID)
@GameTestNamespace("forge")
public class LootEventsTest extends BaseTestMod {
    public static final String MODID = "loot_events";
    @SuppressWarnings("unused")
    private static Logger LOGGER = LogManager.getLogger();

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final RegistryObject<Block> TEST_BLOCK = BLOCKS.register("test", () -> new Block(name(MODID, "test", BlockBehaviour.Properties.of())));

    public LootEventsTest(FMLJavaModLoadingContext context) {
        super(context, false, true);
        GatherDataEvent.getBus(modBus).addListener(this::gatherData);
        LootTableLoadEvent.BUS.addListener(ForgeEvents::onLootTableLoad);
    }

    public void gatherData(GatherDataEvent event) {
        var out = event.getGenerator().getPackOutput();
        var lookup = event.getLookupProvider();
        event.getGenerator().addProvider(event.includeServer(), new LootProvider(out, lookup));
    }

    @GameTest
    public static void test_load_table(GameTestHelper helper) {
        var center = new BlockPos(1, 1, 1);
        helper.setBlock(center, TEST_BLOCK.get());
        helper.assertBlock(center, block -> block == TEST_BLOCK.get(), block -> Component.literal("Failed to set block, was " + block.getDescriptionId()));

        var player = helper.makeMockServerPlayer();
        player.gameMode.destroyBlock(helper.absolutePos(center));

        helper.assertItemEntityPresent(Items.GOLDEN_APPLE, center, 1.0);
        helper.assertItemEntityNotPresent(Items.APPLE, center, 1.0);

        helper.succeed();
    }

    private class ForgeEvents {
        public static void onLootTableLoad(LootTableLoadEvent event) {
            if (event.getName().equals(TEST_BLOCK.get().getLootTable().orElse(null).location())) {
                event.getTable().removePool(0);
                event.getTable().addPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1f))
                        .add(LootItem.lootTableItem(Items.GOLDEN_APPLE))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                        .build()
                );
            }
        }
    }

    private static class LootProvider extends LootTableProvider {
        public LootProvider(PackOutput out, CompletableFuture<HolderLookup.Provider> lookup) {
            super(out, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(BlockLoot::new, LootContextParamSets.BLOCK)
            ), lookup);
        }

        private static class BlockLoot extends BlockLootSubProvider implements IConditionBuilder {
            public BlockLoot(HolderLookup.Provider lookup) {
                super(Set.of(), FeatureFlags.REGISTRY.allFlags(), lookup);
            }

            @Override
            protected Iterable<Block> getKnownBlocks() {
               return List.of(TEST_BLOCK.get());
            }

            @Override
            protected void generate() {
                this.dropOther(TEST_BLOCK.get(), Items.APPLE);
            }
        }
    }
}
