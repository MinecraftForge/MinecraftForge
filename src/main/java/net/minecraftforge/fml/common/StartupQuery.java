/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import net.minecraft.server.MinecraftServer;

import javax.annotation.Nullable;

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
                FMLLog.log.warn("query interrupted");
                abort();
            }

            pending = null;
        }

        return !aborted;
    }

    private static volatile StartupQuery pending;
    private static volatile boolean aborted = false;


    private StartupQuery(String text, @Nullable AtomicBoolean result)
    {
        this.text = text;
        this.result = result;
    }

    @Nullable
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
            FMLLog.log.info("Using fml.queryResult {} to answer the following query:\n{}", prop, text);

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

            FMLLog.log.warn("Invalid value for fml.queryResult: {}, expected confirm or cancel", prop);
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
            FMLLog.log.warn("query interrupted");
            abort();
        }
    }

    private String text;
    @Nullable
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
