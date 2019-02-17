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
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.handshake.client.CPacketHandshake;
import net.minecraft.network.NetHandlerLoginServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IInteractionObject;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

public class NetworkHooks
{
    public static final String NETVERSION = "FML1";
    public static final String NOVERSION = "NONE";

    public static String getFMLVersion(final String ip)
    {
        return ip.contains("\0") ? Objects.equals(ip.split("\0")[1], NETVERSION) ? NETVERSION : ip.split("\0")[1] : NOVERSION;
    }

    public static boolean accepts(final CPacketHandshake packet)
    {
        return Objects.equals(packet.getFMLVersion(), NETVERSION) || NetworkRegistry.acceptsVanillaConnections();
    }

    public static ConnectionType getConnectionType(final NetHandlerPlayServer connection)
    {
        return ConnectionType.forVersionFlag(connection.netManager.channel().attr(FMLNetworking.FML_MARKER).get());
    }

    public static Packet<?> getEntitySpawningPacket(Entity entity)
    {
        if (!entity.getType().usesVanillaSpawning())
        {
            return FMLPlayHandler.channel.toVanillaPacket(new FMLPlayMessages.SpawnEntity(entity), NetworkDirection.PLAY_TO_CLIENT);
        }
        return null;
    }

    public static boolean onCustomPayload(final ICustomPacket<?> packet, final NetworkManager manager) {
        return NetworkRegistry.findTarget(packet.getName()).
                map(ni->ni.dispatch(packet.getDirection(), packet, manager)).orElse(Boolean.FALSE);
    }

    public static void registerServerLoginChannel(NetworkManager manager, CPacketHandshake packet)
    {
        manager.channel().attr(FMLNetworking.FML_MARKER).set(packet.getFMLVersion());
        FMLHandshakeHandler.registerHandshake(manager, NetworkDirection.LOGIN_TO_CLIENT);
    }

    public static void registerClientLoginChannel(NetworkManager manager)
    {
        if (manager == null) return;
        manager.channel().attr(FMLNetworking.FML_MARKER).set(NETVERSION);
        FMLHandshakeHandler.registerHandshake(manager, NetworkDirection.LOGIN_TO_SERVER);
    }

    public static boolean tickNegotiation(NetHandlerLoginServer netHandlerLoginServer, NetworkManager networkManager, EntityPlayerMP player)
    {
        return FMLHandshakeHandler.tickLogin(networkManager);
    }

    public static void openGui(EntityPlayerMP player, IInteractionObject containerSupplier)
    {
        openGui(player, containerSupplier, buf -> {});
    }

    /**
     * Server method to tell the client to open a GUI on behalf of the server
     *
     * The {@link IInteractionObject#getGuiID()} is treated as a {@link ResourceLocation}.
     * It should refer to a valid modId namespace, to trigger opening on the client.
     * The namespace is directly used to lookup the modId in the client side.
     * The maximum size for #extraDataWriter is 32600 bytes.
     *
     * @param player The player to open the GUI for
     * @param containerSupplier The Container Supplier
     * @param extraDataWriter Consumer to write any additional data the GUI needs
     */
    public static void openGui(EntityPlayerMP player, IInteractionObject containerSupplier, Consumer<PacketBuffer> extraDataWriter)
    {
        if (player.world.isRemote) return;
        ResourceLocation id = new ResourceLocation(containerSupplier.getGuiID());
        Container c = containerSupplier.createContainer(player.inventory, player);
        player.closeScreen();
        player.getNextWindowId();
        int openContainer = player.currentWindowId;
        player.openContainer = c;
        player.openContainer.windowId = openContainer;
        player.openContainer.addListener(player);
        MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, c));

        PacketBuffer extraData = new PacketBuffer(Unpooled.buffer());
        extraDataWriter.accept(extraData);
        extraData.readerIndex(0); // reset to beginning in case modders read for whatever reason

        PacketBuffer output = new PacketBuffer(Unpooled.buffer());
        output.writeVarInt(extraData.readableBytes());
        output.writeBytes(extraData);

        if (output.readableBytes() > 32600 || output.readableBytes() < 1) {
            throw new IllegalArgumentException("Invalid PacketBuffer for openGui, found "+ output.readableBytes()+ " bytes");
        }
        FMLPlayMessages.OpenContainer msg = new FMLPlayMessages.OpenContainer(id, openContainer, output);
        FMLPlayHandler.channel.sendTo(msg, player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
    }
}
