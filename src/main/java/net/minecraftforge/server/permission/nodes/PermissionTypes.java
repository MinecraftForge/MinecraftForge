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

import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

/**
 * Default PermissionTypes, if you need additional ones, please PR it.
 */
public final class PermissionTypes
{
    public static final PermissionType<Boolean> BOOLEAN = new PermissionType<>(Boolean.class, "boolean");
    public static final PermissionType<Integer> INTEGER = new PermissionType<>(Integer.class, "integer");
    public static final PermissionType<String> STRING = new PermissionType<>(String.class, "string");
    public static final PermissionType<Component> COMPONENT = new PermissionType<>(Component.class, "component");

    private PermissionTypes()
    {
    }

    @Nullable
    public static PermissionType<?> getTypeByName(String name)
    {
        return switch (name)
            {
                case "boolean" -> BOOLEAN;
                case "integer" -> INTEGER;
                case "string" -> STRING;
                case "component" -> COMPONENT;
                default -> null;
            };
    }
}
