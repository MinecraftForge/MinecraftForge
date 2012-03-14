package net.minecraft.src.orizon;
import net.minecraft.src.*;

public class PropsHelperOrizon {

	public PropsHelperOrizon() { }
	
	public static InfiProps getProps(InfiProps infiprops)
	{
		mod_Orizon.resolveConflicts = infiprops.readBoolean("Automatically Resolve ID conflicts");
		
		mod_Orizon.mineralOreID = infiprops.readInt("Mineral Ore ID");
		mod_Orizon.mineralOreHighID = infiprops.readInt("Mineral Ore High ID");
		mod_Orizon.mineralOreLow1ID = infiprops.readInt("Mineral Ore Medium ID");
		mod_Orizon.mineralOreLow2ID = infiprops.readInt("Mineral Ore Low ID");
		mod_Orizon.mineralOreLow3ID = infiprops.readInt("Mineral Ore Dark ID");
		
		mod_Orizon.mineralOreAltID = infiprops.readInt("Mineral Ore Alt ID");
		mod_Orizon.mineralOreAltHighID = infiprops.readInt("Mineral Ore Alt High ID");
		mod_Orizon.mineralOreAltLow1ID = infiprops.readInt("Mineral Ore Alt Medium ID");
		mod_Orizon.mineralOreAltLow2ID = infiprops.readInt("Mineral Ore Alt Low ID");
		mod_Orizon.mineralOreAltLow3ID = infiprops.readInt("Mineral Ore Alt Dark ID");
		
		mod_Orizon.gemOreID = infiprops.readInt("Gem Ore ID");
		mod_Orizon.gemOreHighID = infiprops.readInt("Gem Ore High ID");
		mod_Orizon.gemOreLow1ID = infiprops.readInt("Gem Ore Medium ID");
		mod_Orizon.gemOreLow2ID = infiprops.readInt("Gem Ore Low ID");
		mod_Orizon.gemOreLow3ID = infiprops.readInt("Gem Ore Dark ID");
		
		mod_Orizon.cStoneID = infiprops.readInt("Colored Stone ID");
		mod_Orizon.cCobbleID = infiprops.readInt("Colored Cobblestone ID");
		mod_Orizon.cBrickID = infiprops.readInt("Colored Brick ID");
		mod_Orizon.cMossyID = infiprops.readInt("Colored Mossy Brick ID");
		mod_Orizon.cCrackedID = infiprops.readInt("Colored Cracked Brick ID");
		mod_Orizon.cTileID = infiprops.readInt("Colored Brick Tile ID");
		mod_Orizon.cFancyID = infiprops.readInt("Colored Fancy Brick ID");
		mod_Orizon.cSquareID = infiprops.readInt("Colored Square Brick ID");
		
		mod_Orizon.calciteOreID = infiprops.readInt("Calcite Ore ID");
		mod_Orizon.netherOreID = infiprops.readInt("Nether Ore ID");
		
		mod_Orizon.replaceOreID = infiprops.readInt("Replacement Ore ID");
		mod_Orizon.replaceMetalID = infiprops.readInt("Replacement Metal Ore ID");
		
		mod_Orizon.marbleID = infiprops.readInt("Marble ID");
		
		mod_Orizon.slimePoolID = infiprops.readInt("Slime Pool IDs");
		
		return infiprops;
	}

