/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.debug.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod(SlipperinessTest.MOD_ID)
@EventBusSubscriber
public class SlipperinessTest
{
    static final String MOD_ID = "slipperiness_test";
    static final String BLOCK_ID = "test_block";

    @ObjectHolder(BLOCK_ID)
    public static final Block BB_BLOCK = null;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> e)
    {
        e.getRegistry().register((new Block(Block.Properties.create(Material.PACKED_ICE))
        {
            @Override
            public float getSlipperiness(BlockState state, IWorldReader world, BlockPos pos, Entity entity)
            {
                return entity instanceof BoatEntity ? 2 : super.getSlipperiness(state, world, pos, entity);
            }
        }).setRegistryName(MOD_ID, BLOCK_ID));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> e)
    {
        e.getRegistry().register(new BlockItem(BB_BLOCK, new Item.Properties()).setRegistryName(BB_BLOCK.getRegistryName()));
    }

    /*
    @EventBusSubscriber(value = Dist.CLIENT, modid = MOD_ID)
    public static class ClientEventHandler
    {
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            ModelLoader.setCustomStateMapper(BB_BLOCK, block -> Collections.emptyMap());
            ModelBakery.registerItemVariants(Item.getItemFromBlock(BB_BLOCK));
        }
    }
    */
}
