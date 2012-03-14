package net.minecraft.src;

import net.minecraft.src.forge.*;
import net.minecraft.src.orizon.*;
//import net.minecraft.src.orizondim.*;
import java.io.File;
import java.util.*;
import net.minecraft.client.Minecraft;

public class mod_Orizon extends BaseModMp
{
    public static InfiProps props;
    
    public static Block mineralOre;
    public static Block mineralOreHigh;
    public static Block mineralOreLow1;
    public static Block mineralOreLow2;
    public static Block mineralOreLow3;
    
    public static Block mineralOreAlt;
    public static Block mineralOreAltHigh;
    public static Block mineralOreAltLow1;
    public static Block mineralOreAltLow2;
    public static Block mineralOreAltLow3;
    
    public static Block gemOre;
    public static Block gemOreHigh;
    public static Block gemOreLow1;
    public static Block gemOreLow2;
    public static Block gemOreLow3;
    
    public static Block calciteOre;
    public static Block netherOre;
    public static Block marble;
    
    public static int mineralOreID;
    public static int mineralOreHighID;
    public static int mineralOreLow1ID;
    public static int mineralOreLow2ID;
    public static int mineralOreLow3ID;
    
    public static int mineralOreAltID;
    public static int mineralOreAltHighID;
    public static int mineralOreAltLow1ID;
    public static int mineralOreAltLow2ID;
    public static int mineralOreAltLow3ID;
    
    public static int gemOreID;
    public static int gemOreHighID;
    public static int gemOreLow1ID;
    public static int gemOreLow2ID;
    public static int gemOreLow3ID;
    
    public static int calciteOreID;
    public static int netherOreID;
    public static int marbleID;
    
    public static Block cStone;
    public static Block cCobble;
    public static Block cBrick;
    public static Block cMossy;
    public static Block cCracked;
    public static Block cTile;
    public static Block cFancy;
    public static Block cSquare;
    
    public static int cStoneID;
    public static int cCobbleID;
    public static int cBrickID;
    public static int cMossyID;
    public static int cCrackedID;
    public static int cTileID;
    public static int cFancyID;
    public static int cSquareID;
    
    public static Block replaceOre;
    public static Block replaceMetal;
    
    public static int replaceOreID;
    public static int replaceMetalID;
    
    public static boolean genCopper;
    public static boolean genTurquoise;
    public static boolean genChalcocite;
    public static boolean genCassiterite;
    public static boolean genTeallite;
    public static boolean genZinc;
    public static boolean genSphalerite;
    public static boolean genCerussite;
    public static boolean genCobalt;
    public static boolean genArdite;
    public static boolean genMyuvil;
    
    public static Block slimeStill;
    public static Block slimeFlowing;
    
    public static int slimePoolID;
    
    public static boolean genSlimePools;
    public static int slimePoolRarity;
    public static int slimePoolHeight;
    
    public static int copperRarity;
    public static int copperHeight;
    public static int turquoiseRarity;
    public static int turquoiseHeight;
    public static int chalcociteRarity;
    public static int chalcociteHeight;
    public static int cassiteriteRarity;
    public static int cassiteriteHeight;
    public static int tealliteRarity;
    public static int tealliteHeight;
    public static int zincBloomRarity;
    public static int zincBloomHeight;
    public static int sphaleriteRarity;
    public static int sphaleriteHeight;
    public static int cerussiteRarity;
    public static int cerussiteHeight;
    public static int cobaltRarity;
    public static int cobaltHeight;
    public static int arditeRarity;
    public static int arditeHeight;
    public static int myuvilRarity;
    public static int myuvilHeight;
    public static int galenaRarity;
    public static int galenaHeight;
    public static int ivymetalRarity;
    public static int ivymetalHeight;
    public static int aggregateRarity;
    public static int aggregateHeight;
    
    public static int goldRarity;
    public static int goldHeight;
    public static int diamondRarity;
    public static int diamondHeight;
    public static int lapisRarity;
    public static int lapisHeight;
    public static int redstoneRarity;
    public static int redstoneHeight;
    
