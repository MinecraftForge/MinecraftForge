package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.WeightedRandom;
import net.minecraft.src.WeightedRandomItem;

public class DungeonHooks
{
    private static int dungeonLootAttempts = 8;
    private static ArrayList<DungeonMob> dungeonMobs = new ArrayList<DungeonMob>();
    private static ArrayList<DungeonLoot> dungeonLoot = new ArrayList<DungeonLoot>();
    /**
     * Set the number of item stacks that will be attempted to be added to each Dungeon chest.
     * Note: Due to random number generation, you will not always get this amount per chest.
     * @param number The maximum number of item stacks to add to a chest.
     */
    public static void setDungeonLootTries(int number)
    {
        dungeonLootAttempts = number;
    }

    /**
     * @return The max number of item stacks found in each dungeon chest.
     */
    public static int getDungeonLootTries()
    {
        return dungeonLootAttempts;
    }

    /**
     * Adds a mob to the possible list of creatures the spawner will create.
     * If the mob is already in the spawn list, the rarity will be added to the existing one,
     * causing the mob to be more common.
     *
     * @param name The name of the monster, use the same name used when registering the entity.
     * @param rarity The rarity of selecting this mob over others. Must be greater then 0.
     *        Vanilla Minecraft has the following mobs:
     *        Spider   100
     *        Skeleton 100
     *        Zombie   200
     *        Meaning, Zombies are twice as common as spiders or skeletons.
     * @return The new rarity of the monster,
     */
    public static float addDungeonMob(String name, int rarity)
    {
        if (rarity <= 0)
        {
            throw new IllegalArgumentException("Rarity must be greater then zero");
        }

        for (DungeonMob mob : dungeonMobs)
        {
            if (name.equals(mob.type))
            {
                return mob.itemWeight += rarity;
            }
        }

        dungeonMobs.add(new DungeonMob(rarity, name));
        return rarity;
    }

    /**
     * Will completely remove a Mob from the dungeon spawn list.
     *
     * @param name The name of the mob to remove
     * @return The rarity of the removed mob, prior to being removed.
     */
    public static int removeDungeonMob(String name)
    {
        for (DungeonMob mob : dungeonMobs)
        {
            if (name.equals(mob.type))
            {
                dungeonMobs.remove(mob);
                return mob.itemWeight;
            }
        }
        return 0;
    }

    /**
     * Gets a random mob name from the list.
     * @param rand World generation random number generator
     * @return The mob name
     */
    public static String getRandomDungeonMob(Random rand)
    {
        DungeonMob mob = (DungeonMob)WeightedRandom.getRandomItem(rand, dungeonMobs);
        if (mob == null)
        {
            return "";
        }
        return mob.type;
    }

    /**
     * Adds a item stack to the dungeon loot list with a stack size
     * of 1.
     *
     * @param item The ItemStack to be added to the loot list
     * @param rarity The relative chance that this item will spawn, Vanilla has
     *          most of its items set to 1. Like the saddle, bread, silk, wheat, etc..
     *          Rarer items are set to lower values, EXA: Golden Apple 0.01
     */
    public static void addDungeonLoot(ItemStack item, int rarity)
    {
        addDungeonLoot(item, rarity, 1, 1);
    }

    /**
     * Adds a item stack, with a range of sizes, to the dungeon loot list.
     * If a stack matching the same item, and size range, is already in the list
     * the rarities will be added together making the item more common.
     *
     * @param item The ItemStack to be added to the loot list
     * @param rarity The relative chance that this item will spawn, Vanilla has
     *          most of its items set to 1. Like the saddle, bread, silk, wheat, etc..
     *          Rarer items are set to lower values, EXA: Golden Apple 0.01
     * @param minCount When this item does generate, the minimum number that is in the stack
     * @param maxCount When this item does generate, the maximum number that can bein the stack
     * @return The new rarity of the loot.
     */
    public static float addDungeonLoot(ItemStack item, int rarity, int minCount, int maxCount)
    {
        for (DungeonLoot loot : dungeonLoot)
        {
            if (loot.equals(item, minCount, maxCount))
            {
                return loot.itemWeight += rarity;
            }
        }

        dungeonLoot.add(new DungeonLoot(rarity, item, minCount, maxCount));
        return rarity;
    }
    /**
     * Removes a item stack from the dungeon loot list, this will remove all items
     * as long as the item stack matches, it will not care about matching the stack
     * size ranges perfectly.
     *
     * @param item The item stack to remove
     */
    public static void removeDungeonLoot(ItemStack item)
    {
        removeDungeonLoot(item, -1, 0);
    }

