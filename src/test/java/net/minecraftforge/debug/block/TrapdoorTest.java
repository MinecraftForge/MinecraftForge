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
import net.minecraft.block.LadderBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@SuppressWarnings("unused")
@Mod(TrapdoorTest.MODID)
public class TrapdoorTest
{
    public static final String MODID = "trapdoor_test";

    // Represents modded ladders to be supported
    private static class TrapdoorTestLadder extends LadderBlock
    {
        private TrapdoorTestLadder(Block.Properties builder)
        { super(builder); }
    }

    private static final Block MODDED_TEST_LADDER = (new TrapdoorTestLadder(
      // Using different block properties as the model is the vanilla ladder model.
      Block.Properties.create(Material.WOOL)
           .hardnessAndResistance(0.1f,0.1f)
           .sound(SoundType.CLOTH).lightValue(15)
      )).setRegistryName(MODID, MODID+"_ladder");

    // ------------------------------------------------------------------------------------

    @Mod.EventBusSubscriber(bus = Bus.MOD)
    public static class ForgeEvents
    {
        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event)
        {
            event.getRegistry().register(MODDED_TEST_LADDER);
        }

        @SubscribeEvent
        @SuppressWarnings("all") // blocks statically defined, regnames are not null here.
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            event.getRegistry().register(new BlockItem(MODDED_TEST_LADDER, new Item.Properties()).setRegistryName(MODDED_TEST_LADDER.getRegistryName()));
        }
    }
}
