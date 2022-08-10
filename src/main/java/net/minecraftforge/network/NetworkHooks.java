/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.network.ConnectionData.ModMismatchData;
import net.minecraftforge.network.filters.NetworkFilters;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.buffer.Unpooled;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.MenuProvider;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.config.ConfigTracker;
import org.jetbrains.annotations.Nullable;

public class NetworkHooks
{
    private static final Logger LOGGER = LogManager.getLogger();

    public static String getFMLVersion(final String ip)
    {
        return ip.contains("\0") ? Objects.equals(ip.split("\0")[1], NetworkConstants.NETVERSION) ? NetworkConstants.NETVERSION : ip.split("\0")[1] : NetworkConstants.NOVERSION;
    }

    public static ConnectionType getConnectionType(final Supplier<Connection> connection)
    {
        return getConnectionType(connection.get().channel());
    }

    public static ConnectionType getConnectionType(ChannelHandlerContext context)
    {
        return getConnectionType(context.channel());
    }

    private static ConnectionType getConnectionType(Channel channel)
    {
        return ConnectionType.forVersionFlag(channel.attr(NetworkConstants.FML_NETVERSION).get());
    }

    public static Packet<?> getEntitySpawningPacket(Entity entity)
    {
        return NetworkConstants.playChannel.toVanillaPacket(new PlayMessages.SpawnEntity(entity), NetworkDirection.PLAY_TO_CLIENT);
    }

    public static boolean onCustomPayload(final ICustomPacket<?> packet, final Connection manager) {
        return NetworkRegistry.findTarget(packet.getName()).
                filter(ni->validateSideForProcessing(packet, ni, manager)).
                map(ni->ni.dispatch(packet.getDirection(), packet, manager)).orElse(Boolean.FALSE);
    }

    private static boolean validateSideForProcessing(final ICustomPacket<?> packet, final NetworkInstance ni, final Connection manager) {
        if (packet.getDirection().getReceptionSide() != EffectiveSide.get()) {
            manager.disconnect(Component.literal("Illegal packet received, terminating connection"));
            return false;
        }
        return true;
    }

    public static void validatePacketDirection(final NetworkDirection packetDirection, final Optional<NetworkDirection> expectedDirection, final Connection connection) {
        if (packetDirection != expectedDirection.orElse(packetDirection)) {
            connection.disconnect(Component.literal("Illegal packet received, terminating connection"));
            throw new IllegalStateException("Invalid packet received, aborting connection");
        }
    }
    public static void registerServerLoginChannel(Connection manager, ClientIntentionPacket packet)
    {
        manager.channel().attr(NetworkConstants.FML_NETVERSION).set(packet.getFMLVersion());
        HandshakeHandler.registerHandshake(manager, NetworkDirection.LOGIN_TO_CLIENT);
    }

    public synchronized static void registerClientLoginChannel(Connection manager)
    {
        manager.channel().attr(NetworkConstants.FML_NETVERSION).set(NetworkConstants.NOVERSION);
        HandshakeHandler.registerHandshake(manager, NetworkDirection.LOGIN_TO_SERVER);
    }

    public synchronized static void sendMCRegistryPackets(Connection manager, String direction) {
        NetworkFilters.injectIfNecessary(manager);
        final Set<ResourceLocation> resourceLocations = NetworkRegistry.buildChannelVersions().keySet().stream().
                filter(rl -> !Objects.equals(rl.getNamespace(), "minecraft")).
                collect(Collectors.toSet());
        MCRegisterPacketHandler.INSTANCE.addChannels(resourceLocations, manager);
        MCRegisterPacketHandler.INSTANCE.sendRegistry(manager, NetworkDirection.valueOf(direction));
    }

    //TODO Dimensions..
/*    public synchronized static void sendDimensionDataPacket(NetworkManager manager, ServerPlayerEntity player) {
        // don't send vanilla dims
        if (player.dimension.isVanilla()) return;
        // don't sent to local - we already have a valid dim registry locally
        if (manager.isLocalChannel()) return;
        FMLNetworkConstants.playChannel.sendTo(new FMLPlayMessages.DimensionInfoMessage(player.dimension), manager, NetworkDirection.PLAY_TO_CLIENT);
    }*/

    public static boolean isVanillaConnection(Connection manager)
    {
        if (manager == null || manager.channel() == null) throw new NullPointerException("ARGH! Network Manager is null (" + manager != null ? "CHANNEL" : "MANAGER"+")" );
        return getConnectionType(() -> manager) == ConnectionType.VANILLA;
    }

