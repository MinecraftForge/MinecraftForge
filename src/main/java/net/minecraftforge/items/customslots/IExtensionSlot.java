/*
 * Minecraft Forge
 * Copyright (c) 2017.
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

package net.minecraftforge.items.customslots;

import com.google.common.collect.ImmutableSet;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * Describes an extension slot and its contents.
 */
public interface IExtensionSlot
{
    /**
     * Gets the container that is managing this slot.
     *
     * @return The owning container.
     */
    IExtensionContainer getContainer();

    /**
     * Gets the slot type identifier for this slot.
     *
     * @return The resource name serving as a namespaced identifier
     */
    ResourceLocation getType();

    /**
     * Gets the contents of the slot. The stack is *NOT* required to be of an IExtensionSlotItem!
     *
     * @return The contained stack
     */
    ItemStack getContents();

    /**
     * Sets the contents of the slot. The stack is *NOT* required to be of an IExtensionSlotItem!
     *
     * @param stack The stack to be assigned to the slot
     */
    void setContents(ItemStack stack);

    // Permissions

    /**
     * Queries whether or not the stack can be placed in this slot.
     *
     * @param stack The ItemStack in the slot.
     */
    default boolean canEquip(ItemStack stack)
    {
        IExtensionSlotItem extItem = stack.getCapability(CapabilityExtensionSlotItem.INSTANCE, null);
        return extItem != null
                && IExtensionSlot.isAcceptableSlot(this, extItem, stack)
                && extItem.canEquip(stack, this);
    }

    /**
     * Queries whether or not the stack can be removed from this slot.
     *
     * @param stack The ItemStack in the slot.
     */
    default boolean canUnequip(ItemStack stack)
    {
        IExtensionSlotItem extItem = stack.getCapability(CapabilityExtensionSlotItem.INSTANCE, null);
        return (extItem == null || extItem.canUnequip(stack, this))
                && EnchantmentHelper.getEnchantmentLevel(Enchantments.BINDING_CURSE, stack) <= 0;
    }

    /**
     * Helper method to decide if a slot should accept a certain item
     *
     * @param slot The slot we are testing
     * @param extItem The extension item representing the stack
     * @param stack The stack being tested
     * @return True if the item acceptable slots contains the slot id, or the ANY id.
     */
    static boolean isAcceptableSlot(IExtensionSlot slot, IExtensionSlotItem extItem, ItemStack stack)
    {
        ImmutableSet<ResourceLocation> slots = extItem.getAcceptableSlots(stack);
        return slots.contains(CapabilityExtensionSlotItem.ANY_SLOT) || slots.contains(slot.getType());
    }
}
