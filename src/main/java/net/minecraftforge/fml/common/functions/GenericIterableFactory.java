/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.fml.common.functions;

import java.util.Iterator;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

public class GenericIterableFactory {
    public static <T> Iterable<T> newCastingIterable(final Iterator<?> input, final Class<T> type)
    {
        return new Iterable<T>()
        {
            @Override
            public Iterator<T> iterator()
            {
                return Iterators.transform(input, new TypeCastFunction<T>(type));
            }
        };
    }

    public static <T> Iterable<T> newCastingIterable(Iterable<?> input, Class<T> type)
    {
        return Iterables.transform(input, new TypeCastFunction<T>(type));
    }
}
