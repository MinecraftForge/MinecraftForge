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

package net.minecraftforge.oredict;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.BlockPrismarine;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.common.MinecraftForge;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.registry.GameData;

public class OreDictionary
{
    private static boolean hasInit = false;
    private static List<String>          idToName = new ArrayList<String>();
    private static Map<String, Integer>  nameToId = new HashMap<String, Integer>(128);
    private static List<List<ItemStack>> idToStack = Lists.newArrayList();
    private static List<List<ItemStack>> idToStackUn = Lists.newArrayList();
    private static Map<Integer, List<Integer>> stackToId = Maps.newHashMapWithExpectedSize((int)(128 * 0.75));
    public static final ImmutableList<ItemStack> EMPTY_LIST = ImmutableList.of();

    /**
     * Minecraft changed from -1 to Short.MAX_VALUE in 1.5 release for the "block wildcard". Use this in case it
     * changes again.
     */
    public static final int WILDCARD_VALUE = Short.MAX_VALUE;

    static {
        initVanillaEntries();
    }

    private static void initVanillaEntries()
    {
        if (!hasInit)
        {
            // tree- and wood-related things
            registerOre("logWood",     new ItemStack(Blocks.LOG, 1, WILDCARD_VALUE));
            registerOre("logWood",     new ItemStack(Blocks.LOG2, 1, WILDCARD_VALUE));
            registerOre("plankWood",   new ItemStack(Blocks.PLANKS, 1, WILDCARD_VALUE));
            registerOre("slabWood",    new ItemStack(Blocks.WOODEN_SLAB, 1, WILDCARD_VALUE));
            registerOre("stairWood",   Blocks.OAK_STAIRS);
            registerOre("stairWood",   Blocks.SPRUCE_STAIRS);
            registerOre("stairWood",   Blocks.BIRCH_STAIRS);
            registerOre("stairWood",   Blocks.JUNGLE_STAIRS);
            registerOre("stairWood",   Blocks.ACACIA_STAIRS);
            registerOre("stairWood",   Blocks.DARK_OAK_STAIRS);
            registerOre("stickWood",   Items.STICK);
            registerOre("treeSapling", new ItemStack(Blocks.SAPLING, 1, WILDCARD_VALUE));
            registerOre("treeLeaves",  new ItemStack(Blocks.LEAVES, 1, WILDCARD_VALUE));
            registerOre("treeLeaves",  new ItemStack(Blocks.LEAVES2, 1, WILDCARD_VALUE));
            registerOre("vine",        Blocks.VINE);

            // Ores
            registerOre("oreGold",     Blocks.GOLD_ORE);
            registerOre("oreIron",     Blocks.IRON_ORE);
            registerOre("oreLapis",    Blocks.LAPIS_ORE);
            registerOre("oreDiamond",  Blocks.DIAMOND_ORE);
            registerOre("oreRedstone", Blocks.REDSTONE_ORE);
            registerOre("oreEmerald",  Blocks.EMERALD_ORE);
            registerOre("oreQuartz",   Blocks.QUARTZ_ORE);
            registerOre("oreCoal",     Blocks.COAL_ORE);

            // ingots/nuggets
            registerOre("ingotIron",     Items.IRON_INGOT);
            registerOre("ingotGold",     Items.GOLD_INGOT);
            registerOre("ingotBrick",    Items.BRICK);
            registerOre("ingotBrickNether", Items.NETHERBRICK);
            registerOre("nuggetGold",  Items.GOLD_NUGGET);

            // gems and dusts
            registerOre("gemDiamond",  Items.DIAMOND);
            registerOre("gemEmerald",  Items.EMERALD);
            registerOre("gemQuartz",   Items.QUARTZ);
            registerOre("gemPrismarine", Items.PRISMARINE_SHARD);
            registerOre("dustPrismarine", Items.PRISMARINE_CRYSTALS);
            registerOre("dustRedstone",  Items.REDSTONE);
            registerOre("dustGlowstone", Items.GLOWSTONE_DUST);
            registerOre("gemLapis",    new ItemStack(Items.DYE, 1, 4));

            // storage blocks
            registerOre("blockGold",     Blocks.GOLD_BLOCK);
            registerOre("blockIron",     Blocks.IRON_BLOCK);
            registerOre("blockLapis",    Blocks.LAPIS_BLOCK);
            registerOre("blockDiamond",  Blocks.DIAMOND_BLOCK);
            registerOre("blockRedstone", Blocks.REDSTONE_BLOCK);
            registerOre("blockEmerald",  Blocks.EMERALD_BLOCK);
            registerOre("blockQuartz",   Blocks.QUARTZ_BLOCK);
            registerOre("blockCoal",     Blocks.COAL_BLOCK);

            // crops
            registerOre("cropWheat",   Items.WHEAT);
            registerOre("cropPotato",  Items.POTATO);
            registerOre("cropCarrot",  Items.CARROT);
            registerOre("cropNetherWart", Items.NETHER_WART);
            registerOre("sugarcane",   Items.REEDS);
            registerOre("blockCactus", Blocks.CACTUS);

            // misc materials
            registerOre("dye",         new ItemStack(Items.DYE, 1, WILDCARD_VALUE));
            registerOre("paper",       new ItemStack(Items.PAPER));

            // mob drops
            registerOre("slimeball",   Items.SLIME_BALL);
            registerOre("enderpearl",  Items.ENDER_PEARL);
            registerOre("bone",        Items.BONE);
            registerOre("gunpowder",   Items.GUNPOWDER);
            registerOre("string",      Items.STRING);
            registerOre("netherStar",  Items.NETHER_STAR);
            registerOre("leather",     Items.LEATHER);
            registerOre("feather",     Items.FEATHER);
            registerOre("egg",         Items.EGG);

            // records
            registerOre("record",      Items.RECORD_13);
            registerOre("record",      Items.RECORD_CAT);
            registerOre("record",      Items.RECORD_BLOCKS);
            registerOre("record",      Items.RECORD_CHIRP);
            registerOre("record",      Items.RECORD_FAR);
            registerOre("record",      Items.RECORD_MALL);
            registerOre("record",      Items.RECORD_MELLOHI);
            registerOre("record",      Items.RECORD_STAL);
            registerOre("record",      Items.RECORD_STRAD);
            registerOre("record",      Items.RECORD_WARD);
            registerOre("record",      Items.RECORD_11);
            registerOre("record",      Items.RECORD_WAIT);

            // blocks
            registerOre("dirt",        Blocks.DIRT);
            registerOre("grass",       Blocks.GRASS);
            registerOre("stone",       Blocks.STONE);
            registerOre("cobblestone", Blocks.COBBLESTONE);
            registerOre("gravel",      Blocks.GRAVEL);
            registerOre("sand",        new ItemStack(Blocks.SAND, 1, WILDCARD_VALUE));
            registerOre("sandstone",   new ItemStack(Blocks.SANDSTONE, 1, WILDCARD_VALUE));
            registerOre("sandstone",   new ItemStack(Blocks.RED_SANDSTONE, 1, WILDCARD_VALUE));
            registerOre("netherrack",  Blocks.NETHERRACK);
            registerOre("obsidian",    Blocks.OBSIDIAN);
            registerOre("glowstone",   Blocks.GLOWSTONE);
            registerOre("endstone",    Blocks.END_STONE);
            registerOre("torch",       Blocks.TORCH);
            registerOre("workbench",   Blocks.CRAFTING_TABLE);
            registerOre("blockSlime",    Blocks.SLIME_BLOCK);
            registerOre("blockPrismarine", new ItemStack(Blocks.PRISMARINE, 1, BlockPrismarine.EnumType.ROUGH.getMetadata()));
            registerOre("blockPrismarineBrick", new ItemStack(Blocks.PRISMARINE, 1, BlockPrismarine.EnumType.BRICKS.getMetadata()));
            registerOre("blockPrismarineDark", new ItemStack(Blocks.PRISMARINE, 1, BlockPrismarine.EnumType.DARK.getMetadata()));
            registerOre("stoneGranite",          new ItemStack(Blocks.STONE, 1, 1));
            registerOre("stoneGranitePolished",  new ItemStack(Blocks.STONE, 1, 2));
            registerOre("stoneDiorite",          new ItemStack(Blocks.STONE, 1, 3));
            registerOre("stoneDioritePolished",  new ItemStack(Blocks.STONE, 1, 4));
            registerOre("stoneAndesite",         new ItemStack(Blocks.STONE, 1, 5));
            registerOre("stoneAndesitePolished", new ItemStack(Blocks.STONE, 1, 6));
            registerOre("blockGlassColorless", Blocks.GLASS);
            registerOre("blockGlass",    Blocks.GLASS);
            registerOre("blockGlass",    new ItemStack(Blocks.STAINED_GLASS, 1, WILDCARD_VALUE));
            //blockGlass{Color} is added below with dyes
            registerOre("paneGlassColorless", Blocks.GLASS_PANE);
            registerOre("paneGlass",     Blocks.GLASS_PANE);
            registerOre("paneGlass",     new ItemStack(Blocks.STAINED_GLASS_PANE, 1, WILDCARD_VALUE));
            //paneGlass{Color} is added below with dyes

            // chests
            registerOre("chest",        Blocks.CHEST);
            registerOre("chest",        Blocks.ENDER_CHEST);
            registerOre("chest",        Blocks.TRAPPED_CHEST);
            registerOre("chestWood",    Blocks.CHEST);
            registerOre("chestEnder",   Blocks.ENDER_CHEST);
            registerOre("chestTrapped", Blocks.TRAPPED_CHEST);
        }

        // Build our list of items to replace with ore tags
        Map<ItemStack, String> replacements = new HashMap<ItemStack, String>();

        // wood-related things
        replacements.put(new ItemStack(Items.STICK), "stickWood");
        replacements.put(new ItemStack(Blocks.PLANKS), "plankWood");
        replacements.put(new ItemStack(Blocks.PLANKS, 1, WILDCARD_VALUE), "plankWood");
        replacements.put(new ItemStack(Blocks.WOODEN_SLAB, 1, WILDCARD_VALUE), "slabWood");

        // ingots/nuggets
        replacements.put(new ItemStack(Items.GOLD_INGOT), "ingotGold");
        replacements.put(new ItemStack(Items.IRON_INGOT), "ingotIron");

        // gems and dusts
        replacements.put(new ItemStack(Items.DIAMOND), "gemDiamond");
        replacements.put(new ItemStack(Items.EMERALD), "gemEmerald");
        replacements.put(new ItemStack(Items.PRISMARINE_SHARD), "gemPrismarine");
        replacements.put(new ItemStack(Items.PRISMARINE_CRYSTALS), "dustPrismarine");
        replacements.put(new ItemStack(Items.REDSTONE), "dustRedstone");
        replacements.put(new ItemStack(Items.GLOWSTONE_DUST), "dustGlowstone");

        // crops
        replacements.put(new ItemStack(Items.REEDS), "sugarcane");
        replacements.put(new ItemStack(Blocks.CACTUS), "blockCactus");

        // misc materials
        replacements.put(new ItemStack(Items.PAPER), "paper");

        // mob drops
        replacements.put(new ItemStack(Items.SLIME_BALL), "slimeball");
        replacements.put(new ItemStack(Items.STRING), "string");
        replacements.put(new ItemStack(Items.LEATHER), "leather");
        replacements.put(new ItemStack(Items.ENDER_PEARL), "enderpearl");
        replacements.put(new ItemStack(Items.GUNPOWDER), "gunpowder");
        replacements.put(new ItemStack(Items.NETHER_STAR), "netherStar");
        replacements.put(new ItemStack(Items.FEATHER), "feather");
        replacements.put(new ItemStack(Items.BONE), "bone");
        replacements.put(new ItemStack(Items.EGG), "egg");

        // blocks
        replacements.put(new ItemStack(Blocks.STONE), "stone");
        replacements.put(new ItemStack(Blocks.COBBLESTONE), "cobblestone");
        replacements.put(new ItemStack(Blocks.COBBLESTONE, 1, WILDCARD_VALUE), "cobblestone");
        replacements.put(new ItemStack(Blocks.GLOWSTONE), "glowstone");
        replacements.put(new ItemStack(Blocks.GLASS), "blockGlassColorless");
        replacements.put(new ItemStack(Blocks.PRISMARINE), "prismarine");
        replacements.put(new ItemStack(Blocks.STONE, 1, 1), "stoneGranite");
        replacements.put(new ItemStack(Blocks.STONE, 1, 2), "stoneGranitePolished");
        replacements.put(new ItemStack(Blocks.STONE, 1, 3), "stoneDiorite");
        replacements.put(new ItemStack(Blocks.STONE, 1, 4), "stoneDioritePolished");
        replacements.put(new ItemStack(Blocks.STONE, 1, 5), "stoneAndesite");
        replacements.put(new ItemStack(Blocks.STONE, 1, 6), "stoneAndesitePolished");

        // chests
        replacements.put(new ItemStack(Blocks.CHEST), "chestWood");
        replacements.put(new ItemStack(Blocks.ENDER_CHEST), "chestEnder");
        replacements.put(new ItemStack(Blocks.TRAPPED_CHEST), "chestTrapped");

        // Register dyes
        String[] dyes =
        {
            "Black",
            "Red",
            "Green",
            "Brown",
            "Blue",
            "Purple",
            "Cyan",
            "LightGray",
            "Gray",
            "Pink",
            "Lime",
            "Yellow",
            "LightBlue",
            "Magenta",
            "Orange",
            "White"
        };

        for(int i = 0; i < 16; i++)
        {
            ItemStack dye = new ItemStack(Items.DYE, 1, i);
            ItemStack block = new ItemStack(Blocks.STAINED_GLASS, 1, 15 - i);
            ItemStack pane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 15 - i);
            if (!hasInit)
            {
                registerOre("dye" + dyes[i], dye);
                registerOre("blockGlass" + dyes[i], block);
                registerOre("paneGlass"  + dyes[i], pane);
            }
            replacements.put(dye,   "dye" + dyes[i]);
            replacements.put(block, "blockGlass" + dyes[i]);
            replacements.put(pane,  "paneGlass" + dyes[i]);
        }
        hasInit = true;

