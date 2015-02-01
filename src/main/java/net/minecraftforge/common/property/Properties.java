package net.minecraftforge.common.property;

import java.lang.reflect.InvocationTargetException;

import net.minecraft.block.properties.IProperty;

import org.apache.commons.lang3.reflect.ConstructorUtils;

public class Properties
{
    public static <P extends IProperty, V>IUnlistedProperty<V> toUnlisted(P property)
    {
        return new PropertyAdapter(property);
    }

    public static class PropertyAdapter<V extends Comparable> implements IUnlistedProperty<V>
    {
        private final IProperty parent;

        public PropertyAdapter(IProperty parent)
        {
            this.parent = parent;
        }

        public String getName()
        {
            return parent.getName();
        }

        public boolean isValid(V value)
        {
            return parent.getAllowedValues().contains(value);
        }

        public Class<V> getType()
        {
            return parent.getValueClass();
        }

        public String valueToString(V value)
        {
            return parent.getName(value);
        }
    }
}
