/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

public interface IForgePackResources
{
    default boolean isHidden()
    {
        return false;
    }
}
