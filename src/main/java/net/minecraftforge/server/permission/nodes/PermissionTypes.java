/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.server.permission.nodes;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

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
