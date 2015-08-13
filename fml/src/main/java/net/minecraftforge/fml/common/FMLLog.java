/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package net.minecraftforge.fml.common;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("static-access")
public class FMLLog
{
    private static net.minecraftforge.fml.relauncher.FMLRelaunchLog coreLog = net.minecraftforge.fml.relauncher.FMLRelaunchLog.log;

    public static void log(String targetLog, Level level, String format, Object... data)
    {
        coreLog.log(targetLog, level, format, data);
    }

    public static void log(Level level, String format, Object... data)
    {
        coreLog.log(level, format, data);
    }

    public static void log(String targetLog, Level level, Throwable ex, String format, Object... data)
    {
        coreLog.log(targetLog, level, ex, format, data);
    }

    public static void log(Level level, Throwable ex, String format, Object... data)
    {
        coreLog.log(level, ex, format, data);
    }

    public static void severe(String format, Object... data)
    {
        log(Level.ERROR, format, data);
    }

    public static void bigWarning(String format, Object... data)
    {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        log(Level.WARN, "****************************************");
        log(Level.WARN, "* "+format, data);
        for (int i = 2; i < 8 && i < trace.length; i++)
        {
            log(Level.WARN, "*  at %s%s", trace[i].toString(), i == 7 ? "..." : "");
        }
        log(Level.WARN, "****************************************");
    }

    public static void warning(String format, Object... data)
    {
        log(Level.WARN, format, data);
    }

    public static void info(String format, Object... data)
    {
        log(Level.INFO, format, data);
    }

    public static void fine(String format, Object... data)
    {
        log(Level.DEBUG, format, data);
    }

    public static void finer(String format, Object... data)
    {
        log(Level.TRACE, format, data);
    }

    public static Logger getLogger()
    {
        return coreLog.getLogger();
    }
}