	public static InfiProps InitProps(InfiProps infiprops)
	{
		infiprops.accessBoolean("Automatically Resolve ID conflicts", true);
		
		infiprops.accessInt("Mineral Ore ID", 137);
		infiprops.accessInt("Mineral Ore High ID", 138);
		infiprops.accessInt("Mineral Ore Medium ID", 139);
		infiprops.accessInt("Mineral Ore Low ID", 140);
		infiprops.accessInt("Mineral Ore Dark ID", 141);
		
		infiprops.accessInt("Mineral Ore Alt ID", 142);
		infiprops.accessInt("Mineral Ore Alt High ID", 143);
		infiprops.accessInt("Mineral Ore Alt Medium ID", 144);
		infiprops.accessInt("Mineral Ore Alt Low ID", 145);
		infiprops.accessInt("Mineral Ore Alt Dark ID", 146);
		
		infiprops.accessInt("Gem Ore ID", 147);
		infiprops.accessInt("Gem Ore High ID", 148);
		infiprops.accessInt("Gem Ore Medium ID", 149);
		infiprops.accessInt("Gem Ore Low ID", 150);
		infiprops.accessInt("Gem Ore Dark ID", 151);
		
		infiprops.accessInt("Colored Stone ID", 152);
		infiprops.accessInt("Colored Cobblestone ID", 153);
		infiprops.accessInt("Colored Brick ID", 154);
		infiprops.accessInt("Colored Mossy Brick ID", 155);
		infiprops.accessInt("Colored Cracked Brick ID", 156);
		infiprops.accessInt("Colored Brick Tile ID", 157);
		infiprops.accessInt("Colored Fancy Brick ID", 158);
		infiprops.accessInt("Colored Square Brick ID", 159);
		
		infiprops.accessInt("Replacement Ore ID", 160);
		infiprops.accessInt("Replacement Metal Ore ID", 161);
		
		infiprops.accessInt("Calcite Ore ID", 162);
		infiprops.accessInt("Nether Ore ID", 163);
		infiprops.accessInt("Marble ID", 164);
		
		infiprops.accessInt("Slime Pool IDs", 3239);
		
		return infiprops;
	}
	
