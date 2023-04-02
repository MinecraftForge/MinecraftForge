/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.world;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import java.util.UUID;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

@Mod(ForgeChunkManagerTest.MODID)
public class ForgeChunkManagerTest
{
    public static final String MODID = "forge_chunk_manager_test";
    private static final Logger LOGGER = LogManager.getLogger(MODID);
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final RegistryObject<Block> CHUNK_LOADER_BLOCK = BLOCKS.register("chunk_loader", () -> new ChunkLoaderBlock(Properties.of().mapColor(MapColor.STONE)));
    private static final RegistryObject<Item> CHUNK_LOADER_ITEM = ITEMS.register("chunk_loader", () -> new BlockItem(CHUNK_LOADER_BLOCK.get(), new Item.Properties()));

    public ForgeChunkManagerTest()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        modEventBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS)
            event.accept(CHUNK_LOADER_ITEM);
    }

    private void commonSetup(FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> ForgeChunkManager.setForcedChunkLoadingCallback(MODID, (world, ticketHelper) -> {
            for (Map.Entry<BlockPos, Pair<LongSet, LongSet>> entry : ticketHelper.getBlockTickets().entrySet())
            {
                BlockPos key = entry.getKey();
                int ticketCount = entry.getValue().getFirst().size();
                int tickingTicketCount = entry.getValue().getSecond().size();
                if (world.getBlockState(key).is(CHUNK_LOADER_BLOCK.get()))
                    LOGGER.info("Allowing {} chunk tickets and {} ticking chunk tickets to be reinstated for position: {}.", ticketCount, tickingTicketCount, key);
                else
                {
                    ticketHelper.removeAllTickets(key);
                    LOGGER.info("Removing {} chunk tickets and {} ticking chunk tickets for no longer valid position: {}.", ticketCount, tickingTicketCount, key);
                }
            }
            for (Map.Entry<UUID, Pair<LongSet, LongSet>> entry : ticketHelper.getEntityTickets().entrySet())
            {
                UUID key = entry.getKey();
                int ticketCount = entry.getValue().getFirst().size();
                int tickingTicketCount = entry.getValue().getSecond().size();
                LOGGER.info("Allowing {} chunk tickets and {} ticking chunk tickets to be reinstated for entity: {}.", ticketCount, tickingTicketCount, key);
            }
        }));
    }

    private static class ChunkLoaderBlock extends Block
    {

        public ChunkLoaderBlock(Properties properties)
        {
            super(properties);
        }

        @Override
        public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
        {
            super.onPlace(state, worldIn, pos, oldState, isMoving);
            if (worldIn instanceof ServerLevel)
            {
                ChunkPos chunkPos = new ChunkPos(pos);
                ForgeChunkManager.forceChunk((ServerLevel) worldIn, MODID, pos, chunkPos.x, chunkPos.z, true, false);
            }
        }

        @Deprecated
        public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving)
        {
            super.onRemove(state, worldIn, pos, newState, isMoving);
            if (worldIn instanceof ServerLevel && !state.is(newState.getBlock()))
            {
                ChunkPos chunkPos = new ChunkPos(pos);
                ForgeChunkManager.forceChunk((ServerLevel) worldIn, MODID, pos, chunkPos.x, chunkPos.z, false, false);
            }
        }
    }
}
