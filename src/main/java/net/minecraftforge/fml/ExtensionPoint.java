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
     */
    public static final ExtensionPoint<Pair<Supplier<String>, BiPredicate<String, Boolean>>> DISPLAYTEST = new ExtensionPoint<>();

    private Class<T> type;

    private ExtensionPoint() {
    }

}
