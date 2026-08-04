/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import java.util.Map;
import java.util.regex.Pattern;

import com.google.common.collect.Maps;

public final class ToolType
{
    private static final Pattern VALID_NAME = Pattern.compile("[^a-z_]"); //Only a-z and _ are allowed, meaning names must be lower case. And use _ to separate words.
    private static final Map<String, ToolType> values = Maps.newHashMap();

    public static final ToolType AXE = get("axe");
    public static final ToolType PICKAXE = get("pickaxe");
    public static final ToolType SHOVEL = get("shovel");


    public static ToolType get(String name)
    {
        if (VALID_NAME.matcher(name).find())
            throw new IllegalArgumentException("ToolType.create() called with invalid name: " + name);
        return values.computeIfAbsent(name, k -> new ToolType(name));
    }

    private final String name;

    private ToolType(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
