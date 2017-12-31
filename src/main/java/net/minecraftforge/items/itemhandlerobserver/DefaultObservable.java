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
import java.util.ArrayList;
import java.util.List;


public class DefaultObservable implements IItemHandlerObservable
{
    private final List<IItemHandlerObserver> observers = new ArrayList<>();

    @Override
    public boolean addObserver(IItemHandlerObserver observer)
    {
        return observers.add(observer);
    }

    @Override
    public void removeObserver(IItemHandlerObserver observer)
    {
        observers.remove(observer);
    }

    public void onStackInserted(IItemHandler handler, int slot, int oldStackSize, @Nonnull ItemStack newStack)
    {
        observers.forEach(observer -> observer.onStackInserted(handler, slot, oldStackSize, newStack));
    }

    public void onStackExtracted(IItemHandler handler, int slot, int oldStackSize, @Nonnull ItemStack newStack)
    {
        observers.forEach(observer -> observer.onStackExtracted(handler, slot, oldStackSize, newStack));
    }

    public void onInvalidated()
    {
        observers.forEach(IItemHandlerObserver::onInvalidated);
    }
}
