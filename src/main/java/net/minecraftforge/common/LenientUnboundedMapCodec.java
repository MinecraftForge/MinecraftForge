// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.
package net.minecraftforge.common;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.codecs.BaseMapCodec;

import java.util.Map;
import java.util.Objects;

/**
 * Key and value decoded independently, unknown set of keys
 */
public class LenientUnboundedMapCodec<K, V> implements BaseMapCodec<K, V>, Codec<Map<K, V>>
{
    private final Codec<K> keyCodec;
    private final Codec<V> elementCodec;

    public LenientUnboundedMapCodec(final Codec<K> keyCodec, final Codec<V> elementCodec) {
        this.keyCodec = keyCodec;
        this.elementCodec = elementCodec;
    }

    @Override
    public Codec<K> keyCodec() {
        return keyCodec;
    }

    @Override
    public Codec<V> elementCodec() {
        return elementCodec;
    }

    @Override // FORGE: Modified from decode() in BaseMapCodec
    public <T> DataResult<Map<K, V>> decode(DynamicOps<T> ops, MapLike<T> input)
    {
        final ImmutableMap.Builder<K, V> read = ImmutableMap.builder();
        final ImmutableList.Builder<Pair<T, T>> failed = ImmutableList.builder();

        final DataResult<Unit> result = input.entries().reduce(
                DataResult.success(Unit.INSTANCE, Lifecycle.stable()),
                (r, pair) -> {
                    final DataResult<K> k = keyCodec().parse(ops, pair.getFirst());
                    final DataResult<V> v = elementCodec().parse(ops, pair.getSecond());

                    final DataResult<Pair<K, V>> entry = k.apply2stable(Pair::of, v);
                    entry.error().ifPresent(e -> failed.add(pair));
                    entry.result().ifPresent(e -> read.put(e.getFirst(), e.getSecond())); // FORGE: This line moved outside the below apply2stable condition
                    return r.apply2stable((u, p) -> u, entry);
                },
                (r1, r2) -> r1.apply2stable((u1, u2) -> u1, r2)
        );

        final Map<K, V> elements = read.build();
        final T errors = ops.createMap(failed.build().stream());

        return result.map(unit -> elements).setPartial(elements).mapError(e -> e + " missed input: " + errors);
    }

    @Override
    public <T> DataResult<Pair<Map<K, V>, T>> decode(final DynamicOps<T> ops, final T input) {
        return ops.getMap(input).setLifecycle(Lifecycle.stable()).flatMap(map -> decode(ops, map)).map(r -> Pair.of(r, input));
    }

    @Override
    public <T> DataResult<T> encode(final Map<K, V> input, final DynamicOps<T> ops, final T prefix) {
        return encode(input, ops, ops.mapBuilder()).build(prefix);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final LenientUnboundedMapCodec<?, ?> that = (LenientUnboundedMapCodec<?, ?>) o;
        return Objects.equals(keyCodec, that.keyCodec) && Objects.equals(elementCodec, that.elementCodec);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyCodec, elementCodec);
    }

    @Override
    public String toString() {
        return "LenientUnboundedMapCodec[" + keyCodec + " -> " + elementCodec + ']';
    }
}
