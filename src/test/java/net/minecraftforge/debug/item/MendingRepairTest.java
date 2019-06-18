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

package net.minecraftforge.debug.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

//@Mod.EventBusSubscriber
//@Mod(modid = MendingRepairTest.MOD_ID, name = "Mending repair amount test mod", version = "1.0")
public class MendingRepairTest
{
    static final boolean ENABLED = true;
    static final String MOD_ID = "mending_repair_test";

    @GameRegistry.ObjectHolder(MOD_ID + ":test_item")
    public static final Item TEST_ITEM = null;

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        if (!ENABLED) return;
        event.getRegistry().register(
                new TestItem()
                        .setRegistryName(MOD_ID, "test_item")
                        .setUnlocalizedName(MOD_ID + ".test_item")
        );
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, value = Side.CLIENT)
    public static final class ClientEventHandler
    {
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            if (!ENABLED) return;
            ModelLoader.setCustomModelResourceLocation(TEST_ITEM, 0, new ModelResourceLocation("minecraft:blaze_rod", "inventory"));
        }
    }

    private static final class TestItem extends Item
    {
        TestItem()
        {
            maxStackSize = 1;
            setMaxDamage(10);
            setCreativeTab(CreativeTabs.TOOLS);
        }

        @Override
        public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
        {
            if (this.isInCreativeTab(tab))
            {
                ItemStack stack = new ItemStack(this);
                stack.addEnchantment(Enchantments.MENDING, 1);
                stack.setItemDamage(stack.getMaxDamage());
                items.add(stack);
            }
        }

        @Override
        public float getXpRepairRatio(ItemStack stack)
        {
            return 0.1f;
        }
    }
}