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

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber
@Mod(modid = ItemModelGenerationTest.MOD_ID, name = "Item model generation test", version = "1.0", acceptableRemoteVersions = "*")
public class ItemModelGenerationTest
{
    static final String MOD_ID = "item_model_generation_test";

    @GameRegistry.ObjectHolder("animation_test")
    public static final Item ANIMATION_TEST = null;

    @GameRegistry.ObjectHolder("intersection_test")
    public static final Item INTERSECTION_TEST = null;

    @GameRegistry.ObjectHolder("opacity_test")
    public static final Item OPACITY_TEST = null;

    @GameRegistry.ObjectHolder("overlap_test")
    public static final Item OVERLAP_TEST = null;

    @GameRegistry.ObjectHolder("pattern_test")
    public static final Item PATTERN_TEST = null;

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(
                new Item()
                        .setRegistryName("animation_test")
                        .setUnlocalizedName(MOD_ID + ".animation_test")
                        .setCreativeTab(CreativeTabs.MISC),
                new Item()
                        .setRegistryName("intersection_test")
                        .setUnlocalizedName(MOD_ID + ".intersection_test")
                        .setCreativeTab(CreativeTabs.MISC),
                new Item()
                        .setRegistryName("opacity_test")
                        .setUnlocalizedName(MOD_ID + ".opacity_test")
                        .setCreativeTab(CreativeTabs.MISC),
                new Item()
                        .setRegistryName("overlap_test")
                        .setUnlocalizedName(MOD_ID + ".overlap_test")
                        .setCreativeTab(CreativeTabs.MISC),
                new Item()
                        .setRegistryName("pattern_test")
                        .setUnlocalizedName(MOD_ID + ".pattern_test")
                        .setCreativeTab(CreativeTabs.MISC)
        );
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, value = Side.CLIENT)
    public static class ClientEventHandler
    {
        @net.minecraftforge.eventbus.api.SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            setCustomMRL(ANIMATION_TEST);
            setCustomMRL(INTERSECTION_TEST);
            setCustomMRL(OPACITY_TEST);
            setCustomMRL(OVERLAP_TEST);
            setCustomMRL(PATTERN_TEST);
        }

        private static void setCustomMRL(Item item)
        {
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
    }
}
