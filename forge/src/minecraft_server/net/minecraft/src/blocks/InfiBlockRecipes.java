package net.minecraft.src.blocks;

import net.minecraft.src.*;

import java.util.*;

public class InfiBlockRecipes
{	
	static ItemStack[] workbenchArray = {
		new ItemStack(Block.cobblestone),
		new ItemStack(Item.ingotIron),
		new ItemStack(Item.redstone),
		new ItemStack(Item.bone),
		new ItemStack(Block.sandStone),
		new ItemStack(Item.dyePowder, 1, 4),
		new ItemStack(Block.obsidian),
		new ItemStack(Block.cactus),
		new ItemStack(Block.netherrack),
		new ItemStack(Block.ice),
		new ItemStack(Block.stoneBrick)
	};
	
	static ItemStack[] furnaceArray = {
		//new ItemStack(mod_InfiTools.ironChunks),
		new ItemStack(Item.brick),
		new ItemStack(Block.sandStone),
		new ItemStack(Block.obsidian),
		new ItemStack(Item.redstone),
		new ItemStack(Block.netherrack),
		new ItemStack(Block.stoneBrick),
		new ItemStack(Block.whiteStone),
		new ItemStack(Block.glowStone)
	};
	
	static ItemStack[] magicSlabStoneArray = {
		new ItemStack(Block.stone),
		new ItemStack(Block.stairSingle, 1, 0),
		new ItemStack(Block.cobblestone),
		new ItemStack(Block.stoneBrick, 1, 0),
		new ItemStack(Block.stoneBrick, 1, 1),
		new ItemStack(Block.stoneBrick, 1, 2),
		new ItemStack(Block.brick),
		new ItemStack(Block.cobblestoneMossy),
		new ItemStack(Block.obsidian),
		new ItemStack(Block.netherrack),
		new ItemStack(Block.sandStone),
		new ItemStack(Item.ingotIron),
		new ItemStack(Item.ingotGold),
		new ItemStack(Item.diamond),
		new ItemStack(Block.whiteStone),
		new ItemStack(Block.netherBrick)
	};
	
	static ItemStack[] magicSlabSoilArray = {
		new ItemStack(Block.dirt),
		new ItemStack(Block.grass),
		new ItemStack(Block.mycelium),
		new ItemStack(Block.sand),
		new ItemStack(Block.gravel),
		new ItemStack(Block.wood, 1, 0),
		new ItemStack(Block.wood, 1, 1),
		new ItemStack(Block.wood, 1, 2),
		new ItemStack(Block.planks),
		new ItemStack(Block.blockSnow),
		new ItemStack(Block.slowSand),
		new ItemStack(Block.mushroomBrown),
		new ItemStack(Block.mushroomRed),
		new ItemStack(Block.glowStone),
		new ItemStack(Block.glass),
		new ItemStack(Block.leaves, 1, -1)
	};
	
	public InfiBlockRecipes()
	{
	}

