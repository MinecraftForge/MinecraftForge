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

import com.google.common.collect.Table;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import java.util.Optional;
import java.util.function.Predicate;

public class CapabilityFilter implements IStackFilter
{
    protected final Table<Capability<?>, Optional<EnumFacing>, Predicate<?>> capabilityFilters;

    public CapabilityFilter(Table<Capability<?>, Optional<EnumFacing>, Predicate<?>> capabilityFilters)
    {
        this.capabilityFilters = capabilityFilters;
    }

    @Override
    public boolean test(ItemStack stack)
    {
        return testCapability(stack);
    }

    @SuppressWarnings("unchecked")
    public <T> boolean testCapability(ItemStack stack)
    {
        for (Table.Cell<Capability<?>, Optional<EnumFacing>, Predicate<?>> cell : capabilityFilters.cellSet())
        {
            Capability<T> cap = (Capability<T>) cell.getRowKey();
            Optional<EnumFacing> face = cell.getColumnKey();

            boolean has = stack.hasCapability(cap, face.orElse(null));
            if (has && ((Predicate<T>) cell.getValue()).test(stack.getCapability(cap, face.orElse(null))))
            {
                return true;
            }
        }
        return false;
    }
}
