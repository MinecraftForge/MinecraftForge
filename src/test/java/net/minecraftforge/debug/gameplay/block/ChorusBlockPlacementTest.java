/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.gameplay.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTest;
import net.minecraftforge.gametest.GameTestNamespace;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.test.BaseTestMod;

@GameTestNamespace("forge")
@Mod(ChorusBlockPlacementTest.MOD_ID)
public class ChorusBlockPlacementTest extends BaseTestMod {
    static final String MOD_ID = "chorus_block_placement";

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    private static final RegistryObject<Block> BLOCK = BLOCKS.register("placeable_on_chorus", () -> new Block(Block.Properties.of().setId(BLOCKS.key("placeable_on_chorus"))));

    public ChorusBlockPlacementTest(FMLJavaModLoadingContext context) {
        super(context, false, true);
        GatherDataEvent.getBus(modBus).addListener(this::gatherData);
    }

    @GameTest
    public static void custom_placeable_block(GameTestHelper helper) {
        var custom = helper.setAssertAndGetBlock(BlockPos.ZERO, BLOCK.get());
        helper.assertTrue(custom.is(Tags.Blocks.CHORUS_ADDITIONALLY_GROWS_ON), () -> "Block %s is not placeable on chorus".formatted(custom.getBlock()));

        helper.setAndAssertBlock(BlockPos.ZERO.above(), Blocks.CHORUS_FLOWER);
        helper.runAfterDelay(1, () -> {
            helper.assertBlockPresent(Blocks.CHORUS_FLOWER, BlockPos.ZERO.above());
            helper.succeed();
        });
    }

    private void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(event.includeServer(), new BlockTags(event));
    }

    private static final class BlockTags extends BlockTagsProvider {
        public BlockTags(GatherDataEvent event) {
            super(event.getGenerator().getPackOutput(), event.getLookupProvider(), MOD_ID, event.getExistingFileHelper());
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            this.tag(Tags.Blocks.CHORUS_ADDITIONALLY_GROWS_ON).add(BLOCK.get());
        }
    }
}
