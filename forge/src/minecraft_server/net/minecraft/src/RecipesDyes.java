package net.minecraft.src;

public class RecipesDyes
{
    /**
     * Adds the dye recipes to the CraftingManager.
     */
    public void addRecipes(CraftingManager par1CraftingManager)
    {
        for (int var2 = 0; var2 < 16; ++var2)
        {
            par1CraftingManager.addShapelessRecipe(new ItemStack(Block.cloth, 1, BlockCloth.getDyeFromBlock(var2)), new Object[] {new ItemStack(Item.dyePowder, 1, var2), new ItemStack(Item.itemsList[Block.cloth.blockID], 1, 0)});
        }

        par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 2, 11), new Object[] {Block.plantYellow});
        par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 2, 1), new Object[] {Block.plantRed});
        par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 3, 15), new Object[] {Item.bone});
        par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 2, 9), new Object[] {new ItemStack(Item.dyePowder, 1, 1), new ItemStack(Item.dyePowder, 1, 15)});
        par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 2, 14), new Object[] {new ItemStack(Item.dyePowder, 1, 1), new ItemStack(Item.dyePowder, 1, 11)});
        par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 2, 10), new Object[] {new ItemStack(Item.dyePowder, 1, 2), new ItemStack(Item.dyePowder, 1, 15)});
        par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 2, 8), new Object[] {new ItemStack(Item.dyePowder, 1, 0), new ItemStack(Item.dyePowder, 1, 15)});
        par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 2, 7), new Object[] {new ItemStack(Item.dyePowder, 1, 8), new ItemStack(Item.dyePowder, 1, 15)});
        par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 3, 7), new Object[] {new ItemStack(Item.dyePowder, 1, 0), new ItemStack(Item.dyePowder, 1, 15), new ItemStack(Item.dyePowder, 1, 15)});
        par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 2, 12), new Object[] {new ItemStack(Item.dyePowder, 1, 4), new ItemStack(Item.dyePowder, 1, 15)});
        par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 2, 6), new Object[] {new ItemStack(Item.dyePowder, 1, 4), new ItemStack(Item.dyePowder, 1, 2)});
        par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 2, 5), new Object[] {new ItemStack(Item.dyePowder, 1, 4), new ItemStack(Item.dyePowder, 1, 1)});
        par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 2, 13), new Object[] {new ItemStack(Item.dyePowder, 1, 5), new ItemStack(Item.dyePowder, 1, 9)});
        par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 3, 13), new Object[] {new ItemStack(Item.dyePowder, 1, 4), new ItemStack(Item.dyePowder, 1, 1), new ItemStack(Item.dyePowder, 1, 9)});
        par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 4, 13), new Object[] {new ItemStack(Item.dyePowder, 1, 4), new ItemStack(Item.dyePowder, 1, 1), new ItemStack(Item.dyePowder, 1, 1), new ItemStack(Item.dyePowder, 1, 15)});
    }
}
