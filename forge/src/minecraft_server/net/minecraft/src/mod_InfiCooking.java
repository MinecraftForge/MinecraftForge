package net.minecraft.src;

import net.minecraft.src.infiflora.*;

import java.io.File;
import java.io.PrintStream;

public class mod_InfiCooking extends BaseModMp
{
    
    public static int woodFryingPanID;
    public static int stoneFryingPanID;
    public static int ironFryingPanID;
    public static int diamondFryingPanID;
    public static int goldFryingPanID;
    public static int redstoneFryingPanID;
    public static int obsidianFryingPanID;
    public static int sandstoneFryingPanID;
    public static int boneFryingPanID;
    public static int paperFryingPanID;
    public static int mossyFryingPanID;
    public static int netherrackFryingPanID;
    public static int glowstoneFryingPanID;
    public static int iceFryingPanID;
    public static int lavaFryingPanID;
    public static int slimeFryingPanID;
    public static int cactusFryingPanID;
    public static int flintFryingPanID;
    public static int brickFryingPanID;
    public static int blazeFryingPanID;
    public static int pumpkinPulpID;
    public static int wDur;
    public static float wMod;
    public static int wDam;
    public static int wLevel;
    public static float wSpeed;
    public static int stDur;
    public static float stMod;
    public static int stDam;
    public static int stLevel;
    public static float stSpeed;
    public static int iDur;
    public static float iMod;
    public static int iDam;
    public static int iLevel;
    public static float iSpeed;
    public static int dDur;
    public static float dMod;
    public static int dDam;
    public static int dLevel;
    public static float dSpeed;
    public static int gDur;
    public static float gMod;
    public static int gDam;
    public static int gLevel;
    public static float gSpeed;
    public static int rDur;
    public static float rMod;
    public static int rDam;
    public static int rLevel;
    public static float rSpeed;
    public static int oDur;
    public static float oMod;
    public static int oDam;
    public static int oLevel;
    public static float oSpeed;
    public static int saDur;
    public static float saMod;
    public static int saDam;
    public static int saLevel;
    public static float saSpeed;
    public static int bDur;
    public static float bMod;
    public static int bDam;
    public static int bLevel;
    public static float bSpeed;
    public static int pDur;
    public static float pMod;
    public static int pDam;
    public static int pLevel;
    public static float pSpeed;
    public static int mDur;
    public static float mMod;
    public static int mDam;
    public static int mLevel;
    public static float mSpeed;
    public static int nDur;
    public static float nMod;
    public static int nDam;
    public static int nLevel;
    public static float nSpeed;
    public static int glDur;
    public static float glMod;
    public static int glDam;
    public static int glLevel;
    public static float glSpeed;
    public static int iceDur;
    public static float iceMod;
    public static int iceDam;
    public static int iceLevel;
    public static float iceSpeed;
    public static int lDur;
    public static float lMod;
    public static int lDam;
    public static int lLevel;
    public static float lSpeed;
    public static int sDur;
    public static float sMod;
    public static int sDam;
    public static int sLevel;
    public static float sSpeed;
    public static int cDur;
    public static float cMod;
    public static int cDam;
    public static int cLevel;
    public static float cSpeed;
    public static int fDur;
    public static float fMod;
    public static int fDam;
    public static int fLevel;
    public static float fSpeed;
    public static int brDur;
    public static float brMod;
    public static int brDam;
    public static int brLevel;
    public static float brSpeed;
    public static int blDur;
    public static float blMod;
    public static int blDam;
    public static int blLevel;
    public static float blSpeed;
    public static int wType;
    public static int stType;
    public static int iType;
    public static int dType;
    public static int gType;
    public static int rType;
    public static int oType;
    public static int saType;
    public static int bType;
    public static int pType;
    public static int mType;
    public static int nType;
    public static int glType;
    public static int iceType;
    public static int lType;
    public static int sType;
    public static int cType;
    public static int fType;
    public static int brType;
    public static int blType;

    public static Item wWoodFryingPan;
    public static Item stWoodFryingPan;
    public static Item saWoodFryingPan;
    public static Item bWoodFryingPan;
    public static Item pWoodFryingPan;
    public static Item nWoodFryingPan;
    public static Item sWoodFryingPan;
    public static Item cWoodFryingPan;
    public static Item fWoodFryingPan;
    public static Item brWoodFryingPan;
    public static Item wStoneFryingPan;
    public static Item stStoneFryingPan;
    public static Item saStoneFryingPan;
    public static Item bStoneFryingPan;
    public static Item pStoneFryingPan;
    public static Item mStoneFryingPan;
    public static Item nStoneFryingPan;
    public static Item iceStoneFryingPan;
    public static Item sStoneFryingPan;
    public static Item cStoneFryingPan;
    public static Item fStoneFryingPan;
    public static Item brStoneFryingPan;
    public static Item wIronFryingPan;
    public static Item stIronFryingPan;
    public static Item iIronFryingPan;
    public static Item dIronFryingPan;
    public static Item gIronFryingPan;
    public static Item rIronFryingPan;
    public static Item oIronFryingPan;
    public static Item bIronFryingPan;
    public static Item nIronFryingPan;
    public static Item glIronFryingPan;
    public static Item iceIronFryingPan;
    public static Item sIronFryingPan;
    public static Item blIronFryingPan;
    public static Item wDiamondFryingPan;
    public static Item stDiamondFryingPan;
    public static Item iDiamondFryingPan;
    public static Item dDiamondFryingPan;
    public static Item gDiamondFryingPan;
    public static Item rDiamondFryingPan;
    public static Item oDiamondFryingPan;
    public static Item bDiamondFryingPan;
    public static Item mDiamondFryingPan;
    public static Item nDiamondFryingPan;
    public static Item glDiamondFryingPan;
    public static Item blDiamondFryingPan;
    public static Item wGoldFryingPan;
    public static Item stGoldFryingPan;
    public static Item gGoldFryingPan;
    public static Item oGoldFryingPan;
    public static Item saGoldFryingPan;
    public static Item bGoldFryingPan;
    public static Item mGoldFryingPan;
    public static Item nGoldFryingPan;
    public static Item glGoldFryingPan;
    public static Item iceGoldFryingPan;
    public static Item sGoldFryingPan;
    public static Item cGoldFryingPan;
    public static Item fGoldFryingPan;
    public static Item wRedstoneFryingPan;
    public static Item stRedstoneFryingPan;
    public static Item iRedstoneFryingPan;
    public static Item dRedstoneFryingPan;
    public static Item rRedstoneFryingPan;
    public static Item oRedstoneFryingPan;
    public static Item bRedstoneFryingPan;
    public static Item mRedstoneFryingPan;
    public static Item glRedstoneFryingPan;
    public static Item sRedstoneFryingPan;
    public static Item blRedstoneFryingPan;
    public static Item wObsidianFryingPan;
    public static Item stObsidianFryingPan;
    public static Item iObsidianFryingPan;
    public static Item dObsidianFryingPan;
    public static Item gObsidianFryingPan;
    public static Item rObsidianFryingPan;
    public static Item oObsidianFryingPan;
    public static Item bObsidianFryingPan;
    public static Item nObsidianFryingPan;
    public static Item glObsidianFryingPan;
    public static Item sObsidianFryingPan;
    public static Item fObsidianFryingPan;
    public static Item blObsidianFryingPan;
    public static Item wSandstoneFryingPan;
    public static Item stSandstoneFryingPan;
    public static Item oSandstoneFryingPan;
    public static Item saSandstoneFryingPan;
    public static Item bSandstoneFryingPan;
    public static Item pSandstoneFryingPan;
    public static Item nSandstoneFryingPan;
    public static Item iceSandstoneFryingPan;
    public static Item sSandstoneFryingPan;
    public static Item cSandstoneFryingPan;
    public static Item fSandstoneFryingPan;
    public static Item brSandstoneFryingPan;
    public static Item wBoneFryingPan;
    public static Item stBoneFryingPan;
    public static Item iBoneFryingPan;
    public static Item dBoneFryingPan;
    public static Item rBoneFryingPan;
    public static Item oBoneFryingPan;
    public static Item bBoneFryingPan;
    public static Item mBoneFryingPan;
    public static Item nBoneFryingPan;
    public static Item glBoneFryingPan;
    public static Item sBoneFryingPan;
    public static Item cBoneFryingPan;
    public static Item fBoneFryingPan;
    public static Item brBoneFryingPan;
    public static Item blBoneFryingPan;
    public static Item wPaperFryingPan;
    public static Item saPaperFryingPan;
    public static Item bPaperFryingPan;
    public static Item pPaperFryingPan;
    public static Item sPaperFryingPan;
    public static Item cPaperFryingPan;
    public static Item brPaperFryingPan;
    public static Item stMossyFryingPan;
    public static Item dMossyFryingPan;
    public static Item rMossyFryingPan;
    public static Item bMossyFryingPan;
    public static Item mMossyFryingPan;
    public static Item glMossyFryingPan;
    public static Item wNetherrackFryingPan;
    public static Item stNetherrackFryingPan;
    public static Item iNetherrackFryingPan;
    public static Item rNetherrackFryingPan;
    public static Item oNetherrackFryingPan;
    public static Item saNetherrackFryingPan;
    public static Item bNetherrackFryingPan;
    public static Item mNetherrackFryingPan;
    public static Item nNetherrackFryingPan;
    public static Item glNetherrackFryingPan;
    public static Item iceNetherrackFryingPan;
    public static Item sNetherrackFryingPan;
    public static Item cNetherrackFryingPan;
    public static Item fNetherrackFryingPan;
    public static Item brNetherrackFryingPan;
    public static Item blNetherrackFryingPan;
    public static Item wGlowstoneFryingPan;
    public static Item stGlowstoneFryingPan;
    public static Item iGlowstoneFryingPan;
    public static Item dGlowstoneFryingPan;
    public static Item rGlowstoneFryingPan;
    public static Item oGlowstoneFryingPan;
    public static Item bGlowstoneFryingPan;
    public static Item mGlowstoneFryingPan;
    public static Item nGlowstoneFryingPan;
    public static Item glGlowstoneFryingPan;
    public static Item iceGlowstoneFryingPan;
    public static Item lGlowstoneFryingPan;
    public static Item sGlowstoneFryingPan;
    public static Item blGlowstoneFryingPan;
    public static Item wIceFryingPan;
    public static Item stIceFryingPan;
    public static Item iIceFryingPan;
    public static Item dIceFryingPan;
    public static Item gIceFryingPan;
    public static Item rIceFryingPan;
    public static Item oIceFryingPan;
    public static Item saIceFryingPan;
    public static Item bIceFryingPan;
    public static Item glIceFryingPan;
    public static Item iceIceFryingPan;
    public static Item sIceFryingPan;
    public static Item cIceFryingPan;
    public static Item fIceFryingPan;
    public static Item brIceFryingPan;
    public static Item dLavaFryingPan;
    public static Item rLavaFryingPan;
    public static Item bLavaFryingPan;
    public static Item nLavaFryingPan;
    public static Item glLavaFryingPan;
    public static Item lLavaFryingPan;
    public static Item blLavaFryingPan;
    public static Item wSlimeFryingPan;
    public static Item stSlimeFryingPan;
    public static Item iSlimeFryingPan;
    public static Item dSlimeFryingPan;
    public static Item gSlimeFryingPan;
    public static Item rSlimeFryingPan;
    public static Item oSlimeFryingPan;
    public static Item saSlimeFryingPan;
    public static Item bSlimeFryingPan;
    public static Item pSlimeFryingPan;
    public static Item mSlimeFryingPan;
    public static Item nSlimeFryingPan;
    public static Item glSlimeFryingPan;
    public static Item iceSlimeFryingPan;
    public static Item lSlimeFryingPan;
    public static Item sSlimeFryingPan;
    public static Item cSlimeFryingPan;
    public static Item fSlimeFryingPan;
    public static Item brSlimeFryingPan;
    public static Item blSlimeFryingPan;
    public static Item wCactusFryingPan;
    public static Item stCactusFryingPan;
    public static Item saCactusFryingPan;
    public static Item bCactusFryingPan;
    public static Item pCactusFryingPan;
    public static Item nCactusFryingPan;
    public static Item sCactusFryingPan;
    public static Item cCactusFryingPan;
    public static Item fCactusFryingPan;
    public static Item brCactusFryingPan;
    public static Item wFlintFryingPan;
    public static Item stFlintFryingPan;
    public static Item iFlintFryingPan;
    public static Item gFlintFryingPan;
    public static Item oFlintFryingPan;
    public static Item saFlintFryingPan;
    public static Item bFlintFryingPan;
    public static Item nFlintFryingPan;
    public static Item iceFlintFryingPan;
    public static Item sFlintFryingPan;
    public static Item cFlintFryingPan;
    public static Item fFlintFryingPan;
    public static Item brFlintFryingPan;
    public static Item blFlintFryingPan;
    public static Item wBrickFryingPan;
    public static Item stBrickFryingPan;
    public static Item saBrickFryingPan;
    public static Item bBrickFryingPan;
    public static Item pBrickFryingPan;
    public static Item mBrickFryingPan;
    public static Item nBrickFryingPan;
    public static Item iceBrickFryingPan;
    public static Item sBrickFryingPan;
    public static Item cBrickFryingPan;
    public static Item fBrickFryingPan;
    public static Item brBrickFryingPan;
    public static Item dBlazeFryingPan;
    public static Item rBlazeFryingPan;
    public static Item bBlazeFryingPan;
    public static Item nBlazeFryingPan;
    public static Item glBlazeFryingPan;
    public static Item lBlazeFryingPan;
    public static Item fBlazeFryingPan;
    public static Item blBlazeFryingPan;
    
