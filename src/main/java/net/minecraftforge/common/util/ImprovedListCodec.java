/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.common.util;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.ListCodec;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * This is a copy of {@link ListCodec}, but it conserves all successfully decoded elements, not only the ones before an element errors.
 * It also provides an option to include/exclude partial results of individual elements in the list partial result.
 *
 * This file was originally part of Data Fixer Upper (https://github.com/Mojang/DataFixerUpper)
 * It is used and modified here as per the MIT license, the text of which is included as per the license requirements:
 *
 * MIT License
 *
 * Copyright (c) Microsoft Corporation. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the Software), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED *AS IS*, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
        return ops.getList(input).setLifecycle(Lifecycle.stable()).flatMap(stream -> {
            final ImmutableList.Builder<A> read = ImmutableList.builder();
            final Stream.Builder<T> failed = Stream.builder();
            final MutableObject<DataResult<Unit>> result = new MutableObject<>(DataResult.success(Unit.INSTANCE, Lifecycle.stable()));

            stream.accept(t -> {
                final DataResult<Pair<A, T>> element = elementCodec.decode(ops, t);
                element.error().ifPresent(e -> failed.add(t));
                if (withPartials || element.result().isPresent())
                    element.resultOrPartial(s->{}).ifPresent(p -> read.add(p.getFirst()));
                result.setValue(result.getValue().apply2stable((r, v) -> r, element));
            });

            final ImmutableList<A> elements = read.build();
            final T errors = ops.createList(failed.build());

            final Pair<List<A>, T> pair = Pair.of(elements, errors);

            return result.getValue().map(unit -> pair).setPartial(pair);
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
