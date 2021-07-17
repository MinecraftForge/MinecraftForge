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

import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

/**
 * This is the core holder object Capabilities.
 * Each capability will have ONE instance of this class,
 * and it will the the one passed into the ICapabilityProvider functions.
 *
 * The CapabilityManager is in charge of creating this class.
 */
public class Capability<T>
{
    /**
     * @return The unique name of this capability, typically this is
     * the fully qualified class name for the target interface.
     */
    public String getName() { return name; }

    public @Nonnull <R> LazyOptional<R> orEmpty(Capability<R> toCheck, LazyOptional<T> inst)
    {
        return this == toCheck ? inst.cast() : LazyOptional.empty();
    }

    // INTERNAL
    private final String name;

    Capability(String name)
    {
        this.name = name;
    }
}
