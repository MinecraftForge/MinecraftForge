package net.minecraft.src.core;

import net.minecraft.src.*;

public class InfiRecipeItems extends mod_InfiTools
{
    public InfiRecipeItems()
    {
    }

    public static int recipeStorm()
    {
    	ModLoader.addName(ironBucketSand, "Iron Bucket");
    	ModLoader.addName(ironBucketGravel, "Iron Bucket");
    	
        ModLoader.addName(woodBucketEmpty, "Wooden Bucket");
        ModLoader.addName(woodBucketWater, "Wooden Bucket");
        ModLoader.addName(woodBucketMilk, "Wooden Bucket");
        ModLoader.addName(woodBucketSand, "Wooden Bucket");
        ModLoader.addName(woodBucketGravel, "Wooden Bucket");
        ModLoader.addRecipe(new ItemStack(woodBucketEmpty, 1), new Object[]
                {
                    "w w", "w w", " w ", Character.valueOf('w'), Block.planks
                });
        ModLoader.addName(cactusBucketEmpty, "Cactus Bucket");
        ModLoader.addName(cactusBucketWater, "Cactus Bucket");
        ModLoader.addName(cactusBucketMilk, "Cactus Bucket");
        ModLoader.addName(cactusBucketSand, "Cactus Bucket");
        ModLoader.addName(cactusBucketGravel, "Cactus Bucket");
        ModLoader.addRecipe(new ItemStack(cactusBucketEmpty, 1), new Object[]
                {
                    "w w", "w w", " w ", Character.valueOf('w'), Block.cactus
                });
        ModLoader.addName(goldBucketEmpty, "Gold Bucket");
        ModLoader.addName(goldBucketWater, "Gold Bucket");
        ModLoader.addName(goldBucketMilk, "Gold Bucket");
        ModLoader.addName(goldBucketSand, "Gold Bucket");
        ModLoader.addName(goldBucketGravel, "Gold Bucket");
        ModLoader.addName(goldBucketLava, "Gold Bucket");
        ModLoader.addRecipe(new ItemStack(goldBucketEmpty, 1), new Object[]
                {
                    "w w", "w w", " w ", Character.valueOf('w'), Item.ingotGold
                });
        ModLoader.addName(iceBucketEmpty, "Ice Bucket");
        ModLoader.addName(iceBucketIce, "Ice Bucket");
        ModLoader.addName(iceBucketMilk, "Ice Bucket");
        ModLoader.addName(iceBucketSand, "Ice Bucket");
        ModLoader.addName(iceBucketGravel, "Ice Bucket");
        ModLoader.addRecipe(new ItemStack(iceBucketEmpty, 1), new Object[]
                {
                    "w w", "w w", " w ", Character.valueOf('w'), Block.ice
                });
        ModLoader.addName(lavaBucketEmpty, "Lava Bucket");
        ModLoader.addName(lavaBucketGlass, "Lava Bucket");
        ModLoader.addName(lavaBucketCobblestone, "Lava Bucket");
        ModLoader.addName(lavaBucketLava, "Lava Bucket");
        ModLoader.addRecipe(new ItemStack(lavaBucketEmpty, 1), new Object[]
                {
                    "w w", "w w", " w ", Character.valueOf('w'), mod_InfiTools.lavaCrystal
                });
        ModLoader.addName(slimeBucketEmpty, "Slime Bucket");
        ModLoader.addName(slimeBucketWater, "Slime Bucket");
        ModLoader.addName(slimeBucketMilk, "Slime Bucket");
        ModLoader.addName(slimeBucketSand, "Slime Bucket");
        ModLoader.addName(slimeBucketGravel, "Slime Bucket");
        ModLoader.addRecipe(new ItemStack(slimeBucketEmpty, 1), new Object[]
                {
                    "w w", "w w", " w ", Character.valueOf('w'), mod_InfiTools.slimeCrystal
                });
        //Cake recipes
        ModLoader.addRecipe(new ItemStack(Item.cake, 1), new Object[]
                {
                    "AAA", "BEB", "CCC", 'A', mod_InfiTools.cactusBucketMilk, 'B', Item.sugar,
                    'C', Item.wheat, 'E', Item.egg
                });
        ModLoader.addRecipe(new ItemStack(Item.cake, 1), new Object[]
                {
                    "AAA", "BEB", "CCC", 'A', mod_InfiTools.iceBucketMilk, 'B', Item.sugar,
                    'C', Item.wheat, 'E', Item.egg
                });
        ModLoader.addRecipe(new ItemStack(Item.cake, 1), new Object[]
                {
                    "AAA", "BEB", "CCC", 'A', mod_InfiTools.slimeBucketMilk, 'B', Item.sugar,
                    'C', Item.wheat, 'E', Item.egg
                });
        ModLoader.addRecipe(new ItemStack(Item.cake, 1), new Object[]
                {
                    "AAA", "BEB", "CCC", 'A', mod_InfiTools.goldBucketMilk, 'B', Item.sugar,
                    'C', Item.wheat, 'E', Item.egg
                });
        ModLoader.addRecipe(new ItemStack(Item.cake, 1), new Object[]
                {
                    "AAA", "BEB", "CCC", 'A', mod_InfiTools.woodBucketMilk, 'B', Item.sugar,
                    'C', Item.wheat, 'E', Item.egg
                });
        
        ModLoader.addName(stoneBowlEmpty, "Stone Bowl");
        ModLoader.addRecipe(new ItemStack(stoneBowlEmpty, 4), new Object[]
                {
                    "S S", " S ", Character.valueOf('S'), Block.cobblestone
                });
        ModLoader.addName(stoneBowlSoup, "Mushroom Soup");
        ModLoader.addRecipe(new ItemStack(stoneBowlSoup, 1), new Object[]
                {
                    "B", "R", "b", Character.valueOf('B'), Block.mushroomBrown, Character.valueOf('R'), Block.mushroomRed, Character.valueOf('b'), stoneBowlEmpty
                });
        ModLoader.addRecipe(new ItemStack(stoneBowlSoup, 1), new Object[]
                {
                    "R", "B", "b", Character.valueOf('B'), Block.mushroomBrown, Character.valueOf('R'), Block.mushroomRed, Character.valueOf('b'), stoneBowlEmpty
                });
      
        ModLoader.addName(goldBowlEmpty, "Gold Bowl");
        ModLoader.addRecipe(new ItemStack(goldBowlEmpty, 4), new Object[]
                {
                    "S S", " S ", Character.valueOf('S'), Item.ingotGold
                });
        ModLoader.addName(goldBowlSoup, "Mushroom Soup");
        ModLoader.addRecipe(new ItemStack(goldBowlSoup, 1), new Object[]
                {
                    "B", "R", "b", Character.valueOf('B'), Block.mushroomBrown, Character.valueOf('R'), Block.mushroomRed, Character.valueOf('b'), goldBowlEmpty
                });
        ModLoader.addRecipe(new ItemStack(goldBowlSoup, 1), new Object[]
                {
                    "R", "B", "b", Character.valueOf('B'), Block.mushroomBrown, Character.valueOf('R'), Block.mushroomRed, Character.valueOf('b'), goldBowlEmpty
                });
        ModLoader.addName(netherrackBowlEmpty, "Netherrack Bowl");
        ModLoader.addRecipe(new ItemStack(netherrackBowlEmpty, 4), new Object[]
                {
                    "S S", " S ", Character.valueOf('S'), Block.netherrack
                });
        ModLoader.addName(netherrackBowlSoup, "Mushroom Soup");
        ModLoader.addRecipe(new ItemStack(netherrackBowlSoup, 1), new Object[]
                {
                    "B", "R", "b", Character.valueOf('B'), Block.mushroomBrown, Character.valueOf('R'), Block.mushroomRed, Character.valueOf('b'), netherrackBowlEmpty
                });
        ModLoader.addRecipe(new ItemStack(netherrackBowlSoup, 1), new Object[]
                {
                    "R", "B", "b", Character.valueOf('B'), Block.mushroomBrown, Character.valueOf('R'), Block.mushroomRed, Character.valueOf('b'), netherrackBowlEmpty
                });
        ModLoader.addName(slimeBowlEmpty, "Slime Bowl");
        ModLoader.addRecipe(new ItemStack(slimeBowlEmpty, 4), new Object[]
                {
                    "S S", " S ", Character.valueOf('S'), mod_InfiTools.slimeCrystal
                });
        ModLoader.addName(slimeBowlSoup, "Mushroom Soup");
        ModLoader.addRecipe(new ItemStack(slimeBowlSoup, 1), new Object[]
                {
                    "B", "R", "b", Character.valueOf('B'), Block.mushroomBrown, Character.valueOf('R'), Block.mushroomRed, Character.valueOf('b'), slimeBowlEmpty
                });
        ModLoader.addRecipe(new ItemStack(slimeBowlSoup, 1), new Object[]
                {
                    "R", "B", "b", Character.valueOf('B'), Block.mushroomBrown, Character.valueOf('R'), Block.mushroomRed, Character.valueOf('b'), slimeBowlEmpty
                });
        ModLoader.addName(cactusBowlEmpty, "Cactus Bowl");
        ModLoader.addRecipe(new ItemStack(cactusBowlEmpty, 4), new Object[]
                {
                    "S S", " S ", Character.valueOf('S'), Block.cactus
                });
        ModLoader.addName(cactusBowlSoup, "Mushroom Soup");
        ModLoader.addRecipe(new ItemStack(cactusBowlSoup, 1), new Object[]
                {
                    "B", "R", "b", Character.valueOf('B'), Block.mushroomBrown, Character.valueOf('R'), Block.mushroomRed, Character.valueOf('b'), cactusBowlEmpty
                });
        ModLoader.addRecipe(new ItemStack(cactusBowlSoup, 1), new Object[]
                {
                    "R", "B", "b", Character.valueOf('B'), Block.mushroomBrown, Character.valueOf('R'), Block.mushroomRed, Character.valueOf('b'), cactusBowlEmpty
                });
        ModLoader.addName(glassBowlEmpty, "Glass Bowl");
        ModLoader.addRecipe(new ItemStack(glassBowlEmpty, 2), new Object[]
                {
                    "S S", " S ", Character.valueOf('S'), mod_InfiTools.glassShard
                });
        ModLoader.addName(glassBowlSoup, "Mushroom Soup");
        ModLoader.addRecipe(new ItemStack(glassBowlSoup, 1), new Object[]
                {
                    "B", "R", "b", Character.valueOf('B'), Block.mushroomBrown, Character.valueOf('R'), Block.mushroomRed, Character.valueOf('b'), glassBowlEmpty
                });
        ModLoader.addRecipe(new ItemStack(glassBowlSoup, 1), new Object[]
                {
                    "R", "B", "b", Character.valueOf('B'), Block.mushroomBrown, Character.valueOf('R'), Block.mushroomRed, Character.valueOf('b'), glassBowlEmpty
                });
        
        ModLoader.addName(pumpkinPulp, "Pumpkin Pulp");
        ModLoader.addShapelessRecipe(new ItemStack(pumpkinPulp, 4), new Object[]
                {
                    Block.pumpkin, Block.pumpkin
                });
        ModLoader.addRecipe(new ItemStack(pumpkinPulp, 2), new Object[]
                {
                    "X", Character.valueOf('X'), Block.pumpkinLantern
                });
        
        return 0;
    }
}
