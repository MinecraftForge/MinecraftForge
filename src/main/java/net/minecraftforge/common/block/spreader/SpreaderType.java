/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.common.block.spreader;

import java.util.HashMap;
import java.util.Map;

public final class SpreaderType
{
    private static final Map<String, SpreaderType> TYPES = new HashMap<>();

    public static final SpreaderType GRASS = getType("grass");
    public static final SpreaderType MYCELIUM = getType("mycelium");
    public static final SpreaderType CRIMSON = getType("crimson");
    public static final SpreaderType WARPED = getType("warped");
    public static final SpreaderType REVERT = getType("revert");

    public static SpreaderType getType(String name)
    {
        return TYPES.computeIfAbsent(name, key -> new SpreaderType(key));
    }

    private String name;

    private SpreaderType(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
