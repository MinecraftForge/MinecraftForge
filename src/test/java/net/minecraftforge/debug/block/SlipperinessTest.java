/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod(SlipperinessTest.MOD_ID)
@EventBusSubscriber
public class SlipperinessTest
{
    static final String MOD_ID = "slipperiness_test";
    static final String BLOCK_ID = "test_block";

    @ObjectHolder(BLOCK_ID)
    public static final Block BB_BLOCK = null;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> e)
    {
        e.getRegistry().register((new Block(Block.Properties.of(Material.ICE_SOLID))
        {
            @Override
            public float getFriction(BlockState state, LevelReader world, BlockPos pos, Entity entity)
            {
                return entity instanceof Boat ? 2 : super.getFriction(state, world, pos, entity);
            }
        }).setRegistryName(MOD_ID, BLOCK_ID));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> e)
    {
        e.getRegistry().register(new BlockItem(BB_BLOCK, new Item.Properties()).setRegistryName(BB_BLOCK.getRegistryName()));
    }

    /*
    @EventBusSubscriber(value = Dist.CLIENT, modid = MOD_ID)
    public static class ClientEventHandler
    {
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            ModelLoader.setCustomStateMapper(BB_BLOCK, block -> Collections.emptyMap());
            ModelBakery.registerItemVariants(Item.getItemFromBlock(BB_BLOCK));
        }
    }
    */
}
