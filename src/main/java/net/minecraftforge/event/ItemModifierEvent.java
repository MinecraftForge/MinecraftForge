/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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
package net.minecraftforge.event;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import com.google.common.collect.Multimap;

/**
 * This event is fired when the attributes for an ItemStack are being calculated.
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
@Cancelable
public class ItemModifierEvent extends Event
{
    private final ItemStack stack;
    private final EquipmentSlotType slotType;
    private final Multimap<Attribute, AttributeModifier> modifiers;

    public ItemModifierEvent(ItemStack stack, EquipmentSlotType slotType, Multimap<Attribute, AttributeModifier> modifiers)
    {
        this.stack = stack;
        this.slotType = slotType;
        this.modifiers = modifiers;
    }
    
    public Multimap<Attribute, AttributeModifier> getModifiers()
    {
        return this.modifiers;
    }
    
    public EquipmentSlotType getSlotType()
    {
        return this.slotType;
    }
    
    public ItemStack getItemStack()
    {
        return this.stack;
    }
}