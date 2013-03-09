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

package cpw.mods.fml.common;

import java.util.logging.Level;
import java.util.logging.Logger;

public class FMLLog
{
    private static cpw.mods.fml.relauncher.FMLRelaunchLog coreLog = cpw.mods.fml.relauncher.FMLRelaunchLog.log;

    public static void log(String logChannel, Level level, String format, Object... data)
    {
        coreLog.log(logChannel, level, format, data);
    }

    public static void log(Level level, String format, Object... data)
    {
        coreLog.log(level, format, data);
    }

    public static void log(String logChannel, Level level, Throwable ex, String format, Object... data)
    {
        coreLog.log(logChannel, level, ex, format, data);
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

    public static void makeLog(String logChannel)
    {
        coreLog.makeLog(logChannel);
    }
}
