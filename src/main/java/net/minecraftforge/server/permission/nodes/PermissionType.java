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
 * <p>Type of a Permission, use the existing Types in {@link PermissionTypes}</p>
 *
 * @implNote this class could be a record but is not in favor of a package private constructor
 */
public final class PermissionType<T>
{
    private final Class<T> typeToken;
    private final String typeName;

    PermissionType(Class<T> typeToken, String typeName)
    {
        this.typeToken = typeToken;
        this.typeName = typeName;
    }

    public Class<T> typeToken()
    {
        return typeToken;
    }

    public String typeName()
    {
        return typeName;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if(!(obj instanceof PermissionType otherType)) return false;
        return Objects.equals(this.typeToken, otherType.typeToken) &&
            Objects.equals(this.typeName, otherType.typeName);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(typeToken, typeName);
    }

    @Override
    public String toString()
    {
        return "PermissionType[" +
            "typeToken=" + typeToken + ", " +
            "typeName=" + typeName + ']';
    }

}
