package net.minecraft.item.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class RecipesCrafting
{
    private static final String __OBFID = "CL_00000095";

    // JAVADOC METHOD $$ func_77589_a
    public void addRecipes(CraftingManager par1CraftingManager)
    {
        par1CraftingManager.addRecipe(new ItemStack(Blocks.chest), new Object[] {"###", "# #", "###", '#', Blocks.planks});
        par1CraftingManager.addRecipe(new ItemStack(Blocks.trapped_chest), new Object[] {"#-", '#', Blocks.chest, '-', Blocks.tripwire_hook});
        par1CraftingManager.addRecipe(new ItemStack(Blocks.ender_chest), new Object[] {"###", "#E#", "###", '#', Blocks.obsidian, 'E', Items.ender_eye});
        par1CraftingManager.addRecipe(new ItemStack(Blocks.furnace), new Object[] {"###", "# #", "###", '#', Blocks.cobblestone});
        par1CraftingManager.addRecipe(new ItemStack(Blocks.crafting_table), new Object[] {"##", "##", '#', Blocks.planks});
        par1CraftingManager.addRecipe(new ItemStack(Blocks.sandstone), new Object[] {"##", "##", '#', new ItemStack(Blocks.sand, 1, 0)});
        par1CraftingManager.addRecipe(new ItemStack(Blocks.sandstone, 4, 2), new Object[] {"##", "##", '#', Blocks.sandstone});
        par1CraftingManager.addRecipe(new ItemStack(Blocks.sandstone, 1, 1), new Object[] {"#", "#", '#', new ItemStack(Blocks.stone_slab, 1, 1)});
        par1CraftingManager.addRecipe(new ItemStack(Blocks.quartz_block, 1, 1), new Object[] {"#", "#", '#', new ItemStack(Blocks.stone_slab, 1, 7)});
        par1CraftingManager.addRecipe(new ItemStack(Blocks.quartz_block, 2, 2), new Object[] {"#", "#", '#', new ItemStack(Blocks.quartz_block, 1, 0)});
        par1CraftingManager.addRecipe(new ItemStack(Blocks.stonebrick, 4), new Object[] {"##", "##", '#', Blocks.stone});
        par1CraftingManager.addRecipe(new ItemStack(Blocks.iron_bars, 16), new Object[] {"###", "###", '#', Items.iron_ingot});
        par1CraftingManager.addRecipe(new ItemStack(Blocks.glass_pane, 16), new Object[] {"###", "###", '#', Blocks.glass});
        par1CraftingManager.addRecipe(new ItemStack(Blocks.redstone_lamp, 1), new Object[] {" R ", "RGR", " R ", 'R', Items.redstone, 'G', Blocks.glowstone});
        par1CraftingManager.addRecipe(new ItemStack(Blocks.beacon, 1), new Object[] {"GGG", "GSG", "OOO", 'G', Blocks.glass, 'S', Items.nether_star, 'O', Blocks.obsidian});
        par1CraftingManager.addRecipe(new ItemStack(Blocks.nether_brick, 1), new Object[] {"NN", "NN", 'N', Items.netherbrick});
    }
}