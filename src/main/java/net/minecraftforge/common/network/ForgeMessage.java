package net.minecraftforge.common.network;

import java.util.Map;

import net.minecraftforge.fluids.FluidRegistry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;

public abstract class ForgeMessage {
    public static class DimensionRegisterMessage extends ForgeMessage {
        /** The dimension ID to register on client */
        int dimensionId;
        /** The provider ID to register with dimension on client */
        int providerId;
        
        public DimensionRegisterMessage(){}
        public DimensionRegisterMessage(int dimensionId, int providerId)
        {
            this.dimensionId = dimensionId;
            this.providerId = providerId;
        }
        
        @Override
        void toBytes(ByteBuf bytes)
        {
            bytes.writeInt(this.dimensionId);
            bytes.writeInt(this.providerId);
        }

        @Override
        void fromBytes(ByteBuf bytes)
        {
            dimensionId = bytes.readInt();
            providerId = bytes.readInt();
        }
    }

    public static class FluidIdMapMessage extends ForgeMessage {
        BiMap<String, Integer> fluidIds = HashBiMap.create();
        @Override
        void toBytes(ByteBuf bytes)
        {
            Map<String, Integer> ids = FluidRegistry.getRegisteredFluidIDs();
            bytes.writeInt(ids.size());
            for (Map.Entry<String, Integer> entry : ids.entrySet())
            {
                ByteBufUtils.writeUTF8String(bytes,entry.getKey());
                bytes.writeInt(entry.getValue());
            }
        }

        @Override
        void fromBytes(ByteBuf bytes)
        {
            int listSize = bytes.readInt();
            for (int i = 0; i < listSize; i++) {
                String fluidName = ByteBufUtils.readUTF8String(bytes);
                int fluidId = bytes.readInt();
                fluidIds.put(fluidName, fluidId);
            }
        }
    }

    abstract void toBytes(ByteBuf bytes);
    abstract void fromBytes(ByteBuf bytes);
}
