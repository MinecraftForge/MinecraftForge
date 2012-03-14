package net.minecraft.src;

import net.minecraft.src.flora.*;
import net.minecraft.src.forge.*;
import net.minecraft.src.core.*;

import java.io.File;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class mod_FloraSoma extends BaseModMp
{
	public String getVersion()
	{
		return "0.3.4 Height";
	}

	public void load()
	{
	}
	
	public static void addEEsupport()
    {
        try
        {
            Class class1 = Class.forName("mod_EE");
            Class class2 = Class.forName("EEProxy");
            System.out.println("Equivalent Exchange integration for Flora & Soma activated");
            
            if(enableCrops){
	            EEProxy.setEMC(new ItemStack(barleySeed, 1, 0), 16);
	            EEProxy.setEMC(new ItemStack(food, 1, 0), 24);
	            EEProxy.setEMC(new ItemStack(food, 1, 1), 24);
            }
            
            if(enableBerryBush) {
	            EEProxy.setEMC(new ItemStack(berryBush, 1, 8), 16);
	            EEProxy.setEMC(new ItemStack(berryBush, 1, 9), 16);
	            EEProxy.setEMC(new ItemStack(berryBush, 1, 10), 16);
	            EEProxy.setEMC(new ItemStack(berryBush, 1, 11), 16);
            }
            
            if(enableTrees)
            {
	            int wood = 32;
	            EEProxy.setEMC(new ItemStack(redwood, 1, 0), wood);
	            EEProxy.setEMC(new ItemStack(redwood, 1, 1), wood);
	            EEProxy.setEMC(new ItemStack(redwood, 1, 2), wood / 4);
	            EEProxy.setEMC(new ItemStack(redwood, 1, 3), wood);
	            EEProxy.setEMC(new ItemStack(redwood, 1, 4), wood / 4);
	            EEProxy.setEMC(new ItemStack(redwood, 1, 5), wood);
	            EEProxy.setEMC(new ItemStack(redwood, 1, 6), wood / 4);
	            EEProxy.setEMC(new ItemStack(redwood, 1, 7), wood);
	            EEProxy.setEMC(new ItemStack(redwood, 1, 8), wood / 4);
	            EEProxy.setEMC(redwoodDoorItem.shiftedIndex, 48);
	            
	            EEProxy.setEMC(floraLeaves.blockID, 0, 1);
	            EEProxy.setEMC(floraLeaves.blockID, 1, 1);
	            EEProxy.setEMC(floraLeaves.blockID, 2, 1);
	            EEProxy.setEMC(cherryLeaves.blockID, 0, 1);
	            EEProxy.setEMC(cherryLeaves.blockID, 1, 1);
            }
            
            if(enableClouds) {
	            EEProxy.setEMC(new ItemStack(cloud, 1, 0), 8);
	            EEProxy.setEMC(new ItemStack(cloud, 1, 1), 256);
	            EEProxy.setEMC(new ItemStack(cloud, 1, 2), 48);
	            EEProxy.setEMC(new ItemStack(cloud, 1, 3), 8);
            }
            EEProxy.setEMC(saguaro.blockID, 8);
            
            if(enableCorruptor) {
	            int corr = 64;
	            int corrBrick = 512;
	            EEProxy.setEMC(new ItemStack(corruptor, 1, 0), corr);
	            EEProxy.setEMC(new ItemStack(corruptor, 1, 1), corr);
	            EEProxy.setEMC(new ItemStack(corruptor, 1, 2), corr);
	            EEProxy.setEMC(new ItemStack(corruptor, 1, 3), corr);
	            EEProxy.setEMC(new ItemStack(corruptor, 1, 4), corr);
	            EEProxy.setEMC(new ItemStack(corruptor, 1, 5), corr);
	            EEProxy.setEMC(new ItemStack(corruptor, 1, 6), corr);
	            EEProxy.setEMC(new ItemStack(corruptor, 1, 7), corr);
	            EEProxy.setEMC(new ItemStack(corruptor, 1, 8), corr);
	            EEProxy.setEMC(new ItemStack(corruptor, 1, 9), corr);
	            EEProxy.setEMC(new ItemStack(corruptor, 1, 10), corr);
	            EEProxy.setEMC(new ItemStack(corruptor, 1, 11), corr);
	            EEProxy.setEMC(new ItemStack(corruptor, 1, 12), corr);
	            EEProxy.setEMC(new ItemStack(corruptor, 1, 13), corr);
	            EEProxy.setEMC(new ItemStack(corruptor, 1, 14), corr);
	            EEProxy.setEMC(new ItemStack(corruptor, 1, 15), corr);
	            
	            EEProxy.setEMC(new ItemStack(corruptBrick, 1, 0), corrBrick);
	            EEProxy.setEMC(new ItemStack(corruptBrick, 1, 1), corrBrick);
	            EEProxy.setEMC(new ItemStack(corruptBrick, 1, 2), corrBrick);
	            EEProxy.setEMC(new ItemStack(corruptBrick, 1, 3), corrBrick);
	            EEProxy.setEMC(new ItemStack(corruptBrick, 1, 4), corrBrick);
	            EEProxy.setEMC(new ItemStack(corruptBrick, 1, 5), corrBrick);
	            EEProxy.setEMC(new ItemStack(corruptBrick, 1, 6), corrBrick);
	            EEProxy.setEMC(new ItemStack(corruptBrick, 1, 7), corrBrick);
	            EEProxy.setEMC(new ItemStack(corruptBrick, 1, 8), corrBrick);
	            EEProxy.setEMC(new ItemStack(corruptBrick, 1, 9), corrBrick);
	            EEProxy.setEMC(new ItemStack(corruptBrick, 1, 10), corrBrick);
	            EEProxy.setEMC(new ItemStack(corruptBrick, 1, 11), corrBrick);
	            EEProxy.setEMC(new ItemStack(corruptBrick, 1, 12), corrBrick);
	            EEProxy.setEMC(new ItemStack(corruptBrick, 1, 13), corrBrick);
	            EEProxy.setEMC(new ItemStack(corruptBrick, 1, 14), corrBrick);
	            EEProxy.setEMC(new ItemStack(corruptBrick, 1, 15), corrBrick);
            }

            System.out.println("EMC values set!");
        }
        catch (Throwable throwable)
        {
            System.out.println("Equivalent Exchange integration failed! Reason:");
            System.out.println(throwable);
        }
    }

	public void addRenderer(Map map)
	{
		map.put(net.minecraft.src.flora.WoodWallEntity.class, new WoodWallRender(new WoodWallModel(), 0.0F));
		map.put(net.minecraft.src.flora.RedwoodBoat.class, new RedwoodBoatRender());
		map.put(net.minecraft.src.flora.BloodBoat.class, new BloodBoatRender());
		map.put(net.minecraft.src.flora.WhiteWoodBoat.class, new WhiteBoatRender());
		map.put(net.minecraft.src.flora.EucalyptusBoat.class, new EucalyptusBoatRender());
	}

	public mod_FloraSoma()
	{
		mc = ModLoader.getMinecraftInstance();
		graphicsLevel = (mc.gameSettings.fancyGraphics);
		if(enableCrops)
			genCrops = new FloraCropGen(floraCrops.blockID);
		
		if(enableBerryBush) {
			raspgen = new BerryBushGen(0);
			bluegen = new BerryBushGen(1);
			blackgen = new BerryBushGen(2);
			geogen = new BerryBushGen(3);
		}
		
		if(enableTrees) {
			genRedwood = new RedwoodTreeGen(false);
			genBlood = new BloodTreeGen(3, 1);
			sakura = new CherryTreeGen(false, redwood.blockID, 0);
			whiteSakura = new WhiteTreeGen(false, redwood.blockID, 1);
			eucalyptusShort = new EucalyptusTreeGenShort(7, 2);
		}
		
		if(enableClouds) {
			smallcloud = new CloudGen(cloud.blockID, 0, 10, false);
			mediumcloud = new CloudGen(cloud.blockID, 0, 20, false);
			largecloud = new CloudGen(cloud.blockID, 0, 30, false);
			hugecloud = new CloudGen(cloud.blockID, 0, 40, false);
			smalldarkcloud = new CloudGen(cloud.blockID, 1, 10, false);
			mediumdarkcloud = new CloudGen(cloud.blockID, 1, 20, false);
			largedarkcloud = new CloudGen(cloud.blockID, 1, 30, false);
			hugedarkcloud = new CloudGen(cloud.blockID, 1, 40, false);
			tinyashcloud = new CloudGen(cloud.blockID, 2, 3, false);
			smallashcloud = new CloudGen(cloud.blockID, 2, 10, false);
			mediumashcloud = new CloudGen(cloud.blockID, 2, 18, false);
			largeashcloud = new CloudGen(cloud.blockID, 2, 27, false);
			hugeashcloud = new CloudGen(cloud.blockID, 2, 37, false);
			tinysulfurcloud = new CloudGen(cloud.blockID, 3, 3, false);
			smallsulfurcloud = new CloudGen(cloud.blockID, 3, 10, false);
			mediumsulfurcloud = new CloudGen(cloud.blockID, 3, 18, false);
			largesulfurcloud = new CloudGen(cloud.blockID, 3, 27, false);
			hugesulfurcloud = new CloudGen(cloud.blockID, 3, 37, false);
		}
		
		if(enableCorruptor){
			corruptred = new CorruptorGen(corruptorID, 0, corruptorSpawnSize);
			corruptblue = new CorruptorGen(corruptorID, 1, corruptorSpawnSize);
			corruptgreen = new CorruptorGen(corruptorID, 2, corruptorSpawnSize);
			corruptamber = new CorruptorGen(corruptorID, 3, corruptorSpawnSize);
			corruptmagenta = new CorruptorGen(corruptorID, 4, corruptorSpawnSize);
			corruptpurple = new CorruptorGen(corruptorID, 5, corruptorSpawnSize);
			corruptyellow = new CorruptorGen(corruptorID, 6, corruptorSpawnSize);
			corruptsilver = new CorruptorGen(corruptorID, 7, corruptorSpawnSize);
		}
		addRecipes();
		if(enableTrees)
			addWoodRecipes();
		addNames();
		berryModelID = ModLoader.getUniqueBlockModelID(this, true);
		saguaroModel = ModLoader.getUniqueBlockModelID(this, true);
		MinecraftForgeClient.preloadTexture("/floratex/plantblocks.png");
		MinecraftForgeClient.preloadTexture("/floratex/stone.png");
		if(enableTrees) {
			ModLoader.registerBlock(berryBush, net.minecraft.src.flora.BerryBushItem.class);
			ModLoader.registerBlock(floraLeaves);
			ModLoader.registerBlock(cherryLeaves);
			ModLoader.registerBlock(redwood, net.minecraft.src.flora.RedwoodItem.class);
			ModLoader.registerBlock(floraSapling, net.minecraft.src.flora.FloraSaplingItem.class);
			ModLoader.registerBlock(redwoodDoor);
		}
		if(enableCrops)
			ModLoader.registerBlock(floraCrops);
		if(enableClouds)
			ModLoader.registerBlock(cloud, net.minecraft.src.flora.CloudItem.class);
		if(enableCorruptor) {
			ModLoader.registerBlock(corruptor, net.minecraft.src.flora.CorruptorItem.class);
			ModLoader.registerBlock(corruptBrick, net.minecraft.src.flora.CorruptBrickItem.class);
		}
		ModLoader.registerBlock(saguaro);
		
		//ModLoader.registerEntityID(net.minecraft.src.flora.WoodWallEntity.class, "WoodWall", 70);
		ModLoader.registerEntityID(net.minecraft.src.flora.RedwoodBoat.class, "redwoodBoat", redwoodBoatID);
		ModLoader.registerEntityID(net.minecraft.src.flora.BloodBoat.class, "bloodBoat", bloodBoatID);
		ModLoader.registerEntityID(net.minecraft.src.flora.WhiteWoodBoat.class, "whiteBoat", whiteBoatID);
		ModLoader.registerEntityID(net.minecraft.src.flora.EucalyptusBoat.class, "eucalyptusBoat", eucalyptusBoatID);
		
		ModLoaderMp.registerNetClientHandlerEntity(net.minecraft.src.flora.RedwoodBoat.class, redwoodBoatID);
        ModLoaderMp.registerNetClientHandlerEntity(net.minecraft.src.flora.BloodBoat.class, bloodBoatID);
        ModLoaderMp.registerNetClientHandlerEntity(net.minecraft.src.flora.WhiteWoodBoat.class, whiteBoatID);
        ModLoaderMp.registerNetClientHandlerEntity(net.minecraft.src.flora.EucalyptusBoat.class, eucalyptusBoatID);
		
		addEEsupport();
		//MinecraftForge.registerBonemealHandler(new FloraSomaBonemealHandler());
		
		//ModLoader.SetInGameHook(this, true, true);
	}
	
	/*public void onTickInGame(float tick, Minecraft game)
	{
		((CherryLeaves) cherryLeaves).setGraphicsLevel(mc.gameSettings.fancyGraphics);
		((FloraLeaves) floraLeaves).setGraphicsLevel(mc.gameSettings.fancyGraphics);
		graphicsLevel = (mc.gameSettings.fancyGraphics);
	}*/
	
	public static boolean getFancy()
	{
		return graphicsLevel;
	}

	@Override
	public void generateSurface(World world, Random random, int xChunk, int zChunk)
	{
		int xCh = xChunk + random.nextInt(16) + 8;
		int yCh = random.nextInt(128);
		int zCh = zChunk + random.nextInt(16) + 8;
		
		BiomeGenBase biome = world.getWorldChunkManager().getBiomeGenAt(xChunk + 16, zChunk + 16);
		if(enableCrops && random.nextInt(5) < 2 && (biome == BiomeGenBase.plains 
				|| biome == BiomeGenBase.forest || biome == BiomeGenBase.forestHills || 
				biome == BiomeGenBase.extremeHills || biome == BiomeGenBase.extremeHillsEdge))
		{
			genCrops.generate(world, random, xCh, yCh + barleySpawnHeight - 64, zCh);
			genCrops.generate(world, random, xCh, yCh + barleySpawnHeight - 64, zCh);
		}
		
		
		if (enableClouds && biome != BiomeGenBase.desert && biome != BiomeGenBase.desertHills 
				&& random.nextInt(cloudSpawnDensity) == 0)
		{
			int xCo = random.nextInt(cloudSpawnRange) + cloudSpawnHeight;
			int size = random.nextInt(12);
			if (size < 5)
			{
				smallcloud.generate(world, random, xCh, xCo, zCh);
			}
			else if (size < 9)
			{
				mediumcloud.generate(world, random, xCh, xCo, zCh);
			}
			else if (size < 11)
			{
				largecloud.generate(world, random, xCh, xCo, zCh);
			}
			else
			{
				hugecloud.generate(world, random, xCh, xCo, zCh);
			}
		}
		if (enableTrees && random.nextInt(redwoodSpawnDensity) == 0)
		{
			int yRw = random.nextInt(redwoodSpawnRange) + redwoodSpawnHeight;
			xCh = xChunk + random.nextInt(16) + 8;
			zCh = zChunk + random.nextInt(16) + 8;
			genRedwood.generate(world, random, xCh, yRw, zCh);
		}
		if (biome == BiomeGenBase.forest)
		{
			if(enableBerryBush && random.nextInt(raspSpawnDensity) == 0)
			{
				int yBry = random.nextInt(raspSpawnRange) + raspSpawnHeight - 64;
				xCh = xChunk + random.nextInt(16) + 8;
				zCh = zChunk + random.nextInt(16) + 8;
				raspgen.generate(world, random, xCh, yBry, zCh);
			}
			if(enableTrees && random.nextInt((int)(sakuraSpawnDensity * 1.4)) == 0)
			{
				for (int iter = 0; iter < sakuraSpawnDensity; iter++)
				{
					int yDo = random.nextInt(sakuraSpawnRange) + sakuraSpawnHeight;
					xCh = xChunk + random.nextInt(16) + 8;
					zCh = zChunk + random.nextInt(16) + 8;
					sakura.generate(world, random, xCh, yDo, zCh);
				}
			}
			if (enableTrees && random.nextInt(eucalyptusShortSpawnDensity) == 0)
			{
				int yDo = random.nextInt(eucalyptusShortSpawnRange) + eucalyptusShortSpawnHeight;
				xCh = xChunk + random.nextInt(16) + 8;
				zCh = zChunk + random.nextInt(16) + 8;
				eucalyptusShort.generate(world, random, xCh, yDo, zCh);
			}
		}
		if (biome == BiomeGenBase.plains)
		{
			if (enableBerryBush && random.nextInt(blueSpawnDensity) == 0)
			{
				int yRp = random.nextInt(blueSpawnRange) + blueSpawnHeight - 64;
				xCh = xChunk + random.nextInt(16) + 8;
				zCh = zChunk + random.nextInt(16) + 8;
				bluegen.generate(world, random, xCh, yRp, zCh);
			}
			if(enableTrees && random.nextInt((int)redwoodSpawnDensity / 6) == 0)
			{
				genRedwood.generate(world, random, xCh, yCh, zCh);
			}
			/*for (int iter = 0; iter < (int)(sakuraSpawnDensity * 0.1); iter++)
			{
				int xDo = xChunk + random.nextInt(16) + 8;
				int yDo = random.nextInt(32) + 64;
				int zDo = zChunk + random.nextInt(16) + 8;
				sakura.generate(world, random, xDo, yDo, zDo);
			}*/
			if (enableTrees && random.nextInt((int)(eucalyptusShortSpawnDensity * 1.5)) == 0)
			{
				int xDo = xChunk + random.nextInt(16) + 8;
				int yDo = random.nextInt(32) + 64;
				int zDo = zChunk + random.nextInt(16) + 8;
				eucalyptusShort.generate(world, random, xDo, yDo, zDo);
			}
		}
		if (biome == BiomeGenBase.extremeHills)
		{
			if(enableBerryBush && random.nextInt(10) == 0) {
				int yBry = random.nextInt(raspSpawnRange) + raspSpawnHeight - 64;
				xCh = xChunk + random.nextInt(16) + 8;
				zCh = zChunk + random.nextInt(16) + 8;
				raspgen.generate(world, random, xCh, yBry, zCh);
			}
			if(enableTrees && random.nextInt(sakuraSpawnDensity) == 0) {
				for (int iter = 0; iter < sakuraSpawnDensity / 3; iter++)
				{
					int xDo = xChunk + random.nextInt(16) + 8;
					int yDo = random.nextInt(48) + 64;
					int zDo = zChunk + random.nextInt(16) + 8;
					sakura.generate(world, random, xDo, yDo, zDo);
				}
			}
			if (enableTrees && random.nextInt(eucalyptusShortSpawnDensity) < 10)
			{
				int xDo = xChunk + random.nextInt(16) + 8;
				int yDo = random.nextInt(eucalyptusShortSpawnRange) + eucalyptusShortSpawnHeight;
				int zDo = zChunk + random.nextInt(16) + 8;
				eucalyptusShort.generate(world, random, xDo, yDo, zDo);
			}
		}
		if (enableBerryBush && biome == BiomeGenBase.taiga && random.nextInt(geoSpawnDensity) == 0)
		{
			int yBry = random.nextInt(geoSpawnRange) + geoSpawnHeight - 64;
			xCh = xChunk + random.nextInt(16) + 8;
			zCh = zChunk + random.nextInt(16) + 8;
			geogen.generate(world, random, xCh, yCh, zCh);
		}
		if (enableBerryBush && biome == BiomeGenBase.swampland)
		{
			for (int iter = 0; iter < (int)(blackSpawnDensity / 5); iter++)
			{
				int yBry = random.nextInt(blackSpawnRange) + blackSpawnHeight - 64;
				xCh = xChunk + random.nextInt(16) + 8;
				zCh = zChunk + random.nextInt(16) + 8;
				blackgen.generate(world, random, xCh, yBry, zCh);
			}
		}
		if (biome == BiomeGenBase.river)
		{
			if(enableTrees) {
				for (int iter = 0; iter < sakuraSpawnDensity; iter++) {
					int xDo = xChunk + random.nextInt(16) + 8;
					int yDo = random.nextInt(sakuraSpawnRange) + sakuraSpawnHeight;
					int zDo = zChunk + random.nextInt(16) + 8;
					sakura.generate(world, random, xDo, yDo, zDo);
				}
			}
			if (enableBerryBush && random.nextInt(blackSpawnDensity * 2) == 0)
			{
				int yBry = random.nextInt(blackSpawnRange) + blackSpawnHeight - 64;
				xCh = xChunk + random.nextInt(16) + 8;
				zCh = zChunk + random.nextInt(16) + 8;
				blackgen.generate(world, random, xCh, yBry, zCh);
			}
		}
		
		
		
		//End Generation
		if (enableClouds && biome == BiomeGenBase.sky && random.nextInt(darkCloudSpawnDensity) < darkCloudSpawnDensity / 4)
		{
			for(int iter = 0; iter < darkCloudSpawnDensity / 4; iter++)
			{
				int height = random.nextInt(darkCloudSpawnRange);
				if (random.nextInt(5) == 0)
				{
					smalldarkcloud.generate(world, random, xCh, height + darkCloudSpawnHeight - 64, zCh);
				}
				else if (random.nextInt(4) == 0)
				{
					mediumcloud.generate(world, random, xCh, height + darkCloudSpawnHeight - 64, zCh);
				}
				else if (random.nextInt(3) == 0)
				{
					largedarkcloud.generate(world, random, xCh, height + darkCloudSpawnHeight - 64, zCh);
				}
				if (random.nextInt(2) == 0)
				{
					hugedarkcloud.generate(world, random, xCh, height + darkCloudSpawnHeight - 64, zCh);
				}
			}
		}
	}

	public void GenerateNether(World world, Random random, int i, int j)
	{
		int xCor = i + random.nextInt(16) + 8;
		int yCor = random.nextInt(bloodSpawnRange) + bloodSpawnHeight;
		int zCor = j + random.nextInt(16) + 8;
		if (enableTrees && random.nextInt(bloodSpawnDensity) == 0)
			genBlood.generate(world, random, xCor, yCor, zCor);
		if(enableClouds) {
			if (random.nextInt(ashSpawnDensity) < 5)
			{
				int j1 = i + random.nextInt(16) + 8;
				int i4 = random.nextInt(ashSpawnRange) + ashSpawnHeight;
				int k6 = j + random.nextInt(16) + 8;
				tinyashcloud.generate(world, random, j1, i4, k6);
			}
			if (random.nextInt(ashSpawnDensity) < 4)
			{
				int k1 = i + random.nextInt(16) + 8;
				int j4 = random.nextInt(ashSpawnRange) + ashSpawnHeight;
				int l6 = j + random.nextInt(16) + 8;
				smallashcloud.generate(world, random, k1, j4, l6);
			}
			else if (random.nextInt(ashSpawnDensity) < 3)
			{
				int l1 = i + random.nextInt(16) + 8;
				int k4 = random.nextInt(ashSpawnRange) + ashSpawnHeight;
				int i7 = j + random.nextInt(16) + 8;
				mediumashcloud.generate(world, random, l1, k4, i7);
			}
			else if (random.nextInt(ashSpawnDensity) < 2)
			{
				int i2 = i + random.nextInt(16) + 8;
				int l4 = random.nextInt(ashSpawnRange) + ashSpawnHeight;
				int j7 = j + random.nextInt(16) + 8;
				largeashcloud.generate(world, random, i2, l4, j7);
			}
			else if (random.nextInt(ashSpawnDensity) < 1)
			{
				int j2 = i + random.nextInt(16) + 8;
				int i5 = random.nextInt(ashSpawnRange) + ashSpawnHeight;
				int k7 = j + random.nextInt(16) + 8;
				hugeashcloud.generate(world, random, j2, i5, k7);
			}
			if (random.nextInt(sulfurSpawnDensity) < 5)
			{
				int k2 = i + random.nextInt(16) + 8;
				int j5 = random.nextInt(sulfurSpawnRange) + sulfurSpawnHeight;
				int l7 = j + random.nextInt(16) + 8;
				tinysulfurcloud.generate(world, random, k2, j5, l7);
			}
			if (random.nextInt(sulfurSpawnDensity) < 4)
			{
				int l2 = i + random.nextInt(16) + 8;
				int k5 = random.nextInt(sulfurSpawnRange) + sulfurSpawnHeight;
				int i8 = j + random.nextInt(16) + 8;
				smallsulfurcloud.generate(world, random, l2, k5, i8);
			}
			else if (random.nextInt(sulfurSpawnDensity) < 3)
			{
				int i3 = i + random.nextInt(16) + 8;
				int l5 = random.nextInt(sulfurSpawnRange) + sulfurSpawnHeight;
				int j8 = j + random.nextInt(16) + 8;
				mediumsulfurcloud.generate(world, random, i3, l5, j8);
			}
			else if (random.nextInt(sulfurSpawnDensity) < 2)
			{
				int j3 = i + random.nextInt(16) + 8;
				int i6 = random.nextInt(sulfurSpawnRange) + sulfurSpawnHeight;
				int k8 = j + random.nextInt(16) + 8;
				largesulfurcloud.generate(world, random, j3, i6, k8);
			}
			else if (random.nextInt(sulfurSpawnDensity) < 1)
			{
				int k3 = i + random.nextInt(16) + 8;
				int j6 = random.nextInt(sulfurSpawnRange) + sulfurSpawnHeight;
				int l8 = j + random.nextInt(16) + 8;
				hugesulfurcloud.generate(world, random, k3, j6, l8);
			}
		}
		
		if(enableCorruptor) {
			xCor = i + random.nextInt(16) + 8;
			yCor = random.nextInt(corruptorSpawnRange) + corruptorSpawnHeight;
			zCor = j + random.nextInt(16) + 8;
			int color = random.nextInt(8);
			for(int iter = 0; iter < corruptorSpawnDensity; iter++)
			{
				switch (color)
				{
					case 0:	corruptred.generate(world, random, xCor, yCor, zCor);		break;
					case 1:	corruptblue.generate(world, random, xCor, yCor, zCor);		break;
					case 2:	corruptgreen.generate(world, random, xCor, yCor, zCor);		break;
					case 3:	corruptamber.generate(world, random, xCor, yCor, zCor);		break;
					case 4:	corruptmagenta.generate(world, random, xCor, yCor, zCor);	break;
					case 5:	corruptpurple.generate(world, random, xCor, yCor, zCor);	break;
					case 6:	corruptyellow.generate(world, random, xCor, yCor, zCor);	break;
					case 7:	corruptsilver.generate(world, random, xCor, yCor, zCor);	break;
				}
			}
		}
		
		if(enableTrees) {
			for (int iter = 0; iter < whiteSpawnDensity; iter++)
			{
				int xDo = i + random.nextInt(16) + 8;
				int yDo = random.nextInt(whiteSpawnRange) + whiteSpawnHeight;
				int zDo = j + random.nextInt(16) + 8;
				whiteSakura.generate(world, random, xDo, yDo, zDo);
			}
		}
	}

	public void addRecipes()
	{
		ModLoader.addRecipe(new ItemStack(seedBag, 1, 0), new Object[]
		{
			"sss", "sss", "sss", 's', Item.seeds
		});
		
		if(enableCrops) {
			ModLoader.addRecipe(new ItemStack(Item.bread), new Object[]
			{
				"bbb", Character.valueOf('b'), new ItemStack(food, 1, 0)
			});
			ModLoader.addRecipe(new ItemStack(food, 1, 1), new Object[]
			{
				"b", Character.valueOf('b'), new ItemStack(food, 1, 0)
			});
			FurnaceRecipes.smelting().addSmelting(food.shiftedIndex, 1, new ItemStack(Item.bread, 1));
		}
		
		if(enableCorruptor) {
			for (int i = 0; i <= 7; i++)
			{
				ModLoader.addRecipe(new ItemStack(corruptor, 1, i), new Object[]
				{
					"c", Character.valueOf('c'), new ItemStack(corruptBrick, 1, i)
				});
			}
	
			for (int j = 0; j <= 7; j++)
			{
				ModLoader.addRecipe(new ItemStack(corruptBrick, 4, j + 8), new Object[]
				{
					"cc", "cc", Character.valueOf('c'), new ItemStack(corruptBrick, 1, j)
				});
			}
		}

		if(enableClouds) {
			ModLoader.addRecipe(new ItemStack(Item.coal, 1, 1), new Object[]
			{
				"cc", "cc", Character.valueOf('c'), new ItemStack(cloud, 1, 2)
			});
			ModLoader.addRecipe(new ItemStack(Item.gunpowder, 1, 0), new Object[]
			{
				"cc", "cc", Character.valueOf('c'), new ItemStack(cloud, 1, 3)
			});
		}
		
		ModLoader.addName(waterDrop, "Drop of Water");
		ModLoader.addRecipe(new ItemStack(waterDrop, 1), new Object[]
				{
					"X", 'X', Block.cactus
				});
		
		ModLoader.addName(wheatFlour, "Flour");
		ModLoader.addRecipe(new ItemStack(wheatFlour, 1), new Object[]
				{
					"X", 'X', Item.wheat
				});
		ModLoader.addName(wheatDough, "Dough");
		ModLoader.addShapelessRecipe(new ItemStack(wheatDough, 1), new Object[]
				{
					wheatFlour, Item.bucketWater
				});
		ModLoader.addShapelessRecipe(new ItemStack(wheatDough, 2), new Object[]
				{
					wheatFlour, Item.egg
				});
		ModLoader.addSmelting(wheatDough.shiftedIndex, new ItemStack(Item.bread, 1));
		ModLoader.addSmelting(wheatFlour.shiftedIndex, new ItemStack(Item.bread, 1));
		ModLoader.addRecipe(new ItemStack(Item.cake, 1), new Object[]
				{
					"AAA", "BEB", " C ", Character.valueOf('A'), Item.bucketMilk, Character.valueOf('B'), Item.sugar, Character.valueOf('C'), wheatFlour, Character.valueOf('E'),
					Item.egg
				});
		ModLoader.addRecipe(new ItemStack(Item.bucketWater, 1), new Object[]
				{
					"www", "wBw", "www", 'w', waterDrop, Character.valueOf('B'), Item.bucketEmpty
				});
		
	}
	
	public void addWoodRecipes()
	{
		int[] woodmeta = { 2, 4, 6, 8 };
		int[] shortwoodmeta = { 2, 6, 8 };
		for(int iter = 0; iter < woodmeta.length; iter++)
		{
			ModLoader.addRecipe(new ItemStack(redwood, 4, woodmeta[iter]), new Object[]
			{	
				"w", 'w', new ItemStack(redwood, 1, woodmeta[iter] - 1)
			});
			ModLoader.addRecipe(new ItemStack(Block.workbench), new Object[]
			{
				"ww", "ww", 'w', new ItemStack(redwood, 1, woodmeta[iter])
			});
			ModLoader.addRecipe(new ItemStack(Block.fenceGate, 1), new Object[]
			{
			   "#W#", "#W#", '#', Item.stick, Character.valueOf('W'), new ItemStack(redwood, 1, woodmeta[iter])
			});
			ModLoader.addRecipe(new ItemStack(Block.jukebox, 1), new Object[]
			{
				"###", "#X#", "###", '#', new ItemStack(redwood, 1, woodmeta[iter]), 'X', Item.diamond
			});
			ModLoader.addRecipe(new ItemStack(Block.music, 1), new Object[]
			{
				"###", "#X#", "###", '#', new ItemStack(redwood, 1, woodmeta[iter]), 'X', Item.redstone
			});
			ModLoader.addRecipe(new ItemStack(Block.stairSingle, 3, 2), new Object[]
			{
				"###", '#', new ItemStack(redwood, 1, woodmeta[iter])
			});
			/*ModLoader.addRecipe(new ItemStack(Item.doorWood, 1), new Object[]
			{
				"##", "##", "##", '#', new ItemStack(redwood, 1, woodmeta[iter])
			});*/
			ModLoader.addRecipe(new ItemStack(Block.trapdoor, 2), new Object[]
			{
				"###", "###", '#', new ItemStack(redwood, 1, woodmeta[iter])
			});
			ModLoader.addRecipe(new ItemStack(Item.sign, 1), new Object[]
			{
				"###", "###", " X ", '#', new ItemStack(redwood, 1, woodmeta[iter]), 'X', Item.stick
			});
			ModLoader.addRecipe(new ItemStack(Item.bowlEmpty, 4), new Object[]
			{
				"# #", " # ", '#', new ItemStack(redwood, 1, woodmeta[iter])
			});
			ModLoader.addRecipe(new ItemStack(Block.stairCompactPlanks, 4), new Object[]
			{
				"#  ", "## ", "###", '#', new ItemStack(redwood, 1, woodmeta[iter])
			});
			ModLoader.addRecipe(new ItemStack(Block.chest), new Object[]
			{
				"###", "# #", "###", '#', new ItemStack(redwood, 1, woodmeta[iter])
			});
			ModLoader.addRecipe(new ItemStack(Block.pressurePlatePlanks, 1), new Object[]
			{
				"##", '#', new ItemStack(redwood, 1, woodmeta[iter])
			});
			ModLoader.addRecipe(new ItemStack(Block.pistonBase, 1), new Object[]
			{	
				"TTT", "#X#", "#R#", '#', Block.cobblestone, 'X', Item.ingotIron, 'R', Item.redstone, 'T',
				new ItemStack(redwood, 1, woodmeta[iter])
			});
			ModLoader.addRecipe(new ItemStack(Item.bed, 1), new Object[]
			{
				"###", "XXX", '#', Block.cloth, 'X', new ItemStack(redwood, 1, woodmeta[iter])
			});
			ModLoader.addRecipe(new ItemStack(floraBoat, 1, iter), new Object[]
			{
				"# #", "###", '#', new ItemStack(redwood, 1, woodmeta[iter])
			});
		}
		for(int iter = 0; iter < shortwoodmeta.length; iter++)
		{
			ModLoader.addRecipe(new ItemStack(Item.stick, 4, 0), new Object[]
			{
				"w", "w", 'w', new ItemStack(redwood, 8, shortwoodmeta[iter])
			});
			ModLoader.addRecipe(new ItemStack(Item.pickaxeWood), new Object[]
			{
				"www", " | ", " | ", 'w', new ItemStack(redwood, 1, woodmeta[iter]), '|', Item.stick
			});
			ModLoader.addRecipe(new ItemStack(Item.shovelWood), new Object[]
			{
				"w", "|", "|", 'w', new ItemStack(redwood, 1, woodmeta[iter]), '|', Item.stick
			});
			ModLoader.addRecipe(new ItemStack(Item.axeWood), new Object[]
			{
				"ww ", "w| ", " | ", 'w', new ItemStack(redwood, 1, woodmeta[iter]), '|', Item.stick
			});
			ModLoader.addRecipe(new ItemStack(Item.swordWood), new Object[]
			{
				"w", "w", "|", 'w', new ItemStack(redwood, 1, woodmeta[iter]), '|', Item.stick
			});
			ModLoader.addRecipe(new ItemStack(Item.hoeWood), new Object[]
			{
				"ww", "| ", "| ", 'w', new ItemStack(redwood, 1, woodmeta[iter]), '|', Item.stick
			});
		}
		ModLoader.addRecipe(new ItemStack(redwoodDoorItem, 1), new Object[]
		{
			"##", "##", "##", '#', new ItemStack(redwood, 1, woodmeta[0])
		});
		ModLoader.addRecipe(new ItemStack(Item.doorWood, 1), new Object[]
		{
			"##", "##", "##", '#', new ItemStack(redwood, 1, woodmeta[1])
		});
		ModLoader.addRecipe(new ItemStack(Item.doorWood, 1), new Object[]
		{
			"##", "##", "##", '#', new ItemStack(redwood, 1, woodmeta[2])
		});
		ModLoader.addRecipe(new ItemStack(Item.doorWood, 1), new Object[]
		{
			"##", "##", "##", '#', new ItemStack(redwood, 1, woodmeta[3])
		});
		
		FurnaceRecipes.smelting().addSmelting(redwood.blockID, 0, new ItemStack(Item.coal, 1, 1));
		FurnaceRecipes.smelting().addSmelting(redwood.blockID, 1, new ItemStack(Item.coal, 1, 1));
		FurnaceRecipes.smelting().addSmelting(redwood.blockID, 5, new ItemStack(Item.coal, 1, 1));
		FurnaceRecipes.smelting().addSmelting(redwood.blockID, 7, new ItemStack(Item.coal, 1, 1));
	}
	
	

	public void addNames()
	{
		ModLoader.addName(seedBag, "Seed Bag");
		if(enableCrops)
			ModLoader.addName(barleySeed, "Barley Seeds");
		if(enableTrees) {
			ModLoader.addName(floraSapling, "Sapling");
			ModLoader.addName(floraLeaves, "Leaves");
			ModLoader.addName(cherryLeaves, "Blossoms");
			ModLoader.addName(redwoodDoorItem, "Redwood Door");
		}
		ModLoader.addName(saguaro, "Saguaro Cactus");
		
		ModLoader.addLocalization("barkRedwood.name", "Redwood Bark");
		ModLoader.addLocalization("heartRedwood.name", "Redwood");
		ModLoader.addLocalization("planksRedwood.name", "Redwood Planks");
		ModLoader.addLocalization("bloodbarkRedwood.name", "Bloodbark");
		ModLoader.addLocalization("bloodplanksRedwood.name", "Bloodplanks");
		ModLoader.addLocalization("sakurabarkRedwood.name", "Wood");
		ModLoader.addLocalization("sakuraplanksRedwood.name", "White Planks");
		ModLoader.addLocalization("eucalyptusbarkRedwood.name", "Eucalyptus Wood");
		ModLoader.addLocalization("eucalyptusplanksRedwood.name", "Eucalyptus Planks");
		
		ModLoader.addLocalization("redwoodBoat.name", "Redwood Boat");
		ModLoader.addLocalization("bloodBoat.name", "Bloodwood Boat");
		ModLoader.addLocalization("whiteBoat.name", "White Boat");
		ModLoader.addLocalization("eucalyptusBoat.name", "Eucalyptus Boat");
		
		ModLoader.addLocalization("raspberry.name", "Raspberry");
		ModLoader.addLocalization("blueberry.name", "Blueberry");
		ModLoader.addLocalization("blackberry.name", "Blackberry");
		ModLoader.addLocalization("geoberry.name", "Geoberry");
		ModLoader.addLocalization("raspberryBush.name", "Raspberry Bush");
		ModLoader.addLocalization("blueberryBush.name", "Blueberry Bush");
		ModLoader.addLocalization("blackberryBush.name", "Blackberry Bush");
		ModLoader.addLocalization("geoberryBush.name", "Geoberry Bush");
		ModLoader.addLocalization("barleyFlora.name", "Barley");
		ModLoader.addLocalization("barleyflourFlora.name", "Barley Flour");
		ModLoader.addLocalization("normalcloud.name", "Cloud");
		ModLoader.addLocalization("darkcloud.name", "Dark Cloud");
		ModLoader.addLocalization("ashcloud.name", "Ash Cloud");
		ModLoader.addLocalization("sulfurcloud.name", "Sulfur Cloud");
		ModLoader.addLocalization("bloodcorruptor.name", "Blood Corruptor");
		ModLoader.addLocalization("azurecorruptor.name", "Azure Corruptor");
		ModLoader.addLocalization("envycorruptor.name", "Envious Corruptor");
		ModLoader.addLocalization("ambercorruptor.name", "Amber Corruptor");
		ModLoader.addLocalization("heartcorruptor.name", "Heart Corruptor");
		ModLoader.addLocalization("darkcorruptor.name", "Dark Corruptor");
		ModLoader.addLocalization("brightcorruptor.name", "Bright Corruptor");
		ModLoader.addLocalization("purecorruptor.name", "Pure Corruptor");
		ModLoader.addLocalization("bloodgrasscorruptor.name", "Blood Corruption Grass");
		ModLoader.addLocalization("azuregrasscorruptor.name", "Azure Corruption Grass");
		ModLoader.addLocalization("envygrasscorruptor.name", "Envious Corruption Grass");
		ModLoader.addLocalization("ambergrasscorruptor.name", "Amber Corruption Grass");
		ModLoader.addLocalization("heartgrasscorruptor.name", "Heart Corruption Grass");
		ModLoader.addLocalization("darkgrasscorruptor.name", "Dark Corruption Grass");
		ModLoader.addLocalization("brightgrasscorruptor.name", "Bright Corruption Grass");
		ModLoader.addLocalization("puregrasscorruptor.name", "Pure Corruption Grass");
		ModLoader.addLocalization("bloodcorrupted.name", "Bloodstone");
		ModLoader.addLocalization("azurecorrupted.name", "Azure Stone");
		ModLoader.addLocalization("envycorrupted.name", "Envious Stone");
		ModLoader.addLocalization("ambercorrupted.name", "Amber Stone");
		ModLoader.addLocalization("heartcorrupted.name", "Heartstone");
		ModLoader.addLocalization("darkcorrupted.name", "Darkstone");
		ModLoader.addLocalization("brightcorrupted.name", "Brightstone");
		ModLoader.addLocalization("purecorrupted.name", "Purified Stone");
		ModLoader.addLocalization("bloodbrickcorrupted.name", "Blood Brick");
		ModLoader.addLocalization("azurebrickcorrupted.name", "Azure Brick");
		ModLoader.addLocalization("envybrickcorrupted.name", "Envious Brick");
		ModLoader.addLocalization("amberbrickcorrupted.name", "Amber Brick");
		ModLoader.addLocalization("heartbrickcorrupted.name", "Heart Brick");
		ModLoader.addLocalization("darkbrickcorrupted.name", "Dark Brick");
		ModLoader.addLocalization("brightbrickcorrupted.name", "Bright Brick");
		ModLoader.addLocalization("purebrickcorrupted.name", "Purified Brick");
	}

	public boolean renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iblockaccess, int x, int y, int z, Block block, int modelID)
	{
		
		((CherryLeaves) cherryLeaves).setGraphicsLevel(mc.gameSettings.fancyGraphics);
		((FloraLeaves) floraLeaves).setGraphicsLevel(mc.gameSettings.fancyGraphics);
		if (modelID == berryModelID)
		{
			((BerryBush) berryBush).setGraphicsLevel(mc.gameSettings.fancyGraphics);
			return RenderBerryInWorld(renderblocks, iblockaccess, x, y, z, block);
		} else
		
		if (modelID == saguaroModel){
			return RenderSaguaroWorld(renderblocks, iblockaccess, x, y, z, (SaguaroBlock)block);
		} else

		{	  	
			return false;
		}
	}

	private boolean RenderBerryInWorld(RenderBlocks renderblocks, IBlockAccess iblockaccess, int i, int j, int k, Block block)
	{
		int md = iblockaccess.getBlockMetadata(i, j, k);
		if (md < 4)
		{
			block.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
			renderblocks.renderStandardBlock(block, i, j, k);
		}
		else if (md < 8)
		{
			block.setBlockBounds(0.125F, 0.0F, 0.125F, 0.875F, 0.75F, 0.875F);
			renderblocks.renderStandardBlock(block, i, j, k);
		}
		else
		{
			block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			renderblocks.renderStandardBlock(block, i, j, k);
		}
		return false;
	}
	
	private boolean RenderSaguaroWorld(RenderBlocks renderblocks, 
			IBlockAccess iblockaccess, int x, int y, int z, SaguaroBlock cactus)
	{
		float offset = 0.125F;
		
		float bX = offset;
		float bY = 0.0F;
		float bZ = offset;
		float tX = 1.0F - offset;
		float tY = 1.0F - offset;
		float tZ = 1.0F - offset;
		
		int airBelow = iblockaccess.getBlockId(x, y-1, z);
		int cactusAbove = iblockaccess.getBlockId(x, y+1, z);	
		
		if(airBelow == 0)
			bY = offset;
		if(cactusAbove == saguaro.blockID)
			tY = 1.0F;
		
		cactus.setBlockBounds(bX, bY, bZ, tX, tY, tZ);
		renderblocks.renderStandardBlock(cactus, x, y, z);
		
		bY = offset;
		tY = 1.0F - offset;
		
		if(cactus.canConnectSuguaroTo(iblockaccess, x + 1, y, z) && 
				(airBelow == 0 || iblockaccess.getBlockId(x+1, y-1, z) == 0) ) {
			bX = 1F - offset;
			tX = 1F;
			cactus.setBlockBounds(bX, bY, bZ, tX, tY, tZ);
			renderblocks.renderStandardBlock(cactus, x, y, z);
		}
		
		if(cactus.canConnectSuguaroTo(iblockaccess, x - 1, y, z) && 
				(airBelow == 0 || iblockaccess.getBlockId(x-1, y-1, z) == 0)) {
			bX = 0F;
			tX = offset;
			cactus.setBlockBounds(bX, bY, bZ, tX, tY, tZ);
			renderblocks.renderStandardBlock(cactus, x, y, z);
		}
		
		bX = offset;
		tX = 1.0F - offset;
		
		if(cactus.canConnectSuguaroTo(iblockaccess, x, y, z + 1) && 
				(airBelow == 0 || iblockaccess.getBlockId(x, y-1, z+1) == 0)) {
			bZ = 1F - offset;
			tZ = 1F;
			cactus.setBlockBounds(bX, bY, bZ, tX, tY, tZ);
			renderblocks.renderStandardBlock(cactus, x, y, z);
		}
		
		if(cactus.canConnectSuguaroTo(iblockaccess, x, y, z - 1) && 
				(airBelow == 0 || iblockaccess.getBlockId(x, y-1, z-1) == 0)) {
			bZ = 0F;
			tZ = offset;
			cactus.setBlockBounds(bX, bY, bZ, tX, tY, tZ);
			renderblocks.renderStandardBlock(cactus, x, y, z);
		}
		return false;
	}

	public void renderInvBlock(RenderBlocks renderblocks, Block block, int i, int j)
	{
		if (j == berryModelID)
		{
			RenderBerryInInv(renderblocks, block, i);
		}
		
		if (j == saguaroModel)
		{
			RenderSaguaroInv(renderblocks, block, i);
		}
	}

	private void RenderBerryInInv(RenderBlocks renderblocks, Block block, int i)
	{
		Tessellator tessellator = Tessellator.instance;
		if (i < 4)
		{
			block.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
			RenderDo(renderblocks, block, i);
		}
		else if (i < 8)
		{
			block.setBlockBounds(0.125F, 0.0F, 0.125F, 0.875F, 0.75F, 0.875F);
			RenderDo(renderblocks, block, i);
		}
		else
		{
			block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			RenderDo(renderblocks, block, i);
		}
		block.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
	}
	
	private void RenderSaguaroInv(RenderBlocks renderblocks, Block block, int i)
	{
		float offset = 0.125F;
		
		float bX = offset;
		float bY = 0.0F;
		float bZ = offset;
		float tX = 1.0F - offset;
		float tY = 1.0F - offset;
		float tZ = 1.0F - offset;
		
		block.setBlockBounds(bX, bY, bZ, tX, tY, tZ);
		RenderDo(renderblocks, block, i);
	}

	private void RenderDo(RenderBlocks renderblocks, Block block, int i)
	{
		Tessellator tessellator = Tessellator.instance;
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1F, 0.0F);
		renderblocks.renderBottomFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(0, i));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderblocks.renderTopFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(1, i));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1F);
		renderblocks.renderEastFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(2, i));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderblocks.renderWestFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(3, i));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1F, 0.0F, 0.0F);
		renderblocks.renderNorthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(4, i));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderblocks.renderSouthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(5, i));
		tessellator.draw();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}
	
	public static Item seedBag;
	public static Item berry;
	public static Item barleySeed;
	public static Item food;

	public static Item waterDrop;
	public static Item wheatFlour;
	public static Item wheatDough;
	public static Item floraBoat;
	public static Item redwoodDoorItem;
	
	public static Block berryBush;
	public static Block redwood;
	public static Block floraLeaves;
	public static Block floraCrops;
	public static Block cloud;
	public static Block corruptor;
	public static Block corruptBrick;
	public static Block floraSapling;
	public static Block cherryLeaves;
	public static Block saguaro;
	
	public static Block redwoodDoor;
	
	public static int seedBagID;
	public static int berryID;
	public static int barleySeedID;
	public static int redwoodID;
	public static int floraLeavesID;
	public static int floraCropsID;
	public static int foodID;
	public static int ingredientsID;
	public static int pumpkinPieID;
	
	public static int cloudID;
	public static int corruptorID;
	public static int corruptBrickID;
	public static int floraSaplingID;
	public static int berryBlockID;
	public static int cherryLeavesID;
	public static int redwoodDoorID;
	public static int redwoodDoorItemID;
	public static int saguaroID;
	
	public static boolean resolveConflicts;
	
	public static int berryModelID;
	public static int saguaroModel;
	public static int boatItemID;
	public static int redwoodBoatID;
	public static int bloodBoatID;
	public static int whiteBoatID;
	public static int eucalyptusBoatID;
	
	FloraCropGen genCrops;
	BerryBushGen raspgen;
	BerryBushGen bluegen;
	BerryBushGen blackgen;
	BerryBushGen geogen;
	
	RedwoodTreeGen genRedwood;
	BloodTreeGen genBlood;
	CherryTreeGen sakura;
	WhiteTreeGen whiteSakura;
	EucalyptusTreeGenShort eucalyptusShort;
	
	CloudGen smallcloud;
	CloudGen mediumcloud;
	CloudGen largecloud;
	CloudGen hugecloud;
	CloudGen smalldarkcloud;
	CloudGen mediumdarkcloud;
	CloudGen largedarkcloud;
	CloudGen hugedarkcloud;
	CloudGen tinyashcloud;
	CloudGen smallashcloud;
	CloudGen mediumashcloud;
	CloudGen largeashcloud;
	CloudGen hugeashcloud;
	CloudGen tinysulfurcloud;
	CloudGen smallsulfurcloud;
	CloudGen mediumsulfurcloud;
	CloudGen largesulfurcloud;
	CloudGen hugesulfurcloud;
	
	CorruptorGen corruptred;
	CorruptorGen corruptblue;
	CorruptorGen corruptgreen;
	CorruptorGen corruptamber;
	CorruptorGen corruptmagenta;
	CorruptorGen corruptpurple;
	CorruptorGen corruptyellow;
	CorruptorGen corruptsilver;
	
	public static boolean enableCrops;
	public static int barleySpawnDensity;
	public static int barleySpawnHeight;
	
	public static boolean enableBerryBush;
	public static int raspSpawnDensity;
	public static int raspSpawnHeight;
	public static int raspSpawnRange;
	public static int blueSpawnDensity;
	public static int blueSpawnHeight;
	public static int blueSpawnRange;
	public static int blackSpawnDensity;
	public static int blackSpawnHeight;
	public static int blackSpawnRange;
	public static int geoSpawnDensity;
	public static int geoSpawnHeight;
	public static int geoSpawnRange;
	
	public static boolean enableTrees;
	public static int redwoodSpawnDensity;
	public static int redwoodSpawnHeight;
	public static int redwoodSpawnRange;
	public static int bloodSpawnDensity;
	public static int bloodSpawnHeight;
	public static int bloodSpawnRange;
	public static int eucalyptusShortSpawnDensity;
	public static int eucalyptusShortSpawnHeight;
	public static int eucalyptusShortSpawnRange;
	public static int sakuraSpawnDensity;
	public static int sakuraSpawnHeight;
	public static int sakuraSpawnRange;
	public static int whiteSpawnDensity;
	public static int whiteSpawnHeight;
	public static int whiteSpawnRange;
	
	public static boolean enableClouds;
	public static int cloudSpawnDensity;
	public static int cloudSpawnHeight;
	public static int cloudSpawnRange;
	public static int darkCloudSpawnDensity;
	public static int darkCloudSpawnHeight;
	public static int darkCloudSpawnRange;
	public static int sulfurSpawnDensity;
	public static int sulfurSpawnHeight;
	public static int sulfurSpawnRange;
	public static int ashSpawnDensity;
	public static int ashSpawnHeight;
	public static int ashSpawnRange;
	
	public static boolean enableCorruptor;
	public static int corruptorSpawnDensity;
	public static int corruptorSpawnHeight;
	public static int corruptorSpawnRange;
	public static int corruptorSpawnSize;
	public static int corruptionSpeed;
	
	public static boolean enableSaguaro;
	public static int saguaroSpawnDensity;
	public static int saguaroSpawnHeight;
	public static int saguaroSpawnRange;
	public static int saguaroSpawnSize;
	
	Minecraft mc;
	public static boolean graphicsLevel;
	
	public static InfiProps props;
	public static InfiProps spawnProps;

	static
	{
		File me = new File( (new StringBuilder().append(Minecraft.getMinecraftDir().getPath())
				.append('/').append("mDiyo").toString() ) );
        me.mkdir();
		props = new InfiProps((new File((new StringBuilder()).append(Minecraft.getMinecraftDir().getPath())
				.append('/').append("mDiyo").append('/').append("FloraSomaIDs.cfg").toString())).getPath());
		props = PropsHelperFloraSoma.InitIDs(props);
		PropsHelperFloraSoma.getIDs(props);
		spawnProps = new InfiProps((new File((new StringBuilder()).append(Minecraft.getMinecraftDir().getPath())
				.append('/').append("mDiyo").append('/').append("FloraSomaSpawn.cfg").toString())).getPath());
		spawnProps = PropsHelperFloraSoma.InitSpawnRate(spawnProps);
		PropsHelperFloraSoma.getSpawnRate(spawnProps);
		
		if(resolveConflicts)
			PropsHelperFloraSoma.resolveIDs(props);
		
		seedBag = (new SeedBag(seedBagID)).setItemName("seedBag");
		
		if(enableBerryBush) {
			berry = (new BerryItem(berryID, 2)).setItemName("berry");
			berryBush = (new BerryBush(berryBlockID, 0)).setHardness(0.3F).setStepSound(Block.soundGrassFootstep).setBlockName("berries");
		}
		
		if(enableClouds)
			cloud = (new CloudBlock(cloudID)).setHardness(0.3F).setStepSound(Block.soundClothFootstep).setBlockName("cloud");
		if(enableCorruptor) {
			corruptor = (new CorruptorBlock(corruptorID)).setHardness(1.0F).setBlockName("corruptor");
			corruptBrick = (new CorruptBrick(corruptBrickID)).setHardness(2.0F).setBlockName("corruptbrick");
		}
		if(enableTrees) {
			redwood = (new RedwoodBlock(redwoodID)).setHardness(1.5F).setResistance(5F).setStepSound(Block.soundWoodFootstep).setBlockName("redwood").setRequiresSelfNotify();
			floraLeaves = (new FloraLeaves(floraLeavesID, 4)).setHardness(0.2F).setLightOpacity(1).setStepSound(Block.soundGrassFootstep).setBlockName("floraLeaves").setRequiresSelfNotify();
			cherryLeaves = (new CherryLeaves(cherryLeavesID, 7)).setHardness(0.2F).setLightOpacity(1).setStepSound(Block.soundGrassFootstep).setBlockName("cherryLeaves").setRequiresSelfNotify();
			floraSapling = (new FloraSaplingBlock(floraSaplingID)).setHardness(0.0F).setStepSound(Block.soundGrassFootstep).setBlockName("floraSapling").setRequiresSelfNotify();
			floraBoat = new FloraBoat(boatItemID).setIconCoord(0, 3).setItemName("floraBoat");
			redwoodDoorItem = new FloraDoorItem(redwoodDoorItemID, redwoodDoorID).setIconCoord(3, 9).setItemName("redwoodDoorItem");
			redwoodDoor = new FloraDoor(redwoodDoorID, 162, Material.wood, redwoodDoorItem.shiftedIndex).setHardness(3F).setStepSound(Block.soundWoodFootstep).setBlockName("redwoodDoor").disableStats().setRequiresSelfNotify();
		}
		
		if(enableCrops) {
			food = (new RecipeItem(foodID, "/floratex/seeds.png")).setIconCoord(1, 3).setItemName("food");
			floraCrops = (new FloraCrops(floraCropsID, 80)).setStepSound(Block.soundGrassFootstep).setBlockName("floraCrops");
			barleySeed = (new FloraSeeds(barleySeedID, floraCrops.blockID, Block.tilledField.blockID)).setIconCoord(1, 2).setItemName("barleySeed");
		}
		waterDrop = (new CactusJuice(ingredientsID + 0, 2, false, 16)).setIconCoord(3, 0).setItemName("waterDrop");
		
		wheatFlour = (new InfiTexture(ingredientsID + 2, "/floratex/infifood.png")).setIconCoord(1, 0).setItemName("wheatFlour");
		wheatDough = (new InfiTexture(ingredientsID + 3, "/floratex/infifood.png")).setIconCoord(2, 0).setItemName("wheatDough");
		
		if(enableSaguaro)
			saguaro = new SaguaroBlock(saguaroID, 149).setHardness(0.8F).setBlockName("Saguaro");
	}
}
