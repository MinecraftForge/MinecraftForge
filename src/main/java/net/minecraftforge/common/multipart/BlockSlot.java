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

import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * Default implementation of {@link IBlockSlot} to reduce redundant code.<br/>
 *
 * @see IBlockSlot
 * @see FaceSlot
 * @see EdgeSlot
 * @see CornerSlot
 */
public class BlockSlot extends ForgeRegistryEntry<IBlockSlot> implements IBlockSlot
{
    /**
     * A slot that takes up the whole block.<br/>
     * This slot is not meant to be used in practice and all implementations should treat blocks returning this as not
     * supporting multipart behavior.
     */
    public static final IBlockSlot FULL_BLOCK = new BlockSlot().setRegistryName("forge", "full_block");
    /**
     * A generic slot located in the center of the block.
     */
    public static final IBlockSlot CENTER = new BlockSlot().setRegistryName("forge", "center");
}
