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
	
	public static void addNames()
	{
		ModLoader.addName(mod_InfiBlocks.chiselIron, "Chisel");
		ModLoader.addName(mod_InfiBlocks.chiselDiamond, "Diamond Chisel");
		
		ModLoader.addLocalization("cobblestoneWorkbench.name", "Cobblestone Workbench");
		ModLoader.addLocalization("ironWorkbench.name", "Iron Workbench");
		ModLoader.addLocalization("redstoneWorkbench.name", "Redstone Workbench");
		ModLoader.addLocalization("boneWorkbench.name", "Bone Workbench");
		ModLoader.addLocalization("sandstoneWorkbench.name", "Sandstone Workbench");
		ModLoader.addLocalization("lapisWorkbench.name", "Lapis Lazuli Workbench");
		ModLoader.addLocalization("obsidianWorkbench.name", "Obsidian Workbench");
		ModLoader.addLocalization("cactusWorkbench.name", "Cactus Workbench");
		ModLoader.addLocalization("netherrackWorkbench.name", "Netherrack Workbench");
		ModLoader.addLocalization("iceWorkbench.name", "Ice Workbench");
		ModLoader.addLocalization("stoneBrickWorkbench.name", "Brick Workbench");
		
		ModLoader.addLocalization("whiteCarpet.name", "Carpet");
		ModLoader.addLocalization("orangeCarpet.name", "Orange Carpet");
		ModLoader.addLocalization("magentaCarpet.name", "Magenta Carpet");
		ModLoader.addLocalization("lightblueCarpet.name", "Light Blue Carpet");
		ModLoader.addLocalization("yellowCarpet.name", "Yellow Carpet");
		ModLoader.addLocalization("limeCarpet.name", "Lime Carpet");
		ModLoader.addLocalization("pinkCarpet.name", "Pink Carpet");
		ModLoader.addLocalization("grayCarpet.name", "Gray Carpet");
		ModLoader.addLocalization("lightgrayCarpet.name", "Light Gray Carpet");
		ModLoader.addLocalization("cyanCarpet.name", "Cyan Carpet");
		ModLoader.addLocalization("purpleCarpet.name", "Purple Carpet");
		ModLoader.addLocalization("blueCarpet.name", "Blue Carpet");
		ModLoader.addLocalization("brownCarpet.name", "Brown Carpet");
		ModLoader.addLocalization("greenCarpet.name", "Green Carpet");
		ModLoader.addLocalization("redCarpet.name", "Red Carpet");
		ModLoader.addLocalization("blackCarpet.name", "Black Carpet");
		
		ModLoader.addLocalization("ironFurnaceInfi.name", "Iron Furnace");
		ModLoader.addLocalization("brickFurnaceInfi.name", "Brick Furnace");
		ModLoader.addLocalization("sandstoneFurnaceInfi.name", "Sandstone Furnace");
		ModLoader.addLocalization("obsidianFurnaceInfi.name", "Obsidian Furnace");
		ModLoader.addLocalization("redstoneFurnaceInfi.name", "Redstone Furnace");
		ModLoader.addLocalization("netherrackFurnaceInfi.name", "Netherrack Furnace");
		ModLoader.addLocalization("stonebrickFurnaceInfi.name", "Brick Furnace");
		ModLoader.addLocalization("endstoneFurnaceInfi.name", "White Stone Furnace");
		ModLoader.addLocalization("glowstoneFurnaceInfi.name", "Glowstone Furnace");
		
		ModLoader.addLocalization("stoneMagicSlab.name", "Stone Magic Slab");
		ModLoader.addLocalization("slabstoneMagicSlab.name", "Magic Slab o' Stone");
		ModLoader.addLocalization("cobblestoneMagicSlab.name", "Cobbled Magic Slab");
		ModLoader.addLocalization("stonebrickMagicSlab.name", "Tiled Magic Slab");
		ModLoader.addLocalization("crackedstonebrickMagicSlab.name", "Tiled Magic Slab");
		ModLoader.addLocalization("mossystonebrickMagicSlab.name", "Tiled Magic Slab");
		ModLoader.addLocalization("brickMagicSlab.name", "Brick Magic Slab");
		ModLoader.addLocalization("mossyMagicSlab.name", "Mossy Magic Slab");
		ModLoader.addLocalization("obsidianMagicSlab.name", "Obsidian Magic Slab");
		ModLoader.addLocalization("netherrackMagicSlab.name", "Netherrack Magic Slab");
		ModLoader.addLocalization("sandstoneMagicSlab.name", "Sandstone Magic Slab");
		ModLoader.addLocalization("ironMagicSlab.name", "Iron Magic Slab");
		ModLoader.addLocalization("goldMagicSlab.name", "Gold Magic Slab");
		ModLoader.addLocalization("diamondMagicSlab.name", "Diamond Magic Slab");
		ModLoader.addLocalization("endstoneMagicSlab.name", "White Magic Slab");
		ModLoader.addLocalization("netherBrickMagicSlab.name", "Nether Brick Magic Slab");
		
		ModLoader.addLocalization("dirtMagicSlab.name", "Dirt Magic Slab");
		ModLoader.addLocalization("grassMagicSlab.name", "Grass Magic Slab");
		ModLoader.addLocalization("myceliumMagicSlab.name", "Mycelium Magic Slab");
		ModLoader.addLocalization("sandMagicSlab.name", "Sand Magic Slab");
		ModLoader.addLocalization("gravelMagicSlab.name", "Gravel Magic Slab");
		ModLoader.addLocalization("oakMagicSlab.name", "Oak Magic Slab");
		ModLoader.addLocalization("pineMagicSlab.name", "Pine Magic Slab");
		ModLoader.addLocalization("birchMagicSlab.name", "Birch Magic Slab");
		ModLoader.addLocalization("planksMagicSlab.name", "Plank Magic Slab");
		ModLoader.addLocalization("snowMagicSlab.name", "Snow Magic Slab");
		ModLoader.addLocalization("soulsandMagicSlab.name", "Soul Sand Magic Slab");
		ModLoader.addLocalization("brownMushroomMagicSlab.name", "Brown Mushroom Magic Slab");
		ModLoader.addLocalization("redMushroomMagicSlab.name", "Red Mushroom Magic Slab");
		ModLoader.addLocalization("glowstoneMagicSlab.name", "Glowstone Magic Slab");
		ModLoader.addLocalization("glassMagicSlab.name", "Glass Magic Slab");
		ModLoader.addLocalization("oakLeavesMagicSlab.name", "Leaves Magic Slab");
		
		ModLoader.addLocalization("whiteMagicSlab.name", "Wool Magic Slab");
		ModLoader.addLocalization("orangeMagicSlab.name", "Orange Magic Slab");
		ModLoader.addLocalization("magentaMagicSlab.name", "Magenta Magic Slab");
		ModLoader.addLocalization("lightblueMagicSlab.name", "Light Blue Magic Slab");
		ModLoader.addLocalization("yellowMagicSlab.name", "Yellow Magic Slab");
		ModLoader.addLocalization("limeMagicSlab.name", "Lime Magic Slab");
		ModLoader.addLocalization("pinkMagicSlab.name", "Pink Magic Slab");
		ModLoader.addLocalization("grayMagicSlab.name", "Gray Magic Slab");
		ModLoader.addLocalization("lightgrayMagicSlab.name", "Light Gray Magic Slab");
		ModLoader.addLocalization("cyanMagicSlab.name", "Cyan Magic Slab");
		ModLoader.addLocalization("purpleMagicSlab.name", "Purple Magic Slab");
		ModLoader.addLocalization("blueMagicSlab.name", "Blue Magic Slab");
		ModLoader.addLocalization("brownMagicSlab.name", "Brown Magic Slab");
		ModLoader.addLocalization("greenMagicSlab.name", "Green Magic Slab");
		ModLoader.addLocalization("redMagicSlab.name", "Red Magic Slab");
		ModLoader.addLocalization("blackMagicSlab.name", "Black Magic Slab");
		
		ModLoader.addLocalization("whiteGlass.name", "White Glass");
		ModLoader.addLocalization("orangeGlass.name", "Orange Glass");
		ModLoader.addLocalization("magentaGlass.name", "Magenta Glass");
		ModLoader.addLocalization("lightblueGlass.name", "Light Blue Glass");
		ModLoader.addLocalization("yellowGlass.name", "Yellow Glass");
		ModLoader.addLocalization("limeGlass.name", "Lime Glass");
		ModLoader.addLocalization("pinkGlass.name", "Pink Glass");
		ModLoader.addLocalization("grayGlass.name", "Gray Glass");
		ModLoader.addLocalization("lightgrayGlass.name", "Light Gray Glass");
		ModLoader.addLocalization("cyanGlass.name", "Cyan Glass");
		ModLoader.addLocalization("purpleGlass.name", "Purple Glass");
		ModLoader.addLocalization("blueGlass.name", "Blue Glass");
		ModLoader.addLocalization("brownGlass.name", "Brown Glass");
		ModLoader.addLocalization("greenGlass.name", "Green Glass");
		ModLoader.addLocalization("redGlass.name", "Red Glass");
		ModLoader.addLocalization("blackGlass.name", "Black Glass");
		
		ModLoader.addLocalization("whiteGlassPane.name", "White Glass Pane");
		ModLoader.addLocalization("orangeGlassPane.name", "Orange Glass Pane");
		ModLoader.addLocalization("magentaGlassPane.name", "Magenta Glass Pane");
		ModLoader.addLocalization("lightblueGlassPane.name", "Light Blue Glass Pane");
		ModLoader.addLocalization("yellowGlassPane.name", "Yellow Glass Pane");
		ModLoader.addLocalization("limeGlassPane.name", "Lime Glass Pane");
		ModLoader.addLocalization("pinkGlassPane.name", "Pink Glass Pane");
		ModLoader.addLocalization("grayGlassPane.name", "Gray Glass Pane");
		ModLoader.addLocalization("lightgrayGlassPane.name", "Light Gray Glass Pane");
		ModLoader.addLocalization("cyanGlassPane.name", "Cyan Glass Pane");
		ModLoader.addLocalization("purpleGlassPane.name", "Purple Glass Pane");
		ModLoader.addLocalization("blueGlassPane.name", "Blue Glass Pane");
		ModLoader.addLocalization("brownGlassPane.name", "Brown Glass Pane");
		ModLoader.addLocalization("greenGlassPane.name", "Green Glass Pane");
		ModLoader.addLocalization("redGlassPane.name", "Red Glass Pane");
		ModLoader.addLocalization("blackGlassPane.name", "Black Glass Pane");
		
		ModLoader.addLocalization("whiteGlassSlab.name", "White Glass Magic Slab");
		ModLoader.addLocalization("orangeGlassSlab.name", "Orange Glass Magic Slab");
		ModLoader.addLocalization("magentaGlassSlab.name", "Magenta Glass Magic Slab");
		ModLoader.addLocalization("lightblueGlassSlab.name", "Light Blue Glass Magic Slab");
		ModLoader.addLocalization("yellowGlassSlab.name", "Yellow Glass Magic Slab");
		ModLoader.addLocalization("limeGlassSlab.name", "Lime Glass Magic Slab");
		ModLoader.addLocalization("pinkGlassSlab.name", "Pink Glass Magic Slab");
		ModLoader.addLocalization("grayGlassSlab.name", "Gray Glass Magic Slab");
		ModLoader.addLocalization("lightgrayGlassSlab.name", "Light Gray Glass Magic Slab");
		ModLoader.addLocalization("cyanGlassSlab.name", "Cyan Glass Magic Slab");
		ModLoader.addLocalization("purpleGlassSlab.name", "Purple Glass Magic Slab");
		ModLoader.addLocalization("blueGlassSlab.name", "Blue Glass Magic Slab");
		ModLoader.addLocalization("brownGlassSlab.name", "Brown Glass Magic Slab");
		ModLoader.addLocalization("greenGlassSlab.name", "Green Glass Magic Slab");
		ModLoader.addLocalization("redGlassSlab.name", "Red Glass Magic Slab");
		ModLoader.addLocalization("blackGlassSlab.name", "Black Glass Magic Slab");
		
		ModLoader.addLocalization("whiteGlassStair.name", "White Glass Magic Stair");
		ModLoader.addLocalization("orangeGlassStair.name", "Orange Glass Magic Stair");
		ModLoader.addLocalization("magentaGlassStair.name", "Magenta Glass Magic Stair");
		ModLoader.addLocalization("lightblueGlassStair.name", "Light Blue Glass Magic Stair");
		ModLoader.addLocalization("yellowGlassStair.name", "Yellow Glass Magic Stair");
		ModLoader.addLocalization("limeGlassStair.name", "Lime Glass Magic Stair");
		ModLoader.addLocalization("pinkGlassStair.name", "Pink Glass Magic Stair");
		ModLoader.addLocalization("grayGlassStair.name", "Gray Glass Magic Stair");
		ModLoader.addLocalization("lightgrayGlassStair.name", "Light Gray Glass Magic Stair");
		ModLoader.addLocalization("cyanGlassStair.name", "Cyan Glass Magic Stair");
		ModLoader.addLocalization("purpleGlassStair.name", "Purple Glass Magic Stair");
		ModLoader.addLocalization("blueGlassStair.name", "Blue Glass Magic Stair");
		ModLoader.addLocalization("brownGlassStair.name", "Brown Glass Magic Stair");
		ModLoader.addLocalization("greenGlassStair.name", "Green Glass Magic Stair");
		ModLoader.addLocalization("redGlassStair.name", "Red Glass Magic Stair");
		ModLoader.addLocalization("blackGlassStair.name", "Black Glass Magic Stair");
		
		ModLoader.addLocalization("obsidianBrick.name", "Obsidian Brick");
		ModLoader.addLocalization("snowBrick.name", "Snow Brick");
		ModLoader.addLocalization("sandstoneBrick.name", "Sandstone Brick");
		ModLoader.addLocalization("brickBrick.name", "Red Brick");
		ModLoader.addLocalization("netherrackBrick.name", "Netherrack Brick");
		ModLoader.addLocalization("diamondBrick.name", "Diamond Brick");
		ModLoader.addLocalization("goldBrick.name", "Gold Brick");
		ModLoader.addLocalization("lapisBrick.name", "Lapis Brick");
		ModLoader.addLocalization("slabBrick.name", "Stone Brick");
		ModLoader.addLocalization("stoneSmallBrick.name", "Stone Brick");
		ModLoader.addLocalization("slabSmallBrick.name", "Stone Brick");
		ModLoader.addLocalization("brickTileBrick.name", "Large Brick Tile");
		ModLoader.addLocalization("ironBrick.name", "Iron Brick");
		ModLoader.addLocalization("redstoneBrick.name", "Redstone Brick");
		ModLoader.addLocalization("slimeBrick.name", "Slime Brick");
		ModLoader.addLocalization("boneBrick.name", "Bone Brick");
		
		ModLoader.addLocalization("obsidianBrickMagicSlab.name", "Obsidian Brick Magic Slab");
		ModLoader.addLocalization("snowBrickMagicSlab.name", "Snow Brick Magic Slab");
		ModLoader.addLocalization("sandstoneBrickMagicSlab.name", "Sandstone Brick Magic Slab");
		ModLoader.addLocalization("brickBrickMagicSlab.name", "Red Brick Magic Slab");
		ModLoader.addLocalization("netherrackBrickMagicSlab.name", "Netherrack Brick Magic Slab");
		ModLoader.addLocalization("diamondBrickMagicSlab.name", "Diamond Brick Magic Slab");
		ModLoader.addLocalization("goldBrickMagicSlab.name", "Gold Brick Magic Slab");
		ModLoader.addLocalization("lapisBrickMagicSlab.name", "Lapis Brick Magic Slab");
		ModLoader.addLocalization("slabBrickMagicSlab.name", "Stone Brick Magic Slab");
		ModLoader.addLocalization("stoneSmallBrickMagicSlab.name", "Stone Brick Magic Slab");
		ModLoader.addLocalization("slabSmallBrickMagicSlab.name", "Stone Brick Magic Slab");
		ModLoader.addLocalization("brickTileBrickMagicSlab.name", "Large Brick Tile");
		ModLoader.addLocalization("ironBrickMagicSlab.name", "Iron Brick Magic Slab");
		ModLoader.addLocalization("redstoneBrickMagicSlab.name", "Redstone Brick Magic Slab");
		ModLoader.addLocalization("slimeBrickMagicSlab.name", "Slime Brick Magic Slab");
		ModLoader.addLocalization("boneBrickMagicSlab.name", "Bone Brick Magic Slab");
		
		ModLoader.addLocalization("obsidianFancyBrick.name", "Fancy Obsidian Brick");
		ModLoader.addLocalization("snowFancyBrick.name", "Fancy Snow Brick");
		ModLoader.addLocalization("sandstoneFancyBrick.name", "Fancy Sandstone Brick");
		ModLoader.addLocalization("brickFancyBrick.name", "Fancy Red Brick");
		ModLoader.addLocalization("netherrackFancyBrick.name", "Fancy Netherrack Brick");
		ModLoader.addLocalization("diamondFancyBrick.name", "Fancy Diamond Brick");
		ModLoader.addLocalization("goldFancyBrick.name", "Fancy Gold Brick");
		ModLoader.addLocalization("lapisFancyBrick.name", "Fancy Lapis Brick");
		ModLoader.addLocalization("slabFancyBrick.name", "Fancy Stone Brick");
		ModLoader.addLocalization("stoneFancyBrick.name", "Fancy Stone Brick");
		ModLoader.addLocalization("brickTileFancyBrick.name", "Red Brick Tiles");
		ModLoader.addLocalization("ironFancyBrick.name", "Fancy Iron Brick");
		ModLoader.addLocalization("redstoneFancyBrick.name", "Fancy Redstone Brick");
		ModLoader.addLocalization("slimeFancyBrick.name", "Fancy Slime Brick");
		ModLoader.addLocalization("boneFancyBrick.name", "Fancy Bone Brick");
		
		ModLoader.addLocalization("obsidianFancyBrickMagicSlab.name", "Fancy Obsidian Brick Magic Slab");
		ModLoader.addLocalization("snowFancyBrickMagicSlab.name", "Fancy Snow Brick Magic Slab");
		ModLoader.addLocalization("sandstoneFancyBrickMagicSlab.name", "Fancy Sandstone Brick Magic Slab");
		ModLoader.addLocalization("brickFancyBrickMagicSlab.name", "Fancy Red Brick Magic Slab");
		ModLoader.addLocalization("netherrackFancyBrickMagicSlab.name", "Fancy Netherrack Brick Magic Slab");
		ModLoader.addLocalization("diamondFancyBrickMagicSlab.name", "Fancy Diamond Brick Magic Slab");
		ModLoader.addLocalization("goldFancyBrickMagicSlab.name", "Fancy Gold Brick Magic Slab");
		ModLoader.addLocalization("lapisFancyBrickMagicSlab.name", "Fancy Lapis Brick Magic Slab");
		ModLoader.addLocalization("slabFancyBrickMagicSlab.name", "Fancy Stone Brick Magic Slab");
		ModLoader.addLocalization("stoneFancyBrickMagicSlab.name", "Fancy Stone Brick Magic Slab");
		ModLoader.addLocalization("brickTileFancyBrickMagicSlab.name", "Red Brick Tiles Magic Slab");
		ModLoader.addLocalization("ironFancyBrickMagicSlab.name", "Fancy Iron Brick Magic Slab");
		ModLoader.addLocalization("redstoneFancyBrickMagicSlab.name", "Fancy Redstone Brick Magic Slab");
		ModLoader.addLocalization("slimeFancyBrickMagicSlab.name", "Fancy Slime Brick Magic Slab");
		ModLoader.addLocalization("boneFancyBrickMagicSlab.name", "Fancy Bone Brick Magic Slab");
		
		ModLoader.addLocalization("clearGlass.name", "Clear Glass");
		ModLoader.addLocalization("soulGlass.name", "Soul Glass");
		ModLoader.addLocalization("clearSoulGlass.name", "Clear Soul Glass");
		
		ModLoader.addLocalization("clearGlassPane.name", "Clear Glass Pane");
		ModLoader.addLocalization("soulGlassPane.name", "Soul Glass Pane");
		ModLoader.addLocalization("clearSoulGlassPane.name", "Clear Soul Glass Pane");
		
		ModLoader.addLocalization("clearGlassMagicSlab.name", "Clear Glass Magic Slab");
		ModLoader.addLocalization("soulGlassMagicSlab.name", "Soul Glass Magic Slab");
		ModLoader.addLocalization("clearSoulGlassMagicSlab.name", "Clear Soul Glass Magic Slab");
		
		ModLoader.addLocalization("rawBrownstone.name", "Rough Brownstone");
		ModLoader.addLocalization("smeltedBrownstone.name", "Brownstone");
		ModLoader.addLocalization("roadBrownstone.name", "Brownstone Road");
		ModLoader.addLocalization("brickBrownstone.name", "Brownstone Brick");
		ModLoader.addLocalization("brickSmallBrownstone.name", "Small Brownstone Brick");
		ModLoader.addLocalization("fancyBrownstone.name", "Fancy Brownstone Brick");
		
		ModLoader.addLocalization("rawBrownstoneMagicSlab.name", "Rough Brownstone Magic Slab");
		ModLoader.addLocalization("smeltedBrownstoneMagicSlab.name", "Brownstone Magic Slab");
		ModLoader.addLocalization("roadBrownstoneMagicSlab.name", "Brownstone Road Magic Slab");
		ModLoader.addLocalization("brickBrownstoneMagicSlab.name", "Brownstone Brick Magic Slab");
		ModLoader.addLocalization("brickSmallBrownstoneMagicSlab.name", "Small Brownstone Brick Magic Slab");
		ModLoader.addLocalization("fancyBrownstoneMagicSlab.name", "Fancy Brownstone Brick Magic Slab");
		
		ModLoader.addLocalization("iceBrick.name", "Ice Brick");
		ModLoader.addLocalization("smallIceBrick.name", "Fancy Ice Brick");
		ModLoader.addLocalization("fancyIceBrick.name", "Fancy Ice Brick");
		
		ModLoader.addLocalization("iceBrickMagicSlab.name", "Ice Brick Magic Slab");
		ModLoader.addLocalization("smallIceBrickMagicSlab.name", "Fancy Ice Brick Magic Slab");
		ModLoader.addLocalization("fancyIceBrickMagicSlab.name", "Fancy Ice Brick Magic Slab");
		
		ModLoader.addLocalization("coalStorage.name", "Coal Block");
		ModLoader.addLocalization("charcoalStorage.name", "Charcoal Block");
		ModLoader.addLocalization("redstoneStorage.name", "Redstone Block");
		ModLoader.addLocalization("slimeStorage.name", "Slime Block");
		ModLoader.addLocalization("boneStorage.name", "Bone Block");
		ModLoader.addLocalization("wheatStorage.name", "Thatching");
		ModLoader.addLocalization("netherrackStorage.name", "Smooth Netherrack");
		ModLoader.addLocalization("sandstoneStorage.name", "Smooth Sandstone");
		ModLoader.addLocalization("slabStorage.name", "Refined Stone");
		ModLoader.addLocalization("brickStorage.name", "Brick block");
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
