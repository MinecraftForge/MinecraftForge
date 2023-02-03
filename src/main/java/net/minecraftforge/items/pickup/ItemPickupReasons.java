/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.items.pickup;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.MinecartHopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;

import java.util.Collection;
import java.util.Collections;

/**
 * Class containing all standard ItemPickupReasons provided by Forge.
 * <p>This interface <b>should not</b> be custom implemented, instead make use of {@link #getOrCreate(String)}
 */
@ApiStatus.NonExtendable
public class ItemPickupReasons
{
    /**
     * Player is attempting to pick up an item
     * <p>{@code collector} types <i>should</i> result in the {@link Player} attempting to pick up the item.
     */
    public static final ItemPickupReason PLAYER = getOrCreate("player");

    /**
     * LivingEntity is attempting to pick up an item
     * <p>{@code collector} types <i>should</i> result in the {@link LivingEntity} attempting to pick up the item.
     */
    public static final ItemPickupReason LIVING_ENTITY = getOrCreate("living_entity");

    /**
     * Hopper is attempting to suck in an item
     * <p>{@code collector} types <i>should</i> result in the {@link HopperBlockEntity} or {@link MinecartHopper} {@link Container containers} attempting to pick up the item.
     */
    public static final ItemPickupReason HOPPER = getOrCreate("hopper");

    /**
     * {@return all registered pickup reasons}
     */
    public static Collection<ItemPickupReason> all()
    {
        return Collections.unmodifiableCollection(ItemPickupReason.reasons.values());
    }

    /**
     * Gets a given pickup reason or creates a new instance of none exists.
     * <p>For custom modded reasons, it is recommended you prefix the name with your mod id
     * <i>[{@code {modId}:{reasonName}}]</i>, this is to prevent registering custom reasons
     * with duplicate names, and potentially using custom mod reasons in places you did not intend on using them.
     *
     * <p>In order to obtain other mods custom reasons, Modders should either
     * <ul>
     *     <li>Provide some {@code public static final} field for their custom reasons in a public API.</li>
     *     <li>Provide the exact name used when registering their custom reasons [as this method will return the same value if given the same name].</li>
     * </ul>
     *
     * @param name Name of the pickup reason.
     * @return Gets or creates a given pickup reason.
     * @implNote This method will return the same value if called multiple times with the same {@code name} argument.
     */
    public static ItemPickupReason getOrCreate(String name)
    {
        return ItemPickupReason.reasons.computeIfAbsent(name, ItemPickupReason::new);
    }

    private ItemPickupReasons() {
    }
}
