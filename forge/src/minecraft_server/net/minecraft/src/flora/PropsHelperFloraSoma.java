package net.minecraft.src.flora;
import net.minecraft.src.*;

public class PropsHelperFloraSoma {
	
	PropsHelperFloraSoma()
	{
	}
	
	public static InfiProps getIDs(InfiProps infiprops)
	{
		mod_FloraSoma.resolveConflicts = infiprops.readBoolean("Automatically Resolve ID conflicts");
		
		mod_FloraSoma.seedBagID = infiprops.readInt("Seed Bag ID");
		mod_FloraSoma.berryID = infiprops.readInt("Berry Food ID");
		mod_FloraSoma.barleySeedID = infiprops.readInt("Barley Seed ID");
		mod_FloraSoma.foodID = infiprops.readInt("Food Items ID");
		mod_FloraSoma.ingredientsID = infiprops.readInt("ingredientsID");
		mod_FloraSoma.pumpkinPieID = infiprops.readInt("Pumpkin Pie ID");
		mod_FloraSoma.boatItemID = infiprops.readInt("Boat Item ID");
		mod_FloraSoma.redwoodDoorItemID = infiprops.readInt("Redwood Door Item ID");
		
		mod_FloraSoma.berryBlockID = infiprops.readInt("Berry Bush ID");
		mod_FloraSoma.redwoodID = infiprops.readInt("Wood ID");
		mod_FloraSoma.floraLeavesID = infiprops.readInt("Leaves ID");
		mod_FloraSoma.floraCropsID = infiprops.readInt("Crops ID");
		mod_FloraSoma.cloudID = infiprops.readInt("Cloud ID");
		mod_FloraSoma.corruptorID = infiprops.readInt("Corruptor ID");
		mod_FloraSoma.corruptBrickID = infiprops.readInt("Corrupt Brick ID");
		mod_FloraSoma.floraSaplingID = infiprops.readInt("Sapling ID");
		mod_FloraSoma.cherryLeavesID = infiprops.readInt("Cherry Leaves ID");
		mod_FloraSoma.redwoodDoorID = infiprops.readInt("Redwood Door ID");
		mod_FloraSoma.saguaroID = infiprops.readInt("Saguaro Cactus ID");
		
		mod_FloraSoma.redwoodBoatID = infiprops.readInt("Redwood Boat ID");
		mod_FloraSoma.bloodBoatID = infiprops.readInt("Bloodboat ID");
		mod_FloraSoma.whiteBoatID = infiprops.readInt("White Boat ID");
		mod_FloraSoma.eucalyptusBoatID = infiprops.readInt("Eucalyptus Boat ID");
		return infiprops;
	}