	public static InfiProps getSpawn(InfiProps infiprops)
	{
		mod_Orizon.genStratifiedStone = infiprops.readBoolean("Generate Stratified Stone");
		mod_Orizon.genColoredStone = infiprops.readBoolean("Generate Colored Stone");
		mod_Orizon.genCalcite = infiprops.readBoolean("Generate Calcite and Calcite Ores");
		mod_Orizon.genMarble = infiprops.readBoolean("Generate Marble");
		mod_Orizon.genNetherOre = infiprops.readBoolean("Generate Nether Ores");
		mod_Orizon.genSilt = infiprops.readBoolean("Generate Silt");
		mod_Orizon.redoVanillaOres = infiprops.readBoolean("Metallurgy Style Spawn");
		mod_Orizon.replaceOres = infiprops.readBoolean("Replace Vanilla Ores");
		mod_Orizon.genGems = infiprops.readBoolean("Generate Gemstones");
		mod_Orizon.genSlimePools = infiprops.readBoolean("Generate Slime Pools");
		
		mod_Orizon.copperRarity = infiprops.readInt("Copper Rarity");
	    mod_Orizon.copperHeight = infiprops.readInt("Copper Height");
	    mod_Orizon.turquoiseRarity = infiprops.readInt("Turquoise Rarity");
	    mod_Orizon.turquoiseHeight = infiprops.readInt("Turquoise Height");
	    mod_Orizon.chalcociteRarity = infiprops.readInt("Chalcocite Rarity");
	    mod_Orizon.chalcociteHeight = infiprops.readInt("Chalcocite Height");
	    mod_Orizon.cassiteriteRarity = infiprops.readInt("Cassiterite Rarity");
	    mod_Orizon.cassiteriteHeight = infiprops.readInt("Cassiterite Height");
	    mod_Orizon.tealliteRarity = infiprops.readInt("Teallite Rarity");
	    mod_Orizon.tealliteHeight = infiprops.readInt("Teallite Height");
	    mod_Orizon.zincBloomRarity = infiprops.readInt("Zinc Bloom Rarity");
	    mod_Orizon.zincBloomHeight = infiprops.readInt("Zinc Bloom Height");
	    mod_Orizon.sphaleriteRarity = infiprops.readInt("Sphalerite Rarity");
	    mod_Orizon.sphaleriteHeight = infiprops.readInt("Sphalerite Height");
	    mod_Orizon.cerussiteRarity = infiprops.readInt("Cerussite Rarity");
	    mod_Orizon.cerussiteHeight = infiprops.readInt("Cerussite Height");
	    mod_Orizon.cobaltRarity = infiprops.readInt("Cobalt Rarity");
	    mod_Orizon.cobaltHeight = infiprops.readInt("Cobalt Height");
	    mod_Orizon.arditeRarity = infiprops.readInt("Ardite Rarity");
	    mod_Orizon.arditeHeight = infiprops.readInt("Ardite Height");
	    mod_Orizon.myuvilRarity = infiprops.readInt("Myuvil Rarity");
	    mod_Orizon.myuvilHeight = infiprops.readInt("Myuvil Height");
	    mod_Orizon.galenaRarity = infiprops.readInt("Galena Rarity");
	    mod_Orizon.galenaHeight = infiprops.readInt("Galena Height");
	    mod_Orizon.ivymetalRarity = infiprops.readInt("Ivymetal Rarity");
	    mod_Orizon.ivymetalHeight = infiprops.readInt("Ivymetal Height");
	    mod_Orizon.aggregateRarity = infiprops.readInt("Aggregate Rarity");
	    mod_Orizon.aggregateHeight = infiprops.readInt("Aggregate Height");
	    
	    mod_Orizon.goldRarity = infiprops.readInt("Gold Rarity");
	    mod_Orizon.goldHeight = infiprops.readInt("Gold Height"); 
	    mod_Orizon.diamondRarity = infiprops.readInt("Diamond Rarity");
	    mod_Orizon.diamondHeight = infiprops.readInt("Diamond Height"); 
	    mod_Orizon.lapisRarity = infiprops.readInt("Lapis Rarity");
	    mod_Orizon.lapisHeight = infiprops.readInt("Lapis Height"); 
	    mod_Orizon.redstoneRarity = infiprops.readInt("Redstone Rarity");
	    mod_Orizon.redstoneHeight = infiprops.readInt("Redstone Height");
	    
	    mod_Orizon.rubyRarity = infiprops.readInt("Ruby Rarity");
	    mod_Orizon.rubyHeight = infiprops.readInt("Ruby Height");
	    mod_Orizon.emeraldRarity = infiprops.readInt("Emerald Rarity");
	    mod_Orizon.emeraldHeight = infiprops.readInt("Emerald Height");
	    mod_Orizon.sapphireRarity = infiprops.readInt("Sapphire Rarity");
	    mod_Orizon.sapphireHeight = infiprops.readInt("Sapphire Height");
	    mod_Orizon.topazRarity = infiprops.readInt("Topaz Rarity");
	    mod_Orizon.topazHeight = infiprops.readInt("Topaz Height");
	    mod_Orizon.amethystRarity = infiprops.readInt("Amethyst Rarity");
	    mod_Orizon.amethystHeight = infiprops.readInt("Amethyst Height");
	    mod_Orizon.quartzRarity = infiprops.readInt("Quartz Rarity");
	    mod_Orizon.quartzHeight = infiprops.readInt("Quartz Height");
	    mod_Orizon.roseQuartzRarity = infiprops.readInt("Rose Quartz Rarity");
	    mod_Orizon.roseQuartzHeight = infiprops.readInt("Rose Quartz Height");
	    mod_Orizon.rockCrystalRarity = infiprops.readInt("Rock Crystal Rarity");
	    mod_Orizon.rockCrystalHeight = infiprops.readInt("Rock Crystal Height");
	    
	    mod_Orizon.marbleRarity = infiprops.readInt("Marble Rarity");
	    mod_Orizon.marbleHeight = infiprops.readInt("Marble Height");
	    
	    mod_Orizon.slimePoolRarity = infiprops.readInt("Slime Pool Rarity");
	    mod_Orizon.slimePoolHeight = infiprops.readInt("Slime Pool Height");
		
		return infiprops;
	}

