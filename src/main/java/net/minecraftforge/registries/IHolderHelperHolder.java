/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

interface IHolderHelperHolder<T extends IForgeRegistryEntry<T>>
{
    NamespacedHolderHelper<T> getHolderHelper();
}
