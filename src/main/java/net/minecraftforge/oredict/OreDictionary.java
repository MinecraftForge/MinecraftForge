package net.minecraftforge.oredict;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.Event;
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

public class OreDictionary
{
    private static boolean hasInit = false;
    private static int maxID = 0;
    private static HashMap<String, Integer> oreIDs = new HashMap<String, Integer>();
    private static HashMap<Integer, ArrayList<ItemStack>> oreStacks = new HashMap<Integer, ArrayList<ItemStack>>();

    /**
     * Minecraft changed from -1 to Short.MAX_VALUE in 1.5 release for the "block wildcard". Use this in case it
     * changes again.
     */
    public static final int WILDCARD_VALUE = Short.MAX_VALUE;

    static {
        initVanillaEntries();
    }

    @SuppressWarnings("unchecked")
    public static void initVanillaEntries()
    {
        if (!hasInit)
        {
            registerOre("logWood",     new ItemStack(Blocks.log, 1, WILDCARD_VALUE));
            registerOre("logWood",     new ItemStack(Blocks.log2, 1, WILDCARD_VALUE));
            registerOre("plankWood",   new ItemStack(Blocks.planks, 1, WILDCARD_VALUE));
            registerOre("slabWood",    new ItemStack(Blocks.wooden_slab, 1, WILDCARD_VALUE));
            registerOre("stairWood",   Blocks.oak_stairs);
            registerOre("stairWood",   Blocks.spruce_stairs);
            registerOre("stairWood",   Blocks.birch_stairs);
            registerOre("stairWood",   Blocks.jungle_stairs);
            registerOre("stairWood",   Blocks.acacia_stairs);
            registerOre("stairWood",   Blocks.dark_oak_stairs);
            registerOre("stickWood",   Items.stick);
            registerOre("treeSapling", new ItemStack(Blocks.sapling, 1, WILDCARD_VALUE));
            registerOre("treeLeaves",  new ItemStack(Blocks.leaves, 1, WILDCARD_VALUE));
            registerOre("treeLeaves",  new ItemStack(Blocks.leaves2, 1, WILDCARD_VALUE));
            registerOre("oreGold",     Blocks.gold_ore);
            registerOre("oreIron",     Blocks.iron_ore);
            registerOre("oreLapis",    Blocks.lapis_ore);
            registerOre("oreDiamond",  Blocks.diamond_ore);
            registerOre("oreRedstone", Blocks.redstone_ore);
            registerOre("oreEmerald",  Blocks.emerald_ore);
            registerOre("oreQuartz",   Blocks.quartz_ore);
            registerOre("oreCoal",     Blocks.coal_ore);
            registerOre("gemDiamond",  Items.diamond);
            registerOre("gemEmerald",  Items.emerald);
            registerOre("dustRedstone",  Items.redstone);
            registerOre("dustGlowstone", Items.glowstone_dust);
            registerOre("glowstone",   Blocks.glowstone);
            registerOre("cropWheat",   Items.wheat);
            registerOre("cropPotato",  Items.potato);
            registerOre("cropCarrot",  Items.carrot);
            registerOre("stone",       Blocks.stone);
            registerOre("cobblestone", Blocks.cobblestone);
            registerOre("record",      Items.record_13);
            registerOre("record",      Items.record_cat);
            registerOre("record",      Items.record_blocks);
            registerOre("record",      Items.record_chirp);
            registerOre("record",      Items.record_far);
            registerOre("record",      Items.record_mall);
            registerOre("record",      Items.record_mellohi);
            registerOre("record",      Items.record_stal);
            registerOre("record",      Items.record_strad);
            registerOre("record",      Items.record_ward);
            registerOre("record",      Items.record_11);
            registerOre("record",      Items.record_wait);
        }

        // Build our list of items to replace with ore tags
        Map<ItemStack, String> replacements = new HashMap<ItemStack, String>();
        replacements.put(new ItemStack(Items.stick), "stickWood");
        replacements.put(new ItemStack(Blocks.planks), "plankWood");
        replacements.put(new ItemStack(Blocks.planks, 1, WILDCARD_VALUE), "plankWood");
        replacements.put(new ItemStack(Blocks.stone), "stone");
        replacements.put(new ItemStack(Blocks.stone, 1, WILDCARD_VALUE), "stone");
        replacements.put(new ItemStack(Blocks.cobblestone), "cobblestone");
        replacements.put(new ItemStack(Blocks.cobblestone, 1, WILDCARD_VALUE), "cobblestone");
        replacements.put(new ItemStack(Items.diamond), "gemDiamond");
        replacements.put(new ItemStack(Items.emerald), "gemEmerald");
        replacements.put(new ItemStack(Items.redstone), "dustRedstone");
        replacements.put(new ItemStack(Items.glowstone_dust), "dustGlowstone");
        replacements.put(new ItemStack(Blocks.glowstone), "glowstone");

        // Register dyes
        String[] dyes =
        {
            "dyeBlack",
            "dyeRed",
            "dyeGreen",
            "dyeBrown",
            "dyeBlue",
            "dyePurple",
            "dyeCyan",
            "dyeLightGray",
            "dyeGray",
            "dyePink",
            "dyeLime",
            "dyeYellow",
            "dyeLightBlue",
            "dyeMagenta",
            "dyeOrange",
            "dyeWhite"
        };

        for(int i = 0; i < 16; i++)
        {
            ItemStack dye = new ItemStack(Items.dye, 1, i);
            if (!hasInit)
            {
                registerOre(dyes[i], dye);
            }
            replacements.put(dye, dyes[i]);
        }
        hasInit = true;

        ItemStack[] replaceStacks = replacements.keySet().toArray(new ItemStack[replacements.keySet().size()]);

        // Ignore recipes for the following items
        ItemStack[] exclusions = new ItemStack[]
        {
            new ItemStack(Blocks.lapis_block),
            new ItemStack(Items.cookie),
            new ItemStack(Blocks.stonebrick),
            new ItemStack(Blocks.stone_slab, 1, WILDCARD_VALUE),
            new ItemStack(Blocks.stone_stairs),
            new ItemStack(Blocks.cobblestone_wall),
            new ItemStack(Blocks.oak_stairs),
            new ItemStack(Blocks.spruce_stairs),
            new ItemStack(Blocks.birch_stairs),
            new ItemStack(Blocks.jungle_stairs),
            new ItemStack(Blocks.acacia_stairs),
            new ItemStack(Blocks.dark_oak_stairs)
        };

        List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
        List<IRecipe> recipesToRemove = new ArrayList<IRecipe>();
        List<IRecipe> recipesToAdd = new ArrayList<IRecipe>();

        // Search vanilla recipes for recipes to replace
        for(Object obj : recipes)
        {
            if(obj instanceof ShapedRecipes)
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
            else if(obj instanceof ShapelessRecipes)
            {
                ShapelessRecipes recipe = (ShapelessRecipes)obj;
                ItemStack output = recipe.getRecipeOutput();
                if (output != null && containsMatch(false, exclusions, output))
                {
                    continue;
                }

                if(containsMatch(true, (ItemStack[])recipe.recipeItems.toArray(new ItemStack[recipe.recipeItems.size()]), replaceStacks))
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
            FMLLog.info("Replaced %d ore recipies", recipesToRemove.size());
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
        Integer val = oreIDs.get(name);
        if (val == null)
        {
            val = maxID++;
            oreIDs.put(name, val);
            oreStacks.put(val, new ArrayList<ItemStack>());
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
        for (Map.Entry<String, Integer> entry : oreIDs.entrySet())
        {
            if (id == entry.getValue())
            {
                return entry.getKey();
            }
        }
        return "Unknown";
    }

    /**
     * Gets the integer ID for the specified item stack.
     * If the item stack is not linked to any ore, this will return -1 and no new entry will be created.
     *
     * @param itemStack The item stack of the ore.
     * @return A number representing the ID for this ore type, or -1 if couldn't find it.
     */
    public static int getOreID(ItemStack itemStack)
    {
        if (itemStack == null)
        {
            return -1;
        }

        for(Entry<Integer, ArrayList<ItemStack>> ore : oreStacks.entrySet())
        {
            for(ItemStack target : ore.getValue())
            {
                if(itemStack.getItem() == target.getItem() && (target.getItemDamage() == WILDCARD_VALUE || itemStack.getItemDamage() == target.getItemDamage()))
                {
                    return ore.getKey();
                }
            }
        }
        return -1; // didn't find it.
    }

    /**
     * Retrieves the ArrayList of items that are registered to this ore type.
     * Creates the list as empty if it did not exist.
     *
     * @param name The ore name, directly calls getOreID
     * @return An arrayList containing ItemStacks registered for this ore
     */
    public static ArrayList<ItemStack> getOres(String name)
    {
        return getOres(getOreID(name));
    }

    /**
     * Retrieves a list of all unique ore names that are already registered.
     *
     * @return All unique ore names that are currently registered.
     */
    public static String[] getOreNames()
    {
        return oreIDs.keySet().toArray(new String[oreIDs.keySet().size()]);
    }

    /**
     * Retrieves the ArrayList of items that are registered to this ore type.
     * Creates the list as empty if it did not exist.
     *
     * @param id The ore ID, see getOreID
     * @return An arrayList containing ItemStacks registered for this ore
     */
    public static ArrayList<ItemStack> getOres(Integer id)
    {
        ArrayList<ItemStack> val = oreStacks.get(id);
        if (val == null)
        {
            val = new ArrayList<ItemStack>();
            oreStacks.put(id, val);
        }
        return val;
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
    public static void registerOre(String name, ItemStack ore){ registerOre(name, getOreID(name), ore); }
    public static void registerOre(int    id,   Item      ore){ registerOre(id,   new ItemStack(ore));  }
    public static void registerOre(int    id,   Block     ore){ registerOre(id,   new ItemStack(ore));  }
    public static void registerOre(int    id,   ItemStack ore){ registerOre(getOreName(id), id, ore);   }

    /**
     * Registers a ore item into the dictionary.
     * Raises the registerOre function in all registered handlers.
     *
     * @param name The name of the ore
     * @param id The ID of the ore
     * @param ore The ore's ItemStack
     */
    private static void registerOre(String name, int id, ItemStack ore)
    {
        ArrayList<ItemStack> ores = getOres(id);
        ore = ore.copy();
        ores.add(ore);
        MinecraftForge.EVENT_BUS.post(new OreRegisterEvent(name, ore));
    }

    public static class OreRegisterEvent extends Event
    {
        public final String Name;
        public final ItemStack Ore;

        public OreRegisterEvent(String name, ItemStack ore)
        {
            this.Name = name;
            this.Ore = ore;
        }
    }
}
