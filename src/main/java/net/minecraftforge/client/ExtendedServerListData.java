/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

public record ExtendedServerListData(String type, boolean isCompatible, int numberOfMods, String extraReason, boolean truncated)
{
    public ExtendedServerListData(String type, boolean isCompatible, int numberOfMods, String extraReason)
    {
        this(type, isCompatible, numberOfMods, extraReason, false);
    }
}