	public static void recipeStorm()
	{
		for (int iter = 0; iter < workbenchArray.length; iter++)
		{
		ModLoader.addRecipe(new ItemStack(mod_InfiBlocks.workbench, 1, iter), new Object[]
				{
					"bb", "bb", 'b', workbenchArray[iter]
				});
		}
		
		for (int iter = 0; iter < furnaceArray.length; iter++)
		{
			ModLoader.addRecipe(new ItemStack(mod_InfiBlocks.furnace, 1, iter+1), new Object[]
			{
				"bbb", "b b", "bbb", 'b', furnaceArray[iter]
			});
		}
		
		ModLoader.addRecipe(new ItemStack(mod_InfiBlocks.furnace, 1, 0), new Object[]
		{
			"bxb", "x x", "bxb", 'b', Item.ingotIron, 'x', Block.cobblestone
		});
		
		ModLoader.addRecipe(new ItemStack(mod_InfiBlocks.furnace, 1, 0), new Object[]
		{
			"bxb", "x x", "bxb", 'b', Item.ingotIron, 'x', Block.stone
		});
		
		
		//Create carpet
		for (int i = 0; i <= 15; i++)
		{
			ModLoader.addRecipe(new ItemStack(mod_InfiBlocks.woolCarpet, 4, i), new Object[]
					{
						"b", 'b', new ItemStack(Block.cloth, 1, i)
					});
		}

		//Carpet dye
		for (int j = 0; j <= 15; j++)
		{
			ModLoader.addShapelessRecipe(new ItemStack(mod_InfiBlocks.woolCarpet, 1, j), new Object[]
					{
						new ItemStack(mod_InfiBlocks.woolCarpet, 1, -1), new ItemStack(Item.dyePowder, 1, 15-j)
					});
		}

		//Cheap carpet dye
		for (int k = 0; k <= 15; k++)
		{
			ModLoader.addRecipe(new ItemStack(mod_InfiBlocks.woolCarpet, 8, k), new Object[]
					{
						"ccc", "cdc", "ccc", 'c', new ItemStack(mod_InfiBlocks.woolCarpet, 1, -1), 
						'd', new ItemStack(Item.dyePowder, 1, 15-k)
					});
		}
		
		//Stained Glass
		for (int j = 0; j <= 15; j++)
		{
			ModLoader.addShapelessRecipe(new ItemStack(mod_InfiBlocks.stainedGlass, 1, j), new Object[]
			{
				new ItemStack(Block.glass), new ItemStack(Item.dyePowder, 1, 15-j)
			});
		}

		//Cheap Stained Glass
		for (int k = 0; k <= 15; k++)
		{
			ModLoader.addRecipe(new ItemStack(mod_InfiBlocks.stainedGlass, 8, k), new Object[]
			{
				"ccc", "cdc", "ccc", 'c', new ItemStack(Block.glass), 
				'd', new ItemStack(Item.dyePowder, 1, 15-k)
			});
		}
		
		//Stained Glass Pane
		for (int k = 0; k <= 15; k++)
		{
			ModLoader.addRecipe(new ItemStack(mod_InfiBlocks.stainedGlassPane, 16, k), new Object[]
			{
				"ccc", "ccc", 'c', new ItemStack(mod_InfiBlocks.stainedGlass, 1, k)
			});
		}
		
		ItemStack[] glassArray = {
			new ItemStack(mod_InfiBlocks.infiGlass, 1, 0),
			new ItemStack(mod_InfiBlocks.infiGlass, 1, 1),
			new ItemStack(mod_InfiBlocks.infiGlass, 1, 2)
		};
		
		//InfiGlass Pane
		for (int k = 0; k < glassArray.length; k++)
		{
			ModLoader.addRecipe(new ItemStack(mod_InfiBlocks.infiGlassPane, 16, k), new Object[]
			{
				"ccc", "ccc", 'c', new ItemStack(mod_InfiBlocks.infiGlass, 1, k)
			});
		}

		ModLoader.addRecipe(new ItemStack(Item.doorWood, 1), new Object[]
		{
			"PP", "PP", "PP", 'P', Block.cactus
		});
		ModLoader.addRecipe(new ItemStack(Block.trapdoor, 2), new Object[]
		{
			"PPP", "PPP", 'P', Block.cactus
		});
		ModLoader.addRecipe(new ItemStack(Block.chest, 1), new Object[]
		{
			"PPP", "P P", "PPP", 'P', Block.cactus
		});
		ModLoader.addRecipe(new ItemStack(Item.bed, 1), new Object[]
		{
			"www", "ccc", 'w', new ItemStack(Block.cloth, 1, -1), 'c', Block.cactus
		});
		ModLoader.addRecipe(new ItemStack(mod_InfiBlocks.chiselIron, 1), new Object[]
		{
			"i ", " w", 'w', Block.planks, 'i', Item.ingotIron
		});
		ModLoader.addRecipe(new ItemStack(mod_InfiBlocks.chiselDiamond, 1), new Object[]
		{
			"i ", " w", 'w', Block.planks, 'i', Item.diamond
		});
		
		ItemStack[] storageArray = {
			new ItemStack(Item.coal, 1, 0), new ItemStack(Item.coal, 1, 1),
			new ItemStack(Item.redstone), new ItemStack(Item.slimeBall), 
			new ItemStack(Item.bone), new ItemStack(Item.wheat)
		};
		
		ItemStack[] storageArrayReverse = {
				new ItemStack(Item.coal, 9, 0), new ItemStack(Item.coal, 9, 1),
				new ItemStack(Item.redstone, 9), new ItemStack(Item.slimeBall, 9), 
				new ItemStack(Item.bone, 9), new ItemStack(Item.wheat, 9)
			};
		
		//Storage block recipes
		for (int iter = 0; iter < storageArray.length; iter++)
		{
			ModLoader.addRecipe(new ItemStack(mod_InfiBlocks.storageBlock, 1, iter), new Object[]
			{
				"xxx", "xxx", "xxx", 'x', storageArray[iter]
			});
		}
		
		for (int iter = 0; iter < storageArray.length; iter++)
		{
			ModLoader.addRecipe(storageArrayReverse[iter], new Object[]
			{
				"x", 'x', new ItemStack(mod_InfiBlocks.storageBlock, 1, iter)
			});
		}
		
		//Override vanilla slab and stairs recipes
		removeRecipe(new ItemStack(Block.stairSingle, 3, 3));
		removeRecipe(new ItemStack(Block.stairSingle, 3, 0));
		removeRecipe(new ItemStack(Block.stairSingle, 3, 1));
		removeRecipe(new ItemStack(Block.stairSingle, 3, 2));
		removeRecipe(new ItemStack(Block.stairSingle, 3, 4));
		removeRecipe(new ItemStack(Block.stairSingle, 3, 5));
		
		removeRecipe(new ItemStack(Block.stairCompactPlanks, 4));
		removeRecipe(new ItemStack(Block.stairCompactCobblestone, 4));
		removeRecipe(new ItemStack(Block.stairsBrick, 4));
		removeRecipe(new ItemStack(Block.stairsStoneBrickSmooth, 4));
		removeRecipe(new ItemStack(Block.stairsNetherBrick, 4));
		
		ModLoader.addRecipe(new ItemStack(Block.stairSingle, 6, 3), new Object[]
		{	
			"###", '#', Block.cobblestone
		});
		ModLoader.addRecipe(new ItemStack(Block.stairSingle, 6, 0), new Object[]
		{
			"###", '#', Block.stone
		});
		ModLoader.addRecipe(new ItemStack(Block.stairSingle, 6, 1), new Object[]
		{
			"###", '#', Block.sandStone
		});
		ModLoader.addRecipe(new ItemStack(Block.stairSingle, 6, 2), new Object[]
		{
			"###", '#', Block.planks
		});
		ModLoader.addRecipe(new ItemStack(Block.stairSingle, 6, 4), new Object[]
		{
			"###", '#', Block.brick
		});
		ModLoader.addRecipe(new ItemStack(Block.stairSingle, 6, 5), new Object[]
		{
			"###", '#', Block.stoneBrick
		});
			
		ModLoader.addRecipe(new ItemStack(Block.stairCompactPlanks, 6), new Object[]
		{
			"#  ", "## ", "###", '#', Block.planks
		});
		ModLoader.addRecipe(new ItemStack(Block.stairCompactCobblestone, 6), new Object[]
		{
			"#  ", "## ", "###", '#', Block.cobblestone
		});
		ModLoader.addRecipe(new ItemStack(Block.stairsBrick, 6), new Object[]
		{
			"#  ", "## ", "###", '#', Block.brick
		});
		ModLoader.addRecipe(new ItemStack(Block.stairsStoneBrickSmooth, 6), new Object[]
		{
			"#  ", "## ", "###", '#', Block.stoneBrick
		});
		ModLoader.addRecipe(new ItemStack(Block.stairsNetherBrick, 6), new Object[]
		{
			"#  ", "## ", "###", '#', Block.netherBrick
		});
		
		/* Chisel Recipes */
		
		DetailManager.addDamageOnCraft(mod_InfiBlocks.chiselIron);
		
		//Diamond
		ModLoader.addShapelessRecipe(new ItemStack(mod_InfiBlocks.brick, 1, 5), new Object[] {
			new ItemStack(mod_InfiBlocks.chiselIron, 1, -1), Item.diamond
		});
		ModLoader.addShapelessRecipe(new ItemStack(mod_InfiBlocks.brick, 1, 5), new Object[] {
			new ItemStack(mod_InfiBlocks.chiselDiamond, 1, -1), Item.diamond
		});
		
		//Gold
		ModLoader.addShapelessRecipe(new ItemStack(mod_InfiBlocks.brick, 1, 6), new Object[] {
			new ItemStack(mod_InfiBlocks.chiselIron, 1, -1), Item.ingotGold
		});
		ModLoader.addShapelessRecipe(new ItemStack(mod_InfiBlocks.brick, 1, 6), new Object[] {
			new ItemStack(mod_InfiBlocks.chiselDiamond, 1, -1), Item.ingotGold
		});
		
		//Lapis
		ModLoader.addShapelessRecipe(new ItemStack(mod_InfiBlocks.brick, 1, 7), new Object[] {
			new ItemStack(mod_InfiBlocks.chiselIron, 1, -1), new ItemStack(Item.dyePowder, 1, 4),
			new ItemStack(Item.dyePowder, 1, 4), new ItemStack(Item.dyePowder, 1, 4)
		});
		ModLoader.addShapelessRecipe(new ItemStack(mod_InfiBlocks.brick, 1, 7), new Object[] {
			new ItemStack(mod_InfiBlocks.chiselDiamond, 1, -1), new ItemStack(Item.dyePowder, 1, 4),
			new ItemStack(Item.dyePowder, 1, 4), new ItemStack(Item.dyePowder, 1, 4)
		});
		
		//Iron
		ModLoader.addShapelessRecipe(new ItemStack(mod_InfiBlocks.brick, 1, 12), new Object[] {
			new ItemStack(mod_InfiBlocks.chiselIron, 1, -1), Item.ingotIron
		});
		ModLoader.addShapelessRecipe(new ItemStack(mod_InfiBlocks.brick, 1, 12), new Object[] {
			new ItemStack(mod_InfiBlocks.chiselDiamond, 1, -1), Item.ingotIron
		});

		//Redstone
		ModLoader.addShapelessRecipe(new ItemStack(mod_InfiBlocks.brick, 1, 13), new Object[] {
			new ItemStack(mod_InfiBlocks.chiselIron, 1, -1), Item.redstone, Item.redstone,
			Item.redstone
		});
		ModLoader.addShapelessRecipe(new ItemStack(mod_InfiBlocks.brick, 1, 13), new Object[] {
			new ItemStack(mod_InfiBlocks.chiselDiamond, 1, -1), Item.redstone, Item.redstone,
			Item.redstone
		});
		
		//Slime
		ModLoader.addShapelessRecipe(new ItemStack(mod_InfiBlocks.brick, 1, 14), new Object[] {
			new ItemStack(mod_InfiBlocks.chiselIron, 1, -1), Item.slimeBall, Item.slimeBall, Item.slimeBall
		});
		ModLoader.addShapelessRecipe(new ItemStack(mod_InfiBlocks.brick, 1, 14), new Object[] {
			new ItemStack(mod_InfiBlocks.chiselDiamond, 1, -1), Item.slimeBall, Item.slimeBall, Item.slimeBall
		});
		
		//Bone
		ModLoader.addShapelessRecipe(new ItemStack(mod_InfiBlocks.brick, 1, 15), new Object[] {
			new ItemStack(mod_InfiBlocks.chiselIron, 1, -1), Item.bone, Item.bone, Item.bone
		});
		ModLoader.addShapelessRecipe(new ItemStack(mod_InfiBlocks.brick, 1, 15), new Object[] {
			new ItemStack(mod_InfiBlocks.chiselDiamond, 1, -1), Item.bone, Item.bone, Item.bone
		});
	}
	
