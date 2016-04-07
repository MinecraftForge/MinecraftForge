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

    public static void initVanillaEntries()
    {
        if (!hasInit)
        {
            // tree- and wood-related things
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
            registerOre("vine",        Blocks.vine);

            // Ores
            registerOre("oreGold",     Blocks.gold_ore);
            registerOre("oreIron",     Blocks.iron_ore);
            registerOre("oreLapis",    Blocks.lapis_ore);
            registerOre("oreDiamond",  Blocks.diamond_ore);
            registerOre("oreRedstone", Blocks.redstone_ore);
            registerOre("oreEmerald",  Blocks.emerald_ore);
            registerOre("oreQuartz",   Blocks.quartz_ore);
            registerOre("oreCoal",     Blocks.coal_ore);

            // ingots/nuggets
            registerOre("ingotIron",     Items.iron_ingot);
            registerOre("ingotGold",     Items.gold_ingot);
            registerOre("ingotBrick",    Items.brick);
            registerOre("ingotBrickNether", Items.netherbrick);
            registerOre("nuggetGold",  Items.gold_nugget);

            // gems and dusts
            registerOre("gemDiamond",  Items.diamond);
            registerOre("gemEmerald",  Items.emerald);
            registerOre("gemQuartz",   Items.quartz);
            registerOre("gemPrismarine", Items.prismarine_shard);
            registerOre("dustPrismarine", Items.prismarine_crystals);
            registerOre("dustRedstone",  Items.redstone);
            registerOre("dustGlowstone", Items.glowstone_dust);
            registerOre("gemLapis",    new ItemStack(Items.dye, 1, 4));

            // storage blocks
            registerOre("blockGold",     Blocks.gold_block);
            registerOre("blockIron",     Blocks.iron_block);
            registerOre("blockLapis",    Blocks.lapis_block);
            registerOre("blockDiamond",  Blocks.diamond_block);
            registerOre("blockRedstone", Blocks.redstone_block);
            registerOre("blockEmerald",  Blocks.emerald_block);
            registerOre("blockQuartz",   Blocks.quartz_block);
            registerOre("blockCoal",     Blocks.coal_block);

            // crops
            registerOre("cropWheat",   Items.wheat);
            registerOre("cropPotato",  Items.potato);
            registerOre("cropCarrot",  Items.carrot);
            registerOre("cropNetherWart", Items.nether_wart);
            registerOre("sugarcane",   Items.reeds);
            registerOre("blockCactus", Blocks.cactus);

            // misc materials
            registerOre("dye",         new ItemStack(Items.dye, 1, WILDCARD_VALUE));
            registerOre("paper",       new ItemStack(Items.paper));

            // mob drops
            registerOre("slimeball",   Items.slime_ball);
            registerOre("enderpearl",  Items.ender_pearl);
            registerOre("bone",        Items.bone);
            registerOre("gunpowder",   Items.gunpowder);
            registerOre("string", Items.string);
            registerOre("netherStar",  Items.nether_star);
            registerOre("leather",     Items.leather);
            registerOre("feather",     Items.feather);
            registerOre("egg",         Items.egg);

            // records
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

            // blocks
            registerOre("dirt",        Blocks.dirt);
            registerOre("grass",       Blocks.grass);
            registerOre("stone",       Blocks.stone);
            registerOre("cobblestone", Blocks.cobblestone);
            registerOre("gravel",      Blocks.gravel);
            registerOre("sand",        new ItemStack(Blocks.sand, 1, WILDCARD_VALUE));
            registerOre("sandstone",   new ItemStack(Blocks.sandstone, 1, WILDCARD_VALUE));
            registerOre("sandstone",   new ItemStack(Blocks.red_sandstone, 1, WILDCARD_VALUE));
            registerOre("netherrack",  Blocks.netherrack);
            registerOre("obsidian",    Blocks.obsidian);
            registerOre("glowstone",   Blocks.glowstone);
            registerOre("endstone",    Blocks.end_stone);
            registerOre("torch",       Blocks.torch);
            registerOre("workbench",   Blocks.crafting_table);
            registerOre("blockSlime",    Blocks.slime_block);
            registerOre("blockPrismarine", new ItemStack(Blocks.prismarine, 1, BlockPrismarine.EnumType.ROUGH.getMetadata()));
            registerOre("blockPrismarineBrick", new ItemStack(Blocks.prismarine, 1, BlockPrismarine.EnumType.BRICKS.getMetadata()));
            registerOre("blockPrismarineDark", new ItemStack(Blocks.prismarine, 1, BlockPrismarine.EnumType.DARK.getMetadata()));
            registerOre("stoneGranite",          new ItemStack(Blocks.stone, 1, 1));
            registerOre("stoneGranitePolished",  new ItemStack(Blocks.stone, 1, 2));
            registerOre("stoneDiorite",          new ItemStack(Blocks.stone, 1, 3));
            registerOre("stoneDioritePolished",  new ItemStack(Blocks.stone, 1, 4));
            registerOre("stoneAndesite",         new ItemStack(Blocks.stone, 1, 5));
            registerOre("stoneAndesitePolished", new ItemStack(Blocks.stone, 1, 6));
            registerOre("blockGlassColorless", Blocks.glass);
            registerOre("blockGlass",    Blocks.glass);
            registerOre("blockGlass",    new ItemStack(Blocks.stained_glass, 1, WILDCARD_VALUE));
            //blockGlass{Color} is added below with dyes
            registerOre("paneGlassColorless", Blocks.glass_pane);
            registerOre("paneGlass",     Blocks.glass_pane);
            registerOre("paneGlass",     new ItemStack(Blocks.stained_glass_pane, 1, WILDCARD_VALUE));
            //paneGlass{Color} is added below with dyes

            // chests
            registerOre("chest",       Blocks.chest);
            registerOre("chest",       Blocks.ender_chest);
            registerOre("chest",       Blocks.trapped_chest);
            registerOre("chestWood",   Blocks.chest);
            registerOre("chestEnder",  Blocks.ender_chest);
            registerOre("chestTrapped", Blocks.trapped_chest);
        }

        // Build our list of items to replace with ore tags
        Map<ItemStack, String> replacements = new HashMap<ItemStack, String>();

        // wood-related things
        replacements.put(new ItemStack(Items.stick), "stickWood");
        replacements.put(new ItemStack(Blocks.planks), "plankWood");
        replacements.put(new ItemStack(Blocks.planks, 1, WILDCARD_VALUE), "plankWood");
        replacements.put(new ItemStack(Blocks.wooden_slab, 1, WILDCARD_VALUE), "slabWood");

        // ingots/nuggets
        replacements.put(new ItemStack(Items.gold_ingot), "ingotGold");
        replacements.put(new ItemStack(Items.iron_ingot), "ingotIron");

        // gems and dusts
        replacements.put(new ItemStack(Items.diamond), "gemDiamond");
        replacements.put(new ItemStack(Items.emerald), "gemEmerald");
        replacements.put(new ItemStack(Items.prismarine_shard), "gemPrismarine");
        replacements.put(new ItemStack(Items.prismarine_crystals), "dustPrismarine");
        replacements.put(new ItemStack(Items.redstone), "dustRedstone");
        replacements.put(new ItemStack(Items.glowstone_dust), "dustGlowstone");

        // crops
        replacements.put(new ItemStack(Items.reeds), "sugarcane");
        replacements.put(new ItemStack(Blocks.cactus), "blockCactus");

        // misc materials
        replacements.put(new ItemStack(Items.paper), "paper");

        // mob drops
        replacements.put(new ItemStack(Items.slime_ball), "slimeball");
        replacements.put(new ItemStack(Items.string), "string");
        replacements.put(new ItemStack(Items.leather), "leather");
        replacements.put(new ItemStack(Items.ender_pearl), "enderpearl");
        replacements.put(new ItemStack(Items.gunpowder), "gunpowder");
        replacements.put(new ItemStack(Items.nether_star), "netherStar");
        replacements.put(new ItemStack(Items.feather), "feather");
        replacements.put(new ItemStack(Items.bone), "bone");
        replacements.put(new ItemStack(Items.egg), "egg");

        // blocks
        replacements.put(new ItemStack(Blocks.stone), "stone");
        replacements.put(new ItemStack(Blocks.cobblestone), "cobblestone");
        replacements.put(new ItemStack(Blocks.cobblestone, 1, WILDCARD_VALUE), "cobblestone");
        replacements.put(new ItemStack(Blocks.glowstone), "glowstone");
        replacements.put(new ItemStack(Blocks.glass), "blockGlassColorless");
        replacements.put(new ItemStack(Blocks.prismarine), "prismarine");
        replacements.put(new ItemStack(Blocks.stone, 1, 1), "stoneGranite");
        replacements.put(new ItemStack(Blocks.stone, 1, 2), "stoneGranitePolished");
        replacements.put(new ItemStack(Blocks.stone, 1, 3), "stoneDiorite");
        replacements.put(new ItemStack(Blocks.stone, 1, 4), "stoneDioritePolished");
        replacements.put(new ItemStack(Blocks.stone, 1, 5), "stoneAndesite");
        replacements.put(new ItemStack(Blocks.stone, 1, 6), "stoneAndesitePolished");

        // chests
        replacements.put(new ItemStack(Blocks.chest), "chestWood");
        replacements.put(new ItemStack(Blocks.ender_chest), "chestEnder");
        replacements.put(new ItemStack(Blocks.trapped_chest), "chestTrapped");

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
            new ItemStack(Blocks.oak_fence),
            new ItemStack(Blocks.oak_fence_gate),
            new ItemStack(Blocks.oak_stairs),
            new ItemStack(Blocks.spruce_fence),
            new ItemStack(Blocks.spruce_fence_gate),
            new ItemStack(Blocks.spruce_stairs),
            new ItemStack(Blocks.birch_fence),
            new ItemStack(Blocks.birch_fence_gate),
            new ItemStack(Blocks.birch_stairs),
            new ItemStack(Blocks.jungle_fence),
            new ItemStack(Blocks.jungle_fence_gate),
            new ItemStack(Blocks.jungle_stairs),
            new ItemStack(Blocks.acacia_fence),
            new ItemStack(Blocks.acacia_fence_gate),
            new ItemStack(Blocks.acacia_stairs),
            new ItemStack(Blocks.dark_oak_fence),
            new ItemStack(Blocks.dark_oak_fence_gate),
            new ItemStack(Blocks.dark_oak_stairs),
            new ItemStack(Blocks.wooden_slab),
            new ItemStack(Blocks.glass_pane),
            null //So the above can have a comma and we don't have to keep editing extra lines.
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
     * Gets all the integer ID for the ores that the specified item stakc is registered to.
     * If the item stack is not linked to any ore, this will return an empty array and no new entry will be created.
     *
     * @param stack The item stack of the ore.
     * @return An array of ids that this ore is registerd as.
     */
    public static int[] getOreIDs(ItemStack stack)
    {
        if (stack == null || stack.getItem() == null) throw new IllegalArgumentException("Stack can not be null!");

        Set<Integer> set = new HashSet<Integer>();

        // HACK: use the registry name's ID. It is unique and it knows about substitutions. Fallback to a -1 value (what Item.getIDForItem would have returned) in the case where the registry is not aware of the item yet
        // IT should be noted that -1 will fail the gate further down, if an entry already exists with value -1 for this name. This is what is broken and being warned about.
        // APPARENTLY it's quite common to do this. OreDictionary should be considered alongside Recipes - you can't make them properly until you've registered with the game.
        ResourceLocation registryName = stack.getItem().delegate.getResourceName();
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
        ResourceLocation registryName = ore.getItem().delegate.getResourceName();
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
        public final String Name;
        public final ItemStack Ore;

        public OreRegisterEvent(String name, ItemStack ore)
        {
            this.Name = name;
            this.Ore = ore;
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
                ResourceLocation name = ore.getItem().delegate.getResourceName();
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
