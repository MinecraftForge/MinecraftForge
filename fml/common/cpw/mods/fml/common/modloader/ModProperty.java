/*
 * The FML Forge Mod Loader suite.
 * Copyright (C) 2012 cpw
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package cpw.mods.fml.common.modloader;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author cpw
 *
 */
public class ModProperty
{
    private String info;
    private double min;
    private double max;
    private String name;
    private Field field;

    public ModProperty(Field f, String info, Double min, Double max, String name)
    {
        this.field = f;
        this.info = info;
        this.min = min != null ? min : Double.MIN_VALUE;
        this.max = max != null ? max : Double.MAX_VALUE;
        this.name = name;
    }
    public ModProperty(Field field, Map<String, Object> annotationInfo)
    {
        this(field, (String)annotationInfo.get("info"), (Double)annotationInfo.get("min"), (Double)annotationInfo.get("max"), (String)annotationInfo.get("name"));
    }

    public String name()
    {
        return name;
    }

    public double min()
    {
        return min;
    }

    public double max()
    {
        return max;
    }

    public String info()
    {
        return info;
    }

    public Field field()
    {
        return field;
    }
}
