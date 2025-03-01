/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.gameplay.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.test.BaseTestMod;

@GameTestHolder("forge." + ChorusBlockPlacementTest.MOD_ID)
@Mod(ChorusBlockPlacementTest.MOD_ID)
public class ChorusBlockPlacementTest extends BaseTestMod {
    static final String MOD_ID = "chorus_block_placement";

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    private static final RegistryObject<Block> BLOCK = BLOCKS.register("placeable_on_chorus", () -> new Block(Block.Properties.of()));

    public ChorusBlockPlacementTest(FMLJavaModLoadingContext context) {
        super(context);
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void custom_placeable_block(GameTestHelper helper) {
        var custom = helper.setAndAssertBlock(BlockPos.ZERO, BLOCK.get());
        helper.assertTrue(custom.is(Tags.Blocks.CHORUS_ADDITIONALLY_GROWS_ON), () -> "Block %s is not placeable on chorus".formatted(custom.getBlock()));

        helper.setAndAssertBlock(BlockPos.ZERO.above(), Blocks.CHORUS_FLOWER);
        helper.runAfterDelay(1, () -> {
            helper.assertBlockPresent(Blocks.CHORUS_FLOWER, BlockPos.ZERO.above());
            helper.succeed();
        });
    }

    @SubscribeEvent
    public void runData(GatherDataEvent event) {
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
