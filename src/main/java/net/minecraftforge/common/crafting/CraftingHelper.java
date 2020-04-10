/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

import javax.annotation.Nonnull;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

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
import net.minecraft.item.crafting.CraftingManager;
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
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.RegistryManager;

public class CraftingHelper {

    private static final boolean DEBUG_LOAD_MINECRAFT = false;
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Map<ResourceLocation, IConditionFactory> conditions = Maps.newHashMap();
    private static final Map<ResourceLocation, IIngredientFactory> ingredients = Maps.newHashMap();
    private static final Map<ResourceLocation, IRecipeFactory> recipes = Maps.newHashMap();

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
                    nbt = JsonToNBT.getTagFromJson(JsonUtils.getString(element, "nbt"));

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

    public static boolean processConditions(JsonObject json, String memberName, JsonContext context)
    {
        return !json.has(memberName) || processConditions(JsonUtils.getJsonArray(json, memberName), context);
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

    public static void init()
    {
        conditions.clear();
        ingredients.clear();
        recipes.clear();

        registerC("forge:mod_loaded", (context, json) -> {
            String modid = JsonUtils.getString(json, "modid");
            return () -> Loader.isModLoaded(modid);
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

    public static final class FactoryLoader<T>
    {
        final String name;
        final Class<T> type;
        final BiConsumer<ResourceLocation, T> consumer;

        FactoryLoader(String name, Class<T> type, BiConsumer<ResourceLocation, T> consumer)
        {
            this.name = name;
            this.type = type;
            this.consumer = consumer;
        }
    }

    public static final FactoryLoader<IIngredientFactory> INGREDIENTS = new FactoryLoader<>("ingredients", IIngredientFactory.class, CraftingHelper::register);
    public static final FactoryLoader<IRecipeFactory> RECIPES = new FactoryLoader<>("recipes", IRecipeFactory.class, CraftingHelper::register);
    public static final FactoryLoader<IConditionFactory> CONDITIONS = new FactoryLoader<>("conditions", IConditionFactory.class, CraftingHelper::register);

    private static void loadFactories(JsonObject json, JsonContext context, FactoryLoader... loaders)
    {
        for (FactoryLoader<?> loader : loaders)
        {
            loadFactory(json, context, loader);
        }
    }

    private static <T> void loadFactory(JsonObject json, JsonContext context, FactoryLoader<T> loader)
    {
        if (json.has(loader.name))
        {
            for (Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, loader.name).entrySet())
            {
                ResourceLocation key = new ResourceLocation(context.getModId(), entry.getKey());
                String clsName = JsonUtils.getString(entry.getValue(), loader.name + "[" + entry.getValue() + "]");
                loader.consumer.accept(key, getClassInstance(clsName, loader.type));
            }
        }
    }

    private static <T> T getClassInstance(String clsName, Class<T> expected)
    {
        try
        {
            Class<?> cls = Class.forName(clsName);
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
        //ModContainer old = Loader.instance().activeModContainer();
        Loader.instance().setActiveModContainer(null);
        Loader.instance().getActiveModList().forEach(CraftingHelper::loadFactories);
        Loader.instance().getActiveModList().forEach(CraftingHelper::loadRecipes);
        Loader.instance().setActiveModContainer(null);

        GameData.fireRegistryEvents(rl -> rl.equals(GameData.RECIPES));

        //reg.freeze();
        FMLCommonHandler.instance().resetClientRecipeBook();
    }

    private static void loadFactories(ModContainer mod)
    {
        loadFactories(mod, "assets/" + mod.getModId() + "/recipes", INGREDIENTS, RECIPES, CONDITIONS);
    }

    public static void loadFactories(ModContainer mod, String base, FactoryLoader... loaders)
    {
        FileSystem fs = null;
        try
        {
            Path fPath = null;
            JsonContext ctx = new JsonContext(mod.getModId());

            if (mod.getSource().isFile())
            {
                fs = FileSystems.newFileSystem(mod.getSource().toPath(), (ClassLoader)null);
                fPath = fs.getPath("/" + base, "_factories.json");
            }
            else if (mod.getSource().isDirectory())
            {
                fPath = mod.getSource().toPath().resolve(base).resolve("_factories.json");
            }

            if (fPath != null && Files.exists(fPath))
            {
                try (BufferedReader reader = Files.newBufferedReader(fPath))
                {
                    JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);
                    loadFactories(json, ctx, loaders);
                }
            }
        }
        catch (JsonParseException | IOException e)
        {
            FMLLog.log.error("Error loading _factories.json: ", e);
        }
        finally
        {
            IOUtils.closeQuietly(fs);
        }
    }

    private static boolean loadRecipes(ModContainer mod)
    {
        JsonContext ctx = new JsonContext(mod.getModId());

        return findFiles(mod, "assets/" + mod.getModId() + "/recipes",
            root ->
            {
                Path fPath = root.resolve("_constants.json");
                if (fPath != null && Files.exists(fPath))
                {
                    try(BufferedReader reader = Files.newBufferedReader(fPath))
                    {
                        JsonObject[] json = JsonUtils.fromJson(GSON, reader, JsonObject[].class);
                        ctx.loadConstants(json);
                    }
                    catch (JsonParseException | IOException e)
                    {
                        FMLLog.log.error("Error loading _constants.json: ", e);
                        return false;
                    }
                }
                return true;
            },
            (root, file) ->
            {
                Loader.instance().setActiveModContainer(mod);

                String relative = root.relativize(file).toString();
                if (!"json".equals(FilenameUtils.getExtension(file.toString())) || relative.startsWith("_"))
                    return true;

                String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/");
                ResourceLocation key = new ResourceLocation(ctx.getModId(), name);

                try(BufferedReader reader = Files.newBufferedReader(file))
                {
                    JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);
                    if (!processConditions(json, "conditions", ctx))
                        return true;
                    IRecipe recipe = CraftingHelper.getRecipe(json, ctx);
                    ForgeRegistries.RECIPES.register(recipe.setRegistryName(key));
                }
                catch (JsonParseException e)
                {
                    FMLLog.log.error("Parsing error loading recipe {}", key, e);
                    return false;
                }
                catch (IOException e)
                {
                    FMLLog.log.error("Couldn't read recipe {} from {}", key, file, e);
                    return false;
                }
                return true;
            },
            true, true
        );
    }

    /**
     * @deprecated Use {@link CraftingHelper#findFiles(ModContainer, String, Function, BiFunction, boolean, boolean)} instead.
     */
    @Deprecated
    public static boolean findFiles(ModContainer mod, String base, Function<Path, Boolean> preprocessor, BiFunction<Path, Path, Boolean> processor)
    {
        return findFiles(mod, base, preprocessor, processor, false, false);
    }

    /**
     * @deprecated Use {@link CraftingHelper#findFiles(ModContainer, String, Function, BiFunction, boolean, boolean)} instead.
     */
    @Deprecated
    public static boolean findFiles(ModContainer mod, String base, Function<Path, Boolean> preprocessor, BiFunction<Path, Path, Boolean> processor, boolean defaultUnfoundRoot)
    {
        return findFiles(mod, base, preprocessor, processor, defaultUnfoundRoot, false);
    }

    public static boolean findFiles(ModContainer mod, String base, Function<Path, Boolean> preprocessor, BiFunction<Path, Path, Boolean> processor,
            boolean defaultUnfoundRoot, boolean visitAllFiles)
    {

        File source = mod.getSource();

        if ("minecraft".equals(mod.getModId()))
        {
            if (!DEBUG_LOAD_MINECRAFT)
                return true;

            try
            {
                URI tmp = CraftingManager.class.getResource("/assets/.mcassetsroot").toURI();
                source = new File(tmp.resolve("..").getPath());
            }
            catch (URISyntaxException e)
            {
                FMLLog.log.error("Error finding Minecraft jar: ", e);
                return false;
            }
        }

        FileSystem fs = null;
        boolean success = true;

        try
        {
            Path root = null;

            if (source.isFile())
            {
                try
                {
                    fs = FileSystems.newFileSystem(source.toPath(), (ClassLoader)null);
                    root = fs.getPath("/" + base);
                }
                catch (IOException e)
                {
                    FMLLog.log.error("Error loading FileSystem from jar: ", e);
                    return false;
                }
            }
            else if (source.isDirectory())
            {
                root = source.toPath().resolve(base);
            }
    
            if (root == null || !Files.exists(root))
                return defaultUnfoundRoot;
    
            if (preprocessor != null)
            {
                Boolean cont = preprocessor.apply(root);
                if (cont == null || !cont.booleanValue())
                    return false;
            }
        
            if (processor != null)
            {
                Iterator<Path> itr = null;
                try
                {
                    itr = Files.walk(root).iterator();
                }
                catch (IOException e)
                {
                    FMLLog.log.error("Error iterating filesystem for: {}", mod.getModId(), e);
                    return false;
                }
    
                while (itr != null && itr.hasNext())
                {
                    Boolean cont = processor.apply(root, itr.next());
    
                    if (visitAllFiles)
                    {
                        success &= cont != null && cont;
                    }
                    else if (cont == null || !cont)
                    {
                        return false;
                    }
                }
            }
        }
        finally
        {
            IOUtils.closeQuietly(fs);
        }

        return success;
    }

    public static JsonContext loadContext(ResourceLocation path) throws IOException
    {
        ModContainer mod = Loader.instance().activeModContainer();
        if(mod == null)
        {
            throw new IllegalStateException("No active mod container");
        }
        return loadContext(path, mod);
     }
    
    public static JsonContext loadContext(ResourceLocation path, ModContainer mod) throws IOException
    {
        return loadContext(mod, new JsonContext(mod.getModId()), path);
    }

    private static JsonContext loadContext(JsonContext ctx, File file) throws IOException
    {
        try(BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            JsonObject[] json = JsonUtils.fromJson(GSON, reader, JsonObject[].class);
            ctx.loadConstants(json);
            return ctx;
        }
        catch (IOException e)
        {
            throw new IOException("Error loading constants from file: " + file.getAbsolutePath(), e);
        }
    }

    private static JsonContext loadContext(ModContainer mod, JsonContext ctx, ResourceLocation path) throws IOException
    {
        Path fPath = null;
        if(mod.getSource().isFile())
        {
            try(FileSystem fs = FileSystems.newFileSystem(mod.getSource().toPath(), (ClassLoader)null))
            {
                fPath = fs.getPath("assets", path.getResourceDomain(), path.getResourcePath());
            }
        }
        else if (mod.getSource().isDirectory())
        {
            fPath = mod.getSource().toPath().resolve(Paths.get("assets", path.getResourceDomain(), path.getResourcePath()));
        }

        if (fPath != null && Files.exists(fPath))
        {
            return loadContext(ctx, fPath.toFile());
        } 
        else 
        {
            throw new FileNotFoundException(fPath != null ? fPath.toString() : path.toString());
        }
    }
}
