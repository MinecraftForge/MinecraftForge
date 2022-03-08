/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

interface IHolderHelperHolder<T>
{
    NamespacedHolderHelper<T> getHolderHelper();
}
