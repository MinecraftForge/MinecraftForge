/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraftforge.common.util.LazyOptional;

public interface ICapabilityProvider
{
    /**
     * Retrieves the Optional handler for the capability requested on the specific side.
     * The return value <strong>CAN</strong> be the same for multiple faces.
     * Modders are encouraged to cache this value, using the listener capabilities of the Optional to
     * be notified if the requested capability get lost.
     *
     * @param capability The capability to check
     * @param facing The Side to check from,
     *   <strong>CAN BE NULL</strong>. Null is defined to represent 'internal' or 'self'
     * @return The requested an optional holding the requested capability.
     */
    @Nonnull <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> cap, final @Nullable Direction side);

    /*
     * Purely added as a bouncer to sided version, to make modders stop complaining about calling with a null value.
     * This should never be OVERRIDDEN, modders should only ever implement the sided version.
     */
    @Nonnull default <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> cap) {
        return getCapability(cap, null);
    }
}
