/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Range;

public class PropertyFloat implements IUnlistedProperty<Float>
{
    private final String name;
    private final Predicate<Float> validator;

    public PropertyFloat(String name)
    {
        this(name, Predicates.alwaysTrue());
    }

    public PropertyFloat(String name, float min, float max)
    {
        this(name, Range.closed(min, max));
    }

    public PropertyFloat(String name, Predicate<Float> validator)
    {
        this.name = name;
        this.validator = validator;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public boolean isValid(Float value)
    {
        return validator.apply(value);
    }

    @Override
    public Class<Float> getType()
    {
        return Float.class;
    }

    @Override
    public String valueToString(Float value)
    {
        return value.toString();
    }
}
