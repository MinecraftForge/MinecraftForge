package net.minecraftforge.common.property;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraftforge.client.model.IModelState;

public class Properties
{
    /**
     * Property indicating if the model should be rendered in the static renderer or in the TESR. AnimationTESR sets it to false.
     */
    public static final PropertyBool StaticProperty = PropertyBool.create("static");

    /**
     * Property holding the IModelState used for animating the model in the TESR.
     */
    public static final IUnlistedProperty<IModelState> AnimationProperty = new IUnlistedProperty<IModelState>()
    {
        public String getName() { return "forge_animation"; }
        public boolean isValid(IModelState state) { return true; }
        public Class<IModelState> getType() { return IModelState.class; }
        public String valueToString(IModelState state) { return state.toString(); }
    };

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
