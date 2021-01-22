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

package net.minecraftforge.common.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;

public class JsonUtils
{
    // http://stackoverflow.com/questions/7706772/deserializing-immutablelist-using-gson/21677349#21677349
    public enum ImmutableListTypeAdapter implements JsonDeserializer<ImmutableList<?>>, JsonSerializer<ImmutableList<?>>
    {
        INSTANCE;

        @Override
        public ImmutableList<?> deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException
        {
            final Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
            final Type parametrizedType = listOf(typeArguments[0]).getType();
            final List<?> list = context.deserialize(json, parametrizedType);
            return ImmutableList.copyOf(list);
        }

        @Override
        public JsonElement serialize(ImmutableList<?> src, Type type, JsonSerializationContext context)
        {
            final Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
            final Type parametrizedType = listOf(typeArguments[0]).getType();
            return context.serialize(src, parametrizedType);
        }
    }

    @SuppressWarnings({ "serial", "unchecked" })
    private static <E> TypeToken<List<E>> listOf(final Type arg)
    {
        return new TypeToken<List<E>>() {}.where(new TypeParameter<E>() {}, (TypeToken<E>) TypeToken.of(arg));
    }

    public enum ImmutableMapTypeAdapter implements JsonDeserializer<ImmutableMap<String, ?>>, JsonSerializer<ImmutableMap<String, ?>>
    {
        INSTANCE;

        @Override
        public ImmutableMap<String, ?> deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException
        {
            final Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
            final Type parameterizedType = mapOf(typeArguments[1]).getType();
            final Map<String, ?> map = context.deserialize(json, parameterizedType);
            return ImmutableMap.copyOf(map);
        }

        @Override
        public JsonElement serialize(ImmutableMap<String, ?> src, Type type, JsonSerializationContext context)
        {
            final Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
            final Type parameterizedType = mapOf(typeArguments[1]).getType();
            return context.serialize(src, parameterizedType);
        }
    }

    @Nullable
    public static CompoundNBT readNBT(JsonObject json, String key)
    {
        if (net.minecraft.util.JSONUtils.hasField(json, key))
        {
            try
            {
                return JsonToNBT.getTagFromJson(net.minecraft.util.JSONUtils.getString(json, key));
            } catch (CommandSyntaxException e)
            {
                throw new JsonSyntaxException("Malformed NBT tag", e);
            }
        } else
        {
            return null;
        }
    }

    @SuppressWarnings({ "serial", "unchecked" })
    private static <E> TypeToken<Map<String, E>> mapOf(final Type arg)
    {
        return new TypeToken<Map<String, E>>() {}.where(new TypeParameter<E>() {}, (TypeToken<E>) TypeToken.of(arg));
    }
}
