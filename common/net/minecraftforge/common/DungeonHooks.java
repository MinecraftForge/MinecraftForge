package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.util.WeightedRandomItem;

import static net.minecraftforge.common.ChestGenHooks.DUNGEON_CHEST;

public class DungeonHooks
{
    private static ArrayList<DungeonMob> dungeonMobs = new ArrayList<DungeonMob>();

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
            return target instanceof DungeonMob && type.equals(((DungeonMob)target).type);
        }
    }

    static
    {
        addDungeonMob("Skeleton", 100);
        addDungeonMob("Zombie",   200);
        addDungeonMob("Spider",   100);
    }


    @Deprecated //Moved to ChestGenHooks
    public static void setDungeonLootTries(int number)
    {
        ChestGenHooks.getInfo(DUNGEON_CHEST).setMax(number);
        ChestGenHooks.getInfo(DUNGEON_CHEST).setMin(number);
    }
    @Deprecated //Moved to ChestGenHooks
    public static int getDungeonLootTries() { return ChestGenHooks.getInfo(DUNGEON_CHEST).getMax(); }
    @Deprecated //Moved to ChestGenHooks
    public void addDungeonLoot(DungeonLoot loot){ ChestGenHooks.getInfo(DUNGEON_CHEST).addItem(loot); }
    @Deprecated //Moved to ChestGenHooks
    public boolean removeDungeonLoot(DungeonLoot loot){ return ChestGenHooks.getInfo(DUNGEON_CHEST).contents.remove(loot); }
    @Deprecated //Moved to ChestGenHooks
    public static void addDungeonLoot(ItemStack item, int rarity){ addDungeonLoot(item, rarity, 1, 1); }
    @Deprecated //Moved to ChestGenHooks
    public static float addDungeonLoot(ItemStack item, int rarity, int minCount, int maxCount)
    {
        ChestGenHooks.addDungeonLoot(ChestGenHooks.getInfo(DUNGEON_CHEST), item, rarity, minCount, maxCount);
        return rarity;
    }
    @Deprecated //Moved to ChestGenHooks
    public static void removeDungeonLoot(ItemStack item){ ChestGenHooks.removeItem(DUNGEON_CHEST, item); }
    @Deprecated //Moved to ChestGenHooks
    public static void removeDungeonLoot(ItemStack item, int minCount, int maxCount){ ChestGenHooks.removeItem(DUNGEON_CHEST, item); }
    @Deprecated //Moved to ChestGenHooks
    public static ItemStack getRandomDungeonLoot(Random rand){ return ChestGenHooks.getOneItem(DUNGEON_CHEST, rand); }

    @Deprecated //Moved to ChestGenHooks
    public static class DungeonLoot extends WeightedRandomChestContent
    {
        @Deprecated
        public DungeonLoot(int weight, ItemStack item, int min, int max)
        {
            super(item, weight, min, max);
        }

        @Deprecated
        public ItemStack generateStack(Random rand)
        {
            int min = theMinimumChanceToGenerateItem;
            int max = theMaximumChanceToGenerateItem;
            
            ItemStack ret = this.theItemId.copy();
            ret.stackSize = min + (rand.nextInt(max - min + 1));
            return ret;
        }

        public boolean equals(ItemStack item, int min, int max)
        {
            int minCount = theMinimumChanceToGenerateItem;
            int maxCount = theMaximumChanceToGenerateItem;
            return (min == minCount && max == maxCount && item.isItemEqual(theItemId) && ItemStack.areItemStackTagsEqual(item, theItemId));
        }
        public boolean equals(ItemStack item){ return item.isItemEqual(theItemId) && ItemStack.areItemStackTagsEqual(item, theItemId); }
    }
}
