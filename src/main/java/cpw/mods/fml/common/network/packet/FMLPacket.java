/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package cpw.mods.fml.common.network.packet;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.NetHandler;

import com.google.common.base.Throwables;
import com.google.common.collect.MapMaker;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.common.primitives.UnsignedBytes;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.FMLNetworkException;
import cpw.mods.fml.common.network.FMLNetworkHandler;

public abstract class FMLPacket
{
    enum Type
    {
        /**
         * Opening salutation from the server to the client -> request all mods from the client
         */
        MOD_LIST_REQUEST(ModListRequestPacket.class, false),
        /**
         * The client responds with the list of mods and versions it has. This is verified by the server.
         */
        MOD_LIST_RESPONSE(ModListResponsePacket.class, false),
        /**
         * At which point the server tells the client the mod identifiers for this session.
         */
        MOD_IDENTIFIERS(ModIdentifiersPacket.class, false),
        /**
         * Or, if there is missing stuff, the server tells the client what's missing and drops the connection.
         */
        MOD_MISSING(ModMissingPacket.class, false),
        /**
         * Open a GUI on the client from the server
         */
        GUIOPEN(OpenGuiPacket.class, false, "OpenGuiHandler"),
        /**
         * Spawn an entity on the client from the server
         */
        ENTITYSPAWN(EntitySpawnPacket.class, false),
        /**
         * Fixes entity location data after spawning
         */
        ENTITYSPAWNADJUSTMENT(EntitySpawnAdjustmentPacket.class, false),
        /**
         * The ID map to send to the client
         */
        MOD_IDMAP(ModIdMapPacket.class, true);


        private Class<? extends FMLPacket> packetType;
        private boolean isMultipart;
        private String executorClass;

        private ConcurrentMap<INetworkManager, FMLPacket> partTracker;

        private Type(Class<? extends FMLPacket> clazz, boolean isMultipart, String executorClass)
        {
            this.packetType = clazz;
            this.isMultipart = isMultipart;
            this.executorClass = executorClass;
        }

        FMLPacket make()
        {
            try
            {
                return this.packetType.newInstance();
            }
            catch (Exception e)
            {
                Throwables.propagateIfPossible(e);
                FMLLog.log(Level.SEVERE, e, "A bizarre critical error occured during packet encoding");
                throw new FMLNetworkException(e);
            }
        }

        boolean isMultipart()
        {
            return isMultipart;
        }

        private FMLPacket findCurrentPart(NetworkManager network)
        {
            if (partTracker == null)
            {
                partTracker = new MapMaker().weakKeys().weakValues().makeMap();
            }
            if (!partTracker.containsKey(network))
            {
                partTracker.put(network, make());
            }
            return partTracker.get(network);
        }
    }

    public static byte[][] makePacketSet(Type type, Object... data)
    {
        if (!type.isMultipart())
        {
            return new byte[0][];
        }
        byte[] packetData = type.make().generatePacketData(data);

        byte[][] chunks = new byte[packetData.length / 32000 + 1][];
        for (int i = 0; i < packetData.length / 32000 + 1; i++)
        {
            int len = Math.min(32000, packetData.length - i* 32000);
            chunks[i] = Bytes.concat(new byte[] { UnsignedBytes.checkedCast(type.ordinal()), UnsignedBytes.checkedCast(i), UnsignedBytes.checkedCast(chunks.length)}, Ints.toByteArray(len), Arrays.copyOfRange(packetData, i * 32000, len + i * 32000));
        }
        return chunks;
    }
    static byte[] makePacket(Type type, Object... data)
    {
        byte[] packetData = type.make().generatePacketData(data);
        return Bytes.concat(new byte[] { UnsignedBytes.checkedCast(type.ordinal()) }, packetData );
    }

    abstract byte[] generatePacketData(Object... data);

    abstract FMLPacket consumePacketData(byte[] data);
}
