/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
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
