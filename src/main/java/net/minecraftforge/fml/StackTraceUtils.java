/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

import java.util.Objects;

public final class StackTraceUtils {
    private StackTraceUtils() {}

    public static boolean threadClassNameEquals(final String className) {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return Objects.equals(stackTrace[stackTrace.length-1].getClassName(), className);
    }
}
