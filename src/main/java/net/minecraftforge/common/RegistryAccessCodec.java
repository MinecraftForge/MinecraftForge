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
