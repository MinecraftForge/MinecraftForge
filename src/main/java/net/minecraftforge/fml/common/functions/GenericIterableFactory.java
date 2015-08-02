package net.minecraftforge.fml.common.functions;

import java.util.Iterator;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

public class GenericIterableFactory {
    public static <T> Iterable<T> newCastingIterable(final Iterator<?> input, final Class<T> type)
    {
        return new Iterable<T>()
        {
            @Override
            public Iterator<T> iterator()
            {
                return Iterators.transform(input, new TypeCastFunction<T>(type));
            }
        };
    }

    public static <T> Iterable<T> newCastingIterable(Iterable<?> input, Class<T> type)
    {
        return Iterables.transform(input, new TypeCastFunction<T>(type));
    }
}
