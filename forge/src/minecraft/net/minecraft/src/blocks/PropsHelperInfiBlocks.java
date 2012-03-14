package net.minecraft.src.blocks;
import net.minecraft.src.*;

public class PropsHelperInfiBlocks {

	public PropsHelperInfiBlocks() { }
	
	public static InfiProps getProps(InfiProps infiprops)
	{
		mod_InfiBlocks.resolveConflicts = infiprops.readBoolean("Automatically Resolve ID conflicts");
		
		mod_InfiBlocks.blockCraftingID = infiprops.readInt("Crafting Table ID");
		mod_InfiBlocks.blockChestID = infiprops.readInt("Chest ID");
		mod_InfiBlocks.blockFurnaceID = infiprops.readInt("Furnace ID");
		mod_InfiBlocks.magicSlabStoneID = infiprops.readInt("Magic Slab Stone ID");
		mod_InfiBlocks.magicSlabSoilID = infiprops.readInt("Magic Slab Soil ID");
		mod_InfiBlocks.magicSlabWoolID = infiprops.readInt("Magic Slab Wool ID");
		
		mod_InfiBlocks.woolCarpetID = infiprops.readInt("Wool Carpet ID");
		mod_InfiBlocks.craftingGuiID = infiprops.readInt("Crafting Gui ID");
		mod_InfiBlocks.furnaceGuiID = infiprops.readInt("Furnace Gui ID");
		
		mod_InfiBlocks.stainedGlassID = infiprops.readInt("Stained Glass ID");
		mod_InfiBlocks.stainedGlassPaneID = infiprops.readInt("Stained Glass Pane ID");
		mod_InfiBlocks.stainedGlassMagicSlabID = infiprops.readInt("Stained Glass Magic Slab ID");
		
		mod_InfiBlocks.storageBlockID = infiprops.readInt("Storage Block ID");
		mod_InfiBlocks.brickID = infiprops.readInt("Brick ID");
		mod_InfiBlocks.crackedBrickID = infiprops.readInt("Cracked Brick ID");
		mod_InfiBlocks.fancyBrickID = infiprops.readInt("Fancy Brick ID");
		mod_InfiBlocks.runeBrickID = infiprops.readInt("Runic Brick ID");
		mod_InfiBlocks.infiGlassID = infiprops.readInt("Infi-Glass ID");
		mod_InfiBlocks.infiGlassPaneID = infiprops.readInt("Infi-Glass Pane ID");
		mod_InfiBlocks.iceBrickID = infiprops.readInt("Ice Brick ID");
		mod_InfiBlocks.brownstoneID = infiprops.readInt("Brownstone ID");
		
		mod_InfiBlocks.storageBlockMagicSlabID = infiprops.readInt("Storage Block Magic Slab ID");
		mod_InfiBlocks.brickMagicSlabID = infiprops.readInt("Brick Magic Slab ID");
		mod_InfiBlocks.crackedBrickMagicSlabID = infiprops.readInt("Cracked Brick Magic Slab ID");
		mod_InfiBlocks.fancyBrickMagicSlabID = infiprops.readInt("Fancy Brick Magic Slab ID");
		mod_InfiBlocks.runeBrickMagicSlabID = infiprops.readInt("Runic Brick Magic Slab ID");
		mod_InfiBlocks.infiGlassMagicSlabID = infiprops.readInt("Infi-Glass Magic Slab ID");
		mod_InfiBlocks.iceBrickMagicSlabID = infiprops.readInt("Ice Brick Magic Slab ID");
		mod_InfiBlocks.brownstoneMagicSlabID = infiprops.readInt("Brownstone Magic Slab ID");
		
		mod_InfiBlocks.chiselID = infiprops.readInt("Chisel ID");
		return infiprops;
	}

	public static InfiProps InitProps(InfiProps infiprops)
	{
		infiprops.accessBoolean("Automatically Resolve ID conflicts", true);
		
		infiprops.accessInt("Crafting Table ID", 196);
		infiprops.accessInt("Chest ID", 197);
		infiprops.accessInt("Furnace ID", 198);
		infiprops.accessInt("Wool Carpet ID", 200);
		
		infiprops.accessInt("Magic Slab Stone ID", 193);
		infiprops.accessInt("Magic Slab Soil ID", 194);
		infiprops.accessInt("Magic Slab Wool ID", 195);
		
		infiprops.accessInt("Stained Glass ID", 201);
		infiprops.accessInt("Stained Glass Pane ID", 202);
		infiprops.accessInt("Stained Glass Magic Slab ID", 203);
		
		infiprops.accessInt("Storage Block ID", 204);
		infiprops.accessInt("Storage Block Magic Slab ID", 205);
		
		infiprops.accessInt("Brick ID", 206);
		infiprops.accessInt("Brick Magic Slab ID", 207);
		
		infiprops.accessInt("Cracked Brick ID", 208);
		infiprops.accessInt("Cracked Brick Magic Slab ID", 209);
		
		infiprops.accessInt("Fancy Brick ID", 210);
		infiprops.accessInt("Fancy Brick Magic Slab ID", 211);
		
		infiprops.accessInt("Runic Brick ID", 212);
		infiprops.accessInt("Runic Brick Magic Slab ID", 213);
		
		infiprops.accessInt("Infi-Glass ID", 214);
		infiprops.accessInt("Infi-Glass Pane ID", 215);
		infiprops.accessInt("Infi-Glass Magic Slab ID", 216);
		
		infiprops.accessInt("Ice Brick ID", 189);
		infiprops.accessInt("Ice Brick Magic Slab ID", 190);
		
		infiprops.accessInt("Brownstone ID", 191);
		infiprops.accessInt("Brownstone Magic Slab ID", 192);
		
		infiprops.accessInt("Crafting Gui ID", 172);
		infiprops.accessInt("Furnace Gui ID", 173);
		
		infiprops.accessInt("Chisel ID", 12101);
		return infiprops;
	}
	
