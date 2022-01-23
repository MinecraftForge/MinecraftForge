/*
 * Minecraft Forge
 * Copyright (c) 2016-2022.
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

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;

@Mod(EnchantmentLevelTest.MODID)
public class EnchantmentLevelTest
{
    public static final String MODID = "enchantment_level_test";

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static RegistryObject<Item> soul_boots = ITEMS.register("soul_boots", () ->
        new ArmorItem(ArmorMaterials.LEATHER, EquipmentSlot.FEET, (new Item.Properties().tab(CreativeModeTab.TAB_MISC)))
        {
            @Override
            public int getEnchantmentLevel(ItemStack stack, Enchantment enchantment)
            {
                if (enchantment == Enchantments.SOUL_SPEED)
                {
                    return 3;
                }
                if (enchantment == Enchantments.BINDING_CURSE)
                {
                    return 1;
                }
                return super.getEnchantmentLevel(stack, enchantment);
            }

            @Override
            public Map<Enchantment,Integer> getAllEnchantments(ItemStack stack)
            {
                Map<Enchantment,Integer> map = super.getAllEnchantments(stack);
                map.put(Enchantments.SOUL_SPEED, 3);
                map.put(Enchantments.BINDING_CURSE, 1);
                return map;
            }
        }
    );

    public EnchantmentLevelTest()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
    }
}
