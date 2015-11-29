/*
 * The FML Forge Mod Loader suite. Copyright (C) 2012 cpw
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package net.minecraftforge.fml.server;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.command.ServerCommand;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.storage.SaveFormatOld;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.IFMLSidedHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.StartupQuery;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.functions.GenericIterableFactory;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.registry.LanguageRegistry;
import net.minecraftforge.fml.relauncher.Side;

import com.google.common.collect.ImmutableList;

/**
 * Handles primary communication from hooked code into the system
 *
 * The FML entry point is {@link #beginServerLoading(MinecraftServer)} called from
 * {@link net.minecraft.server.dedicated.DedicatedServer}
 *
 * Obfuscated code should focus on this class and other members of the "server"
 * (or "client") code
 *
 * The actual mod loading is handled at arms length by {@link Loader}
 *
 * It is expected that a similar class will exist for each target environment:
 * Bukkit and Client side.
 *
 * It should not be directly modified.
 *
 * @author cpw
 *
 */
public class FMLServerHandler implements IFMLSidedHandler
{
    /**
     * The singleton
     */
    private static final FMLServerHandler INSTANCE = new FMLServerHandler();

    /**
     * A reference to the server itself
     */
    private MinecraftServer server;

    private FMLServerHandler()
    {
        FMLCommonHandler.instance().beginLoading(this);
    }
    /**
     * Called to start the whole game off from
     * {@link MinecraftServer#startServer}
     *
     * @param minecraftServer
     */
    @Override
    public void beginServerLoading(MinecraftServer minecraftServer)
    {
        server = minecraftServer;
        Loader.instance().loadMods();
        Loader.instance().preinitializeMods();
    }

    /**
     * Called a bit later on during server initialization to finish loading mods
     */
    @Override
    public void finishServerLoading()
    {
        Loader.instance().initializeMods();
    }

    @Override
    public void haltGame(String message, Throwable exception)
    {
        throw new RuntimeException(message, exception);
    }

    @Override
    public File getSavesDirectory()
    {
        return ((SaveFormatOld) server.getActiveAnvilConverter()).savesDirectory;
    }

    /**
     * Get the server instance
     */
    @Override
    public MinecraftServer getServer()
    {
        return server;
    }

    /**
     * @return the instance
     */
    public static FMLServerHandler instance()
    {
        return INSTANCE;
    }

    /* (non-Javadoc)
     * @see net.minecraftforge.fml.common.IFMLSidedHandler#getAdditionalBrandingInformation()
     */
    @Override
    public List<String> getAdditionalBrandingInformation()
    {
        return ImmutableList.<String>of();
    }

    /* (non-Javadoc)
     * @see net.minecraftforge.fml.common.IFMLSidedHandler#getSide()
     */
    @Override
    public Side getSide()
    {
        return Side.SERVER;
    }

    @Override
    public void showGuiScreen(Object clientGuiElement)
    {

    }

    @Override
    public void queryUser(StartupQuery query) throws InterruptedException
    {
        if (query.getResult() == null)
        {
            FMLLog.warning("%s", query.getText());
            query.finish();
        }
        else
        {
            String text = query.getText() +
                    "\n\nRun the command /fml confirm or or /fml cancel to proceed." +
                    "\nAlternatively start the server with -Dfml.queryResult=confirm or -Dfml.queryResult=cancel to preselect the answer.";
            FMLLog.warning("%s", text);

            if (!query.isSynchronous()) return; // no-op until mc does commands in another thread (if ever)

            boolean done = false;

            while (!done && server.isServerRunning())
            {
                if (Thread.interrupted()) throw new InterruptedException();

                DedicatedServer dedServer = (DedicatedServer) server;

                // rudimentary command processing, check for fml confirm/cancel and stop commands
                synchronized (dedServer.pendingCommandList)
                {
                    for (Iterator<ServerCommand> it = GenericIterableFactory.newCastingIterable(dedServer.pendingCommandList, ServerCommand.class).iterator(); it.hasNext(); )
                    {
                        String cmd = it.next().command.trim().toLowerCase();

                        if (cmd.equals("/fml confirm"))
                        {
                            FMLLog.info("confirmed");
                            query.setResult(true);
                            done = true;
                            it.remove();
                        }
                        else if (cmd.equals("/fml cancel"))
                        {
                            FMLLog.info("cancelled");
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

                Thread.sleep(10L);
            }

            query.finish();
        }
    }

    @Override
    public boolean shouldServerShouldBeKilledQuietly()
    {
        return false;
    }
    @Override
    public void addModAsResource(ModContainer container)
    {
        LanguageRegistry.instance().loadLanguagesFor(container, Side.SERVER);
    }

    @Override
    public String getCurrentLanguage()
    {
        return "en_US";
    }

    @Override
    public void serverStopped()
    {
        // NOOP
    }
    @Override
    public NetworkManager getClientToServerNetworkManager()
    {
        throw new RuntimeException("Missing");
    }
    @Override
    public INetHandler getClientPlayHandler()
    {
        return null;
    }

    @Override
    public void fireNetRegistrationEvent(EventBus bus, NetworkManager manager, Set<String> channelSet, String channel, Side side)
    {
        bus.post(new FMLNetworkEvent.CustomPacketRegistrationEvent<NetHandlerPlayServer>(manager, channelSet, channel, side, NetHandlerPlayServer.class));
    }

    @Override
    public boolean shouldAllowPlayerLogins()
    {
        return DedicatedServer.allowPlayerLogins;
    }

    @Override
    public void allowLogins() {
        DedicatedServer.allowPlayerLogins = true;
    }

    @Override
    public IThreadListener getWorldThread(INetHandler net)
    {
        // Always the server on the dedicated server, eventually add Per-World if Mojang adds world stuff.
        return getServer();
    }

    @Override
    public void processWindowMessages()
    {
        // NOOP
    }

    @Override
    public String stripSpecialChars(String message)
    {
        return message;
    }
}
