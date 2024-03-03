/*
 * Copyright (c) Forge Development LLC and contributors
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