	public static void magicSlabFrenzy()
	{
		for (int iter = 0; iter < magicSlabStoneArray.length; iter++)
		{
			ModLoader.addRecipe(new ItemStack(mod_InfiBlocks.magicSlabStone, 8, iter), new Object[]
			{
				" b ", "b b", " b ", 'b', magicSlabStoneArray[iter]
			});
		}
		
		for (int iter = 0; iter < magicSlabSoilArray.length; iter++)
		{
			ModLoader.addRecipe(new ItemStack(mod_InfiBlocks.magicSlabSoil, 8, iter), new Object[]
			{
				" b ", "b b", " b ", 'b', magicSlabSoilArray[iter]
			});
		}
		
		for (int iter = 0; iter < 16; iter++)
		{
			ModLoader.addRecipe(new ItemStack(mod_InfiBlocks.magicSlabWool, 8, iter), new Object[]
			{
				" b ", "b b", " b ", 'b', new ItemStack(Block.cloth, 1, iter)
			});
		}
		
		for (int k = 0; k <= 15; k++)
		{
			ModLoader.addRecipe(new ItemStack(mod_InfiBlocks.stainedGlassMagicSlab, 8, k), new Object[]
			{
				" c ", "c c", " c ", 'c', new ItemStack(mod_InfiBlocks.stainedGlass, 1, k)
			});
		}
		
		for(int i = 0; i < 16; i++) {
			ModLoader.addRecipe(new ItemStack(mod_InfiBlocks.brickMagicSlab, 8, i), new Object[]
			{
				" b ", "b b", " b ", 'b', new ItemStack(mod_InfiBlocks.brick, 1, i)
			});
		}
		
		for(int i = 0; i < 10; i++) {
			ModLoader.addRecipe(new ItemStack(mod_InfiBlocks.fancyBrickMagicSlab, 8, i), new Object[]
			{
				" b ", "b b", " b ", 'b', new ItemStack(mod_InfiBlocks.fancyBrick, 1, i)
			});
		}
		
		for(int i = 11; i < 15; i++) {
			ModLoader.addRecipe(new ItemStack(mod_InfiBlocks.fancyBrickMagicSlab, 8, i), new Object[]
			{
				" b ", "b b", " b ", 'b', new ItemStack(mod_InfiBlocks.fancyBrick, 1, i)
			});
		}
		
		for(int i = 0; i < 3; i++) {
			ModLoader.addRecipe(new ItemStack(mod_InfiBlocks.infiGlassMagicSlab, 8, i), new Object[]
			{
				" b ", "b b", " b ", 'b', new ItemStack(mod_InfiBlocks.infiGlass, 1, i)
			});
		}
		
		for(int i = 0; i < 6; i++) {
			ModLoader.addRecipe(new ItemStack(mod_InfiBlocks.brownstoneMagicSlab, 8, i), new Object[]
			{
				" b ", "b b", " b ", 'b', new ItemStack(mod_InfiBlocks.brownstone, 1, i)
			});
		}
		
		for(int i = 0; i < 3; i++) {
			ModLoader.addRecipe(new ItemStack(mod_InfiBlocks.iceBrickMagicSlab, 8, i), new Object[]
			{
				" b ", "b b", " b ", 'b', new ItemStack(mod_InfiBlocks.iceBrick, 1, i)
			});
		}
		
		//Magic Slab Reversal
		for (int iter = 0; iter < magicSlabStoneArray.length; iter++)
		{
			ModLoader.addRecipe(magicSlabStoneArray[iter], new Object[]
			{
				"c", "c", 'c', new ItemStack(mod_InfiBlocks.magicSlabStone, 1, iter)
			});
		}
		
		for (int iter = 0; iter < magicSlabSoilArray.length; iter++)
		{
			ModLoader.addRecipe(magicSlabSoilArray[iter], new Object[]
			{
				"c", "c", 'c', new ItemStack(mod_InfiBlocks.magicSlabSoil, 1, iter)
			});
		}
		
		for (int iter = 0; iter < 16; iter++)
		{
			ModLoader.addRecipe( new ItemStack(Block.cloth, 1, iter), new Object[]
			{
				"c", "c", 'c', new ItemStack(mod_InfiBlocks.magicSlabWool, 1, iter)
			});
		}
		
		for (int k = 0; k <= 15; k++)
		{
			ModLoader.addRecipe(new ItemStack(mod_InfiBlocks.stainedGlass, 1, k), new Object[]
			{
				"c", "c", 'c', new ItemStack(mod_InfiBlocks.stainedGlassMagicSlab, 1, k)
			});
		}
			
		for(int i = 0; i < 16; i++) {
			ModLoader.addRecipe(new ItemStack(mod_InfiBlocks.brick, 1, i), new Object[]
			{
				"c", "c", 'c', new ItemStack(mod_InfiBlocks.brickMagicSlab, 1, i)
			});
		}
		
		for(int i = 0; i < 10; i++) {
			ModLoader.addRecipe(new ItemStack(mod_InfiBlocks.fancyBrick, 1, i), new Object[]
			{
				"c", "c", 'c', new ItemStack(mod_InfiBlocks.fancyBrickMagicSlab, 1, i)
			});
		}
		
		for(int i = 11; i < 15; i++) {
			ModLoader.addRecipe(new ItemStack(mod_InfiBlocks.fancyBrick, 1, i), new Object[]
			{
				"c", "c", 'c', new ItemStack(mod_InfiBlocks.fancyBrickMagicSlab, 1, i)
			});
		}
		
		for(int i = 0; i < 3; i++) {
			ModLoader.addRecipe(new ItemStack(mod_InfiBlocks.infiGlass, 1, i), new Object[]
			{
				"c", "c", 'c', new ItemStack(mod_InfiBlocks.infiGlassMagicSlab, 1, i)
			});
		}
		
		for(int i = 0; i < 6; i++) {
			ModLoader.addRecipe(new ItemStack(mod_InfiBlocks.brownstone, 1, i), new Object[]
			{
				"c", "c", 'c', new ItemStack(mod_InfiBlocks.brownstoneMagicSlab, 1, i)
			});
		}
		
		for(int i = 0; i < 3; i++) {
			ModLoader.addRecipe(new ItemStack(mod_InfiBlocks.iceBrickMagicSlab, 1, i), new Object[]
			{
				"c", "c", 'c', new ItemStack(mod_InfiBlocks.iceBrick, 1, i)
			});
		}
		
		//Slab dye
		for (int i = 0; i < 16; i++)
		{
			ModLoader.addRecipe(new ItemStack(mod_InfiBlocks.magicSlabWool, 8, i), new Object[]
			{
				"bbb", "bxb", "bbb", 'b', new ItemStack(mod_InfiBlocks.magicSlabWool, 1, -1),
					'x', new ItemStack(Item.dyePowder, 1, 15-i)
			});
		}
		
		for (int i = 0; i <= 15; i++)
		{
			ModLoader.addShapelessRecipe(new ItemStack(mod_InfiBlocks.magicSlabWool, 1, i), new Object[]
			{
				new ItemStack(mod_InfiBlocks.magicSlabWool, 1, -1), 
					new ItemStack(Item.dyePowder, 1, 15-i)
			});
		}
		
		for (int i = 0; i < 16; i++)
		{
			ModLoader.addRecipe(new ItemStack(mod_InfiBlocks.stainedGlassMagicSlab, 8, i), new Object[]
			{
				"bbb", "bxb", "bbb", 'b', new ItemStack(mod_InfiBlocks.stainedGlassMagicSlab, 1, -1),
					'd', new ItemStack(Item.dyePowder, 1, 15-i)
			});
		}
		
		for (int i = 0; i <= 15; i++)
		{
			ModLoader.addShapelessRecipe(new ItemStack(mod_InfiBlocks.stainedGlassMagicSlab, 1, i), new Object[]
			{
				new ItemStack(mod_InfiBlocks.stainedGlassMagicSlab, 1, -1), 
					new ItemStack(Item.dyePowder, 1, 15-i)
			});
		}
	}
	
