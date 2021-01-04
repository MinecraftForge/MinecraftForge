package net.minecraftforge.common.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.ListBuilder;
import com.mojang.serialization.codecs.ListCodec;
import net.minecraft.util.Unit;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * ListCodec that provides a correct partial result.
 * The normal {@link ListCodec} will stop collecting results after the first complete error.
 *
 * This also gives the option to not include the partial results of individual elements to the partial result list.
 */
public class ImprovedListCodec<A> implements Codec<List<A>>
{
    private final Codec<A> elementCodec;
    private final boolean withPartials;

    public static <A> Codec<List<A>> create(Codec<A> codec)
    {
        return new ImprovedListCodec<>(codec, true);
    }

    public static <A> Codec<List<A>> createNoPartials(Codec<A> codec)
    {
        return new ImprovedListCodec<>(codec, false);
    }

    private ImprovedListCodec(Codec<A> codec, boolean withPartials)
    {
        this.elementCodec = codec;
        this.withPartials = withPartials;
    }

    @Override
    public <T> DataResult<Pair<List<A>, T>> decode(DynamicOps<T> ops, T input)
    {
        return ops.getList(input).flatMap(list ->
        {
            MutableObject<DataResult<Pair<List<A>, Stream.Builder<T>>>> ret =
                    new MutableObject<>(DataResult.success(Pair.of(new ArrayList<>(), Stream.builder())));
            MutableObject<DataResult<Unit>> errorAccumulator = new MutableObject<>(DataResult.success(Unit.INSTANCE));

            list.accept(t ->
            {
                DataResult<A> dr = elementCodec.parse(ops, t);
                ret.setValue(ret.getValue().map(p ->
                        p.mapFirst(l ->
                        {
                            if (withPartials || dr.result().isPresent())
                                dr.map(l::add);
                            return l;
                        }).mapSecond(pre -> dr.result().isPresent() ? pre : pre.add(t)) // Add the json to the errors.
                ));
                errorAccumulator.setValue(errorAccumulator.getValue()
                        .flatMap(u -> dr)
                        .map(o -> Unit.INSTANCE)
                        .setPartial(Unit.INSTANCE)); // Set partial, to be able to always concatenate the error. This is also what sets the partial for the ret list.
            });

            return errorAccumulator.getValue().flatMap(u -> ret.getValue()).map(dr -> dr.mapSecond(sb -> ops.createList(sb.build())));
        });
    }

    @Override
    public <T> DataResult<T> encode(List<A> input, DynamicOps<T> ops, T prefix) 
    {
        final ListBuilder<T> builder = ops.listBuilder();

        for (final A a : input)
            builder.add(elementCodec.encodeStart(ops, a));

        return builder.build(prefix);
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final ImprovedListCodec<?> other = (ImprovedListCodec<?>) o;
        return Objects.equals(elementCodec, other.elementCodec) && Objects.equals(withPartials, other.withPartials);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(elementCodec, withPartials);
    }

    @Override
    public String toString()
    {
        return "BetterListCodec[withPartials=" + withPartials + ", " + elementCodec +"]";
    }
}
