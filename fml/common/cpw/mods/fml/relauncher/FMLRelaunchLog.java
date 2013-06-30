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

package cpw.mods.fml.relauncher;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import net.minecraft.launchwrapper.LogWrapper;

import com.google.common.base.Throwables;

public class FMLRelaunchLog
{

    private static class ConsoleLogWrapper extends Handler
    {
        @Override
        public void publish(LogRecord record)
        {
            boolean currInt = Thread.interrupted();
            try
            {
                ConsoleLogThread.recordQueue.put(record);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace(errCache);
            }
            if (currInt)
            {
                Thread.currentThread().interrupt();
            }
        }

        @Override
        public void flush()
        {

        }

        @Override
        public void close() throws SecurityException
        {
        }

    }
    private static class ConsoleLogThread implements Runnable
    {
        static ConsoleHandler wrappedHandler = new ConsoleHandler();
        static LinkedBlockingQueue<LogRecord> recordQueue = new LinkedBlockingQueue<LogRecord>();
        @Override
        public void run()
        {
            do
            {
                LogRecord lr;
                try
                {
                    lr = recordQueue.take();
                    wrappedHandler.publish(lr);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace(errCache);
                    Thread.interrupted();
                    // Stupid
                }
            }
            while (true);
        }
    }
    private static class LoggingOutStream extends ByteArrayOutputStream
    {
        private Logger log;
        private StringBuilder currentMessage;

        public LoggingOutStream(Logger log)
        {
            this.log = log;
            this.currentMessage = new StringBuilder();
        }

        @Override
        public void flush() throws IOException
        {
            String record;
            synchronized(FMLRelaunchLog.class)
            {
                super.flush();
                record = this.toString();
                super.reset();

                currentMessage.append(record.replace(FMLLogFormatter.LINE_SEPARATOR, "\n"));
                // Are we longer than just the line separator?
                int lastIdx = -1;
                int idx = currentMessage.indexOf("\n",lastIdx+1);
                while (idx >= 0)
                {
                    log.log(Level.INFO, currentMessage.substring(lastIdx+1,idx));
                    lastIdx = idx;
                    idx = currentMessage.indexOf("\n",lastIdx+1);
                }
                if (lastIdx >= 0)
                {
                    String rem = currentMessage.substring(lastIdx+1);
                    currentMessage.setLength(0);
                    currentMessage.append(rem);
                }
            }
        }
    }
    /**
     * Our special logger for logging issues to. We copy various assets from the
     * Minecraft logger to achieve a similar appearance.
     */
    public static FMLRelaunchLog log = new FMLRelaunchLog();

    static File minecraftHome;
    private static boolean configured;

    private static Thread consoleLogThread;

    private static PrintStream errCache;
    private Logger myLog;

    private static FileHandler fileHandler;

    private static FMLLogFormatter formatter;

    static String logFileNamePattern;

    private FMLRelaunchLog()
    {
    }
    /**
     * Configure the FML logger
     */
    private static void configureLogging()
    {
        LogManager.getLogManager().reset();
        Logger globalLogger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        globalLogger.setLevel(Level.OFF);

        log.myLog = Logger.getLogger("ForgeModLoader");
        LogWrapper.retarget(log.myLog);

        Logger stdOut = Logger.getLogger("STDOUT");
        stdOut.setParent(log.myLog);
        Logger stdErr = Logger.getLogger("STDERR");
        stdErr.setParent(log.myLog);
        log.myLog.setLevel(Level.ALL);
        log.myLog.setUseParentHandlers(false);
        consoleLogThread = new Thread(new ConsoleLogThread());
        consoleLogThread.setDaemon(true);
        consoleLogThread.start();
        formatter = new FMLLogFormatter();
        try
        {
            File logPath = new File(minecraftHome, logFileNamePattern);
            fileHandler = new FileHandler(logPath.getPath(), 0, 3)
            {
                public synchronized void close() throws SecurityException {
                    // We don't want this handler to reset
                }
            };
        }
        catch (Throwable t)
        {
            throw Throwables.propagate(t);
        }

        resetLoggingHandlers();

        // Set system out to a log stream
        errCache = System.err;

        System.setOut(new PrintStream(new LoggingOutStream(stdOut), true));
        System.setErr(new PrintStream(new LoggingOutStream(stdErr), true));

        configured = true;
    }
    private static void resetLoggingHandlers()
    {
        ConsoleLogThread.wrappedHandler.setLevel(Level.parse(System.getProperty("fml.log.level","INFO")));
        // Console handler captures the normal stderr before it gets replaced
        log.myLog.addHandler(new ConsoleLogWrapper());
        ConsoleLogThread.wrappedHandler.setFormatter(formatter);
        fileHandler.setLevel(Level.ALL);
        fileHandler.setFormatter(formatter);
        log.myLog.addHandler(fileHandler);
    }

    public static void loadLogConfiguration(File logConfigFile)
    {
        if (logConfigFile!=null && logConfigFile.exists() && logConfigFile.canRead())
        {
            try
            {
                LogManager.getLogManager().readConfiguration(new FileInputStream(logConfigFile));
                resetLoggingHandlers();
            }
            catch (Exception e)
            {
                log(Level.SEVERE, e, "Error reading logging configuration file %s", logConfigFile.getName());
            }
        }
    }
    public static void log(String logChannel, Level level, String format, Object... data)
    {
        makeLog(logChannel);
        Logger.getLogger(logChannel).log(level, String.format(format, data));
    }

    public static void log(Level level, String format, Object... data)
    {
        if (!configured)
        {
            configureLogging();
        }
        log.myLog.log(level, String.format(format, data));
    }

    public static void log(String logChannel, Level level, Throwable ex, String format, Object... data)
    {
        makeLog(logChannel);
        Logger.getLogger(logChannel).log(level, String.format(format, data), ex);
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
        log(Level.SEVERE, format, data);
    }

    public static void warning(String format, Object... data)
    {
        log(Level.WARNING, format, data);
    }

    public static void info(String format, Object... data)
    {
        log(Level.INFO, format, data);
    }

    public static void fine(String format, Object... data)
    {
        log(Level.FINE, format, data);
    }

    public static void finer(String format, Object... data)
    {
        log(Level.FINER, format, data);
    }

    public static void finest(String format, Object... data)
    {
        log(Level.FINEST, format, data);
    }
    public Logger getLogger()
    {
        return myLog;
    }
    public static void makeLog(String logChannel)
    {
        Logger l = Logger.getLogger(logChannel);
        l.setParent(log.myLog);
    }
}
