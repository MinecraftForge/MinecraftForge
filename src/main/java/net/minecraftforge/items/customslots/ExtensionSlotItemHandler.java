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

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandlerModifiable;

public class ExtensionSlotItemHandler implements IExtensionSlot
{
    private final String id;
    private String slotType;

    protected final IExtensionContainer owner;
    protected final IItemHandlerModifiable inventory;
    protected final int slot;

    public ExtensionSlotItemHandler(IExtensionContainer owner, String id, IItemHandlerModifiable inventory, int slot)
    {
        this.owner = owner;
        this.id = id;
        this.slotType = id; // Call setType to change from default!
        this.inventory = inventory;
        this.slot = slot;
    }

    @Override
    public IExtensionContainer getContainer()
    {
        return owner;
    }

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public String getType()
    {
        return slotType;
    }

    public ExtensionSlotItemHandler setType(String typeId)
    {
        this.slotType = typeId;
        return this;
    }

    /**
     * Gets the contents of the slot. The stack is *NOT* required to be of an IExtensionSlotItem!
     *
     * @return The contained stack
     */
    @Override
    public ItemStack getContents()
    {
        return inventory.getStackInSlot(slot);
    }

    /**
     * Sets the contents of the slot. The stack is *NOT* required to be of an IExtensionSlotItem!
     *
     * @param stack The stack to be assigned to the slot
     */
    @Override
    public void setContents(ItemStack stack)
    {
        ItemStack oldStack = getContents();
        if (oldStack == stack) return;
        if (!oldStack.isEmpty())
            notifyUnequip(oldStack);
        inventory.setStackInSlot(slot, stack);
        if (!stack.isEmpty())
            notifyEquip(stack);
    }

    private void notifyEquip(ItemStack stack)
    {
        IExtensionSlotItem extItem = stack.getCapability(CapabilityExtensionSlotItem.INSTANCE, null);
        if (extItem == null)
            return;
        extItem.onEquipped(stack, this);
    }

    private void notifyUnequip(ItemStack stack)
    {
        IExtensionSlotItem extItem = stack.getCapability(CapabilityExtensionSlotItem.INSTANCE, null);
        if (extItem == null)
            return;
        extItem.onUnequipped(stack, this);
    }
}
