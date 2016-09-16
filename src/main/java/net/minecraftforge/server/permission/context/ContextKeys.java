/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.server.permission.context;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

/**
 * Some default context keys, for easier compatibility
 */
public class ContextKeys
{
    /**
     * BlockPos for interacting, breaking and other permissions
     */
    public static final ContextKey<BlockPos> POS = ContextKey.create("pos", BlockPos.class);

    /**
     * The entity can be anything that gets interacted with - a sheep when you try to dye it, skeleton that you attack, etc.
     */
    public static final ContextKey<Entity> TARGET = ContextKey.create("target", Entity.class);

    public static final ContextKey<EnumFacing> FACING = ContextKey.create("facing", EnumFacing.class);
    public static final ContextKey<AxisAlignedBB> AREA = ContextKey.create("area", AxisAlignedBB.class);
    public static final ContextKey<IBlockState> BLOCK_STATE = ContextKey.create("blockstate", IBlockState.class);
}