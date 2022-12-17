/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.conditions;

import com.mojang.serialization.JsonOps;
import org.slf4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceKey;
import net.minecraftforge.common.conditions.Condition.IContext;

/**
 * 
 * Holds code relating to the de/serialization and processing of {@link Condition}s
 */
public class ConditionHelper {

    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Serializes a single condition as a {@link JsonObject}
     * @param condition The condition to be serialized
     * @return The serialized JSON
     * @throws RuntimeException If no serializer is registered for the condition, or if the serializer encountered an exception encoding
     */
    public static JsonElement serialize(Condition condition)
    {
        return Condition.DIRECT_CODEC.encodeStart(JsonOps.INSTANCE, condition)
                .getOrThrow(false, msg -> LOGGER.error("Encountered exception encoding condition: {}", msg));
    }

    /**
     * Serializes an array of conditions as a {@link JsonArray}
     * @param conditions The conditions to be serialized
     * @return The serialized JSON
     * @throws RuntimeException If no serializer is registered for any of the passed conditions.
     */
    public static JsonArray serialize(Condition... conditions)
    {
        JsonArray arr = new JsonArray();
        for (Condition iCond : conditions)
        {
            arr.add(ConditionHelper.serialize(iCond));
        }
        return arr;
    }

    /**
     * Parses an {@link Condition} from a {@link JsonObject}.
     * @param json The serialized condition JSON
     * @return The deserialized condition object
     * @throws RuntimeException If no serializer is registered for the specified condition type, or if the serializer encountered an exception decoding
     */
    public static Condition getCondition(JsonElement json)
    {
        return Condition.DIRECT_CODEC.decode(JsonOps.INSTANCE, json)
                .getOrThrow(false, msg -> LOGGER.error("Encountered exception decoding condition: {}", msg)).getFirst();
    }

    /**
     * Given an array of conditions as JSON and a context, deserializes and executes all conditions.<br>
     * This method will fail-fast, in that if a condition fails, any following conditions will not be parsed or executed.
     * @param conditions The serialized condition array
     * @param context The {@linkplain Condition.IContext Condition Context}
     * @return True if all conditions were successfully parsed and returned true, or the condition array is empty.
     * @throws JsonSyntaxException If a condition fails to be parsed.
     */
    public static boolean processConditions(JsonArray conditions, Condition.IContext context)
    {
        for (int x = 0; x < conditions.size(); x++)
        {
            if (!conditions.get(x).isJsonObject())
                throw new JsonSyntaxException("Conditions must be an array of JsonObjects");
    
            JsonObject json = conditions.get(x).getAsJsonObject();
            if (!getCondition(json).test(context))
                return false;
        }
        return true;
    }

    /**
     * Given a {@link JsonObject}, the name of the field holding the condition array, and the context object,
     * this method retrieves the array and parses+executes all of the stored conditions.<br>
     * If the object does not contain the specified condition array, this method returns true.
     * @param json The outer JSON object
     * @param memberName The name of the condition array within the object
     * @param context The {@linkplain Condition.IContext Condition Context}
     * @return True if all conditions were successfully parsed and returned true, or the condition array is empty/absent.
     * @throws JsonSyntaxException If a condition fails to be parsed
     */
    public static boolean processConditions(JsonObject json, String memberName, Condition.IContext context)
    {
        final JsonElement conditionsMember = json.get(memberName);
        if (conditionsMember == null) return true;
        final JsonArray array;
        if (conditionsMember.isJsonArray())
        {
            array = conditionsMember.getAsJsonArray();
        } else
        {
            array = new JsonArray();
            array.add(conditionsMember);
        }
        return !json.has(memberName) || processConditions(array, context);
    }

    /**
     * Checks for and executes conditions on a dynamic registry entry, if present.<br>
     * This method will not throw an exception in production to prevent a crash when loading a bad datapack.
     * @param element A {@link JsonElement} representing a serialized dynamic registry entry.
     * @param key The ID of the serialized entry.
     * @param packId The name of the datapack providing the entry.
     * @return True if all conditions were successfully parsed and returned true, or the condition array is empty/absent.
     */
    public static boolean processRegistryEntry(JsonElement element, ResourceKey<?> key, String packId)
    {
        if (element == null || !element.isJsonObject())
        {
            return true; // If the element is not a JSON object, it cannot not have conditions.
        }

        JsonObject obj = element.getAsJsonObject();
        try
        {
            return processConditions(obj, "forge:conditions", IContext.TAGS_INVALID);
        } 
        catch (Exception ex)
        {
            LOGGER.warn("Encountered exception reading conditions of entry {}/{} in pack {}: {}", key.location(), key.registry(), packId, ex);
            return false; // Prevent loading if an exception is thrown
        }
    }

}
