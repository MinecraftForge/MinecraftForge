/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.debug.world;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ForgeChunkManagerTest.MODID)
public class ForgeChunkManagerTest
{
    public static final String MODID = "forge_chunk_manager_test";
    private static final Logger LOGGER = LogManager.getLogger(MODID);
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final RegistryObject<Block> CHUNK_LOADER_BLOCK = BLOCKS.register("chunk_loader", () -> new ChunkLoaderBlock(AbstractBlock.Properties.create(Material.ROCK)));
    private static final RegistryObject<Item> CHUNK_LOADER_ITEM = ITEMS.register("chunk_loader", () -> new BlockItem(CHUNK_LOADER_BLOCK.get(), new Item.Properties().group(ItemGroup.MISC)));

    public ForgeChunkManagerTest()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
    }

    private void commonSetup(FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> ForgeChunkManager.setForcedChunkLoadingCallback(MODID, (world, ticketHelper) -> {
            for (Map.Entry<BlockPos, Pair<LongSet, LongSet>> entry : ticketHelper.getBlockTickets().entrySet())
            {
                BlockPos key = entry.getKey();
                int ticketCount = entry.getValue().getFirst().size() + entry.getValue().getSecond().size();
                if (world.getBlockState(key).isIn(CHUNK_LOADER_BLOCK.get()))
                    LOGGER.info("Allowing {} chunk tickets to be reinstated for position: {}.", ticketCount, key);
                else
                {
                    ticketHelper.removeAllTickets(key);
                    LOGGER.info("Removing {} chunk tickets for no longer valid position: {}.", ticketCount, key);
                }
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
        public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
        {
            super.onBlockAdded(state, worldIn, pos, oldState, isMoving);
            if (worldIn instanceof ServerWorld)
            {
                ChunkPos chunkPos = new ChunkPos(pos);
                ForgeChunkManager.forceChunk((ServerWorld) worldIn, MODID, pos, chunkPos.x, chunkPos.z, true, false);
            }
        }

        @Deprecated
        public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
        {
            super.onReplaced(state, worldIn, pos, newState, isMoving);
            if (worldIn instanceof ServerWorld && !state.isIn(newState.getBlock()))
            {
                ChunkPos chunkPos = new ChunkPos(pos);
                ForgeChunkManager.forceChunk((ServerWorld) worldIn, MODID, pos, chunkPos.x, chunkPos.z, false, false);
            }
        }
    }
}
