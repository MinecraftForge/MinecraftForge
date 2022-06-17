/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

import net.minecraftforge.api.distmarker.Dist;

/**
 * A logical side of the Minecraft game.
 * <p>
 * The {@linkplain Dist#CLIENT client distribution} has a copy of the logical client and the logical server, while the
 * {@linkplain Dist#DEDICATED_SERVER dedicated server distribution} only holds
 * the logical server.
 *
 * @see Dist
 */
public enum LogicalSide
{
    /**
     * The logical client of the Minecraft game, which interfaces with the player's inputs and renders the player's
     * viewpoint.
     * <p>
     * The logical client is only shipped with the client distribution of the game.
     *
     * @see Dist#CLIENT
     */
    CLIENT,
    /**
     * The logical server of the Minecraft game, responsible for connecting to clients and running the simulation logic
     * on the level.
     * <p>
     * The logical server is shipped with both client and dedicated server distributions. The client distribution runs
     * the logical server for singleplayer mode and LAN play.
     *
     * @see Dist#DEDICATED_SERVER
     */
    SERVER;

    /**
     * {@return if this logical side is the server}
     */
    public boolean isServer()
    {
        return !isClient();
    }

    /**
     * {@return if the logical side is the client}
     */
    public boolean isClient()
    {
        return this == CLIENT;
    }
}
