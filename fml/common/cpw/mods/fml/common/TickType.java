/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package cpw.mods.fml.common;

import java.util.EnumSet;

public enum TickType {
    /**
     * Fired during the world evaluation loop
     * server and client side
     *
     * arg 0 : The world that is ticking
     */
    WORLD,
    /**
     * client side
     * Fired during the render processing phase
     * arg 0 : float "partial render time"
     */
    RENDER,
    /**
     * server side
     * Fired once as the world loads from disk
     */
    WORLDLOAD,
    /**
     * client side only
     * Fired once per client tick loop.
     */
    CLIENT,
    /**
     * client and server side.
     * Fired whenever the players update loop runs.
     * arg 0 : the player
     * arg 1 : the world the player is in
     */
    PLAYER,
    /**
     * server side only.
     * This is the server game tick.
     * Fired once per tick loop on the server.
     */
    SERVER;

    /**
     * Partner ticks that are also cancelled by returning false from onTickInGame
     */
    public EnumSet<TickType> partnerTicks()
    {
        if (this==CLIENT) return EnumSet.of(RENDER);
        if (this==RENDER) return EnumSet.of(CLIENT);
        return EnumSet.noneOf(TickType.class);
    }
}
