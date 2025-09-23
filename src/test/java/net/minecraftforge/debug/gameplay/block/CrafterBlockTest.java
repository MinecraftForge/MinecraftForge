/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.gameplay.block;

import net.minecraftforge.gametest.GameTestHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.test.BaseTestMod;


@Mod(CrafterBlockTest.MOD_ID)
@GameTestHolder("forge." + CrafterBlockTest.MOD_ID)
public class CrafterBlockTest extends BaseTestMod {
    static final String MOD_ID = "crafter_block";

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, MOD_ID);
    private static final RegistryObject<Block> IITEM_BLOCK = BLOCKS.register("iitem", () -> new SimpleIItemHandlerBlock(Block.Properties.of()));

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MOD_ID);
    private static final RegistryObject<BlockEntityType<SimpleIItemHandler>> IITEM_BLOCK_TYPE = BLOCK_TYPES.register("iitem", () -> BlockEntityType.Builder.of(SimpleIItemHandler::new, IITEM_BLOCK.get()).build(null));

    public CrafterBlockTest(FMLJavaModLoadingContext context) {
        super(context);
    }

    // Make sure they can add items to vanilla containers.
    @GameTest(template = "forge:empty3x3x3")
    public static void places_in_chest(GameTestHelper helper) {
        var chestPos = new BlockPos(0, 1, 0);
        helper.setBlock(chestPos, Blocks.CHEST);
        helper.setBlock(BlockPos.ZERO, Blocks.CRAFTER.defaultBlockState().setValue(BlockStateProperties.ORIENTATION, FrontAndTop.UP_NORTH));
        var crafter = (CrafterBlockEntity) helper.getBlockEntity(BlockPos.ZERO);
        crafter.setItem(0, new ItemStack(Blocks.OAK_LOG));
        helper.tickBlock(BlockPos.ZERO);
        helper.assertContainerContains(chestPos, Blocks.OAK_PLANKS.asItem(), 4);
        helper.succeed();
    }

    // Make sure they can add items to IItemHandler BlockEntities that are not WorldlyContainers.
    @GameTest(template = "forge:empty3x3x3")
    public static void places_in_iitemhandler_block(GameTestHelper helper) {
        var chestPos = new BlockPos(0, 1, 0);
        helper.setBlock(chestPos, IITEM_BLOCK.get());
        helper.setBlock(BlockPos.ZERO, Blocks.CRAFTER.defaultBlockState().setValue(BlockStateProperties.ORIENTATION, FrontAndTop.UP_NORTH));
        var crafter = (CrafterBlockEntity) helper.getBlockEntity(BlockPos.ZERO);
        crafter.setItem(0, new ItemStack(Blocks.OAK_LOG));
        helper.tickBlock(BlockPos.ZERO);
        helper.assertItemHandlerContains(chestPos, Blocks.OAK_PLANKS.asItem(), 4);
        helper.succeed();
    }

    private static class SimpleIItemHandlerBlock extends BaseEntityBlock {
        private static final MapCodec<SimpleIItemHandlerBlock> CODEC = simpleCodec(SimpleIItemHandlerBlock::new);

        protected SimpleIItemHandlerBlock(Properties props) {
            super(props);
        }

        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return IITEM_BLOCK_TYPE.get().create(pos, state);
        }

        @Override
        protected MapCodec<? extends BaseEntityBlock> codec() {
            return CODEC;
        }
    }

    private static class SimpleIItemHandler extends BlockEntity {
        private final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> new ItemStackHandler(1));

        public SimpleIItemHandler(BlockPos pos, BlockState state) {
            super(IITEM_BLOCK_TYPE.get(), pos, state);
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull final Capability<T> cap, final @Nullable Direction side) {
            if (cap == ForgeCapabilities.ITEM_HANDLER)
                return itemHandler.cast();
            return super.getCapability(cap, side);
        }
    }
}
