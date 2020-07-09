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

package net.minecraftforge.fml;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.google.common.base.Strings;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.PendingCommand;
import net.minecraftforge.fml.client.gui.screen.ConfirmationScreen;
import net.minecraftforge.fml.client.gui.screen.NotificationScreen;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class StartupQuery {

    public static class QueryBuilder
    {
        private String header = "";
        private String text = "";
        private String action = "";

        QueryBuilder() {}

        public QueryBuilder header(String header)
        {
            this.header = Strings.nullToEmpty(header);
            return this;
        }

        public QueryBuilder text(String text)
        {
            this.text = Strings.nullToEmpty(text);
            return this;
        }

        public QueryBuilder action(String action)
        {
            this.action = Strings.nullToEmpty(action);
            return this;
        }

        public boolean confirm()
        {
            return build(new AtomicBoolean()).getResult();
        }

        public void notification()
        {
            build(null);
        }

        private StartupQuery build(AtomicBoolean result)
        {
            StartupQuery query = new StartupQuery(header, text, action, new AtomicBoolean());
            query.execute();
            return query;
        }
    }

    // internal class/functionality, do not use
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker SQ = MarkerManager.getMarker("STARTUPQUERY");

    public static QueryBuilder builder()
    {
        return new QueryBuilder();
    }

    private InterruptedException exception;

    public static void abort()
    {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) server.initiateShutdown(false);

        throw new AbortedException(); // to halt the server
    }

    public static boolean pendingQuery() {
        return pending != null;
    }
    public static void reset()
    {
        pending = null;
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
                    LOGGER.error(SQ,"An exception occurred during startup query handling", e);
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

        return true;
    }

    private void throwException() throws InterruptedException
    {
        if (exception != null) throw exception;
    }

    private static volatile StartupQuery pending;


    private StartupQuery(String header, String text, String action, @Nullable AtomicBoolean result)
    {
        this.header = header;
        this.text = text;
        this.action = action;
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

    public String getHeader()
    {
        return header;
    }

    public String getText()
    {
        return text;
    }

    public String getAction()
    {
        return action;
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

    private final String header;
    private final String text;
    private final String action;
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


    public static class QueryWrapperClient {
        public static Consumer<StartupQuery> clientQuery(Supplier<Minecraft> clientSupplier) {
            return (query) -> {
                Minecraft client = clientSupplier.get();
                if (query.getResult() == null) {
                    client.displayGuiScreen(new NotificationScreen(query));
                } else {
                    client.displayGuiScreen(new ConfirmationScreen(query));
                }

                if (query.isSynchronous()) {
                    while (client.currentScreen instanceof NotificationScreen) {
                        if (Thread.interrupted()) {
                            query.exception = new InterruptedException();
                            throw new RuntimeException();
                        }

                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException ie) {
                            query.exception = ie;
                        }
                    }
                }
            };
        }
    }

    public static class QueryWrapperServer {
        public static Consumer<StartupQuery> dedicatedServerQuery(Supplier<DedicatedServer> serverSupplier)
        {
            return (query) -> {
                DedicatedServer server = serverSupplier.get();
                if (query.getResult() == null)
                {
                    if (!query.getHeader().isEmpty())
                    {
                        LOGGER.warn(SQ, "\n" + query.getHeader() + "\n");
                    }
                    LOGGER.warn(SQ, "\n" + query.getText());
                    if (!query.getAction().isEmpty())
                    {
                        LOGGER.warn(SQ, "\n\n" + query.getAction());
                    }
                    query.finish();
                }
                else
                {
                    StringBuilder text = new StringBuilder("\n");
                    if (!query.getHeader().isEmpty()) {
                        text.append(query.getHeader()).append("\n\n");
                    }
                    text.append(query.getText()).append("\n");
                    if (!query.getAction().isEmpty()) {
                        text.append("\n").append(query.getAction());
                    }
                    text.append("\nConfirm with '/fml confirm' or cancel with '/fml cancel'.")
                        .append("\nAlternatively start the server with -Dfml.queryResult=confirm or -Dfml.queryResult=cancel to preselect the answer.");
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
                                cmd = cmd.charAt(0) == '/' ? cmd.substring(1) : cmd; // strip the forward slash to make it optional

                                if (cmd.equals("fml confirm"))
                                {
                                    LOGGER.info(SQ, "confirmed");
                                    query.setResult(true);
                                    done = true;
                                    it.remove();
                                }
                                else if (cmd.equals("fml cancel"))
                                {
                                    LOGGER.info(SQ, "cancelled");
                                    query.setResult(false);
                                    done = true;
                                    it.remove();
                                }
                                else if (cmd.equals("stop"))
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
