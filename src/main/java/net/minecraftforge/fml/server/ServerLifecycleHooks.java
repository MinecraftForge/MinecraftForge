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

package net.minecraftforge.fml.server;

import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.CPacketHandshake;
import net.minecraft.network.login.server.SPacketDisconnectLogin;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

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
        return !MinecraftForge.EVENT_BUS.post(new FMLServerAboutToStartEvent(server));
    }

    public static boolean handleServerStarting(final MinecraftServer server)
    {
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

    public static boolean handleServerLogin(final CPacketHandshake packet, final NetworkManager manager) {
        if (!allowLogins.get())
        {
            TextComponentString text = new TextComponentString("Server is still starting! Please wait before reconnecting.");
            LOGGER.info(SERVERHOOKS,"Disconnecting Player (server is still starting): {}", text.getUnformattedComponentText());
            manager.sendPacket(new SPacketDisconnectLogin(text));
            manager.closeChannel(text);
            return false;
        }

        if (packet.getRequestedState() == EnumConnectionState.LOGIN && !NetworkHooks.accepts(packet))
        {
            manager.setConnectionState(EnumConnectionState.LOGIN);
            TextComponentString text = new TextComponentString("This server has mods that require Forge to be installed on the client. Contact your server admin for more details.");
            List<String> modNames = net.minecraftforge.fml.network.NetworkRegistry.getNonVanillaNetworkMods();
            LOGGER.info(SERVERHOOKS,"Disconnecting vanilla connection attempt. Required mods {}", modNames);
            manager.sendPacket(new SPacketDisconnectLogin(text));
            manager.closeChannel(text);
            return false;
        }

        NetworkHooks.registerServerLoginChannel(manager, packet);
        return true;

    }

    public void handleExit(int retVal)
    {
/*
        CountDownLatch latch = exitLatch;

        if (latch != null)
        {
            try
            {
                FMLLog.log.info("Waiting for the server to terminate/save.");
                if (!latch.await(10, TimeUnit.SECONDS))
                {
                    FMLLog.log.warn("The server didn't stop within 10 seconds, exiting anyway.");
                }
                else
                {
                    FMLLog.log.info("Server terminated.");
                }
            }
            catch (InterruptedException e)
            {
                FMLLog.log.warn("Interrupted wait, exiting.");
            }
        }

*/
        System.exit(retVal);
    }

}