        ItemStack[] replaceStacks = replacements.keySet().toArray(new ItemStack[replacements.keySet().size()]);

        // Ignore recipes for the following items
        ItemStack[] exclusions = new ItemStack[]
        {
            new ItemStack(Blocks.LAPIS_BLOCK),
            new ItemStack(Items.COOKIE),
            new ItemStack(Blocks.STONEBRICK),
            new ItemStack(Blocks.STONE_SLAB, 1, WILDCARD_VALUE),
            new ItemStack(Blocks.STONE_STAIRS),
            new ItemStack(Blocks.COBBLESTONE_WALL),
            new ItemStack(Blocks.OAK_FENCE),
            new ItemStack(Blocks.OAK_FENCE_GATE),
            new ItemStack(Blocks.OAK_STAIRS),
            new ItemStack(Blocks.SPRUCE_FENCE),
            new ItemStack(Blocks.SPRUCE_FENCE_GATE),
            new ItemStack(Blocks.SPRUCE_STAIRS),
            new ItemStack(Blocks.BIRCH_STAIRS),
            new ItemStack(Blocks.BIRCH_FENCE_GATE),
            new ItemStack(Blocks.BIRCH_STAIRS),
            new ItemStack(Blocks.JUNGLE_FENCE),
            new ItemStack(Blocks.JUNGLE_FENCE_GATE),
            new ItemStack(Blocks.JUNGLE_STAIRS),
            new ItemStack(Blocks.ACACIA_FENCE),
            new ItemStack(Blocks.ACACIA_FENCE_GATE),
            new ItemStack(Blocks.ACACIA_STAIRS),
            new ItemStack(Blocks.DARK_OAK_FENCE),
            new ItemStack(Blocks.DARK_OAK_FENCE_GATE),
            new ItemStack(Blocks.DARK_OAK_STAIRS),
            new ItemStack(Blocks.WOODEN_SLAB),
            new ItemStack(Blocks.GLASS_PANE),
            new ItemStack(Blocks.BONE_BLOCK), // Bone Block, to prevent conversion of dyes into bone meal.
            new ItemStack(Items.BOAT), 
            null //So the above can have a comma and we don't have to keep editing extra lines.
        };

