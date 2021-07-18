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

import java.util.function.Function;
import java.util.function.Supplier;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

/**
 * Similar to the standard DFU Dispatch Codec
 * but accepts a "fallback" decoder that will be used if the serialized object has no "type" field.
 * 
 * When decoding a serialized object, if the object has no type field and the fallback decoder fails to parse,
 * the fallback decoder's error result will be reported.
 * If the object *does* have a type field and fails to parse, then the dispatch codec's error result will be reported.
 * 
 * This fallback behavior could be implemented with a standard DFU Either Codec,
 * but when both of an either codec's decoders fail, only the second (fallback) decoder's parse error is reported.
 * This can make finding errors difficult when the either's first codec is the one being intended to be used (hence this class)
 */
public class FallbackKeyDispatchCodec<V> implements Codec<V>
{
    /**
     * 
     * @param <K> The "key" type (something that can provide a codec to read the value type)
     * @param <V> The "value" type (the things that jsons can be parsed into)
     * @param typeCodec The codec for the key type (typically looks up a value in a static registry from an ID)
     * @param typeLookup A function to determine a value object's type instance
     * @param codecLookup A function to retrieve a value type codec from a key type instance
     * @param fallbackCodec The codec to use to parse a json if the "type" field is not specified
     * @return a dispatch-like codec
     */
    public static <K,V> Codec<V> dispatch(final Codec<K> typeCodec, final Function<? super V, ? extends K> typeLookup, final Function<? super K, ? extends Codec<? extends V>> codecLookup, Supplier<Codec<V>> fallbackCodec)
    {
        return new FallbackKeyDispatchCodec<>(typeCodec.dispatch(typeLookup, codecLookup), fallbackCodec, typeLookup.andThen(codecLookup));
    }
    
    protected static final String TYPEKEY = "type";
    private final Codec<V> dispatchCodec;
    private final Supplier<Codec<V>> fallbackCodec;
    private final Function<? super V, ? extends Codec<? extends V>> valueToCodecLookup;

    protected FallbackKeyDispatchCodec(final Codec<V> dispatchCodec, final Supplier<Codec<V>> fallbackCodec, final Function<? super V, ? extends Codec<? extends V>> valueToCodecLookup)
    {
        this.dispatchCodec = dispatchCodec;
        this.fallbackCodec = fallbackCodec;
        this.valueToCodecLookup = valueToCodecLookup;
    }
    
    @Override
    public <T> DataResult<T> encode(final V input, final DynamicOps<T> ops, final T prefix)
    {
        final Codec<? extends V> codecForInput = this.valueToCodecLookup.apply(input);
        if (codecForInput == this.fallbackCodec)
        {
            return this.fallbackCodec.get().encode(input, ops, prefix);
        }
        else
        {
            return this.dispatchCodec.encode(input, ops, prefix);
        }
    }

    @Override
    public <T> DataResult<Pair<V, T>> decode(final DynamicOps<T> ops, final T input)
    {
        // if type field is present, decode using the dispatch codec
        // if type field is present, any errors reported must be reported via the dispatch codec
        final DataResult<T> keyRead = ops.get(input, TYPEKEY);
        if (keyRead.result().isPresent())
        {
            return this.dispatchCodec.decode(ops, input);
        }
        // otherwise, we decode using the fallback codec -- any errors reported can only come from the fallback codec
        else
        {
            return this.fallbackCodec.get().decode(ops, input);
        }
    }
    
    @Override
    public String toString()
    {
        return "FallbackKeyDispatchCodec[" + this.dispatchCodec + "; " + this.fallbackCodec + "]";
    }
}
