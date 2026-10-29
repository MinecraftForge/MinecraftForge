/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;

import java.util.Map;

/// @deprecated This class is scheduled for removal in 26.4 due to a lack of adoption by mods, in order to ease porting.
///             Please reach out to the Forge team if you are relying on this and would like to see it remain in Forge.
///             We may keep it or add it back in a future version if there is enough demand.
@Deprecated(forRemoval = true, since = "26.4")
public class ItemStackMap {

    public static <V> Map<ItemStack, V> createTypeAndTagLinkedMap() {
        return new Object2ObjectLinkedOpenCustomHashMap<>(ItemStackLinkedSet.TYPE_AND_TAG);
    }

    public static <V> Map<ItemStack, V> createTypeAndTagMap() {
        return new Object2ObjectOpenCustomHashMap<>(ItemStackLinkedSet.TYPE_AND_TAG);
    }
}