    public static void handleClientLoginSuccess(Connection manager) {
        if (isVanillaConnection(manager)) {
            LOGGER.info("Connected to a vanilla server. Catching up missing behaviour.");
            ConfigTracker.INSTANCE.loadDefaultServerConfigs();
        } else {
            LOGGER.info("Connected to a modded server.");
        }
    }

    public static boolean tickNegotiation(ServerLoginPacketListenerImpl netHandlerLoginServer, Connection networkManager, ServerPlayer player)
    {
        return HandshakeHandler.tickLogin(networkManager);
    }

    /**
     * Request to open a GUI on the client, from the server
     *
     * Refer to {@link ConfigScreenHandler.ConfigScreenFactory} for how to provide a function to consume
     * these GUI requests on the client.
     *
     * @param player The player to open the GUI for
     * @param containerSupplier A supplier of container properties including the registry name of the container
     */
    public static void openScreen(ServerPlayer player, MenuProvider containerSupplier)
    {
        openScreen(player, containerSupplier, buf -> {});
    }

    /**
     * Request to open a GUI on the client, from the server
     *
     * Refer to {@link ConfigScreenHandler.ConfigScreenFactory} for how to provide a function to consume
     * these GUI requests on the client.
     *
     * @param player The player to open the GUI for
     * @param containerSupplier A supplier of container properties including the registry name of the container
     * @param pos A block pos, which will be encoded into the auxillary data for this request
     */
    public static void openScreen(ServerPlayer player, MenuProvider containerSupplier, BlockPos pos)
    {
        openScreen(player, containerSupplier, buf -> buf.writeBlockPos(pos));
    }
    /**
     * Request to open a GUI on the client, from the server
     *
     * Refer to {@link ConfigScreenHandler.ConfigScreenFactory} for how to provide a function to consume
     * these GUI requests on the client.
     *
     * The maximum size for #extraDataWriter is 32600 bytes.
     *
     * @param player The player to open the GUI for
     * @param containerSupplier A supplier of container properties including the registry name of the container
     * @param extraDataWriter Consumer to write any additional data the GUI needs
     */
    public static void openScreen(ServerPlayer player, MenuProvider containerSupplier, Consumer<FriendlyByteBuf> extraDataWriter)
    {
        if (player.level.isClientSide) return;
        player.doCloseContainer();
        player.nextContainerCounter();
        int openContainerId = player.containerCounter;
        FriendlyByteBuf extraData = new FriendlyByteBuf(Unpooled.buffer());
        extraDataWriter.accept(extraData);
        extraData.readerIndex(0); // reset to beginning in case modders read for whatever reason

        FriendlyByteBuf output = new FriendlyByteBuf(Unpooled.buffer());
        output.writeVarInt(extraData.readableBytes());
        output.writeBytes(extraData);

        if (output.readableBytes() > 32600 || output.readableBytes() < 1) {
            throw new IllegalArgumentException("Invalid PacketBuffer for openGui, found "+ output.readableBytes()+ " bytes");
        }
        AbstractContainerMenu c = containerSupplier.createMenu(openContainerId, player.getInventory(), player);
        MenuType<?> type = c.getType();
        PlayMessages.OpenContainer msg = new PlayMessages.OpenContainer(type, openContainerId, containerSupplier.getDisplayName(), output);
        NetworkConstants.playChannel.sendTo(msg, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);

        player.containerMenu = c;
        player.initMenu(player.containerMenu);
        MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, c));
    }

    /**
     * Updates the current ConnectionData instance with new mod or channel data if the old instance did not have either of these yet,
     * or creates a new ConnectionData instance with the new data if the current ConnectionData instance doesn't exist yet.
     */
    static void appendConnectionData(Connection mgr, Map<String, Pair<String, String>> modData, Map<ResourceLocation, String> channels)
    {
        ConnectionData oldData = mgr.channel().attr(NetworkConstants.FML_CONNECTION_DATA).get();

        oldData = oldData != null ? new ConnectionData(oldData.getModData().isEmpty() ? modData : oldData.getModData(), oldData.getChannels().isEmpty() ? channels : oldData.getChannels()) : new ConnectionData(modData, channels);
        mgr.channel().attr(NetworkConstants.FML_CONNECTION_DATA).set(oldData);
    }

    @Nullable
    public static ConnectionData getConnectionData(Connection mgr)
    {
        return mgr.channel().attr(NetworkConstants.FML_CONNECTION_DATA).get();
    }

    @Nullable
    public static ModMismatchData getModMismatchData(Connection mgr)
    {
        return mgr.channel().attr(NetworkConstants.FML_MOD_MISMATCH_DATA).get();
    }

    @Nullable
    public static MCRegisterPacketHandler.ChannelList getChannelList(Connection mgr)
    {
        return mgr.channel().attr(NetworkConstants.FML_MC_REGISTRY).get();
    }
}
