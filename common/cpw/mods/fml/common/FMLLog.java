package cpw.mods.fml.common;

import java.util.logging.Level;
import java.util.logging.Logger;

public class FMLLog
{
    private static cpw.mods.fml.relauncher.FMLRelaunchLog coreLog = cpw.mods.fml.relauncher.FMLRelaunchLog.log;

    public static void log(Level level, String format, Object... data)
    {
        coreLog.log(level, format, data);
    }

    public static void log(Level level, Throwable ex, String format, Object... data)
    {
        coreLog.log(level, ex, format, data);
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
    public static Logger getLogger()
    {
        return coreLog.getLogger();
    }
}
