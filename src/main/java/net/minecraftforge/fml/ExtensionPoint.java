/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.resources.IResourcePack;
import net.minecraftforge.fml.packs.ModFileResourcePack;

public class ExtensionPoint<T>
{
    public static final ExtensionPoint<BiFunction<Minecraft, Screen, Screen>> CONFIGGUIFACTORY = new ExtensionPoint<>();
    public static final ExtensionPoint<BiFunction<Minecraft, ModFileResourcePack, IResourcePack>> RESOURCEPACK = new ExtensionPoint<>();
    /**
     * Compatibility display test for the mod.
     * Used for displaying compatibility with remote servers with the same mod, and on disk saves.
     *
     * The supplier provides my "local" version for sending across the network or writing to disk
     * The predicate tests the version from a remote instance or save for acceptability (Boolean is true for network, false for local save)
     * and returns true if the version is compatible.
     *
     * <p>Return {@link net.minecraftforge.fml.network.FMLNetworkConstants#IGNORESERVERONLY} in the supplier, if you wish to be ignored
     * as a server side only mod.</p>
     * <p>Return true in the predicate for all values of the input string (when network boolean is true) if you are client side,
     * and don't care about matching any potential server version.</p>
     *
     * <p>
     * Examples: A server only mod
     *
     * <code><pre>
     *     registerExtensionPoint(DISPLAYTEST, ()->Pair.of(
     *      ()->FMLNetworkConstants.IGNORESERVERONLY, // ignore me, I'm a server only mod
     *      (s,b)->true // I accept anything from the server or the save, if I'm asked
     *     )
     * </pre></code>
     * </p>
     * <p>
     * Examples: A client only mod
     * <code><pre>
     *     registerExtensionPoint(DISPLAYTEST, ()->Pair.of(
     *      ()->"anything. I don't care", // if I'm actually on the server, this string is sent but I'm a client only mod, so it won't be
     *      (remoteversionstring,networkbool)->networkbool // I accept anything from the server, by returning true if it's asking about the server
     *     )
     * </pre></code>
     * </p>
     *
     */
    public static final ExtensionPoint<Pair<Supplier<String>, BiPredicate<String, Boolean>>> DISPLAYTEST = new ExtensionPoint<>();

    private Class<T> type;

    private ExtensionPoint() {
    }

}
