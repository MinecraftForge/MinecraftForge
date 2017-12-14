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

package net.minecraftforge.items.filter;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.function.Predicate;

@FunctionalInterface
public interface IStackFilter extends Predicate<ItemStack>
{
    NonNullList<ItemStack> emptyNNList = NonNullList.withSize(0, ItemStack.EMPTY);

    @Override
    boolean test(ItemStack stack);

    default NonNullList<ItemStack> getExamples()
    {
        return emptyNNList;
    }
}
