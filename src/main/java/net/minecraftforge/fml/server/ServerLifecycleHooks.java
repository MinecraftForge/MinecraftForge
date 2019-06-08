/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.fml.server;

import net.minecraft.network.ProtocolType;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.CHandshakePacket;
import net.minecraft.network.login.server.SDisconnectLoginPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.loading.FileUtils;
import net.minecraftforge.fml.network.ConnectionType;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.packs.ResourcePackLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerLifecycleHooks
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker SERVERHOOKS = MarkerManager.getMarker("SERVERHOOKS");
    private static volatile CountDownLatch exitLatch = null;
    private static MinecraftServer currentServer;

    public static boolean handleServerAboutToStart(final MinecraftServer server)
    {
        currentServer = server;
        LogicalSidedProvider.setServer(()->server);
        final Path serverConfig = server.getActiveAnvilConverter().getFile(server.getFolderName(), "serverconfig").toPath();
        FileUtils.getOrCreateDirectory(serverConfig, "serverconfig");
        ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.SERVER, serverConfig);
        ResourcePackLoader.loadResourcePacks(currentServer.getResourcePacks());
        return !MinecraftForge.EVENT_BUS.post(new FMLServerAboutToStartEvent(server));
    }

    public static boolean handleServerStarting(final MinecraftServer server)
    {
        DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, ()->()->LanguageHook.loadLanguagesOnServer(server));
        return !MinecraftForge.EVENT_BUS.post(new FMLServerStartingEvent(server));
    }

    public static void handleServerStarted(final MinecraftServer server)
    {
        MinecraftForge.EVENT_BUS.post(new FMLServerStartedEvent(server));
        allowLogins.set(true);
    }

    public static void handleServerStopping(final MinecraftServer server)
    {
        allowLogins.set(false);
        MinecraftForge.EVENT_BUS.post(new FMLServerStoppingEvent(server));
    }

    public static void expectServerStopped()
    {
        exitLatch = new CountDownLatch(1);
    }

    public static void handleServerStopped(final MinecraftServer server)
    {
        MinecraftForge.EVENT_BUS.post(new FMLServerStoppedEvent(server));
        currentServer = null;
        LogicalSidedProvider.setServer(null);
        CountDownLatch latch = exitLatch;

        if (latch != null)
        {
            latch.countDown();
            exitLatch = null;
        }
    }

    public static MinecraftServer getCurrentServer()
    {
        return currentServer;
    }
    private static AtomicBoolean allowLogins = new AtomicBoolean(false);

    public static boolean handleServerLogin(final CHandshakePacket packet, final NetworkManager manager) {
        if (!allowLogins.get())
        {
            StringTextComponent text = new StringTextComponent("Server is still starting! Please wait before reconnecting.");
            LOGGER.info(SERVERHOOKS,"Disconnecting Player (server is still starting): {}", text.getUnformattedComponentText());
            manager.sendPacket(new SDisconnectLoginPacket(text));
            manager.closeChannel(text);
            return false;
        }

        if (packet.getRequestedState() == ProtocolType.LOGIN) {
            final ConnectionType connectionType = ConnectionType.forVersionFlag(packet.getFMLVersion());
            final int versionNumber = connectionType.getFMLVersionNumber(packet.getFMLVersion());

            if (connectionType == ConnectionType.MODDED && versionNumber != FMLNetworkConstants.FMLNETVERSION) {
                rejectConnection(manager, connectionType, "This modded server is not network compatible with your modded client. Please verify your Forge version closely matches the server. Got net version "+ versionNumber + " this server is net version "+FMLNetworkConstants.FMLNETVERSION);
                return false;
            }

            if (connectionType == ConnectionType.VANILLA && !NetworkRegistry.acceptsVanillaClientConnections()) {
                rejectConnection(manager, connectionType, "This server has mods that require Forge to be installed on the client. Contact your server admin for more details.");
                return false;
            }
        }

        if (packet.getRequestedState() == ProtocolType.STATUS) return true;

        NetworkHooks.registerServerLoginChannel(manager, packet);
        return true;

    }

    private static void rejectConnection(final NetworkManager manager, ConnectionType type, String message) {
        manager.setConnectionState(ProtocolType.LOGIN);
        LOGGER.info(SERVERHOOKS, "Disconnecting {} connection attempt: {}", type, message);
        StringTextComponent text = new StringTextComponent(message);
        manager.sendPacket(new SDisconnectLoginPacket(text));
        manager.closeChannel(text);
    }

    public static void handleExit(int retVal)
    {
/*
        CountDownLatch latch = exitLatch;

        if (latch != null)
        {
            try
            {
                LOGGER.info("Waiting for the server to terminate/save.");
                if (!latch.await(10, TimeUnit.SECONDS))
                {
                    LOGGER.warn("The server didn't stop within 10 seconds, exiting anyway.");
                }
                else
                {
                    LOGGER.info("Server terminated.");
                }
            }
            catch (InterruptedException e)
            {
                LOGGER.warn("Interrupted wait, exiting.");
            }
        }

*/
        System.exit(retVal);
    }

}
