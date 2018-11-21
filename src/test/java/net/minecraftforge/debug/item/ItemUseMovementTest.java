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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = ItemUseMovementTest.MODID, name = "Item Use Movement Test", version = "1.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber(modid = ItemUseMovementTest.MODID)
public class ItemUseMovementTest
{
    public static  final String MODID = "iumtest";
    private static final boolean ENABLED = false;
    public static final Item LIGHTSTEAK = new ItemLightFood(3, false).setAlwaysEdible().setRegistryName("light_steak");

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        if(ENABLED) event.getRegistry().registerAll(LIGHTSTEAK);
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        if(ENABLED) ModelLoader.setCustomModelResourceLocation(LIGHTSTEAK, 0, new ModelResourceLocation("minecraft:cooked_beef"));
    }

    private static class ItemLightFood extends ItemFood
    {
        public ItemLightFood(int amount, boolean isWolfFood)
        {
            super(amount, isWolfFood);
        }

        @Override
        public float getSpeedModifier(ItemStack stack, EntityLivingBase entity)
        {
            return 1F;
        }

        @Override
        public boolean shouldPreventSprinting(ItemStack stack, EntityLivingBase entity)
        {
            return false;
        }
    }
}