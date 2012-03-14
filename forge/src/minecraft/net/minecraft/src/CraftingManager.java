package net.minecraft.src;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CraftingManager
{
    /** The static instance of this class */
    private static final CraftingManager instance = new CraftingManager();

    /** A list of all the recipes added */
    private List recipes = new ArrayList();

    /**
     * Returns the static instance of this class
     */
    public static final CraftingManager getInstance()
    {
        return instance;
    }

    private CraftingManager()
    {
        (new RecipesTools()).addRecipes(this);
        (new RecipesWeapons()).addRecipes(this);
        (new RecipesIngots()).addRecipes(this);
        (new RecipesFood()).addRecipes(this);
        (new RecipesCrafting()).addRecipes(this);
        (new RecipesArmor()).addRecipes(this);
        (new RecipesDyes()).addRecipes(this);
        this.addRecipe(new ItemStack(Item.paper, 3), new Object[] {"###", '#', Item.reed});
        this.addRecipe(new ItemStack(Item.book, 1), new Object[] {"#", "#", "#", '#', Item.paper});
        this.addRecipe(new ItemStack(Block.fence, 2), new Object[] {"###", "###", '#', Item.stick});
        this.addRecipe(new ItemStack(Block.netherFence, 6), new Object[] {"###", "###", '#', Block.netherBrick});
        this.addRecipe(new ItemStack(Block.fenceGate, 1), new Object[] {"#W#", "#W#", '#', Item.stick, 'W', Block.planks});
        this.addRecipe(new ItemStack(Block.jukebox, 1), new Object[] {"###", "#X#", "###", '#', Block.planks, 'X', Item.diamond});
        this.addRecipe(new ItemStack(Block.music, 1), new Object[] {"###", "#X#", "###", '#', Block.planks, 'X', Item.redstone});
        this.addRecipe(new ItemStack(Block.bookShelf, 1), new Object[] {"###", "XXX", "###", '#', Block.planks, 'X', Item.book});
        this.addRecipe(new ItemStack(Block.blockSnow, 1), new Object[] {"##", "##", '#', Item.snowball});
        this.addRecipe(new ItemStack(Block.blockClay, 1), new Object[] {"##", "##", '#', Item.clay});
        this.addRecipe(new ItemStack(Block.brick, 1), new Object[] {"##", "##", '#', Item.brick});
        this.addRecipe(new ItemStack(Block.glowStone, 1), new Object[] {"##", "##", '#', Item.lightStoneDust});
        this.addRecipe(new ItemStack(Block.cloth, 1), new Object[] {"##", "##", '#', Item.silk});
        this.addRecipe(new ItemStack(Block.tnt, 1), new Object[] {"X#X", "#X#", "X#X", 'X', Item.gunpowder, '#', Block.sand});
        this.addRecipe(new ItemStack(Block.stairSingle, 6, 3), new Object[] {"###", '#', Block.cobblestone});
        this.addRecipe(new ItemStack(Block.stairSingle, 6, 0), new Object[] {"###", '#', Block.stone});
        this.addRecipe(new ItemStack(Block.stairSingle, 6, 1), new Object[] {"###", '#', Block.sandStone});
        this.addRecipe(new ItemStack(Block.stairSingle, 6, 2), new Object[] {"###", '#', Block.planks});
        this.addRecipe(new ItemStack(Block.stairSingle, 6, 4), new Object[] {"###", '#', Block.brick});
        this.addRecipe(new ItemStack(Block.stairSingle, 6, 5), new Object[] {"###", '#', Block.stoneBrick});
        this.addRecipe(new ItemStack(Block.ladder, 3), new Object[] {"# #", "###", "# #", '#', Item.stick});
        this.addRecipe(new ItemStack(Item.doorWood, 1), new Object[] {"##", "##", "##", '#', Block.planks});
        this.addRecipe(new ItemStack(Block.trapdoor, 2), new Object[] {"###", "###", '#', Block.planks});
        this.addRecipe(new ItemStack(Item.doorSteel, 1), new Object[] {"##", "##", "##", '#', Item.ingotIron});
        this.addRecipe(new ItemStack(Item.sign, 1), new Object[] {"###", "###", " X ", '#', Block.planks, 'X', Item.stick});
        this.addRecipe(new ItemStack(Item.cake, 1), new Object[] {"AAA", "BEB", "CCC", 'A', Item.bucketMilk, 'B', Item.sugar, 'C', Item.wheat, 'E', Item.egg});
        this.addRecipe(new ItemStack(Item.sugar, 1), new Object[] {"#", '#', Item.reed});
        this.addRecipe(new ItemStack(Block.planks, 4), new Object[] {"#", '#', Block.wood});
        this.addRecipe(new ItemStack(Item.stick, 4), new Object[] {"#", "#", '#', Block.planks});
        this.addRecipe(new ItemStack(Block.torchWood, 4), new Object[] {"X", "#", 'X', Item.coal, '#', Item.stick});
        this.addRecipe(new ItemStack(Block.torchWood, 4), new Object[] {"X", "#", 'X', new ItemStack(Item.coal, 1, 1), '#', Item.stick});
        this.addRecipe(new ItemStack(Item.bowlEmpty, 4), new Object[] {"# #", " # ", '#', Block.planks});
        this.addRecipe(new ItemStack(Item.glassBottle, 3), new Object[] {"# #", " # ", '#', Block.glass});
        this.addRecipe(new ItemStack(Block.rail, 16), new Object[] {"X X", "X#X", "X X", 'X', Item.ingotIron, '#', Item.stick});
        this.addRecipe(new ItemStack(Block.railPowered, 6), new Object[] {"X X", "X#X", "XRX", 'X', Item.ingotGold, 'R', Item.redstone, '#', Item.stick});
        this.addRecipe(new ItemStack(Block.railDetector, 6), new Object[] {"X X", "X#X", "XRX", 'X', Item.ingotIron, 'R', Item.redstone, '#', Block.pressurePlateStone});
        this.addRecipe(new ItemStack(Item.minecartEmpty, 1), new Object[] {"# #", "###", '#', Item.ingotIron});
        this.addRecipe(new ItemStack(Item.cauldron, 1), new Object[] {"# #", "# #", "###", '#', Item.ingotIron});
        this.addRecipe(new ItemStack(Item.brewingStand, 1), new Object[] {" B ", "###", '#', Block.cobblestone, 'B', Item.blazeRod});
        this.addRecipe(new ItemStack(Block.pumpkinLantern, 1), new Object[] {"A", "B", 'A', Block.pumpkin, 'B', Block.torchWood});
        this.addRecipe(new ItemStack(Item.minecartCrate, 1), new Object[] {"A", "B", 'A', Block.chest, 'B', Item.minecartEmpty});
        this.addRecipe(new ItemStack(Item.minecartPowered, 1), new Object[] {"A", "B", 'A', Block.stoneOvenIdle, 'B', Item.minecartEmpty});
        this.addRecipe(new ItemStack(Item.boat, 1), new Object[] {"# #", "###", '#', Block.planks});
        this.addRecipe(new ItemStack(Item.bucketEmpty, 1), new Object[] {"# #", " # ", '#', Item.ingotIron});
        this.addRecipe(new ItemStack(Item.flintAndSteel, 1), new Object[] {"A ", " B", 'A', Item.ingotIron, 'B', Item.flint});
        this.addRecipe(new ItemStack(Item.bread, 1), new Object[] {"###", '#', Item.wheat});
        this.addRecipe(new ItemStack(Block.stairCompactPlanks, 4), new Object[] {"#  ", "## ", "###", '#', Block.planks});
        this.addRecipe(new ItemStack(Item.fishingRod, 1), new Object[] {"  #", " #X", "# X", '#', Item.stick, 'X', Item.silk});
        this.addRecipe(new ItemStack(Block.stairCompactCobblestone, 4), new Object[] {"#  ", "## ", "###", '#', Block.cobblestone});
        this.addRecipe(new ItemStack(Block.stairsBrick, 4), new Object[] {"#  ", "## ", "###", '#', Block.brick});
        this.addRecipe(new ItemStack(Block.stairsStoneBrickSmooth, 4), new Object[] {"#  ", "## ", "###", '#', Block.stoneBrick});
        this.addRecipe(new ItemStack(Block.stairsNetherBrick, 4), new Object[] {"#  ", "## ", "###", '#', Block.netherBrick});
        this.addRecipe(new ItemStack(Item.painting, 1), new Object[] {"###", "#X#", "###", '#', Item.stick, 'X', Block.cloth});
        this.addRecipe(new ItemStack(Item.appleGold, 1), new Object[] {"###", "#X#", "###", '#', Item.goldNugget, 'X', Item.appleRed});
        this.addRecipe(new ItemStack(Block.lever, 1), new Object[] {"X", "#", '#', Block.cobblestone, 'X', Item.stick});
        this.addRecipe(new ItemStack(Block.torchRedstoneActive, 1), new Object[] {"X", "#", '#', Item.stick, 'X', Item.redstone});
        this.addRecipe(new ItemStack(Item.redstoneRepeater, 1), new Object[] {"#X#", "III", '#', Block.torchRedstoneActive, 'X', Item.redstone, 'I', Block.stone});
        this.addRecipe(new ItemStack(Item.pocketSundial, 1), new Object[] {" # ", "#X#", " # ", '#', Item.ingotGold, 'X', Item.redstone});
        this.addRecipe(new ItemStack(Item.compass, 1), new Object[] {" # ", "#X#", " # ", '#', Item.ingotIron, 'X', Item.redstone});
        this.addRecipe(new ItemStack(Item.map, 1), new Object[] {"###", "#X#", "###", '#', Item.paper, 'X', Item.compass});
        this.addRecipe(new ItemStack(Block.button, 1), new Object[] {"#", "#", '#', Block.stone});
        this.addRecipe(new ItemStack(Block.pressurePlateStone, 1), new Object[] {"##", '#', Block.stone});
        this.addRecipe(new ItemStack(Block.pressurePlatePlanks, 1), new Object[] {"##", '#', Block.planks});
        this.addRecipe(new ItemStack(Block.dispenser, 1), new Object[] {"###", "#X#", "#R#", '#', Block.cobblestone, 'X', Item.bow, 'R', Item.redstone});
        this.addRecipe(new ItemStack(Block.pistonBase, 1), new Object[] {"TTT", "#X#", "#R#", '#', Block.cobblestone, 'X', Item.ingotIron, 'R', Item.redstone, 'T', Block.planks});
        this.addRecipe(new ItemStack(Block.pistonStickyBase, 1), new Object[] {"S", "P", 'S', Item.slimeBall, 'P', Block.pistonBase});
        this.addRecipe(new ItemStack(Item.bed, 1), new Object[] {"###", "XXX", '#', Block.cloth, 'X', Block.planks});
        this.addRecipe(new ItemStack(Block.enchantmentTable, 1), new Object[] {" B ", "D#D", "###", '#', Block.obsidian, 'B', Item.book, 'D', Item.diamond});
        this.addShapelessRecipe(new ItemStack(Item.eyeOfEnder, 1), new Object[] {Item.enderPearl, Item.blazePowder});
        this.addShapelessRecipe(new ItemStack(Item.field_48439_bE, 3), new Object[] {Item.gunpowder, Item.blazePowder, Item.coal});
        this.addShapelessRecipe(new ItemStack(Item.field_48439_bE, 3), new Object[] {Item.gunpowder, Item.blazePowder, new ItemStack(Item.coal, 1, 1)});
        Collections.sort(this.recipes, new RecipeSorter(this));
        System.out.println(this.recipes.size() + " recipes");
    }

    /**
     * Adds a recipe. See spreadsheet on first page for details.
     */
    void addRecipe(ItemStack par1ItemStack, Object ... par2ArrayOfObj)
    {
        String var3 = "";
        int var4 = 0;
        int var5 = 0;
        int var6 = 0;

        if (par2ArrayOfObj[var4] instanceof String[])
        {
            String[] var7 = (String[])((String[])par2ArrayOfObj[var4++]);

            for (int var8 = 0; var8 < var7.length; ++var8)
            {
                String var9 = var7[var8];
                ++var6;
                var5 = var9.length();
                var3 = var3 + var9;
            }
        }
        else
        {
            while (par2ArrayOfObj[var4] instanceof String)
            {
                String var11 = (String)par2ArrayOfObj[var4++];
                ++var6;
                var5 = var11.length();
                var3 = var3 + var11;
            }
        }

        HashMap var12;

        for (var12 = new HashMap(); var4 < par2ArrayOfObj.length; var4 += 2)
        {
            Character var13 = (Character)par2ArrayOfObj[var4];
            ItemStack var14 = null;

            if (par2ArrayOfObj[var4 + 1] instanceof Item)
            {
                var14 = new ItemStack((Item)par2ArrayOfObj[var4 + 1]);
            }
            else if (par2ArrayOfObj[var4 + 1] instanceof Block)
            {
                var14 = new ItemStack((Block)par2ArrayOfObj[var4 + 1], 1, -1);
            }
            else if (par2ArrayOfObj[var4 + 1] instanceof ItemStack)
            {
                var14 = (ItemStack)par2ArrayOfObj[var4 + 1];
            }

            var12.put(var13, var14);
        }

        ItemStack[] var15 = new ItemStack[var5 * var6];

        for (int var16 = 0; var16 < var5 * var6; ++var16)
        {
            char var10 = var3.charAt(var16);

            if (var12.containsKey(Character.valueOf(var10)))
            {
                var15[var16] = ((ItemStack)var12.get(Character.valueOf(var10))).copy();
            }
            else
            {
                var15[var16] = null;
            }
        }

        this.recipes.add(new ShapedRecipes(var5, var6, var15, par1ItemStack));
    }

    void addShapelessRecipe(ItemStack par1ItemStack, Object ... par2ArrayOfObj)
    {
        ArrayList var3 = new ArrayList();
        Object[] var4 = par2ArrayOfObj;
        int var5 = par2ArrayOfObj.length;

        for (int var6 = 0; var6 < var5; ++var6)
        {
            Object var7 = var4[var6];

            if (var7 instanceof ItemStack)
            {
                var3.add(((ItemStack)var7).copy());
            }
            else if (var7 instanceof Item)
            {
                var3.add(new ItemStack((Item)var7));
            }
            else
            {
                if (!(var7 instanceof Block))
                {
                    throw new RuntimeException("Invalid shapeless recipy!");
                }

                var3.add(new ItemStack((Block)var7));
            }
        }

        this.recipes.add(new ShapelessRecipes(par1ItemStack, var3));
    }

    public ItemStack findMatchingRecipe(InventoryCrafting par1InventoryCrafting)
    {
        int var2 = 0;
        ItemStack var3 = null;
        ItemStack var4 = null;
        int var5;

        for (var5 = 0; var5 < par1InventoryCrafting.getSizeInventory(); ++var5)
        {
            ItemStack var6 = par1InventoryCrafting.getStackInSlot(var5);

            if (var6 != null)
            {
                if (var2 == 0)
                {
                    var3 = var6;
                }

                if (var2 == 1)
                {
                    var4 = var6;
                }

                ++var2;
            }
        }

        if (var2 == 2 && var3.itemID == var4.itemID && var3.stackSize == 1 && var4.stackSize == 1 && Item.itemsList[var3.itemID].isRepairable())
        {
            Item var11 = Item.itemsList[var3.itemID];
            int var10 = var11.getMaxDamage() - var3.getItemDamageForDisplay();
            int var7 = var11.getMaxDamage() - var4.getItemDamageForDisplay();
            int var8 = var10 + var7 + var11.getMaxDamage() * 10 / 100;
            int var9 = var11.getMaxDamage() - var8;

            if (var9 < 0)
            {
                var9 = 0;
            }

            return new ItemStack(var3.itemID, 1, var9);
        }
        else
        {
            for (var5 = 0; var5 < this.recipes.size(); ++var5)
            {
                IRecipe var12 = (IRecipe)this.recipes.get(var5);

                if (var12.matches(par1InventoryCrafting))
                {
                    return var12.getCraftingResult(par1InventoryCrafting);
                }
            }

            return null;
        }
    }

    public List getRecipeList()
    {
        return this.recipes;
    }
}
