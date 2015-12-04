package net.minecraftforge.common.property;

import net.minecraft.block.properties.IProperty;

public class Properties
{
    public static <V extends Comparable<V>> IUnlistedProperty<V> toUnlisted(IProperty<V> property)
    {
        return new PropertyAdapter<V>(property);
    }

    public static class PropertyAdapter<V extends Comparable<V>> implements IUnlistedProperty<V>
    {
        private final IProperty<V> parent;

        public PropertyAdapter(IProperty<V> parent)
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
