package cpw.mods.fml.common;

import java.io.File;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class FMLLog
{

    /**
     * Our special logger for logging issues to. We copy various assets from the
     * Minecraft logger to acheive a similar appearance.
     */
    public static FMLLog log = new FMLLog();
    
    private static File minecraftHome;
    private static boolean configured;
    private Logger myLog;

    private FMLLog()
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
        FMLLogFormatter formatter = new FMLLogFormatter();
        ConsoleHandler ch = new ConsoleHandler();
        log.myLog.setUseParentHandlers(false);
        log.myLog.addHandler(ch);
        ch.setFormatter(formatter);
        log.myLog.setLevel(Level.ALL);
        try
        {
            File logPath = new File(minecraftHome, "ForgeModLoader-%g.log");
            FileHandler fileHandler = new FileHandler(logPath.getPath(), 0, 3);
            fileHandler.setFormatter(new FMLLogFormatter());
            fileHandler.setLevel(Level.ALL);
            log.myLog.addHandler(fileHandler);
        }
        catch (Exception e)
        {
        }
        
        // Reset global logging to shut up other logging sources (thanks guava!)
        configured = true;
    }
    
    public static void log(Level level, String format, Object... data)
    {
        if (!configured)
        {
            configureLogging();
        }
        log.myLog.log(level, String.format(format, data));
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
}
