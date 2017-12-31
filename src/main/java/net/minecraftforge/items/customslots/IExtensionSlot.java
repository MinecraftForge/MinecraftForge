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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Describes an extension slot and its contents.
 */
public interface IExtensionSlot extends INBTSerializable<NBTTagCompound>
{
    /**
     * Gets the container that is managing this slot.
     * <p>
     * Note that multiple mods can share a slot through the GlobalSlotRegistry,
     * which means the container returned here may be the FlexibleExtensionContainer,
     * and not the container that returned this instance from getSlot/getSlots.
     *
     * @return The owning container.
     */
    IExtensionContainer getContainer();

    /**
     * Gets the slot identifier for this slot. This identifier should be unique within the container.
     *
     * @return The slot id
     */
    String getId();

    /**
     * Gets the slot type identifier for this slot. More than one slot can share the same type,
     * e.g. multiple ring slots.
     *
     * @return The slot type name
     */
    String getType();

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

    /**
     * Queries whether or not the stack can be placed in this slot.
     *
     * @param stack The ItemStack in the slot.
     */
    default boolean canEquip(ItemStack stack)
    {
        IExtensionSlotItem extItem = stack.getCapability(CapabilityExtensionSlotItem.INSTANCE, null);

        // By default, a slot won't accept plain items, override if you want to allow them.
        if (extItem == null)
            return false;

        ImmutableSet<ResourceLocation> slots = extItem.getAcceptableSlots(stack);
        return (slots.contains(CapabilityExtensionSlotItem.ANY_SLOT) || slots.contains(getType()))
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
     * Checks if the slot needs to be stored, useful for integrating other mods' slots into this system,
     * while keeping their separate storage solution.
     *
     * See VanillaEquipment for an use case.
     *
     * @return False if you don't want the slot storage managed by the FlexibleExtensionContainer.
     */
    default boolean skipStorage()
    {
        return false;
    }

    @Override
    default NBTTagCompound serializeNBT()
    {
        return getContents().serializeNBT();
    }

    @Override
    default void deserializeNBT(NBTTagCompound nbt)
    {
        setContents(new ItemStack(nbt));
    }

    /**
     * Calls the tick handler for the contained item.
     */
    default void onWornTick()
    {
        ItemStack stack = getContents();
        if (stack.isEmpty())
            return;
        IExtensionSlotItem extItem = stack.getCapability(CapabilityExtensionSlotItem.INSTANCE, null);
        if (extItem != null)
            extItem.onWornTick(stack, this);
    }
}
