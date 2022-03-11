/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ObjectHolder;

@Mod(StickyBlockTest.MODID)
@Mod.EventBusSubscriber(bus = Bus.MOD)
public class StickyBlockTest
{
    static final String MODID = "custom_slime_block_test";
    static final String BLOCK_ID = "test_block";

    @ObjectHolder(BLOCK_ID)
    public static Block BLUE_BLOCK;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().register((new Block(Block.Properties.of(Material.STONE))
        {
            @Override
            public boolean isStickyBlock(BlockState state)
            {
                return true;
            }
        }).setRegistryName(MODID, BLOCK_ID));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new BlockItem(BLUE_BLOCK, new Item.Properties()).setRegistryName(MODID, BLOCK_ID));
    }
}
