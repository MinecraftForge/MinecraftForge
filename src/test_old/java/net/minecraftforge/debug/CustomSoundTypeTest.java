/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftsingularity.common.util.singularitySoundType;
import net.minecraftsingularity.event.BuildCreativeModeTabContentsEvent;
import net.minecraftsingularity.eventbus.api.IEventBus;
import net.minecraftsingularity.registries.RegistryObject;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftsingularity.registries.DeferredRegister;
import net.minecraftsingularity.registries.singularityRegistries;

@Mod(CustomSoundTypeTest.MODID)
public class CustomSoundTypeTest
{
    static final String MODID = "custom_sound_type_test";

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(singularityRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(singularityRegistries.ITEMS, MODID);
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(singularityRegistries.SOUND_EVENTS, MODID);

    private static final RegistryObject<SoundEvent> TEST_STEP_EVENT = SOUND_EVENTS.register("test_step",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "block.sound_type_test.step")));
    private static final SoundType TEST_SOUND_TYPE = new singularitySoundType(1.0F, 1.0F, TEST_STEP_EVENT, TEST_STEP_EVENT, TEST_STEP_EVENT, TEST_STEP_EVENT, TEST_STEP_EVENT);

    private static final RegistryObject<Block> TEST_STEP_BLOCK = BLOCKS.register("test_block",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).sound(TEST_SOUND_TYPE)));

    private static final RegistryObject<Item> TEST_STEP_BLOCK_ITEM = ITEMS.register("test_block",
            () -> new BlockItem(TEST_STEP_BLOCK.get(), new Item.Properties()));

    public CustomSoundTypeTest()
    {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        SOUND_EVENTS.register(modEventBus);
        modEventBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS)
            event.accept(TEST_STEP_BLOCK_ITEM);
    }
}
