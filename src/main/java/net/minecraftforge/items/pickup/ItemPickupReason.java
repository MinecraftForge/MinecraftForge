/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.items.pickup;

import com.google.common.collect.Maps;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import net.minecraft.util.StringRepresentable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Class representing a reason in which an ItemEntity is being picked up.
 */
public final class ItemPickupReason implements StringRepresentable, Comparable<ItemPickupReason>
{
    /**
     * package level of visibility to be accessible in {@link ItemPickupReasons}.
     * <p>Modders should <b>NOT</b> access this field.
     */
    @ApiStatus.Internal
    static final Map<String, ItemPickupReason> reasons = Maps.newConcurrentMap();

    private final String name;

    /**
     * package level of visibility to be accessible in {@link ItemPickupReasons}.
     * <p>Modders should <b>NOT</b> use this constructor.
     * @implNote Invoking this directly will <b>not</b> register the reason correctly to the internal reasons map.
     */
    @ApiStatus.Internal
    ItemPickupReason(String name)
    {
        this.name = name;
    }

    /**
     * Returns the name of this pickup reason.
     *
     * @return The name of this pickup reason.
     */
    public String name()
    {
        return name;
    }

    @NotNull
    @Override
    public String getSerializedName()
    {
        return name;
    }

    @Override
    public int compareTo(@NotNull ItemPickupReason other)
    {
        return name.compareTo(other.name);
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this == obj) return true;
        if(obj instanceof ItemPickupReason other) return name.equals(other.name);
        return false;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(name);
    }

    @Override
    public String toString()
    {
        return "ItemPickupReason[%s]".formatted(name);
    }
}
