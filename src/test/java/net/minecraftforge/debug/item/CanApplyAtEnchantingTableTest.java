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
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = CanApplyAtEnchantingTableTest.MODID, name = "CanApplyAtEnchantingTableTest", version = "0.0.0", acceptableRemoteVersions = "*")
public class CanApplyAtEnchantingTableTest
{
    public static final String MODID = "can_apply_at_enchanting_table_test";
    public static final boolean ENABLE = false;

    public static final Item testItem = new Item()
    {
        @Override
        public boolean isEnchantable(ItemStack stack)
        {
            return true;
        }

        @Override
        public int getItemEnchantability()
        {
            return 30;
        }

        @Override
        public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
        {
            return super.canApplyAtEnchantingTable(stack, enchantment) || enchantment == Enchantments.FORTUNE;
        }
    };

    @Mod.EventBusSubscriber(modid = MODID)
    public static class Registration
    {
        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            if (ENABLE)
            {
                event.getRegistry().register(
                        testItem.setRegistryName("test_item")
                                .setUnlocalizedName("FortuneEnchantableOnly")
                                .setMaxStackSize(1));
            }
        }
    }

    @Mod.EventBusSubscriber(value = Side.CLIENT, modid = MODID)
    public static class ClientEventHandler
    {
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            if (ENABLE)
            {
                ModelBakery.registerItemVariants(testItem);
            }
        }
    }
}
