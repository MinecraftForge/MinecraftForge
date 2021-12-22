/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.common;

import java.util.stream.Stream;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryReadOps;

/**
 * Unit codec for getting access to dynamic registries during dimension loading.
 * This is only safe to use while loading dimensions (LevelStems, ChunkGenerators, and BiomeSources);
 * when access to registries is needed while loading dynamic registry objects such as configured features,
 * then RegistryLookupCodec should be used instead.
 */
public final class RegistryAccessCodec extends MapCodec<RegistryAccess>
{
    public static final RegistryAccessCodec INSTANCE = new RegistryAccessCodec();

    @Override
    public <T> DataResult<RegistryAccess> decode(DynamicOps<T> ops, MapLike<T> input)
    {
        return ops instanceof RegistryReadOps ? DataResult.success(((RegistryReadOps)ops).registryAccess) : DataResult.error("Not a registry ops");
    }

    @Override
    public <T> RecordBuilder<T> encode(RegistryAccess input, DynamicOps<T> ops, RecordBuilder<T> prefix)
    {
        return prefix;
    }

    @Override
    public <T> Stream<T> keys(DynamicOps<T> ops)
    {
        return Stream.empty();
    }
}
