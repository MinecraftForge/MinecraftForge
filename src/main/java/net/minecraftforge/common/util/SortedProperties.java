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

package net.minecraftforge.common.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/**
 * An Implementation of Properties that is sorted when iterating.
 * Made because i got tired of seeing config files written in random orders.
 * This is implemented very basically, and thus is not a speedy system.
 * This is not recommended for used in high traffic areas, and is mainly intended for writing to disc.
 */
public class SortedProperties extends Properties
{
    private static final long serialVersionUID = -8913480931455982442L;

    @Override
    public Set<Map.Entry<Object, Object>> entrySet()
    {
        Set<Map.Entry<Object, Object>> ret = new TreeSet<>((left, right) -> left.getKey().toString().compareTo(right.getKey().toString()));
        ret.addAll(super.entrySet());
        return ret;
    }

    @Override
    public Set<Object> keySet()
    {
        return new TreeSet<>(super.keySet());
    }

    @Override
    public synchronized Enumeration<Object> keys()
    {
        return Collections.enumeration(new TreeSet<>(super.keySet()));
    }

    public static void store(Properties props, OutputStream stream, String comment) throws IOException
    {
        SortedProperties sorted = new SortedProperties();
        sorted.putAll(props);
        sorted.store(stream, comment);
    }
}