    public static int rubyRarity;
    public static int rubyHeight;
    public static int emeraldRarity;
    public static int emeraldHeight;
    public static int sapphireRarity;
    public static int sapphireHeight;
    public static int amethystRarity;
    public static int amethystHeight;
    public static int topazRarity;
    public static int topazHeight;
    public static int quartzRarity;
    public static int quartzHeight;
    public static int roseQuartzRarity;
    public static int roseQuartzHeight;
    public static int rockCrystalRarity;
    public static int rockCrystalHeight;
    
    public static int marbleRarity;
    public static int marbleHeight;
    
    public static boolean resolveConflicts;
    public static boolean genStratifiedStone;
    public static boolean genColoredStone;
    public static boolean genCalcite;
    public static boolean genMarble;
    public static boolean genNetherOre;
    public static boolean genSilt;
    public static boolean redoVanillaOres;
    public static boolean replaceOres;
    public static boolean genGems;
    
    public static TextureFX slimeAnimStill;
    public static TextureFX slimeAnimFlowing;

    public String getVersion()
    {
        return "v0.1 Zing";
    }

    public mod_Orizon()
    {
        /*
        MinecraftForge.setBlockHarvestLevel(mineralOre, 0, "pickaxe", 1);
        MinecraftForge.setBlockHarvestLevel(calciteOre, 0, "pickaxe", 0);*/
    	ModLoader.registerBlock(mineralOre, net.minecraft.src.orizon.MineralOreItem.class);
    	ModLoader.registerBlock(mineralOreHigh, net.minecraft.src.orizon.MineralOreHighItem.class);
    	ModLoader.registerBlock(mineralOreLow1, net.minecraft.src.orizon.MineralOreMediumItem.class);
    	ModLoader.registerBlock(mineralOreLow2, net.minecraft.src.orizon.MineralOreLowItem.class);
    	ModLoader.registerBlock(mineralOreLow3, net.minecraft.src.orizon.MineralOreDarkItem.class);
    	
    	ModLoader.registerBlock(mineralOreAlt, net.minecraft.src.orizon.MineralOreAltItem.class);
    	ModLoader.registerBlock(mineralOreAltHigh, net.minecraft.src.orizon.MineralOreAltHighItem.class);
    	ModLoader.registerBlock(mineralOreAltLow1, net.minecraft.src.orizon.MineralOreAltMediumItem.class);
    	ModLoader.registerBlock(mineralOreAltLow2, net.minecraft.src.orizon.MineralOreAltLowItem.class);
    	ModLoader.registerBlock(mineralOreAltLow3, net.minecraft.src.orizon.MineralOreAltDarkItem.class);
    	
    	ModLoader.registerBlock(gemOre, net.minecraft.src.orizon.GemOreItem.class);
    	ModLoader.registerBlock(gemOreHigh, net.minecraft.src.orizon.GemOreHighItem.class);
    	ModLoader.registerBlock(gemOreLow1, net.minecraft.src.orizon.GemOreMediumItem.class);
    	ModLoader.registerBlock(gemOreLow2, net.minecraft.src.orizon.GemOreLowItem.class);
    	ModLoader.registerBlock(gemOreLow3, net.minecraft.src.orizon.GemOreDarkItem.class);
    	
    	ModLoader.registerBlock(cStone, net.minecraft.src.orizon.ColoredStoneItem.class);
    	ModLoader.registerBlock(cCobble, net.minecraft.src.orizon.ColoredStoneItem.class);
    	ModLoader.registerBlock(cBrick, net.minecraft.src.orizon.ColoredStoneItem.class);
    	ModLoader.registerBlock(cMossy, net.minecraft.src.orizon.ColoredStoneItem.class);
    	ModLoader.registerBlock(cCracked, net.minecraft.src.orizon.ColoredStoneItem.class);
    	ModLoader.registerBlock(cTile, net.minecraft.src.orizon.ColoredStoneItem.class);
    	ModLoader.registerBlock(cFancy, net.minecraft.src.orizon.ColoredStoneItem.class);
    	ModLoader.registerBlock(cSquare, net.minecraft.src.orizon.ColoredStoneItem.class);
    	
    	ModLoader.registerBlock(calciteOre, net.minecraft.src.orizon.CalciteOreItem.class);
    	ModLoader.registerBlock(marble, net.minecraft.src.orizon.MarbleItem.class);
    	ModLoader.registerBlock(replaceOre, net.minecraft.src.orizon.OreReplacementItem.class);
    	ModLoader.registerBlock(replaceMetal, net.minecraft.src.orizon.OreReplacementMetalItem.class);
    	ModLoader.registerBlock(slimeStill);
    	ModLoader.registerBlock(slimeFlowing);
    	
    	MinecraftForgeClient.preloadTexture("/oretex/ores.png");
    	MinecraftForgeClient.preloadTexture("/oretex/stone.png");
    	MinecraftForgeClient.preloadTexture("/oretex/gems.png");
    	MinecraftForgeClient.preloadTexture("/oretex/slime.png");
    	
    	Orizon.addNames();
    	//Orizon.addRecipes();
    	
    	slimeAnimStill = new TextureSlimeFX();
        slimeAnimFlowing = new TextureSlimeFlowFX();
    	ModLoader.addAnimation(slimeAnimStill);
    	ModLoader.addAnimation(slimeAnimFlowing);
    	
    	//DimensionManager.registerDimension(7, new WorldProviderOrizon(), true);
    	
    	/*ModLoader.registerBlock(warpPlank);
		ModLoader.addName(warpPlank, "Reality-warping Plank");
		ModLoader.addRecipe(new ItemStack(warpPlank, 1), new Object[] {
			"p ", " p", 'p', Block.planks //Planks in shears pattern
		});*/
    	
    	addInfiBlockSupport();
    }
    
