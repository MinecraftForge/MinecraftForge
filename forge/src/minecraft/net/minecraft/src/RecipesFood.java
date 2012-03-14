package net.minecraft.src;

public class RecipesFood
{
    /**
     * Adds the food recipes to the CraftingManager.
     */
    public void addRecipes(CraftingManager par1CraftingManager)
    {
        par1CraftingManager.addShapelessRecipe(new ItemStack(Item.bowlSoup), new Object[] {Block.mushroomBrown, Block.mushroomRed, Item.bowlEmpty});
        par1CraftingManager.addRecipe(new ItemStack(Item.cookie, 8), new Object[] {"#X#", 'X', new ItemStack(Item.dyePowder, 1, 3), '#', Item.wheat});
        par1CraftingManager.addRecipe(new ItemStack(Block.melon), new Object[] {"MMM", "MMM", "MMM", 'M', Item.melon});
        par1CraftingManager.addRecipe(new ItemStack(Item.melonSeeds), new Object[] {"M", 'M', Item.melon});
        par1CraftingManager.addRecipe(new ItemStack(Item.pumpkinSeeds, 4), new Object[] {"M", 'M', Block.pumpkin});
        par1CraftingManager.addShapelessRecipe(new ItemStack(Item.fermentedSpiderEye), new Object[] {Item.spiderEye, Block.mushroomBrown, Item.sugar});
        par1CraftingManager.addShapelessRecipe(new ItemStack(Item.speckledMelon), new Object[] {Item.melon, Item.goldNugget});
        par1CraftingManager.addShapelessRecipe(new ItemStack(Item.blazePowder, 2), new Object[] {Item.blazeRod});
        par1CraftingManager.addShapelessRecipe(new ItemStack(Item.magmaCream), new Object[] {Item.blazePowder, Item.slimeBall});
    }
}
