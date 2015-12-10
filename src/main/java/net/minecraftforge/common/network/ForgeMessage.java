package net.minecraftforge.common.network;

import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Level;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.ByteBufUtils;
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
        BiMap<Fluid, Integer> fluidIds = HashBiMap.create();
        Set<String> defaultFluids = Sets.newHashSet();
        @SuppressWarnings("deprecation")
        @Override
        void toBytes(ByteBuf bytes)
        {
            Map<Fluid, Integer> ids = FluidRegistry.getRegisteredFluidIDs();
            bytes.writeInt(ids.size());
            for (Map.Entry<Fluid, Integer> entry : ids.entrySet())
            {
                ByteBufUtils.writeUTF8String(bytes,entry.getKey().getName());
                bytes.writeInt(entry.getValue());
            }
            for (Map.Entry<Fluid, Integer> entry : ids.entrySet())
            {
                String defaultName = FluidRegistry.getDefaultFluidName(entry.getKey());
                ByteBufUtils.writeUTF8String(bytes, defaultName);
            }
        }

        @Override
        void fromBytes(ByteBuf bytes)
        {
            int listSize = bytes.readInt();
            for (int i = 0; i < listSize; i++) {
                String fluidName = ByteBufUtils.readUTF8String(bytes);
                int fluidId = bytes.readInt();
                fluidIds.put(FluidRegistry.getFluid(fluidName), fluidId);
            }
            // do we have a defaults list?

            if (bytes.isReadable())
            {
                for (int i = 0; i < listSize; i++)
                {
                    defaultFluids.add(ByteBufUtils.readUTF8String(bytes));
                }
            }
            else
            {
                FMLLog.getLogger().log(Level.INFO, "Legacy server message contains no default fluid list - there may be problems with fluids");
                defaultFluids.clear();
            }
        }
    }

    abstract void toBytes(ByteBuf bytes);
    abstract void fromBytes(ByteBuf bytes);
}
