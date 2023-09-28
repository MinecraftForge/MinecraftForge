/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug;

import net.minecraftforge.fml.CrashReportCallables;
import net.minecraftforge.fml.common.Mod;

/**
 * Test features and guards of crash-callables.
 * <ul>
 * <li>The "AlwaysActiveCrashCallable" must always be printed to the crash report</li>
 * <li>The "ToggleableCrashCallable" must only be printed to the crash report when the {@code ENABLED} flag is {@code true}</li>
 * <li>The "BadContentCrashCallable" must always print "BadContentCrashCallable: ERR" to the crash report and
 * print an exception to the log (relies on a try-catch in vanilla code)</li>
 * <li>The "BadFlagCrashCallable" must never print to the crash report and must print an exception to the log</li>
 * </ul>
 * To initiate a debug crash to test this, hold F3 + C for 10 seconds.
 */
@Mod("crash_callable_test")
public class CrashCallableTest
{
    private static final boolean ENABLED = true;

    public CrashCallableTest()
    {
        CrashReportCallables.registerCrashCallable("AlwaysActiveCrashCallable", () -> "test");
        CrashReportCallables.registerCrashCallable("ToggleableCrashCallable", () -> "active", () -> ENABLED);
        CrashReportCallables.registerCrashCallable("BadContentCrashCallable", () ->
        {
            throw new UnsupportedOperationException();
        });
        CrashReportCallables.registerCrashCallable("BadFlagCrashCallable", () -> "why am I here?!", () ->
        {
            throw new UnsupportedOperationException();
        });
    }
}
