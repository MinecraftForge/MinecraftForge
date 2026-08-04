/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

public enum LogicalSide
{
    CLIENT, SERVER;

    /**
     * @return if the logical side is a server.
     */
    public boolean isServer()
    {
        return !isClient();
    }

    /**
     * @return if the logical side is a client.
     */
    public boolean isClient()
    {
        return this == CLIENT;
    }
}