	public static void furnaceBlaze()
	{
		//FurnaceRecipes.smelting().addSmelting(Item.dyePowder.shiftedIndex, 2, new ItemStack(Item.coal, 1, 1));
		FurnaceRecipes.smelting().addSmelting(Block.netherrack.blockID, 
				new ItemStack(mod_InfiBlocks.storageBlock, 1, 12));
		FurnaceRecipes.smelting().addSmelting(Block.sandStone.blockID, 
				new ItemStack(mod_InfiBlocks.storageBlock, 1, 13));
		FurnaceRecipes.smelting().addSmelting(Block.stone.blockID, 
				new ItemStack(mod_InfiBlocks.storageBlock, 1, 14));
		FurnaceRecipes.smelting().addSmelting(Block.blockClay.blockID, 
				new ItemStack(mod_InfiBlocks.storageBlock, 1, 15));
		FurnaceRecipes.smelting().addSmelting(Block.gravel.blockID, 
				new ItemStack(mod_InfiBlocks.brownstone, 1, 0));
		FurnaceRecipes.smelting().addSmelting(mod_InfiBlocks.brownstone.blockID, 0,
				new ItemStack(mod_InfiBlocks.brownstone, 1, 1));
		
		FurnaceRecipes.smelting().addSmelting(Block.glass.blockID,
				new ItemStack(mod_InfiBlocks.infiGlass, 1, 0));
		FurnaceRecipes.smelting().addSmelting(Block.slowSand.blockID,
				new ItemStack(mod_InfiBlocks.infiGlass, 1, 1));
		FurnaceRecipes.smelting().addSmelting(mod_InfiBlocks.infiGlass.blockID, 1,
				new ItemStack(mod_InfiBlocks.infiGlass, 1, 2));
	}
	
	//Code by yope_fried inspired by pigalot
	private static void removeRecipe(ItemStack resultItem) {
		List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
		for (int i = 0; i < recipes.size(); i++) 
		{
			IRecipe tmpRecipe = recipes.get(i);
			if (tmpRecipe instanceof ShapedRecipes) {
				ShapedRecipes recipe = (ShapedRecipes)tmpRecipe;
				ItemStack recipeResult = recipe.getRecipeOutput();
			
				if (ItemStack.areItemStacksEqual(resultItem, recipeResult)) {
					recipes.remove(i--);
				}
			}
		}
	}
}
