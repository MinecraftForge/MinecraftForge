package net.minecraftforge.oredict;

import java.util.ArrayList;
import java.util.Collections;
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
import net.minecraftforge.oredict.CustomHashMap.Hasher;

public class OreDictionary
{
    private static boolean hasInit = false;
    private static final List<Container> idToInfo = new ArrayList<Container>();
    private static final Map<String, Integer> oreIDs = new HashMap<String, Integer>();
    private static final Hasher<ItemStack> hasher = new Hasher<ItemStack>() {

        @Override
        public boolean equals(ItemStack first, ItemStack second)
        {
            boolean itemsEqual = first.getItem() == second.getItem();
            int damage1 = first.getItemDamage();
            if (damage1 == WILDCARD_VALUE)
            {
                return itemsEqual;
            }
            int damage2 = second.getItemDamage();
            if (damage2 == WILDCARD_VALUE)
            {
                return itemsEqual;
            }
            return itemsEqual && damage1 == damage2;
        }

        @Override
        public int hash(ItemStack object)
        {
            int h = Item.getIdFromItem(object.getItem());
            int damage = object.getItemDamage();
            if (damage != WILDCARD_VALUE)
            {
                h = h | (damage << 16);
            }
            h ^= (h >>> 20) ^ (h >>> 12);
            return h ^ (h >>> 7) ^ (h >>> 4);
        }
    };
    private static final CustomHashMap<ItemStack, List<Integer>> stackToIds = new CustomHashMap<ItemStack, List<Integer>>(hasher, 100);

    private static final Container EMPTY = new Container("Unknwon");

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
            registerOre("blockGold",     Blocks.gold_block);
            registerOre("blockIron",     Blocks.iron_block);
            registerOre("blockLapis",    Blocks.lapis_block);
            registerOre("blockDiamond",  Blocks.diamond_block);
            registerOre("blockRedstone", Blocks.redstone_block);
            registerOre("blockEmerald",  Blocks.emerald_block);
            registerOre("blockQuartz",   Blocks.quartz_block);
            registerOre("blockCoal",     Blocks.coal_block);
            registerOre("blockGlassColorless", Blocks.glass);
            registerOre("blockGlass",    Blocks.glass);
            registerOre("blockGlass",    new ItemStack(Blocks.stained_glass, 1, WILDCARD_VALUE));
            //blockGlass{Color} is added below with dyes
            registerOre("paneGlassColorless", Blocks.glass_pane);
            registerOre("paneGlass",     Blocks.glass_pane);
            registerOre("paneGlass",     new ItemStack(Blocks.stained_glass_pane, 1, WILDCARD_VALUE));
            //paneGlass{Color} is added below with dyes
            registerOre("ingotIron",     Items.iron_ingot);
            registerOre("ingotGold",     Items.gold_ingot);
            registerOre("ingotBrick",    Items.brick);
            registerOre("ingotBrickNether", Items.netherbrick);
            registerOre("nuggetGold",  Items.gold_nugget);
            registerOre("gemDiamond",  Items.diamond);
            registerOre("gemEmerald",  Items.emerald);
            registerOre("gemQuartz",   Items.quartz);
            registerOre("dustRedstone",  Items.redstone);
            registerOre("dustGlowstone", Items.glowstone_dust);
            registerOre("dustLapis",   new ItemStack(Items.dye, 1, 4));
            registerOre("slimeball",   Items.slime_ball);
            registerOre("glowstone",   Blocks.glowstone);
            registerOre("cropWheat",   Items.wheat);
            registerOre("cropPotato",  Items.potato);
            registerOre("cropCarrot",  Items.carrot);
            registerOre("stone",       Blocks.stone);
            registerOre("cobblestone", Blocks.cobblestone);
            registerOre("sandstone",   new ItemStack(Blocks.sandstone, 1, WILDCARD_VALUE));
            registerOre("dye",         new ItemStack(Items.dye, 1, WILDCARD_VALUE));
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
        replacements.put(new ItemStack(Items.gold_ingot), "ingotGold");
        replacements.put(new ItemStack(Items.iron_ingot), "ingotIron");
        replacements.put(new ItemStack(Items.diamond), "gemDiamond");
        replacements.put(new ItemStack(Items.emerald), "gemEmerald");
        replacements.put(new ItemStack(Items.redstone), "dustRedstone");
        replacements.put(new ItemStack(Items.glowstone_dust), "dustGlowstone");
        replacements.put(new ItemStack(Blocks.glowstone), "glowstone");
        replacements.put(new ItemStack(Items.slime_ball), "slimeball");
        replacements.put(new ItemStack(Blocks.glass), "blockGlassColorless");

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
            ItemStack dye = new ItemStack(Items.dye, 1, i);
            ItemStack block = new ItemStack(Blocks.stained_glass, 1, 15 - i);
            ItemStack pane = new ItemStack(Blocks.stained_glass_pane, 1, 15 - i);
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
            new ItemStack(Blocks.dark_oak_stairs),
            new ItemStack(Blocks.glass_pane),
            new ItemStack(Blocks.stained_glass)
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
            val = idToInfo.size();
            oreIDs.put(name, val);
            idToInfo.add(new Container(name));
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
        return getContainer(id).name;
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
        List<Integer> ids = stackToIds.get(itemStack);
        if (ids == null)
        {
            return -1;
        }
        return ids.get(0);
    }

    public static List<Integer> getIDs(ItemStack stack)
    {
        List<Integer> ids = stackToIds.get(stack);
        if (ids == null)
        {
            ids = Collections.emptyList();
        }
        return ids;
    }

    /**
     * Gets all the integer ID for the ores that the specified item stakc is registered to.
     * If the item stack is not linked to any ore, this will return an empty array and no new entry will be created.
     *
     * @param itemStack The item stack of the ore.
     * @return An array of ids that this ore is registerd as.
     */
    public static int[] getOreIDs(ItemStack itemStack)
    {
        List<Integer> ids = getIDs(itemStack);
        int[] ret = new int[ids.size()];
        for (int x = 0; x < ids.size(); x++)
            ret[x] = ids.get(x);
        return ret;
    }

    /**
     * Retrieves the ArrayList of items that are registered to this ore type.
     * Creates the list as empty if it did not exist.
     *
     * @param name The ore name, directly calls getOreID
     * @return An arrayList containing ItemStacks registered for this ore
     */
    public static List<ItemStack> getOres(String name)
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
    public static List<ItemStack> getOres(int id)
    {
        return getContainer(id).stacks;
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

    private static boolean containsMatch(boolean strict, List<ItemStack> inputs, ItemStack... targets)
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
        List<ItemStack> ores = getOres(id);
        if (containsMatch(false, ores, ore)) return; // Prevent duplicates.
        ore = ore.copy();
        ores.add(ore);
        List<Integer> ids = stackToIds.get(ore);
        if(ids == null)
        {
          ids = new ArrayList<Integer>();
          stackToIds.put(ore, ids);
        }
        ids.add(id);
        MinecraftForge.EVENT_BUS.post(new OreRegisterEvent(name, ore));
    }

    private static Container getContainer(int id)
    {
        Container container = idToInfo.get(id);
        if (container == null)
        {
            return EMPTY;
        }
        return container;
    }

    private static class Container {
        private final String name;
        private final List<ItemStack> stacks;

        private Container(String name)
        {
            this.name = name;
            stacks = new ArrayList<ItemStack>();
        }
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
