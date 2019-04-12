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

package net.minecraftforge.debug.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber
@Mod(modid = CustomRarityTest.MOD_ID, name = "Custom rarity test mod", version = "1.0", acceptableRemoteVersions = "*")
public class CustomRarityTest
{
    static final String MOD_ID = "custom_rarity_test";

    @GameRegistry.ObjectHolder("test_item")
    public static final Item TEST_ITEM = null;

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new TestItem()
                .setRegistryName(MOD_ID, "test_item")
                .setUnlocalizedName(MOD_ID + ".test_item")
                .setCreativeTab(CreativeTabs.MISC)
        );
    }

    @Mod.EventBusSubscriber(value = Side.CLIENT, modid = MOD_ID)
    public static class ClientEventHandler
    {
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            ModelLoader.setCustomModelResourceLocation(TEST_ITEM, 0, new ModelResourceLocation("minecraft:book#inventory"));
        }
    }

    static final class TestItem extends Item
    {
        private static final IRarity RARITY = new IRarity()
        {
            @Override
            public TextFormatting getColor()
            {
                return TextFormatting.RED;
            }

            @Override
            public String getName()
            {
                return "Test";
            }
        };

        @Override
        public IRarity getForgeRarity(ItemStack stack)
        {
            return RARITY;
        }
    }
}
