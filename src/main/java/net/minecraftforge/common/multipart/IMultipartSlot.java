/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.common.multipart;

import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * Represents a slot inside a block space.<br/>
 * In most cases, you will want to extend {@link MultipartSlot} instead of implementing this interface directly.<br/>
 *
 * Default implementations are provided for face, edge, corner and center slots, as well as a general-purpose full-block
 * slot.
 *
 * @see MultipartSlot
 * @see FaceSlot
 * @see EdgeSlot
 * @see CornerSlot
 */
public interface IMultipartSlot extends IForgeRegistryEntry<IMultipartSlot>
{
}
