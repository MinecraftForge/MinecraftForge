/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     cpw - implementation
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
