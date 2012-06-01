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

import java.io.File;

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

    public ModProperty(String info, double min, double max, String name)
    {
        this.info = info;
        this.min = min;
        this.max = max;
        this.name = name;
    }
    /**
     * @return
     */
    public String name()
    {
        // TODO Auto-generated method stub
        return name;
    }
    /**
     * @return
     */
    public double min()
    {
        // TODO Auto-generated method stub
        return min;
    }
    /**
     * @return
     */
    public double max()
    {
        // TODO Auto-generated method stub
        return max;
    }
    /**
     * @return
     */
    public String info()
    {
        // TODO Auto-generated method stub
        return info;
    }
}
