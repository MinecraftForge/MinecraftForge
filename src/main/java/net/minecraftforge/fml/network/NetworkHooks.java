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

package net.minecraftforge.fml.network;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.handshake.client.CPacketHandshake;
import net.minecraft.network.NetHandlerLoginServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IInteractionObject;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.config.ConfigTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NetworkHooks
{
    private static final Logger LOGGER = LogManager.getLogger();

    public static String getFMLVersion(final String ip)
    {
        return ip.contains("\0") ? Objects.equals(ip.split("\0")[1], FMLNetworkConstants.NETVERSION) ? FMLNetworkConstants.NETVERSION : ip.split("\0")[1] : FMLNetworkConstants.NOVERSION;
    }

    public static ConnectionType getConnectionType(final Supplier<NetworkManager> connection)
    {
        return ConnectionType.forVersionFlag(connection.get().channel().attr(FMLNetworkConstants.FML_NETVERSION).get());
    }

    public static Packet<?> getEntitySpawningPacket(Entity entity)
    {
        if (!entity.getType().usesVanillaSpawning())
        {
            return FMLNetworkConstants.playChannel.toVanillaPacket(new FMLPlayMessages.SpawnEntity(entity), NetworkDirection.PLAY_TO_CLIENT);
        }
        return null;
    }

    public static boolean onCustomPayload(final ICustomPacket<?> packet, final NetworkManager manager) {
        return NetworkRegistry.findTarget(packet.getName()).
                map(ni->ni.dispatch(packet.getDirection(), packet, manager)).orElse(Boolean.FALSE);
    }

    public static void registerServerLoginChannel(NetworkManager manager, CPacketHandshake packet)
    {
        manager.channel().attr(FMLNetworkConstants.FML_NETVERSION).set(packet.getFMLVersion());
        FMLHandshakeHandler.registerHandshake(manager, NetworkDirection.LOGIN_TO_CLIENT);
    }

    public synchronized static void registerClientLoginChannel(NetworkManager manager)
    {
        manager.channel().attr(FMLNetworkConstants.FML_NETVERSION).set(FMLNetworkConstants.NOVERSION);
        FMLHandshakeHandler.registerHandshake(manager, NetworkDirection.LOGIN_TO_SERVER);
    }

    public static void handleClientLoginSuccess(NetworkManager manager) {
        if (manager == null || manager.channel() == null) throw new NullPointerException("ARGH! Network Manager is null (" + manager != null ? "CHANNEL" : "MANAGER"+")" );
        if (getConnectionType(()->manager) == ConnectionType.VANILLA) {
            LOGGER.info("Connected to a vanilla server. Catching up missing behaviour.");
            ConfigTracker.INSTANCE.loadDefaultServerConfigs();
        } else {
            LOGGER.info("Connected to a modded server.");
        }
    }

    public static boolean tickNegotiation(NetHandlerLoginServer netHandlerLoginServer, NetworkManager networkManager, EntityPlayerMP player)
    {
        return FMLHandshakeHandler.tickLogin(networkManager);
    }

    /**
     * Request to open a GUI on the client, from the server
     *
     * Refer to {@link net.minecraftforge.fml.ExtensionPoint#GUIFACTORY} for how to provide a function to consume
     * these GUI requests on the client.
     *
     * The {@link IInteractionObject#getGuiID()} is treated as a {@link ResourceLocation}.
     * It should refer to a valid modId namespace, to trigger opening on the client.
     * The namespace is directly used to lookup the modId in the client side.
     *
     * @param player The player to open the GUI for
     * @param containerSupplier A supplier of container properties including the registry name of the container
     */
    public static void openGui(EntityPlayerMP player, IInteractionObject containerSupplier)
    {
        openGui(player, containerSupplier, buf -> {});
    }

    /**
     * Request to open a GUI on the client, from the server
     *
     * Refer to {@link net.minecraftforge.fml.ExtensionPoint#GUIFACTORY} for how to provide a function to consume
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
    public static void openGui(EntityPlayerMP player, IInteractionObject containerSupplier, BlockPos pos)
    {
        openGui(player, containerSupplier, buf -> buf.writeBlockPos(pos));
    }
    /**
     * Request to open a GUI on the client, from the server
     *
     * Refer to {@link net.minecraftforge.fml.ExtensionPoint#GUIFACTORY} for how to provide a function to consume
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
    public static void openGui(EntityPlayerMP player, IInteractionObject containerSupplier, Consumer<PacketBuffer> extraDataWriter)
    {
        if (player.world.isRemote) return;
        ResourceLocation id = new ResourceLocation(containerSupplier.getGuiID());
        player.closeContainer();
        player.getNextWindowId();
        int openContainerId = player.currentWindowId;
        PacketBuffer extraData = new PacketBuffer(Unpooled.buffer());
        extraDataWriter.accept(extraData);
        extraData.readerIndex(0); // reset to beginning in case modders read for whatever reason

        PacketBuffer output = new PacketBuffer(Unpooled.buffer());
        output.writeVarInt(extraData.readableBytes());
        output.writeBytes(extraData);

        if (output.readableBytes() > 32600 || output.readableBytes() < 1) {
            throw new IllegalArgumentException("Invalid PacketBuffer for openGui, found "+ output.readableBytes()+ " bytes");
        }
        FMLPlayMessages.OpenContainer msg = new FMLPlayMessages.OpenContainer(id, openContainerId, output);
        FMLNetworkConstants.playChannel.sendTo(msg, player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);

        Container c = containerSupplier.createContainer(player.inventory, player);
        player.openContainer = c;
        player.openContainer.windowId = openContainerId;
        player.openContainer.addListener(player);
        MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, c));
    }
}
