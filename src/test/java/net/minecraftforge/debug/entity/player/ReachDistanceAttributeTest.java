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

package net.minecraftforge.debug.entity.player;

import com.google.common.collect.Multimap;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod(modid = ReachDistanceAttributeTest.MODID, name = ReachDistanceAttributeTest.MODID, version = "1.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class ReachDistanceAttributeTest
{
    public static final String MODID = "reachdistanceattributetest";
    private static final Item PLATE = new ExtendedReachPlate().setRegistryName(MODID, "extended_reach_plate");

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> evt) {
        evt.getRegistry().register(PLATE);
    }

    @Mod.EventBusSubscriber(Side.CLIENT)
    public static class ClientEvents
    {
        @net.minecraftforge.eventbus.api.SubscribeEvent
        public static void registerModels(ModelRegistryEvent evt)
        {
            ModelLoader.setCustomModelResourceLocation(PLATE, 0, new ModelResourceLocation("minecraft:diamond_chestplate", "inventory"));
        }
    }

    public static class ExtendedReachPlate extends ItemArmor
    {
        private static final AttributeModifier BOOST = new AttributeModifier("extended reach plate boost", 3, 0);

        public ExtendedReachPlate()
        {
            super(ArmorMaterial.DIAMOND, 3, EntityEquipmentSlot.CHEST);
            setUnlocalizedName("extendedReachPlate");
        }

        @Override
        public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)
        {
            Multimap<String, AttributeModifier> attribs = super.getAttributeModifiers(slot, stack);
            if (slot == this.armorType)
            {
                attribs.put(EntityPlayer.REACH_DISTANCE.getName(), BOOST);
            }
            return attribs;
        }
    }
}
