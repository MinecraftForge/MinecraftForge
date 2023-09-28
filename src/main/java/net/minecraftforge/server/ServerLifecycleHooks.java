/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestServer;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.handshake.ClientIntent;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.common.world.StructureModifier;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.Logging;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.ModLoadingWarning;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.network.ConnectionType;
import net.minecraftforge.network.NetworkContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import net.minecraftforge.resource.PathPackResources;
import net.minecraftforge.server.permission.PermissionAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.Pack.Info;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.GameData;
import org.jetbrains.annotations.ApiStatus;

@SuppressWarnings("deprecation")
@ApiStatus.Internal
public class ServerLifecycleHooks {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker SERVERHOOKS = MarkerManager.getMarker("SERVERHOOKS");
    private static final LevelResource SERVERCONFIG = new LevelResource("serverconfig");
    private static final AtomicBoolean allowLogins = new AtomicBoolean(false);
    private static volatile CountDownLatch exitLatch = null;
    private static MinecraftServer currentServer;

    private static Path getServerConfigPath(final MinecraftServer server) {
        final Path serverConfig = server.getWorldPath(SERVERCONFIG);
        if (!Files.isDirectory(serverConfig)) {
            try {
                Files.createDirectories(serverConfig);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return serverConfig;
    }

    public static boolean handleServerAboutToStart(final MinecraftServer server) {
        currentServer = server;
        // on the dedi server we need to force the stuff to setup properly
        LogicalSidedProvider.setServer(()->server);
        ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.SERVER, getServerConfigPath(server));
        runModifiers(server);
        return !MinecraftForge.EVENT_BUS.post(new ServerAboutToStartEvent(server));
    }

    public static boolean handleServerStarting(final MinecraftServer server) {
        DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, ()->()->{
            LanguageHook.loadLanguagesOnServer(server);
            // GameTestServer requires the gametests to be registered earlier, so it is done in main and should not be done twice.
            if (!(server instanceof GameTestServer))
                net.minecraftforge.gametest.ForgeGameTestHooks.registerGametests();
        });
        PermissionAPI.initializePermissionAPI();
        return !MinecraftForge.EVENT_BUS.post(new ServerStartingEvent(server));
    }


    public static void expectServerStopped() {
        exitLatch = new CountDownLatch(1);
    }

    public static void handleServerStopped(final MinecraftServer server) {
        if (!server.isDedicatedServer()) GameData.revertToFrozen();
        MinecraftForge.EVENT_BUS.post(new ServerStoppedEvent(server));
        currentServer = null;
        LogicalSidedProvider.setServer(null);
        CountDownLatch latch = exitLatch;

        if (latch != null) {
            latch.countDown();
            exitLatch = null;
        }
        ConfigTracker.INSTANCE.unloadConfigs(ModConfig.Type.SERVER, getServerConfigPath(server));
    }

    public static MinecraftServer getCurrentServer() {
        return currentServer;
    }

    @ApiStatus.Internal
    public static RepositorySource buildPackFinder(Map<IModFile, ? extends PathPackResources> modResourcePacks) {
        return packAcceptor -> serverPackFinder(modResourcePacks, packAcceptor);
    }

    private static void serverPackFinder(Map<IModFile, ? extends PathPackResources> modResourcePacks, Consumer<Pack> packAcceptor) {
        for (Entry<IModFile, ? extends PathPackResources> e : modResourcePacks.entrySet()) {
            IModInfo mod = e.getKey().getModInfos().get(0);
            if (Objects.equals(mod.getModId(), "minecraft")) continue; // skip the minecraft "mod"
            final String name = "mod:" + mod.getModId();

            var supplier = new Pack.ResourcesSupplier() {
                @Override
                public PackResources openPrimary(String path) {
                    return e.getValue();
                }

                @Override
                public PackResources openFull(String path, Info info) {
                    return e.getValue(); // TODO: composite
                }

            };

            final Pack modPack = Pack.readMetaAndCreate(name, Component.literal(e.getValue().packId()), false, supplier, PackType.SERVER_DATA, Pack.Position.BOTTOM, PackSource.DEFAULT);
            if (modPack == null) {
                // Vanilla only logs an error, instead of propagating, so handle null and warn that something went wrong
                ModLoader.get().addWarning(new ModLoadingWarning(mod, ModLoadingStage.ERROR, "fml.modloading.brokenresources", e.getKey()));
                continue;
            }
            LOGGER.debug(Logging.CORE, "Generating PackInfo named {} for mod file {}", name, e.getKey().getFilePath());
            packAcceptor.accept(modPack);
        }
    }

