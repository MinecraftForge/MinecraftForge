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

public enum MergeStrategy
{
    /**
     * Adds all content from all namespaces to the registry entry's data.
     */
    ALL_NAMESPACES,

    /**
     * Adds all content from modded namespaces (ie not "minecraft:") to the registry entry's data.
     */
    MOD_NAMESPACES,

    /**
     * Only adds content from a namespace when that namespace does not already appear in the registry entry's data.
     * <p>
     * For example; if the data currently contains "minecraft:content_a" and a lower-order datapack or registry
     * contains "minecraft:content_b" it is assumed that the omission of "minecraft:content_b" was a deliberate
     * configuration choice, so it will not be added by the merge/inject operation.
     */
    ABSENT_NAMESPACES,
    ;
}
