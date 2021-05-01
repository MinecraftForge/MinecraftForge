/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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
 * Attributes are calculated on the server when equipping and unequipping an item to add and remove attributes respectively, both must be consistent.
 * Attributes are calculated on the client when rendering an item's tooltip to show relevant attributes.
 * <br>
 * Note that this event is fired regardless of if the stack has NBT overriding attributes or not. If your attribute should be
 * ignored when attributes are overridden, you can check for the presence of the {@code AttributeModifiers} tag.
 * <br>
 * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.
 */
public class ItemAttributeModifierEvent extends Event
{
    private final ItemStack stack;
    private final EquipmentSlotType slotType;
    private final Multimap<Attribute, AttributeModifier> originalModifiers;
    private Multimap<Attribute, AttributeModifier> unmodifiableModifiers;
    @Nullable
    private Multimap<Attribute, AttributeModifier> modifiableModifiers;

    public ItemAttributeModifierEvent(ItemStack stack, EquipmentSlotType slotType, Multimap<Attribute, AttributeModifier> modifiers)
    {
        this.stack = stack;
        this.slotType = slotType;
        this.unmodifiableModifiers = this.originalModifiers = modifiers;
    }

    /**
     * Returns an unmodifiable view of the attribute multimap. Use other methods from this event to modify the attributes map.
     * Note that adding attributes based on existing attributes may lead to inconsistent results between the tooltip (client)
     * and the actual attributes (server) if the listener order is different. Using {@link #getOriginalModifiers()} instead will give more consistent results.
     */
    public Multimap<Attribute, AttributeModifier> getModifiers()
    {
        return this.unmodifiableModifiers;
    }

    /**
     * Returns the attribute map before any changes from other event listeners was made.
     */
    public Multimap<Attribute, AttributeModifier> getOriginalModifiers()
    {
        return this.originalModifiers;
    }

    /**
     * Gets a modifiable map instance, creating it if the current map is currently unmodifiable
     */
    private Multimap<Attribute, AttributeModifier> getModifiableMap()
    {
        if (this.modifiableModifiers == null)
        {
            this.modifiableModifiers = HashMultimap.create(this.originalModifiers);
            this.unmodifiableModifiers = Multimaps.unmodifiableMultimap(this.modifiableModifiers);
        }
        return this.modifiableModifiers;
    }

    /**
     * Adds a new attribute modifier to the given stack.
     * Modifier must have a consistent UUID for consistency between equipping and unequipping items.
     * Modifier name should clearly identify the mod that added the modifier.
     * @param attribute  Attribute
     * @param modifier   Modifier instance.
     * @return  True if the attribute was added, false if it was already present
     */
    public boolean addModifier(Attribute attribute, AttributeModifier modifier)
    {
        return getModifiableMap().put(attribute, modifier);
    }

    /**
     * Removes a single modifier for the given attribute
     * @param attribute  Attribute
     * @param modifier   Modifier instance
     * @return  True if an attribute was removed, false if no change
     */
    public boolean removeModifier(Attribute attribute, AttributeModifier modifier)
    {
        return getModifiableMap().remove(attribute, modifier);
    }

    /**
     * Removes all modifiers for the given attribute
     * @param attribute  Attribute
     * @return  Collection of removed modifiers
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

    /** Gets the slot containing this stack */
    public EquipmentSlotType getSlotType()
    {
        return this.slotType;
    }

    /** Gets the item stack instance */
    public ItemStack getItemStack()
    {
        return this.stack;
    }
}