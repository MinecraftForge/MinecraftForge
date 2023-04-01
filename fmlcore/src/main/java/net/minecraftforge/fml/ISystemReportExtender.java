/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

import java.util.function.Supplier;

public interface ISystemReportExtender extends Supplier<String>
{
    String getLabel();

    default boolean isActive()
    {
        return true;
    }
}