        List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
        List<IRecipe> recipesToRemove = new ArrayList<IRecipe>();
        List<IRecipe> recipesToAdd = new ArrayList<IRecipe>();

        // Search vanilla recipes for recipes to replace
        for(Object obj : recipes)
        {
            if(obj.getClass() == ShapedRecipes.class)
            {
                ShapedRecipes recipe = (ShapedRecipes)obj;
                ItemStack output = recipe.getRecipeOutput();
                if (output != null && containsMatch(false, exclusions, output))
                {
                    continue;
                }

                if(containsMatch(true, recipe.recipeItems, replaceStacks))
                {
                    recipesToRemove.add(recipe);
                    recipesToAdd.add(new ShapedOreRecipe(recipe, replacements));
                }
            }
            else if(obj.getClass() == ShapelessRecipes.class)
            {
                ShapelessRecipes recipe = (ShapelessRecipes)obj;
                ItemStack output = recipe.getRecipeOutput();
                if (output != null && containsMatch(false, exclusions, output))
                {
                    continue;
                }

                if(containsMatch(true, recipe.recipeItems.toArray(new ItemStack[recipe.recipeItems.size()]), replaceStacks))
                {
                    recipesToRemove.add((IRecipe)obj);
                    IRecipe newRecipe = new ShapelessOreRecipe(recipe, replacements);
                    recipesToAdd.add(newRecipe);
                }
            }
        }

