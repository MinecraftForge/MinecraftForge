/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.block;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(HideNeighborFaceTest.MOD_ID)
public class HideNeighborFaceTest
{
    public static final String MOD_ID = "hide_neighbor_face_test";

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    private static final RegistryObject<Block> GLASS_SLAB = BLOCKS.register("glass_slab", GlassSlab::new);
    private static final RegistryObject<Item> GLASS_SLAB_ITEM = ITEMS.register("glass_slab", () -> new BlockItem(GLASS_SLAB.get(), new Item.Properties()));

    public HideNeighborFaceTest()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        BLOCKS.register(bus);
        ITEMS.register(bus);
    }

    private static class GlassSlab extends SlabBlock
    {
        public GlassSlab() { super(Properties.copy(Blocks.GLASS)); }

        @Override
        public boolean skipRendering(BlockState state, BlockState neighborState, Direction face)
        {
            SlabType type = state.getValue(TYPE);

            if (neighborState.is(Blocks.GLASS))
            {
                return (type == SlabType.BOTTOM && face == Direction.DOWN) ||
                       (type == SlabType.TOP && face == Direction.UP) ||
                       type == SlabType.DOUBLE;
            }
            else if (neighborState.is(this))
            {
                SlabType neighborType = neighborState.getValue(TYPE);
                return (type != SlabType.BOTTOM && neighborType != SlabType.TOP && face == Direction.UP) ||
                       (type != SlabType.TOP && neighborType != SlabType.BOTTOM && face == Direction.DOWN) ||
                       (type == neighborType && face.getAxis() != Direction.Axis.Y);
            }

            return false;
        }

        @Override
        public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir)
        {
            SlabType type = state.getValue(TYPE);

            if (neighborState.is(Blocks.GLASS))
            {
                return (type == SlabType.BOTTOM && dir == Direction.DOWN) ||
                       (type == SlabType.TOP && dir == Direction.UP) ||
                       type == SlabType.DOUBLE;
            }

            return false;
        }
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientEvents
    {
        @SubscribeEvent
        public static void onClientSetup(final FMLClientSetupEvent event)
        {
            ItemBlockRenderTypes.setRenderLayer(GLASS_SLAB.get(), RenderType.cutout());
        }
    }
}
