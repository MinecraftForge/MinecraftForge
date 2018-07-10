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

import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = IsBookEnchantableTest.MOD_ID, name = "Test for isBookEnchantable", version = "1.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class IsBookEnchantableTest
{
    public static final boolean ENABLED = false;
    static final String MOD_ID = "is_book_enchantable_test";

    private static final Item TEST_ITEM = new TestItem();

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event)
    {
        if (ENABLED)
        {
            event.getRegistry().register(TEST_ITEM);
        }
    }

    @Mod.EventBusSubscriber(value = Side.CLIENT, modid = MOD_ID)
    public static class ClientEventHandler
    {
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            if (ENABLED)
            {
                ModelBakery.registerItemVariants(TEST_ITEM);
            }
        }
    }

    private static class TestItem extends Item
    {
        private static final String NAME = "test_item";

        private TestItem()
        {
            maxStackSize = 1;
            setUnlocalizedName(MOD_ID + "." + NAME);
            setRegistryName(NAME);
            setCreativeTab(CreativeTabs.MISC);
        }

        @Override
        public boolean isEnchantable(ItemStack stack)
        {
            return true;
        }

        @Override
        public int getItemEnchantability()
        {
            return 15;
        }

        @Override
        public boolean isBookEnchantable(ItemStack stack, ItemStack book)
        {
            return false;
        }

        @Override
        public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
        {
            return true;
        }
    }
}
