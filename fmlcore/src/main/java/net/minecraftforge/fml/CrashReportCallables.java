/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class CrashReportCallables
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final List<ISystemReportExtender> crashCallables = Collections.synchronizedList(new ArrayList<>());

    /**
     * Register a custom {@link ISystemReportExtender}
     */
    public static void registerCrashCallable(ISystemReportExtender callable)
    {
        crashCallables.add(callable);
    }

    /**
     * Register a {@link ISystemReportExtender system report extender} with the given header name and content
     * generator, which will always be appended to the system report
     * @param headerName The name of the system report entry
     * @param reportGenerator The report generator to be called when a crash report is built
     */
    public static void registerCrashCallable(String headerName, Supplier<String> reportGenerator)
    {
        registerCrashCallable(new ISystemReportExtender()
        {
            @Override
            public String getLabel()
            {
                return headerName;
            }

            @Override
            public String get()
            {
                return reportGenerator.get();
            }
        });
    }

    /**
     * Register a {@link ISystemReportExtender system report extender} with the given header name and content
     * generator, which will only be appended to the system report when the given {@link BooleanSupplier} returns true
     * @param headerName The name of the system report entry
     * @param reportGenerator The report generator to be called when a crash report is built
     * @param active The supplier of the flag to be checked when a crash report is built
     */
    public static void registerCrashCallable(String headerName, Supplier<String> reportGenerator, BooleanSupplier active)
    {
        registerCrashCallable(new ISystemReportExtender()
        {
            @Override
            public String getLabel()
            {
                return headerName;
            }

            @Override
            public String get()
            {
                return reportGenerator.get();
            }

            @Override
            public boolean isActive()
            {
                try
                {
                    return active.getAsBoolean();
                }
                catch (Throwable t)
                {
                    LOGGER.warn("CrashCallable '{}' threw an exception while checking the active flag, disabling", headerName, t);
                    return false;
                }
            }
        });
    }

    public static List<ISystemReportExtender> allCrashCallables()
    {
        return List.copyOf(crashCallables);
    }
}
