package net.minecraftforge.common;

import java.util.*;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.structure.*;
import net.minecraftforge.oredict.OreDictionary;

public class ChestGenHooks
{
    //Currently implemented categories for chests/dispensers, Dungeon loot is still in DungeonHooks
    public static final String MINESHAFT_CORRIDOR       = "mineshaftCorridor";
    public static final String PYRAMID_DESERT_CHEST     = "pyramidDesertyChest";
    public static final String PYRAMID_JUNGLE_CHEST     = "pyramidJungleChest";
    public static final String PYRAMID_JUNGLE_DISPENSER = "pyramidJungleDispenser";
    public static final String STRONGHOLD_CORRIDOR      = "strongholdCorridor";
    public static final String STRONGHOLD_LIBRARY       = "strongholdLibrary";
    public static final String STRONGHOLD_CROSSING      = "strongholdCrossing";
    public static final String VILLAGE_BLACKSMITH       = "villageBlacksmith";
    public static final String BONUS_CHEST              = "bonusChest";
    public static final String DUNGEON_CHEST            = "dungeonChest";

    private static final HashMap<String, ChestGenHooks> chestInfo = new HashMap<String, ChestGenHooks>();
    private static boolean hasInit = false;
    static
    {
        init();
    }

    private static void init()
    {
        if (hasInit)
        {
            return;
        }

        hasInit = true;

        addInfo(MINESHAFT_CORRIDOR,       StructureMineshaftPieces.mineshaftChestContents,                         3,  7);
        addInfo(PYRAMID_DESERT_CHEST,     ComponentScatteredFeatureDesertPyramid.itemsToGenerateInTemple,          2,  7);
        addInfo(PYRAMID_JUNGLE_CHEST,     ComponentScatteredFeatureJunglePyramid.junglePyramidsChestContents,      2,  7);
        addInfo(PYRAMID_JUNGLE_DISPENSER, ComponentScatteredFeatureJunglePyramid.junglePyramidsDispenserContents,  2,  2);
        addInfo(STRONGHOLD_CORRIDOR,      ComponentStrongholdChestCorridor.strongholdChestContents,                2,  4);
        addInfo(STRONGHOLD_LIBRARY,       ComponentStrongholdLibrary.strongholdLibraryChestContents,               1,  5);
        addInfo(STRONGHOLD_CROSSING,      ComponentStrongholdRoomCrossing.strongholdRoomCrossingChestContents,     1,  5);
        addInfo(VILLAGE_BLACKSMITH,       ComponentVillageHouse2.villageBlacksmithChestContents,                   3,  9);
        addInfo(BONUS_CHEST,              WorldServer.bonusChestContent,                                          10, 10);
        addInfo(DUNGEON_CHEST,            WorldGenDungeons.field_111189_a,                                         8,  8);

        ItemStack book = new ItemStack(Item.enchantedBook, 1, 0);
        WeightedRandomChestContent tmp = new WeightedRandomChestContent(book, 1, 1, 1);
        getInfo(MINESHAFT_CORRIDOR  ).addItem(tmp);
        getInfo(PYRAMID_DESERT_CHEST).addItem(tmp);
        getInfo(PYRAMID_JUNGLE_CHEST).addItem(tmp);
        getInfo(STRONGHOLD_CORRIDOR ).addItem(tmp);
        getInfo(STRONGHOLD_LIBRARY  ).addItem(new WeightedRandomChestContent(book, 1, 5, 2));
        getInfo(STRONGHOLD_CROSSING ).addItem(tmp);
        getInfo(DUNGEON_CHEST       ).addItem(tmp);
    }

    static void addDungeonLoot(ChestGenHooks dungeon, ItemStack item, int weight, int min, int max)
    {
        dungeon.addItem(new WeightedRandomChestContent(item, min, max, weight));
    }

    private static void addInfo(String category, WeightedRandomChestContent[] items, int min, int max)
    {
        chestInfo.put(category, new ChestGenHooks(category, items, min, max));
    }

    /**
     * Retrieves, or creates the info class for the specified category.
     *
     * @param category The category name
     * @return A instance of ChestGenHooks for the specified category.
     */
    public static ChestGenHooks getInfo(String category)
    {
        if (!chestInfo.containsKey(category))
        {
            chestInfo.put(category, new ChestGenHooks(category));
        }
        return chestInfo.get(category);
    }

