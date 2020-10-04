package net.minecraftforge.common.world;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Optional;

public interface ICodecExtraHolder
{
    class PrivateFieldHolder
    {
        private static IdentityHashMap<ICodecExtraHolder, List<ICodecExtra>> EXTRA_HOLDER = new IdentityHashMap<>();
    }

    default void setExtra(ICodecExtra extra)
    {
        List<ICodecExtra> extras = PrivateFieldHolder.EXTRA_HOLDER.computeIfAbsent(this, s -> new ArrayList<>());
        if(extras.stream().anyMatch(e -> e.getType().equals(extra.getType())))
            throw new IllegalArgumentException("Can not add twice the same extra type to the same object");
        extras.add(extra);
    }

    default void setupExtras(List<ICodecExtra> extras)
    {
        if(PrivateFieldHolder.EXTRA_HOLDER.put(this, extras) != null)
        {
            throw new RuntimeException("Setup extras on the same object twice!");
        }
    }

    default <A extends ICodecExtra> Optional<A> getExtra(CodecExtraType<A> type)
    {
        return PrivateFieldHolder.EXTRA_HOLDER.get(this).stream().filter(e -> e.getType().equals(type)).findAny().map(e -> (A) e);
    }

    class ExtraParser<E extends ICodecExtraHolder> implements Codec<E>
    {
        private final Codec<E> base;

        public ExtraParser(Codec<E> base)
        {
            this.base = base;
        }

        @Override
        public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> ops, T input)
        {
            return this.base.decode(ops, input).flatMap(baseResult ->
            {
                DataResult<List<ICodecExtra>> extrasResult = ICodecExtra.LIST_CODEC.parse(ops, input);
                if(extrasResult.result().isPresent())
                {
                    baseResult.getFirst().setupExtras(extrasResult.result().get());
                    return DataResult.success(baseResult);
                }
                return DataResult.error("Could not setup forge extras", baseResult);
            });
        }

        @Override
        public <T> DataResult<T> encode(E input, DynamicOps<T> ops, T prefix)
        {
            return this.base.encode(input, ops, prefix)
                    .flatMap(t -> ICodecExtra.LIST_CODEC.encode(PrivateFieldHolder.EXTRA_HOLDER.get(input), ops, t));
        }
    }
}
