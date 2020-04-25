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

package net.minecraftforge.fml.util;

import java.util.function.Consumer;

/**
 * Three-consumer version of consumer. Allows wrapping methods with three arguments.
 */
public interface ThreeConsumer<T, U, V>
{

    /**
     * Bind arguments to the three consumer to generate a consumer.
     *
     * <pre>
     * {@code
     * ThreeConsumer.bindArgs(MyClass::instanceMethodReference, arg1, arg2).apply(myClassInstance)
     * }
     * </pre>
     *
     * @return a Consumer which has the second and third arguments bound.
     */
    static <T, U, V> Consumer<T> bindArgs(ThreeConsumer<? super T, U, V> c, U arg2, V arg3)
    {
        return (arg1) -> c.accept(arg1, arg2, arg3);
    }

    void accept(T t, U u, V v);
}
