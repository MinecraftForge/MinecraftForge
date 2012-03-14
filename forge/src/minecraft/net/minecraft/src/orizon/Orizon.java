package net.minecraft.src.orizon;
import net.minecraft.src.*;
public class Orizon {

	public static void addNames() {
    	ModLoader.addLocalization("mineralCopper.name", "Copper Ore");
    	ModLoader.addLocalization("mineralTurquoise.name", "Turquoise Ore");
    	ModLoader.addLocalization("mineralChalcocite.name", "Chalcocite Ore");
    	ModLoader.addLocalization("mineralCassiterite.name", "Cassiterite Ore");
    	ModLoader.addLocalization("mineralTeallite.name", "Teallite Ore");
    	ModLoader.addLocalization("mineralZincBloom.name", "Zinc Bloom Ore");
    	ModLoader.addLocalization("mineralSphalerite.name", "Sphalerite Ore");
    	ModLoader.addLocalization("mineralCerussite.name", "Cerussite Ore");
    	ModLoader.addLocalization("mineralCobalt.name", "Cobalt Ore");
    	ModLoader.addLocalization("mineralArdite.name", "Ardite Ore");
    	ModLoader.addLocalization("mineralMyuvil.name", "Myuvil Ore");
    	ModLoader.addLocalization("mineralGalena.name", "Galena Ore");
    	ModLoader.addLocalization("mineralIvymetal.name", "Ivymetal Ore");
    	
    	ModLoader.addLocalization("gemRubyOre.name", "Ruby Ore");
    	ModLoader.addLocalization("gemEmeraldOre.name", "Emerald Ore");
    	ModLoader.addLocalization("gemSapphireOre.name", "Sapphire Ore");
    	ModLoader.addLocalization("gemTopazOre.name", "Topaz Ore");
    	ModLoader.addLocalization("gemAmethystOre.name", "Amethyst Ore");
    	ModLoader.addLocalization("gemQuartzOre.name", "Quartz Ore");
    	ModLoader.addLocalization("gemRoseQuartzOre.name", "Rose Quartz Ore");
    	ModLoader.addLocalization("gemRockCrystalOre.name", "Rock Crystal Ore");
    	
    	ModLoader.addLocalization("gemRuby.name", "Ruby");
    	ModLoader.addLocalization("gemEmerald.name", "Emerald");
    	ModLoader.addLocalization("gemSapphire.name", "Sapphire");
    	ModLoader.addLocalization("gemTopaz.name", "Topaz");
    	ModLoader.addLocalization("gemAmethyst.name", "Amethyst");
    	ModLoader.addLocalization("gemQuartz.name", "Quartz");
    	ModLoader.addLocalization("gemRoseQuartz.name", "Rose Quartz");
    	ModLoader.addLocalization("gemRockCrystal.name", "Rock Crystal");
    }
	
	public static void addRecipes()
	{
		ModLoader.addRecipe(new ItemStack(Block.stairSingle, 6, 3), new Object[] 
				{"###", '#', new ItemStack(mod_Orizon.cCobble, 1, -1)});
		ModLoader.addRecipe(new ItemStack(Block.stairSingle, 6, 0), new Object[] 
				{"###", '#', new ItemStack(mod_Orizon.cStone, 1, -1)});
		ModLoader.addRecipe(new ItemStack(Block.stairSingle, 6, 5), new Object[] 
				{"###", '#', new ItemStack(mod_Orizon.cBrick, 1, -1)});
		ModLoader.addRecipe(new ItemStack(Item.brewingStand, 1), new Object[] 
				{" B ", "###", '#', new ItemStack(mod_Orizon.cCobble, 1, -1), 'B', Item.blazeRod});
		ModLoader.addRecipe(new ItemStack(Block.stairCompactCobblestone, 4), new Object[] 
				{"#  ", "## ", "###", '#', new ItemStack(mod_Orizon.cCobble, 1, -1)});
		ModLoader.addRecipe(new ItemStack(Block.stairsStoneBrickSmooth, 4), new Object[] 
				{"#  ", "## ", "###", '#', new ItemStack(mod_Orizon.cBrick, 1, -1)});
		ModLoader.addRecipe(new ItemStack(Block.lever, 1), new Object[] 
				{"X", "#", '#', new ItemStack(mod_Orizon.cCobble, 1, -1), 'X', Item.stick});
		ModLoader.addRecipe(new ItemStack(Item.redstoneRepeater, 1), new Object[] 
				{"#X#", "III", '#', Block.torchRedstoneActive, 'X', Item.redstone, 'I', new ItemStack(mod_Orizon.cStone, 1, -1)});
		ModLoader.addRecipe(new ItemStack(Block.pressurePlateStone, 1), new Object[] 
				{"##", '#', new ItemStack(mod_Orizon.cStone, 1, -1)});
        ModLoader.addRecipe(new ItemStack(Block.dispenser, 1), new Object[] 
        		{"###", "#X#", "#R#", '#', new ItemStack(mod_Orizon.cCobble, 1, -1), 'X', Item.bow, 'R', Item.redstone});
        ModLoader.addRecipe(new ItemStack(Block.pistonBase, 1), new Object[] 
        		{"TTT", "#X#", "#R#", '#', new ItemStack(mod_Orizon.cCobble, 1, -1), 'X', Item.ingotIron, 'R', Item.redstone, 'T', Block.planks});
	
        ModLoader.addRecipe(new ItemStack(Item.pickaxeStone), new Object[] 
				{"###", " | ", " | ", '#', new ItemStack(mod_Orizon.cCobble, 1, -1)}, '|', Item.stick);
        ModLoader.addRecipe(new ItemStack(Item.shovelStone), new Object[] 
				{"#", "|", "|", '#', new ItemStack(mod_Orizon.cCobble, 1, -1)}, '|', Item.stick);
        ModLoader.addRecipe(new ItemStack(Item.axeStone), new Object[] 
				{"##", "#|", " |", '#', new ItemStack(mod_Orizon.cCobble, 1, -1)}, '|', Item.stick);
        ModLoader.addRecipe(new ItemStack(Item.swordStone), new Object[] 
				{"#", "#", "|", '#', new ItemStack(mod_Orizon.cCobble, 1, -1)}, '|', Item.stick);
        ModLoader.addRecipe(new ItemStack(Item.hoeStone), new Object[] 
				{"##", " |", " |", '#', new ItemStack(mod_Orizon.cCobble, 1, -1)}, '|', Item.stick);
        
        ModLoader.addRecipe(new ItemStack(Block.stoneOvenIdle), new Object[] 
        		{"###", "# #", "###", '#', new ItemStack(mod_Orizon.cCobble, 1, -1)});
        for(int i = 0; i < 16; i++) {
        	ModLoader.addRecipe(new ItemStack(mod_Orizon.cBrick, 4, i), new Object[] 
        		{"##", "##", '#', new ItemStack(mod_Orizon.cStone, 1, i)});
        }
	}
}
