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
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraftforge.fml.common.network.internal.FMLMessage;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.util.Objects;

public class NetworkHooks
{
    public static final String NETVERSION = "FML1";
    public static final String NOVERSION = "NONE";
    public static String getFMLVersion(final String ip)
    {
        return ip.contains("\0") ? Objects.equals(ip.split("\0")[1], NETVERSION) ? NETVERSION : ip.split("\0")[1] : NOVERSION;
    }

    public static boolean accepts(final C00Handshake packet)
    {
        return Objects.equals(packet.getFMLVersion(), NETVERSION);
    }

    public static ConnectionType getConnectionType(final NetHandlerPlayServer connection)
    {
        return ConnectionType.MODDED;
    }

    public static Packet<?> getEntitySpawningPacket(Entity entity)
    {
        EntityRegistry.EntityRegistration er = EntityRegistry.instance().lookupModSpawn(entity.getClass(), false);
        if (er == null || er.usesVanillaSpawning())
        {
            return null;
        }
        return null;
    }
}
