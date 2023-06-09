/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraft.nbt.TagParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.Nullable;

public class CraftingHelper
{
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogManager.getLogger();
    @SuppressWarnings("unused")
    private static final Marker CRAFTHELPER = MarkerManager.getMarker("CRAFTHELPER");
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Map<ResourceLocation, IConditionSerializer<?>> conditions = new HashMap<>();
    private static final BiMap<ResourceLocation, IIngredientSerializer<?>> ingredients = HashBiMap.create();

    public static IConditionSerializer<?> register(IConditionSerializer<?> serializer)
    {
        ResourceLocation key = serializer.getID();
        if (conditions.containsKey(key))
            throw new IllegalStateException("Duplicate recipe condition serializer: " + key);
        conditions.put(key, serializer);
        return serializer;
    }
    public static <T extends Ingredient> IIngredientSerializer<T> register(ResourceLocation key, IIngredientSerializer<T> serializer)
    {
        if (ingredients.containsKey(key))
            throw new IllegalStateException("Duplicate recipe ingredient serializer: " + key);
        if (ingredients.containsValue(serializer))
            throw new IllegalStateException("Duplicate recipe ingredient serializer: " + key + " " + serializer);
        ingredients.put(key, serializer);
        return serializer;
    }
    @Nullable
    public static ResourceLocation getID(IIngredientSerializer<?> serializer)
    {
        return ingredients.inverse().get(serializer);
    }
    public static <T extends Ingredient> void write(FriendlyByteBuf buffer, T ingredient)
    {
        @SuppressWarnings("unchecked") //I wonder if there is a better way generic wise...
        IIngredientSerializer<T> serializer = (IIngredientSerializer<T>)ingredient.getSerializer();
        ResourceLocation key = ingredients.inverse().get(serializer);
        if (key == null)
            throw new IllegalArgumentException("Tried to serialize unregistered Ingredient: " + ingredient + " " + serializer);
        if (serializer != VanillaIngredientSerializer.INSTANCE)
        {
            buffer.writeVarInt(-1); //Marker to know there is a custom ingredient
            buffer.writeResourceLocation(key);
        }
        serializer.write(buffer, ingredient);
    }

    public static Ingredient getIngredient(ResourceLocation type, FriendlyByteBuf buffer)
    {
        IIngredientSerializer<?> serializer = ingredients.get(type);
        if (serializer == null)
            throw new IllegalArgumentException("Can not deserialize unknown Ingredient type: " + type);
        return serializer.parse(buffer);
    }

    public static Ingredient getIngredient(JsonElement json, boolean allowEmpty)
    {
        if (json == null || json.isJsonNull())
            throw new JsonSyntaxException("Json cannot be null");

        if (json.isJsonArray())
        {
            List<Ingredient> ingredients = Lists.newArrayList();
            List<Ingredient> vanilla = Lists.newArrayList();
            json.getAsJsonArray().forEach((ele) ->
            {
                Ingredient ing = CraftingHelper.getIngredient(ele, allowEmpty);

                if (ing.getClass() == Ingredient.class) //Vanilla, Due to how we read it splits each itemstack, so we pull out to re-merge later
                    vanilla.add(ing);
                else
                    ingredients.add(ing);
            });

            if (!vanilla.isEmpty())
                ingredients.add(Ingredient.merge(vanilla));

            if (ingredients.size() == 0)
            {
                if (allowEmpty)
                {
                    return Ingredient.EMPTY;
                }
                throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
            }

            if (ingredients.size() == 1)
                return ingredients.get(0);

            return new CompoundIngredient(ingredients);
        }

        if (!json.isJsonObject())
            throw new JsonSyntaxException("Expcted ingredient to be a object or array of objects");

        JsonObject obj = (JsonObject)json;

        String type = GsonHelper.getAsString(obj, "type", "minecraft:item");
        if (type.isEmpty())
            throw new JsonSyntaxException("Ingredient type can not be an empty string");

        IIngredientSerializer<?> serializer = ingredients.get(new ResourceLocation(type));
        if (serializer == null)
            throw new JsonSyntaxException("Unknown ingredient type: " + type);

        return serializer.parse(obj);
    }

