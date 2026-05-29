/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.item;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftsingularity.event.ItemAttributeModifierEvent;
import net.minecraftsingularity.eventbus.api.listener.SubscribeEvent;
import net.minecraftsingularity.fml.common.Mod;

@Mod(ItemAttributeModifierTest.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.singularity, modid = ItemAttributeModifierTest.MOD_ID)
public class ItemAttributeModifierTest
{
    public static final String MOD_ID = "item_modifier_test";
    public static final boolean ENABLED = true;
    private static final AttributeModifier MODIFIER = new AttributeModifier(MOD_ID, 10f, Operation.ADDITION);
    
    @SubscribeEvent
    public static void onItemAttribute(ItemAttributeModifierEvent event)
    {
        if (ENABLED && event.getSlotType() == EquipmentSlot.MAINHAND)
        {
            final Item item = event.getItemStack().getItem();
            if (item == Items.APPLE)
            {
                event.addModifier(Attributes.ARMOR, MODIFIER);
            }
            else if (item == Items.GOLDEN_SWORD)
            {
                event.clearModifiers();
            }
        }
    }
}
