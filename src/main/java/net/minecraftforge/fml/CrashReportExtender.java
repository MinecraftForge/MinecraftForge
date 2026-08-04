/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

import cpw.mods.modlauncher.log.TransformingThrowablePatternConverter;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraftforge.fml.common.ICrashCallable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

public class CrashReportExtender
{
    private static List<ICrashCallable> crashCallables = Collections.synchronizedList(new ArrayList<>());

    public static void enhanceCrashReport(final CrashReport crashReport, final CrashReportCategory category)
    {
        for (final ICrashCallable call: crashCallables)
        {
            category.addDetail(call.getLabel(), call);
        }
    }

    public static void registerCrashCallable(ICrashCallable callable)
    {
        crashCallables.add(callable);
    }

    public static void registerCrashCallable(String headerName, Callable<String> reportGenerator) {
        registerCrashCallable(new ICrashCallable() {
            @Override
            public String getLabel() {
                return headerName;
            }
            @Override
            public String call() throws Exception {
                return reportGenerator.call();
            }
        });
    }
    public static void addCrashReportHeader(StringBuilder stringbuilder, CrashReport crashReport)
    {
    }

    public static String generateEnhancedStackTrace(final Throwable throwable) {
        return TransformingThrowablePatternConverter.generateEnhancedStackTrace(throwable);
    }

}
