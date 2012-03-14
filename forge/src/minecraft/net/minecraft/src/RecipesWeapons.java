package net.minecraft.src;

public class RecipesWeapons
{
    private String[][] recipePatterns = new String[][] {{"X", "X", "#"}};
    private Object[][] recipeItems;

    public RecipesWeapons()
    {
        this.recipeItems = new Object[][] {{Block.planks, Block.cobblestone, Item.ingotIron, Item.diamond, Item.ingotGold}, {Item.swordWood, Item.swordStone, Item.swordSteel, Item.swordDiamond, Item.swordGold}};
    }

    /**
     * Adds the weapon recipes to the CraftingManager.
     */
    public void addRecipes(CraftingManager par1CraftingManager)
    {
        for (int var2 = 0; var2 < this.recipeItems[0].length; ++var2)
        {
            Object var3 = this.recipeItems[0][var2];

            for (int var4 = 0; var4 < this.recipeItems.length - 1; ++var4)
            {
                Item var5 = (Item)this.recipeItems[var4 + 1][var2];
                par1CraftingManager.addRecipe(new ItemStack(var5), new Object[] {this.recipePatterns[var4], '#', Item.stick, 'X', var3});
            }
        }

        par1CraftingManager.addRecipe(new ItemStack(Item.bow, 1), new Object[] {" #X", "# X", " #X", 'X', Item.silk, '#', Item.stick});
        par1CraftingManager.addRecipe(new ItemStack(Item.arrow, 4), new Object[] {"X", "#", "Y", 'Y', Item.feather, 'X', Item.flint, '#', Item.stick});
    }
}
