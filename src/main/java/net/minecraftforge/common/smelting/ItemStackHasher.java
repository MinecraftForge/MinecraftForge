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

package net.minecraftforge.common.smelting;

import gnu.trove.strategy.HashingStrategy;
import net.minecraft.item.ItemStack;

final class ItemStackHasher implements HashingStrategy<ItemStack>
{

    @Override
    public int computeHashCode(ItemStack stack)
    {
        int hash = 31 + System.identityHashCode(stack.getItem());
        hash = 31 * hash + stack.getMetadata();
        return hash;
    }

    @Override
    public boolean equals(ItemStack o1, ItemStack o2)
    {
        return o1.getItem() == o2.getItem() && o1.getMetadata() == o2.getMetadata();
    }

}
