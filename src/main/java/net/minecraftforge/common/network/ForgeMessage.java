/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.common.network;

import java.util.Map;
import java.util.Set;

import net.minecraftforge.ingredients.Ingredient;
import net.minecraftforge.ingredients.IngredientRegistry;
import org.apache.logging.log4j.Level;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import com.google.common.base.Charsets;
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
        String providerId;

        public DimensionRegisterMessage(){}
        public DimensionRegisterMessage(int dimensionId, String providerId)
        {
            this.dimensionId = dimensionId;
            this.providerId = providerId;
        }

        @Override
        void toBytes(ByteBuf bytes)
        {
            bytes.writeInt(this.dimensionId);
            byte[] data = this.providerId.getBytes(Charsets.UTF_8);
            bytes.writeShort(data.length);
            bytes.writeBytes(data);
        }

        @Override
        void fromBytes(ByteBuf bytes)
        {
            dimensionId = bytes.readInt();
            byte[] data = new byte[bytes.readShort()];
            bytes.readBytes(data);
            providerId = new String(data, Charsets.UTF_8);
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

    public static class IngredientIdMapMessage extends ForgeMessage {
        BiMap<Ingredient, Integer> ingredientIds = HashBiMap.create();
        Set<String> defaultIngredients = Sets.newHashSet();
        @SuppressWarnings("deprecation")
        @Override
        void toBytes(ByteBuf bytes)
        {
            Map<Ingredient, Integer> ids = IngredientRegistry.getRegisteredIngredientIDs();
            bytes.writeInt(ids.size());
            for (Map.Entry<Ingredient, Integer> entry : ids.entrySet())
            {
                ByteBufUtils.writeUTF8String(bytes,entry.getKey().getName());
                bytes.writeInt(entry.getValue());
            }
            for (Map.Entry<Ingredient, Integer> entry : ids.entrySet())
            {
                String defaultName = IngredientRegistry.getDefaultIngredientName(entry.getKey());
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
                ingredientIds.put(IngredientRegistry.getIngredient(fluidName), fluidId);
            }
            // do we have a defaults list?

            if (bytes.isReadable())
            {
                for (int i = 0; i < listSize; i++)
                {
                    defaultIngredients.add(ByteBufUtils.readUTF8String(bytes));
                }
            }
            else
            {
                FMLLog.getLogger().log(Level.INFO, "Legacy server message contains no default ingredient list - there may be problems with ingredients");
                defaultIngredients.clear();
            }
        }
    }


    abstract void toBytes(ByteBuf bytes);
    abstract void fromBytes(ByteBuf bytes);
}
