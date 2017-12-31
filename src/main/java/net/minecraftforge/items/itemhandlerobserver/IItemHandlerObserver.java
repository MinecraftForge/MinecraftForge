/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.items.itemhandlerobserver;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public interface IItemHandlerObserver
{
    /**
     * @param handler the handler that was inserted in to
     * @param slot the slot that was inserted in to
     * @param oldStackSize the old stackSize, 0 if it was {@link ItemStack#EMPTY}
     * @param newStack the stack that is inserted into the slot
     */
    void onStackInserted(IItemHandler handler, int slot, int oldStackSize, @Nonnull ItemStack newStack);

    /**
     * @param handler that got extracted from
     * @param slot that got extracted from
     * @param oldStackSize the amount from the old stack
     * @param newStack the new stack in the slot
     */
    void onStackExtracted(IItemHandler handler, int slot, int oldStackSize, @Nonnull ItemStack newStack);

    /**
     * this gets called when the IItemHandlerObservable Invalids/unloads
     */
    void onInvalidated();

    /**
     * @return true when this observer is still valid
     */
    boolean isValid();
}
