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

package net.minecraftforge.fml.relauncher;

import java.io.File;
import java.util.Locale;

import net.minecraftforge.fml.common.TracingPrintStream;

import org.apache.logging.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class FMLRelaunchLog {

    /**
     * Our special logger for logging issues to. We copy various assets from the
     * Minecraft logger to achieve a similar appearance.
     */
    public static FMLRelaunchLog log = new FMLRelaunchLog();

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
        log.myLog = LoggerFactory.getLogger("FML");
        MDC.put("side", side.name().toLowerCase(Locale.ENGLISH));
        configured = true;
        
        FMLRelaunchLog.fine("Injecting tracing printstreams for STDOUT/STDERR.");
        System.setOut(new TracingPrintStream(LoggerFactory.getLogger("STDOUT"), System.out));
        System.setErr(new TracingPrintStream(LoggerFactory.getLogger("STDERR"), System.err));
    }

    public static void log(String targetLog, Level level, String format, Object... data)
    {
        log(LoggerFactory.getLogger(targetLog), level, String.format(format, data));
    }

    public static void log(Level level, String format, Object... data)
    {
        if (!configured)
        {
            configureLogging();
        }
        log(log.myLog, level, String.format(format, data));
    }

    public static void log(String targetLog, Level level, Throwable ex, String format, Object... data)
    {
        log(LoggerFactory.getLogger(targetLog), level, String.format(format, data), ex);
    }

    public static void log(Level level, Throwable ex, String format, Object... data)
    {
        if (!configured)
        {
            configureLogging();
        }
        log(log.myLog, level, String.format(format, data), ex);
    }

    // For log4j2 -> slf4j transition
    private static void log(Logger log, Level level, String format, Object... objs) {
        switch(level) {
            case TRACE:
                log.trace(format, objs);
                break;
            case DEBUG:
                log.debug(format, objs);
                break;
            case INFO:
                log.info(format, objs);
                break;
            case WARN:
                log.warn(format, objs);
                break;
            case ERROR:
                log.error(format, objs);
                break;
            case FATAL:
                log.error(format, objs);
                break;
        }
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
