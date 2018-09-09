/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

import javax.annotation.Nonnull;

import net.minecraft.client.util.RecipeBookClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import org.apache.commons.io.FilenameUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.RegistryManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class CraftingHelper {

    private static final boolean DEBUG_LOAD_MINECRAFT = false;
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker CRAFTHELPER = MarkerManager.getMarker("CRAFTHELPER");
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static Map<ResourceLocation, IConditionFactory> conditions = new HashMap<>();
    private static Map<ResourceLocation, IIngredientFactory> ingredients = new HashMap<>();
    private static Map<ResourceLocation, IRecipeFactory> recipes = new HashMap<>();

    static {
        init();
    }

    public static void register(ResourceLocation key, IConditionFactory factory)
    {
        if (conditions.containsKey(key))
            throw new IllegalStateException("Duplicate recipe condition factory: " + key);
        conditions.put(key, factory);
    }
    public static void register(ResourceLocation key, IRecipeFactory factory)
    {
        if (recipes.containsKey(key))
            throw new IllegalStateException("Duplicate recipe factory: " + key);
        recipes.put(key, factory);
    }
    public static void register(ResourceLocation key, IIngredientFactory factory)
    {
        if (ingredients.containsKey(key))
            throw new IllegalStateException("Duplicate recipe ingredient factory: " + key);
        ingredients.put(key, factory);
    }


    public static Ingredient getIngredient(Object obj)
    {
        if (obj instanceof Ingredient)
            return (Ingredient)obj;
        else if (obj instanceof ItemStack)
            return Ingredient.fromStacks(((ItemStack)obj).copy());
        else if (obj instanceof Item)
            return Ingredient.fromItem((Item)obj);
        else if (obj instanceof Block)
            return Ingredient.fromStacks(new ItemStack((Block)obj, 1, OreDictionary.WILDCARD_VALUE));
        else if (obj instanceof String)
            return new OreIngredient((String)obj);
        else if (obj instanceof JsonElement)
            throw new IllegalArgumentException("JsonObjects must use getIngredient(JsonObject, JsonContext)");

        return null;
    }

    @Nonnull
    public static Ingredient getIngredient(JsonElement json, JsonContext context)
    {
        if (json == null || json.isJsonNull())
            throw new JsonSyntaxException("Json cannot be null");
        if (context == null)
            throw new IllegalArgumentException("getIngredient Context cannot be null");

        if (json.isJsonArray())
        {
            List<Ingredient> ingredients = Lists.newArrayList();
            List<Ingredient> vanilla = Lists.newArrayList();
            json.getAsJsonArray().forEach((ele) ->
            {
                Ingredient ing = CraftingHelper.getIngredient(ele, context);

                if (ing.getClass() == Ingredient.class)
                {
                    //Vanilla, Due to how we read it splits each itemstack, so we pull out to re-merge later
                    vanilla.add(ing);
                }
                else
                {
                    ingredients.add(ing);
                }
            });

            if (!vanilla.isEmpty())
            {
                ingredients.add(Ingredient.merge(vanilla));
            }

            if (ingredients.size() == 0)
                throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");

            if (ingredients.size() == 1)
                return ingredients.get(0);

            return new CompoundIngredient(ingredients);
        }

        if (!json.isJsonObject())
            throw new JsonSyntaxException("Expcted ingredient to be a object or array of objects");

        JsonObject obj = (JsonObject)json;

        String type = context.appendModId(JsonUtils.getString(obj, "type", "minecraft:item"));
        if (type.isEmpty())
            throw new JsonSyntaxException("Ingredient type can not be an empty string");

        if (type.equals("minecraft:item"))
        {
            String item = JsonUtils.getString(obj, "item");
            if (item.startsWith("#"))
            {
                Ingredient constant = context.getConstant(item.substring(1));
                if (constant == null)
                    throw new JsonSyntaxException("Ingredient referenced invalid constant: " + item);
                return constant;
            }
        }

        IIngredientFactory factory = ingredients.get(new ResourceLocation(type));
        if (factory == null)
            throw new JsonSyntaxException("Unknown ingredient type: " + type);

        return factory.parse(context, obj);
    }

    public static ItemStack getItemStack(JsonObject json, JsonContext context)
    {
        String itemName = context.appendModId(JsonUtils.getString(json, "item"));

        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));

        if (item == null)
            throw new JsonSyntaxException("Unknown item '" + itemName + "'");

        if (item.getHasSubtypes() && !json.has("data"))
            throw new JsonParseException("Missing data for item '" + itemName + "'");

        if (json.has("nbt"))
        {
            // Lets hope this works? Needs test
            try
            {
                JsonElement element = json.get("nbt");
                NBTTagCompound nbt;
                if(element.isJsonObject())
                    nbt = JsonToNBT.getTagFromJson(GSON.toJson(element));
                else
                    nbt = JsonToNBT.getTagFromJson(element.getAsString());

                NBTTagCompound tmp = new NBTTagCompound();
                if (nbt.hasKey("ForgeCaps"))
                {
                    tmp.setTag("ForgeCaps", nbt.getTag("ForgeCaps"));
                    nbt.removeTag("ForgeCaps");
                }

                tmp.setTag("tag", nbt);
                tmp.setString("id", itemName);
                tmp.setInteger("Count", JsonUtils.getInt(json, "count", 1));
                tmp.setInteger("Damage", JsonUtils.getInt(json, "data", 0));

                return new ItemStack(tmp);
            }
            catch (NBTException e)
            {
                throw new JsonSyntaxException("Invalid NBT Entry: " + e.toString());
            }
        }

        return new ItemStack(item, JsonUtils.getInt(json, "count", 1), JsonUtils.getInt(json, "data", 0));
    }


    public static ItemStack getItemStackBasic(JsonObject json, JsonContext context)
    {
        String itemName = context.appendModId(JsonUtils.getString(json, "item"));

        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));

        if (item == null)
            throw new JsonSyntaxException("Unknown item '" + itemName + "'");

        if (item.getHasSubtypes() && !json.has("data"))
            throw new JsonParseException("Missing data for item '" + itemName + "'");

        return new ItemStack(item, 1, JsonUtils.getInt(json, "data", 0));
    }

    public static class ShapedPrimer {
        public int height, width;
        public boolean mirrored = true;
        public NonNullList<Ingredient> input;
    }

    public static ShapedPrimer parseShaped(Object... recipe)
    {
        ShapedPrimer ret = new ShapedPrimer();
        String shape = "";
        int idx = 0;

        if (recipe[idx] instanceof Boolean)
        {
            ret.mirrored = (Boolean)recipe[idx];
            if (recipe[idx+1] instanceof Object[])
                recipe = (Object[])recipe[idx+1];
            else
                idx = 1;
        }

        if (recipe[idx] instanceof String[])
        {
            String[] parts = ((String[])recipe[idx++]);

            for (String s : parts)
            {
                ret.width = s.length();
                shape += s;
            }

            ret.height = parts.length;
        }
        else
        {
            while (recipe[idx] instanceof String)
            {
                String s = (String)recipe[idx++];
                shape += s;
                ret.width = s.length();
                ret.height++;
            }
        }

        if (ret.width * ret.height != shape.length() || shape.length() == 0)
        {
            String err = "Invalid shaped recipe: ";
            for (Object tmp :  recipe)
            {
                err += tmp + ", ";
            }
            throw new RuntimeException(err);
        }

        HashMap<Character, Ingredient> itemMap = Maps.newHashMap();
        itemMap.put(' ', Ingredient.EMPTY);

        for (; idx < recipe.length; idx += 2)
        {
            Character chr = (Character)recipe[idx];
            Object in = recipe[idx + 1];
            Ingredient ing = CraftingHelper.getIngredient(in);

            if (' ' == chr.charValue())
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");

            if (ing != null)
            {
                itemMap.put(chr, ing);
            }
            else
            {
                String err = "Invalid shaped ore recipe: ";
                for (Object tmp :  recipe)
                {
                    err += tmp + ", ";
                }
                throw new RuntimeException(err);
            }
        }

        ret.input = NonNullList.withSize(ret.width * ret.height, Ingredient.EMPTY);

        Set<Character> keys = Sets.newHashSet(itemMap.keySet());
        keys.remove(' ');

        int x = 0;
        for (char chr : shape.toCharArray())
        {
            Ingredient ing = itemMap.get(chr);
            if (ing == null)
                throw new IllegalArgumentException("Pattern references symbol '" + chr + "' but it's not defined in the key");
            ret.input.set(x++, ing);
            keys.remove(chr);
        }

        if (!keys.isEmpty())
            throw new IllegalArgumentException("Key defines symbols that aren't used in pattern: " + keys);

        return ret;
    }

    public static boolean processConditions(JsonArray conditions, JsonContext context)
    {
        for (int x = 0; x < conditions.size(); x++)
        {
            if (!conditions.get(x).isJsonObject())
                throw new JsonSyntaxException("Conditions must be an array of JsonObjects");

            JsonObject json = conditions.get(x).getAsJsonObject();
            BooleanSupplier cond = CraftingHelper.getCondition(json, context);
            if (!cond.getAsBoolean())
                return false;
        }
        return true;
    }

    public static BooleanSupplier getCondition(JsonObject json, JsonContext context)
    {
        ResourceLocation type = new ResourceLocation(context.appendModId(JsonUtils.getString(json, "type")));
        IConditionFactory factory = conditions.get(type);
        if (factory == null)
            throw new JsonSyntaxException("Unknown condition type: " + type.toString());
        return factory.parse(context, json);
    }

    public static IRecipe getRecipe(JsonObject json, JsonContext context)
    {
        if (json == null || json.isJsonNull())
            throw new JsonSyntaxException("Json cannot be null");
        if (context == null)
            throw new IllegalArgumentException("getRecipe Context cannot be null");

        String type = context.appendModId(JsonUtils.getString(json, "type"));
        if (type.isEmpty())
            throw new JsonSyntaxException("Recipe type can not be an empty string");

        IRecipeFactory factory = recipes.get(new ResourceLocation(type));
        if (factory == null)
            throw new JsonSyntaxException("Unknown recipe type: " + type);

        return factory.parse(context, json);
    }


    //=======================================================
    // INTERNAL
    //=======================================================

    private static void init()
    {
        conditions.clear();
        ingredients.clear();
        recipes.clear();

        registerC("forge:mod_loaded", (context, json) -> {
            String modid = JsonUtils.getString(json, "modid");
            return () -> ModList.get().isLoaded(modid);
        });
        registerC("minecraft:item_exists", (context, json) -> {
            String itemName = context.appendModId(JsonUtils.getString(json, "item"));
            return () -> ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
        });
        registerC("forge:not", (context, json) -> {
            BooleanSupplier child = CraftingHelper.getCondition(JsonUtils.getJsonObject(json, "value"), context);
            return () -> !child.getAsBoolean();
        });
        registerC("forge:or", (context, json) -> {
            JsonArray values = JsonUtils.getJsonArray(json, "values");
            List<BooleanSupplier> children = Lists.newArrayList();
            for (JsonElement j : values)
            {
                if (!j.isJsonObject())
                    throw new JsonSyntaxException("Or condition values must be an array of JsonObjects");
                children.add(CraftingHelper.getCondition(j.getAsJsonObject(), context));
            }
            return () -> children.stream().anyMatch(BooleanSupplier::getAsBoolean);
        });
        registerC("forge:and", (context, json) -> {
            JsonArray values = JsonUtils.getJsonArray(json, "values");
            List<BooleanSupplier> children = Lists.newArrayList();
            for (JsonElement j : values)
            {
                if (!j.isJsonObject())
                    throw new JsonSyntaxException("And condition values must be an array of JsonObjects");
                children.add(CraftingHelper.getCondition(j.getAsJsonObject(), context));
            }
            return () -> children.stream().allMatch(c -> c.getAsBoolean());
        });
        registerC("forge:false", (context, json) -> {
            return () -> false;
        });

        registerR("minecraft:crafting_shaped", (context, json) -> {
            String group = JsonUtils.getString(json, "group", "");
            //if (!group.isEmpty() && group.indexOf(':') == -1)
            //    group = context.getModId() + ":" + group;

            Map<Character, Ingredient> ingMap = Maps.newHashMap();
            for (Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "key").entrySet())
            {
                if (entry.getKey().length() != 1)
                    throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
                if (" ".equals(entry.getKey()))
                    throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");

                ingMap.put(entry.getKey().toCharArray()[0], CraftingHelper.getIngredient(entry.getValue(), context));
            }
            ingMap.put(' ', Ingredient.EMPTY);

            JsonArray patternJ = JsonUtils.getJsonArray(json, "pattern");

            if (patternJ.size() == 0)
                throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
            if (patternJ.size() > 3)
                throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");

            String[] pattern = new String[patternJ.size()];
            for (int x = 0; x < pattern.length; ++x)
            {
                String line = JsonUtils.getString(patternJ.get(x), "pattern[" + x + "]");
                if (line.length() > 3)
                    throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
                if (x > 0 && pattern[0].length() != line.length())
                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                pattern[x] = line;
            }

            NonNullList<Ingredient> input = NonNullList.withSize(pattern[0].length() * pattern.length, Ingredient.EMPTY);
            Set<Character> keys = Sets.newHashSet(ingMap.keySet());
            keys.remove(' ');

            int x = 0;
            for (String line : pattern)
            {
                for (char chr : line.toCharArray())
                {
                    Ingredient ing = ingMap.get(chr);
                    if (ing == null)
                        throw new JsonSyntaxException("Pattern references symbol '" + chr + "' but it's not defined in the key");
                    input.set(x++, ing);
                    keys.remove(chr);
                }
            }

            if (!keys.isEmpty())
                throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + keys);

            ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);
            return new ShapedRecipes(group, pattern[0].length(), pattern.length, input, result);
        });
        registerR("minecraft:crafting_shapeless", (context, json) -> {
            String group = JsonUtils.getString(json, "group", "");

            NonNullList<Ingredient> ings = NonNullList.create();
            for (JsonElement ele : JsonUtils.getJsonArray(json, "ingredients"))
                ings.add(CraftingHelper.getIngredient(ele, context));

            if (ings.isEmpty())
                throw new JsonParseException("No ingredients for shapeless recipe");
            if (ings.size() > 9)
                throw new JsonParseException("Too many ingredients for shapeless recipe");

            ItemStack itemstack = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);
            return new ShapelessRecipes(group, itemstack, ings);
        });
        registerR("forge:ore_shaped", ShapedOreRecipe::factory);
        registerR("forge:ore_shapeless", ShapelessOreRecipe::factory);

        registerI("minecraft:item", (context, json) -> Ingredient.fromStacks(CraftingHelper.getItemStackBasic(json, context)));
        registerI("minecraft:empty", (context, json) -> Ingredient.EMPTY);
        registerI("minecraft:item_nbt", (context, json) -> new IngredientNBT(CraftingHelper.getItemStack(json, context)));
        registerI("forge:ore_dict", (context, json) -> new OreIngredient(JsonUtils.getString(json, "ore")));
    }

    private static void registerC(String name, IConditionFactory fac) {
        register(new ResourceLocation(name), fac);
    }
    private static void registerR(String name, IRecipeFactory fac) {
        register(new ResourceLocation(name), fac);
    }
    private static void registerI(String name, IIngredientFactory fac) {
        register(new ResourceLocation(name), fac);
    }

    private static void loadFactories(JsonObject json, JsonContext context)
    {
        if (json.has("ingredients"))
        {
            for (Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "ingredients").entrySet())
            {
                ResourceLocation key = new ResourceLocation(context.getModId(), entry.getKey());
                String clsName = JsonUtils.getString(entry.getValue(), "ingredients[" + entry.getValue() + "]");
                register(key, getClassInstance(clsName, IIngredientFactory.class));
            }
        }

        if (json.has("recipes"))
        {
            for (Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "recipes").entrySet())
            {
                ResourceLocation key = new ResourceLocation(context.getModId(), entry.getKey());
                String clsName = JsonUtils.getString(entry.getValue(), "recipes[" + entry.getValue() + "]");
                register(key, getClassInstance(clsName, IRecipeFactory.class));
            }
        }

        if (json.has("conditions"))
        {
            for (Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "conditions").entrySet())
            {
                ResourceLocation key = new ResourceLocation(context.getModId(), entry.getKey());
                String clsName = JsonUtils.getString(entry.getValue(), "conditions[" + entry.getValue() + "]");
                register(key, getClassInstance(clsName, IConditionFactory.class));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T getClassInstance(String clsName, Class<T> expected)
    {
        try
        {
            Class<?> cls = Class.forName(clsName, true, Thread.currentThread().getContextClassLoader());
            if (!expected.isAssignableFrom(cls))
                throw new JsonSyntaxException("Class '" + clsName + "' is not an " + expected.getSimpleName());
            return (T)cls.newInstance();
        }
        catch (ClassNotFoundException e)
        {
            throw new JsonSyntaxException("Could not find " + expected.getSimpleName() + ": " + clsName, e);
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            throw new JsonSyntaxException("Could not instantiate " + expected.getSimpleName() + ": " + clsName, e);
        }
    }

    public static void loadRecipes(boolean revertFrozen)
    {
        //TODO: If this errors in ServerInit it freezes the client at loading world, find a way to pop that up?
        //TODO: Figure out how to remove recipes, and override them. This relies on cpw to help.
        //For now this is only done one after mod init, I want to move this to ServerInit and re-do it many times.
        init();
        ForgeRegistry<IRecipe> reg = (ForgeRegistry<IRecipe>)ForgeRegistries.RECIPES;
        //reg.unfreeze();
        if (DEBUG_LOAD_MINECRAFT)
            reg.clear();
        else if (revertFrozen)
            GameData.revert(RegistryManager.FROZEN, GameData.RECIPES, false);

        ModList.get().forEachModFile(CraftingHelper::loadFactories);
        ModList.get().forEachModFile(CraftingHelper::loadRecipes);

        GameData.fireRegistryEvents(rl -> rl.equals(GameData.RECIPES));

        //reg.freeze();
        DistExecutor.runWhenOn(Dist.CLIENT, ()-> RecipeBookClient::rebuildTable);
    }

    private static void loadFactories(final ModFile modFile)
    {
        modFile.getModInfos().forEach(modInfo-> {
            final String modId = modInfo.getModId();
            JsonContext ctx = new JsonContext(modId);
            final Path fPath = modFile.getLocator().findPath(modFile, "assets", modId, "recipes","_factories.json");
            if (fPath != null && Files.exists(fPath))
            {
                try (final BufferedReader reader = Files.newBufferedReader(fPath))
                {
                    JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);
                    loadFactories(json, ctx);
                }
                catch (final IOException e)
                {
                    LOGGER.error(CRAFTHELPER,"Encountered error reading recipe factories for {}", modId, e);
                }
            }
        });
    }

    private static boolean loadRecipes(final ModFile modFile)
    {
        final AtomicBoolean errored = new AtomicBoolean(false);
        modFile.getModInfos().forEach(modInfo-> {
            final String modId = modInfo.getModId();
            JsonContext ctx = new JsonContext(modId);
            final Path root = modFile.getLocator().findPath(modFile, "assets", modId, "recipes");
            if (!Files.exists(root)) return;
            final Path constants = modFile.getLocator().findPath(modFile, "assets", modId, "recipes", "_constants.json");
            if (Files.exists(constants))
            {
                try (BufferedReader reader = Files.newBufferedReader(constants))
                {
                    JsonObject[] json = JsonUtils.fromJson(GSON, reader, JsonObject[].class);
                    ctx.loadConstants(json);
                }
                catch (final IOException e)
                {
                    LOGGER.error(CRAFTHELPER, "Error loading _constants.json: ", e);
                    errored.set(true);
                }
            }
            try
            {
                Files.walk(root).
                        filter(p -> p.getFileName().toString().startsWith("_") || Objects.equals(FilenameUtils.getExtension(p.getFileName().toString()), "json")).
                        forEach(p -> {
                            final String relative = root.relativize(p).toString();
                            final String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/");
                            final ResourceLocation key = new ResourceLocation(modId, name);
                            try (BufferedReader reader = Files.newBufferedReader(constants))
                            {
                                JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);
                                if (json.has("conditions") && !CraftingHelper.processConditions(JsonUtils.getJsonArray(json, "conditions"), ctx))
                                    return;
                                IRecipe recipe = CraftingHelper.getRecipe(json, ctx);
                                ForgeRegistries.RECIPES.register(recipe.setRegistryName(key));
                            }
                            catch (final JsonParseException e)
                            {
                                LOGGER.error(CRAFTHELPER, "Parsing error loading recipe {}", key, e);
                                errored.set(true);
                            }
                            catch (final IOException e)
                            {
                                LOGGER.error(CRAFTHELPER, "Couldn't read recipe {} from {}", key, p, e);
                                errored.set(true);
                            }

                        });
            }
            catch (final IOException e)
            {
                LOGGER.error(CRAFTHELPER, "Error occurred walking file tree", e);
                errored.set(true);
            }
        });
        return errored.get();
    }
}
