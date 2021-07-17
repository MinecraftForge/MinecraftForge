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

package net.minecraftforge.fmllegacy.network;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.tags.TagCollection;
import net.minecraft.tags.TagContainer;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.common.extensions.IForgeTagContainer;
import net.minecraftforge.fml.util.thread.EffectiveSide;
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
import net.minecraftforge.network.NetworkFilters;

import javax.annotation.Nullable;

public class NetworkHooks
{
    private static final Logger LOGGER = LogManager.getLogger();

    public static String getFMLVersion(final String ip)
    {
        return ip.contains("\0") ? Objects.equals(ip.split("\0")[1], FMLNetworkConstants.NETVERSION) ? FMLNetworkConstants.NETVERSION : ip.split("\0")[1] : FMLNetworkConstants.NOVERSION;
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
        return ConnectionType.forVersionFlag(channel.attr(FMLNetworkConstants.FML_NETVERSION).get());
    }

    public static Packet<?> getEntitySpawningPacket(Entity entity)
    {
        return FMLNetworkConstants.playChannel.toVanillaPacket(new FMLPlayMessages.SpawnEntity(entity), NetworkDirection.PLAY_TO_CLIENT);
    }

    public static boolean onCustomPayload(final ICustomPacket<?> packet, final Connection manager) {
        return NetworkRegistry.findTarget(packet.getName()).
                filter(ni->validateSideForProcessing(packet, ni, manager)).
                map(ni->ni.dispatch(packet.getDirection(), packet, manager)).orElse(Boolean.FALSE);
    }

    private static boolean validateSideForProcessing(final ICustomPacket<?> packet, final NetworkInstance ni, final Connection manager) {
        if (packet.getDirection().getReceptionSide() != EffectiveSide.get()) {
            manager.disconnect(new TextComponent("Illegal packet received, terminating connection"));
            return false;
        }
        return true;
    }

    public static void validatePacketDirection(final NetworkDirection packetDirection, final Optional<NetworkDirection> expectedDirection, final Connection connection) {
        if (packetDirection != expectedDirection.orElse(packetDirection)) {
            connection.disconnect(new TextComponent("Illegal packet received, terminating connection"));
            throw new IllegalStateException("Invalid packet received, aborting connection");
        }
    }
    public static void registerServerLoginChannel(Connection manager, ClientIntentionPacket packet)
    {
        manager.channel().attr(FMLNetworkConstants.FML_NETVERSION).set(packet.getFMLVersion());
        FMLHandshakeHandler.registerHandshake(manager, NetworkDirection.LOGIN_TO_CLIENT);
    }

    public synchronized static void registerClientLoginChannel(Connection manager)
    {
        manager.channel().attr(FMLNetworkConstants.FML_NETVERSION).set(FMLNetworkConstants.NOVERSION);
        FMLHandshakeHandler.registerHandshake(manager, NetworkDirection.LOGIN_TO_SERVER);
    }

    public synchronized static void sendMCRegistryPackets(Connection manager, String direction) {
        NetworkFilters.injectIfNecessary(manager);
        final Set<ResourceLocation> resourceLocations = NetworkRegistry.buildChannelVersions().keySet().stream().
                filter(rl -> !Objects.equals(rl.getNamespace(), "minecraft")).
                collect(Collectors.toSet());
        FMLMCRegisterPacketHandler.INSTANCE.addChannels(resourceLocations, manager);
        FMLMCRegisterPacketHandler.INSTANCE.sendRegistry(manager, NetworkDirection.valueOf(direction));
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
        return FMLHandshakeHandler.tickLogin(networkManager);
    }

