/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client.model;

import net.minecraft.block.Block;
import net.minecraft.block.WallBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ObjectHolder;

@Mod(ForgeBlockStatesLoaderTest.MODID)
@Mod.EventBusSubscriber(modid = ForgeBlockStatesLoaderTest.MODID, bus= Mod.EventBusSubscriber.Bus.MOD)
public class ForgeBlockStatesLoaderTest
{
    public static final String MODID = "forgeblockstatesloader";

    @ObjectHolder(MODID)
    public static class BLOCKS
    {
        public static final WallBlock custom_wall = null;
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().registerAll(
                new WallBlock(Block.Properties.create(Material.ROCK))
                        .setRegistryName(MODID, "custom_wall")
        );
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(
               new BlockItem(BLOCKS.custom_wall, new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName(BLOCKS.custom_wall.getRegistryName())
        );
    }
}
