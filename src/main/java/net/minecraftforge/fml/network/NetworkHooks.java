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

package net.minecraftforge.fml.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.CPacketHandshake;
import net.minecraft.network.login.client.CPacketCustomPayloadLogin;
import net.minecraft.server.network.NetHandlerLoginServer;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public class NetworkHooks
{
    public static final String NETVERSION = "FML1";
    public static final String NOVERSION = "NONE";
    static final ResourceLocation FMLHANDSHAKE = new ResourceLocation("fml", "handshake");
    public static String getFMLVersion(final String ip)
    {
        return ip.contains("\0") ? Objects.equals(ip.split("\0")[1], NETVERSION) ? NETVERSION : ip.split("\0")[1] : NOVERSION;
    }

    public static boolean accepts(final CPacketHandshake packet)
    {
        return Objects.equals(packet.getFMLVersion(), NETVERSION);
    }

    public static ConnectionType getConnectionType(final NetHandlerPlayServer connection)
    {
        return ConnectionType.forVersionFlag(connection.netManager.channel().attr(FMLNetworking.FML_MARKER).get());
    }

    public static Packet<?> getEntitySpawningPacket(Entity entity)
    {
        return null;
    }

    public static boolean onCustomPayload(final ICustomPacket<?> packet, final NetworkManager manager) {
        return NetworkRegistry.findTarget(packet.getName()).
                map(ni->ni.dispatch(packet.getDirection(), packet, manager)).orElse(Boolean.FALSE);
    }

    public static void registerServerLoginChannel(NetworkManager manager, CPacketHandshake packet)
    {
        manager.channel().attr(FMLNetworking.FML_MARKER).set(packet.getFMLVersion());
        FMLNetworking.registerHandshake(manager, NetworkDirection.LOGIN_TO_CLIENT);
    }

    public static void registerClientLoginChannel(NetworkManager manager)
    {
        manager.channel().attr(FMLNetworking.FML_MARKER).set(NETVERSION);
        FMLNetworking.registerHandshake(manager, NetworkDirection.LOGIN_TO_SERVER);
    }

    public static boolean tickNegotiation(NetHandlerLoginServer netHandlerLoginServer, NetworkManager networkManager, EntityPlayerMP player)
    {
        return FMLNetworking.dispatch(networkManager);
    }
}
