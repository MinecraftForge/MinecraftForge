/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.eventtest.internal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * A marker annotation used with the Event Testing Framework.
 * Can only be placed on a class, enum or record.
 *
 * This annotation is used with FML Scan Data to construct and register the test automatically.
 * The testName field is used in the output of the test.
 *
 * @author Curle
 */
@Target({ElementType.TYPE})
public @interface TestHolder {
    String value();
}