	public static InfiProps InitSpawn(InfiProps infiprops)
	{		
		infiprops.accessBoolean("Generate Stratified Stone", true);
		infiprops.accessBoolean("Generate Colored Stone", true);
		infiprops.accessBoolean("Generate Calcite and Calcite Ores", true);
		infiprops.accessBoolean("Generate Marble", true);
		infiprops.accessBoolean("Generate Nether Ores", true);
		infiprops.accessBoolean("Generate Silt", true);
		infiprops.accessBoolean("Metallurgy Style Spawn", true);
		infiprops.accessBoolean("Replace Vanilla Ores", true);
		infiprops.accessBoolean("Generate Gemstones", true);
		infiprops.accessBoolean("Generate Slime Pools", true);
		
		infiprops.accessInt("Copper Rarity", 8);
	    infiprops.accessInt("Copper Height", 64);
	    infiprops.accessInt("Turquoise Rarity", 10);
	    infiprops.accessInt("Turquoise Height", 64);
	    infiprops.accessInt("Chalcocite Rarity", 4);
	    infiprops.accessInt("Chalcocite Height", 64);
	    infiprops.accessInt("Cassiterite Rarity", 10);
	    infiprops.accessInt("Cassiterite Height", 64);
	    infiprops.accessInt("Teallite Rarity", 8);
	    infiprops.accessInt("Teallite Height", 64);
	    infiprops.accessInt("Zinc Bloom Rarity", 9);
	    infiprops.accessInt("Zinc Bloom Height", 64);
	    infiprops.accessInt("Sphalerite Rarity", 7);
	    infiprops.accessInt("Sphalerite Height", 64);
	    infiprops.accessInt("Cerussite Rarity", 3);
	    infiprops.accessInt("Cerussite Height", 64);
	    infiprops.accessInt("Cobalt Rarity", 5);
	    infiprops.accessInt("Cobalt Height", 64);
	    infiprops.accessInt("Ardite Rarity", 5);
	    infiprops.accessInt("Ardite Height", 64);
	    infiprops.accessInt("Myuvil Rarity", 3);
	    infiprops.accessInt("Myuvil Height", 64);
	    infiprops.accessInt("Galena Rarity", 3);
	    infiprops.accessInt("Galena Height", 64);
	    infiprops.accessInt("Ivymetal Rarity", 2);
	    infiprops.accessInt("Ivymetal Height", 64);
	    infiprops.accessInt("Aggregate Rarity", 8);
	    infiprops.accessInt("Aggregate Height", 64);
	    
	    infiprops.accessInt("Gold Rarity", 4);
	    infiprops.accessInt("Gold Height", 64);
	    infiprops.accessInt("Diamond Rarity", 1);
	    infiprops.accessInt("Diamond Height", 64);
	    infiprops.accessInt("Lapis Rarity", 2);
	    infiprops.accessInt("Lapis Height", 64);
	    infiprops.accessInt("Redstone Rarity", 10);
	    infiprops.accessInt("Redstone Height", 64);
	    
	    infiprops.accessInt("Ruby Rarity", 1);
	    infiprops.accessInt("Ruby Height", 64);
	    infiprops.accessInt("Sapphire Rarity", 1);
	    infiprops.accessInt("Sapphire Height", 64);
	    infiprops.accessInt("Emerald Rarity", 1);
	    infiprops.accessInt("Emerald Height", 64);
	    infiprops.accessInt("Topaz Rarity", 1);
	    infiprops.accessInt("Topaz Height", 64);
	    infiprops.accessInt("Amethyst Rarity", 1);
	    infiprops.accessInt("Amethyst Height", 64);
	    infiprops.accessInt("Quartz Rarity", 2);
	    infiprops.accessInt("Quartz Height", 64);
	    infiprops.accessInt("Rose Quartz Rarity", 2);
	    infiprops.accessInt("Rose Quartz Height", 64);
	    infiprops.accessInt("Rock Crystal Rarity", 2);
	    infiprops.accessInt("Rock Crystal Height", 64);
	    
	    infiprops.accessInt("Marble Rarity", 22);
	    infiprops.accessInt("Marble Height", 64);
	    
	    infiprops.accessInt("Slime Pool Rarity", 30);
	    infiprops.accessInt("Slime Pool Height", 40);
		return infiprops;
	}
	
