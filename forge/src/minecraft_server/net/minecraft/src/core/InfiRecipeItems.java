package net.minecraft.src.core;

import net.minecraft.src.*;

public class InfiRecipeItems extends mod_InfiTools
{
    public InfiRecipeItems()
    {
    }

    public static int recipeStorm()
    {
        
        
        
        
        
        ModLoader.addRecipe(new ItemStack(woodBucketEmpty, 1), new Object[]
                {
                    "w w", "w w", " w ", Character.valueOf('w'), Block.planks
                });
        
        
        
        
        
        ModLoader.addRecipe(new ItemStack(cactusBucketEmpty, 1), new Object[]
                {
                    "w w", "w w", " w ", Character.valueOf('w'), Block.cactus
                });
        
        
        
        
        
        
        ModLoader.addRecipe(new ItemStack(goldBucketEmpty, 1), new Object[]
                {
                    "w w", "w w", " w ", Character.valueOf('w'), Item.ingotGold
                });
        
        
        
        
        
        ModLoader.addRecipe(new ItemStack(iceBucketEmpty, 1), new Object[]
                {
                    "w w", "w w", " w ", Character.valueOf('w'), Block.ice
                });
        
        
        
        
        ModLoader.addRecipe(new ItemStack(lavaBucketEmpty, 1), new Object[]
                {
                    "w w", "w w", " w ", Character.valueOf('w'), mod_InfiTools.lavaCrystal
                });
        
        
        
        
        
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
        
        
        ModLoader.addRecipe(new ItemStack(stoneBowlEmpty, 4), new Object[]
                {
                    "S S", " S ", Character.valueOf('S'), Block.cobblestone
                });
        
        ModLoader.addRecipe(new ItemStack(stoneBowlSoup, 1), new Object[]
                {
                    "B", "R", "b", Character.valueOf('B'), Block.mushroomBrown, Character.valueOf('R'), Block.mushroomRed, Character.valueOf('b'), stoneBowlEmpty
                });
        ModLoader.addRecipe(new ItemStack(stoneBowlSoup, 1), new Object[]
                {
                    "R", "B", "b", Character.valueOf('B'), Block.mushroomBrown, Character.valueOf('R'), Block.mushroomRed, Character.valueOf('b'), stoneBowlEmpty
                });
      
        
        ModLoader.addRecipe(new ItemStack(goldBowlEmpty, 4), new Object[]
                {
                    "S S", " S ", Character.valueOf('S'), Item.ingotGold
                });
        
        ModLoader.addRecipe(new ItemStack(goldBowlSoup, 1), new Object[]
                {
                    "B", "R", "b", Character.valueOf('B'), Block.mushroomBrown, Character.valueOf('R'), Block.mushroomRed, Character.valueOf('b'), goldBowlEmpty
                });
        ModLoader.addRecipe(new ItemStack(goldBowlSoup, 1), new Object[]
                {
                    "R", "B", "b", Character.valueOf('B'), Block.mushroomBrown, Character.valueOf('R'), Block.mushroomRed, Character.valueOf('b'), goldBowlEmpty
                });
        
        ModLoader.addRecipe(new ItemStack(netherrackBowlEmpty, 4), new Object[]
                {
                    "S S", " S ", Character.valueOf('S'), Block.netherrack
                });
        
        ModLoader.addRecipe(new ItemStack(netherrackBowlSoup, 1), new Object[]
                {
                    "B", "R", "b", Character.valueOf('B'), Block.mushroomBrown, Character.valueOf('R'), Block.mushroomRed, Character.valueOf('b'), netherrackBowlEmpty
                });
        ModLoader.addRecipe(new ItemStack(netherrackBowlSoup, 1), new Object[]
                {
                    "R", "B", "b", Character.valueOf('B'), Block.mushroomBrown, Character.valueOf('R'), Block.mushroomRed, Character.valueOf('b'), netherrackBowlEmpty
                });
        
        ModLoader.addRecipe(new ItemStack(slimeBowlEmpty, 4), new Object[]
                {
                    "S S", " S ", Character.valueOf('S'), mod_InfiTools.slimeCrystal
                });
        
        ModLoader.addRecipe(new ItemStack(slimeBowlSoup, 1), new Object[]
                {
                    "B", "R", "b", Character.valueOf('B'), Block.mushroomBrown, Character.valueOf('R'), Block.mushroomRed, Character.valueOf('b'), slimeBowlEmpty
                });
        ModLoader.addRecipe(new ItemStack(slimeBowlSoup, 1), new Object[]
                {
                    "R", "B", "b", Character.valueOf('B'), Block.mushroomBrown, Character.valueOf('R'), Block.mushroomRed, Character.valueOf('b'), slimeBowlEmpty
                });
        
        ModLoader.addRecipe(new ItemStack(cactusBowlEmpty, 4), new Object[]
                {
                    "S S", " S ", Character.valueOf('S'), Block.cactus
                });
        
        ModLoader.addRecipe(new ItemStack(cactusBowlSoup, 1), new Object[]
                {
                    "B", "R", "b", Character.valueOf('B'), Block.mushroomBrown, Character.valueOf('R'), Block.mushroomRed, Character.valueOf('b'), cactusBowlEmpty
                });
        ModLoader.addRecipe(new ItemStack(cactusBowlSoup, 1), new Object[]
                {
                    "R", "B", "b", Character.valueOf('B'), Block.mushroomBrown, Character.valueOf('R'), Block.mushroomRed, Character.valueOf('b'), cactusBowlEmpty
                });
        
        ModLoader.addRecipe(new ItemStack(glassBowlEmpty, 2), new Object[]
                {
                    "S S", " S ", Character.valueOf('S'), mod_InfiTools.glassShard
                });
        
        ModLoader.addRecipe(new ItemStack(glassBowlSoup, 1), new Object[]
                {
                    "B", "R", "b", Character.valueOf('B'), Block.mushroomBrown, Character.valueOf('R'), Block.mushroomRed, Character.valueOf('b'), glassBowlEmpty
                });
        ModLoader.addRecipe(new ItemStack(glassBowlSoup, 1), new Object[]
                {
                    "R", "B", "b", Character.valueOf('B'), Block.mushroomBrown, Character.valueOf('R'), Block.mushroomRed, Character.valueOf('b'), glassBowlEmpty
                });
        
        
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
