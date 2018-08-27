/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.resources.IResourcePack;
import net.minecraftforge.fml.client.ModFileResourcePack;

import java.util.function.BiFunction;
import java.util.function.Function;

public class ExtensionPoint<T>
{
    public static final ExtensionPoint<BiFunction<Minecraft, GuiScreen, GuiScreen>> GUIFACTORY = new ExtensionPoint<>();
    public static final ExtensionPoint<BiFunction<Minecraft, ModFileResourcePack, IResourcePack>> RESOURCEPACK = new ExtensionPoint<>();
    private Class<T> type;

    private ExtensionPoint() {
    }

}
