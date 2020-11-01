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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import net.minecraftforge.eventbus.api.Event;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import com.google.common.collect.Multimap;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * This event is fired when the attributes for an ItemStack are being calculated.
 * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.
 */
public class ItemAttributeModifierEvent extends Event
{
    private final ItemStack stack;
    private final EquipmentSlotType slotType;
    private Multimap<Attribute, AttributeModifier> unmodifiableModifiers;
    @Nullable
    private Multimap<Attribute, AttributeModifier> modifiableModifiers;

    public ItemAttributeModifierEvent(ItemStack stack, EquipmentSlotType slotType, Multimap<Attribute, AttributeModifier> modifiers)
    {
        this.stack = stack;
        this.slotType = slotType;
        this.unmodifiableModifiers = modifiers;
    }

    /**
     * Returns an unmodifiable view of the attribute multimap. Use other methods from this event to modify the attributes map.
     */
    public Multimap<Attribute, AttributeModifier> getModifiers()
    {
        return this.unmodifiableModifiers;
    }

    /**
     * Gets a modifiable map instance, creating it if the current map is currently unmodifiable
     */
    private Multimap<Attribute, AttributeModifier> getModifiableMap() {
        if (modifiableModifiers == null)
        {
            modifiableModifiers = HashMultimap.create(unmodifiableModifiers);
            unmodifiableModifiers = Multimaps.unmodifiableMultimap(modifiableModifiers);
        }
        return modifiableModifiers;
    }

    /**
     * Adds a new attribute modifier to the given stack
     * @param attribute  Attribute
     * @param modifier   Modifier instance
     * @return  True if the attribute was added, false if it was already present
     */
    public boolean addModifier(Attribute attribute, AttributeModifier modifier)
    {
        return getModifiableMap().put(attribute, modifier);
    }

    /**
     * Removes a single modifier for the given attribute
     */
    public boolean removeModifier(Attribute attribute, AttributeModifier modifier)
    {
        return getModifiableMap().remove(attribute, modifier);
    }

    /**
     * Removes all modifiers for the given attribute
     */
    public Collection<AttributeModifier> removeAttribute(Attribute attribute)
    {
        return getModifiableMap().removeAll(attribute);
    }

    /**
     * Removes all modifiers for all attributes
     */
    public void clearModifiers()
    {
        getModifiableMap().clear();
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