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
