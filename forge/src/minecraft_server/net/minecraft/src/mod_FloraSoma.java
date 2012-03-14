package net.minecraft.src;

import net.minecraft.src.flora.*;
import net.minecraft.src.forge.*;
import net.minecraft.src.core.*;

import java.io.File;
import java.util.Map;
import java.util.Random;

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

	public mod_FloraSoma()
	{
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
		berryModelID = ModLoader.getUniqueBlockModelID(this, true);
		saguaroModel = ModLoader.getUniqueBlockModelID(this, true);
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
		
		ModLoader.registerEntityID(net.minecraft.src.flora.RedwoodBoat.class, "redwoodBoat", redwoodBoatID);
		ModLoader.registerEntityID(net.minecraft.src.flora.BloodBoat.class, "bloodBoat", bloodBoatID);
		ModLoader.registerEntityID(net.minecraft.src.flora.WhiteWoodBoat.class, "whiteBoat", whiteBoatID);
		ModLoader.registerEntityID(net.minecraft.src.flora.EucalyptusBoat.class, "eucalyptusBoat", eucalyptusBoatID);
		
		ModLoaderMp.registerEntityTracker(net.minecraft.src.flora.RedwoodBoat.class, 160, 2);
		ModLoaderMp.registerEntityTracker(net.minecraft.src.flora.BloodBoat.class, 160, 2);
		ModLoaderMp.registerEntityTracker(net.minecraft.src.flora.WhiteWoodBoat.class, 160, 2);
		ModLoaderMp.registerEntityTracker(net.minecraft.src.flora.EucalyptusBoat.class, 160, 2);
		
		ModLoaderMp.registerEntityTrackerEntry(net.minecraft.src.flora.RedwoodBoat.class, redwoodBoatID);
		ModLoaderMp.registerEntityTrackerEntry(net.minecraft.src.flora.BloodBoat.class, bloodBoatID);
		ModLoaderMp.registerEntityTrackerEntry(net.minecraft.src.flora.WhiteWoodBoat.class, whiteBoatID);
		ModLoaderMp.registerEntityTrackerEntry(net.minecraft.src.flora.EucalyptusBoat.class, eucalyptusBoatID);
		
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
	

	@Override
	public void generateSurface(World world, Random random, int xChunk, int zChunk)
	{
		int xCh = xChunk + random.nextInt(16) + 8;
		int yCh = random.nextInt(128);
		int zCh = zChunk + random.nextInt(16) + 8;
		if(enableCrops && random.nextInt(5) < 2)
		{
			genCrops.generate(world, random, xCh, yCh + barleySpawnHeight - 64, zCh);
			genCrops.generate(world, random, xCh, yCh + barleySpawnHeight - 64, zCh);
		}
		
		BiomeGenBase biomegenbase = world.getWorldChunkManager().getBiomeGenAt(xChunk + 16, zChunk + 16);
		if (enableClouds && biomegenbase != BiomeGenBase.desert && biomegenbase != BiomeGenBase.desertHills 
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
		if (biomegenbase == BiomeGenBase.forest)
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
		if (biomegenbase == BiomeGenBase.plains)
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
		if (biomegenbase == BiomeGenBase.extremeHills)
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
		if (enableBerryBush && biomegenbase == BiomeGenBase.taiga && random.nextInt(geoSpawnDensity) == 0)
		{
			int yBry = random.nextInt(geoSpawnRange) + geoSpawnHeight - 64;
			xCh = xChunk + random.nextInt(16) + 8;
			zCh = zChunk + random.nextInt(16) + 8;
			geogen.generate(world, random, xCh, yCh, zCh);
		}
		if (enableBerryBush && biomegenbase == BiomeGenBase.swampland)
		{
			for (int iter = 0; iter < (int)(blackSpawnDensity / 5); iter++)
			{
				int yBry = random.nextInt(blackSpawnRange) + blackSpawnHeight - 64;
				xCh = xChunk + random.nextInt(16) + 8;
				zCh = zChunk + random.nextInt(16) + 8;
				blackgen.generate(world, random, xCh, yBry, zCh);
			}
		}
		if (biomegenbase == BiomeGenBase.river)
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
		if (enableClouds && biomegenbase == BiomeGenBase.sky && random.nextInt(darkCloudSpawnDensity) < darkCloudSpawnDensity / 4)
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
		
		
		ModLoader.addRecipe(new ItemStack(waterDrop, 1), new Object[]
				{
					"X", 'X', Block.cactus
				});
		
		
		ModLoader.addRecipe(new ItemStack(wheatFlour, 1), new Object[]
				{
					"X", 'X', Item.wheat
				});
		
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
	
	public static InfiProps props;
	public static InfiProps spawnProps;

	public static File getMinecraftDir()
    {
        return new File(".");
    }

	static
	{
		File me = new File( (new StringBuilder().append(getMinecraftDir().getPath())
				.append('/').append("mDiyo").toString() ) );
        me.mkdir();
		props = new InfiProps((new File((new StringBuilder()).append(getMinecraftDir().getPath())
				.append('/').append("mDiyo").append('/').append("FloraSomaIDs.cfg").toString())).getPath());
		props = PropsHelperFloraSoma.InitIDs(props);
		PropsHelperFloraSoma.getIDs(props);
		spawnProps = new InfiProps((new File((new StringBuilder()).append(getMinecraftDir().getPath())
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