    /**
     * Removes a item stack from the dungeon loot list. If 'minCount' parameter
     * is greater then 0, it will only remove loot items that have the same exact
     * stack size range as passed in by parameters.
     *
     * @param item The item stack to remove
     * @param minCount The minimum count for the match check, if less then 0,
     *          the size check is skipped
     * @param maxCount The max count used in match check when 'minCount' is >= 0
     */
    public static void removeDungeonLoot(ItemStack item, int minCount, int maxCount)
    {
        ArrayList<DungeonLoot> lootTmp = (ArrayList<DungeonLoot>)dungeonLoot.clone();
        if (minCount < 0)
        {
            for (DungeonLoot loot : lootTmp)
            {
                if (loot.equals(item))
                {
                    dungeonLoot.remove(loot);
                }
            }
        }
        else
        {
            for (DungeonLoot loot : lootTmp)
            {
                if (loot.equals(item, minCount, maxCount))
                {
                    dungeonLoot.remove(loot);
                }
            }
        }
    }

    /**
     * Gets a random item stack to place in a dungeon chest during world generation
     * @param rand World generation random number generator
     * @return The item stack
     */
    public static ItemStack getRandomDungeonLoot(Random rand)
    {
        DungeonLoot ret = (DungeonLoot)WeightedRandom.getRandomItem(rand, dungeonLoot);
        if (ret != null)
        {
            return ret.generateStack(rand);
        }
        return null;
    }
    
    public static class DungeonLoot extends WeightedRandomItem
    {
        private ItemStack itemStack;
        private int minCount = 1;
        private int maxCount = 1;

        /**
         * @param item A item stack
         * @param min Minimum stack size when randomly generating
         * @param max Maximum stack size when randomly generating
         */
        public DungeonLoot(int weight, ItemStack item, int min, int max)
        {
            super(weight);
            this.itemStack = item;
            minCount = min;
            maxCount = max;
        }

        /**
         * Grabs a ItemStack ready to be added to the dungeon chest,
         * the stack size will be between minCount and maxCount
         * @param rand World gen random number generator
         * @return The ItemStack to be added to the chest
         */
        public ItemStack generateStack(Random rand)
        {
            ItemStack ret = this.itemStack.copy();
            ret.stackSize = minCount + (rand.nextInt(maxCount - minCount + 1));
            return ret;
        }

        public boolean equals(ItemStack item, int min, int max)
        {
            return (min == minCount && max == maxCount && item.isItemEqual(this.itemStack));
        }

        public boolean equals(ItemStack item)
        {
            return item.isItemEqual(this.itemStack);
        }
    }
    
    public static class DungeonMob extends WeightedRandomItem
    {
        public String type;
        public DungeonMob(int weight, String type)
        {
            super(weight);
            this.type = type;
        }
        
        @Override
        public boolean equals(Object target)
        {
            if (target instanceof DungeonMob)
            {
                return this.type.equals(((DungeonMob)target).type);
            }
            return false;
        }
    }

    public void addDungeonLoot(DungeonLoot loot)
    {
        dungeonLoot.add(loot);
    }

    public boolean removeDungeonLoot(DungeonLoot loot)
    {
        return dungeonLoot.remove(loot);
    }

    static
    {
        addDungeonMob("Skeleton", 100);
        addDungeonMob("Zombie",   200);
        addDungeonMob("Spider",   100);

        addDungeonLoot(new ItemStack(Item.saddle),          100      );
        addDungeonLoot(new ItemStack(Item.ingotIron),       100, 1, 4);
        addDungeonLoot(new ItemStack(Item.bread),           100      );
        addDungeonLoot(new ItemStack(Item.wheat),           100, 1, 4);
        addDungeonLoot(new ItemStack(Item.gunpowder),       100, 1, 4);
        addDungeonLoot(new ItemStack(Item.silk),            100, 1, 4);
        addDungeonLoot(new ItemStack(Item.bucketEmpty),     100      );
        addDungeonLoot(new ItemStack(Item.appleGold),       001      );
        addDungeonLoot(new ItemStack(Item.redstone),        050, 1, 4);
        addDungeonLoot(new ItemStack(Item.record13),        005      );
        addDungeonLoot(new ItemStack(Item.recordCat),       005      );
        addDungeonLoot(new ItemStack(Item.dyePowder, 1, 3), 100      );
    }
}