	public static boolean resolveIDs(InfiProps props)
	{
		mod_Orizon.mineralOreID = changeID(props, mod_Orizon.mineralOreID, 
				"Mineral Ore ID");
		mod_Orizon.mineralOreHighID = changeID(props, mod_Orizon.mineralOreHighID, 
				"Mineral Ore High ID");
		mod_Orizon.mineralOreLow1ID = changeID(props, mod_Orizon.mineralOreLow1ID, 
				"Mineral Ore Medium ID");
		mod_Orizon.mineralOreLow2ID = changeID(props, mod_Orizon.mineralOreLow2ID, 
				"Mineral Ore Low ID");
		mod_Orizon.mineralOreLow3ID = changeID(props, mod_Orizon.mineralOreLow3ID, 
				"Mineral Ore Dark ID");
		
		mod_Orizon.mineralOreAltID = changeID(props, mod_Orizon.mineralOreAltID, 
				"Mineral Ore Alt ID");
		mod_Orizon.mineralOreAltHighID = changeID(props, mod_Orizon.mineralOreAltHighID, 
				"Mineral Ore Alt High ID");
		mod_Orizon.mineralOreAltLow1ID = changeID(props, mod_Orizon.mineralOreAltLow1ID, 
				"Mineral Ore Alt Medium ID");
		mod_Orizon.mineralOreAltLow2ID = changeID(props, mod_Orizon.mineralOreAltLow2ID, 
				"Mineral Ore Alt Low ID");
		mod_Orizon.mineralOreAltLow3ID = changeID(props, mod_Orizon.mineralOreAltLow3ID, 
				"Mineral Ore Alt Dark ID");
		
		mod_Orizon.gemOreID = changeID(props, mod_Orizon.gemOreID, 
				"Gem Ore ID");
		mod_Orizon.gemOreHighID = changeID(props, mod_Orizon.gemOreHighID, 
				"Gem Ore High ID");
		mod_Orizon.gemOreLow1ID = changeID(props, mod_Orizon.gemOreLow1ID, 
				"Gem Ore Medium ID");
		mod_Orizon.gemOreLow2ID = changeID(props, mod_Orizon.gemOreLow2ID, 
				"Gem Ore Low ID");
		mod_Orizon.gemOreLow3ID = changeID(props, mod_Orizon.gemOreLow3ID, 
				"Gem Ore Dark ID");
		
		mod_Orizon.marbleID = changeID(props, mod_Orizon.marbleID, 
				"Marble ID");
		mod_Orizon.calciteOreID = changeID(props, mod_Orizon.calciteOreID, 
				"Calcite Ore ID");
		mod_Orizon.netherOreID = changeID(props, mod_Orizon.calciteOreID, 
				"Nether Ore ID");
		mod_Orizon.replaceOreID = changeID(props, mod_Orizon.replaceOreID, 
				"Replacement Ore ID");
		mod_Orizon.replaceMetalID = changeID(props, mod_Orizon.replaceMetalID, 
				"Replacement Metal Ore ID");
		
		mod_Orizon.cStoneID = changeID(props, mod_Orizon.cStoneID, 
				"Colored Stone ID");
		mod_Orizon.cCobbleID = changeID(props, mod_Orizon.cCobbleID, 
				"Colored Cobblestone ID");
		mod_Orizon.cBrickID = changeID(props, mod_Orizon.cBrickID, 
				"Colored Brick ID");
		mod_Orizon.cMossyID = changeID(props, mod_Orizon.cMossyID, 
				"Colored Mossy Brick ID");
		mod_Orizon.cCrackedID = changeID(props, mod_Orizon.cCrackedID, 
				"Colored Cracked Brick ID");
		mod_Orizon.cTileID = changeID(props, mod_Orizon.cTileID, 
				"Colored Brick Tile ID");
		mod_Orizon.cFancyID = changeID(props, mod_Orizon.cFancyID, 
				"Colored Fancy Brick ID");
		mod_Orizon.cSquareID = changeID(props, mod_Orizon.cSquareID, 
				"Colored Square Brick ID");
		
		props.writeBoolean("Automatically Resolve ID conflicts", false);
		return true;
	}
	
	private static int idStart = 130;
	
	public static int changeID(InfiProps props, int id, String s)
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
