// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.
package net.minecraftforge.common.world.fixes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.List;
import java.util.Objects;

public class LenientCompoundListCodec<K, V> implements Codec<List<Pair<K, V>>> {
    private final Codec<K> keyCodec;
    private final Codec<V> elementCodec;

    public LenientCompoundListCodec(final Codec<K> keyCodec, final Codec<V> elementCodec) {
        this.keyCodec = keyCodec;
        this.elementCodec = elementCodec;
    }

    @Override
    public <T> DataResult<Pair<List<Pair<K, V>>, T>> decode(final DynamicOps<T> ops, final T input) {
        return ops.getMapEntries(input).flatMap(map -> {
            final ImmutableList.Builder<Pair<K, V>> read = ImmutableList.builder();
            final ImmutableMap.Builder<T, T> failed = ImmutableMap.builder();

            // TODO: AtomicReference.getPlain/setPlain in java9+
            final MutableObject<DataResult<Unit>> result = new MutableObject<>(DataResult.success(Unit.INSTANCE, Lifecycle.experimental()));

            map.accept((key, value) -> {
                final DataResult<K> k = keyCodec.parse(ops, key);
                final DataResult<V> v = elementCodec.parse(ops, value);

                final DataResult<Pair<K, V>> readEntry = k.apply2stable(Pair::new, v);

                readEntry.error().ifPresent(e -> failed.put(key, value));
                readEntry.result().ifPresent(read::add); //FORGE: move outside of apply2stable.

                result.setValue(result.getValue().apply2stable((u, e) -> u, readEntry));
            });

            final ImmutableList<Pair<K, V>> elements = read.build();
            final T errors = ops.createMap(failed.build());

            final Pair<List<Pair<K, V>>, T> pair = Pair.of(elements, errors);

            return result.getValue().map(unit -> pair).setPartial(pair);
        });
    }

    @Override
    public <T> DataResult<T> encode(final List<Pair<K, V>> input, final DynamicOps<T> ops, final T prefix) {
        final RecordBuilder<T> builder = ops.mapBuilder();

        for (final Pair<K, V> pair : input) {
            builder.add(keyCodec.encodeStart(ops, pair.getFirst()), elementCodec.encodeStart(ops, pair.getSecond()));
        }

        return builder.build(prefix);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final LenientCompoundListCodec<?, ?> that = (LenientCompoundListCodec<?, ?>) o;
        return Objects.equals(keyCodec, that.keyCodec) && Objects.equals(elementCodec, that.elementCodec);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyCodec, elementCodec);
    }

    @Override
    public String toString() {
        return "LenientCompoundListCodec[" + keyCodec + " -> " + elementCodec + ']';
    }
}
