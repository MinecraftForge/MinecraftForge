package net.minecraftforge.oredict;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.src.Block;
import net.minecraft.src.CraftingManager;
import net.minecraft.src.IRecipe;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event;

public class OreDictionary
{
    private static int maxID = 0;
    private static HashMap<String, Integer> oreIDs = new HashMap<String, Integer>();
    private static HashMap<Integer, ArrayList<ItemStack>> oreStacks = new HashMap<Integer, ArrayList<ItemStack>>();
    
    static {
        initVanillaEntries();
    }

    public static void initVanillaEntries(){
        registerOre("logWood", new ItemStack(Block.wood, 1, 0));
        registerOre("logWood", new ItemStack(Block.wood, 1, 1));
        registerOre("logWood", new ItemStack(Block.wood, 1, 2));
        registerOre("logWood", new ItemStack(Block.wood, 1, 3));

        registerOre("plankWood", new ItemStack(Block.planks, 1, 0));
        registerOre("plankWood", new ItemStack(Block.planks, 1, 1));
        registerOre("plankWood", new ItemStack(Block.planks, 1, 2));
        registerOre("plankWood", new ItemStack(Block.planks, 1, 3));

        registerOre("slabWood", new ItemStack(Block.woodSingleSlab, 1, 0));
        registerOre("slabWood", new ItemStack(Block.woodSingleSlab, 1, 1));
        registerOre("slabWood", new ItemStack(Block.woodSingleSlab, 1, 2));
        registerOre("slabWood", new ItemStack(Block.woodSingleSlab, 1, 3));

        registerOre("stairWood", Block.stairCompactPlanks);
        registerOre("stairWood", Block.stairsWoodBirch);
        registerOre("stairWood", Block.stairsWoodJungle);
        registerOre("stairWood", Block.stairsWoodSpruce);

        registerOre("dyeBlack", new ItemStack(Item.dyePowder, 1, 0));
        registerOre("dyeRed", new ItemStack(Item.dyePowder, 1, 1));
        registerOre("dyeGreen", new ItemStack(Item.dyePowder, 1, 2));
        registerOre("dyeBrown", new ItemStack(Item.dyePowder, 1, 3));
        registerOre("dyeBlue", new ItemStack(Item.dyePowder, 1, 4));
        registerOre("dyePurple", new ItemStack(Item.dyePowder, 1, 5));
        registerOre("dyeCyan", new ItemStack(Item.dyePowder, 1, 6));
        registerOre("dyeLightGrey", new ItemStack(Item.dyePowder, 1, 7));
        registerOre("dyeGrey", new ItemStack(Item.dyePowder, 1, 8));
        registerOre("dyePink", new ItemStack(Item.dyePowder, 1, 9));
        registerOre("dyeLime", new ItemStack(Item.dyePowder, 1, 10));
        registerOre("dyeYellow", new ItemStack(Item.dyePowder, 1, 11));
        registerOre("dyeLightBlue", new ItemStack(Item.dyePowder, 1, 12));
        registerOre("dyeMagenta", new ItemStack(Item.dyePowder, 1, 13));
        registerOre("dyeOrange", new ItemStack(Item.dyePowder, 1, 14));
        registerOre("dyeWhite", new ItemStack(Item.dyePowder, 1, 15));

        IRecipe recipe = new ShapedOreRecipe(Block.chest,
            "ppp",
            "p p",
            "ppp",
            Character.valueOf('p'), "plankWood");
        CraftingManager.getInstance().getRecipeList().add(recipe);

        recipe = new ShapedOreRecipe(Block.workbench,
            "pp",
            "pp",
            Character.valueOf('p'), "plankWood");
        CraftingManager.getInstance().getRecipeList().add(recipe);

        recipe = new ShapedOreRecipe(Item.pickaxeWood,
            "ppp",
            " s ",
            " s ",
            Character.valueOf('p'), "plankWood",
            Character.valueOf('s'), new ItemStack(Item.stick));
        CraftingManager.getInstance().getRecipeList().add(recipe);

        recipe = new ShapedOreRecipe(Item.shovelWood,
            "p",
            "s",
            "s",
            Character.valueOf('p'), "plankWood",
            Character.valueOf('s'), new ItemStack(Item.stick));
        CraftingManager.getInstance().getRecipeList().add(recipe);

        recipe = new ShapedOreRecipe(Item.axeWood,
            "pp",
            "ps",
            " s",
            Character.valueOf('p'), "plankWood",
            Character.valueOf('s'), new ItemStack(Item.stick));
        CraftingManager.getInstance().getRecipeList().add(recipe);

        recipe = new ShapedOreRecipe(Item.hoeWood,
            "pp",
            " s",
            " s",
            Character.valueOf('p'), "plankWood",
            Character.valueOf('s'), new ItemStack(Item.stick));
        CraftingManager.getInstance().getRecipeList().add(recipe);

        recipe = new ShapedOreRecipe(Item.swordWood,
            "p",
            "p",
            "s",
            Character.valueOf('p'), "plankWood",
            Character.valueOf('s'), new ItemStack(Item.stick));
        CraftingManager.getInstance().getRecipeList().add(recipe);

        recipe = new ShapedOreRecipe(Block.fenceGate,
            "sps",
            "sps",
            Character.valueOf('p'), "plankWood",
            Character.valueOf('s'), new ItemStack(Item.stick));
        CraftingManager.getInstance().getRecipeList().add(recipe);

        recipe = new ShapedOreRecipe(Block.jukebox,
            "ppp",
            "pdp",
            "ppp",
            Character.valueOf('p'), "plankWood",
            Character.valueOf('r'), new ItemStack(Item.diamond));
        CraftingManager.getInstance().getRecipeList().add(recipe);

        recipe = new ShapedOreRecipe(Block.music,
            "ppp",
            "prp",
            "ppp",
            Character.valueOf('p'), "plankWood",
            Character.valueOf('r'), new ItemStack(Item.redstone));
        CraftingManager.getInstance().getRecipeList().add(recipe);

        recipe = new ShapedOreRecipe(Block.bookShelf,
            "ppp",
            "bbb",
            "ppp",
            Character.valueOf('p'), "plankWood",
            Character.valueOf('b'), new ItemStack(Item.book));
        CraftingManager.getInstance().getRecipeList().add(recipe);

        recipe = new ShapelessOreRecipe(Block.field_82511_ci, "plankWood");
        CraftingManager.getInstance().getRecipeList().add(recipe);

        recipe = new ShapedOreRecipe(Item.doorWood,
            "pp",
            "pp",
            "pp",
            Character.valueOf('p'), "plankWood");
        CraftingManager.getInstance().getRecipeList().add(recipe);

        recipe = new ShapedOreRecipe(new ItemStack(Block.trapdoor, 2),
            "ppp",
            "ppp",
            Character.valueOf('p'), "plankWood");
        CraftingManager.getInstance().getRecipeList().add(recipe);

        recipe = new ShapedOreRecipe(new ItemStack(Item.sign, 3),
            "ppp",
            "ppp",
            " s ",
            Character.valueOf('p'), "plankWood",
            Character.valueOf('s'), new ItemStack(Item.stick));
        CraftingManager.getInstance().getRecipeList().add(recipe);

        recipe = new ShapedOreRecipe(new ItemStack(Item.stick, 4),
            "p",
            "p",
            Character.valueOf('p'), "plankWood");
        CraftingManager.getInstance().getRecipeList().add(recipe);

        recipe = new ShapedOreRecipe(new ItemStack(Item.bowlEmpty, 4),
            "p p",
            " p ",
            Character.valueOf('p'), "plankWood");
        CraftingManager.getInstance().getRecipeList().add(recipe);

        recipe = new ShapedOreRecipe(Item.boat,
            "p p",
            "ppp",
            Character.valueOf('p'), "plankWood");
        CraftingManager.getInstance().getRecipeList().add(recipe);

        recipe = new ShapedOreRecipe(Block.pressurePlatePlanks,
            "pp",
            Character.valueOf('p'), "plankWood");
        CraftingManager.getInstance().getRecipeList().add(recipe);

        recipe = new ShapedOreRecipe(Block.pistonBase,
            "ppp",
            "cic",
            "crc",
            Character.valueOf('p'), "plankWood",
            Character.valueOf('c'), new ItemStack(Block.cobblestone),
            Character.valueOf('i'), new ItemStack(Item.ingotIron),
            Character.valueOf('r'), new ItemStack(Item.redstone));
        CraftingManager.getInstance().getRecipeList().add(recipe);

        recipe = new ShapedOreRecipe(Item.bed,
            "ccc",
            "ppp",
            Character.valueOf('p'), "plankWood",
            Character.valueOf('c'), Block.cloth);
        CraftingManager.getInstance().getRecipeList().add(recipe);

        recipe = new ShapedOreRecipe(new ItemStack(Block.tripWireSource, 2),
            "i",
            "s",
            "p",
            Character.valueOf('p'), "plankWood",
            Character.valueOf('i'), new ItemStack(Item.ingotIron),
            Character.valueOf('s'), new ItemStack(Item.stick));
        CraftingManager.getInstance().getRecipeList().add(recipe);
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
        return oreIDs.keySet().toArray(new String[0]);
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