	public static InfiProps InitIDs(InfiProps infiprops)
	{
		infiprops.accessBoolean("Automatically Resolve ID conflicts", true);
		
		infiprops.accessInt("Seed Bag ID", 12401);
		infiprops.accessInt("Berry Food ID", 12402);
		infiprops.accessInt("Barley Seed ID", 12403);
		infiprops.accessInt("Food Items ID", 12404);
		infiprops.accessInt("Boat Item ID", 12405);
		infiprops.accessInt("Redwood Door Item ID", 12406);

		infiprops.accessInt("ingredientsID", 14033);
		
		infiprops.accessInt("Berry Bush ID", 167);
		infiprops.accessInt("Wood ID", 168);
		infiprops.accessInt("Leaves ID", 169);
		infiprops.accessInt("Crops ID", 170);
		infiprops.accessInt("Cloud ID", 171);
		infiprops.accessInt("Corruptor ID", 172);
		infiprops.accessInt("Corrupt Brick ID", 173);
		infiprops.accessInt("Sapling ID", 174);
		infiprops.accessInt("Cherry Leaves ID", 175);
		infiprops.accessInt("Redwood Door ID", 176);
		infiprops.accessInt("Saguaro Cactus ID", 177);
		
		infiprops.accessInt("Redwood Boat ID", 62);
		infiprops.accessInt("Bloodboat ID", 63);
		infiprops.accessInt("White Boat ID", 64);
		infiprops.accessInt("Eucalyptus Boat ID", 65);
		return infiprops;
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
	
	public static boolean resolveIDs(InfiProps props)
	{
		mod_FloraSoma.berryBlockID = changeID(props, mod_FloraSoma.berryBlockID, "Berry Bush ID");
		mod_FloraSoma.redwoodID = changeID(props, mod_FloraSoma.redwoodID, "Wood ID");
		mod_FloraSoma.floraLeavesID = changeID(props, mod_FloraSoma.floraLeavesID, "Leaves ID");
		mod_FloraSoma.cherryLeavesID = changeID(props, mod_FloraSoma.cherryLeavesID, "Cherry Leaves ID");
		mod_FloraSoma.cloudID = changeID(props, mod_FloraSoma.cloudID, "Cloud ID");
		mod_FloraSoma.corruptorID = changeID(props, mod_FloraSoma.corruptorID, "Corruptor ID");
		mod_FloraSoma.corruptBrickID = changeID(props, mod_FloraSoma.corruptBrickID, "Corrupt Brick ID");
		mod_FloraSoma.redwoodDoorID = changeID(props, mod_FloraSoma.redwoodDoorID, "Redwood Door ID");
		mod_FloraSoma.saguaroID = changeID(props, mod_FloraSoma.saguaroID, "Saguaro Cactus ID");
			
		props.writeBoolean("Automatically Resolve ID conflicts", false);
		return true;
	}

	public static InfiProps InitSpawnRate(InfiProps spawnProps) {
		spawnProps.accessBoolean("Enable Crops", true);
		spawnProps.accessBoolean("Enable Berry Bushes", true);
		spawnProps.accessBoolean("Enable Trees", true);
		spawnProps.accessBoolean("Enable Clouds", true);
		spawnProps.accessBoolean("Enable Corruptor", true);
		spawnProps.accessBoolean("Enable Saguaro Cactus", true);
		
		spawnProps.accessInt("Barley Spawn Density", 64);
		spawnProps.accessInt("Barley Spawn Height", 64);
		
		spawnProps.accessInt("Raspberry Spawn Density", 14);
		spawnProps.accessInt("Raspberry Spawn Height", 64);
		spawnProps.accessInt("Raspberry Spawn Range", 128);
		spawnProps.accessInt("Blueberry Spawn Density", 12);
		spawnProps.accessInt("Blueberry Spawn Height", 64);
		spawnProps.accessInt("Blueberry Spawn Range", 128);
		spawnProps.accessInt("Blackberry Spawn Density", 10);
		spawnProps.accessInt("Blackberry Spawn Height", 64);
		spawnProps.accessInt("Blackberry Spawn Range", 128);
		spawnProps.accessInt("Geoberry Spawn Density", 14);
		spawnProps.accessInt("Geoberry Spawn Height", 64);
		spawnProps.accessInt("Geoberry Spawn Range", 128);
		
		spawnProps.accessInt("Redwood Tree Spawn Density", 6);
		spawnProps.accessInt("Redwood Tree Spawn Height", 64);
		spawnProps.accessInt("Redwood Tree Spawn Range", 32);
		spawnProps.accessInt("Bloodwood Tree Spawn Density", 5);
		spawnProps.accessInt("Bloodwood Tree Spawn Height", 32);
		spawnProps.accessInt("Bloodwood Tree Spawn Range", 64);
		spawnProps.accessInt("Small Eucalyptus Tree Spawn Density", 25);
		spawnProps.accessInt("Small Eucalyptus Tree Spawn Height", 64);
		spawnProps.accessInt("Small Eucalyptus Tree Spawn Range", 32);
		spawnProps.accessInt("Sakura Tree Spawn Density", 10);
		spawnProps.accessInt("Sakura Tree Spawn Height", 64);
		spawnProps.accessInt("Sakura Tree Spawn Range", 32);
		spawnProps.accessInt("Ghost Tree Spawn Density", 25);
		spawnProps.accessInt("Ghost Tree Spawn Height", 16);
		spawnProps.accessInt("Ghost Tree Spawn Range", 80);
		
		spawnProps.accessInt("Cloud Spawn Density", 10);
		spawnProps.accessInt("Cloud Spawn Height", 192);
		spawnProps.accessInt("Cloud Spawn Range", 48);
		spawnProps.accessInt("Dark Cloud Spawn Density", 10);
		spawnProps.accessInt("Dark Cloud Spawn Height", 64);
		spawnProps.accessInt("Dark Cloud Spawn Range", 128);
		spawnProps.accessInt("Sulfur Cloud Spawn Density", 20);
		spawnProps.accessInt("Sulfur Cloud Spawn Height", 40);
		spawnProps.accessInt("Sulfur Cloud Spawn Range", 78);
		spawnProps.accessInt("Ash Cloud Spawn Density", 20);
		spawnProps.accessInt("Ash Cloud Spawn Height", 40);
		spawnProps.accessInt("Ash Cloud Spawn Range", 78);
		
		spawnProps.accessInt("Corruptor Spawn Density", 1);
		spawnProps.accessInt("Corruptor Spawn Height", 0);
		spawnProps.accessInt("Corruptor Spawn Range", 128);
		spawnProps.accessInt("Corruptor Spawn Size", 8);
		spawnProps.accessInt("Corruption Speed", 5);
		
		spawnProps.accessInt("Saguaro Cactus Spawn Density", 1);
		spawnProps.accessInt("Saguaro Cactus Spawn Height", 0);
		spawnProps.accessInt("Saguaro Cactus Spawn Range", 128);
		spawnProps.accessInt("Saguaro Cactus Spawn Size", 8);
		
		return spawnProps;
	}

	public static InfiProps getSpawnRate(InfiProps spawnProps) {
		mod_FloraSoma.enableCrops = spawnProps.readBoolean("Enable Crops");
		mod_FloraSoma.enableBerryBush = spawnProps.readBoolean("Enable Berry Bushes");
		mod_FloraSoma.enableTrees = spawnProps.readBoolean("Enable Trees");
		mod_FloraSoma.enableClouds = spawnProps.readBoolean("Enable Clouds");
		mod_FloraSoma.enableCorruptor = spawnProps.readBoolean("Enable Corruptor");
		mod_FloraSoma.enableSaguaro = spawnProps.readBoolean("Enable Saguaro Cactus");
		
		mod_FloraSoma.barleySpawnDensity = spawnProps.readInt("Barley Spawn Density");
		mod_FloraSoma.barleySpawnHeight = spawnProps.readInt("Barley Spawn Height");
		
		mod_FloraSoma.raspSpawnDensity = spawnProps.readInt("Raspberry Spawn Density");
		mod_FloraSoma.raspSpawnHeight = spawnProps.readInt("Raspberry Spawn Height");
		mod_FloraSoma.raspSpawnRange = spawnProps.readInt("Raspberry Spawn Range");
		mod_FloraSoma.blueSpawnDensity = spawnProps.readInt("Blueberry Spawn Density");
		mod_FloraSoma.blueSpawnHeight = spawnProps.readInt("Blueberry Spawn Height");
		mod_FloraSoma.blueSpawnRange = spawnProps.readInt("Blueberry Spawn Range");
		mod_FloraSoma.blackSpawnDensity = spawnProps.readInt("Blackberry Spawn Density");
		mod_FloraSoma.blackSpawnHeight = spawnProps.readInt("Blackberry Spawn Height");
		mod_FloraSoma.blackSpawnRange = spawnProps.readInt("Blackberry Spawn Range");
		mod_FloraSoma.geoSpawnDensity = spawnProps.readInt("Geoberry Spawn Density");
		mod_FloraSoma.geoSpawnHeight = spawnProps.readInt("Geoberry Spawn Height");
		mod_FloraSoma.geoSpawnRange = spawnProps.readInt("Geoberry Spawn Range");
		
		mod_FloraSoma.redwoodSpawnDensity = spawnProps.readInt("Redwood Tree Spawn Density");
		mod_FloraSoma.redwoodSpawnHeight = spawnProps.readInt("Redwood Tree Spawn Height");
		mod_FloraSoma.redwoodSpawnRange = spawnProps.readInt("Redwood Tree Spawn Range");
		mod_FloraSoma.bloodSpawnDensity = spawnProps.readInt("Bloodwood Tree Spawn Density");
		mod_FloraSoma.bloodSpawnHeight = spawnProps.readInt("Bloodwood Tree Spawn Height");
		mod_FloraSoma.bloodSpawnRange = spawnProps.readInt("Bloodwood Tree Spawn Range");
		mod_FloraSoma.eucalyptusShortSpawnDensity = spawnProps.readInt("Small Eucalyptus Tree Spawn Density");
		mod_FloraSoma.eucalyptusShortSpawnHeight = spawnProps.readInt("Small Eucalyptus Tree Spawn Height");
		mod_FloraSoma.eucalyptusShortSpawnRange = spawnProps.readInt("Small Eucalyptus Tree Spawn Range");
		mod_FloraSoma.sakuraSpawnDensity = spawnProps.readInt("Sakura Tree Spawn Density");
		mod_FloraSoma.sakuraSpawnHeight = spawnProps.readInt("Sakura Tree Spawn Height");
		mod_FloraSoma.sakuraSpawnRange = spawnProps.readInt("Sakura Tree Spawn Range");
		mod_FloraSoma.whiteSpawnDensity = spawnProps.readInt("Ghost Tree Spawn Density");
		mod_FloraSoma.whiteSpawnHeight = spawnProps.readInt("Ghost Tree Spawn Height");
		mod_FloraSoma.whiteSpawnRange = spawnProps.readInt("Ghost Tree Spawn Range");
		
		mod_FloraSoma.cloudSpawnDensity = spawnProps.readInt("Cloud Spawn Density");
		mod_FloraSoma.cloudSpawnHeight = spawnProps.readInt("Cloud Spawn Height");
		mod_FloraSoma.cloudSpawnRange = spawnProps.readInt("Cloud Spawn Range");
		mod_FloraSoma.darkCloudSpawnDensity = spawnProps.readInt("Dark Cloud Spawn Density");
		mod_FloraSoma.darkCloudSpawnHeight = spawnProps.readInt("Dark Cloud Spawn Height");
		mod_FloraSoma.darkCloudSpawnRange = spawnProps.readInt("Dark Cloud Spawn Range");
		mod_FloraSoma.sulfurSpawnDensity = spawnProps.readInt("Sulfur Cloud Spawn Density");
		mod_FloraSoma.sulfurSpawnHeight = spawnProps.readInt("Sulfur Cloud Spawn Height");
		mod_FloraSoma.sulfurSpawnRange = spawnProps.readInt("Sulfur Cloud Spawn Range");
		mod_FloraSoma.ashSpawnDensity = spawnProps.readInt("Ash Cloud Spawn Density");
		mod_FloraSoma.ashSpawnHeight = spawnProps.readInt("Ash Cloud Spawn Height");
		mod_FloraSoma.ashSpawnRange = spawnProps.readInt("Ash Cloud Spawn Range");
		
		mod_FloraSoma.corruptorSpawnDensity = spawnProps.readInt("Corruptor Spawn Density");
		mod_FloraSoma.corruptorSpawnHeight = spawnProps.readInt("Corruptor Spawn Height");
		mod_FloraSoma.corruptorSpawnRange = spawnProps.readInt("Corruptor Spawn Range");
		mod_FloraSoma.corruptorSpawnSize = spawnProps.readInt("Corruptor Spawn Size");
		mod_FloraSoma.corruptionSpeed = spawnProps.readInt("Corruption Speed");
		
		mod_FloraSoma.saguaroSpawnDensity = spawnProps.readInt("Saguaro Cactus Spawn Density");
		mod_FloraSoma.saguaroSpawnHeight = spawnProps.readInt("Saguaro Cactus Spawn Height");
		mod_FloraSoma.saguaroSpawnRange = spawnProps.readInt("Saguaro Cactus Spawn Range");
		mod_FloraSoma.saguaroSpawnSize = spawnProps.readInt("Saguaro Cactus Spawn Size");
		
		return spawnProps;
	}

}
