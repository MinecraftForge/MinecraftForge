package cpw.mods.fml.common.functions;

import com.google.common.collect.Iterables;

public class GenericIterableFactory {
    public static <T> Iterable<T> newCastingIterable(Iterable<?> input, Class<T> type)
    {
        return Iterables.transform(input, new TypeCastFunction<T>(type));
    }
}
