/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.server;

import static net.minecraftforge.fml.Logging.CORE;

import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.ModLoadingWarning;
import net.minecraftforge.network.ConnectionType;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.network.ServerStatusPing;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.resource.PathResourcePack;
import net.minecraftforge.server.permission.PermissionAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.fml.loading.FileUtils;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.registries.GameData;

public class ServerLifecycleHooks
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker SERVERHOOKS = MarkerManager.getMarker("SERVERHOOKS");
    private static final LevelResource SERVERCONFIG = new LevelResource("serverconfig");
    private static volatile CountDownLatch exitLatch = null;
    private static MinecraftServer currentServer;

    private static Path getServerConfigPath(final MinecraftServer server)
    {
        final Path serverConfig = server.getWorldPath(SERVERCONFIG);
        FileUtils.getOrCreateDirectory(serverConfig, "serverconfig");
        return serverConfig;
    }

    public static boolean handleServerAboutToStart(final MinecraftServer server)
    {
        currentServer = server;
        currentServer.getStatus().setForgeData(new ServerStatusPing()); //gathers NetworkRegistry data
        // on the dedi server we need to force the stuff to setup properly
        LogicalSidedProvider.setServer(()->server);
        ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.SERVER, getServerConfigPath(server));
        return !MinecraftForge.EVENT_BUS.post(new ServerAboutToStartEvent(server));
    }

    public static boolean handleServerStarting(final MinecraftServer server)
    {
        DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, ()->()->LanguageHook.loadLanguagesOnServer(server));
        PermissionAPI.initializePermissionAPI();
        return !MinecraftForge.EVENT_BUS.post(new ServerStartingEvent(server));
    }

    public static void handleServerStarted(final MinecraftServer server)
    {
        MinecraftForge.EVENT_BUS.post(new ServerStartedEvent(server));
        allowLogins.set(true);
    }

    public static void handleServerStopping(final MinecraftServer server)
    {
        allowLogins.set(false);
        PermissionAPI.resetPermissionAPI();
        MinecraftForge.EVENT_BUS.post(new ServerStoppingEvent(server));
    }

    public static void expectServerStopped()
    {
        exitLatch = new CountDownLatch(1);
    }

    public static void handleServerStopped(final MinecraftServer server)
    {
        if (!server.isDedicatedServer()) GameData.revertToFrozen();
        MinecraftForge.EVENT_BUS.post(new ServerStoppedEvent(server));
        currentServer = null;
        LogicalSidedProvider.setServer(null);
        CountDownLatch latch = exitLatch;

        if (latch != null)
        {
            latch.countDown();
            exitLatch = null;
        }
        ConfigTracker.INSTANCE.unloadConfigs(ModConfig.Type.SERVER, getServerConfigPath(server));
    }

    public static MinecraftServer getCurrentServer()
    {
        return currentServer;
    }
    private static AtomicBoolean allowLogins = new AtomicBoolean(false);

    public static boolean handleServerLogin(final ClientIntentionPacket packet, final Connection manager) {
        if (!allowLogins.get())
        {
            TextComponent text = new TextComponent("Server is still starting! Please wait before reconnecting.");
            LOGGER.info(SERVERHOOKS,"Disconnecting Player (server is still starting): {}", text.getContents());
            manager.send(new ClientboundLoginDisconnectPacket(text));
            manager.disconnect(text);
            return false;
        }

        if (packet.getIntention() == ConnectionProtocol.LOGIN) {
            final ConnectionType connectionType = ConnectionType.forVersionFlag(packet.getFMLVersion());
            final int versionNumber = connectionType.getFMLVersionNumber(packet.getFMLVersion());

            if (connectionType == ConnectionType.MODDED && versionNumber != NetworkConstants.FMLNETVERSION) {
                rejectConnection(manager, connectionType, "This modded server is not impl compatible with your modded client. Please verify your Forge version closely matches the server. Got net version "+ versionNumber + " this server is net version "+ NetworkConstants.FMLNETVERSION);
                return false;
            }

            if (connectionType == ConnectionType.VANILLA && !NetworkRegistry.acceptsVanillaClientConnections()) {
                rejectConnection(manager, connectionType, "This server has mods that require Forge to be installed on the client. Contact your server admin for more details.");
                return false;
            }
        }

        if (packet.getIntention() == ConnectionProtocol.STATUS) return true;

        NetworkHooks.registerServerLoginChannel(manager, packet);
        return true;

    }

    private static void rejectConnection(final Connection manager, ConnectionType type, String message) {
        manager.setProtocol(ConnectionProtocol.LOGIN);
        LOGGER.info(SERVERHOOKS, "Disconnecting {} connection attempt: {}", type, message);
        TextComponent text = new TextComponent(message);
        manager.send(new ClientboundLoginDisconnectPacket(text));
        manager.disconnect(text);
    }

    public static void handleExit(int retVal)
    {
        System.exit(retVal);
    }

    //INTERNAL MODDERS DO NOT USE
    public static RepositorySource buildPackFinder(Map<IModFile, ? extends PathResourcePack> modResourcePacks) {
        return (packList, factory) -> serverPackFinder(modResourcePacks, packList, factory);
    }

    private static void serverPackFinder(Map<IModFile, ? extends PathResourcePack> modResourcePacks, Consumer<Pack> consumer, Pack.PackConstructor factory) {
        for (Entry<IModFile, ? extends PathResourcePack> e : modResourcePacks.entrySet())
        {
            IModInfo mod = e.getKey().getModInfos().get(0);
            if (Objects.equals(mod.getModId(), "minecraft")) continue; // skip the minecraft "mod"
            final String name = "mod:" + mod.getModId();
            final Pack packInfo = Pack.create(name, false, e::getValue, factory, Pack.Position.BOTTOM, PackSource.DEFAULT);
            if (packInfo == null) {
                // Vanilla only logs an error, instead of propagating, so handle null and warn that something went wrong
                ModLoader.get().addWarning(new ModLoadingWarning(mod, ModLoadingStage.ERROR, "fml.modloading.brokenresources", e.getKey()));
                continue;
            }
            LOGGER.debug(CORE, "Generating PackInfo named {} for mod file {}", name, e.getKey().getFilePath());
            consumer.accept(packInfo);
        }
    }
}
