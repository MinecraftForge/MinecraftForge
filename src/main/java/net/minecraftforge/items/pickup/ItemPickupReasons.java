/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.items.pickup;

import static net.minecraftforge.items.pickup.ItemPickupReason.getOrCreate;

/**
 * Class containing all standard ItemPickupReasons provided by Forge.
 *
 * @see ItemPickupReasons
 */
public final class ItemPickupReasons
{
    /** Player is attempting to pick up an item */
    public static final ItemPickupReason PLAYER = getOrCreate("player");

    /** LivingEntity is attempting to pick up an item */
    public static final ItemPickupReason LIVING_ENTITY = getOrCreate("living_entity");

    /** Hopper is attempting to suck in an item */
    public static final ItemPickupReason HOPPER = getOrCreate("hopper");
}
