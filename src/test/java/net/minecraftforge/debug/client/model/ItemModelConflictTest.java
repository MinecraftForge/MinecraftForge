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
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber
@Mod(modid = ItemModelConflictTest.MODID, name = "Test mod for model conflicts", version = "1.0", acceptableRemoteVersions = "*")
public class ItemModelConflictTest
{
    public static final String MODID = "item_model_conflict_test";

    @GameRegistry.ObjectHolder(MODID + ":item")
    public static final Item TEST_ITEM = null;

    @GameRegistry.ObjectHolder(MODID + ":test")
    public static final Block TEST_BLOCK = null;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().register(
                new Block(Material.ROCK)
                        .setRegistryName(MODID, "test")
                        .setUnlocalizedName(MODID + ".test")
                        .setCreativeTab(CreativeTabs.BUILDING_BLOCKS)
        );
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(
                new Item()
                        .setRegistryName(MODID, "item")
                        .setUnlocalizedName(MODID + ".item")
                        .setCreativeTab(CreativeTabs.MISC),
                new ItemBlock(TEST_BLOCK)
                        .setRegistryName(TEST_BLOCK.getRegistryName())
        );
    }

    @Mod.EventBusSubscriber(modid = MODID, value = Side.CLIENT)
    public static class ClientEventHandler
    {
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TEST_BLOCK), 0, new ModelResourceLocation(TEST_BLOCK.getRegistryName(), "normal"));
            ModelLoader.setCustomModelResourceLocation(TEST_ITEM, 0, new ModelResourceLocation(MODID + ":test", "inventory"));
        }
    }
}