        recipes.removeAll(recipesToRemove);
        recipes.addAll(recipesToAdd);
        if (recipesToRemove.size() > 0)
        {
            FMLLog.info("Replaced %d ore recipes", recipesToRemove.size());
        }
    }

    /**
     * Gets the integer ID for the specified ore name.
     * If the name does not have a ID it assigns it a new one.
     *
     * @param name The unique name for this ore 'oreIron', 'ingotIron', etc..
     * @return A number representing the ID for this ore type
     */
    public static int getOreID(String name)
    {
        Integer val = nameToId.get(name);
        if (val == null)
        {
            idToName.add(name);
            val = idToName.size() - 1; //0 indexed
            nameToId.put(name, val);
            List<ItemStack> back = Lists.newArrayList();
            idToStack.add(back);
            idToStackUn.add(Collections.unmodifiableList(back));
        }
        return val;
    }

    /**
     * Reverse of getOreID, will not create new entries.
     *
     * @param id The ID to translate to a string
     * @return The String name, or "Unknown" if not found.
     */
    public static String getOreName(int id)
    {
        return (id >= 0 && id < idToName.size()) ? idToName.get(id) : "Unknown";
    }

    /**
     * Gets all the integer ID for the ores that the specified item stack is registered to.
     * If the item stack is not linked to any ore, this will return an empty array and no new entry will be created.
     *
     * @param stack The item stack of the ore.
     * @return An array of ids that this ore is registered as.
     */
    public static int[] getOreIDs(ItemStack stack)
    {
        if (stack == null || stack.getItem() == null) throw new IllegalArgumentException("Stack can not be null!");

        Set<Integer> set = new HashSet<Integer>();

        // HACK: use the registry name's ID. It is unique and it knows about substitutions. Fallback to a -1 value (what Item.getIDForItem would have returned) in the case where the registry is not aware of the item yet
        // IT should be noted that -1 will fail the gate further down, if an entry already exists with value -1 for this name. This is what is broken and being warned about.
        // APPARENTLY it's quite common to do this. OreDictionary should be considered alongside Recipes - you can't make them properly until you've registered with the game.
        ResourceLocation registryName = stack.getItem().delegate.name();
        int id;
        if (registryName == null)
        {
            FMLLog.log(Level.DEBUG, "Attempted to find the oreIDs for an unregistered object (%s). This won't work very well.", stack);
            return new int[0];
        }
        else
        {
            id = GameData.getItemRegistry().getId(registryName);
        }
        List<Integer> ids = stackToId.get(id);
        if (ids != null) set.addAll(ids);
        ids = stackToId.get(id | ((stack.getItemDamage() + 1) << 16));
        if (ids != null) set.addAll(ids);

        Integer[] tmp = set.toArray(new Integer[set.size()]);
        int[] ret = new int[tmp.length];
        for (int x = 0; x < tmp.length; x++)
            ret[x] = tmp[x];
        return ret;
    }

    /**
     * Retrieves the ArrayList of items that are registered to this ore type.
     * Creates the list as empty if it did not exist.
     *
     * The returned List is unmodifiable, but will be updated if a new ore
     * is registered using registerOre
     *
     * @param name The ore name, directly calls getOreID
     * @return An arrayList containing ItemStacks registered for this ore
     */
    public static List<ItemStack> getOres(String name)
    {
        return getOres(getOreID(name));
    }

    /**
     * Retrieves the List of items that are registered to this ore type at this instant.
     * If the flag is TRUE, then it will create the list as empty if it did not exist.
     *
     * This option should be used by modders who are doing blanket scans in postInit.
     * It greatly reduces clutter in the OreDictionary is the responsible and proper
     * way to use the dictionary in a large number of cases.
     *
     * The other function above is utilized in OreRecipe and is required for the
     * operation of that code.
     *
     * @param name The ore name, directly calls getOreID if the flag is TRUE
     * @param alwaysCreateEntry Flag - should a new entry be created if empty
     * @return An arraylist containing ItemStacks registered for this ore
     */
    public static List<ItemStack> getOres(String name, boolean alwaysCreateEntry)
    {
        if (alwaysCreateEntry) {
            return getOres(getOreID(name));
        }
        return nameToId.get(name) != null ? getOres(getOreID(name)) : EMPTY_LIST;
    }

    /**
     * Returns whether or not an oreName exists in the dictionary.
     * This function can be used to safely query the Ore Dictionary without
     * adding needless clutter to the underlying map structure.
     *
     * Please use this when possible and appropriate.
     *
     * @param name The ore name
     * @return Whether or not that name is in the Ore Dictionary.
     */
    public static boolean doesOreNameExist(String name)
    {
        return nameToId.get(name) != null;
    }

    /**
     * Retrieves a list of all unique ore names that are already registered.
     *
     * @return All unique ore names that are currently registered.
     */
    public static String[] getOreNames()
    {
        return idToName.toArray(new String[idToName.size()]);
    }

    /**
     * Retrieves the List of items that are registered to this ore type.
     * Creates the list as empty if it did not exist.
     *
     * @param id The ore ID, see getOreID
     * @return An List containing ItemStacks registered for this ore
     */
    private static List<ItemStack> getOres(int id)
    {
        return idToStackUn.size() > id ? idToStackUn.get(id) : EMPTY_LIST;
    }

    private static boolean containsMatch(boolean strict, ItemStack[] inputs, ItemStack... targets)
    {
        for (ItemStack input : inputs)
        {
            for (ItemStack target : targets)
            {
                if (itemMatches(target, input, strict))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean containsMatch(boolean strict, List<ItemStack> inputs, ItemStack... targets)
    {
        for (ItemStack input : inputs)
        {
            for (ItemStack target : targets)
            {
                if (itemMatches(target, input, strict))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean itemMatches(ItemStack target, ItemStack input, boolean strict)
    {
        if (input == null && target != null || input != null && target == null)
        {
            return false;
        }
        return (target.getItem() == input.getItem() && ((target.getItemDamage() == WILDCARD_VALUE && !strict) || target.getItemDamage() == input.getItemDamage()));
    }

    //Convenience functions that make for cleaner code mod side. They all drill down to registerOre(String, int, ItemStack)
    public static void registerOre(String name, Item      ore){ registerOre(name, new ItemStack(ore));  }
    public static void registerOre(String name, Block     ore){ registerOre(name, new ItemStack(ore));  }
    public static void registerOre(String name, ItemStack ore){ registerOreImpl(name, ore);             }

    /**
     * Registers a ore item into the dictionary.
     * Raises the registerOre function in all registered handlers.
     *
     * @param name The name of the ore
     * @param ore The ore's ItemStack
     */
    private static void registerOreImpl(String name, ItemStack ore)
    {
        if ("Unknown".equals(name)) return; //prevent bad IDs.
        if (ore == null || ore.getItem() == null)
        {
            FMLLog.bigWarning("Invalid registration attempt for an Ore Dictionary item with name %s has occurred. The registration has been denied to prevent crashes. The mod responsible for the registration needs to correct this.", name);
            return; //prevent bad ItemStacks.
        }

        int oreID = getOreID(name);
        // HACK: use the registry name's ID. It is unique and it knows about substitutions. Fallback to a -1 value (what Item.getIDForItem would have returned) in the case where the registry is not aware of the item yet
        // IT should be noted that -1 will fail the gate further down, if an entry already exists with value -1 for this name. This is what is broken and being warned about.
        // APPARENTLY it's quite common to do this. OreDictionary should be considered alongside Recipes - you can't make them properly until you've registered with the game.
        ResourceLocation registryName = ore.getItem().delegate.name();
        int hash;
        if (registryName == null)
        {
            FMLLog.bigWarning("A broken ore dictionary registration with name %s has occurred. It adds an item (type: %s) which is currently unknown to the game registry. This dictionary item can only support a single value when"
                    + " registered with ores like this, and NO I am not going to turn this spam off. Just register your ore dictionary entries after the GameRegistry.\n"
                    + "TO USERS: YES this is a BUG in the mod "+Loader.instance().activeModContainer().getName()+" report it to them!", name, ore.getItem().getClass());
            hash = -1;
        }
        else
        {
            hash = GameData.getItemRegistry().getId(registryName);
        }
        if (ore.getItemDamage() != WILDCARD_VALUE)
        {
            hash |= ((ore.getItemDamage() + 1) << 16); // +1 so 0 is significant
        }

        //Add things to the baked version, and prevent duplicates
        List<Integer> ids = stackToId.get(hash);
        if (ids != null && ids.contains(oreID)) return;
        if (ids == null)
        {
            ids = Lists.newArrayList();
            stackToId.put(hash, ids);
        }
        ids.add(oreID);

        //Add to the unbaked version
        ore = ore.copy();
        idToStack.get(oreID).add(ore);
        MinecraftForge.EVENT_BUS.post(new OreRegisterEvent(name, ore));
    }

    public static class OreRegisterEvent extends Event
    {
        private final String Name;
        private final ItemStack Ore;

        public OreRegisterEvent(String name, ItemStack ore)
        {
            this.Name = name;
            this.Ore = ore;
        }

        public String getName()
        {
            return Name;
        }

        public ItemStack getOre()
        {
            return Ore;
        }
    }

    public static void rebakeMap()
    {
        //System.out.println("Baking OreDictionary:");
        stackToId.clear();
        for (int id = 0; id < idToStack.size(); id++)
        {
            List<ItemStack> ores = idToStack.get(id);
            if (ores == null) continue;
            for (ItemStack ore : ores)
            {
                // HACK: use the registry name's ID. It is unique and it knows about substitutions
                ResourceLocation name = ore.getItem().delegate.name();
                int hash;
                if (name == null)
                {
                    FMLLog.log(Level.DEBUG, "Defaulting unregistered ore dictionary entry for ore dictionary %s: type %s to -1", getOreName(id), ore.getItem().getClass());
                    hash = -1;
                }
                else
                {
                    hash = GameData.getItemRegistry().getId(name);
                }
                if (ore.getItemDamage() != WILDCARD_VALUE)
                {
                    hash |= ((ore.getItemDamage() + 1) << 16); // +1 so meta 0 is significant
                }
                List<Integer> ids = stackToId.get(hash);
                if (ids == null)
                {
                    ids = Lists.newArrayList();
                    stackToId.put(hash, ids);
                }
                ids.add(id);
                //System.out.println(id + " " + getOreName(id) + " " + Integer.toHexString(hash) + " " + ore);
            }
        }
    }
}
