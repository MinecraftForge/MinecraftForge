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

package net.minecraftforge.common.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;

public enum Models
{
    ;

    public static Object getHiddenModelPart(ImmutableList<String> path)
    {
        return new HiddenModelPart(path);
    }

    public static UnmodifiableIterator<String> getParts(Object part)
    {
        if(part instanceof HiddenModelPart)
        {
            return ((HiddenModelPart) part).getPath().iterator();
        }
        ImmutableSet<String> ret = ImmutableSet.of();
        return ret.iterator();
    }
}
