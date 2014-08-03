package net.minecraftforge.common;

import com.google.common.base.Predicate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandomFishable;

public class FishingHooks
{
    private static ArrayList<WeightedRandomFishable> fish = new ArrayList<WeightedRandomFishable>();
    private static ArrayList<WeightedRandomFishable> junk = new ArrayList<WeightedRandomFishable>();
    private static ArrayList<WeightedRandomFishable> treasure = new ArrayList<WeightedRandomFishable>();

    public static void addFish(WeightedRandomFishable item) { fish.add(item); }
    public static void addJunk(WeightedRandomFishable item) { junk.add(item); }
    public static void addTreasure(WeightedRandomFishable item) { treasure.add(item); }

    public static void removeFish(Predicate<WeightedRandomFishable> test) { remove(fish.iterator(), test); }
    public static void removeJunk(Predicate<WeightedRandomFishable> test) { remove(junk.iterator(), test); }
    public static void removeTreasure(Predicate<WeightedRandomFishable> test) { remove(treasure.iterator(), test); }

    public static ItemStack getRandomFishable(Random rand, float chance)
    {
        return getRandomFishable(rand, chance, 0, 0);
    }

    public static ItemStack getRandomFishable(Random rand, float chance, int luck, int speed)
    {
        float junkChance = 0.1F - luck * 0.025F - speed * 0.01F;
        float treasureChance = 0.05F + luck * 0.01F - speed * 0.01F;
        junkChance = MathHelper.clamp_float(junkChance, 0.0F, 1.0F);
        treasureChance = MathHelper.clamp_float(treasureChance, 0.0F, 1.0F);

        if (chance < junkChance)
        {
            return ((WeightedRandomFishable)WeightedRandom.getRandomItem(rand, junk)).func_150708_a(rand);
        }

        chance -= junkChance;
        if (chance < treasureChance)
        {
            return ((WeightedRandomFishable)WeightedRandom.getRandomItem(rand, treasure)).func_150708_a(rand);
        }

        chance -= treasureChance;
        // this is done in EntityFishHook.func_146033_f. more loot types expected?
        {
            return ((WeightedRandomFishable)WeightedRandom.getRandomItem(rand, fish)).func_150708_a(rand);
        }
    }

    public static FishableCategory getFishableCategory(float chance, int luck, int speed)
    {
        float junkChance = 0.1F - luck * 0.025F - speed * 0.01F;
        float treasureChance = 0.05F + luck * 0.01F - speed * 0.01F;
        junkChance = MathHelper.clamp_float(junkChance, 0.0F, 1.0F);
        treasureChance = MathHelper.clamp_float(treasureChance, 0.0F, 1.0F);

        if (chance < junkChance)
        {
            return FishableCategory.JUNK;
        }

        chance -= junkChance;
        if (chance < treasureChance)
        {
            return FishableCategory.TREASURE;
        }

        chance -= treasureChance;
        // this is done in EntityFishHook.func_146033_f. more loot types expected?
        {
            return FishableCategory.FISH;
        }
    }

    private static void remove(Iterator<WeightedRandomFishable> iter, Predicate<WeightedRandomFishable> test)
    {
        while (iter.hasNext())
            if (!test.apply(iter.next()))
                iter.remove();
    }

    static
    {
        fish.addAll(EntityFishHook.field_146036_f);
        junk.addAll(EntityFishHook.field_146039_d);
        treasure.addAll(EntityFishHook.field_146041_e);
    }

    public static enum FishableCategory
    {
        JUNK(StatList.field_151183_A),
        TREASURE(StatList.field_151184_B),
        FISH(StatList.fishCaughtStat);

        public final StatBase stat;

        FishableCategory(StatBase stat)
        {
            this.stat = stat;
        }
    }
}
