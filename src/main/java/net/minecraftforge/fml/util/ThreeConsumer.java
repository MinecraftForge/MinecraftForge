/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
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
