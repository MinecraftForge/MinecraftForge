/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

import java.util.function.BiPredicate;
import java.util.function.Supplier;

public interface IExtensionPoint<T extends Record>
{
    /**
     * Compatibility display test for the mod.
     * Used for displaying compatibility with remote servers with the same mod, and on disk saves.
     *
     * The supplier provides my "local" version for sending across the impl or writing to disk
     * The predicate tests the version from a remote instance or save for acceptability (Boolean is true for impl, false for local save)
     * and returns true if the version is compatible.
     *
     * <p>Return {@code net.minecraftforge.network.NetworkConstants#IGNORESERVERONLY} in the supplier, if you wish to be ignored
     * as a server side only mod.</p>
     * <p>Return true in the predicate for all values of the input string (when impl boolean is true) if you are client side,
     * and don't care about matching any potential server version.</p>
     *
     * <p>
     * Examples: A server only mod
     * </p>
     * <pre>{@code
     *     registerExtensionPoint(DisplayTest.class, ()->new DisplayTest(
     *      ()->NetworkConstants.IGNORESERVERONLY, // ignore me, I'm a server only mod
     *      (s,b)->true // i accept anything from the server or the save, if I'm asked
     *     )
     * }</pre>
     *
     * <p>
     * Examples: A client only mod
     * </p>
     * <pre>{@code
     *     registerExtensionPoint(DisplayTest.class, ()->new DisplayTest(
     *      ()->"anything. i don't care", // if i'm actually on the server, this string is sent but i'm a client only mod, so it won't be
     *      (remoteversionstring,networkbool)->networkbool // i accept anything from the server, by returning true if it's asking about the server
     *     )
     * }</pre>
     *
     */
    record DisplayTest(Supplier<String> suppliedVersion, BiPredicate<String, Boolean> remoteVersionTest) implements IExtensionPoint<DisplayTest> {}
}
