/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(FlowerPotTest.MODID)
@Mod.EventBusSubscriber(modid = FlowerPotTest.MODID, bus = Bus.MOD)
public class FlowerPotTest
{
    static final String MODID = "flower_pot_test";
    static final String BLOCK_ID = "test_flower_pot";
    
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<FlowerPotBlock> EMPTY_FLOWER_POT = BLOCKS.register(BLOCK_ID, () -> new FlowerPotBlock(null, Blocks.AIR.delegate, Block.Properties.copy(Blocks.FLOWER_POT)));
    public static final RegistryObject<FlowerPotBlock> OAK_FLOWER_POT = BLOCKS.<FlowerPotBlock>register(
            BLOCK_ID + "_oak", () -> new FlowerPotBlock(EMPTY_FLOWER_POT, Blocks.OAK_SAPLING.delegate, Block.Properties.copy(Blocks.FLOWER_POT)));
    
    static {
        ITEMS.register(BLOCK_ID, () -> new BlockItem(EMPTY_FLOWER_POT.get(), new Item.Properties()));
    }
    
    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event) {
        EMPTY_FLOWER_POT.get().addPlant(Blocks.OAK_SAPLING.getRegistryName(), OAK_FLOWER_POT);
    }

    public FlowerPotTest() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
    }
}
