/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.common;

import net.minecraft.crash.ICrashReportDetail;

public interface ICrashCallable extends ICrashReportDetail<String>
{
    String getLabel();
}