    /**
     * Request to open a GUI on the client, from the server
     *
     * Refer to {@link net.minecraftforge.fml.ExtensionPoint#CONFIGGUIFACTORY} for how to provide a function to consume
     * these GUI requests on the client.
     *
     * The {@link IInteractionObject#getGuiID()} is treated as a {@link ResourceLocation}.
     * It should refer to a valid modId namespace, to trigger opening on the client.
     * The namespace is directly used to lookup the modId in the client side.
     *
     * @param player The player to open the GUI for
     * @param containerSupplier A supplier of container properties including the registry name of the container
     */
    public static void openGui(ServerPlayer player, MenuProvider containerSupplier)
    {
        openGui(player, containerSupplier, buf -> {});
    }

    /**
     * Request to open a GUI on the client, from the server
     *
     * Refer to {@link net.minecraftforge.fml.ExtensionPoint#CONFIGGUIFACTORY} for how to provide a function to consume
     * these GUI requests on the client.
     *
     * The {@link IInteractionObject#getGuiID()} is treated as a {@link ResourceLocation}.
     * It should refer to a valid modId namespace, to trigger opening on the client.
     * The namespace is directly used to lookup the modId in the client side.
     *
     * @param player The player to open the GUI for
     * @param containerSupplier A supplier of container properties including the registry name of the container
     * @param pos A block pos, which will be encoded into the auxillary data for this request
     */
    public static void openGui(ServerPlayer player, MenuProvider containerSupplier, BlockPos pos)
    {
        openGui(player, containerSupplier, buf -> buf.writeBlockPos(pos));
    }
    /**
     * Request to open a GUI on the client, from the server
     *
     * Refer to {@link net.minecraftforge.fml.ExtensionPoint#CONFIGGUIFACTORY} for how to provide a function to consume
     * these GUI requests on the client.
     *
     * The {@link IInteractionObject#getGuiID()} is treated as a {@link ResourceLocation}.
     * It should refer to a valid modId namespace, to trigger opening on the client.
     * The namespace is directly used to lookup the modId in the client side.
     * The maximum size for #extraDataWriter is 32600 bytes.
     *
     * @param player The player to open the GUI for
     * @param containerSupplier A supplier of container properties including the registry name of the container
     * @param extraDataWriter Consumer to write any additional data the GUI needs
     */
    public static void openGui(ServerPlayer player, MenuProvider containerSupplier, Consumer<FriendlyByteBuf> extraDataWriter)
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
        FMLPlayMessages.OpenContainer msg = new FMLPlayMessages.OpenContainer(type, openContainerId, containerSupplier.getDisplayName(), output);
        FMLNetworkConstants.playChannel.sendTo(msg, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);

        player.containerMenu = c;
        player.initMenu(player.containerMenu);
        MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, c));
    }

    /**
     * Syncs the custom tag types attached to a {@link IForgeTagContainer} to all connected players.
     * @param tagCollectionSupplier The tag collection supplier containing the custom tags
     */
    public static void syncCustomTagTypes(TagContainer tagCollectionSupplier)
    {
        Map<ResourceLocation, TagCollection<?>> customTagTypes = tagCollectionSupplier.getCustomTagTypes();
        if (!customTagTypes.isEmpty())
        {
            FMLNetworkConstants.playChannel.send(PacketDistributor.ALL.noArg(), new FMLPlayMessages.SyncCustomTagTypes(customTagTypes));
        }
    }

    /**
     * Syncs the custom tag types attached to a {@link IForgeTagContainer} to the given player.
     * @param player                The player to sync the custom tags to.
     * @param tagCollectionSupplier The tag collection supplier containing the custom tags
     */
    public static void syncCustomTagTypes(ServerPlayer player, TagContainer tagCollectionSupplier)
    {
        Map<ResourceLocation, TagCollection<?>> customTagTypes = tagCollectionSupplier.getCustomTagTypes();
        if (!customTagTypes.isEmpty())
        {
            FMLNetworkConstants.playChannel.sendTo(new FMLPlayMessages.SyncCustomTagTypes(customTagTypes), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    @Nullable
    public static FMLConnectionData getConnectionData(Connection mgr)
    {
        return mgr.channel().attr(FMLNetworkConstants.FML_CONNECTION_DATA).get();
    }
}
