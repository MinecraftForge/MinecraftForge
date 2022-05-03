/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
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
     *      (s,b)->true // i accept anything from the server or the save, if I'm asked
     *     )
     * </pre></code>
     * </p>
     * <p>
     * Examples: A client only mod
     * <code><pre>
     *     registerExtensionPoint(DISPLAYTEST, ()->Pair.of(
     *      ()->"anything. i don't care", // if i'm actually on the server, this string is sent but i'm a client only mod, so it won't be
     *      (remoteversionstring,networkbool)->networkbool // i accept anything from the server, by returning true if it's asking about the server
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
