/*
 * Minecraft Forge
 * Copyright (c) 2018.
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

package net.minecraftforge.fml;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.PendingCommand;
import net.minecraftforge.fml.client.gui.GuiConfirmation;
import net.minecraftforge.fml.client.gui.GuiNotification;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;

public class StartupQuery {
    // internal class/functionality, do not use
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker SQ = MarkerManager.getMarker("STARTUPQUERY");

    public static boolean confirm(String text)
    {
        StartupQuery query = new StartupQuery(text, new AtomicBoolean());
        query.execute();
        return query.getResult();
    }

    private InterruptedException exception;

    public static void notify(String text)
    {
        StartupQuery query = new StartupQuery(text, null);
        query.execute();
    }

    public static void abort()
    {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
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
                try
                {
                    SidedProvider.STARTUPQUERY.<Consumer<StartupQuery>>get().accept(pending);
                }
                catch (RuntimeException e)
                {
                }
                pending.throwException();
            }
            catch (InterruptedException e)
            {
                LOGGER.warn(SQ, "query interrupted");
                abort();
            }

            pending = null;
        }

        return !aborted;
    }

    private void throwException() throws InterruptedException
    {
        if (exception != null) throw exception;
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
            LOGGER.info(SQ, "Using fml.queryResult {} to answer the following query:\n{}", prop, text);

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

            LOGGER.warn(SQ, "Invalid value for fml.queryResult: {}, expected confirm or cancel", prop);
        }

        synchronous = false;
        pending = this; // let the other thread start the query

        // from the integrated server thread: the client will eventually check pending and execute the query
        // from the client thread: synchronous execution
        // dedicated server: command handling in mc is synchronous, execute the server-side query directly
        if (FMLEnvironment.dist.isDedicatedServer() || EffectiveSide.get() == LogicalSide.CLIENT)
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
            LOGGER.warn(SQ, "query interrupted");
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


    public static class QueryWrapper
    {
        public static Consumer<StartupQuery> clientQuery(Supplier<Minecraft> clientSupplier)
        {
            return (query) -> {
                Minecraft client = clientSupplier.get();
                if (query.getResult() == null)
                {
                    client.displayGuiScreen(new GuiNotification(query));
                }
                else
                {
                    client.displayGuiScreen(new GuiConfirmation(query));
                }

                if (query.isSynchronous())
                {
                    while (client.currentScreen instanceof GuiNotification)
                    {
                        if (Thread.interrupted())
                        {
                            query.exception = new InterruptedException();
                            throw new RuntimeException();
                        }

                        try
                        {
                            Thread.sleep(50);
                        }
                        catch (InterruptedException ie)
                        {
                            query.exception = ie;
                        }
                    }
                }
            };
        }

        public static Consumer<StartupQuery> dedicatedServerQuery(Supplier<DedicatedServer> serverSupplier)
        {
            return (query) -> {
                DedicatedServer server = serverSupplier.get();
                if (query.getResult() == null)
                {
                    LOGGER.warn(SQ, query.getText());
                    query.finish();
                }
                else
                {
                    String text = query.getText() +
                            "\n\nRun the command /fml confirm or or /fml cancel to proceed." +
                            "\nAlternatively start the server with -Dfml.queryResult=confirm or -Dfml.queryResult=cancel to preselect the answer.";
                    LOGGER.warn(SQ, text);

                    if (!query.isSynchronous()) return; // no-op until mc does commands in another thread (if ever)

                    boolean done = false;

                    while (!done && server.isServerRunning())
                    {
                        if (Thread.interrupted())
                        {
                            query.exception = new InterruptedException();
                            throw new RuntimeException();
                        }

                        DedicatedServer dedServer = (DedicatedServer)server;

                        // rudimentary command processing, check for fml confirm/cancel and stop commands
                        synchronized (dedServer.pendingCommandList)
                        {
                            for (Iterator<PendingCommand> it = dedServer.pendingCommandList.iterator(); it.hasNext(); )
                            {
                                String cmd = it.next().command.trim().toLowerCase();

                                if (cmd.equals("/fml confirm"))
                                {
                                    LOGGER.info(SQ, "confirmed");
                                    query.setResult(true);
                                    done = true;
                                    it.remove();
                                }
                                else if (cmd.equals("/fml cancel"))
                                {
                                    LOGGER.info(SQ, "cancelled");
                                    query.setResult(false);
                                    done = true;
                                    it.remove();
                                }
                                else if (cmd.equals("/stop"))
                                {
                                    StartupQuery.abort();
                                }
                            }
                        }
                        try
                        {
                            Thread.sleep(10L);
                        }
                        catch (InterruptedException ie)
                        {
                            query.exception = ie;
                        }
                    }

                    query.finish();
                }
            };
        }
    }
}
