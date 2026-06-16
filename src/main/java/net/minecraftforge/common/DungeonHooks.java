/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import java.util.ArrayList;

import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;

public class DungeonHooks {
    private static WeightedList<EntityType<?>> dungeonMobs = WeightedList.<EntityType<?>>builder()
        .add(EntityTypes.SKELETON, 100)
        .add(EntityTypes.ZOMBIE, 200)
        .add(EntityTypes.SPIDER, 100)
        .build();

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
            throw new IllegalArgumentException("Rarity must be greater then zero");

        var list = new ArrayList<>(dungeonMobs.unwrap());
        var itr = list.iterator();
        while (itr.hasNext()) {
            var mob = itr.next();
            if (type == mob.value()) {
                itr.remove();
                rarity = mob.weight() + rarity;
                break;
            }
        }

        dungeonMobs = WeightedList.<EntityType<?>>builder().addAll(list).add(type, rarity).build();
        return rarity;
    }

    /**
     * Will completely remove a Mob from the dungeon spawn list.
     *
     * @param name The name of the mob to remove
     * @return The rarity of the removed mob, prior to being removed.
     */
    public static int removeDungeonMob(EntityType<?> name) {
        var list = new ArrayList<>(dungeonMobs.unwrap());
        for (var mob : list) {
            if (mob.value() != name) continue;

            list.remove(mob);
            dungeonMobs = WeightedList.<EntityType<?>>builder().addAll(list).build();
            return mob.weight();
        }

        return 0;
    }

    /**
     * Gets a random mob name from the list.
     * @param rand World generation random number generator
     * @return The mob name
     */
    public static EntityType<?> getRandomDungeonMob(RandomSource rand) {
        return dungeonMobs.getRandomOrThrow(rand);
    }
}
