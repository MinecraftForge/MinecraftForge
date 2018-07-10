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

package net.minecraftforge.common.property;

import java.util.Collection;

import net.minecraft.block.state.IBlockState;

import java.util.Optional;
import com.google.common.collect.ImmutableMap;

public interface IExtendedBlockState extends IBlockState
{
    Collection<IUnlistedProperty<?>> getUnlistedNames();

    <V>V getValue(IUnlistedProperty<V> property);

    <V>IExtendedBlockState withProperty(IUnlistedProperty<V> property, V value);

    ImmutableMap<IUnlistedProperty<?>, Optional<?>> getUnlistedProperties();
    
    IBlockState getClean();
}
