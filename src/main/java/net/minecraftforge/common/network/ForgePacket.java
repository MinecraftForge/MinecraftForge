package net.minecraftforge.common.network;

import java.util.Arrays;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraftforge.common.network.packet.DimensionRegisterPacket;
import net.minecraftforge.fluids.FluidIdMapPacket;

import com.google.common.base.Throwables;
import com.google.common.collect.MapMaker;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.common.primitives.UnsignedBytes;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.FMLNetworkException;

public abstract class ForgePacket
{
    public static final String CHANNEL_ID = "FORGE";
    enum Type
    {
        /**
         * Registers a dimension for a provider on client
         */
        REGISTERDIMENSION(DimensionRegisterPacket.class),
        /**
         * The Fluid ID map to send to the client
         */
        FLUID_IDMAP(FluidIdMapPacket.class);

        private Class<? extends ForgePacket> packetType;
        private ConcurrentMap<INetworkManager, ForgePacket> partTracker;

        private Type(Class<? extends ForgePacket> clazz)
        {
            this.packetType = clazz;
        }

        ForgePacket make()
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

        private ForgePacket consumePart(INetworkManager network, byte[] data)
        {
            if (partTracker == null)
            {
                partTracker = new MapMaker().weakKeys().weakValues().makeMap();
            }
            if (!partTracker.containsKey(network))
            {
                partTracker.put(network, make());
            }

            ForgePacket pkt = partTracker.get(network);

            ByteArrayDataInput bdi = ByteStreams.newDataInput(data);
            int chunkIdx = UnsignedBytes.toInt(bdi.readByte());
            int chunkTotal = UnsignedBytes.toInt(bdi.readByte());
            int chunkLength = bdi.readInt();

            if (pkt.partials == null)
            {
                pkt.partials = new byte[chunkTotal][];
            }

            pkt.partials[chunkIdx] = new byte[chunkLength];
            bdi.readFully(pkt.partials[chunkIdx]);
            for (int i = 0; i < pkt.partials.length; i++)
            {
                if (pkt.partials[i] == null)
                {
                    return null;
                }
            }

            return pkt;
        }
    }

    private Type type;
    private byte[][] partials;

    public static Packet250CustomPayload[] makePacketSet(ForgePacket packet)
    {
        byte[] packetData = packet.generatePacket();

        if (packetData.length < 32000)
        {
            return new Packet250CustomPayload[]
            {
                new Packet250CustomPayload(CHANNEL_ID, 
                    Bytes.concat(new byte[]
                    {
                        UnsignedBytes.checkedCast(0), //IsMultipart: False
                        UnsignedBytes.checkedCast(packet.getID())
                    },
                    packetData))
            };
        }
        else
        {
            byte[][] chunks = new byte[packetData.length / 32000 + 1][];
            for (int i = 0; i < packetData.length / 32000 + 1; i++)
            {
                int len = Math.min(32000, packetData.length - i* 32000);
                chunks[i] = Bytes.concat(new byte[]
                    {
                        UnsignedBytes.checkedCast(1),              //IsMultipart: True
                        UnsignedBytes.checkedCast(packet.getID()), //Packet ID
                        UnsignedBytes.checkedCast(i),              //Part Number
                        UnsignedBytes.checkedCast(chunks.length),  //Total Parts
                    },
                    Ints.toByteArray(len),                         //Length
                    Arrays.copyOfRange(packetData, i * 32000, len + i * 32000));
            }

            Packet250CustomPayload[] ret = new Packet250CustomPayload[chunks.length];
            for (int i = 0; i < chunks.length; i++)
            {
                ret[i] = new Packet250CustomPayload(CHANNEL_ID, chunks[i]);
            }
            return ret;
        }
    }

    public static ForgePacket readPacket(INetworkManager network, byte[] payload)
    {
        boolean multipart = UnsignedBytes.toInt(payload[0]) == 1;
        int type = UnsignedBytes.toInt(payload[1]);
        Type eType = Type.values()[type];
        byte[] data = Arrays.copyOfRange(payload, 2, payload.length);

        if (multipart)
        {
            ForgePacket pkt = eType.consumePart(network, data);
            if (pkt != null)
            {
                return pkt.consumePacket(Bytes.concat(pkt.partials));
            }
            return null;
        }
        else
        {
            return eType.make().consumePacket(data);
        }
    }

    public ForgePacket()
    {
        for (Type t : Type.values())
        {
            if (t.packetType == getClass())
            {
                type = t;
                continue;
            }
        }
        if (type == null)
        {
            throw new RuntimeException("ForgePacket constructor called on ungregistered type.");
        }
    }

    public byte getID()
    {
        return UnsignedBytes.checkedCast(type.ordinal());
    }

    public abstract byte[] generatePacket();

    public abstract ForgePacket consumePacket(byte[] data);

    public abstract void execute(INetworkManager network, EntityPlayer player);
}
