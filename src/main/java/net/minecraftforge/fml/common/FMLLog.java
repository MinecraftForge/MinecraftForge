/*
 * Minecraft Forge
 * Copyright (c) 2016.
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