    private static void runModifiers(final MinecraftServer server) {
        final RegistryAccess registries = server.registryAccess();

        // The order of holders() is the order modifiers were loaded in.
        final List<BiomeModifier> biomeModifiers = registries.registryOrThrow(ForgeRegistries.Keys.BIOME_MODIFIERS)
            .holders()
            .map(Holder::value)
            .toList();
        final List<StructureModifier> structureModifiers = registries.registryOrThrow(Keys.STRUCTURE_MODIFIERS)
              .holders()
              .map(Holder::value)
              .toList();

        // Apply sorted biome modifiers to each biome.
        registries.registryOrThrow(Registries.BIOME).holders().forEach(biomeHolder ->
            biomeHolder.value().modifiableBiomeInfo().applyBiomeModifiers(biomeHolder, biomeModifiers)
        );
        // Apply sorted structure modifiers to each structure.
        registries.registryOrThrow(Registries.STRUCTURE).holders().forEach(structureHolder ->
            structureHolder.value().modifiableStructureInfo().applyStructureModifiers(structureHolder, structureModifiers)
        );
    }

    //==================================================================================================================================================================================
    //==================================================================================================================================================================================
    //==================================================================================================================================================================================
    //==================================================================================================================================================================================
    //==================================================================================================================================================================================
    //==================================================================================================================================================================================

    public static void handleServerStarted(final MinecraftServer server) {
        MinecraftForge.EVENT_BUS.post(new ServerStartedEvent(server));
        allowLogins.set(true);
    }

    public static void handleServerStopping(final MinecraftServer server) {
        allowLogins.set(false);
        MinecraftForge.EVENT_BUS.post(new ServerStoppingEvent(server));
    }

    public static boolean handleServerLogin(final ClientIntentionPacket packet, final Connection connection) {
        var ctx = NetworkContext.get(connection);
        ctx.processIntention(packet.hostName());

        if (!allowLogins.get())
            return rejectConnection(connection, ctx.getType(), "Server is still starting! Please wait before reconnecting.");

        if (packet.nextProtocol() != ConnectionProtocol.LOGIN)
            return true;

        if (ctx.getType() == ConnectionType.MODDED && ctx.getNetVersion() != NetworkContext.NET_VERSION)
            return rejectConnection(connection, ctx.getType(), "This modded server is not impl compatible with your modded client. Please verify your Forge version closely matches the server. Got net version " + ctx.getNetVersion() + " this server is net version " + NetworkContext.NET_VERSION);

        if (ctx.getType() == ConnectionType.VANILLA && !NetworkRegistry.acceptsVanillaClientConnections())
            return rejectConnection(connection, ctx.getType(), "This server has mods that require Forge to be installed on the client. Contact your server admin for more details.");

        NetworkRegistry.onConnectionStart(connection);
        return true;
    }

    private static boolean rejectConnection(final Connection connection, ConnectionType type, String message) {
        connection.setClientboundProtocolAfterHandshake(ClientIntent.LOGIN);
        LOGGER.info(SERVERHOOKS, "[{}] Disconnecting {} connection attempt: {}", connection.getLoggableAddress(true), type, message); // TODO: Respect logIP setting
        var text = Component.literal(message);
        connection.send(new ClientboundLoginDisconnectPacket(text));
        connection.disconnect(text);
        return false;
    }
}
