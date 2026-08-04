/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;

@Mod(PathNodeTypeTest.MOD_ID)
@Mod.EventBusSubscriber
public class PathNodeTypeTest
{
    static final String MOD_ID = "ai_node_type_test";
    static final String BLOCK_ID = "test_block";


    @ObjectHolder(BLOCK_ID)
    private static Block TEST_BLOCK = null;

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().register((new Block(Block.Properties.create(Material.ROCK))
        {
            @Override
            public PathNodeType getAiPathNodeType(BlockState state, IBlockReader world, BlockPos pos, @Nullable MobEntity entity)
            {
                return PathNodeType.DOOR_OPEN;
            }
        }).setRegistryName(MOD_ID, BLOCK_ID));
    }

    /*
    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MOD_ID)
    public static class ClientEventHandler
    {
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            if (ENABLED)
            {
                ModelLoader.setCustomStateMapper(TEST_BLOCK, block -> Collections.emptyMap());
                ModelBakery.registerItemVariants(Item.getItemFromBlock(TEST_BLOCK));
            }
        }
    }
    */
}
