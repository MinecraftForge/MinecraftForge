/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import java.util.function.Function;

public enum ConnectionType
{
    MODDED(s->Integer.valueOf(s.substring(NetworkConstants.FMLNETMARKER.length()))), VANILLA(s->0);

    private final Function<String, Integer> versionExtractor;

    ConnectionType(Function<String, Integer> versionExtractor)
    {
        this.versionExtractor = versionExtractor;
    }

    public static ConnectionType forVersionFlag(String vers)
    {
        return vers.startsWith(NetworkConstants.FMLNETMARKER) ? MODDED : VANILLA;
    }

    public int getFMLVersionNumber(final String fmlVersion)
    {
        return versionExtractor.apply(fmlVersion);
    }

    public boolean isVanilla()
    {
        return this == VANILLA;
    }

}
