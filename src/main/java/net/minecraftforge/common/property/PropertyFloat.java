package net.minecraftforge.common.property;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class PropertyFloat implements IUnlistedProperty<Float>
{
    private final String name;
    private final Predicate<Float> validator;

    public PropertyFloat(String name)
    {
        this(name, Predicates.<Float>alwaysTrue());
    }

    public PropertyFloat(String name, Predicate<Float> validator)
    {
        this.name = name;
        this.validator = validator;
    }

    public String getName()
    {
        return name;
    }

    public boolean isValid(Float value)
    {
        return validator.apply(value);
    }

    public Class<Float> getType()
    {
        return Float.class;
    }

    public String valueToString(Float value)
    {
        return value.toString();
    }
}
