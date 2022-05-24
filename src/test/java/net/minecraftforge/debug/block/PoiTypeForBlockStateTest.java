/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.block;

import net.minecraft.Util;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;
import java.util.Set;

/**
 * Tests IForgeBlock#getPoiType.
 * To test place both blocks provided by this mod.
 * When right clicked, "test_block" must report the PoiType "beehive" and must report that it is a beehive.
 * When right clicked, "block_for_poi" must report the PoiType "custom_poi" and must report that it is not a beehive.
 *
 * Then set ENABLE_COLLISION to true. In this case the game must not start and must report an error, because "block_for_poi"
 * is assigned to "custom_poi" twice.
 */
@Mod(PoiTypeForBlockStateTest.MODID)
public class PoiTypeForBlockStateTest
{
    public static final boolean ENABLE = true;
    public static final boolean ENABLE_COLLISION = false;
    public static final String MODID = "poi_type_for_block_state_test";

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final RegistryObject<Block> BLOCK_FOR_POI = BLOCKS.register("block_for_poi", () -> new CustomBlock2(Properties.of(Material.WOOD)));
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    static
    {
        BLOCKS.register("test_block", () -> new CustomBlock(Properties.of(Material.WOOD)));
        for (var block : BLOCKS.getEntries())
        {
            ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
        }
    }

    private static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, MODID);
    private static final RegistryObject<PoiType> CUSTOM_POI_TYPE = POI_TYPES.register("custom_poi", () -> new PoiType(
            MODID + ":" + "custom_poi_type",
            Set.of(BLOCK_FOR_POI.get().defaultBlockState()),
            1, 1
    ));

    public PoiTypeForBlockStateTest()
    {
        if (ENABLE)
        {
            final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
            BLOCKS.register(eventBus);
            ITEMS.register(eventBus);
            POI_TYPES.register(eventBus);

            MinecraftForge.EVENT_BUS.addListener(this::onBlockRightClick);
        }
    }

    private void onBlockRightClick(PlayerInteractEvent.RightClickBlock event)
    {
        if (event.getHand() == InteractionHand.MAIN_HAND && event.getWorld() instanceof ServerLevel level)
        {
            var poiType = level.getPoiManager().getType(event.getPos()).map(PoiType::getRegistryName).orElse(null);
            event.getPlayer().sendMessage(new TextComponent("PoiType: " + poiType), Util.NIL_UUID);
            event.getPlayer().sendMessage(new TextComponent("Is Beehive? " + (PoiType.BEEHIVE.is(event.getWorld().getBlockState(event.getPos())))), Util.NIL_UUID);
        }
    }

    public static class CustomBlock extends Block
    {

        public CustomBlock(Properties properties)
        {
            super(properties);
        }

        @Override
        public Optional<PoiType> getPoiType(BlockState state)
        {
            return Optional.of(PoiType.BEEHIVE);
        }
    }

    public static class CustomBlock2 extends Block
    {

        public CustomBlock2(Properties properties)
        {
            super(properties);
        }

        @Override
        public Optional<PoiType> getPoiType(BlockState state)
        {
            return ENABLE_COLLISION ? Optional.of(CUSTOM_POI_TYPE.get()) : Optional.empty();
        }
    }

}
