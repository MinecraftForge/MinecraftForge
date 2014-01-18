package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.util.WeightedRandom;

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


    public static class DungeonMob extends WeightedRandom.Item
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
}
