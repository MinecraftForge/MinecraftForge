/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting.conditions;

import java.util.Optional;
import java.util.function.Supplier;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import net.minecraft.resources.DelegatingOps;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;

public class ConditionCodec {
    public static <T> Codec<T> checkingDecode(Codec<T> normal, Supplier<T> _default) {
        return checkingDecode(normal, _default, ICondition.DEFAULT_FIELD);
    }

    public static <T> Codec<T> checkingDecode(Codec<T> normal, Supplier<T> _default, String key) {
        return Codec.of(normal, new UnwrapDecoder<>(wrap(normal, key), _default));
    }

    public static <T> Decoder<Optional<T>> wrap(Decoder<T> normal) {
        return wrap(normal, ICondition.DEFAULT_FIELD);
    }

    public static <T> Decoder<Optional<T>> wrap(Decoder<T> normal, String key) {
        return new OptionalConditionalDecoder<>(normal, key);
    }

    public static <T> IContext getContext(DynamicOps<T> ops) {
        IContext ret = null;
        if (ops instanceof DelegatingOps<?> dele)
            ret = dele.getContext(ICondition.IContext.KEY);
        return ret == null ? IContext.EMPTY : ret;
    }

    private static record UnwrapDecoder<A>(Decoder<Optional<A>> normal, Supplier<A> _default) implements Decoder<A> {
        @Override
        public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
            var ret = normal.decode(ops, input);
            if (!ret.result().isPresent() || !ret.result().get().getFirst().isPresent())
                return ret.map(p -> p.mapFirst(e -> _default.get()));
            return ret.map(p -> p.mapFirst(Optional::get));
        }
    }

    private static record OptionalConditionalDecoder<A>(Decoder<A> normal, String key) implements Decoder<Optional<A>> {
        @Override
        public <T> DataResult<Pair<Optional<A>, T>> decode(DynamicOps<T> ops, T input) {
            var conditionRaw = ops.get(input, key);
            if (conditionRaw.error().isPresent() || !conditionRaw.result().isPresent())
                return normal.decode(ops, input).map(p -> p.mapFirst(Optional::of));

            var conditionDecoded = ICondition.CODEC.parse(ops, conditionRaw.result().get());
            if (conditionDecoded.error().isPresent())
                return DataResult.error(() -> conditionDecoded.error().get().message());

            var condition = conditionDecoded.result().get();
            if (!condition.test(getContext(ops), ops))
                return DataResult.success(Pair.of(Optional.empty(), ops.empty()));

            return normal.decode(ops, input).map(p -> p.mapFirst(Optional::of));
        }
    }
}
