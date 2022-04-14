/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

public class ExtendedServerListData {
    public final String type;
    public final boolean isCompatible;
    public int numberOfMods;
    public String extraReason;
    public final boolean truncated;

    public ExtendedServerListData(String type, boolean isCompatible, int num, String extraReason)
    {
        this(type, isCompatible, num, extraReason, false);
    }

    public ExtendedServerListData(String type, boolean isCompatible, int num, String extraReason, boolean truncated)
    {
        this.type = type;
        this.isCompatible = isCompatible;
        this.numberOfMods = num;
        this.extraReason = extraReason;
        this.truncated = truncated;
    }
}
