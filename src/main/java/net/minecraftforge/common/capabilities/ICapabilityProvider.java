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

package net.minecraftforge.common.capabilities;

import javax.annotation.Nullable;

import net.minecraft.util.EnumFacing;

public interface ICapabilityProvider
{
    /**
     * Determines if this object has support for the capability in question on the specific side.
     * The return value of this MIGHT change during runtime if this object gains or looses support
     * for a capability.
     *
     * Example:
     *   A Pipe getting a cover placed on one side causing it loose the Inventory attachment function for that side.
     *
     * This is a light weight version of getCapability, intended for metadata uses.
     *
     * @param capability The capability to check
     * @param facing The Side to check from:
     *   CAN BE NULL. Null is defined to represent 'internal' or 'self'
     * @return True if this object supports the capability.
     */
    boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing);

    /**
     * Retrieves the handler for the capability requested on the specific side.
     * The return value CAN be null if the object does not support the capability.
     * The return value CAN be the same for multiple faces.
     *
     * @param capability The capability to check
     * @param facing The Side to check from:
     *   CAN BE NULL. Null is defined to represent 'internal' or 'self'
     * @return True if this object supports the capability.
     */
    <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing);
}
