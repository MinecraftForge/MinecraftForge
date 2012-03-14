package net.minecraft.src;

public class RecipesIngots
{
    private Object[][] recipeItems;

    public RecipesIngots()
    {
        this.recipeItems = new Object[][] {{Block.blockGold, new ItemStack(Item.ingotGold, 9)}, {Block.blockSteel, new ItemStack(Item.ingotIron, 9)}, {Block.blockDiamond, new ItemStack(Item.diamond, 9)}, {Block.blockLapis, new ItemStack(Item.dyePowder, 9, 4)}};
    }

    /**
     * Adds the ingot recipes to the CraftingManager.
     */
    public void addRecipes(CraftingManager par1CraftingManager)
    {
        for (int var2 = 0; var2 < this.recipeItems.length; ++var2)
        {
            Block var3 = (Block)this.recipeItems[var2][0];
            ItemStack var4 = (ItemStack)this.recipeItems[var2][1];
            par1CraftingManager.addRecipe(new ItemStack(var3), new Object[] {"###", "###", "###", '#', var4});
            par1CraftingManager.addRecipe(var4, new Object[] {"#", '#', var3});
        }

        par1CraftingManager.addRecipe(new ItemStack(Item.ingotGold), new Object[] {"###", "###", "###", '#', Item.goldNugget});
        par1CraftingManager.addRecipe(new ItemStack(Item.goldNugget, 9), new Object[] {"#", '#', Item.ingotGold});
    }
}