    public static InfiProps props;

    public String getVersion()
    {
        return "v0.1.1 Infi~";
    }

    public void load()
    {
    	//checkInitialized();
    }
    
    /*public void checkInitialized()
    {
    	try
        {
            Class infi = Class.forName("mod_InfiTools");
            mod_InfiTools.getInitialized();
            InfiRecipeFryingPans.recipeStorm();
            addInfiToolsRecipes();
        }
    	catch (Exception e)
        {
            System.out.println("Flora + InfiTools initalization failed! Reason:");
            System.out.println(e);
        }
    }*/
    
    public static void addInfiToolsRecipes()
	{
		ModLoader.addRecipe(new ItemStack(mod_InfiTools.woodBucketWater, 1), new Object[]
				{
					"www", "wBw", "www", 'w', mod_FloraSoma.waterDrop, Character.valueOf('B'), mod_InfiTools.woodBucketEmpty
				});
		ModLoader.addRecipe(new ItemStack(mod_InfiTools.cactusBucketWater, 1), new Object[]
				{
					"www", "wBw", "www", 'w', mod_FloraSoma.waterDrop, Character.valueOf('B'), mod_InfiTools.cactusBucketEmpty
				});
		ModLoader.addRecipe(new ItemStack(mod_InfiTools.goldBucketWater, 1), new Object[]
				{
					"www", "wBw", "www", 'w', mod_FloraSoma.waterDrop, Character.valueOf('B'), mod_InfiTools.goldBucketEmpty
				});
		ModLoader.addRecipe(new ItemStack(mod_InfiTools.iceBucketIce, 1), new Object[]
				{
					"www", "wBw", "www", 'w', mod_FloraSoma.waterDrop, Character.valueOf('B'), mod_InfiTools.iceBucketEmpty
				});
		ModLoader.addRecipe(new ItemStack(mod_InfiTools.slimeBucketWater, 1), new Object[]
				{
					"www", "wBw", "www", 'w', mod_FloraSoma.waterDrop, 'B', mod_InfiTools.slimeBucketEmpty
				});
		
		
		ModLoader.addRecipe(new ItemStack(mod_InfiTools.woodBowlRawPumpkinPie, 1), new Object[]
				{
					"p", "d", "b", 'p', mod_InfiTools.pumpkinPulp, 'd', mod_FloraSoma.wheatDough, Character.valueOf('b'), Item.bowlEmpty
				});
		
		ModLoader.addSmelting(mod_InfiTools.woodBowlRawPumpkinPie.shiftedIndex, new ItemStack(mod_InfiTools.woodBowlPumpkinPie, 1));
		
		ModLoader.addRecipe(new ItemStack(mod_InfiTools.woodBowlRawPumpkinPie, 1), new Object[]
				{
					"p", "d", "b", 'p', mod_InfiTools.pumpkinPulp, 'd', mod_FloraSoma.wheatDough, Character.valueOf('b'), mod_InfiTools.stoneBowlEmpty
				});
		
		ModLoader.addSmelting(mod_InfiTools.stoneBowlRawPumpkinPie.shiftedIndex, new ItemStack(mod_InfiTools.stoneBowlPumpkinPie, 1));
		
		ModLoader.addRecipe(new ItemStack(mod_InfiTools.goldBowlRawPumpkinPie, 1), new Object[]
				{
					"p", "d", "b", 'p', mod_InfiTools.pumpkinPulp, 'd', mod_FloraSoma.wheatDough, Character.valueOf('b'), mod_InfiTools.goldBowlEmpty
				});
		
		ModLoader.addSmelting(mod_InfiTools.goldBowlRawPumpkinPie.shiftedIndex, new ItemStack(mod_InfiTools.goldBowlPumpkinPie, 1));
		
		ModLoader.addRecipe(new ItemStack(mod_InfiTools.netherrackBowlRawPumpkinPie, 1), new Object[]
				{
					"p", "d", "b", 'p', mod_InfiTools.pumpkinPulp, 'd', mod_FloraSoma.wheatDough, Character.valueOf('b'), mod_InfiTools.netherrackBowlEmpty
				});
		
		ModLoader.addSmelting(mod_InfiTools.netherrackBowlRawPumpkinPie.shiftedIndex, new ItemStack(mod_InfiTools.netherrackBowlPumpkinPie, 1));
		
		ModLoader.addRecipe(new ItemStack(mod_InfiTools.slimeBowlRawPumpkinPie, 1), new Object[]
				{
					"p", "d", "b", 'p', mod_InfiTools.pumpkinPulp, 'd', mod_FloraSoma.wheatDough, Character.valueOf('b'), mod_InfiTools.slimeBowlEmpty
				});
		
		ModLoader.addSmelting(mod_InfiTools.slimeBowlRawPumpkinPie.shiftedIndex, new ItemStack(mod_InfiTools.slimeBowlPumpkinPie, 1));
		
		ModLoader.addRecipe(new ItemStack(mod_InfiTools.cactusBowlRawPumpkinPie, 1), new Object[]
				{
					"p", "d", "b", 'p', mod_InfiTools.pumpkinPulp, 'd', mod_FloraSoma.wheatDough, Character.valueOf('b'), mod_InfiTools.cactusBowlEmpty
				});
		
		ModLoader.addSmelting(mod_InfiTools.cactusBowlRawPumpkinPie.shiftedIndex, new ItemStack(mod_InfiTools.cactusBowlPumpkinPie, 1));
		
		ModLoader.addRecipe(new ItemStack(mod_InfiTools.cactusBowlRawPumpkinPie, 1), new Object[]
				{
					"p", "d", "b", 'p', mod_InfiTools.pumpkinPulp, 'd', mod_FloraSoma.wheatDough, Character.valueOf('b'), mod_InfiTools.cactusBowlEmpty
				});
		
		ModLoader.addSmelting(mod_InfiTools.glassBowlRawPumpkinPie.shiftedIndex, new ItemStack(mod_InfiTools.glassBowlPumpkinPie, 1));
	}

    
    public static InfiProps getProps(InfiProps infiprops)
    {
        
        woodFryingPanID = infiprops.readInt("woodFryingPanID");
        stoneFryingPanID = infiprops.readInt("stoneFryingPanID");
        ironFryingPanID = infiprops.readInt("ironFryingPanID");
        diamondFryingPanID = infiprops.readInt("diamondFryingPanID");
        goldFryingPanID = infiprops.readInt("goldFryingPanID");
        redstoneFryingPanID = infiprops.readInt("redstoneFryingPanID");
        obsidianFryingPanID = infiprops.readInt("obsidianFryingPanID");
        sandstoneFryingPanID = infiprops.readInt("sandstoneFryingPanID");
        boneFryingPanID = infiprops.readInt("boneFryingPanID");
        paperFryingPanID = infiprops.readInt("paperFryingPanID");
        mossyFryingPanID = infiprops.readInt("mossyFryingPanID");
        netherrackFryingPanID = infiprops.readInt("netherrackFryingPanID");
        glowstoneFryingPanID = infiprops.readInt("glowstoneFryingPanID");
        iceFryingPanID = infiprops.readInt("iceFryingPanID");
        lavaFryingPanID = infiprops.readInt("lavaFryingPanID");
        slimeFryingPanID = infiprops.readInt("slimeFryingPanID");
        cactusFryingPanID = infiprops.readInt("cactusFryingPanID");
        flintFryingPanID = infiprops.readInt("flintFryingPanID");
        brickFryingPanID = infiprops.readInt("brickFryingPanID");
        blazeFryingPanID = infiprops.readInt("blazeFryingPanID");
        return infiprops;
    }

