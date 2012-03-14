package net.minecraft.src;

public class RecipesCrafting
{
    /**
     * Adds the crafting recipes to the CraftingManager.
     */
    public void addRecipes(CraftingManager par1CraftingManager)
    {
        par1CraftingManager.addRecipe(new ItemStack(Block.chest), new Object[] {"###", "# #", "###", '#', Block.planks});
        par1CraftingManager.addRecipe(new ItemStack(Block.stoneOvenIdle), new Object[] {"###", "# #", "###", '#', Block.cobblestone});
        par1CraftingManager.addRecipe(new ItemStack(Block.workbench), new Object[] {"##", "##", '#', Block.planks});
        par1CraftingManager.addRecipe(new ItemStack(Block.sandStone), new Object[] {"##", "##", '#', Block.sand});
        par1CraftingManager.addRecipe(new ItemStack(Block.stoneBrick, 4), new Object[] {"##", "##", '#', Block.stone});
        par1CraftingManager.addRecipe(new ItemStack(Block.fenceIron, 16), new Object[] {"###", "###", '#', Item.ingotIron});
        par1CraftingManager.addRecipe(new ItemStack(Block.thinGlass, 16), new Object[] {"###", "###", '#', Block.glass});
        par1CraftingManager.addRecipe(new ItemStack(Block.field_48209_bL, 1), new Object[] {" R ", "RGR", " R ", 'R', Item.redstone, 'G', Block.glowStone});
    }
}