	public static boolean resolveIDs(InfiProps props)
	{	
		mod_InfiBlocks.blockCraftingID = changeID(props, mod_InfiBlocks.blockCraftingID, 
				"Crafting Table ID");
		mod_InfiBlocks.woolCarpetID = changeID(props, mod_InfiBlocks.woolCarpetID, 
				"Wool Carpet ID");
		mod_InfiBlocks.blockChestID = changeID(props, mod_InfiBlocks.blockChestID, 
				"Chest ID");
		mod_InfiBlocks.blockFurnaceID = changeID(props, mod_InfiBlocks.blockFurnaceID, 
				"Furnace ID");
		mod_InfiBlocks.magicSlabStoneID = changeID(props, mod_InfiBlocks.magicSlabStoneID, 
				"Magic Slab Stone ID");
		mod_InfiBlocks.magicSlabSoilID = changeID(props, mod_InfiBlocks.magicSlabSoilID, 
				"Magic Slab Soil ID");
		mod_InfiBlocks.magicSlabWoolID = changeID(props, mod_InfiBlocks.magicSlabWoolID, 
				"Magic Slab Wool ID");
		mod_InfiBlocks.stainedGlassID = changeID(props, mod_InfiBlocks.stainedGlassID, 
				"Stained Glass ID");
		mod_InfiBlocks.stainedGlassPaneID = changeID(props, mod_InfiBlocks.stainedGlassPaneID, 
				"Stained Glass Pane ID");
		mod_InfiBlocks.stainedGlassMagicSlabID = changeID(props, mod_InfiBlocks.stainedGlassMagicSlabID, 
				"Stained Glass Magic Slab ID");
		mod_InfiBlocks.storageBlockID = changeID(props, mod_InfiBlocks.storageBlockID, 
				"Storage Block ID");
		mod_InfiBlocks.brickID = changeID(props, mod_InfiBlocks.brickID, 
				"Brick ID");
		mod_InfiBlocks.crackedBrickID = changeID(props, mod_InfiBlocks.crackedBrickID, 
				"Cracked Brick ID");
		mod_InfiBlocks.fancyBrickID = changeID(props, mod_InfiBlocks.fancyBrickID, 
				"Fancy Brick ID");
		mod_InfiBlocks.runeBrickID = changeID(props, mod_InfiBlocks.runeBrickID, 
				"Runic Brick ID");
		mod_InfiBlocks.infiGlassID = changeID(props, mod_InfiBlocks.infiGlassID, 
				"Infi-Glass ID");
		mod_InfiBlocks.iceBrickID = changeID(props, mod_InfiBlocks.iceBrickID, 
				"Ice Brick ID");
		mod_InfiBlocks.brownstoneID = changeID(props, mod_InfiBlocks.brownstoneID, 
				"Brownstone ID");
		mod_InfiBlocks.storageBlockMagicSlabID = changeID(props, mod_InfiBlocks.storageBlockMagicSlabID, 
				"Storage Block Magic Slab ID");
		mod_InfiBlocks.brickMagicSlabID = changeID(props, mod_InfiBlocks.brickMagicSlabID, 
				"Brick Magic Slab ID");
		mod_InfiBlocks.fancyBrickMagicSlabID = changeID(props, mod_InfiBlocks.fancyBrickMagicSlabID, 
				"Fancy Brick Magic Slab ID");
		mod_InfiBlocks.iceBrickMagicSlabID = changeID(props, mod_InfiBlocks.iceBrickMagicSlabID, 
				"Ice Brick Magic Slab ID");
		mod_InfiBlocks.brownstoneMagicSlabID = changeID(props, mod_InfiBlocks.brownstoneMagicSlabID, 
				"Brownstone Magic Slab ID");
		props.writeBoolean("Automatically Resolve ID conflicts", false);
		return true;
	}
	
	private static int idStart = 130;
	
	private static int changeID(InfiProps props, int id, String s)
	{
		if(Block.blocksList[id] != null) {
			for(int i = idStart; i < Block.blocksList.length; i++) {
				if(Block.blocksList[i] == null) {
					props.writeInt(s, i);
					idStart = i + 1;
					return i;
				}
			}
		}
		return id;
	}
}
