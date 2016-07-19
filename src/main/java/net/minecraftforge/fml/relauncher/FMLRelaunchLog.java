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

package net.minecraftforge.fml.relauncher;

import java.io.File;
import java.util.Locale;

import net.minecraftforge.fml.common.TracingPrintStream;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

public class FMLRelaunchLog {

    /**
     * Our special logger for logging issues to. We copy various assets from the
     * Minecraft logger to achieve a similar appearance.
     */
    public static final FMLRelaunchLog log = new FMLRelaunchLog();

    static File minecraftHome;
    private static boolean configured;

    private Logger myLog;

    static Side side;

    private FMLRelaunchLog()
    {
    }

    /**
     * Configure the FML logger and inject tracing printstreams.
     */
    private static void configureLogging()
    {
        log.myLog = LogManager.getLogger("FML");
        // Default side to client for test harness purposes
        if (side == null) side = Side.CLIENT;
        ThreadContext.put("side", side.name().toLowerCase(Locale.ENGLISH));
        configured = true;
        
        FMLRelaunchLog.fine("Injecting tracing printstreams for STDOUT/STDERR.");
        System.setOut(new TracingPrintStream(LogManager.getLogger("STDOUT"), System.out));
        System.setErr(new TracingPrintStream(LogManager.getLogger("STDERR"), System.err));
    }

    public static void log(String targetLog, Level level, String format, Object... data)
    {
        LogManager.getLogger(targetLog).log(level, String.format(format, data));
    }

    public static void log(Level level, String format, Object... data)
    {
        if (!configured)
        {
            configureLogging();
        }
        log.myLog.log(level, String.format(format, data));
    }

    public static void log(String targetLog, Level level, Throwable ex, String format, Object... data)
    {
        LogManager.getLogger(targetLog).log(level, String.format(format, data), ex);
    }

    public static void log(Level level, Throwable ex, String format, Object... data)
    {
        if (!configured)
        {
            configureLogging();
        }
        log.myLog.log(level, String.format(format, data), ex);
    }

    public static void severe(String format, Object... data)
    {
        log(Level.ERROR, format, data);
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

    public Logger getLogger()
    {
        return myLog;
    }
}
