/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/*


package net.minecraftforge.debug.entity.player;

import com.google.common.collect.Multimap;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

//@Mod(modid = SwimSpeedAttributeTest.MODID, name = SwimSpeedAttributeTest.MODID, version = "1.0", acceptableRemoteVersions = "*")
//@Mod.EventBusSubscriber
public class SwimSpeedAttributeTest
{
    public static final String MODID = "swimspeedattributetest";
    private static final Item PLATE = new SwimSpeedPlate().setRegistryName(MODID, "swim_speed_plate");

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> evt) {
        evt.getRegistry().register(PLATE);
    }

    //@Mod.EventBusSubscriber(Side.CLIENT)
    public static class ClientEvents
    {
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent evt)
        {
            ModelLoader.setCustomModelResourceLocation(PLATE, 0, new ModelResourceLocation("minecraft:diamond_chestplate", "inventory"));
        }
    }

    public static class SwimSpeedPlate extends ItemArmor
    {
        private static final AttributeModifier BOOST = new AttributeModifier("swim speed plate boost", 3, 0);

        public SwimSpeedPlate()
        {
            super(ArmorMaterial.DIAMOND, 3, EntityEquipmentSlot.CHEST);
            setUnlocalizedName("swimSpeedPlate");
        }

        @Override
        public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)
        {
            Multimap<String, AttributeModifier> attribs = super.getAttributeModifiers(slot, stack);
            if (slot == this.armorType)
            {
                attribs.put(EntityLivingBase.SWIM_SPEED.getName(), BOOST);
            }
            return attribs;
        }
    }
}
*/
