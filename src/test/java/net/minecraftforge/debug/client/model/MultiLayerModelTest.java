/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@Mod(modid = MultiLayerModelTest.MODID, name = "ForgeDebugMultiLayerModel", version = MultiLayerModelTest.VERSION, acceptableRemoteVersions = "*")
public class MultiLayerModelTest
{
    private static final boolean ENABLED = true;
    public static final String MODID = "forgedebugmultilayermodel";
    public static final String VERSION = "0.0";

    private static final String blockName = "test_layer_block";
    private static final ResourceLocation blockId = new ResourceLocation(MODID, blockName);
    @ObjectHolder(blockName)
    public static final Block TEST_BLOCK = null;

    @Mod.EventBusSubscriber(modid = MODID)
    public static class Registration
    {
        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event)
        {
            if (!ENABLED)
                return;
            event.getRegistry().register(
                new Block(Material.WOOD)
                {
                    {
                        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
                        setUnlocalizedName(MODID + "." + blockName);
                        setRegistryName(blockId);
                    }

                    @Override
                    public boolean isOpaqueCube(IBlockState state)
                    {
                        return false;
                    }

                    @Override
                    public boolean isFullCube(IBlockState state)
                    {
                        return false;
                    }

                    @Override
                    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer)
                    {
                        return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.TRANSLUCENT;
                    }
                }
            );
        }

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            event.getRegistry().register(new ItemBlock(TEST_BLOCK).setRegistryName(TEST_BLOCK.getRegistryName()));
        }

        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            if (!ENABLED)
                return;
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TEST_BLOCK), 0, new ModelResourceLocation(blockId, "inventory"));
        }
    }
}
