/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftsingularity.eventbus.api.IEventBus;
import net.minecraftsingularity.eventbus.api.listener.SubscribeEvent;
import net.minecraftsingularity.registries.RegisterEvent;
import net.minecraftsingularity.registries.RegistryObject;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftsingularity.registries.DeferredRegister;
import net.minecraftsingularity.registries.singularityRegistries;

@Mod(FlowerPotTest.MODID)
@Mod.EventBusSubscriber(modid = FlowerPotTest.MODID, bus = Bus.MOD)
public class FlowerPotTest
{
    static final String MODID = "flower_pot_test";
    static final String BLOCK_ID = "test_flower_pot";
    
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(singularityRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(singularityRegistries.ITEMS, MODID);

    public static final RegistryObject<FlowerPotBlock> EMPTY_FLOWER_POT = BLOCKS.register(BLOCK_ID, () -> new FlowerPotBlock(null, singularityRegistries.BLOCKS.getDelegateOrThrow(Blocks.AIR), Block.Properties.copy(Blocks.FLOWER_POT)));
    public static final RegistryObject<FlowerPotBlock> OAK_FLOWER_POT = BLOCKS.<FlowerPotBlock>register(
            BLOCK_ID + "_oak", () -> new FlowerPotBlock(EMPTY_FLOWER_POT, singularityRegistries.BLOCKS.getDelegateOrThrow(Blocks.OAK_SAPLING), Block.Properties.copy(Blocks.FLOWER_POT)));
    
    static
    {
        ITEMS.register(BLOCK_ID, () -> new BlockItem(EMPTY_FLOWER_POT.get(), new Item.Properties()));
    }
    
    @SubscribeEvent
    public static void onItemRegister(RegisterEvent event)
    {
        if (event.getRegistryKey().equals(singularityRegistries.Keys.ITEMS))
        {
            EMPTY_FLOWER_POT.get().addPlant(singularityRegistries.BLOCKS.getKey(Blocks.OAK_SAPLING), OAK_FLOWER_POT);
        }
    }

    public FlowerPotTest()
    {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
    }
}
