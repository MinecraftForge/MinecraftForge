/*
 * The FML Forge Mod Loader suite.
 * Copyright (C) 2012 cpw
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
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
     * Not fired
     */
    @Deprecated
    GUI,
    /**
     * Not fired
     */
    @Deprecated
    CLIENTGUI,
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
        if (this==GUI) return EnumSet.of(CLIENTGUI);
        if (this==CLIENTGUI) return EnumSet.of(GUI);
        return EnumSet.noneOf(TickType.class);
    }
}