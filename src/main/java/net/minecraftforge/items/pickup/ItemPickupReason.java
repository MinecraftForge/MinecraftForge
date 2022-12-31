/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.items.pickup;

import com.google.common.collect.Maps;
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
    private static final Map<String, ItemPickupReason> reasons = Maps.newConcurrentMap();

    private final String name;

    private ItemPickupReason(String name)
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

    /**
     * Returns all registered pickup reasons.
     *
     * @return All registered pickup reasons.
     */
    public static Collection<ItemPickupReason> getPickupReasons()
    {
        return Collections.unmodifiableCollection(reasons.values());
    }

    /**
     * Gets a given pickup reason or creates a new instance of none exists.
     *
     * @param name Name of the pickup reason.
     * @return Gets or creates a given pickup reason.
     */
    public static ItemPickupReason getOrCreate(String name)
    {
        return reasons.computeIfAbsent(name, ItemPickupReason::new);
    }
}
