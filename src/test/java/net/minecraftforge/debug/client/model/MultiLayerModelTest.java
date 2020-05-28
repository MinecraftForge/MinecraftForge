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

package net.minecraftforge.debug.client.model;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;

@Mod(MultiLayerModelTest.MODID)
public class MultiLayerModelTest
{
    private static final boolean ENABLED = true;
    public static final String MODID = "forgedebugmultilayermodel";
    public static final String VERSION = "0.0";

    @ObjectHolder("test_layer_block_new")
    public static final Block TEST_BLOCK_NEW = null;

    public MultiLayerModelTest()
    {
        if (!ENABLED)
            return;
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.register(Registration.class);
    }

    private static class MultiLayerBlock extends Block
    {
        public MultiLayerBlock()
        {
            super(Properties.create(Material.CLAY));
        }

        @Override
        public boolean canRenderInLayer(BlockState state, BlockRenderLayer layer)
        {
            return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.TRANSLUCENT;
        }
    }

    public static class Registration
    {
        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event)
        {
            event.getRegistry().registerAll(
                    new MultiLayerBlock().setRegistryName(new ResourceLocation(MODID, "test_layer_block_new"))
            );
        }

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            event.getRegistry().registerAll(
                    new BlockItem(TEST_BLOCK_NEW, new Item.Properties().group(ItemGroup.MISC)).setRegistryName(TEST_BLOCK_NEW.getRegistryName())
            );
        }
    }
}
