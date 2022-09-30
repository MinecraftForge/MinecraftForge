/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.block;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.SignItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.AddValidBlocksToBlockEntityEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.WoodType;

@Mod(CustomSignsTest.MODID)
public class CustomSignsTest
{
    public static final boolean ENABLE = true; // TODO fix
    public static final String MODID = "custom_signs_test";

    public static final WoodType TEST_WOOD_TYPE = WoodType.create(new ResourceLocation(MODID, "test").toString());

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final RegistryObject<StandingSignBlock> TEST_STANDING_SIGN = BLOCKS.register("test_sign", () -> new StandingSignBlock(Properties.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundType.WOOD), CustomSignsTest.TEST_WOOD_TYPE));
    public static final RegistryObject<WallSignBlock> TEST_WALL_SIGN = BLOCKS.register("test_wall_sign", () -> new WallSignBlock(Properties.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundType.WOOD), CustomSignsTest.TEST_WOOD_TYPE));

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<SignItem> TEST_SIGN = ITEMS.register("test_sign", () -> new SignItem((new Item.Properties()).stacksTo(16).tab(CreativeModeTab.TAB_DECORATIONS), TEST_STANDING_SIGN.get(), TEST_WALL_SIGN.get()));

    public CustomSignsTest()
    {
        if (ENABLE)
        {
            final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
            BLOCKS.register(eventBus);
            ITEMS.register(eventBus);

            eventBus.addListener(this::clientSetup);
            eventBus.addListener(this::commonSetup);
            eventBus.addListener(this::onBlockEntityBlocks);
        }
    }

    private void clientSetup(final FMLClientSetupEvent event)
    {
        event.enqueueWork(() -> {
           Sheets.addWoodType(TEST_WOOD_TYPE);
        });
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> WoodType.register(TEST_WOOD_TYPE));
    }

    private void onBlockEntityBlocks(final AddValidBlocksToBlockEntityEvent event)
    {
        event.addValidBlock(BlockEntityType.SIGN, TEST_STANDING_SIGN.get());
        event.addValidBlock(BlockEntityType.SIGN, TEST_WALL_SIGN.get());
    }
}
