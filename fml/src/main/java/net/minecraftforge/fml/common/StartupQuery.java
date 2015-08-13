package net.minecraftforge.fml.common;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import net.minecraft.server.MinecraftServer;

public class StartupQuery {
    // internal class/functionality, do not use

    public static boolean confirm(String text)
    {
        StartupQuery query = new StartupQuery(text, new AtomicBoolean());
        query.execute();
        return query.getResult();
    }

    public static void notify(String text)
    {
        StartupQuery query = new StartupQuery(text, null);
        query.execute();
    }

    public static void abort()
    {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server != null) server.initiateShutdown();

        aborted = true; // to abort loading and go back to the main menu
        throw new AbortedException(); // to halt the server
    }


    public static void reset()
    {
        pending = null;
        aborted = false;
    }

    public static boolean check()
    {
        if (pending != null)
        {
            try
            {
                FMLCommonHandler.instance().queryUser(pending);
            }
            catch (InterruptedException e)
            {
                FMLLog.warning("query interrupted");
                abort();
            }

            pending = null;
        }

        return !aborted;
    }

    private static volatile StartupQuery pending;
    private static volatile boolean aborted = false;


    private StartupQuery(String text, AtomicBoolean result)
    {
        this.text = text;
        this.result = result;
    }

    public Boolean getResult()
    {
        return result == null ? null : result.get();
    }

    public void setResult(boolean result)
    {
        this.result.set(result);
    }

    public String getText()
    {
        return text;
    }

    public boolean isSynchronous()
    {
        return synchronous;
    }

    public void finish()
    {
        signal.countDown();
    }

    private void execute()
    {
        String prop = System.getProperty("fml.queryResult");

        if (result != null && prop != null)
        {
            FMLLog.info("Using fml.queryResult %s to answer the following query:\n%s", prop, text);

            if (prop.equalsIgnoreCase("confirm"))
            {
                setResult(true);
                return;
            }
            else if (prop.equalsIgnoreCase("cancel"))
            {
                setResult(false);
                return;
            }

            FMLLog.warning("Invalid value for fml.queryResult: %s, expected confirm or cancel", prop);
        }

        synchronous = false;
        pending = this; // let the other thread start the query

        // from the integrated server thread: the client will eventually check pending and execute the query
        // from the client thread: synchronous execution
        // dedicated server: command handling in mc is synchronous, execute the server-side query directly
        if (FMLCommonHandler.instance().getSide().isServer() ||
                FMLCommonHandler.instance().getEffectiveSide().isClient())
        {
            synchronous = true;
            check();
        }

        try
        {
            signal.await();
            reset();
        }
        catch (InterruptedException e)
        {
            FMLLog.warning("query interrupted");
            abort();
        }
    }

    private String text;
    private AtomicBoolean result;
    private CountDownLatch signal = new CountDownLatch(1);
    private volatile boolean synchronous;


    /**
     * Exception not being caught by the crash report generation logic.
     */
    public static class AbortedException extends RuntimeException
    {
        private static final long serialVersionUID = -5933665223696833921L;

        private AbortedException()
        {
            super();
        }
    }
}
