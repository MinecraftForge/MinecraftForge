/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Mod(CustomRespawnTest.MODID)
public class CustomRespawnTest
{
    public static final boolean ENABLE = true;
    public static final String MODID = "custom_respawn_test";

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final RegistryObject<Block> TEST_RESPAWN_BLOCK = BLOCKS.register("test_respawn_block", () -> new CustomRespawnBlock(Block.Properties.of().mapColor(MapColor.WOOD)));

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> TEST_RESPAWN_BLOCK_ITEM = ITEMS.register("test_respawn_block", () -> new BlockItem(TEST_RESPAWN_BLOCK.get(), (new Item.Properties())));

    public CustomRespawnTest()
    {
        if (ENABLE)
        {
            final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
            BLOCKS.register(eventBus);
            ITEMS.register(eventBus);
        }
    }

    public static class CustomRespawnBlock extends Block
    {

        public CustomRespawnBlock(Properties propertiesIn)
        {
            super(propertiesIn);
        }

        @Override
        public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult)
        {
            if (!world.isClientSide && player instanceof ServerPlayer serverPlayer)
            {
                serverPlayer.setRespawnPosition(world.dimension(), pos, 0, false, true);
            }
            return InteractionResult.sidedSuccess(world.isClientSide);
        }

        @Override
        public Optional<Vec3> getRespawnPosition(BlockState state, EntityType<?> type, LevelReader levelReader, BlockPos pos, float orientation, @Nullable LivingEntity entity)
        {
            return RespawnAnchorBlock.findStandUpPosition(type, levelReader, pos);
        }
    }

}
