/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions;

public interface IForgeResourcePack
{
    default boolean isHidden()
    {
        return false;
    }
}
