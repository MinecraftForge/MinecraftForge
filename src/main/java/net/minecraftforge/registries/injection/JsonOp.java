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

public interface JsonOp
{
    /**
     * Provides a contextual name for the implementing class's operation used for logging/exceptions.
     *
     * @return The name of the operation.
     */
    String getName();

    /**
     * Represents an operation that injects content into the json data of a registry entry.
     * <p>
     * To maintain compatibility with datapacks, content should only ever be additive and should
     * not overwrite values already contained in the json data.
     *
     * @param <E> The type of registry entry this injector applies to.
     */
    interface Inject<E> extends JsonOp
    {
        void inject(RegistryKey<E> entryKey, JsonElement entryData) throws Exception;
    }

    /**
     * Represents an operation that merges two different json representations of the same registry entry.
     *
     * @param <E> The type of registry entry this merger applies to.
     */
    interface Merge<E> extends JsonOp
    {
        void merge(RegistryKey<E> entryKey, JsonElement dest, JsonElement src) throws Exception;
    }
}
