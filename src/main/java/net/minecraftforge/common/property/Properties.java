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

package net.minecraftforge.common.property;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraftforge.common.model.IModelState;

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
        @Override
        public String getName() { return "forge_animation"; }
        @Override
        public boolean isValid(IModelState state) { return true; }
        @Override
        public Class<IModelState> getType() { return IModelState.class; }
        @Override
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

        @Override
        public String getName()
        {
            return parent.getName();
        }

        @Override
        public boolean isValid(V value)
        {
            return parent.getAllowedValues().contains(value);
        }

        @Override
        public Class<V> getType()
        {
            return parent.getValueClass();
        }

        @Override
        public String valueToString(V value)
        {
            return parent.getName(value);
        }
    }
}
