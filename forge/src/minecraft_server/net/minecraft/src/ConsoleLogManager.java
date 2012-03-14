package net.minecraft.src;

import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsoleLogManager
{
    /** Reference to the logger. */
    public static Logger logger = Logger.getLogger("Minecraft");

    /**
     * Initialises the console logger.
     */
    public static void init()
    {
        ConsoleLogFormatter var0 = new ConsoleLogFormatter();
        logger.setUseParentHandlers(false);
        ConsoleHandler var1 = new ConsoleHandler();
        var1.setFormatter(var0);
        logger.addHandler(var1);

        try
        {
            FileHandler var2 = new FileHandler("server.log", true);
            var2.setFormatter(var0);
            logger.addHandler(var2);
        }
        catch (Exception var3)
        {
            logger.log(Level.WARNING, "Failed to log to server.log", var3);
        }
    }
}
