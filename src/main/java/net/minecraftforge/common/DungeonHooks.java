/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.entity.EntityType;

public class DungeonHooks
{
    private static ArrayList<DungeonMob> dungeonMobs = new ArrayList<DungeonMob>();

    /**
     * Adds a mob to the possible list of creatures the spawner will create.
     * If the mob is already in the spawn list, the rarity will be added to the existing one,
     * causing the mob to be more common.
     *
     * @param type Monster type
     * @param rarity The rarity of selecting this mob over others. Must be greater then 0.
     *        Vanilla Minecraft has the following mobs:
     *        Spider   100
     *        Skeleton 100
     *        Zombie   200
     *        Meaning, Zombies are twice as common as spiders or skeletons.
     * @return The new rarity of the monster,
     */
    public static float addDungeonMob(EntityType<?> type, int rarity)
    {
        if (rarity <= 0)
        {
            throw new IllegalArgumentException("Rarity must be greater then zero");
        }

        Iterator<DungeonMob> itr = dungeonMobs.iterator();
        while (itr.hasNext())
        {
            DungeonMob mob = itr.next();
            if (type == mob.type)
            {
                itr.remove();
                rarity = mob.getWeight().asInt() + rarity;
                break;
            }
        }

        dungeonMobs.add(new DungeonMob(rarity, type));
        return rarity;
    }

    /**
     * Will completely remove a Mob from the dungeon spawn list.
     *
     * @param name The name of the mob to remove
     * @return The rarity of the removed mob, prior to being removed.
     */
    public static int removeDungeonMob(EntityType<?> name)
    {
        for (DungeonMob mob : dungeonMobs)
        {
            if (name == mob.type)
            {
                dungeonMobs.remove(mob);
                return mob.getWeight().asInt();
            }
        }
        return 0;
    }

    /**
     * Gets a random mob name from the list.
     * @param rand World generation random number generator
     * @return The mob name
     */
    public static EntityType<?> getRandomDungeonMob(RandomSource rand)
    {
        DungeonMob mob = WeightedRandom.getRandomItem(rand, dungeonMobs).orElseThrow();
        return mob.type;
    }


    public static class DungeonMob extends WeightedEntry.IntrusiveBase
    {
        public final EntityType<?> type;
        public DungeonMob(int weight, EntityType<?> type)
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
        addDungeonMob(EntityType.SKELETON, 100);
        addDungeonMob(EntityType.ZOMBIE,   200);
        addDungeonMob(EntityType.SPIDER,   100);
    }
}
