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
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ObjectHolder;

@Mod(FlowerPotTest.MODID)
@Mod.EventBusSubscriber(bus = Bus.MOD)
public class FlowerPotTest
{
    static final String MODID = "flower_pot_test";
    static final String BLOCK_ID = "test_flower_pot";

    @ObjectHolder(BLOCK_ID)
    public static FlowerPotBlock EMPTY_FLOWER_POT;

    @ObjectHolder(BLOCK_ID)
    public static FlowerPotBlock OAK_FLOWER_POT;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        FlowerPotBlock empty = new FlowerPotBlock(null, Blocks.AIR, Block.Properties.from(Blocks.FLOWER_POT));
        event.getRegistry().register(empty.setRegistryName(BLOCK_ID));
        event.getRegistry().register(new FlowerPotBlock(empty, Blocks.OAK_SAPLING, Block.Properties.from(empty)).setRegistryName(BLOCK_ID + "_oak"));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new BlockItem(EMPTY_FLOWER_POT, new Item.Properties()).setRegistryName(MODID, BLOCK_ID));
    }
}
