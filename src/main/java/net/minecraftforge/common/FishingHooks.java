package net.minecraftforge.common;

import java.util.ArrayList;
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

	public static ItemStack getRandomfishable(Random rand, float chance)
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

	public static void addFish(WeightedRandomFishable item) { fish.add(item); }
	public static void addJunk(WeightedRandomFishable item) { junk.add(item); }
	public static void addTreasure(WeightedRandomFishable item) { treasure.add(item); }

	static
	{
		fish.addAll(EntityFishHook.field_146036_f);
		junk.addAll(EntityFishHook.field_146039_d);
		treasure.addAll(EntityFishHook.field_146041_e);
	}
}