    /**
     * Generates an array of items based on the input min/max count.
     * If the stack can not hold the total amount, it will be split into
     * stacks of size 1.
     *
     * @param rand A random number generator
     * @param source Source item stack
     * @param min Minimum number of items
     * @param max Maximum number of items
     * @return An array containing the generated item stacks
     */
    public static ItemStack[] generateStacks(Random rand, ItemStack source, int min, int max)
    {
        int count = min + (rand.nextInt(max - min + 1));

        ItemStack[] ret;
        if (source.getItem() == null)
        {
            ret = new ItemStack[0];
        }
        else if (count > source.getMaxStackSize())
        {
            ret = new ItemStack[count];
            for (int x = 0; x < count; x++)
            {
                ret[x] = source.copy();
                ret[x].stackSize = 1;
            }
        }
        else
        {
            ret = new ItemStack[1];
            ret[0] = source.copy();
            ret[0].stackSize = count;
        }
        return ret;
    }

    //shortcut functions, See the non-static versions below
    public static WeightedRandomChestContent[] getItems(String category, Random rnd){ return getInfo(category).getItems(rnd); }
    public static int getCount(String category, Random rand){ return getInfo(category).getCount(rand); }
    public static void addItem(String category, WeightedRandomChestContent item){ getInfo(category).addItem(item); }
    public static void removeItem(String category, ItemStack item){ getInfo(category).removeItem(item); }
    public static ItemStack getOneItem(String category, Random rand){ return getInfo(category).getOneItem(rand); }

    private String category;
    private int countMin = 0;
    private int countMax = 0;
    //TO-DO: Privatize this once again when we remove the Deprecated stuff in DungeonHooks
    ArrayList<WeightedRandomChestContent> contents = new ArrayList<WeightedRandomChestContent>();

    public ChestGenHooks(String category)
    {
        this.category = category;
    }

    public ChestGenHooks(String category, WeightedRandomChestContent[] items, int min, int max)
    {
        this(category);
        for (WeightedRandomChestContent item : items)
        {
            contents.add(item);
        }
        countMin = min;
        countMax = max;
    }

    /**
     * Adds a new entry into the possible items to generate.
     *
     * @param item The item to add.
     */
    public void addItem(WeightedRandomChestContent item)
    {
        contents.add(item);
    }

    /**
     * Removes all items that match the input item stack, Only metadata and item ID are checked.
     * If the input item has a metadata of -1, all metadatas will match.
     *
     * @param item The item to check
     */
    public void removeItem(ItemStack item)
    {
        Iterator<WeightedRandomChestContent> itr = contents.iterator();
        while(itr.hasNext())
        {
            WeightedRandomChestContent cont = itr.next();
            if (item.isItemEqual(cont.theItemId) || (item.getItemDamage() == OreDictionary.WILDCARD_VALUE && item.itemID == cont.theItemId.itemID))
            {
                itr.remove();
            }
        }
    }

    /**
     * Gets an array of all random objects that are associated with this category.
     *
     * @return The random objects
     */
    public WeightedRandomChestContent[] getItems(Random rnd)
    {
        ArrayList<WeightedRandomChestContent> ret = new ArrayList<WeightedRandomChestContent>();

        for (WeightedRandomChestContent orig : contents)
        {
            Item item = orig.theItemId.getItem();

            if (item != null)
            {
                WeightedRandomChestContent n = item.getChestGenBase(this, rnd, orig);
                if (n != null)
                {
                    ret.add(n);
                }
            }
        }

        return ret.toArray(new WeightedRandomChestContent[ret.size()]);
    }

    /**
     * Gets a random number between countMin and countMax.
     *
     * @param rand A RNG
     * @return A random number where countMin <= num <= countMax
     */
    public int getCount(Random rand)
    {
        return countMin < countMax ? countMin + rand.nextInt(countMax - countMin) : countMin;
    }

    /**
     * Returns a single ItemStack from the possible items in this registry,
     * Useful if you just want a quick and dirty random Item.
     *
     * @param rand  A Random Number gen
     * @return A single ItemStack, or null if it could not get one.
     */
    public ItemStack getOneItem(Random rand)
    {
        WeightedRandomChestContent[] items = getItems(rand);
        WeightedRandomChestContent item = (WeightedRandomChestContent)WeightedRandom.getRandomItem(rand, items);
        ItemStack[] stacks = ChestGenHooks.generateStacks(rand, item.theItemId, item.theMinimumChanceToGenerateItem, item.theMaximumChanceToGenerateItem);
        return (stacks.length > 0 ? stacks[0] : null);
    }

    //Accessors
    public int getMin(){ return countMin; }
    public int getMax(){ return countMax; }
    public void setMin(int value){ countMin = value; }
    public void setMax(int value){ countMax = value; }
}