    public mod_InfiCooking()
    {
    	InfiRecipeFryingPans.recipeStorm();
    }

    public static int enchantBase(int i)
    {
        switch (i)
        {
            case 1:
                return 15;

            case 2:
                return 5;

            case 3:
                return 14;

            case 4:
                return 10;

            case 5:
                return 22;

            case 6:
                return 18;

            case 7:
                return 14;

            case 8:
                return 5;

            case 9:
                return 13;

            case 10:
                return 5;

            case 11:
                return 22;

            case 12:
                return 8;

            case 13:
                return 12;

            case 14:
                return 18;

            case 15:
                return 13;

            case 16:
                return 15;

            case 17:
                return 14;

            case 18:
                return 10;

            case 19:
                return 8;

            case 20:
                return 27;
        }
        return 0;
    }

    public static File getMinecraftDir()
    {
        return new File(".");
    }

    static
    {
        wDur = 59;
        wMod = 1.0F;
        wDam = 0;
        wLevel = 0;
        wSpeed = 2.0F;
        stDur = 131;
        stMod = 1.2F;
        stDam = 1;
        stLevel = 1;
        stSpeed = 4F;
        iDur = 250;
        iMod = 1.5F;
        iDam = 2;
        iLevel = 2;
        iSpeed = 6F;
        dDur = 1561;
        dMod = 2.0F;
        dDam = 3;
        dLevel = 3;
        dSpeed = 8F;
        gDur = 32;
        gMod = 0.5F;
        gDam = 0;
        gLevel = 0;
        gSpeed = 12F;
        rDur = 71;
        rMod = 1.0F;
        rDam = 3;
        rLevel = 3;
        rSpeed = 12F;
        oDur = 191;
        oMod = 1.8F;
        oDam = 2;
        oLevel = 3;
        oSpeed = 8F;
        saDur = 48;
        saMod = 0.9F;
        saDam = 2;
        saLevel = 0;
        saSpeed = 2.5F;
        bDur = 250;
        bMod = 1.5F;
        bDam = 2;
        bLevel = 2;
        bSpeed = 7F;
        pDur = 131;
        pMod = 1.0F;
        pDam = 0;
        pLevel = 1;
        pSpeed = 1.0F;
        mDur = 1000;
        mMod = 1.8F;
        mDam = 3;
        mLevel = 4;
        mSpeed = 10F;
        nDur = 131;
        nMod = 1.2F;
        nDam = 1;
        nLevel = 1;
        nSpeed = 4F;
        glDur = 800;
        glMod = 1.3F;
        glDam = 1;
        glLevel = 2;
        glSpeed = 6F;
        iceDur = 122;
        iceMod = 1.5F;
        iceDam = 1;
        iceLevel = 0;
        iceSpeed = 4F;
        lDur = 636;
        lMod = 2.4F;
        lDam = 1;
        lLevel = 3;
        lSpeed = 10F;
        sDur = 9001;
        sMod = 1.0F;
        sDam = 0;
        sLevel = 6;
        sSpeed = 1.0F;
        cDur = 150;
        cMod = 1.2F;
        cDam = 2;
        cLevel = 1;
        cSpeed = 5F;
        fDur = 75;
        fMod = 1.2F;
        fDam = 2;
        fLevel = 1;
        fSpeed = 5F;
        brDur = 82;
        brMod = 1.0F;
        brDam = 1;
        brLevel = 1;
        brSpeed = 4F;
        blDur = 1231;
        blMod = 1.58F;
        blDam = 5;
        blLevel = 4;
        blSpeed = 4F;
        wType = 1;
        stType = 2;
        iType = 3;
        dType = 4;
        gType = 5;
        rType = 6;
        oType = 7;
        saType = 8;
        bType = 9;
        pType = 10;
        mType = 11;
        nType = 12;
        glType = 13;
        iceType = 14;
        lType = 15;
        sType = 16;
        cType = 17;
        fType = 18;
        brType = 19;
        blType = 20;
        File me = new File( (new StringBuilder().append(getMinecraftDir().getPath())
        		.append('/').append("mDiyo").toString() ) );
        me.mkdir();
        props = new InfiProps((new File((new StringBuilder()).append(getMinecraftDir().getPath())
        		.append('/').append("mDiyo").append('/').append("InfiCooking.cfg").toString())).getPath());
        props = InfiTools.InitProps(props);
        getProps(props);
        
        wWoodFryingPan = (new InfiToolFryingPan(woodFryingPanID + 0, (int)((float)wDur * wMod), wDam, wType, wType)).setItemName("wWoodFryingPan");
        stWoodFryingPan = (new InfiToolFryingPan(woodFryingPanID + 1, (int)((float)wDur * stMod), wDam, wType, stType)).setItemName("stWoodFryingPan");
        saWoodFryingPan = (new InfiToolFryingPan(woodFryingPanID + 2, (int)((float)wDur * saMod), wDam, wType, saType)).setItemName("saWoodFryingPan");
        bWoodFryingPan = (new InfiToolFryingPan(woodFryingPanID + 3, (int)((float)wDur * bMod), wDam, wType, bType)).setItemName("bWoodFryingPan");
        pWoodFryingPan = (new InfiToolFryingPan(woodFryingPanID + 4, (int)((float)wDur * pMod), wDam, wType, pType)).setItemName("pWoodFryingPan");
        nWoodFryingPan = (new InfiToolFryingPan(woodFryingPanID + 5, (int)((float)wDur * nMod), wDam, wType, nType)).setItemName("nWoodFryingPan");
        sWoodFryingPan = (new InfiToolFryingPan(woodFryingPanID + 6, (int)((float)wDur * sMod), wDam, wType, sType)).setItemName("sWoodFryingPan");
        cWoodFryingPan = (new InfiToolFryingPan(woodFryingPanID + 7, (int)((float)wDur * cMod), wDam, wType, cType)).setItemName("cWoodFryingPan");
        fWoodFryingPan = (new InfiToolFryingPan(woodFryingPanID + 8, (int)((float)wDur * fMod), wDam, wType, fType)).setItemName("fWoodFryingPan");
        brWoodFryingPan = (new InfiToolFryingPan(woodFryingPanID + 9, (int)((float)wDur * brMod), wDam, wType, brType)).setItemName("brWoodFryingPan");
        wStoneFryingPan = (new InfiToolFryingPan(stoneFryingPanID + 0, (int)((float)stDur * wMod), stDam, stType, wType)).setItemName("wStoneFryingPan");
        stStoneFryingPan = (new InfiToolFryingPan(stoneFryingPanID + 1, (int)((float)stDur * stMod), stDam, stType, stType)).setItemName("stStoneFryingPan");
        saStoneFryingPan = (new InfiToolFryingPan(stoneFryingPanID + 2, (int)((float)stDur * saMod), stDam, stType, saType)).setItemName("saStoneFryingPan");
        bStoneFryingPan = (new InfiToolFryingPan(stoneFryingPanID + 3, (int)((float)stDur * bMod), stDam, stType, bType)).setItemName("bStoneFryingPan");
        pStoneFryingPan = (new InfiToolFryingPan(stoneFryingPanID + 4, (int)((float)stDur * pMod), stDam, stType, pType)).setItemName("pStoneFryingPan");
        mStoneFryingPan = (new InfiToolFryingPan(stoneFryingPanID + 5, (int)((float)stDur * mMod), stDam, stType, mType)).setItemName("mStoneFryingPan");
        nStoneFryingPan = (new InfiToolFryingPan(stoneFryingPanID + 6, (int)((float)stDur * nMod), stDam, stType, nType)).setItemName("nStoneFryingPan");
        iceStoneFryingPan = (new InfiToolFryingPan(stoneFryingPanID + 7, (int)((float)stDur * iceMod), stDam, stType, iceType)).setItemName("iceStoneFryingPan");
        sStoneFryingPan = (new InfiToolFryingPan(stoneFryingPanID + 8, (int)((float)stDur * sMod), stDam, stType, sType)).setItemName("sStoneFryingPan");
        cStoneFryingPan = (new InfiToolFryingPan(stoneFryingPanID + 9, (int)((float)stDur * cMod), stDam, stType, cType)).setItemName("cStoneFryingPan");
        fStoneFryingPan = (new InfiToolFryingPan(stoneFryingPanID + 10, (int)((float)stDur * fMod), stDam, stType, fType)).setItemName("fStoneFryingPan");
        brStoneFryingPan = (new InfiToolFryingPan(stoneFryingPanID + 11, (int)((float)stDur * brMod), stDam, stType, brType)).setItemName("brStoneFryingPan");
        wIronFryingPan = (new InfiToolFryingPan(ironFryingPanID + 0, (int)((float)iDur * wMod), iDam, iType, wType)).setItemName("wIronFryingPan");
        stIronFryingPan = (new InfiToolFryingPan(ironFryingPanID + 1, (int)((float)iDur * stMod), iDam, iType, stType)).setItemName("stIronFryingPan");
        iIronFryingPan = (new InfiToolFryingPan(ironFryingPanID + 2, (int)((float)iDur * iMod), iDam, iType, iType)).setItemName("iIronFryingPan");
        dIronFryingPan = (new InfiToolFryingPan(ironFryingPanID + 3, (int)((float)iDur * dMod), iDam, iType, dType)).setItemName("dIronFryingPan");
        gIronFryingPan = (new InfiToolFryingPan(ironFryingPanID + 4, (int)((float)iDur * gMod), iDam, iType, gType)).setItemName("gIronFryingPan");
        rIronFryingPan = (new InfiToolFryingPan(ironFryingPanID + 5, (int)((float)iDur * rMod), iDam, iType, rType)).setItemName("rIronFryingPan");
        oIronFryingPan = (new InfiToolFryingPan(ironFryingPanID + 6, (int)((float)iDur * oMod), iDam, iType, oType)).setItemName("oIronFryingPan");
        bIronFryingPan = (new InfiToolFryingPan(ironFryingPanID + 7, (int)((float)iDur * bMod), iDam, iType, bType)).setItemName("bIronFryingPan");
        nIronFryingPan = (new InfiToolFryingPan(ironFryingPanID + 8, (int)((float)iDur * nMod), iDam, iType, nType)).setItemName("nIronFryingPan");
        glIronFryingPan = (new InfiToolFryingPan(ironFryingPanID + 9, (int)((float)iDur * glMod), iDam, iType, glType)).setItemName("glIronFryingPan");
        iceIronFryingPan = (new InfiToolFryingPan(ironFryingPanID + 10, (int)((float)iDur * iceMod), iDam, iType, iceType)).setItemName("iceIronFryingPan");
        sIronFryingPan = (new InfiToolFryingPan(ironFryingPanID + 11, (int)((float)iDur * sMod), iDam, iType, sType)).setItemName("sIronFryingPan");
        blIronFryingPan = (new InfiToolFryingPan(ironFryingPanID + 12, (int)((float)iDur * blMod), iDam, iType, blType)).setItemName("blIronFryingPan");
        wDiamondFryingPan = (new InfiToolFryingPan(diamondFryingPanID + 0, (int)((float)dDur * wMod), dDam, dType, wType)).setItemName("wDiamondFryingPan");
        stDiamondFryingPan = (new InfiToolFryingPan(diamondFryingPanID + 1, (int)((float)dDur * stMod), dDam, dType, stType)).setItemName("stDiamondFryingPan");
        iDiamondFryingPan = (new InfiToolFryingPan(diamondFryingPanID + 2, (int)((float)dDur * iMod), dDam, dType, iType)).setItemName("iDiamondFryingPan");
        dDiamondFryingPan = (new InfiToolFryingPan(diamondFryingPanID + 3, (int)((float)dDur * dMod), dDam, dType, dType)).setItemName("dDiamondFryingPan");
        gDiamondFryingPan = (new InfiToolFryingPan(diamondFryingPanID + 4, (int)((float)dDur * gMod), dDam, dType, gType)).setItemName("gDiamondFryingPan");
        rDiamondFryingPan = (new InfiToolFryingPan(diamondFryingPanID + 5, (int)((float)dDur * rMod), dDam, dType, rType)).setItemName("rDiamondFryingPan");
        oDiamondFryingPan = (new InfiToolFryingPan(diamondFryingPanID + 6, (int)((float)dDur * oMod), dDam, dType, oType)).setItemName("oDiamondFryingPan");
        bDiamondFryingPan = (new InfiToolFryingPan(diamondFryingPanID + 7, (int)((float)dDur * bMod), dDam, dType, bType)).setItemName("bDiamondFryingPan");
        mDiamondFryingPan = (new InfiToolFryingPan(diamondFryingPanID + 8, (int)((float)dDur * mMod), dDam, dType, mType)).setItemName("mDiamondFryingPan");
        nDiamondFryingPan = (new InfiToolFryingPan(diamondFryingPanID + 9, (int)((float)dDur * nMod), dDam, dType, nType)).setItemName("nDiamondFryingPan");
        glDiamondFryingPan = (new InfiToolFryingPan(diamondFryingPanID + 10, (int)((float)dDur * glMod), dDam, dType, glType)).setItemName("glDiamondFryingPan");
        blDiamondFryingPan = (new InfiToolFryingPan(diamondFryingPanID + 11, (int)((float)dDur * blMod), dDam, dType, blType)).setItemName("blDiamondFryingPan");
        wGoldFryingPan = (new InfiToolFryingPan(goldFryingPanID + 0, (int)((float)gDur * wMod), gDam, gType, wType)).setItemName("wGoldFryingPan");
        stGoldFryingPan = (new InfiToolFryingPan(goldFryingPanID + 1, (int)((float)gDur * stMod), gDam, gType, stType)).setItemName("stGoldFryingPan");
        gGoldFryingPan = (new InfiToolFryingPan(goldFryingPanID + 2, (int)((float)gDur * gMod), gDam, gType, gType)).setItemName("gGoldFryingPan");
        oGoldFryingPan = (new InfiToolFryingPan(goldFryingPanID + 3, (int)((float)gDur * oMod), gDam, gType, oType)).setItemName("oGoldFryingPan");
        saGoldFryingPan = (new InfiToolFryingPan(goldFryingPanID + 4, (int)((float)gDur * saMod), gDam, gType, saType)).setItemName("saGoldFryingPan");
        bGoldFryingPan = (new InfiToolFryingPan(goldFryingPanID + 5, (int)((float)gDur * bMod), gDam, gType, bType)).setItemName("bGoldFryingPan");
        mGoldFryingPan = (new InfiToolFryingPan(goldFryingPanID + 6, (int)((float)gDur * mMod), gDam, gType, mType)).setItemName("mGoldFryingPan");
        nGoldFryingPan = (new InfiToolFryingPan(goldFryingPanID + 7, (int)((float)gDur * nMod), gDam, gType, nType)).setItemName("nGoldFryingPan");
        glGoldFryingPan = (new InfiToolFryingPan(goldFryingPanID + 8, (int)((float)gDur * glMod), gDam, gType, glType)).setItemName("glGoldFryingPan");
        iceGoldFryingPan = (new InfiToolFryingPan(goldFryingPanID + 9, (int)((float)gDur * iceMod), gDam, gType, iceType)).setItemName("iceGoldFryingPan");
        sGoldFryingPan = (new InfiToolFryingPan(goldFryingPanID + 10, (int)((float)gDur * sMod), gDam, gType, sType)).setItemName("sGoldFryingPan");
        fGoldFryingPan = (new InfiToolFryingPan(goldFryingPanID + 11, (int)((float)gDur * fMod), gDam, gType, fType)).setItemName("fGoldFryingPan");
        wRedstoneFryingPan = (new InfiToolFryingPan(redstoneFryingPanID + 0, (int)((float)rDur * wMod), rDam, rType, wType)).setItemName("wRedstoneFryingPan");
        stRedstoneFryingPan = (new InfiToolFryingPan(redstoneFryingPanID + 1, (int)((float)rDur * stMod), rDam, rType, stType)).setItemName("stRedstoneFryingPan");
        iRedstoneFryingPan = (new InfiToolFryingPan(redstoneFryingPanID + 2, (int)((float)rDur * iMod), rDam, rType, iType)).setItemName("iRedstoneFryingPan");
        dRedstoneFryingPan = (new InfiToolFryingPan(redstoneFryingPanID + 3, (int)((float)rDur * dMod), rDam, rType, dType)).setItemName("dRedstoneFryingPan");
        rRedstoneFryingPan = (new InfiToolFryingPan(redstoneFryingPanID + 4, (int)((float)rDur * rMod), rDam, rType, rType)).setItemName("rRedstoneFryingPan");
        oRedstoneFryingPan = (new InfiToolFryingPan(redstoneFryingPanID + 5, (int)((float)rDur * oMod), rDam, rType, oType)).setItemName("oRedstoneFryingPan");
        bRedstoneFryingPan = (new InfiToolFryingPan(redstoneFryingPanID + 6, (int)((float)rDur * bMod), rDam, rType, bType)).setItemName("bRedstoneFryingPan");
        mRedstoneFryingPan = (new InfiToolFryingPan(redstoneFryingPanID + 7, (int)((float)rDur * mMod), rDam, rType, mType)).setItemName("mRedstoneFryingPan");
        glRedstoneFryingPan = (new InfiToolFryingPan(redstoneFryingPanID + 8, (int)((float)rDur * glMod), rDam, rType, glType)).setItemName("glRedstoneFryingPan");
        sRedstoneFryingPan = (new InfiToolFryingPan(redstoneFryingPanID + 9, (int)((float)rDur * sMod), rDam, rType, sType)).setItemName("sRedstoneFryingPan");
        blRedstoneFryingPan = (new InfiToolFryingPan(redstoneFryingPanID + 10, (int)((float)rDur * blMod), rDam, rType, blType)).setItemName("blRedstoneFryingPan");
        wObsidianFryingPan = (new InfiToolFryingPan(obsidianFryingPanID + 0, (int)((float)oDur * wMod), oDam, oType, wType)).setItemName("wObsidianFryingPan");
        stObsidianFryingPan = (new InfiToolFryingPan(obsidianFryingPanID + 1, (int)((float)oDur * stMod), oDam, oType, stType)).setItemName("stObsidianFryingPan");
        iObsidianFryingPan = (new InfiToolFryingPan(obsidianFryingPanID + 2, (int)((float)oDur * iMod), oDam, oType, iType)).setItemName("iObsidianFryingPan");
        dObsidianFryingPan = (new InfiToolFryingPan(obsidianFryingPanID + 3, (int)((float)oDur * dMod), oDam, oType, dType)).setItemName("dObsidianFryingPan");
        gObsidianFryingPan = (new InfiToolFryingPan(obsidianFryingPanID + 4, (int)((float)oDur * gMod), oDam, oType, gType)).setItemName("gObsidianFryingPan");
        rObsidianFryingPan = (new InfiToolFryingPan(obsidianFryingPanID + 5, (int)((float)oDur * rMod), oDam, oType, rType)).setItemName("rObsidianFryingPan");
        oObsidianFryingPan = (new InfiToolFryingPan(obsidianFryingPanID + 6, (int)((float)oDur * oMod), oDam, oType, oType)).setItemName("oObsidianFryingPan");
        bObsidianFryingPan = (new InfiToolFryingPan(obsidianFryingPanID + 7, (int)((float)oDur * bMod), oDam, oType, bType)).setItemName("bObsidianFryingPan");
        nObsidianFryingPan = (new InfiToolFryingPan(obsidianFryingPanID + 8, (int)((float)oDur * nMod), oDam, oType, nType)).setItemName("nObsidianFryingPan");
        glObsidianFryingPan = (new InfiToolFryingPan(obsidianFryingPanID + 9, (int)((float)oDur * glMod), oDam, oType, glType)).setItemName("glObsidianFryingPan");
        sObsidianFryingPan = (new InfiToolFryingPan(obsidianFryingPanID + 10, (int)((float)oDur * sMod), oDam, oType, sType)).setItemName("sObsidianFryingPan");
        fObsidianFryingPan = (new InfiToolFryingPan(obsidianFryingPanID + 11, (int)((float)oDur * sMod), oDam, oType, fType)).setItemName("fObsidianFryingPan");
        blObsidianFryingPan = (new InfiToolFryingPan(obsidianFryingPanID + 12, (int)((float)oDur * blMod), oDam, oType, blType)).setItemName("blObsidianFryingPan");
        wSandstoneFryingPan = (new InfiToolFryingPan(sandstoneFryingPanID + 0, (int)((float)saDur * wMod), saDam, saType, wType)).setItemName("wSandstoneFryingPan");
        stSandstoneFryingPan = (new InfiToolFryingPan(sandstoneFryingPanID + 1, (int)((float)saDur * stMod), saDam, saType, stType)).setItemName("stSandstoneFryingPan");
        saSandstoneFryingPan = (new InfiToolFryingPan(sandstoneFryingPanID + 2, (int)((float)saDur * saMod), saDam, saType, saType)).setItemName("saSandstoneFryingPan");
        bSandstoneFryingPan = (new InfiToolFryingPan(sandstoneFryingPanID + 3, (int)((float)saDur * bMod), saDam, saType, bType)).setItemName("bSandstoneFryingPan");
        pSandstoneFryingPan = (new InfiToolFryingPan(sandstoneFryingPanID + 4, (int)((float)saDur * pMod), saDam, saType, pType)).setItemName("pSandstoneFryingPan");
        nSandstoneFryingPan = (new InfiToolFryingPan(sandstoneFryingPanID + 5, (int)((float)saDur * nMod), saDam, saType, nType)).setItemName("nSandstoneFryingPan");
        iceSandstoneFryingPan = (new InfiToolFryingPan(sandstoneFryingPanID + 6, (int)((float)saDur * iceMod), saDam, saType, iceType)).setItemName("iceSandstoneFryingPan");
        sSandstoneFryingPan = (new InfiToolFryingPan(sandstoneFryingPanID + 7, (int)((float)saDur * sMod), saDam, saType, sType)).setItemName("sSandstoneFryingPan");
        cSandstoneFryingPan = (new InfiToolFryingPan(sandstoneFryingPanID + 8, (int)((float)saDur * cMod), saDam, saType, cType)).setItemName("cSandstoneFryingPan");
        fSandstoneFryingPan = (new InfiToolFryingPan(sandstoneFryingPanID + 9, (int)((float)saDur * fMod), saDam, saType, fType)).setItemName("fSandstoneFryingPan");
        brSandstoneFryingPan = (new InfiToolFryingPan(sandstoneFryingPanID + 10, (int)((float)saDur * brMod), saDam, saType, brType)).setItemName("brSandstoneFryingPan");
        wBoneFryingPan = (new InfiToolFryingPan(boneFryingPanID + 0, (int)((float)bDur * wMod), bDam, bType, wType)).setItemName("wBoneFryingPan");
        stBoneFryingPan = (new InfiToolFryingPan(boneFryingPanID + 1, (int)((float)bDur * stMod), bDam, bType, stType)).setItemName("stBoneFryingPan");
        iBoneFryingPan = (new InfiToolFryingPan(boneFryingPanID + 2, (int)((float)bDur * iMod), bDam, bType, iType)).setItemName("iBoneFryingPan");
        dBoneFryingPan = (new InfiToolFryingPan(boneFryingPanID + 3, (int)((float)bDur * iMod), bDam, bType, dType)).setItemName("dBoneFryingPan");
        rBoneFryingPan = (new InfiToolFryingPan(boneFryingPanID + 4, (int)((float)bDur * rMod), bDam, bType, rType)).setItemName("rBoneFryingPan");
        oBoneFryingPan = (new InfiToolFryingPan(boneFryingPanID + 5, (int)((float)bDur * oMod), bDam, bType, oType)).setItemName("oBoneFryingPan");
        bBoneFryingPan = (new InfiToolFryingPan(boneFryingPanID + 6, (int)((float)bDur * bMod), bDam, bType, bType)).setItemName("bBoneFryingPan");
        mBoneFryingPan = (new InfiToolFryingPan(boneFryingPanID + 7, (int)((float)bDur * mMod), bDam, bType, mType)).setItemName("mBoneFryingPan");
        nBoneFryingPan = (new InfiToolFryingPan(boneFryingPanID + 8, (int)((float)bDur * nMod), bDam, bType, nType)).setItemName("nBoneFryingPan");
        glBoneFryingPan = (new InfiToolFryingPan(boneFryingPanID + 9, (int)((float)bDur * gMod), bDam, bType, glType)).setItemName("glBoneFryingPan");
        sBoneFryingPan = (new InfiToolFryingPan(boneFryingPanID + 10, (int)((float)bDur * sMod), bDam, bType, sType)).setItemName("sBoneFryingPan");
        cBoneFryingPan = (new InfiToolFryingPan(boneFryingPanID + 11, (int)((float)bDur * cMod), bDam, bType, cType)).setItemName("cBoneFryingPan");
        fBoneFryingPan = (new InfiToolFryingPan(boneFryingPanID + 12, (int)((float)bDur * fMod), bDam, bType, fType)).setItemName("fBoneFryingPan");
        brBoneFryingPan = (new InfiToolFryingPan(boneFryingPanID + 13, (int)((float)bDur * brMod), bDam, bType, brType)).setItemName("brBoneFryingPan");
        blBoneFryingPan = (new InfiToolFryingPan(boneFryingPanID + 14, (int)((float)bDur * blMod), bDam, bType, blType)).setItemName("blBoneFryingPan");
        wPaperFryingPan = (new InfiToolFryingPan(paperFryingPanID + 0, (int)((float)pDur * wMod), pDam, pType, wType)).setItemName("wPaperFryingPan");
        saPaperFryingPan = (new InfiToolFryingPan(paperFryingPanID + 1, (int)((float)pDur * saMod), pDam, pType, saType)).setItemName("saPaperFryingPan");
        bPaperFryingPan = (new InfiToolFryingPan(paperFryingPanID + 2, (int)((float)pDur * bMod), pDam, pType, bType)).setItemName("bPaperFryingPan");
        pPaperFryingPan = (new InfiToolFryingPan(paperFryingPanID + 3, (int)((float)pDur * pMod), pDam, pType, pType)).setItemName("pPaperFryingPan");
        sPaperFryingPan = (new InfiToolFryingPan(paperFryingPanID + 4, (int)((float)pDur * sMod), pDam, pType, sType)).setItemName("sPaperFryingPan");
        cPaperFryingPan = (new InfiToolFryingPan(paperFryingPanID + 5, (int)((float)pDur * cMod), pDam, pType, cType)).setItemName("cPaperFryingPan");
        brPaperFryingPan = (new InfiToolFryingPan(paperFryingPanID + 6, (int)((float)pDur * brMod), pDam, pType, brType)).setItemName("brPaperFryingPan");
        stMossyFryingPan = (new InfiToolFryingPan(mossyFryingPanID + 0, (int)((float)mDur * stMod), mDam, mType, stType)).setItemName("stMossyFryingPan");
        dMossyFryingPan = (new InfiToolFryingPan(mossyFryingPanID + 1, (int)((float)mDur * iMod), mDam, mType, dType)).setItemName("dMossyFryingPan");
        rMossyFryingPan = (new InfiToolFryingPan(mossyFryingPanID + 2, (int)((float)mDur * rMod), mDam, mType, rType)).setItemName("rMossyFryingPan");
        bMossyFryingPan = (new InfiToolFryingPan(mossyFryingPanID + 3, (int)((float)mDur * bMod), mDam, mType, bType)).setItemName("bMossyFryingPan");
        mMossyFryingPan = (new InfiToolFryingPan(mossyFryingPanID + 4, (int)((float)mDur * mMod), mDam, mType, mType)).setItemName("mMossyFryingPan");
        glMossyFryingPan = (new InfiToolFryingPan(mossyFryingPanID + 5, (int)((float)mDur * gMod), mDam, mType, glType)).setItemName("glMossyFryingPan");
        wNetherrackFryingPan = (new InfiToolFryingPan(netherrackFryingPanID + 0, (int)((float)nDur * wMod), nDam, nType, wType)).setItemName("wNetherrackFryingPan");
        stNetherrackFryingPan = (new InfiToolFryingPan(netherrackFryingPanID + 1, (int)((float)nDur * stMod), nDam, nType, stType)).setItemName("stNetherrackFryingPan");
        iNetherrackFryingPan = (new InfiToolFryingPan(netherrackFryingPanID + 2, (int)((float)nDur * iMod), nDam, nType, iType)).setItemName("iNetherrackFryingPan");
        rNetherrackFryingPan = (new InfiToolFryingPan(netherrackFryingPanID + 3, (int)((float)nDur * rMod), nDam, nType, rType)).setItemName("rNetherrackFryingPan");
        oNetherrackFryingPan = (new InfiToolFryingPan(netherrackFryingPanID + 4, (int)((float)nDur * oMod), nDam, nType, oType)).setItemName("oNetherrackFryingPan");
        saNetherrackFryingPan = (new InfiToolFryingPan(netherrackFryingPanID + 5, (int)((float)nDur * saMod), nDam, nType, saType)).setItemName("saNetherrackFryingPan");
        bNetherrackFryingPan = (new InfiToolFryingPan(netherrackFryingPanID + 6, (int)((float)nDur * bMod), nDam, nType, bType)).setItemName("bNetherrackFryingPan");
        mNetherrackFryingPan = (new InfiToolFryingPan(netherrackFryingPanID + 7, (int)((float)nDur * mMod), nDam, nType, mType)).setItemName("mNetherrackFryingPan");
        nNetherrackFryingPan = (new InfiToolFryingPan(netherrackFryingPanID + 8, (int)((float)nDur * nMod), nDam, nType, nType)).setItemName("nNetherrackFryingPan");
        glNetherrackFryingPan = (new InfiToolFryingPan(netherrackFryingPanID + 9, (int)((float)nDur * glMod), nDam, nType, glType)).setItemName("glNetherrackFryingPan");
        iceNetherrackFryingPan = (new InfiToolFryingPan(netherrackFryingPanID + 10, (int)((float)nDur * iceMod), nDam, nType, iceType)).setItemName("iceNetherrackFryingPan");
        sNetherrackFryingPan = (new InfiToolFryingPan(netherrackFryingPanID + 11, (int)((float)nDur * sMod), nDam, nType, sType)).setItemName("sNetherrackFryingPan");
        cNetherrackFryingPan = (new InfiToolFryingPan(netherrackFryingPanID + 12, (int)((float)nDur * cMod), nDam, nType, cType)).setItemName("cNetherrackFryingPan");
        fNetherrackFryingPan = (new InfiToolFryingPan(netherrackFryingPanID + 13, (int)((float)nDur * fMod), nDam, nType, fType)).setItemName("fNetherrackFryingPan");
        brNetherrackFryingPan = (new InfiToolFryingPan(netherrackFryingPanID + 14, (int)((float)nDur * brMod), nDam, nType, brType)).setItemName("brNetherrackFryingPan");
        blNetherrackFryingPan = (new InfiToolFryingPan(netherrackFryingPanID + 15, (int)((float)nDur * blMod), nDam, nType, blType)).setItemName("blNetherrackFryingPan");
        wGlowstoneFryingPan = (new InfiToolFryingPan(glowstoneFryingPanID + 0, (int)((float)glDur * wMod), glDam, glType, wType)).setItemName("wGlowstoneFryingPan");
        stGlowstoneFryingPan = (new InfiToolFryingPan(glowstoneFryingPanID + 1, (int)((float)glDur * stMod), glDam, glType, stType)).setItemName("stGlowstoneFryingPan");
        iGlowstoneFryingPan = (new InfiToolFryingPan(glowstoneFryingPanID + 2, (int)((float)glDur * iMod), glDam, glType, iType)).setItemName("iGlowstoneFryingPan");
        dGlowstoneFryingPan = (new InfiToolFryingPan(glowstoneFryingPanID + 3, (int)((float)glDur * iMod), glDam, glType, dType)).setItemName("dGlowstoneFryingPan");
        rGlowstoneFryingPan = (new InfiToolFryingPan(glowstoneFryingPanID + 4, (int)((float)glDur * rMod), glDam, glType, rType)).setItemName("rGlowstoneFryingPan");
        oGlowstoneFryingPan = (new InfiToolFryingPan(glowstoneFryingPanID + 5, (int)((float)glDur * oMod), glDam, glType, oType)).setItemName("oGlowstoneFryingPan");
        bGlowstoneFryingPan = (new InfiToolFryingPan(glowstoneFryingPanID + 6, (int)((float)glDur * bMod), glDam, glType, bType)).setItemName("bGlowstoneFryingPan");
        mGlowstoneFryingPan = (new InfiToolFryingPan(glowstoneFryingPanID + 7, (int)((float)glDur * mMod), glDam, glType, mType)).setItemName("mGlowstoneFryingPan");
        nGlowstoneFryingPan = (new InfiToolFryingPan(glowstoneFryingPanID + 8, (int)((float)glDur * nMod), glDam, glType, nType)).setItemName("nGlowstoneFryingPan");
        glGlowstoneFryingPan = (new InfiToolFryingPan(glowstoneFryingPanID + 9, (int)((float)glDur * gMod), glDam, glType, glType)).setItemName("glGlowstoneFryingPan");
        iceGlowstoneFryingPan = (new InfiToolFryingPan(glowstoneFryingPanID + 10, (int)((float)glDur * iceMod), glDam, glType, iceType)).setItemName("iceGlowstoneFryingPan");
        lGlowstoneFryingPan = (new InfiToolFryingPan(glowstoneFryingPanID + 11, (int)((float)glDur * gMod), glDam, glType, lType)).setItemName("lGlowstoneFryingPan");
        sGlowstoneFryingPan = (new InfiToolFryingPan(glowstoneFryingPanID + 12, (int)((float)glDur * sMod), glDam, glType, sType)).setItemName("sGlowstoneFryingPan");
        blGlowstoneFryingPan = (new InfiToolFryingPan(glowstoneFryingPanID + 13, (int)((float)glDur * blMod), glDam, glType, blType)).setItemName("blGlowstoneFryingPan");
        wIceFryingPan = (new InfiToolFryingPan(iceFryingPanID + 0, (int)((float)iceDur * wMod), iceDam, iceType, wType)).setItemName("wIceFryingPan");
        stIceFryingPan = (new InfiToolFryingPan(iceFryingPanID + 1, (int)((float)iceDur * stMod), iceDam, iceType, stType)).setItemName("stIceFryingPan");
        iIceFryingPan = (new InfiToolFryingPan(iceFryingPanID + 2, (int)((float)iceDur * iMod), iceDam, iceType, iType)).setItemName("iIceFryingPan");
        dIceFryingPan = (new InfiToolFryingPan(iceFryingPanID + 3, (int)((float)iceDur * dMod), iceDam, iceType, dType)).setItemName("dIceFryingPan");
        gIceFryingPan = (new InfiToolFryingPan(iceFryingPanID + 4, (int)((float)iceDur * gMod), iceDam, iceType, gType)).setItemName("gIceFryingPan");
        rIceFryingPan = (new InfiToolFryingPan(iceFryingPanID + 5, (int)((float)iceDur * rMod), iceDam, iceType, rType)).setItemName("rIceFryingPan");
        oIceFryingPan = (new InfiToolFryingPan(iceFryingPanID + 6, (int)((float)iceDur * oMod), iceDam, iceType, oType)).setItemName("oIceFryingPan");
        saIceFryingPan = (new InfiToolFryingPan(iceFryingPanID + 7, (int)((float)iceDur * saMod), iceDam, iceType, saType)).setItemName("saIceFryingPan");
        bIceFryingPan = (new InfiToolFryingPan(iceFryingPanID + 8, (int)((float)iceDur * bMod), iceDam, iceType, bType)).setItemName("bIceFryingPan");
        glIceFryingPan = (new InfiToolFryingPan(iceFryingPanID + 9, (int)((float)iceDur * gMod), iceDam, iceType, glType)).setItemName("glIceFryingPan");
        iceIceFryingPan = (new InfiToolFryingPan(iceFryingPanID + 10, (int)((float)iceDur * iMod), iceDam, iceType, iceType)).setItemName("iceIceFryingPan");
        sIceFryingPan = (new InfiToolFryingPan(iceFryingPanID + 11, (int)((float)iceDur * sMod), iceDam, iceType, sType)).setItemName("sIceFryingPan");
        cIceFryingPan = (new InfiToolFryingPan(iceFryingPanID + 12, (int)((float)iceDur * cMod), iceDam, iceType, cType)).setItemName("cIceFryingPan");
        fIceFryingPan = (new InfiToolFryingPan(iceFryingPanID + 13, (int)((float)iceDur * fMod), iceDam, iceType, fType)).setItemName("fIceFryingPan");
        brIceFryingPan = (new InfiToolFryingPan(iceFryingPanID + 14, (int)((float)iceDur * brMod), iceDam, iceType, brType)).setItemName("brIceFryingPan");
        dLavaFryingPan = (new InfiToolFryingPan(lavaFryingPanID + 0, (int)((float)lDur * iMod), lDam, lType, dType)).setItemName("dLavaFryingPan");
        rLavaFryingPan = (new InfiToolFryingPan(lavaFryingPanID + 1, (int)((float)lDur * rMod), lDam, lType, rType)).setItemName("rLavaFryingPan");
        bLavaFryingPan = (new InfiToolFryingPan(lavaFryingPanID + 2, (int)((float)lDur * bMod), lDam, lType, bType)).setItemName("bLavaFryingPan");
        nLavaFryingPan = (new InfiToolFryingPan(lavaFryingPanID + 3, (int)((float)lDur * nMod), lDam, lType, nType)).setItemName("nLavaFryingPan");
        glLavaFryingPan = (new InfiToolFryingPan(lavaFryingPanID + 4, (int)((float)lDur * gMod), lDam, lType, glType)).setItemName("glLavaFryingPan");
        lLavaFryingPan = (new InfiToolFryingPan(lavaFryingPanID + 5, (int)((float)lDur * lMod), lDam, lType, lType)).setItemName("lLavaFryingPan");
        blLavaFryingPan = (new InfiToolFryingPan(lavaFryingPanID + 6, (int)((float)lDur * blMod), lDam, lType, blType)).setItemName("blLavaFryingPan");
        wSlimeFryingPan = (new InfiToolFryingPan(slimeFryingPanID + 0, (int)((float)sDur * wMod), sDam, sType, wType)).setItemName("wSlimeFryingPan");
        stSlimeFryingPan = (new InfiToolFryingPan(slimeFryingPanID + 1, (int)((float)sDur * stMod), sDam, sType, stType)).setItemName("stSlimeFryingPan");
        iSlimeFryingPan = (new InfiToolFryingPan(slimeFryingPanID + 2, (int)((float)sDur * iMod), sDam, sType, iType)).setItemName("iSlimeFryingPan");
        dSlimeFryingPan = (new InfiToolFryingPan(slimeFryingPanID + 3, (int)((float)sDur * iMod), sDam, sType, dType)).setItemName("dSlimeFryingPan");
        gSlimeFryingPan = (new InfiToolFryingPan(slimeFryingPanID + 4, (int)((float)sDur * gMod), sDam, sType, gType)).setItemName("gSlimeFryingPan");
        rSlimeFryingPan = (new InfiToolFryingPan(slimeFryingPanID + 5, (int)((float)sDur * rMod), sDam, sType, rType)).setItemName("rSlimeFryingPan");
        oSlimeFryingPan = (new InfiToolFryingPan(slimeFryingPanID + 6, (int)((float)sDur * oMod), sDam, sType, oType)).setItemName("oSlimeFryingPan");
        saSlimeFryingPan = (new InfiToolFryingPan(slimeFryingPanID + 7, (int)((float)sDur * saMod), sDam, sType, saType)).setItemName("saSlimeFryingPan");
        bSlimeFryingPan = (new InfiToolFryingPan(slimeFryingPanID + 8, (int)((float)sDur * bMod), sDam, sType, bType)).setItemName("bSlimeFryingPan");
        pSlimeFryingPan = (new InfiToolFryingPan(slimeFryingPanID + 9, (int)((float)sDur * pMod), sDam, sType, pType)).setItemName("pSlimeFryingPan");
        mSlimeFryingPan = (new InfiToolFryingPan(slimeFryingPanID + 10, (int)((float)sDur * mMod), sDam, sType, mType)).setItemName("mSlimeFryingPan");
        nSlimeFryingPan = (new InfiToolFryingPan(slimeFryingPanID + 11, (int)((float)sDur * nMod), sDam, sType, nType)).setItemName("nSlimeFryingPan");
        glSlimeFryingPan = (new InfiToolFryingPan(slimeFryingPanID + 12, (int)((float)sDur * gMod), sDam, sType, glType)).setItemName("glSlimeFryingPan");
        iceSlimeFryingPan = (new InfiToolFryingPan(slimeFryingPanID + 13, (int)((float)sDur * iMod), sDam, sType, iceType)).setItemName("iceSlimeFryingPan");
        lSlimeFryingPan = (new InfiToolFryingPan(slimeFryingPanID + 14, (int)((float)sDur * lMod), sDam, sType, lType)).setItemName("lSlimeFryingPan");
        sSlimeFryingPan = (new InfiToolFryingPan(slimeFryingPanID + 15, (int)((float)sDur * sMod), sDam, sType, sType)).setItemName("sSlimeFryingPan");
        cSlimeFryingPan = (new InfiToolFryingPan(slimeFryingPanID + 16, (int)((float)sDur * cMod), sDam, sType, cType)).setItemName("cSlimeFryingPan");
        fSlimeFryingPan = (new InfiToolFryingPan(slimeFryingPanID + 17, (int)((float)sDur * fMod), sDam, sType, fType)).setItemName("fSlimeFryingPan");
        brSlimeFryingPan = (new InfiToolFryingPan(slimeFryingPanID + 18, (int)((float)sDur * brMod), sDam, sType, brType)).setItemName("brSlimeFryingPan");
        blSlimeFryingPan = (new InfiToolFryingPan(slimeFryingPanID + 19, (int)((float)sDur * blMod), sDam, sType, blType)).setItemName("blSlimeFryingPan");
        wCactusFryingPan = (new InfiToolFryingPan(cactusFryingPanID + 0, (int)((float)cDur * wMod), cDam, cType, wType)).setItemName("wCactusFryingPan");
        stCactusFryingPan = (new InfiToolFryingPan(cactusFryingPanID + 1, (int)((float)cDur * stMod), cDam, cType, stType)).setItemName("stCactusFryingPan");
        saCactusFryingPan = (new InfiToolFryingPan(cactusFryingPanID + 2, (int)((float)cDur * saMod), cDam, cType, saType)).setItemName("saCactusFryingPan");
        bCactusFryingPan = (new InfiToolFryingPan(cactusFryingPanID + 3, (int)((float)cDur * bMod), cDam, cType, bType)).setItemName("bCactusFryingPan");
        pCactusFryingPan = (new InfiToolFryingPan(cactusFryingPanID + 4, (int)((float)cDur * pMod), cDam, cType, pType)).setItemName("pCactusFryingPan");
        nCactusFryingPan = (new InfiToolFryingPan(cactusFryingPanID + 5, (int)((float)cDur * nMod), cDam, cType, nType)).setItemName("nCactusFryingPan");
        sCactusFryingPan = (new InfiToolFryingPan(cactusFryingPanID + 6, (int)((float)cDur * sMod), cDam, cType, sType)).setItemName("sCactusFryingPan");
        cCactusFryingPan = (new InfiToolFryingPan(cactusFryingPanID + 7, (int)((float)cDur * cMod), cDam, cType, cType)).setItemName("cCactusFryingPan");
        fCactusFryingPan = (new InfiToolFryingPan(cactusFryingPanID + 8, (int)((float)cDur * fMod), cDam, cType, fType)).setItemName("fCactusFryingPan");
        brCactusFryingPan = (new InfiToolFryingPan(cactusFryingPanID + 9, (int)((float)cDur * brMod), cDam, cType, brType)).setItemName("brCactusFryingPan");
        wFlintFryingPan = (new InfiToolFryingPan(flintFryingPanID + 0, (int)((float)fDur * wMod), fDam, fType, wType)).setItemName("wFlintFryingPan");
        stFlintFryingPan = (new InfiToolFryingPan(flintFryingPanID + 1, (int)((float)fDur * stMod), fDam, fType, stType)).setItemName("stFlintFryingPan");
        iFlintFryingPan = (new InfiToolFryingPan(flintFryingPanID + 2, (int)((float)fDur * iMod), fDam, fType, iType)).setItemName("iFlintFryingPan");
        gFlintFryingPan = (new InfiToolFryingPan(flintFryingPanID + 3, (int)((float)fDur * gMod), fDam, fType, gType)).setItemName("gFlintFryingPan");
        oFlintFryingPan = (new InfiToolFryingPan(flintFryingPanID + 4, (int)((float)fDur * oMod), fDam, fType, oType)).setItemName("oFlintFryingPan");
        saFlintFryingPan = (new InfiToolFryingPan(flintFryingPanID + 5, (int)((float)fDur * saMod), fDam, fType, saType)).setItemName("saFlintFryingPan");
        bFlintFryingPan = (new InfiToolFryingPan(flintFryingPanID + 6, (int)((float)fDur * bMod), fDam, fType, bType)).setItemName("bFlintFryingPan");
        nFlintFryingPan = (new InfiToolFryingPan(flintFryingPanID + 7, (int)((float)fDur * nMod), fDam, fType, nType)).setItemName("nFlintFryingPan");
        iceFlintFryingPan = (new InfiToolFryingPan(flintFryingPanID + 8, (int)((float)fDur * wMod), fDam, fType, iceType)).setItemName("iceFlintFryingPan");
        sFlintFryingPan = (new InfiToolFryingPan(flintFryingPanID + 9, (int)((float)fDur * sMod), fDam, fType, sType)).setItemName("sFlintFryingPan");
        cFlintFryingPan = (new InfiToolFryingPan(flintFryingPanID + 10, (int)((float)fDur * cMod), fDam, fType, cType)).setItemName("cFlintFryingPan");
        fFlintFryingPan = (new InfiToolFryingPan(flintFryingPanID + 11, (int)((float)fDur * fMod), fDam, fType, fType)).setItemName("fFlintFryingPan");
        brFlintFryingPan = (new InfiToolFryingPan(flintFryingPanID + 12, (int)((float)fDur * brMod), fDam, fType, brType)).setItemName("brFlintFryingPan");
        blFlintFryingPan = (new InfiToolFryingPan(flintFryingPanID + 13, (int)((float)fDur * blMod), fDam, fType, blType)).setItemName("blFlintFryingPan");
        wBrickFryingPan = (new InfiToolFryingPan(brickFryingPanID + 0, (int)((float)brDur * wMod), brDam, brType, wType)).setItemName("wBrickFryingPan");
        stBrickFryingPan = (new InfiToolFryingPan(brickFryingPanID + 1, (int)((float)brDur * stMod), brDam, brType, stType)).setItemName("stBrickFryingPan");
        saBrickFryingPan = (new InfiToolFryingPan(brickFryingPanID + 2, (int)((float)brDur * saMod), brDam, brType, saType)).setItemName("saBrickFryingPan");
        bBrickFryingPan = (new InfiToolFryingPan(brickFryingPanID + 3, (int)((float)brDur * bMod), brDam, brType, bType)).setItemName("bBrickFryingPan");
        pBrickFryingPan = (new InfiToolFryingPan(brickFryingPanID + 4, (int)((float)brDur * pMod), brDam, brType, pType)).setItemName("pBrickFryingPan");
        nBrickFryingPan = (new InfiToolFryingPan(brickFryingPanID + 5, (int)((float)brDur * nMod), brDam, brType, nType)).setItemName("nBrickFryingPan");
        iceBrickFryingPan = (new InfiToolFryingPan(brickFryingPanID + 6, (int)((float)brDur * iceMod), brDam, brType, iceType)).setItemName("iceBrickFryingPan");
        sBrickFryingPan = (new InfiToolFryingPan(brickFryingPanID + 7, (int)((float)brDur * sMod), brDam, brType, sType)).setItemName("sBrickFryingPan");
        cBrickFryingPan = (new InfiToolFryingPan(brickFryingPanID + 8, (int)((float)brDur * cMod), brDam, brType, cType)).setItemName("cBrickFryingPan");
        fBrickFryingPan = (new InfiToolFryingPan(brickFryingPanID + 9, (int)((float)brDur * fMod), brDam, brType, fType)).setItemName("fBrickFryingPan");
        brBrickFryingPan = (new InfiToolFryingPan(brickFryingPanID + 10, (int)((float)brDur * brMod), brDam, brType, brType)).setItemName("brBrickFryingPan");
        dBlazeFryingPan = (new InfiToolFryingPan(blazeFryingPanID + 0, (int)((float)blDur * dMod), blDam, blType, dType)).setItemName("wBlazeFryingPan");
        rBlazeFryingPan = (new InfiToolFryingPan(blazeFryingPanID + 1, (int)((float)blDur * rMod), blDam, blType, rType)).setItemName("rBlazeFryingPan");
        bBlazeFryingPan = (new InfiToolFryingPan(blazeFryingPanID + 2, (int)((float)blDur * bMod), blDam, blType, bType)).setItemName("bBlazeFryingPan");
        nBlazeFryingPan = (new InfiToolFryingPan(blazeFryingPanID + 3, (int)((float)blDur * nMod), blDam, blType, nType)).setItemName("nBlazeFryingPan");
        glBlazeFryingPan = (new InfiToolFryingPan(blazeFryingPanID + 4, (int)((float)blDur * glMod), blDam, blType, glType)).setItemName("glBlazeFryingPan");
        lBlazeFryingPan = (new InfiToolFryingPan(blazeFryingPanID + 5, (int)((float)blDur * lMod), blDam, blType, lType)).setItemName("lBlazeFryingPan");
        fBlazeFryingPan = (new InfiToolFryingPan(blazeFryingPanID + 6, (int)((float)blDur * fMod), blDam, blType, fType)).setItemName("fBlazeFryingPan");
        blBlazeFryingPan = (new InfiToolFryingPan(blazeFryingPanID + 7, (int)((float)blDur * bMod), blDam, blType, bType)).setItemName("blBlazeFryingPan");
    }
}
