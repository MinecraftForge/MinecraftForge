/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
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

import net.minecraft.nbt.TagParser;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

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
    public static CompoundTag readNBT(JsonObject json, String key)
    {
        if (net.minecraft.util.GsonHelper.isValidNode(json, key))
        {
            try
            {
                return TagParser.parseTag(net.minecraft.util.GsonHelper.getAsString(json, key));
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
