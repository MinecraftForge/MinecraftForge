package net.minecraftforge.fml.common;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoadingModsTimer implements Runnable
{
    private static final Thread loadingModsTimer = new Thread(new LoadingModsTimer(), "Loading Mods Timer");
    private static final Logger LOGGER = LogManager.getLogger("LoadingModsTimer");
    private static boolean isLoadingMods = false;
    private static int initSecond = 0;

    private LoadingModsTimer()
    {

    }

    static void startCounter()
    {
        isLoadingMods = true;
        LOGGER.log(Level.INFO, "Start to count the time that loading mods takes");
        loadingModsTimer.start();
    }

    static void stopCounter()
    {
        isLoadingMods = false;
        LOGGER.log(Level.INFO, "Loading mods took " + initSecond + " seconds");
    }

    public void run()
    {
        long t0 = System.currentTimeMillis();
        try
        {
            while (isLoadingMods)
            {
                long t1 = System.currentTimeMillis();
                if (t1 - t0 >= 999)
                {
                    t0 = System.currentTimeMillis();
                    initSecond++;
                }
                Thread.sleep(1);
            }
        }
        catch (InterruptedException ex)
        {
            ex.printStackTrace();
        }
    }

    public static int getInitSecond()
    {
        if (Loader.instance().hasReachedState(LoaderState.AVAILABLE))
        {
            return initSecond;
        }
        else
        {
            throw new IllegalStateException("Do not access initSecond until FML has reached state AVAILABLE");
        }
    }
}
