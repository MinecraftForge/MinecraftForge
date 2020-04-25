/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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
     */
    public static final ExtensionPoint<Pair<Supplier<String>, BiPredicate<String, Boolean>>> DISPLAYTEST = new ExtensionPoint<>();

    private Class<T> type;

    private ExtensionPoint() {
    }

}
