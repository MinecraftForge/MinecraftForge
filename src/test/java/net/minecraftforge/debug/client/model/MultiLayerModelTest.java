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
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ObjectHolder;

@Mod(MultiLayerModelTest.MODID)
public class MultiLayerModelTest
{
    private static final boolean ENABLED = true;
    public static final String MODID = "forgedebugmultilayermodel";
    public static final String VERSION = "0.0";

    private static final String blockName = "test_layer_block";
    private static final ResourceLocation blockId = new ResourceLocation(MODID, blockName);
    @ObjectHolder(blockName)
    public static final Block TEST_BLOCK = null;

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Registration
    {
        @net.minecraftforge.eventbus.api.SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event)
        {
            if (!ENABLED)
                return;
            event.getRegistry().register(
                new Block(Block.Properties.create(Material.WOOD))
                {
                }.setRegistryName(blockId)
            );
        }

        @net.minecraftforge.eventbus.api.SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            if (!ENABLED)
                return;
            event.getRegistry().register(new BlockItem(TEST_BLOCK, new Item.Properties().group(ItemGroup.MISC)).setRegistryName(TEST_BLOCK.getRegistryName()));
        }

        @net.minecraftforge.eventbus.api.SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event)
        {
            if (!ENABLED)
                return;
            RenderTypeLookup.setRenderLayer(TEST_BLOCK, (layer) -> {
                return layer == RenderType.func_228639_c_() || layer == RenderType.func_228645_f_();
            });
        }
    }
}
