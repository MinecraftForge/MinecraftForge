// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.
package net.minecraftforge.common.util;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.ListBuilder;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class ImprovedListCodec<A> implements Codec<List<A>> {
    private final Codec<A> elementCodec;

    public ImprovedListCodec(final Codec<A> elementCodec) {
        this.elementCodec = elementCodec;
    }

    @Override
    public <T> DataResult<T> encode(final List<A> input, final DynamicOps<T> ops, final T prefix) {
        final ListBuilder<T> builder = ops.listBuilder();

        for (final A a : input) {
            builder.add(elementCodec.encodeStart(ops, a));
        }

        return builder.build(prefix);
    }

    @Override
    public <T> DataResult<Pair<List<A>, T>> decode(final DynamicOps<T> ops, final T input) {
        return ops.getList(input).setLifecycle(Lifecycle.stable()).flatMap(stream -> {
            final ImmutableList.Builder<A> read = ImmutableList.builder();
            final Stream.Builder<T> failed = Stream.builder();
            // TODO: AtomicReference.getPlain/setPlain in java9+
            final MutableObject<DataResult<Unit>> result = new MutableObject<>(DataResult.success(Unit.INSTANCE, Lifecycle.stable()));

            stream.accept(t -> {
                final DataResult<Pair<A, T>> element = elementCodec.decode(ops, t);
                element.error().ifPresent(e -> failed.add(t));
                element.result().ifPresent(r -> read.add(r.getFirst())); // FORGE: This line moved outside the below apply2stable condition
                result.setValue(result.getValue().apply2stable((r, v) -> r, element));
            });

            final ImmutableList<A> elements = read.build();
            final T errors = ops.createList(failed.build());

            final Pair<List<A>, T> pair = Pair.of(elements, errors);

            return result.getValue().map(unit -> pair).setPartial(pair);
        });
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ImprovedListCodec<?> listCodec = (ImprovedListCodec<?>) o;
        return Objects.equals(elementCodec, listCodec.elementCodec);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elementCodec);
    }

    @Override
    public String toString() {
        return "ImprovedListCodec[" + elementCodec + ']';
    }
}