    public static ItemStack getItemStack(JsonObject json, boolean readNBT)
    {
        return getItemStack(json, readNBT, false);
    }

    public static Item getItem(String itemName, boolean disallowsAirInRecipe)
    {
        ResourceLocation itemKey = new ResourceLocation(itemName);
        if (!ForgeRegistries.ITEMS.containsKey(itemKey))
            throw new JsonSyntaxException("Unknown item '" + itemName + "'");

        Item item = ForgeRegistries.ITEMS.getValue(itemKey);
        if (disallowsAirInRecipe && item == Items.AIR)
            throw new JsonSyntaxException("Invalid item: " + itemName);
        return Objects.requireNonNull(item);
    }

    public static CompoundTag getNBT(JsonElement element)
    {
        try
        {
            if (element.isJsonObject())
                return TagParser.parseTag(GSON.toJson(element));
            else
                return TagParser.parseTag(GsonHelper.convertToString(element, "nbt"));
        }
        catch (CommandSyntaxException e)
        {
            throw new JsonSyntaxException("Invalid NBT Entry: " + e);
        }
    }

    public static ItemStack getItemStack(JsonObject json, boolean readNBT, boolean disallowsAirInRecipe)
    {
        String itemName = GsonHelper.getAsString(json, "item");
        Item item = getItem(itemName, disallowsAirInRecipe);
        if (readNBT && json.has("nbt"))
        {
            CompoundTag nbt = getNBT(json.get("nbt"));
            CompoundTag tmp = new CompoundTag();
            if (nbt.contains("ForgeCaps"))
            {
                tmp.put("ForgeCaps", nbt.get("ForgeCaps"));
                nbt.remove("ForgeCaps");
            }

            tmp.put("tag", nbt);
            tmp.putString("id", itemName);
            tmp.putInt("Count", GsonHelper.getAsInt(json, "count", 1));

            return ItemStack.of(tmp);
        }

        return new ItemStack(item, GsonHelper.getAsInt(json, "count", 1));
    }

    public static boolean processConditions(JsonObject json, String memberName, ICondition.IContext context)
    {
        return !json.has(memberName) || processConditions(GsonHelper.getAsJsonArray(json, memberName), context);
    }

    public static boolean processConditions(JsonArray conditions, ICondition.IContext context)
    {
        for (int x = 0; x < conditions.size(); x++)
        {
            if (!conditions.get(x).isJsonObject())
                throw new JsonSyntaxException("Conditions must be an array of JsonObjects");

            JsonObject json = conditions.get(x).getAsJsonObject();
            if (!CraftingHelper.getCondition(json).test(context))
                return false;
        }
        return true;
    }

    public static ICondition getCondition(JsonObject json)
    {
        ResourceLocation type = new ResourceLocation(GsonHelper.getAsString(json, "type"));
        IConditionSerializer<?> serializer = conditions.get(type);
        if (serializer == null)
            throw new JsonSyntaxException("Unknown condition type: " + type.toString());
        return serializer.read(json);
    }

    public static <T extends ICondition> JsonObject serialize(T condition)
    {
        @SuppressWarnings("unchecked")
        IConditionSerializer<T> serializer = (IConditionSerializer<T>)conditions.get(condition.getID());
        if (serializer == null)
            throw new JsonSyntaxException("Unknown condition type: " + condition.getID().toString());
        return serializer.getJson(condition);
    }

    public static JsonArray serialize(ICondition... conditions)
    {
        JsonArray arr = new JsonArray();
        for(ICondition iCond : conditions)
        {
            arr.add(serialize(iCond));
        }
        return arr;
    }
}
