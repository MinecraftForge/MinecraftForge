/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml;

import java.util.function.Supplier;

public interface ISystemReportExtender extends Supplier<String>
{
    String getLabel();

    default boolean isActive()
    {
        return true;
    }
}
