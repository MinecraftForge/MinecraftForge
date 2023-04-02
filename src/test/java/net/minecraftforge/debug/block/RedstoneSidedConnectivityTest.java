/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

@Mod(RedstoneSidedConnectivityTest.MODID)
public class RedstoneSidedConnectivityTest
{
    public static final boolean ENABLE = true;
    static final String MODID = "redstone_sided_connectivity_test";
    static final String BLOCK_ID = "test_east_redstone_connect";

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    private static final RegistryObject<Block> TEST_REDSTONE_BLOCK = BLOCKS.register(BLOCK_ID, EastRedstoneBlock::new);
    private static final RegistryObject<Item> TEST_REDSTONE_BLOCKITEM = ITEMS.register(BLOCK_ID,  () -> new BlockItem(TEST_REDSTONE_BLOCK.get(), new Item.Properties()));

    public RedstoneSidedConnectivityTest()
    {
        if (!ENABLE)
            return;

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        modBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
            event.accept(TEST_REDSTONE_BLOCKITEM);
    }

    private static class EastRedstoneBlock extends Block
    {
        //This block visually connect to redstone dust only on the east side
        //if a furnace block is placed on top of it

        public EastRedstoneBlock()
        {
            super(Properties.of());
        }

        @Override
        public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction)
        {
            //The passed direction is relative to the redstone dust
            //This block connects on the east side relative to this block, which is west for the dust
            return direction == Direction.WEST &&
                    level.getBlockEntity(pos.relative(Direction.UP)) instanceof FurnaceBlockEntity;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving)
        {
            if (pos.relative(Direction.UP).equals(fromPos))
            {
                //Notify neighbors if the redstone connection may change
                state.updateNeighbourShapes(world, pos, UPDATE_ALL);
            }

            super.neighborChanged(state, world, pos, block, fromPos, isMoving);
        }
    }
}
