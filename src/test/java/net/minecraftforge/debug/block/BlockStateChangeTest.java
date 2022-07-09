/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.block;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

/**
 * This test mod tests the blockstate update callback which is called after a block's state has changed and neighbors
 * have been notified.
 *
 * To test the callback, place the "blockstate_change_test:test_block" and right click it twice. Each of these three
 * actions must print two log lines, one on the server and one on the client side. Both lines must be equivalent execpt
 * for the side.
 * When placing, the log must show a change from "{minecraft:air}" to "{blockstate_change_test:test_block}[lit=false]".
 * When interacting with the block, the log must show a change from "{blockstate_change_test:test_block}[lit=false]" to
 * "{blockstate_change_test:test_block}[lit=true]" or viceversa in accordance with the change of the visual state of
 * the block in-game.
 */
@Mod(BlockStateChangeTest.MOD_ID)
public class BlockStateChangeTest
{
    public static final String MOD_ID = "blockstate_change_test";

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final boolean ENABLED = true;

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    private static final RegistryObject<Block> TEST_BLOCK = BLOCKS.register("test_block", TestBlock::new);
    private static final RegistryObject<Item> TEST_BLOCK_ITEM = ITEMS.register(
            "test_block",
            () -> new BlockItem(TEST_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_MISC))
    );

    public BlockStateChangeTest()
    {
        if (ENABLED)
        {
            IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
            BLOCKS.register(bus);
            ITEMS.register(bus);
        }
    }

    private static class TestBlock extends Block
    {
        public TestBlock()
        {
            super(Properties.of(Material.STONE));
            registerDefaultState(defaultBlockState().setValue(BlockStateProperties.LIT, false));
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
        {
            builder.add(BlockStateProperties.LIT);
        }

        @Override
        public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
        {
            if (!level.isClientSide())
            {
                boolean lit = state.getValue(BlockStateProperties.LIT);
                level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.LIT, !lit));
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        @Override
        public void onBlockStateChange(LevelReader level, BlockPos pos, BlockState oldState, BlockState newState)
        {
            LOGGER.info("TestBlock at {} changed from {} to {} on side '{}'", pos, oldState, newState, level.isClientSide() ? "CLIENT" : "SERVER");
        }
    }
}
