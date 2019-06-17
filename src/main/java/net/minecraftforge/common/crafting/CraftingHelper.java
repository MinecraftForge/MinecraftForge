/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.common.crafting;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import net.minecraftforge.fml.ModList;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.Ingredient.IItemList;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class CraftingHelper
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker CRAFTHELPER = MarkerManager.getMarker("CRAFTHELPER");
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Map<ResourceLocation, IConditionSerializer> conditions = new HashMap<>();
    private static final BiMap<ResourceLocation, IIngredientSerializer<?>> ingredients = HashBiMap.create();
    private static Map<ResourceLocation, IItemList> constants = new HashMap<>();

    public static final IConditionSerializer CONDITION_MOD_LOADED = condition("mod_loaded", json -> {
                                                                        String modid = JSONUtils.getString(json, "modid");
                                                                        return () -> ModList.get().isLoaded(modid);
                                                                    });
    public static final IConditionSerializer CONDITION_ITEM_EXISTS = condition("item_exists", json -> {
                                                                        String itemName = JSONUtils.getString(json, "item");
                                                                        return () -> ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
                                                                    });
    public static final IConditionSerializer CONDITION_NOT = condition("not", json -> {
                                                                BooleanSupplier child = CraftingHelper.getCondition(JSONUtils.getJsonObject(json, "value"));
                                                                return () -> !child.getAsBoolean();
                                                            });
    public static final IConditionSerializer CONDITION_OR = condition("or", json -> {
                                                                JsonArray values = JSONUtils.getJsonArray(json, "values");
                                                                List<BooleanSupplier> children = Lists.newArrayList();
                                                                for (JsonElement j : values)
                                                                {
                                                                    if (!j.isJsonObject())
                                                                        throw new JsonSyntaxException("Or condition values must be an array of JsonObjects");
                                                                    children.add(CraftingHelper.getCondition(j.getAsJsonObject()));
                                                                }
                                                                return () -> children.stream().anyMatch(BooleanSupplier::getAsBoolean);
                                                            });
    public static final IConditionSerializer CONDITION_AND = condition("and", json -> {
                                                                JsonArray values = JSONUtils.getJsonArray(json, "values");
                                                                List<BooleanSupplier> children = Lists.newArrayList();
                                                                for (JsonElement j : values)
                                                                {
                                                                    if (!j.isJsonObject())
                                                                        throw new JsonSyntaxException("And condition values must be an array of JsonObjects");
                                                                    children.add(CraftingHelper.getCondition(j.getAsJsonObject()));
                                                                }
                                                                return () -> children.stream().allMatch(c -> c.getAsBoolean());
                                                            });
    public static final IConditionSerializer CONDITION_FALSE = condition("false", json -> () -> false);

    public static final IIngredientSerializer<IngredientNBT> INGREDIENT_NBT = register(new ResourceLocation("forge", "nbt"), new IngredientNBT.Serializer());
    public static final IIngredientSerializer<CompoundIngredient> INGREDIENT_COMPOUND = register(new ResourceLocation("forge", "compound"), new CompoundIngredient.Serializer());
    public static final IIngredientSerializer<Ingredient> INGREDIENT_VANILLA = register(new ResourceLocation("minecraft", "item"), new IIngredientSerializer<Ingredient>() {
                        public Ingredient parse(PacketBuffer buffer) {
                            return Ingredient.fromItemListStream(Stream.generate(() -> new Ingredient.SingleItemList(buffer.readItemStack())).limit(buffer.readVarInt()));
                        }

                        public Ingredient parse(JsonObject json) {
                           return Ingredient.fromItemListStream(Stream.of(Ingredient.deserializeItemList(json)));
                        }

                        public void write(PacketBuffer buffer, Ingredient ingredient) {
                            ItemStack[] items = ingredient.getMatchingStacks();
                            buffer.writeVarInt(items.length);

                            for (ItemStack stack : items)
                                buffer.writeItemStack(stack);
                        }
                    });

    public static IConditionSerializer register(ResourceLocation key, IConditionSerializer serializer)
    {
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

    private static IConditionSerializer condition(String name, IConditionSerializer serializer) {
        return register(new ResourceLocation("forge", name), serializer);
    }

    public static <T extends Ingredient> void write(PacketBuffer buffer, T ingredient)
    {
        @SuppressWarnings("unchecked") //I wonder if there is a better way generic wise...
        IIngredientSerializer<T> serializer = (IIngredientSerializer<T>)ingredient.getSerializer();
        ResourceLocation key = ingredients.inverse().get(serializer);
        if (key == null)
            throw new IllegalArgumentException("Tried to serialize unregistered Ingredient: " + ingredient + " " + serializer);
        if (serializer != INGREDIENT_VANILLA)
        {
            buffer.writeVarInt(-1); //Marker to know there is a custom ingredient
            buffer.writeResourceLocation(key);
        }
        serializer.write(buffer, ingredient);
    }

    public static Ingredient getIngredient(ResourceLocation type, PacketBuffer buffer)
    {
        IIngredientSerializer<?> serializer = ingredients.get(type);
        if (serializer == null)
            throw new IllegalArgumentException("Can not deserialize unknown Ingredient type: " + type);
        return serializer.parse(buffer);
    }

    public static Ingredient getIngredient(JsonElement json)
    {
        if (json == null || json.isJsonNull())
            throw new JsonSyntaxException("Json cannot be null");

        if (json.isJsonArray())
        {
            List<Ingredient> ingredients = Lists.newArrayList();
            List<Ingredient> vanilla = Lists.newArrayList();
            json.getAsJsonArray().forEach((ele) ->
            {
                Ingredient ing = CraftingHelper.getIngredient(ele);

                if (ing.getClass() == Ingredient.class) //Vanilla, Due to how we read it splits each itemstack, so we pull out to re-merge later
                    vanilla.add(ing);
                else
                    ingredients.add(ing);
            });

            if (!vanilla.isEmpty())
                ingredients.add(Ingredient.merge(vanilla));

            if (ingredients.size() == 0)
                throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");

            if (ingredients.size() == 1)
                return ingredients.get(0);

            return new CompoundIngredient(ingredients);
        }

        if (!json.isJsonObject())
            throw new JsonSyntaxException("Expcted ingredient to be a object or array of objects");

        JsonObject obj = (JsonObject)json;

        String type = JSONUtils.getString(obj, "type", "minecraft:item");
        if (type.isEmpty())
            throw new JsonSyntaxException("Ingredient type can not be an empty string");

        IIngredientSerializer<?> serializer = ingredients.get(new ResourceLocation(type));
        if (serializer == null)
            throw new JsonSyntaxException("Unknown ingredient type: " + type);

        return serializer.parse(obj);
    }

    public static ItemStack getItemStack(JsonObject json, boolean readNBT)
    {
        String itemName = JSONUtils.getString(json, "item");

        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));

        if (item == null)
            throw new JsonSyntaxException("Unknown item '" + itemName + "'");

        if (readNBT && json.has("nbt"))
        {
            // Lets hope this works? Needs test
            try
            {
                JsonElement element = json.get("nbt");
                CompoundNBT nbt;
                if(element.isJsonObject())
                    nbt = JsonToNBT.getTagFromJson(GSON.toJson(element));
                else
                    nbt = JsonToNBT.getTagFromJson(JSONUtils.getString(element, "nbt"));

                CompoundNBT tmp = new CompoundNBT();
                if (nbt.contains("ForgeCaps"))
                {
                    tmp.put("ForgeCaps", nbt.get("ForgeCaps"));
                    nbt.remove("ForgeCaps");
                }

                tmp.put("tag", nbt);
                tmp.putString("id", itemName);
                tmp.putInt("Count", JSONUtils.getInt(json, "count", 1));

                return ItemStack.read(tmp);
            }
            catch (CommandSyntaxException e)
            {
                throw new JsonSyntaxException("Invalid NBT Entry: " + e.toString());
            }
        }

        return new ItemStack(item, JSONUtils.getInt(json, "count", 1));
    }

    public static boolean processConditions(JsonObject json, String memberName)
    {
        return !json.has(memberName) || processConditions(JSONUtils.getJsonArray(json, memberName));
    }

    public static boolean processConditions(JsonArray conditions)
    {
        for (int x = 0; x < conditions.size(); x++)
        {
            if (!conditions.get(x).isJsonObject())
                throw new JsonSyntaxException("Conditions must be an array of JsonObjects");

            JsonObject json = conditions.get(x).getAsJsonObject();
            BooleanSupplier cond = CraftingHelper.getCondition(json);
            if (!cond.getAsBoolean())
                return false;
        }
        return true;
    }

    public static BooleanSupplier getCondition(JsonObject json)
    {
        ResourceLocation type = new ResourceLocation(JSONUtils.getString(json, "type"));
        IConditionSerializer serrializer = conditions.get(type);
        if (serrializer == null)
            throw new JsonSyntaxException("Unknown condition type: " + type.toString());
        return serrializer.parse(json);
    }

    @Nullable
    public static IItemList getConstant(ResourceLocation key) {
        return constants.get(key);
    }

    public static void reloadConstants(IResourceManager manager) {
        Map<ResourceLocation, IItemList> tmp = new HashMap<>();
        for(ResourceLocation key : manager.getAllResourceLocations("recipes", filename -> filename.equals("_constants.json")))
        {
            String path = key.getPath();
            if (!path.equals("recipes/_constants.json")) //Top level only
                continue;

            tmp.putAll(loadConstants(manager, key));
        }
        constants = tmp;
    }

    public static Map<ResourceLocation, IItemList> loadConstants(IResourceManager manager, ResourceLocation key) {
        Map<ResourceLocation, IItemList> tmp = new HashMap<>();
        try (IResource iresource = manager.getResource(key))
        {
            JsonObject[] elements = JSONUtils.fromJson(GSON, IOUtils.toString(iresource.getInputStream(), StandardCharsets.UTF_8), JsonObject[].class);
            for (int x = 0; x < elements.length; x++)
            {
                JsonObject json = elements[x];
                //Force namespace to the directory that this constants file is in, to prevent modders from overriding other's sneakily
                //TODO: Move back to a resource pack/mod specific constant list?
                ResourceLocation name = json.has("name") ? new ResourceLocation(JSONUtils.getString(json, "name")) : null;
                if (name != null)
                    name = new ResourceLocation(key.getNamespace(), name.getPath());

                if (json == null || json.size() == 0)
                    LOGGER.error(CRAFTHELPER, "Couldn't load constant #{} from {} as it's null or empty", x, key);
                else if (!processConditions(json, "conditions"))
                    LOGGER.info(CRAFTHELPER, "Skipping loading constant #{} from {} as it's conditions were not met", x, key);
                else if (name == null)
                    LOGGER.error(CRAFTHELPER, "Couldn't load constant #{} from {} as it's missing `name`", x, key);
                else if (json.has("items"))
                {
                    List<ItemStack> items = new ArrayList<>();
                    for (JsonElement item : JSONUtils.getJsonArray(json, "items"))
                    {
                        if (item.isJsonObject())
                            items.add(getItemStack(item.getAsJsonObject(), true));
                        else
                        {
                            LOGGER.error(CRAFTHELPER, "Couldn't load constant #{} from {} as it's `items` entry is not a object", x, key);
                            items.clear();
                            break;
                        }
                    }
                    if (!items.isEmpty())
                        tmp.put(name, new StackList(items));
                }
                else if (json.has("tag"))
                    tmp.put(name, Ingredient.deserializeItemList(json));
                else if (json.has("item"))
                    tmp.put(name, new StackList(Lists.newArrayList(getItemStack(JSONUtils.getJsonObject(json, "item"), true))));
                else
                    LOGGER.error(CRAFTHELPER, "Couldn't load constant #{} from {} as it's missing `item` or `items` element", x, key);
            }

        }
        catch (IllegalArgumentException | JsonParseException e)
        {
           LOGGER.error(CRAFTHELPER, "Parsing error loading constants {}", key, e);
        }
        catch (IOException e)
        {
           LOGGER.error(CRAFTHELPER, "Couldn't read constants from {}", key, e);
        }
        return tmp;
    }
}
