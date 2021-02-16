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

package net.minecraftforge.registries.injection;

import com.google.gson.JsonElement;
import net.minecraft.util.RegistryKey;

/**
 * Represents an operation that merges two different json representations of the same registry entry.
 *
 * @param <E> The type of registry entry this merger applies to.
 */
public interface Merger<E>
{
    void merge(RegistryKey<E> entryKey, JsonElement dest, JsonElement src) throws Exception;
}