    public void addInfiBlockSupport() {
    	try
        {
    		Class class1 = Class.forName("DetailManager");
        } catch (Throwable throwable)
        {
            System.out.println("InfiBlock detailing failed! Reason:");
            System.out.println(throwable);
        }
    }

    //public static Block warpPlank;
    
    @Override
    public void generateSurface(World world, Random random, int chunkX, int chunkZ)
    {
    	if(genCalcite)
    		WorldGenStones.generateCalcite(world, random, chunkX, chunkZ);
    	if(genMarble)
    		WorldGenStones.generateMarble(world, random, chunkX, chunkZ);
    	if(genSilt)
    		WorldGenStones.generateSilt(world, random, chunkX, chunkZ);
    	if(genGems)
    		WorldGenStones.generateGems(world, random, chunkX, chunkZ);
    	if(genStratifiedStone)
    		WorldGenStones.generateStratifiedStone(world, random, chunkX, chunkZ);
    	if(genSlimePools)
    		WorldGenStones.generateSlimePool(world, random, chunkX, chunkZ);
        WorldGenStones.generateOres(world, random, chunkX, chunkZ);
    }

    public void load() {}

    static
    {
    	File me = new File( (new StringBuilder().append(Minecraft.getMinecraftDir().getPath())
				.append('/').append("mDiyo").toString() ) );
        me.mkdir();
		props = new InfiProps((new File((new StringBuilder()).append(Minecraft.getMinecraftDir().getPath())
				.append('/').append("mDiyo").append('/').append("OrizonIDs.cfg").toString())).getPath());
		props = PropsHelperOrizon.InitProps(props);
		PropsHelperOrizon.getProps(props);
		props = new InfiProps((new File((new StringBuilder()).append(Minecraft.getMinecraftDir().getPath())
				.append('/').append("mDiyo").append('/').append("OrizonWorldGen.cfg").toString())).getPath());
		props = PropsHelperOrizon.InitSpawn(props);
		PropsHelperOrizon.getSpawn(props);
		
		if(resolveConflicts)
			PropsHelperOrizon.resolveIDs(props);
		
		cStone = new ColoredStone(cStoneID, 0).setHardness(Block.stone.getHardness()).setBlockName("Colored Stone");
		cCobble = new CustomBlockStone(cCobbleID, 16).setHardness(Block.cobblestone.getHardness()).setBlockName("Colored Cobblestone");
		cBrick = new CustomBlockStone(cBrickID, 32).setHardness(Block.stone.getHardness()).setBlockName("Colored Stone Brick");
		cMossy = new CustomBlockStone(cMossyID, 48).setHardness(Block.stone.getHardness()).setBlockName("Colored Moss Brick");
		cCracked = new CustomBlockStone(cCrackedID, 64).setHardness(Block.stone.getHardness()).setBlockName("Colored Cracked Brick");
		cTile = new CustomBlockStone(cTileID, 80).setHardness(Block.stone.getHardness()).setBlockName("Colored Tile");
		cFancy = new CustomBlockStone(cFancyID, 96).setHardness(Block.stone.getHardness()).setBlockName("Colored Fancy Brick");
		cSquare = new CustomBlockStone(cSquareID, 112).setHardness(Block.stone.getHardness()).setBlockName("Colored Square Brick");
		
        mineralOre = new MineralOre(mineralOreID, 32).setStepSound(Block.soundStoneFootstep).setHardness(3.0F).setBlockName("Mineral Ore");
        mineralOreHigh = new MineralOre(mineralOreHighID, 0).setStepSound(Block.soundStoneFootstep).setHardness(3.0F).setBlockName("Mineral Ore High");
        mineralOreLow1 = new MineralOre(mineralOreLow1ID, 64).setStepSound(Block.soundStoneFootstep).setHardness(3.0F).setBlockName("Mineral Ore Medium");
        mineralOreLow2 = new MineralOre(mineralOreLow2ID, 96).setStepSound(Block.soundStoneFootstep).setHardness(3.0F).setBlockName("Mineral Ore Low");
        mineralOreLow3 = new MineralOre(mineralOreLow3ID, 128).setStepSound(Block.soundStoneFootstep).setHardness(3.0F).setBlockName("Mineral Ore Dark");
        
        mineralOreAlt = new MineralOreAlt(mineralOreAltID, 48).setStepSound(Block.soundStoneFootstep).setHardness(3.0F).setBlockName("Mineral Ore Alt");
        mineralOreAltHigh = new MineralOreAlt(mineralOreAltHighID, 16).setStepSound(Block.soundStoneFootstep).setHardness(3.0F).setBlockName("Mineral Ore Alt High");
        mineralOreAltLow1 = new MineralOreAlt(mineralOreAltLow1ID, 80).setStepSound(Block.soundStoneFootstep).setHardness(3.0F).setBlockName("Mineral Ore Alt Medium");
        mineralOreAltLow2 = new MineralOreAlt(mineralOreAltLow2ID, 112).setStepSound(Block.soundStoneFootstep).setHardness(3.0F).setBlockName("Mineral Ore Alt Low");
        mineralOreAltLow3 = new MineralOreAlt(mineralOreAltLow3ID, 144).setStepSound(Block.soundStoneFootstep).setHardness(3.0F).setBlockName("Mineral Ore Alt Dark");
        
        gemOre = new GemOre(gemOreID, 16).setStepSound(Block.soundStoneFootstep).setHardness(3.0F).setBlockName("Gem Ore");
        gemOreHigh = new GemOre(gemOreHighID, 0).setStepSound(Block.soundStoneFootstep).setHardness(3.0F).setBlockName("Gem Ore High");
        gemOreLow1 = new GemOre(gemOreLow1ID, 32).setStepSound(Block.soundStoneFootstep).setHardness(3.0F).setBlockName("Gem Ore Medium");
        gemOreLow2 = new GemOre(gemOreLow2ID, 48).setStepSound(Block.soundStoneFootstep).setHardness(3.0F).setBlockName("Gem Ore Low");
        gemOreLow3 = new GemOre(gemOreLow3ID, 64).setStepSound(Block.soundStoneFootstep).setHardness(3.0F).setBlockName("Gem Ore Dark");
        
        calciteOre = new CalciteOre(calciteOreID, 160).setStepSound(Block.soundStoneFootstep).setHardness(1.0F).setBlockName("Calcite Ore");
        marble = new Marble(marbleID, 176).setHardness(5F).setBlockName("Marble");

        replaceOre = new OreReplacement(replaceOreID, 208).setHardness(3F).setBlockName("Replacement Ore");
        replaceMetal = new OreReplacementMetal(replaceMetalID, 224).setHardness(3F).setBlockName("Replacement Metal Ore");
        
        slimeStill = new SlimeBlockStill(slimePoolID+1, 0, Material.water).setBlockName("Slime Pool");
        slimeFlowing = new SlimeBlockFlowing(slimePoolID, 0, Material.water).setBlockName("Slime Pool Flowing");
        
    }
}
