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

/**
 * Exposed as a CAPABILITY by items that want to be accepted in special equipment slots, and optionally to provide
 * custom processing for insertion, ticking, etc.
 */
public interface IExtensionSlotItem
{
    /**
     * Returns the list of slot IDs for extension containers. "forge:any" should be accepted by all extension containers
     * and is the default return type. Should be used by extension containers to test for equipability, and to display
     * in tooltips.
     * <p>
     * Ideally, the value would be stored in a field somewhere, and only returned here
     * <p>
     * Example:
     * ImmutableSet.of(new ResourceLocation("baubles:belt"), new ResourceLocation("toolbelt:pocket"));
     *
     * @param stack The ItemStack for which the acceptable slots are being requested.
     * @return An immutable list with the ResourceLocations of the slots.
     */
    default ImmutableSet<ResourceLocation> getAcceptableSlots(ItemStack stack)
    {
        return CapabilityExtensionSlotItem.ANY_SLOT_SET;
    }

    /**
     * Runs once per tick for as long as the item remains equipped in the given slot.
     *
     * @param stack The ItemStack in the slot.
     * @param slot  The slot being referenced.
     */
    default void onWornTick(ItemStack stack, IExtensionSlot slot)
    {
    }

    /**
     * Called when the item is equipped to an extension slot.
     *
     * @param stack The ItemStack in the slot.
     * @param slot  The slot being referenced.
     */
    default void onEquipped(ItemStack stack, IExtensionSlot slot)
    {
    }

    /**
     * Called when the item is removed from an extension slot.
     *
     * @param stack The ItemStack in the slot.
     * @param slot  The slot being referenced.
     */
    default void onUnequipped(ItemStack stack, IExtensionSlot slot)
    {
    }

    /**
     * Queries whether or not the stack can be placed in the slot.
     *
     * @param stack The ItemStack in the slot.
     * @param slot  The slot being referenced.
     */
    default boolean canEquip(ItemStack stack, IExtensionSlot slot)
    {
        return true;
    }

    /**
     * Queries whether or not the stack can be removed from the slot.
     *
     * Curse of Binding is handled by the slot, so it is not needed here.
     *
     * @param stack The ItemStack in the slot.
     * @param slot  The slot being referenced.
     */
    default boolean canUnequip(ItemStack stack, IExtensionSlot slot)
    {
        return true;
    }
}
