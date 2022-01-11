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

package net.minecraftforge.server.permission.nodes;

import java.util.Objects;

/**
 * Pair of a PermissionDynamicContextKey and a value of the corresponding type.
 * Use {@link PermissionDynamicContextKey#createContext(Object)} )} for constructing.
 *
 * <p>Note: While the DynamicContext behaves similar to BlockStates, it does not oblige to the same limitations.
 * There is no string representation that you have to follow, nor is there a limit on how many unique value a DynamicContext may have</p>
 *
 * @implNote this class could be a record but is not in favor of a package private constructor
 */
public final class PermissionDynamicContext<T>
{
    private PermissionDynamicContextKey<T> dynamic;
    private T value;

    PermissionDynamicContext(PermissionDynamicContextKey<T> dynamic, T value)
    {
        this.dynamic = dynamic;
        this.value = value;
    }

    public PermissionDynamicContextKey<T> getDynamic()
    {
        return dynamic;
    }

    public T getValue()
    {
        return value;
    }

    public String getSerializedValue(){
        return this.dynamic.serializer().apply(this.value);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof PermissionDynamicContext otherContext)) return false;
        return dynamic.equals(otherContext.dynamic) && value.equals(otherContext.value);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(dynamic, value);
    }
}
