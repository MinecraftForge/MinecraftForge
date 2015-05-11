package net.minecraftforge.fml.common.functions;

import com.google.common.base.Function;

public class TypeCastFunction<T> implements Function<Object, T> {
    private Class<T> type;
    public TypeCastFunction(Class<T> type)
    {
        this.type = type;
    }

    @Override
    public T apply(Object input)
    {
        return type.cast(input);
    }
}
