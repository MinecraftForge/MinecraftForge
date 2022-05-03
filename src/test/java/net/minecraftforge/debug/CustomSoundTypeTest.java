/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(CustomSoundTypeTest.MODID)
public class CustomSoundTypeTest
{
    static final String MODID = "custom_sound_type_test";

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);

    private static final RegistryObject<SoundEvent> TEST_STEP_EVENT = SOUND_EVENTS.register("test_step",
            () -> new SoundEvent(new ResourceLocation(MODID, "block.sound_type_test.step")));
    private static final SoundType TEST_SOUND_TYPE = new ForgeSoundType(1.0F, 1.0F, TEST_STEP_EVENT, TEST_STEP_EVENT, TEST_STEP_EVENT, TEST_STEP_EVENT, TEST_STEP_EVENT);

    private static final RegistryObject<Block> TEST_STEP_BLOCK = BLOCKS.register("test_block",
            () -> new Block(AbstractBlock.Properties.of(Material.WOOD).sound(TEST_SOUND_TYPE)));

    private static final RegistryObject<Item> TEST_STEP_BLOCK_ITEM = ITEMS.register("test_block",
            () -> new BlockItem(TEST_STEP_BLOCK.get(), new Item.Properties().tab(ItemGroup.TAB_MISC)));

    public CustomSoundTypeTest()
    {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        SOUND_EVENTS.register(modEventBus);
    }
}
