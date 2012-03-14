package net.minecraft.src;

import net.minecraft.src.EEProxy;
import net.minecraft.src.core.*;
import net.minecraft.src.flora.CactusJuice;
import net.minecraft.src.forge.*;

import java.io.File;
import java.io.PrintStream;
import java.util.Random;

import net.minecraft.server.MinecraftServer;

public class mod_InfiTools extends BaseModMp
{
    public static int blockMossID;
    public static int rodID;
    public static int paperMaterialsID;
    public static int metalChunksID;
    public static int mossID;
    public static int crystalID;
    public static int woodBucketID;
    public static int cactusBucketID;
    public static int goldBucketID;
    public static int iceBucketID;
    public static int lavaBucketID;
    public static int slimeBucketID;
    public static int ironBucketID;
    public static int bowlID;
    public static int materialShardID;
    public static int pumpkinPieID;
    public static int woodSwordID;
    public static int stoneSwordID;
    public static int ironSwordID;
    public static int diamondSwordID;
    public static int goldSwordID;
    public static int redstoneSwordID;
    public static int obsidianSwordID;
    public static int sandstoneSwordID;
    public static int boneSwordID;
    public static int paperSwordID;
    public static int mossySwordID;
    public static int netherrackSwordID;
    public static int glowstoneSwordID;
    public static int iceSwordID;
    public static int lavaSwordID;
    public static int slimeSwordID;
    public static int cactusSwordID;
    public static int flintSwordID;
    public static int brickSwordID;
    public static int blazeSwordID;
    public static int woodPickaxeID;
    public static int stonePickaxeID;
    public static int ironPickaxeID;
    public static int diamondPickaxeID;
    public static int goldPickaxeID;
    public static int redstonePickaxeID;
    public static int obsidianPickaxeID;
    public static int sandstonePickaxeID;
    public static int bonePickaxeID;
    public static int paperPickaxeID;
    public static int mossyPickaxeID;
    public static int netherrackPickaxeID;
    public static int glowstonePickaxeID;
    public static int icePickaxeID;
    public static int lavaPickaxeID;
    public static int slimePickaxeID;
    public static int cactusPickaxeID;
    public static int flintPickaxeID;
    public static int brickPickaxeID;
    public static int blazePickaxeID;
    public static int woodShovelID;
    public static int stoneShovelID;
    public static int ironShovelID;
    public static int diamondShovelID;
    public static int goldShovelID;
    public static int redstoneShovelID;
    public static int obsidianShovelID;
    public static int sandstoneShovelID;
    public static int boneShovelID;
    public static int paperShovelID;
    public static int mossyShovelID;
    public static int netherrackShovelID;
    public static int glowstoneShovelID;
    public static int iceShovelID;
    public static int lavaShovelID;
    public static int slimeShovelID;
    public static int cactusShovelID;
    public static int flintShovelID;
    public static int brickShovelID;
    public static int blazeShovelID;
    public static int woodAxeID;
    public static int stoneAxeID;
    public static int ironAxeID;
    public static int diamondAxeID;
    public static int goldAxeID;
    public static int redstoneAxeID;
    public static int obsidianAxeID;
    public static int sandstoneAxeID;
    public static int boneAxeID;
    public static int paperAxeID;
    public static int mossyAxeID;
    public static int netherrackAxeID;
    public static int glowstoneAxeID;
    public static int iceAxeID;
    public static int lavaAxeID;
    public static int slimeAxeID;
    public static int cactusAxeID;
    public static int flintAxeID;
    public static int brickAxeID;
    public static int blazeAxeID;
    public static int woodHoeID;
    public static int stoneHoeID;
    public static int ironHoeID;
    public static int diamondHoeID;
    public static int goldHoeID;
    public static int redstoneHoeID;
    public static int obsidianHoeID;
    public static int sandstoneHoeID;
    public static int boneHoeID;
    public static int paperHoeID;
    public static int mossyHoeID;
    public static int netherrackHoeID;
    public static int glowstoneHoeID;
    public static int iceHoeID;
    public static int lavaHoeID;
    public static int slimeHoeID;
    public static int cactusHoeID;
    public static int flintHoeID;
    public static int brickHoeID;
    public static int blazeHoeID;
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
    public static Block blockMoss;
    public static Item pumpkinPulp;
    public static Item woodBucketEmpty;
    public static Item woodBucketWater;
    public static Item woodBucketMilk;
    public static Item woodBucketSand;
    public static Item woodBucketGravel;
    public static Item cactusBucketEmpty;
    public static Item cactusBucketWater;
    public static Item cactusBucketMilk;
    public static Item cactusBucketSand;
    public static Item cactusBucketGravel;
    public static Item goldBucketEmpty;
    public static Item goldBucketWater;
    public static Item goldBucketMilk;
    public static Item goldBucketSand;
    public static Item goldBucketGravel;
    public static Item goldBucketLava;
    public static Item iceBucketEmpty;
    public static Item iceBucketIce;
    public static Item iceBucketMilk;
    public static Item iceBucketSand;
    public static Item iceBucketGravel;
    public static Item lavaBucketEmpty;
    public static Item lavaBucketGlass;
    public static Item lavaBucketCobblestone;
    public static Item lavaBucketLava;
    public static Item slimeBucketEmpty;
    public static Item slimeBucketWater;
    public static Item slimeBucketMilk;
    public static Item slimeBucketSand;
    public static Item slimeBucketGravel;
    public static Item ironBucketSand;
    public static Item ironBucketGravel;
    public static Item paperStack;
    public static Item paperDust;
    public static Item ironChunks;
    public static Item goldChunks;
    public static Item mossBall;
    public static Item mossBallGiant;
    public static Item mossBallCrafted;
    public static Item redstoneCrystal;
    public static Item glowstoneCrystal;
    public static Item lavaCrystal;
    public static Item slimeCrystal;
    public static Item obsidianCrystal;
    public static Item blazeCrystal;
    public static Item woodSplinters;
    public static Item stoneShard;
    public static Item diamondShard;
    public static Item redstoneFragment;
    public static Item obsidianShard;
    public static Item sandstoneShard;
    public static Item netherrackShard;
    public static Item glowstoneFragment;
    public static Item iceShard;
    public static Item lavaFragment;
    public static Item slimeFragment;
    public static Item glassShard;
    public static Item coalBits;
    public static Item flintShard;
    public static Item miniBrick;
    public static Item blazeFragment;
    
    public static Item stoneRod;
    public static Item ironRod;
    public static Item diamondRod;
    public static Item goldRod;
    public static Item redstoneRod;
    public static Item obsidianRod;
    public static Item sandstoneRod;
    public static Item boneRod;
    public static Item paperRod;
    public static Item mossyRod;
    public static Item netherrackRod;
    public static Item glowstoneRod;
    public static Item iceRod;
    public static Item lavaRod;
    public static Item slimeRod;
    public static Item cactusRod;
    public static Item flintRod;
    public static Item brickRod;
    public static Item stoneBowlEmpty;
    public static Item stoneBowlSoup;
    public static Item ironBowlEmpty;
    public static Item ironBowlSoup;
    public static Item goldBowlEmpty;
    public static Item goldBowlSoup;
    public static Item netherrackBowlEmpty;
    public static Item netherrackBowlSoup;
    public static Item slimeBowlEmpty;
    public static Item slimeBowlSoup;
    public static Item cactusBowlEmpty;
    public static Item cactusBowlSoup;
    public static Item glassBowlEmpty;
    public static Item glassBowlSoup;
    public static Item woodBowlRawPumpkinPie;
    public static Item woodBowlPumpkinPie;
    public static Item stoneBowlRawPumpkinPie;
    public static Item stoneBowlPumpkinPie;
    public static Item ironBowlRawPumpkinPie;
    public static Item ironBowlPumpkinPie;
    public static Item goldBowlRawPumpkinPie;
    public static Item goldBowlPumpkinPie;
    public static Item netherrackBowlRawPumpkinPie;
    public static Item netherrackBowlPumpkinPie;
    public static Item slimeBowlRawPumpkinPie;
    public static Item slimeBowlPumpkinPie;
    public static Item cactusBowlRawPumpkinPie;
    public static Item cactusBowlPumpkinPie;
    public static Item glassBowlRawPumpkinPie;
    public static Item glassBowlPumpkinPie;
    
    public static Item stWoodSword;
    public static Item saWoodSword;
    public static Item bWoodSword;
    public static Item pWoodSword;
    public static Item nWoodSword;
    public static Item sWoodSword;
    public static Item cWoodSword;
    public static Item fWoodSword;
    public static Item brWoodSword;
    public static Item stStoneSword;
    public static Item saStoneSword;
    public static Item bStoneSword;
    public static Item pStoneSword;
    public static Item mStoneSword;
    public static Item nStoneSword;
    public static Item iceStoneSword;
    public static Item sStoneSword;
    public static Item cStoneSword;
    public static Item fStoneSword;
    public static Item brStoneSword;
    public static Item stIronSword;
    public static Item iIronSword;
    public static Item dIronSword;
    public static Item gIronSword;
    public static Item rIronSword;
    public static Item oIronSword;
    public static Item bIronSword;
    public static Item nIronSword;
    public static Item glIronSword;
    public static Item iceIronSword;
    public static Item sIronSword;
    public static Item blIronSword;
    public static Item stDiamondSword;
    public static Item iDiamondSword;
    public static Item dDiamondSword;
    public static Item gDiamondSword;
    public static Item rDiamondSword;
    public static Item oDiamondSword;
    public static Item bDiamondSword;
    public static Item mDiamondSword;
    public static Item nDiamondSword;
    public static Item glDiamondSword;
    public static Item blDiamondSword;
    public static Item stGoldSword;
    public static Item gGoldSword;
    public static Item oGoldSword;
    public static Item saGoldSword;
    public static Item bGoldSword;
    public static Item mGoldSword;
    public static Item nGoldSword;
    public static Item glGoldSword;
    public static Item iceGoldSword;
    public static Item sGoldSword;
    public static Item cGoldSword;
    public static Item fGoldSword;
    public static Item wRedstoneSword;
    public static Item stRedstoneSword;
    public static Item iRedstoneSword;
    public static Item dRedstoneSword;
    public static Item rRedstoneSword;
    public static Item oRedstoneSword;
    public static Item bRedstoneSword;
    public static Item mRedstoneSword;
    public static Item glRedstoneSword;
    public static Item sRedstoneSword;
    public static Item blRedstoneSword;
    public static Item wObsidianSword;
    public static Item stObsidianSword;
    public static Item iObsidianSword;
    public static Item dObsidianSword;
    public static Item gObsidianSword;
    public static Item rObsidianSword;
    public static Item oObsidianSword;
    public static Item bObsidianSword;
    public static Item nObsidianSword;
    public static Item glObsidianSword;
    public static Item sObsidianSword;
    public static Item fObsidianSword;
    public static Item blObsidianSword;
    public static Item wSandstoneSword;
    public static Item stSandstoneSword;
    public static Item oSandstoneSword;
    public static Item saSandstoneSword;
    public static Item bSandstoneSword;
    public static Item pSandstoneSword;
    public static Item nSandstoneSword;
    public static Item iceSandstoneSword;
    public static Item sSandstoneSword;
    public static Item cSandstoneSword;
    public static Item fSandstoneSword;
    public static Item brSandstoneSword;
    public static Item wBoneSword;
    public static Item stBoneSword;
    public static Item iBoneSword;
    public static Item dBoneSword;
    public static Item rBoneSword;
    public static Item oBoneSword;
    public static Item bBoneSword;
    public static Item mBoneSword;
    public static Item nBoneSword;
    public static Item glBoneSword;
    public static Item sBoneSword;
    public static Item cBoneSword;
    public static Item fBoneSword;
    public static Item brBoneSword;
    public static Item blBoneSword;
    public static Item wPaperSword;
    public static Item saPaperSword;
    public static Item bPaperSword;
    public static Item pPaperSword;
    public static Item sPaperSword;
    public static Item cPaperSword;
    public static Item brPaperSword;
    public static Item stMossySword;
    public static Item dMossySword;
    public static Item rMossySword;
    public static Item bMossySword;
    public static Item mMossySword;
    public static Item glMossySword;
    public static Item wNetherrackSword;
    public static Item stNetherrackSword;
    public static Item iNetherrackSword;
    public static Item rNetherrackSword;
    public static Item oNetherrackSword;
    public static Item saNetherrackSword;
    public static Item bNetherrackSword;
    public static Item mNetherrackSword;
    public static Item nNetherrackSword;
    public static Item glNetherrackSword;
    public static Item iceNetherrackSword;
    public static Item sNetherrackSword;
    public static Item cNetherrackSword;
    public static Item fNetherrackSword;
    public static Item brNetherrackSword;
    public static Item blNetherrackSword;
    public static Item wGlowstoneSword;
    public static Item stGlowstoneSword;
    public static Item iGlowstoneSword;
    public static Item dGlowstoneSword;
    public static Item rGlowstoneSword;
    public static Item oGlowstoneSword;
    public static Item bGlowstoneSword;
    public static Item mGlowstoneSword;
    public static Item nGlowstoneSword;
    public static Item glGlowstoneSword;
    public static Item iceGlowstoneSword;
    public static Item lGlowstoneSword;
    public static Item sGlowstoneSword;
    public static Item blGlowstoneSword;
    public static Item wIceSword;
    public static Item stIceSword;
    public static Item iIceSword;
    public static Item dIceSword;
    public static Item gIceSword;
    public static Item rIceSword;
    public static Item oIceSword;
    public static Item saIceSword;
    public static Item bIceSword;
    public static Item glIceSword;
    public static Item iceIceSword;
    public static Item sIceSword;
    public static Item cIceSword;
    public static Item fIceSword;
    public static Item brIceSword;
    public static Item dLavaSword;
    public static Item rLavaSword;
    public static Item bLavaSword;
    public static Item nLavaSword;
    public static Item glLavaSword;
    public static Item lLavaSword;
    public static Item blLavaSword;
    public static Item wSlimeSword;
    public static Item stSlimeSword;
    public static Item iSlimeSword;
    public static Item dSlimeSword;
    public static Item gSlimeSword;
    public static Item rSlimeSword;
    public static Item oSlimeSword;
    public static Item saSlimeSword;
    public static Item bSlimeSword;
    public static Item pSlimeSword;
    public static Item mSlimeSword;
    public static Item nSlimeSword;
    public static Item glSlimeSword;
    public static Item iceSlimeSword;
    public static Item lSlimeSword;
    public static Item sSlimeSword;
    public static Item cSlimeSword;
    public static Item fSlimeSword;
    public static Item brSlimeSword;
    public static Item blSlimeSword;
    public static Item wCactusSword;
    public static Item stCactusSword;
    public static Item saCactusSword;
    public static Item bCactusSword;
    public static Item pCactusSword;
    public static Item nCactusSword;
    public static Item sCactusSword;
    public static Item cCactusSword;
    public static Item fCactusSword;
    public static Item brCactusSword;
    public static Item wFlintSword;
    public static Item stFlintSword;
    public static Item iFlintSword;
    public static Item gFlintSword;
    public static Item oFlintSword;
    public static Item saFlintSword;
    public static Item bFlintSword;
    public static Item nFlintSword;
    public static Item iceFlintSword;
    public static Item sFlintSword;
    public static Item cFlintSword;
    public static Item fFlintSword;
    public static Item brFlintSword;
    public static Item blFlintSword;
    public static Item wBrickSword;
    public static Item stBrickSword;
    public static Item saBrickSword;
    public static Item bBrickSword;
    public static Item pBrickSword;
    public static Item mBrickSword;
    public static Item nBrickSword;
    public static Item iceBrickSword;
    public static Item sBrickSword;
    public static Item cBrickSword;
    public static Item fBrickSword;
    public static Item brBrickSword;
    public static Item dBlazeSword;
    public static Item rBlazeSword;
    public static Item bBlazeSword;
    public static Item nBlazeSword;
    public static Item glBlazeSword;
    public static Item lBlazeSword;
    public static Item fBlazeSword;
    public static Item blBlazeSword;
    public static Item stWoodPickaxe;
    public static Item saWoodPickaxe;
    public static Item bWoodPickaxe;
    public static Item pWoodPickaxe;
    public static Item nWoodPickaxe;
    public static Item sWoodPickaxe;
    public static Item cWoodPickaxe;
    public static Item fWoodPickaxe;
    public static Item brWoodPickaxe;
    public static Item stStonePickaxe;
    public static Item saStonePickaxe;
    public static Item bStonePickaxe;
    public static Item pStonePickaxe;
    public static Item mStonePickaxe;
    public static Item nStonePickaxe;
    public static Item iceStonePickaxe;
    public static Item sStonePickaxe;
    public static Item cStonePickaxe;
    public static Item fStonePickaxe;
    public static Item brStonePickaxe;
    public static Item stIronPickaxe;
    public static Item iIronPickaxe;
    public static Item dIronPickaxe;
    public static Item gIronPickaxe;
    public static Item rIronPickaxe;
    public static Item oIronPickaxe;
    public static Item bIronPickaxe;
    public static Item nIronPickaxe;
    public static Item glIronPickaxe;
    public static Item iceIronPickaxe;
    public static Item sIronPickaxe;
    public static Item blIronPickaxe;
    public static Item stDiamondPickaxe;
    public static Item iDiamondPickaxe;
    public static Item dDiamondPickaxe;
    public static Item gDiamondPickaxe;
    public static Item rDiamondPickaxe;
    public static Item oDiamondPickaxe;
    public static Item bDiamondPickaxe;
    public static Item mDiamondPickaxe;
    public static Item nDiamondPickaxe;
    public static Item glDiamondPickaxe;
    public static Item blDiamondPickaxe;
    public static Item stGoldPickaxe;
    public static Item gGoldPickaxe;
    public static Item oGoldPickaxe;
    public static Item saGoldPickaxe;
    public static Item bGoldPickaxe;
    public static Item mGoldPickaxe;
    public static Item nGoldPickaxe;
    public static Item glGoldPickaxe;
    public static Item iceGoldPickaxe;
    public static Item sGoldPickaxe;
    public static Item cGoldPickaxe;
    public static Item fGoldPickaxe;
    public static Item wRedstonePickaxe;
    public static Item stRedstonePickaxe;
    public static Item iRedstonePickaxe;
    public static Item dRedstonePickaxe;
    public static Item rRedstonePickaxe;
    public static Item oRedstonePickaxe;
    public static Item bRedstonePickaxe;
    public static Item mRedstonePickaxe;
    public static Item glRedstonePickaxe;
    public static Item sRedstonePickaxe;
    public static Item blRedstonePickaxe;
    public static Item wObsidianPickaxe;
    public static Item stObsidianPickaxe;
    public static Item iObsidianPickaxe;
    public static Item dObsidianPickaxe;
    public static Item gObsidianPickaxe;
    public static Item rObsidianPickaxe;
    public static Item oObsidianPickaxe;
    public static Item bObsidianPickaxe;
    public static Item nObsidianPickaxe;
    public static Item glObsidianPickaxe;
    public static Item sObsidianPickaxe;
    public static Item fObsidianPickaxe;
    public static Item blObsidianPickaxe;
    public static Item wSandstonePickaxe;
    public static Item stSandstonePickaxe;
    public static Item oSandstonePickaxe;
    public static Item saSandstonePickaxe;
    public static Item bSandstonePickaxe;
    public static Item pSandstonePickaxe;
    public static Item nSandstonePickaxe;
    public static Item iceSandstonePickaxe;
    public static Item sSandstonePickaxe;
    public static Item cSandstonePickaxe;
    public static Item fSandstonePickaxe;
    public static Item brSandstonePickaxe;
    public static Item wBonePickaxe;
    public static Item stBonePickaxe;
    public static Item iBonePickaxe;
    public static Item dBonePickaxe;
    public static Item rBonePickaxe;
    public static Item oBonePickaxe;
    public static Item bBonePickaxe;
    public static Item mBonePickaxe;
    public static Item nBonePickaxe;
    public static Item glBonePickaxe;
    public static Item sBonePickaxe;
    public static Item cBonePickaxe;
    public static Item fBonePickaxe;
    public static Item brBonePickaxe;
    public static Item blBonePickaxe;
    public static Item wPaperPickaxe;
    public static Item saPaperPickaxe;
    public static Item bPaperPickaxe;
    public static Item pPaperPickaxe;
    public static Item sPaperPickaxe;
    public static Item cPaperPickaxe;
    public static Item brPaperPickaxe;
    public static Item stMossyPickaxe;
    public static Item dMossyPickaxe;
    public static Item rMossyPickaxe;
    public static Item bMossyPickaxe;
    public static Item mMossyPickaxe;
    public static Item glMossyPickaxe;
    public static Item wNetherrackPickaxe;
    public static Item stNetherrackPickaxe;
    public static Item iNetherrackPickaxe;
    public static Item rNetherrackPickaxe;
    public static Item oNetherrackPickaxe;
    public static Item saNetherrackPickaxe;
    public static Item bNetherrackPickaxe;
    public static Item mNetherrackPickaxe;
    public static Item nNetherrackPickaxe;
    public static Item glNetherrackPickaxe;
    public static Item iceNetherrackPickaxe;
    public static Item sNetherrackPickaxe;
    public static Item cNetherrackPickaxe;
    public static Item fNetherrackPickaxe;
    public static Item brNetherrackPickaxe;
    public static Item blNetherrackPickaxe;
    public static Item wGlowstonePickaxe;
    public static Item stGlowstonePickaxe;
    public static Item iGlowstonePickaxe;
    public static Item dGlowstonePickaxe;
    public static Item rGlowstonePickaxe;
    public static Item oGlowstonePickaxe;
    public static Item bGlowstonePickaxe;
    public static Item mGlowstonePickaxe;
    public static Item nGlowstonePickaxe;
    public static Item glGlowstonePickaxe;
    public static Item iceGlowstonePickaxe;
    public static Item lGlowstonePickaxe;
    public static Item sGlowstonePickaxe;
    public static Item blGlowstonePickaxe;
    public static Item wIcePickaxe;
    public static Item stIcePickaxe;
    public static Item iIcePickaxe;
    public static Item dIcePickaxe;
    public static Item gIcePickaxe;
    public static Item rIcePickaxe;
    public static Item oIcePickaxe;
    public static Item saIcePickaxe;
    public static Item bIcePickaxe;
    public static Item glIcePickaxe;
    public static Item iceIcePickaxe;
    public static Item sIcePickaxe;
    public static Item cIcePickaxe;
    public static Item fIcePickaxe;
    public static Item brIcePickaxe;
    public static Item dLavaPickaxe;
    public static Item rLavaPickaxe;
    public static Item bLavaPickaxe;
    public static Item nLavaPickaxe;
    public static Item glLavaPickaxe;
    public static Item lLavaPickaxe;
    public static Item blLavaPickaxe;
    public static Item wSlimePickaxe;
    public static Item stSlimePickaxe;
    public static Item iSlimePickaxe;
    public static Item dSlimePickaxe;
    public static Item gSlimePickaxe;
    public static Item rSlimePickaxe;
    public static Item oSlimePickaxe;
    public static Item saSlimePickaxe;
    public static Item bSlimePickaxe;
    public static Item pSlimePickaxe;
    public static Item mSlimePickaxe;
    public static Item nSlimePickaxe;
    public static Item glSlimePickaxe;
    public static Item iceSlimePickaxe;
    public static Item lSlimePickaxe;
    public static Item sSlimePickaxe;
    public static Item cSlimePickaxe;
    public static Item fSlimePickaxe;
    public static Item brSlimePickaxe;
    public static Item blSlimePickaxe;
    public static Item wCactusPickaxe;
    public static Item stCactusPickaxe;
    public static Item saCactusPickaxe;
    public static Item bCactusPickaxe;
    public static Item pCactusPickaxe;
    public static Item nCactusPickaxe;
    public static Item sCactusPickaxe;
    public static Item cCactusPickaxe;
    public static Item fCactusPickaxe;
    public static Item brCactusPickaxe;
    public static Item wFlintPickaxe;
    public static Item stFlintPickaxe;
    public static Item iFlintPickaxe;
    public static Item gFlintPickaxe;
    public static Item oFlintPickaxe;
    public static Item saFlintPickaxe;
    public static Item bFlintPickaxe;
    public static Item nFlintPickaxe;
    public static Item iceFlintPickaxe;
    public static Item sFlintPickaxe;
    public static Item cFlintPickaxe;
    public static Item fFlintPickaxe;
    public static Item brFlintPickaxe;
    public static Item blFlintPickaxe;
    public static Item wBrickPickaxe;
    public static Item stBrickPickaxe;
    public static Item saBrickPickaxe;
    public static Item bBrickPickaxe;
    public static Item pBrickPickaxe;
    public static Item mBrickPickaxe;
    public static Item nBrickPickaxe;
    public static Item iceBrickPickaxe;
    public static Item sBrickPickaxe;
    public static Item cBrickPickaxe;
    public static Item fBrickPickaxe;
    public static Item brBrickPickaxe;
    public static Item dBlazePickaxe;
    public static Item rBlazePickaxe;
    public static Item bBlazePickaxe;
    public static Item nBlazePickaxe;
    public static Item glBlazePickaxe;
    public static Item lBlazePickaxe;
    public static Item fBlazePickaxe;
    public static Item blBlazePickaxe;
    public static Item stWoodShovel;
    public static Item saWoodShovel;
    public static Item bWoodShovel;
    public static Item pWoodShovel;
    public static Item nWoodShovel;
    public static Item sWoodShovel;
    public static Item cWoodShovel;
    public static Item fWoodShovel;
    public static Item brWoodShovel;
    public static Item stStoneShovel;
    public static Item saStoneShovel;
    public static Item bStoneShovel;
    public static Item pStoneShovel;
    public static Item mStoneShovel;
    public static Item nStoneShovel;
    public static Item iceStoneShovel;
    public static Item sStoneShovel;
    public static Item cStoneShovel;
    public static Item fStoneShovel;
    public static Item brStoneShovel;
    public static Item stIronShovel;
    public static Item iIronShovel;
    public static Item dIronShovel;
    public static Item gIronShovel;
    public static Item rIronShovel;
    public static Item oIronShovel;
    public static Item bIronShovel;
    public static Item nIronShovel;
    public static Item glIronShovel;
    public static Item iceIronShovel;
    public static Item sIronShovel;
    public static Item blIronShovel;
    public static Item stDiamondShovel;
    public static Item iDiamondShovel;
    public static Item dDiamondShovel;
    public static Item gDiamondShovel;
    public static Item rDiamondShovel;
    public static Item oDiamondShovel;
    public static Item bDiamondShovel;
    public static Item mDiamondShovel;
    public static Item nDiamondShovel;
    public static Item glDiamondShovel;
    public static Item blDiamondShovel;
    public static Item stGoldShovel;
    public static Item gGoldShovel;
    public static Item oGoldShovel;
    public static Item saGoldShovel;
    public static Item bGoldShovel;
    public static Item mGoldShovel;
    public static Item nGoldShovel;
    public static Item glGoldShovel;
    public static Item iceGoldShovel;
    public static Item sGoldShovel;
    public static Item cGoldShovel;
    public static Item fGoldShovel;
    public static Item wRedstoneShovel;
    public static Item stRedstoneShovel;
    public static Item iRedstoneShovel;
    public static Item dRedstoneShovel;
    public static Item rRedstoneShovel;
    public static Item oRedstoneShovel;
    public static Item bRedstoneShovel;
    public static Item mRedstoneShovel;
    public static Item glRedstoneShovel;
    public static Item sRedstoneShovel;
    public static Item blRedstoneShovel;
    public static Item wObsidianShovel;
    public static Item stObsidianShovel;
    public static Item iObsidianShovel;
    public static Item dObsidianShovel;
    public static Item gObsidianShovel;
    public static Item rObsidianShovel;
    public static Item oObsidianShovel;
    public static Item bObsidianShovel;
    public static Item nObsidianShovel;
    public static Item glObsidianShovel;
    public static Item sObsidianShovel;
    public static Item fObsidianShovel;
    public static Item blObsidianShovel;
    public static Item wSandstoneShovel;
    public static Item stSandstoneShovel;
    public static Item oSandstoneShovel;
    public static Item saSandstoneShovel;
    public static Item bSandstoneShovel;
    public static Item pSandstoneShovel;
    public static Item nSandstoneShovel;
    public static Item iceSandstoneShovel;
    public static Item sSandstoneShovel;
    public static Item cSandstoneShovel;
    public static Item fSandstoneShovel;
    public static Item brSandstoneShovel;
    public static Item wBoneShovel;
    public static Item stBoneShovel;
    public static Item iBoneShovel;
    public static Item dBoneShovel;
    public static Item rBoneShovel;
    public static Item oBoneShovel;
    public static Item bBoneShovel;
    public static Item mBoneShovel;
    public static Item nBoneShovel;
    public static Item glBoneShovel;
    public static Item sBoneShovel;
    public static Item cBoneShovel;
    public static Item fBoneShovel;
    public static Item brBoneShovel;
    public static Item blBoneShovel;
    public static Item wPaperShovel;
    public static Item saPaperShovel;
    public static Item bPaperShovel;
    public static Item pPaperShovel;
    public static Item sPaperShovel;
    public static Item cPaperShovel;
    public static Item brPaperShovel;
    public static Item stMossyShovel;
    public static Item dMossyShovel;
    public static Item rMossyShovel;
    public static Item bMossyShovel;
    public static Item mMossyShovel;
    public static Item glMossyShovel;
    public static Item wNetherrackShovel;
    public static Item stNetherrackShovel;
    public static Item iNetherrackShovel;
    public static Item rNetherrackShovel;
    public static Item oNetherrackShovel;
    public static Item saNetherrackShovel;
    public static Item bNetherrackShovel;
    public static Item mNetherrackShovel;
    public static Item nNetherrackShovel;
    public static Item glNetherrackShovel;
    public static Item iceNetherrackShovel;
    public static Item sNetherrackShovel;
    public static Item cNetherrackShovel;
    public static Item fNetherrackShovel;
    public static Item brNetherrackShovel;
    public static Item blNetherrackShovel;
    public static Item wGlowstoneShovel;
    public static Item stGlowstoneShovel;
    public static Item iGlowstoneShovel;
    public static Item dGlowstoneShovel;
    public static Item rGlowstoneShovel;
    public static Item oGlowstoneShovel;
    public static Item bGlowstoneShovel;
    public static Item mGlowstoneShovel;
    public static Item nGlowstoneShovel;
    public static Item glGlowstoneShovel;
    public static Item iceGlowstoneShovel;
    public static Item lGlowstoneShovel;
    public static Item sGlowstoneShovel;
    public static Item blGlowstoneShovel;
    public static Item wIceShovel;
    public static Item stIceShovel;
    public static Item iIceShovel;
    public static Item dIceShovel;
    public static Item gIceShovel;
    public static Item rIceShovel;
    public static Item oIceShovel;
    public static Item saIceShovel;
    public static Item bIceShovel;
    public static Item glIceShovel;
    public static Item iceIceShovel;
    public static Item sIceShovel;
    public static Item cIceShovel;
    public static Item fIceShovel;
    public static Item brIceShovel;
    public static Item dLavaShovel;
    public static Item rLavaShovel;
    public static Item bLavaShovel;
    public static Item nLavaShovel;
    public static Item glLavaShovel;
    public static Item lLavaShovel;
    public static Item blLavaShovel;
    public static Item wSlimeShovel;
    public static Item stSlimeShovel;
    public static Item iSlimeShovel;
    public static Item dSlimeShovel;
    public static Item gSlimeShovel;
    public static Item rSlimeShovel;
    public static Item oSlimeShovel;
    public static Item saSlimeShovel;
    public static Item bSlimeShovel;
    public static Item pSlimeShovel;
    public static Item mSlimeShovel;
    public static Item nSlimeShovel;
    public static Item glSlimeShovel;
    public static Item iceSlimeShovel;
    public static Item lSlimeShovel;
    public static Item sSlimeShovel;
    public static Item cSlimeShovel;
    public static Item fSlimeShovel;
    public static Item brSlimeShovel;
    public static Item blSlimeShovel;
    public static Item wCactusShovel;
    public static Item stCactusShovel;
    public static Item saCactusShovel;
    public static Item bCactusShovel;
    public static Item pCactusShovel;
    public static Item nCactusShovel;
    public static Item sCactusShovel;
    public static Item cCactusShovel;
    public static Item fCactusShovel;
    public static Item brCactusShovel;
    public static Item wFlintShovel;
    public static Item stFlintShovel;
    public static Item iFlintShovel;
    public static Item gFlintShovel;
    public static Item oFlintShovel;
    public static Item saFlintShovel;
    public static Item bFlintShovel;
    public static Item nFlintShovel;
    public static Item iceFlintShovel;
    public static Item sFlintShovel;
    public static Item cFlintShovel;
    public static Item fFlintShovel;
    public static Item brFlintShovel;
    public static Item blFlintShovel;
    public static Item wBrickShovel;
    public static Item stBrickShovel;
    public static Item saBrickShovel;
    public static Item bBrickShovel;
    public static Item pBrickShovel;
    public static Item mBrickShovel;
    public static Item nBrickShovel;
    public static Item iceBrickShovel;
    public static Item sBrickShovel;
    public static Item cBrickShovel;
    public static Item fBrickShovel;
    public static Item brBrickShovel;
    public static Item dBlazeShovel;
    public static Item rBlazeShovel;
    public static Item bBlazeShovel;
    public static Item nBlazeShovel;
    public static Item glBlazeShovel;
    public static Item lBlazeShovel;
    public static Item fBlazeShovel;
    public static Item blBlazeShovel;
    public static Item stWoodAxe;
    public static Item saWoodAxe;
    public static Item bWoodAxe;
    public static Item pWoodAxe;
    public static Item nWoodAxe;
    public static Item sWoodAxe;
    public static Item cWoodAxe;
    public static Item fWoodAxe;
    public static Item brWoodAxe;
    public static Item stStoneAxe;
    public static Item saStoneAxe;
    public static Item bStoneAxe;
    public static Item pStoneAxe;
    public static Item mStoneAxe;
    public static Item nStoneAxe;
    public static Item iceStoneAxe;
    public static Item sStoneAxe;
    public static Item cStoneAxe;
    public static Item fStoneAxe;
    public static Item brStoneAxe;
    public static Item stIronAxe;
    public static Item iIronAxe;
    public static Item dIronAxe;
    public static Item gIronAxe;
    public static Item rIronAxe;
    public static Item oIronAxe;
    public static Item bIronAxe;
    public static Item nIronAxe;
    public static Item glIronAxe;
    public static Item iceIronAxe;
    public static Item sIronAxe;
    public static Item blIronAxe;
    public static Item stDiamondAxe;
    public static Item iDiamondAxe;
    public static Item dDiamondAxe;
    public static Item gDiamondAxe;
    public static Item rDiamondAxe;
    public static Item oDiamondAxe;
    public static Item bDiamondAxe;
    public static Item mDiamondAxe;
    public static Item nDiamondAxe;
    public static Item glDiamondAxe;
    public static Item blDiamondAxe;
    public static Item stGoldAxe;
    public static Item gGoldAxe;
    public static Item oGoldAxe;
    public static Item saGoldAxe;
    public static Item bGoldAxe;
    public static Item mGoldAxe;
    public static Item nGoldAxe;
    public static Item glGoldAxe;
    public static Item iceGoldAxe;
    public static Item sGoldAxe;
    public static Item cGoldAxe;
    public static Item fGoldAxe;
    public static Item wRedstoneAxe;
    public static Item stRedstoneAxe;
    public static Item iRedstoneAxe;
    public static Item dRedstoneAxe;
    public static Item rRedstoneAxe;
    public static Item oRedstoneAxe;
    public static Item bRedstoneAxe;
    public static Item mRedstoneAxe;
    public static Item glRedstoneAxe;
    public static Item sRedstoneAxe;
    public static Item blRedstoneAxe;
    public static Item wObsidianAxe;
    public static Item stObsidianAxe;
    public static Item iObsidianAxe;
    public static Item dObsidianAxe;
    public static Item gObsidianAxe;
    public static Item rObsidianAxe;
    public static Item oObsidianAxe;
    public static Item bObsidianAxe;
    public static Item nObsidianAxe;
    public static Item glObsidianAxe;
    public static Item sObsidianAxe;
    public static Item fObsidianAxe;
    public static Item blObsidianAxe;
    public static Item wSandstoneAxe;
    public static Item stSandstoneAxe;
    public static Item oSandstoneAxe;
    public static Item saSandstoneAxe;
    public static Item bSandstoneAxe;
    public static Item pSandstoneAxe;
    public static Item nSandstoneAxe;
    public static Item iceSandstoneAxe;
    public static Item sSandstoneAxe;
    public static Item cSandstoneAxe;
    public static Item fSandstoneAxe;
    public static Item brSandstoneAxe;
    public static Item wBoneAxe;
    public static Item stBoneAxe;
    public static Item iBoneAxe;
    public static Item dBoneAxe;
    public static Item rBoneAxe;
    public static Item oBoneAxe;
    public static Item bBoneAxe;
    public static Item mBoneAxe;
    public static Item nBoneAxe;
    public static Item glBoneAxe;
    public static Item sBoneAxe;
    public static Item cBoneAxe;
    public static Item fBoneAxe;
    public static Item brBoneAxe;
    public static Item blBoneAxe;
    public static Item wPaperAxe;
    public static Item saPaperAxe;
    public static Item bPaperAxe;
    public static Item pPaperAxe;
    public static Item sPaperAxe;
    public static Item cPaperAxe;
    public static Item brPaperAxe;
    public static Item stMossyAxe;
    public static Item dMossyAxe;
    public static Item rMossyAxe;
    public static Item bMossyAxe;
    public static Item mMossyAxe;
    public static Item glMossyAxe;
    public static Item wNetherrackAxe;
    public static Item stNetherrackAxe;
    public static Item iNetherrackAxe;
    public static Item rNetherrackAxe;
    public static Item oNetherrackAxe;
    public static Item saNetherrackAxe;
    public static Item bNetherrackAxe;
    public static Item mNetherrackAxe;
    public static Item nNetherrackAxe;
    public static Item glNetherrackAxe;
    public static Item iceNetherrackAxe;
    public static Item sNetherrackAxe;
    public static Item cNetherrackAxe;
    public static Item fNetherrackAxe;
    public static Item brNetherrackAxe;
    public static Item blNetherrackAxe;
    public static Item wGlowstoneAxe;
    public static Item stGlowstoneAxe;
    public static Item iGlowstoneAxe;
    public static Item dGlowstoneAxe;
    public static Item rGlowstoneAxe;
    public static Item oGlowstoneAxe;
    public static Item bGlowstoneAxe;
    public static Item mGlowstoneAxe;
    public static Item nGlowstoneAxe;
    public static Item glGlowstoneAxe;
    public static Item iceGlowstoneAxe;
    public static Item lGlowstoneAxe;
    public static Item sGlowstoneAxe;
    public static Item blGlowstoneAxe;
    public static Item wIceAxe;
    public static Item stIceAxe;
    public static Item iIceAxe;
    public static Item dIceAxe;
    public static Item gIceAxe;
    public static Item rIceAxe;
    public static Item oIceAxe;
    public static Item saIceAxe;
    public static Item bIceAxe;
    public static Item glIceAxe;
    public static Item iceIceAxe;
    public static Item sIceAxe;
    public static Item cIceAxe;
    public static Item fIceAxe;
    public static Item brIceAxe;
    public static Item dLavaAxe;
    public static Item rLavaAxe;
    public static Item bLavaAxe;
    public static Item nLavaAxe;
    public static Item glLavaAxe;
    public static Item lLavaAxe;
    public static Item blLavaAxe;
    public static Item wSlimeAxe;
    public static Item stSlimeAxe;
    public static Item iSlimeAxe;
    public static Item dSlimeAxe;
    public static Item gSlimeAxe;
    public static Item rSlimeAxe;
    public static Item oSlimeAxe;
    public static Item saSlimeAxe;
    public static Item bSlimeAxe;
    public static Item pSlimeAxe;
    public static Item mSlimeAxe;
    public static Item nSlimeAxe;
    public static Item glSlimeAxe;
    public static Item iceSlimeAxe;
    public static Item lSlimeAxe;
    public static Item sSlimeAxe;
    public static Item cSlimeAxe;
    public static Item fSlimeAxe;
    public static Item brSlimeAxe;
    public static Item blSlimeAxe;
    public static Item wCactusAxe;
    public static Item stCactusAxe;
    public static Item saCactusAxe;
    public static Item bCactusAxe;
    public static Item pCactusAxe;
    public static Item nCactusAxe;
    public static Item sCactusAxe;
    public static Item cCactusAxe;
    public static Item fCactusAxe;
    public static Item brCactusAxe;
    public static Item wFlintAxe;
    public static Item stFlintAxe;
    public static Item iFlintAxe;
    public static Item gFlintAxe;
    public static Item oFlintAxe;
    public static Item saFlintAxe;
    public static Item bFlintAxe;
    public static Item nFlintAxe;
    public static Item iceFlintAxe;
    public static Item sFlintAxe;
    public static Item cFlintAxe;
    public static Item fFlintAxe;
    public static Item brFlintAxe;
    public static Item blFlintAxe;
    public static Item wBrickAxe;
    public static Item stBrickAxe;
    public static Item saBrickAxe;
    public static Item bBrickAxe;
    public static Item pBrickAxe;
    public static Item mBrickAxe;
    public static Item nBrickAxe;
    public static Item iceBrickAxe;
    public static Item sBrickAxe;
    public static Item cBrickAxe;
    public static Item fBrickAxe;
    public static Item brBrickAxe;
    public static Item dBlazeAxe;
    public static Item rBlazeAxe;
    public static Item bBlazeAxe;
    public static Item nBlazeAxe;
    public static Item glBlazeAxe;
    public static Item lBlazeAxe;
    public static Item fBlazeAxe;
    public static Item blBlazeAxe;
    public static Item stWoodHoe;
    public static Item saWoodHoe;
    public static Item bWoodHoe;
    public static Item pWoodHoe;
    public static Item nWoodHoe;
    public static Item sWoodHoe;
    public static Item cWoodHoe;
    public static Item fWoodHoe;
    public static Item brWoodHoe;
    public static Item stStoneHoe;
    public static Item saStoneHoe;
    public static Item bStoneHoe;
    public static Item pStoneHoe;
    public static Item mStoneHoe;
    public static Item nStoneHoe;
    public static Item iceStoneHoe;
    public static Item sStoneHoe;
    public static Item cStoneHoe;
    public static Item fStoneHoe;
    public static Item brStoneHoe;
    public static Item stIronHoe;
    public static Item iIronHoe;
    public static Item dIronHoe;
    public static Item gIronHoe;
    public static Item rIronHoe;
    public static Item oIronHoe;
    public static Item bIronHoe;
    public static Item nIronHoe;
    public static Item glIronHoe;
    public static Item iceIronHoe;
    public static Item sIronHoe;
    public static Item blIronHoe;
    public static Item stDiamondHoe;
    public static Item iDiamondHoe;
    public static Item dDiamondHoe;
    public static Item gDiamondHoe;
    public static Item rDiamondHoe;
    public static Item oDiamondHoe;
    public static Item bDiamondHoe;
    public static Item mDiamondHoe;
    public static Item nDiamondHoe;
    public static Item glDiamondHoe;
    public static Item blDiamondHoe;
    public static Item stGoldHoe;
    public static Item gGoldHoe;
    public static Item oGoldHoe;
    public static Item saGoldHoe;
    public static Item bGoldHoe;
    public static Item mGoldHoe;
    public static Item nGoldHoe;
    public static Item glGoldHoe;
    public static Item iceGoldHoe;
    public static Item sGoldHoe;
    public static Item cGoldHoe;
    public static Item fGoldHoe;
    public static Item wRedstoneHoe;
    public static Item stRedstoneHoe;
    public static Item iRedstoneHoe;
    public static Item dRedstoneHoe;
    public static Item rRedstoneHoe;
    public static Item oRedstoneHoe;
    public static Item bRedstoneHoe;
    public static Item mRedstoneHoe;
    public static Item glRedstoneHoe;
    public static Item sRedstoneHoe;
    public static Item blRedstoneHoe;
    public static Item wObsidianHoe;
    public static Item stObsidianHoe;
    public static Item iObsidianHoe;
    public static Item dObsidianHoe;
    public static Item gObsidianHoe;
    public static Item rObsidianHoe;
    public static Item oObsidianHoe;
    public static Item bObsidianHoe;
    public static Item nObsidianHoe;
    public static Item glObsidianHoe;
    public static Item sObsidianHoe;
    public static Item fObsidianHoe;
    public static Item blObsidianHoe;
    public static Item wSandstoneHoe;
    public static Item stSandstoneHoe;
    public static Item oSandstoneHoe;
    public static Item saSandstoneHoe;
    public static Item bSandstoneHoe;
    public static Item pSandstoneHoe;
    public static Item nSandstoneHoe;
    public static Item iceSandstoneHoe;
    public static Item sSandstoneHoe;
    public static Item cSandstoneHoe;
    public static Item fSandstoneHoe;
    public static Item brSandstoneHoe;
    public static Item wBoneHoe;
    public static Item stBoneHoe;
    public static Item iBoneHoe;
    public static Item dBoneHoe;
    public static Item rBoneHoe;
    public static Item oBoneHoe;
    public static Item bBoneHoe;
    public static Item mBoneHoe;
    public static Item nBoneHoe;
    public static Item glBoneHoe;
    public static Item sBoneHoe;
    public static Item cBoneHoe;
    public static Item fBoneHoe;
    public static Item brBoneHoe;
    public static Item blBoneHoe;
    public static Item wPaperHoe;
    public static Item saPaperHoe;
    public static Item bPaperHoe;
    public static Item pPaperHoe;
    public static Item sPaperHoe;
    public static Item cPaperHoe;
    public static Item brPaperHoe;
    public static Item stMossyHoe;
    public static Item dMossyHoe;
    public static Item rMossyHoe;
    public static Item bMossyHoe;
    public static Item mMossyHoe;
    public static Item glMossyHoe;
    public static Item wNetherrackHoe;
    public static Item stNetherrackHoe;
    public static Item iNetherrackHoe;
    public static Item rNetherrackHoe;
    public static Item oNetherrackHoe;
    public static Item saNetherrackHoe;
    public static Item bNetherrackHoe;
    public static Item mNetherrackHoe;
    public static Item nNetherrackHoe;
    public static Item glNetherrackHoe;
    public static Item iceNetherrackHoe;
    public static Item sNetherrackHoe;
    public static Item cNetherrackHoe;
    public static Item fNetherrackHoe;
    public static Item brNetherrackHoe;
    public static Item blNetherrackHoe;
    public static Item wGlowstoneHoe;
    public static Item stGlowstoneHoe;
    public static Item iGlowstoneHoe;
    public static Item dGlowstoneHoe;
    public static Item rGlowstoneHoe;
    public static Item oGlowstoneHoe;
    public static Item bGlowstoneHoe;
    public static Item mGlowstoneHoe;
    public static Item nGlowstoneHoe;
    public static Item glGlowstoneHoe;
    public static Item iceGlowstoneHoe;
    public static Item lGlowstoneHoe;
    public static Item sGlowstoneHoe;
    public static Item blGlowstoneHoe;
    public static Item wIceHoe;
    public static Item stIceHoe;
    public static Item iIceHoe;
    public static Item dIceHoe;
    public static Item gIceHoe;
    public static Item rIceHoe;
    public static Item oIceHoe;
    public static Item saIceHoe;
    public static Item bIceHoe;
    public static Item glIceHoe;
    public static Item iceIceHoe;
    public static Item sIceHoe;
    public static Item cIceHoe;
    public static Item fIceHoe;
    public static Item brIceHoe;
    public static Item dLavaHoe;
    public static Item rLavaHoe;
    public static Item bLavaHoe;
    public static Item nLavaHoe;
    public static Item glLavaHoe;
    public static Item lLavaHoe;
    public static Item blLavaHoe;
    public static Item wSlimeHoe;
    public static Item stSlimeHoe;
    public static Item iSlimeHoe;
    public static Item dSlimeHoe;
    public static Item gSlimeHoe;
    public static Item rSlimeHoe;
    public static Item oSlimeHoe;
    public static Item saSlimeHoe;
    public static Item bSlimeHoe;
    public static Item pSlimeHoe;
    public static Item mSlimeHoe;
    public static Item nSlimeHoe;
    public static Item glSlimeHoe;
    public static Item iceSlimeHoe;
    public static Item lSlimeHoe;
    public static Item sSlimeHoe;
    public static Item cSlimeHoe;
    public static Item fSlimeHoe;
    public static Item brSlimeHoe;
    public static Item blSlimeHoe;
    public static Item wCactusHoe;
    public static Item stCactusHoe;
    public static Item saCactusHoe;
    public static Item bCactusHoe;
    public static Item pCactusHoe;
    public static Item nCactusHoe;
    public static Item sCactusHoe;
    public static Item cCactusHoe;
    public static Item fCactusHoe;
    public static Item brCactusHoe;
    public static Item wFlintHoe;
    public static Item stFlintHoe;
    public static Item iFlintHoe;
    public static Item gFlintHoe;
    public static Item oFlintHoe;
    public static Item saFlintHoe;
    public static Item bFlintHoe;
    public static Item nFlintHoe;
    public static Item iceFlintHoe;
    public static Item sFlintHoe;
    public static Item cFlintHoe;
    public static Item fFlintHoe;
    public static Item brFlintHoe;
    public static Item blFlintHoe;
    public static Item wBrickHoe;
    public static Item stBrickHoe;
    public static Item saBrickHoe;
    public static Item bBrickHoe;
    public static Item pBrickHoe;
    public static Item mBrickHoe;
    public static Item nBrickHoe;
    public static Item iceBrickHoe;
    public static Item sBrickHoe;
    public static Item cBrickHoe;
    public static Item fBrickHoe;
    public static Item brBrickHoe;
    public static Item dBlazeHoe;
    public static Item rBlazeHoe;
    public static Item bBlazeHoe;
    public static Item nBlazeHoe;
    public static Item glBlazeHoe;
    public static Item lBlazeHoe;
    public static Item fBlazeHoe;
    public static Item blBlazeHoe;
    /*public static Item wWoodFryingPan;
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
    public static Item blBlazeFryingPan;*/
    
    public static InfiProps props;
    public static boolean initialized = false;
    InfiEnchantFreezing freeze = new InfiEnchantFreezing(42, 2);

    public String getVersion()
    {
        return "v2.3.1 Twi";
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
            System.out.println("Equivalent Exchange integration for InfiTools activated");
            EEProxy.setEMC(redstoneCrystal.shiftedIndex, 405);
            EEProxy.setEMC(glowstoneCrystal.shiftedIndex, 3754);
            EEProxy.setEMC(lavaCrystal.shiftedIndex, 3104);
            EEProxy.setEMC(slimeCrystal.shiftedIndex, 88);
            EEProxy.setEMC(blazeCrystal.shiftedIndex, 3930);
            EEProxy.setEMC(paperDust.shiftedIndex, 928);
            EEProxy.setEMC(obsidianCrystal.shiftedIndex, 2848);
            EEProxy.setEMC(mossBallGiant.shiftedIndex, 145);
            EEProxy.setEMC(mossBallCrafted.shiftedIndex, 581);
            System.out.println("EMC values set!");
        }
        catch (Throwable throwable)
        {
            System.out.println("Equivalent Exchange integration failed! Reason:");
            System.out.println(throwable);
        }
    }

    /*public void addMetallurgySupport()
    {
        try
        {
            Class class1 = Class.forName("mod_MetallurgyBase");
            System.out.println("Metallurgy integration for InfiTools activated");
            wLevel = 0;
            stLevel = 1;
            iLevel = 5;
            dLevel = 7;
            gLevel = 9;
            rLevel = 7;
            oLevel = 9;
            saLevel = 1;
            bLevel = 3;
            pLevel = 1;
            mLevel = 6;
            nLevel = 4;
            glLevel = 7;
            lLevel = 11;
            iceLevel = 0;
            sLevel = 6;
            cLevel = 2;
            fLevel = 1;
            brLevel = 2;
            blLevel = 11;
        }
        catch (Throwable throwable)
        {
            System.out.println("Metallurgy not detected");
        }
        InfiRecipeSwords.recipeStorm();
        InfiRecipePickaxes.recipeStorm();
        InfiRecipeShovels.recipeStorm();
        InfiRecipeAxes.recipeStorm();
        InfiRecipeHoes.recipeStorm();
        //InfiRecipeFryingPans.recipeStorm();
    }*/
    
    public static boolean getInitialized()
    {
    	return initialized;
    }

    public static InfiProps getProps(InfiProps infiprops)
    {
        blockMossID = infiprops.readInt("blockMossID");
        rodID = infiprops.readInt("rodID");
        paperMaterialsID = infiprops.readInt("paperMaterialsID");
        metalChunksID = infiprops.readInt("metalChunksID");
        mossID = infiprops.readInt("mossID");
        crystalID = infiprops.readInt("crystalID");
        woodBucketID = infiprops.readInt("woodBucketID");
        cactusBucketID = infiprops.readInt("cactusBucketID");
        goldBucketID = infiprops.readInt("goldBucketID");
        iceBucketID = infiprops.readInt("iceBucketID");
        lavaBucketID = infiprops.readInt("lavaBucketID");
        slimeBucketID = infiprops.readInt("slimeBucketID");
        ironBucketID = infiprops.readInt("ironBucketID");
        bowlID = infiprops.readInt("bowlID");
        materialShardID = infiprops.readInt("materialShardID");
        pumpkinPieID = infiprops.readInt("pumpkinPieID");
        pumpkinPulpID = infiprops.readInt("pumpkinPulpID");
        woodSwordID = infiprops.readInt("woodSwordID");
        stoneSwordID = infiprops.readInt("stoneSwordID");
        ironSwordID = infiprops.readInt("ironSwordID");
        diamondSwordID = infiprops.readInt("diamondSwordID");
        goldSwordID = infiprops.readInt("goldSwordID");
        redstoneSwordID = infiprops.readInt("redstoneSwordID");
        obsidianSwordID = infiprops.readInt("obsidianSwordID");
        sandstoneSwordID = infiprops.readInt("sandstoneSwordID");
        boneSwordID = infiprops.readInt("boneSwordID");
        paperSwordID = infiprops.readInt("paperSwordID");
        mossySwordID = infiprops.readInt("mossySwordID");
        netherrackSwordID = infiprops.readInt("netherrackSwordID");
        glowstoneSwordID = infiprops.readInt("glowstoneSwordID");
        iceSwordID = infiprops.readInt("iceSwordID");
        lavaSwordID = infiprops.readInt("lavaSwordID");
        slimeSwordID = infiprops.readInt("slimeSwordID");
        cactusSwordID = infiprops.readInt("cactusSwordID");
        flintSwordID = infiprops.readInt("flintSwordID");
        brickSwordID = infiprops.readInt("brickSwordID");
        blazeSwordID = infiprops.readInt("blazeSwordID");
        woodPickaxeID = infiprops.readInt("woodPickaxeID");
        stonePickaxeID = infiprops.readInt("stonePickaxeID");
        ironPickaxeID = infiprops.readInt("ironPickaxeID");
        diamondPickaxeID = infiprops.readInt("diamondPickaxeID");
        goldPickaxeID = infiprops.readInt("goldPickaxeID");
        redstonePickaxeID = infiprops.readInt("redstonePickaxeID");
        obsidianPickaxeID = infiprops.readInt("obsidianPickaxeID");
        sandstonePickaxeID = infiprops.readInt("sandstonePickaxeID");
        bonePickaxeID = infiprops.readInt("bonePickaxeID");
        paperPickaxeID = infiprops.readInt("paperPickaxeID");
        mossyPickaxeID = infiprops.readInt("mossyPickaxeID");
        netherrackPickaxeID = infiprops.readInt("netherrackPickaxeID");
        glowstonePickaxeID = infiprops.readInt("glowstonePickaxeID");
        icePickaxeID = infiprops.readInt("icePickaxeID");
        lavaPickaxeID = infiprops.readInt("lavaPickaxeID");
        slimePickaxeID = infiprops.readInt("slimePickaxeID");
        cactusPickaxeID = infiprops.readInt("cactusPickaxeID");
        flintPickaxeID = infiprops.readInt("flintPickaxeID");
        brickPickaxeID = infiprops.readInt("brickPickaxeID");
        blazePickaxeID = infiprops.readInt("blazePickaxeID");
        woodShovelID = infiprops.readInt("woodShovelID");
        stoneShovelID = infiprops.readInt("stoneShovelID");
        ironShovelID = infiprops.readInt("ironShovelID");
        diamondShovelID = infiprops.readInt("diamondShovelID");
        goldShovelID = infiprops.readInt("goldShovelID");
        redstoneShovelID = infiprops.readInt("redstoneShovelID");
        obsidianShovelID = infiprops.readInt("obsidianShovelID");
        sandstoneShovelID = infiprops.readInt("sandstoneShovelID");
        boneShovelID = infiprops.readInt("boneShovelID");
        paperShovelID = infiprops.readInt("paperShovelID");
        mossyShovelID = infiprops.readInt("mossyShovelID");
        netherrackShovelID = infiprops.readInt("netherrackShovelID");
        glowstoneShovelID = infiprops.readInt("glowstoneShovelID");
        iceShovelID = infiprops.readInt("iceShovelID");
        lavaShovelID = infiprops.readInt("lavaShovelID");
        slimeShovelID = infiprops.readInt("slimeShovelID");
        cactusShovelID = infiprops.readInt("cactusShovelID");
        flintShovelID = infiprops.readInt("flintShovelID");
        brickShovelID = infiprops.readInt("brickShovelID");
        blazeShovelID = infiprops.readInt("blazeShovelID");
        woodAxeID = infiprops.readInt("woodAxeID");
        stoneAxeID = infiprops.readInt("stoneAxeID");
        ironAxeID = infiprops.readInt("ironAxeID");
        diamondAxeID = infiprops.readInt("diamondAxeID");
        goldAxeID = infiprops.readInt("goldAxeID");
        redstoneAxeID = infiprops.readInt("redstoneAxeID");
        obsidianAxeID = infiprops.readInt("obsidianAxeID");
        sandstoneAxeID = infiprops.readInt("sandstoneAxeID");
        boneAxeID = infiprops.readInt("boneAxeID");
        paperAxeID = infiprops.readInt("paperAxeID");
        mossyAxeID = infiprops.readInt("mossyAxeID");
        netherrackAxeID = infiprops.readInt("netherrackAxeID");
        glowstoneAxeID = infiprops.readInt("glowstoneAxeID");
        iceAxeID = infiprops.readInt("iceAxeID");
        lavaAxeID = infiprops.readInt("lavaAxeID");
        slimeAxeID = infiprops.readInt("slimeAxeID");
        cactusAxeID = infiprops.readInt("cactusAxeID");
        flintAxeID = infiprops.readInt("flintAxeID");
        brickAxeID = infiprops.readInt("brickAxeID");
        blazeAxeID = infiprops.readInt("blazeAxeID");
        woodHoeID = infiprops.readInt("woodHoeID");
        stoneHoeID = infiprops.readInt("stoneHoeID");
        ironHoeID = infiprops.readInt("ironHoeID");
        diamondHoeID = infiprops.readInt("diamondHoeID");
        goldHoeID = infiprops.readInt("goldHoeID");
        redstoneHoeID = infiprops.readInt("redstoneHoeID");
        obsidianHoeID = infiprops.readInt("obsidianHoeID");
        sandstoneHoeID = infiprops.readInt("sandstoneHoeID");
        boneHoeID = infiprops.readInt("boneHoeID");
        paperHoeID = infiprops.readInt("paperHoeID");
        mossyHoeID = infiprops.readInt("mossyHoeID");
        netherrackHoeID = infiprops.readInt("netherrackHoeID");
        glowstoneHoeID = infiprops.readInt("glowstoneHoeID");
        iceHoeID = infiprops.readInt("iceHoeID");
        lavaHoeID = infiprops.readInt("lavaHoeID");
        slimeHoeID = infiprops.readInt("slimeHoeID");
        cactusHoeID = infiprops.readInt("cactusHoeID");
        flintHoeID = infiprops.readInt("flintHoeID");
        brickHoeID = infiprops.readInt("brickHoeID");
        blazeHoeID = infiprops.readInt("blazeHoeID");
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

    public mod_InfiTools()
    {
    	initialized = true;
        InfiRecipeItems.recipeStorm();
        InfiRecipeSwords.recipeStorm();
        InfiRecipePickaxes.recipeStorm();
        InfiRecipeAxes.recipeStorm();
        InfiRecipeShovels.recipeStorm();
        InfiRecipeHoes.recipeStorm();

        ModLoader.registerBlock(blockMoss);
        
        ModLoader.addRecipe(new ItemStack(paperStack, 1), new Object[]
                {
                    "pp", "pp", Character.valueOf('p'), Item.paper
                });
        
        ModLoader.addRecipe(new ItemStack(paperDust, 1), new Object[]
                {
                    " r ", "gpg", " r ", Character.valueOf('p'), Item.paper, Character.valueOf('r'), Item.redstone, Character.valueOf('g'), Item.lightStoneDust
                });
        ModLoader.addRecipe(new ItemStack(paperDust, 1), new Object[]
                {
                    " g ", "rpr", " g ", Character.valueOf('p'), Item.paper, Character.valueOf('r'), Item.redstone, Character.valueOf('g'), Item.lightStoneDust
                });
        
        ModLoader.addRecipe(new ItemStack(ironChunks, 6), new Object[]
                {
                    "ii", Character.valueOf('i'), Item.ingotIron
                });
        ModLoader.addShapelessRecipe(new ItemStack(Item.ingotIron), new Object[]
                {
                    ironChunks, ironChunks, ironChunks
                });
        
        ModLoader.addRecipe(new ItemStack(goldChunks, 6), new Object[]
                {
                    "gg", Character.valueOf('g'), Item.ingotGold
                });
        ModLoader.addShapelessRecipe(new ItemStack(Item.ingotGold), new Object[]
                {
                    goldChunks, goldChunks, goldChunks
                });
        ModLoader.addShapelessRecipe(new ItemStack(goldChunks, 1), new Object[]
                {
                    Item.goldNugget, Item.goldNugget, Item.goldNugget
                });
        ModLoader.addRecipe(new ItemStack(Item.goldNugget, 3), new Object[]
                {
                    "g", Character.valueOf('g'), goldChunks
                });
        
        ModLoader.addRecipe(new ItemStack(mossBall, 3), new Object[]
                {
                    "m", Character.valueOf('m'), Block.cobblestoneMossy
                });
        ModLoader.addRecipe(new ItemStack(mossBall, 3), new Object[]
                {
                    "m", Character.valueOf('m'), new ItemStack(Block.stoneBrick, 3, 1)
                });
        
        ModLoader.addRecipe(new ItemStack(mossBallGiant, 1), new Object[]
                {
                    "mm", "mm", Character.valueOf('m'), mossBall
                });
        
        ModLoader.addRecipe(new ItemStack(mossBallCrafted, 1), new Object[]
                {
                    " m ", "msm", " m ", Character.valueOf('m'), mossBallGiant, Character.valueOf('s'), Block.stone
                });
        
        ModLoader.addRecipe(new ItemStack(redstoneCrystal, 1), new Object[]
                {
                    "rrr", "rIr", Character.valueOf('r'), Item.redstone, Character.valueOf('I'), ironChunks
                });
        ModLoader.addRecipe(new ItemStack(redstoneCrystal, 1), new Object[]
                {
                    "rIr", "rrr", Character.valueOf('r'), Item.redstone, Character.valueOf('I'), ironChunks
                });
        
        ModLoader.addRecipe(new ItemStack(glowstoneCrystal, 1), new Object[]
                {
                    "ggg", "gIg", "ggg", Character.valueOf('g'), Item.lightStoneDust, Character.valueOf('I'), goldChunks
                });
        
        ModLoader.addShapelessRecipe(new ItemStack(obsidianCrystal), new Object[]
                {
                    Block.obsidian, paperDust, paperDust, paperDust
                });
        
        ModLoader.addRecipe(new ItemStack(lavaCrystal, 1), new Object[]
                {
                    " l ", "lpl", " l ", Character.valueOf('l'), new ItemStack(Item.bucketLava, 1, -1), Character.valueOf('p'), obsidianCrystal
                });
        ModLoader.addRecipe(new ItemStack(lavaCrystal, 1), new Object[]
                {
                    " l ", "lpl", " l ", Character.valueOf('l'), new ItemStack(goldBucketLava, 1, -1), Character.valueOf('p'), obsidianCrystal
                });
        ModLoader.addRecipe(new ItemStack(lavaCrystal, 1), new Object[]
                {
                    " l ", "lpl", " l ", Character.valueOf('l'), new ItemStack(lavaBucketLava, 1, -1), Character.valueOf('p'), obsidianCrystal
                });
        
        ModLoader.addRecipe(new ItemStack(slimeCrystal, 1), new Object[]
                {
                    " f ", "rsr", " f ", Character.valueOf('s'), Item.slimeBall, Character.valueOf('f'), Block.plantYellow, Character.valueOf('r'), Block.plantRed
                });
        ModLoader.addRecipe(new ItemStack(slimeCrystal, 1), new Object[]
                {
                    " r ", "fsf", " r ", Character.valueOf('s'), Item.slimeBall, Character.valueOf('f'), Block.plantYellow, Character.valueOf('r'), Block.plantRed
                });
        
        ModLoader.addRecipe(new ItemStack(blazeCrystal, 1), new Object[]
                {
                    "pwp", "ncn", "plp", Character.valueOf('p'), Item.blazePowder, Character.valueOf('w'), new ItemStack(Item.bucketWater, 1, -1), Character.valueOf('l'), new ItemStack(Item.bucketLava, 1, -1), Character.valueOf('c'),
                    Item.magmaCream, Character.valueOf('n'), Block.netherrack
                });
        ModLoader.addRecipe(new ItemStack(blazeCrystal, 1), new Object[]
                {
                    "plp", "ncn", "pwp", Character.valueOf('p'), Item.blazePowder, Character.valueOf('w'), new ItemStack(Item.bucketWater, 1, -1), Character.valueOf('l'), new ItemStack(Item.bucketLava, 1, -1), Character.valueOf('c'),
                    Item.magmaCream, Character.valueOf('n'), Block.netherrack
                });
        ModLoader.addRecipe(new ItemStack(blazeCrystal, 1), new Object[]
                {
                    "pwp", "ncn", "plp", Character.valueOf('p'), Item.blazePowder, Character.valueOf('w'), new ItemStack(Item.bucketWater, 1, -1), Character.valueOf('l'), new ItemStack(goldBucketLava, 1, -1), Character.valueOf('c'),
                    Item.magmaCream, Character.valueOf('n'), Block.netherrack
                });
        ModLoader.addRecipe(new ItemStack(blazeCrystal, 1), new Object[]
                {
                    "plp", "ncn", "pwp", Character.valueOf('p'), Item.blazePowder, Character.valueOf('w'), new ItemStack(Item.bucketWater, 1, -1), Character.valueOf('l'), new ItemStack(goldBucketLava, 1, -1), Character.valueOf('c'),
                    Item.magmaCream, Character.valueOf('n'), Block.netherrack
                });
        ModLoader.addRecipe(new ItemStack(blazeCrystal, 1), new Object[]
                {
                    "pwp", "ncn", "plp", Character.valueOf('p'), Item.blazePowder, Character.valueOf('w'), new ItemStack(Item.bucketWater, 1, -1), Character.valueOf('l'), new ItemStack(lavaBucketLava, 1, -1), Character.valueOf('c'),
                    Item.magmaCream, Character.valueOf('n'), Block.netherrack
                });
        ModLoader.addRecipe(new ItemStack(blazeCrystal, 1), new Object[]
                {
                    "plp", "ncn", "pwp", Character.valueOf('p'), Item.blazePowder, Character.valueOf('w'), new ItemStack(Item.bucketWater, 1, -1), Character.valueOf('l'), new ItemStack(lavaBucketLava, 1, -1), Character.valueOf('c'),
                    Item.magmaCream, Character.valueOf('n'), Block.netherrack
                });
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        ModLoader.addRecipe(new ItemStack(Item.stick, 3), new Object[]
                {
                    "s", "s", "s", Character.valueOf('s'), woodSplinters
                });
        ModLoader.addRecipe(new ItemStack(redstoneFragment, 3), new Object[]
                {
                    "r", Character.valueOf('r'), redstoneCrystal
                });
        ModLoader.addRecipe(new ItemStack(glowstoneFragment, 3), new Object[]
                {
                    "r", Character.valueOf('r'), glowstoneCrystal
                });
        ModLoader.addRecipe(new ItemStack(lavaFragment, 3), new Object[]
                {
                    "r", Character.valueOf('r'), lavaCrystal
                });
        ModLoader.addRecipe(new ItemStack(slimeFragment, 3), new Object[]
                {
                    "r", Character.valueOf('r'), slimeCrystal
                });
        ModLoader.addRecipe(new ItemStack(blazeFragment, 3), new Object[]
                {
                    "r", Character.valueOf('r'), blazeCrystal
                });
        
        ModLoader.addRecipe(new ItemStack(stoneRod, 4), new Object[]
                {
                    "X", "X", Character.valueOf('X'), Block.cobblestone
                });
        ModLoader.addRecipe(new ItemStack(stoneRod, 1), new Object[]
                {
                    "X", "X", Character.valueOf('X'), stoneShard
                });
        
        ModLoader.addRecipe(new ItemStack(ironRod, 4), new Object[]
                {
                    "X", "X", Character.valueOf('X'), Item.ingotIron
                });
        ModLoader.addRecipe(new ItemStack(ironRod, 2), new Object[]
                {
                    "X", "X", Character.valueOf('X'), ironChunks
                });
        
        ModLoader.addRecipe(new ItemStack(diamondRod, 4), new Object[]
                {
                    "X", "X", Character.valueOf('X'), Item.diamond
                });
        ModLoader.addRecipe(new ItemStack(diamondRod, 2), new Object[]
                {
                    "X", "X", Character.valueOf('X'), diamondShard
                });
        
        ModLoader.addRecipe(new ItemStack(goldRod, 4), new Object[]
                {
                    "X", "X", Character.valueOf('X'), Item.ingotGold
                });
        ModLoader.addRecipe(new ItemStack(goldRod, 2), new Object[]
                {
                    "X", "X", Character.valueOf('X'), goldChunks
                });
        
        ModLoader.addRecipe(new ItemStack(redstoneRod, 4), new Object[]
                {
                    "X", "X", Character.valueOf('X'), redstoneCrystal
                });
        ModLoader.addRecipe(new ItemStack(redstoneRod, 2), new Object[]
                {
                    "X", "X", Character.valueOf('X'), redstoneFragment
                });
        
        ModLoader.addRecipe(new ItemStack(obsidianRod, 4), new Object[]
                {
                    "X", "X", Character.valueOf('X'), Block.obsidian
                });
        ModLoader.addRecipe(new ItemStack(obsidianRod, 2), new Object[]
                {
                    "X", "X", Character.valueOf('X'), obsidianShard
                });
        
        ModLoader.addRecipe(new ItemStack(sandstoneRod, 4), new Object[]
                {
                    "X", "X", Character.valueOf('X'), Block.sandStone
                });
        ModLoader.addRecipe(new ItemStack(sandstoneRod, 2), new Object[]
                {
                    "X", "X", Character.valueOf('X'), sandstoneShard
                });
        
        ModLoader.addRecipe(new ItemStack(boneRod, 2), new Object[]
                {
                    "X", "X", Character.valueOf('X'), Item.bone
                });
        
        ModLoader.addRecipe(new ItemStack(paperRod, 4), new Object[]
                {
                    "X", "X", Character.valueOf('X'), paperStack
                });
        ModLoader.addRecipe(new ItemStack(paperRod, 1), new Object[]
                {
                    "X", "X", Character.valueOf('X'), Item.paper
                });
        
        ModLoader.addRecipe(new ItemStack(mossyRod, 1), new Object[]
                {
                    "X", "X", Character.valueOf('X'), mossBallGiant
                });
        ModLoader.addRecipe(new ItemStack(mossyRod, 4), new Object[]
                {
                    "X", "X", Character.valueOf('X'), mossBallCrafted
                });
        
        ModLoader.addRecipe(new ItemStack(netherrackRod, 4), new Object[]
                {
                    "X", "X", Character.valueOf('X'), Block.netherrack
                });
        ModLoader.addRecipe(new ItemStack(netherrackRod, 2), new Object[]
                {
                    "X", "X", Character.valueOf('X'), netherrackShard
                });
        
        ModLoader.addRecipe(new ItemStack(glowstoneRod, 4), new Object[]
                {
                    "X", "X", Character.valueOf('X'), glowstoneCrystal
                });
        ModLoader.addRecipe(new ItemStack(glowstoneRod, 2), new Object[]
                {
                    "X", "X", Character.valueOf('X'), glowstoneFragment
                });
        
        ModLoader.addRecipe(new ItemStack(iceRod, 4), new Object[]
                {
                    "X", "X", Character.valueOf('X'), Block.ice
                });
        ModLoader.addRecipe(new ItemStack(iceRod, 2), new Object[]
                {
                    "X", "X", Character.valueOf('X'), iceShard
                });
        
        ModLoader.addRecipe(new ItemStack(lavaRod, 4), new Object[]
                {
                    "X", "X", Character.valueOf('X'), lavaCrystal
                });
        ModLoader.addRecipe(new ItemStack(lavaRod, 2), new Object[]
                {
                    "X", "X", Character.valueOf('X'), lavaFragment
                });
        
        ModLoader.addRecipe(new ItemStack(slimeRod, 4), new Object[]
                {
                    "X", "X", Character.valueOf('X'), slimeCrystal
                });
        ModLoader.addRecipe(new ItemStack(slimeRod, 2), new Object[]
                {
                    "X", "X", Character.valueOf('X'), slimeFragment
                });
        
        ModLoader.addRecipe(new ItemStack(cactusRod, 4), new Object[]
                {
                    "X", "X", Character.valueOf('X'), Block.cactus
                });
        ModLoader.addRecipe(new ItemStack(cactusRod, 4), new Object[]
                {
                    "X", "X", Character.valueOf('X'), new ItemStack(Item.dyePowder, 1, 2)
                });
        
        ModLoader.addRecipe(new ItemStack(flintRod, 4), new Object[]
                {
                    "X", "X", Character.valueOf('X'), Item.flint
                });
        
        ModLoader.addRecipe(new ItemStack(brickRod, 4), new Object[]
                {
                    "X", "X", Character.valueOf('X'), Item.brick
                });
        ModLoader.addRecipe(new ItemStack(Item.helmetChain, 1), new Object[]
                {
                    "XXX", "X X", Character.valueOf('X'), ironChunks
                });
        ModLoader.addRecipe(new ItemStack(Item.legsChain, 1), new Object[]
                {
                    "XXX", "X X", "X X", Character.valueOf('X'), ironChunks
                });
        ModLoader.addRecipe(new ItemStack(Item.bootsChain, 1), new Object[]
                {
                    "X X", "X X", Character.valueOf('X'), ironChunks
                });
        ModLoader.addRecipe(new ItemStack(Item.plateChain, 1), new Object[]
                {
                    "X X", "XXX", "XXX", Character.valueOf('X'), ironChunks
                });
        
        ModLoader.addShapelessRecipe(new ItemStack(Block.cobblestoneMossy, 1, 0), new Object[]
                {
                    mossBallGiant, Block.cobblestone
                });
        ModLoader.addShapelessRecipe(new ItemStack(Block.stoneBrick, 1, 1), new Object[]
                {
                    mossBallGiant, Block.stoneBrick
                });
        
        Item[] stickArray = { 
    		stoneRod, ironRod, diamondRod, goldRod, redstoneRod, obsidianRod,
    		sandstoneRod, Item.bone, paperRod, mossyRod, netherrackRod, glowstoneRod, lavaRod, 
    		iceRod, slimeRod, cactusRod, flintRod, flintRod, brickRod, Item.blazeRod
    	};
        
        Item[] shardArrayShort = {
        	stoneShard, ironChunks, diamondShard, goldChunks, redstoneFragment, obsidianShard,
        	sandstoneShard, netherrackShard, glowstoneFragment, iceShard, lavaFragment, slimeFragment,
        	blazeFragment
        };
        
        for (int iter = 0; iter < stickArray.length; iter++)
		{
        	ModLoader.addRecipe(new ItemStack(Item.bow, 1), new Object[]
            {
        		"s| ", "s |", "s| ", Character.valueOf('s'), Item.silk, Character.valueOf('|'), stickArray[iter]
            });
			ModLoader.addRecipe(new ItemStack(Block.lever, 1), new Object[]
			{
				"s", "c", 'c', new ItemStack(Block.cobblestone, 1, 0), 's', stickArray[iter]
			});
			ModLoader.addRecipe(new ItemStack(Block.rail, 16), new Object[]
			{
				"c c", "csc", "c c", 'c', new ItemStack(Item.ingotIron, 1, 0), 's', stickArray[iter]
			});
			ModLoader.addRecipe(new ItemStack(Block.railPowered, 6), new Object[]
			{
				"c c", "csc", "crc", 'c', new ItemStack(Item.ingotGold, 1, -1), 
				's', stickArray[iter], 'r', new ItemStack(Item.redstone, 1, 0)
			});
			ModLoader.addRecipe(new ItemStack(Block.rail, 16), new Object[]
			{
				"c c", "csc", "c c", 'c', new ItemStack(Item.ingotIron, 1, 0), 's', stickArray[iter]
			});
			ModLoader.addRecipe(new ItemStack(Item.feather, 4), new Object[]
			{
				" | ", "s|s", "s|s", '|', stickArray[iter], 's', Item.silk
			});
			ModLoader.addRecipe(new ItemStack(Item.feather, 4), new Object[]
			{
				"s|s", "s|s", " | ", '|', stickArray[iter], 's', Item.silk
			});
		}
        
        ModLoader.addRecipe(new ItemStack(Item.feather, 4), new Object[]
    	{
    		" | ", "s|s", "s|s", '|', Item.stick, 's', Item.silk
    	});
    	ModLoader.addRecipe(new ItemStack(Item.feather, 4), new Object[]
    	{
    		"s|s", "s|s", " | ", '|', Item.stick, 's', Item.silk
    	});
        
        for (int stickIter = 0; stickIter < stickArray.length; stickIter++)
		{
        	for(int shardIter = 0; shardIter < shardArrayShort.length; shardIter++) {
        		ModLoader.addRecipe(new ItemStack(Item.arrow, 1), new Object[]
                {
                    "s", "|", 's', shardArrayShort[shardIter], '|', stickArray[stickIter]
                });
        		ModLoader.addRecipe(new ItemStack(Item.arrow, 4), new Object[]
                {
                    "s", "|", "f", 's', shardArrayShort[shardIter], '|', stickArray[stickIter], 'f', Item.feather
                });
        	}
		}
        
        ModLoader.addRecipe(new ItemStack(Item.brewingStand, 1), new Object[]
                {
                    "|||", " g ", "ccc", Character.valueOf('|'), ironRod, Character.valueOf('g'), Item.ingotGold, Character.valueOf('c'), Block.cobblestone
                });
        
        FurnaceRecipes.smelting().addSmelting(Item.dyePowder.shiftedIndex, 2, new ItemStack(Item.coal, 1, 1));
        
        addEEsupport();
        //addMetallurgySupport();
        MinecraftForge.registerCustomBucketHandler(new InfiBucketHandler());
        //ModLoader.setInGameHook(this, true, true);
    }
    
    public static Random rand = new Random();
    
    /*@Override
    public boolean onTickInGame(MinecraftServer mc)
    {
    	if(rand.nextInt(100) == 0) {
    		EntityPlayer player = mc.thePlayer;
    		InventoryPlayer inv = player.inventory;
    		for(int i = 0; i < inv.mainInventory.length; i++) {    			
		    	if(inv.mainInventory[i] != null) {
		    		ItemStack is = inv.mainInventory[i];
		    		Item item = is.getItem();
		    		if(item instanceof InfiToolBase) {
			    		InfiToolBase tool = (InfiToolBase)item;
			    		if(is.getItemDamage() > 0) {
			    			int heal = 0;
			    			if(tool.getHeadType() == 11)
			    				heal += 4;
			    			if(tool.getHandleType() == 11)
			    				heal += 1;
			    			if(heal > 0 && rand.nextInt(5) < heal)
			    				is.damageItem(-1, player);
			    		}
			    		if(tool.getHeadType() == 11 && is.getItemDamage() > 0) {
			    			is.damageItem(-1, player);
			    		}
		    		}
		    		if(item instanceof InfiWeaponBase) {
			    		InfiWeaponBase weapon = (InfiWeaponBase)item;
			    		if(is.getItemDamage() > 0) {
			    			int heal = 0;
			    			if(weapon.getHeadType() == 11)
			    				heal += 4;
			    			if(weapon.getHandleType() == 11)
			    				heal += 1;
			    			if(heal > 0 && rand.nextInt(5) < heal)
			    				is.damageItem(-1, player);
			    		}
			    		if(weapon.getHeadType() == 11 && is.getItemDamage() > 0) {
			    			is.damageItem(-1, player);
			    		}
		    		}
		    	}
    		}
    	}
    	return true;
    }*/

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

    public int addFuel(int i, int j)
    {
        if (i == Block.cactus.blockID)
        {
            return 100;
        } else
        if(i == coalBits.shiftedIndex)
        {
        	return 600;
        } else
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
        bDur = 200;
        bMod = 1.2F;
        bDam = 2;
        bLevel = 1;
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
        		.append('/').append("mDiyo").append('/').append("InfiTools.cfg").toString())).getPath());
        props = InfiTools.InitProps(props);
        getProps(props);
        
        pumpkinPulp = (new PumpkinPulp(pumpkinPulpID, 2, false, 16)).setIconCoord(0, 0).setItemName("pumpkinPulp");
        
        blockMoss = new InfiBlockMoss(blockMossID, 0).setHardness(0.2F).setStepSound(Block.soundGrassFootstep).setBlockName("mossBlock");
        paperStack = (new InfiTexture(paperMaterialsID + 0, "/infitools/infitems.png")).setIconCoord(0, 1).setItemName("paperStack");
        paperDust = (new InfiTexture(paperMaterialsID + 1, "/infitools/infitems.png")).setIconCoord(1, 1).setItemName("paperDust");
        ironChunks = (new InfiTexture(metalChunksID + 0, "/infitools/infitems.png")).setIconCoord(2, 1).setItemName("ironChunks");
        goldChunks = (new InfiTexture(metalChunksID + 1, "/infitools/infitems.png")).setIconCoord(3, 1).setItemName("goldChunks");
        mossBall = (new InfiTexture(mossID + 0, "/infitools/infitems.png")).setIconCoord(4, 1).setItemName("mossBall");
        mossBallGiant = (new InfiItemMoss(mossID + 1, "/infitools/infitems.png")).setIconCoord(4, 2).setItemName("mossBallGiant");
        mossBallCrafted = (new InfiTexture(mossID + 2, "/infitools/infitems.png")).setIconCoord(6, 2).setItemName("mossBallCrafted");
        redstoneCrystal = (new InfiTexture(crystalID + 0, "/infitools/infitems.png")).setIconCoord(0, 2).setItemName("redstoneCrystal");
        glowstoneCrystal = (new InfiTexture(crystalID + 1, "/infitools/infitems.png")).setIconCoord(1, 2).setItemName("glowstoneCrystal");
        obsidianCrystal = (new InfiTexture(crystalID + 4, "/infitools/infitems.png")).setIconCoord(5, 2).setItemName("obsidianCrystal");
        lavaCrystal = (new InfiTexture(crystalID + 2, "/infitools/infitems.png")).setIconCoord(2, 2).setItemName("lavaCrystal");
        slimeCrystal = (new InfiTexture(crystalID + 3, "/infitools/infitems.png")).setIconCoord(3, 2).setItemName("slimeCrystal");
        blazeCrystal = (new InfiTexture(crystalID + 5, "/gui/items.png")).setIconCoord(14, 2).setItemName("blazeCrystal");
        woodSplinters = (new InfiTexture(materialShardID + 0, "/infitools/infitems.png")).setIconCoord(0, 5).setItemName("woodSplinters");
        stoneShard = (new InfiTexture(materialShardID + 1, "/infitools/infitems.png")).setIconCoord(1, 5).setItemName("stoneShard");
        diamondShard = (new InfiTexture(materialShardID + 2, "/infitools/infitems.png")).setIconCoord(2, 5).setItemName("diamondShard");
        redstoneFragment = (new InfiTexture(materialShardID + 3, "/infitools/infitems.png")).setIconCoord(3, 5).setItemName("redstoneFragment");
        obsidianShard = (new InfiTexture(materialShardID + 4, "/infitools/infitems.png")).setIconCoord(4, 5).setItemName("obsidianShard");
        sandstoneShard = (new InfiTexture(materialShardID + 5, "/infitools/infitems.png")).setIconCoord(5, 5).setItemName("sandstoneShard");
        netherrackShard = (new InfiTexture(materialShardID + 6, "/infitools/infitems.png")).setIconCoord(6, 5).setItemName("netherrackShard");
        glowstoneFragment = (new InfiTexture(materialShardID + 7, "/infitools/infitems.png")).setIconCoord(7, 5).setItemName("glowstoneFragment");
        iceShard = (new InfiTexture(materialShardID + 8, "/infitools/infitems.png")).setIconCoord(8, 5).setItemName("iceShard");
        lavaFragment = (new InfiTexture(materialShardID + 9, "/infitools/infitems.png")).setIconCoord(9, 5).setItemName("lavaFragment");
        slimeFragment = (new InfiTexture(materialShardID + 10, "/infitools/infitems.png")).setIconCoord(10, 5).setItemName("slimeFragment");
        glassShard = (new InfiTexture(materialShardID + 11, "/infitools/infitems.png")).setIconCoord(11, 5).setItemName("glassShard");
        coalBits = (new InfiTexture(materialShardID + 12, "/infitools/infitems.png")).setIconCoord(12, 5).setItemName("coalBits");
        flintShard = (new InfiTexture(materialShardID + 13, "/infitools/infitems.png")).setIconCoord(14, 5).setItemName("flintShard");
        miniBrick = (new InfiTexture(materialShardID + 14, "/infitools/infitems.png")).setIconCoord(15, 5).setItemName("miniBrick");
        blazeFragment = (new InfiTexture(materialShardID + 15, "/infitools/infitems.png")).setIconCoord(0, 6).setItemName("blazeFragment");
        stoneRod = (new InfiTexture(rodID + 0, "/infitools/infitems.png")).setIconCoord(1, 0).setItemName("stoneRod");
        ironRod = (new InfiTexture(rodID + 1, "/infitools/infitems.png")).setIconCoord(2, 0).setItemName("ironRod");
        diamondRod = (new InfiTexture(rodID + 2, "/infitools/infitems.png")).setIconCoord(3, 0).setItemName("diamondRod");
        goldRod = (new InfiTexture(rodID + 3, "/infitools/infitems.png")).setIconCoord(4, 0).setItemName("goldRod");
        redstoneRod = (new InfiTexture(rodID + 4, "/infitools/infitems.png")).setIconCoord(5, 0).setItemName("redstoneRod");
        obsidianRod = (new InfiTexture(rodID + 5, "/infitools/infitems.png")).setIconCoord(6, 0).setItemName("obsidianRod");
        sandstoneRod = (new InfiTexture(rodID + 6, "/infitools/infitems.png")).setIconCoord(7, 0).setItemName("sandstoneRod");
        boneRod = (new InfiTexture(rodID + 7, "/infitools/infitems.png")).setIconCoord(8, 0).setItemName("boneRod");
        paperRod = (new InfiTexture(rodID + 8, "/infitools/infitems.png")).setIconCoord(9, 0).setItemName("paperRod");
        mossyRod = (new InfiTexture(rodID + 9, "/infitools/infitems.png")).setIconCoord(10, 0).setItemName("mossyRod");
        netherrackRod = (new InfiTexture(rodID + 10, "/infitools/infitems.png")).setIconCoord(11, 0).setItemName("netherrackRod");
        glowstoneRod = (new InfiTexture(rodID + 11, "/infitools/infitems.png")).setIconCoord(12, 0).setItemName("glowstoneRod");
        iceRod = (new InfiTexture(rodID + 12, "/infitools/infitems.png")).setIconCoord(13, 0).setItemName("iceRod");
        lavaRod = (new InfiTexture(rodID + 13, "/infitools/infitems.png")).setIconCoord(14, 0).setItemName("lavaRod");
        slimeRod = (new InfiTexture(rodID + 14, "/infitools/infitems.png")).setIconCoord(15, 0).setItemName("slimeRod");
        cactusRod = (new InfiTexture(rodID + 15, "/infitools/infitems.png")).setIconCoord(12, 1).setItemName("cactusRod");
        flintRod = (new InfiTexture(rodID + 16, "/infitools/infitems.png")).setIconCoord(13, 1).setItemName("flintRod");
        brickRod = (new InfiTexture(rodID + 17, "/infitools/infitems.png")).setIconCoord(14, 1).setItemName("brickRod");
        
        woodBucketEmpty = (new InfiBucketWood(woodBucketID + 0, 0)).setIconCoord(0, 3).setItemName("woodBucketEmpty");
        woodBucketWater = (new InfiBucketWood(woodBucketID + 1, Block.waterMoving.blockID)).setIconCoord(1, 3).setItemName("woodBucketWater");
        woodBucketMilk = (new InfiBucketWoodMilk(woodBucketID + 2)).setIconCoord(2, 3).setItemName("woodBucketMilk");
        woodBucketSand = (new InfiBucketWood(woodBucketID + 3, Block.sand.blockID)).setIconCoord(3, 3).setItemName("woodBucketSand");
        woodBucketGravel = (new InfiBucketWood(woodBucketID + 4, Block.gravel.blockID)).setIconCoord(4, 3).setItemName("woodBucketGravel");
        cactusBucketEmpty = (new InfiBucketCactus(cactusBucketID + 0, 0)).setIconCoord(5, 3).setItemName("cactusBucketEmpty");
        cactusBucketWater = (new InfiBucketCactus(cactusBucketID + 1, Block.waterMoving.blockID)).setIconCoord(6, 3).setItemName("cactusBucketWater");
        cactusBucketMilk = (new InfiBucketCactusMilk(cactusBucketID + 2)).setIconCoord(7, 3).setItemName("cactusBucketMilk");
        cactusBucketSand = (new InfiBucketCactus(cactusBucketID + 3, Block.sand.blockID)).setIconCoord(8, 3).setItemName("cactusBucketSand");
        cactusBucketGravel = (new InfiBucketCactus(cactusBucketID + 4, Block.gravel.blockID)).setIconCoord(9, 3).setItemName("cactusBucketGravel");
        goldBucketEmpty = (new InfiBucketGold(goldBucketID + 0, 0)).setIconCoord(10, 3).setItemName("goldBucketEmpty");
        goldBucketWater = (new InfiBucketGold(goldBucketID + 1, Block.waterMoving.blockID)).setIconCoord(11, 3).setItemName("goldBucketWater");
        goldBucketMilk = (new InfiBucketGoldMilk(goldBucketID + 2)).setIconCoord(12, 3).setItemName("goldBucketMilk");
        goldBucketSand = (new InfiBucketGold(goldBucketID + 3, Block.sand.blockID)).setIconCoord(13, 3).setItemName("goldBucketSand");
        goldBucketGravel = (new InfiBucketGold(goldBucketID + 4, Block.gravel.blockID)).setIconCoord(14, 3).setItemName("goldBucketGravel");
        goldBucketLava = (new InfiBucketGold(goldBucketID + 5, Block.lavaMoving.blockID)).setIconCoord(15, 3).setItemName("goldBucketLava");
        iceBucketEmpty = (new InfiBucketIce(iceBucketID + 0, 0)).setIconCoord(0, 4).setItemName("iceBucketEmpty");
        iceBucketIce = (new InfiBucketIce(iceBucketID + 1, Block.ice.blockID)).setIconCoord(1, 4).setItemName("iceBucketIce");
        iceBucketMilk = (new InfiBucketIceMilk(iceBucketID + 2)).setIconCoord(2, 4).setItemName("iceBucketMilk");
        iceBucketSand = (new InfiBucketIce(iceBucketID + 3, Block.sand.blockID)).setIconCoord(3, 4).setItemName("iceBucketSand");
        iceBucketGravel = (new InfiBucketIce(iceBucketID + 4, Block.gravel.blockID)).setIconCoord(4, 4).setItemName("iceBucketGravel");
        lavaBucketEmpty = (new InfiBucketLava(lavaBucketID + 0, 0)).setIconCoord(5, 4).setItemName("lavaBucketEmpty");
        lavaBucketGlass = (new InfiBucketLava(lavaBucketID + 1, Block.glass.blockID)).setIconCoord(6, 4).setItemName("lavaBucketGlass");
        lavaBucketCobblestone = (new InfiBucketLava(lavaBucketID + 2, Block.cobblestone.blockID)).setIconCoord(7, 4).setItemName("lavaBucketCobblestone");
        lavaBucketLava = (new InfiBucketLava(lavaBucketID + 3, Block.lavaMoving.blockID)).setIconCoord(8, 4).setItemName("lavaBucketLava");
        slimeBucketEmpty = (new InfiBucketSlime(slimeBucketID + 0, 0)).setIconCoord(9, 4).setItemName("slimeBucketEmpty");
        slimeBucketWater = (new InfiBucketSlime(slimeBucketID + 1, Block.waterMoving.blockID)).setIconCoord(10, 4).setItemName("slimeBucketWater");
        slimeBucketMilk = (new InfiBucketSlimeMilk(slimeBucketID + 2)).setIconCoord(11, 4).setItemName("slimeBucketMilk");
        slimeBucketSand = (new InfiBucketSlime(slimeBucketID + 3, Block.sand.blockID)).setIconCoord(12, 4).setItemName("slimeBucketSand");
        slimeBucketGravel = (new InfiBucketSlime(slimeBucketID + 4, Block.gravel.blockID)).setIconCoord(13, 4).setItemName("slimeBucketGravel");
        ironBucketSand = (new InfiBucketIron(ironBucketID + 0, Block.sand.blockID)).setIconCoord(14, 4).setItemName("ironBucketSand");
        ironBucketGravel = (new InfiBucketIron(ironBucketID + 1, Block.gravel.blockID)).setIconCoord(15, 4).setItemName("ironBucketGravel");
        woodBowlRawPumpkinPie = (new InfiFoodBowl(pumpkinPieID + 0, 7, 1)).setIconCoord(0, 2).setItemName("woodBowlRawPumpkinPie");
        woodBowlPumpkinPie = (new InfiFoodBowl(pumpkinPieID + 1, 12, 1)).setIconCoord(1, 2).setItemName("woodBowlPumpkinPie");
        stoneBowlEmpty = (new InfiTexture(bowlID + 0, "/infitools/infifood.png")).setIconCoord(2, 1).setItemName("stoneBowlEmpty");
        stoneBowlSoup = (new InfiFoodBowl(bowlID + 1, 10, 2)).setIconCoord(3, 1).setItemName("stoneBowlSoup");
        stoneBowlRawPumpkinPie = (new InfiFoodBowl(pumpkinPieID + 2, 7, 2)).setIconCoord(2, 2).setItemName("stoneBowlRawPumpkinPie");
        stoneBowlPumpkinPie = (new InfiFoodBowl(pumpkinPieID + 3, 12, 2)).setIconCoord(3, 2).setItemName("stoneBowlPumpkinPie");
        goldBowlEmpty = (new InfiTexture(bowlID + 4, "/infitools/infifood.png")).setIconCoord(6, 1).setItemName("goldBowlEmpty");
        goldBowlSoup = (new InfiFoodBowl(bowlID + 5, 10, 4)).setIconCoord(7, 1).setItemName("goldBowlSoup");
        goldBowlRawPumpkinPie = (new InfiFoodBowl(pumpkinPieID + 6, 7, 4)).setIconCoord(6, 2).setItemName("goldBowlRawPumpkinPie");
        goldBowlPumpkinPie = (new InfiFoodBowl(pumpkinPieID + 7, 12, 4)).setIconCoord(7, 2).setItemName("goldBowlPumpkinPie");
        netherrackBowlEmpty = (new InfiTexture(bowlID + 6, "/infitools/infifood.png")).setIconCoord(8, 1).setItemName("netherrackBowlEmpty");
        netherrackBowlSoup = (new InfiFoodBowl(bowlID + 7, 10, 5)).setIconCoord(9, 1).setItemName("netherrackBowlSoup");
        netherrackBowlRawPumpkinPie = (new InfiFoodBowl(pumpkinPieID + 8, 7, 5)).setIconCoord(8, 2).setItemName("netherrackBowlRawPumpkinPie");
        netherrackBowlPumpkinPie = (new InfiFoodBowl(pumpkinPieID + 9, 10, 5)).setIconCoord(9, 2).setItemName("netherrackBowlPumpkinPie");
        slimeBowlEmpty = (new InfiTexture(bowlID + 8, "/infitools/infifood.png")).setIconCoord(10, 1).setItemName("slimeBowlEmpty");
        slimeBowlSoup = (new InfiFoodBowl(bowlID + 9, 10, 6)).setIconCoord(11, 1).setItemName("slimeBowlSoup");
        slimeBowlRawPumpkinPie = (new InfiFoodBowl(pumpkinPieID + 10, 7, 6)).setIconCoord(10, 2).setItemName("slimeBowlRawPumpkinPie");
        slimeBowlPumpkinPie = (new InfiFoodBowl(pumpkinPieID + 11, 12, 6)).setIconCoord(11, 2).setItemName("slimeBowlPumpkinPie");
        cactusBowlEmpty = (new InfiTexture(bowlID + 10, "/infitools/infifood.png")).setIconCoord(12, 1).setItemName("cactusBowlEmpty");
        cactusBowlSoup = (new InfiFoodBowl(bowlID + 11, 10, 7)).setIconCoord(13, 1).setItemName("cactusBowlSoup");
        cactusBowlRawPumpkinPie = (new InfiFoodBowl(pumpkinPieID + 12, 7, 7)).setIconCoord(12, 2).setItemName("cactusBowlRawPumpkinPie");
        cactusBowlPumpkinPie = (new InfiFoodBowl(pumpkinPieID + 13, 12, 7)).setIconCoord(13, 2).setItemName("cactusBowlPumpkinPie");
        glassBowlEmpty = (new InfiTexture(bowlID + 12, "/infitools/infifood.png")).setIconCoord(14, 1).setItemName("glassBowlEmpty");
        glassBowlSoup = (new InfiFoodBowl(bowlID + 13, 10, 8)).setIconCoord(15, 1).setItemName("glassBowlSoup");
        glassBowlRawPumpkinPie = (new InfiFoodBowl(pumpkinPieID + 14, 7, 8)).setIconCoord(14, 2).setItemName("glassBowlRawPumpkinPie");
        glassBowlPumpkinPie = (new InfiFoodBowl(pumpkinPieID + 15, 12, 8)).setIconCoord(15, 2).setItemName("glassBowlPumpkinPie");
        stWoodSword = (new InfiToolSword(woodSwordID + 0, (int)((float)wDur * stMod), wDam, wType, stType)).setItemName("stWoodSword");
        saWoodSword = (new InfiToolSword(woodSwordID + 1, (int)((float)wDur * saMod), wDam, wType, saType)).setItemName("saWoodSword");
        bWoodSword = (new InfiToolSword(woodSwordID + 2, (int)((float)wDur * bMod), wDam, wType, bType)).setItemName("bWoodSword");
        pWoodSword = (new InfiToolSword(woodSwordID + 3, (int)((float)wDur * pMod), wDam, wType, pType)).setItemName("pWoodSword");
        nWoodSword = (new InfiToolSword(woodSwordID + 4, (int)((float)wDur * nMod), wDam, wType, nType)).setItemName("nWoodSword");
        sWoodSword = (new InfiToolSword(woodSwordID + 5, (int)((float)wDur * sMod), wDam, wType, sType)).setItemName("sWoodSword");
        cWoodSword = (new InfiToolSword(woodSwordID + 6, (int)((float)wDur * cMod), wDam, wType, cType)).setItemName("cWoodSword");
        fWoodSword = (new InfiToolSword(woodSwordID + 7, (int)((float)wDur * fMod), wDam, wType, fType)).setItemName("fWoodSword");
        brWoodSword = (new InfiToolSword(woodSwordID + 8, (int)((float)wDur * brMod), wDam, wType, brType)).setItemName("brWoodSword");
        stStoneSword = (new InfiToolSword(stoneSwordID + 0, (int)((float)stDur * stMod), stDam, stType, stType)).setItemName("stStoneSword");
        saStoneSword = (new InfiToolSword(stoneSwordID + 1, (int)((float)stDur * saMod), stDam, stType, saType)).setItemName("saStoneSword");
        bStoneSword = (new InfiToolSword(stoneSwordID + 2, (int)((float)stDur * bMod), stDam, stType, bType)).setItemName("bStoneSword");
        pStoneSword = (new InfiToolSword(stoneSwordID + 3, (int)((float)stDur * pMod), stDam, stType, pType)).setItemName("pStoneSword");
        mStoneSword = (new InfiToolSword(stoneSwordID + 4, (int)((float)stDur * mMod), stDam, stType, mType)).setItemName("mStoneSword");
        nStoneSword = (new InfiToolSword(stoneSwordID + 5, (int)((float)stDur * nMod), stDam, stType, nType)).setItemName("nStoneSword");
        iceStoneSword = (new InfiToolSword(stoneSwordID + 6, (int)((float)stDur * iceMod), stDam, stType, iceType)).setItemName("iceStoneSword");
        sStoneSword = (new InfiToolSword(stoneSwordID + 7, (int)((float)stDur * sMod), stDam, stType, sType)).setItemName("sStoneSword");
        cStoneSword = (new InfiToolSword(stoneSwordID + 8, (int)((float)stDur * cMod), stDam, stType, cType)).setItemName("cStoneSword");
        fStoneSword = (new InfiToolSword(stoneSwordID + 9, (int)((float)stDur * fMod), stDam, stType, fType)).setItemName("fStoneSword");
        brStoneSword = (new InfiToolSword(stoneSwordID + 10, (int)((float)stDur * brMod), stDam, stType, brType)).setItemName("brStoneSword");
        stIronSword = (new InfiToolSword(ironSwordID + 0, (int)((float)iDur * stMod), iDam, iType, stType)).setItemName("stIronSword");
        iIronSword = (new InfiToolSword(ironSwordID + 1, (int)((float)iDur * iMod), iDam, iType, iType)).setItemName("iIronSword");
        dIronSword = (new InfiToolSword(ironSwordID + 2, (int)((float)iDur * dMod), iDam, iType, dType)).setItemName("dIronSword");
        gIronSword = (new InfiToolSword(ironSwordID + 3, (int)((float)iDur * gMod), iDam, iType, gType)).setItemName("gIronSword");
        rIronSword = (new InfiToolSword(ironSwordID + 4, (int)((float)iDur * rMod), iDam, iType, rType)).setItemName("rIronSword");
        oIronSword = (new InfiToolSword(ironSwordID + 5, (int)((float)iDur * oMod), iDam, iType, oType)).setItemName("oIronSword");
        bIronSword = (new InfiToolSword(ironSwordID + 6, (int)((float)iDur * bMod), iDam, iType, bType)).setItemName("bIronSword");
        nIronSword = (new InfiToolSword(ironSwordID + 7, (int)((float)iDur * nMod), iDam, iType, nType)).setItemName("nIronSword");
        glIronSword = (new InfiToolSword(ironSwordID + 8, (int)((float)iDur * glMod), iDam, iType, glType)).setItemName("glIronSword");
        iceIronSword = (new InfiToolSword(ironSwordID + 9, (int)((float)iDur * iceMod), iDam, iType, iceType)).setItemName("iceIronSword");
        sIronSword = (new InfiToolSword(ironSwordID + 10, (int)((float)iDur * sMod), iDam, iType, sType)).setItemName("sIronSword");
        blIronSword = (new InfiToolSword(ironSwordID + 11, (int)((float)iDur * blMod), iDam, iType, blType)).setItemName("blIronSword");
        stDiamondSword = (new InfiToolSword(diamondSwordID + 0, (int)((float)dDur * stMod), dDam, dType, stType)).setItemName("stDiamondSword");
        iDiamondSword = (new InfiToolSword(diamondSwordID + 1, (int)((float)dDur * iMod), dDam, dType, iType)).setItemName("iDiamondSword");
        dDiamondSword = (new InfiToolSword(diamondSwordID + 2, (int)((float)dDur * dMod), dDam, dType, dType)).setItemName("dDiamondSword");
        gDiamondSword = (new InfiToolSword(diamondSwordID + 3, (int)((float)dDur * gMod), dDam, dType, gType)).setItemName("gDiamondSword");
        rDiamondSword = (new InfiToolSword(diamondSwordID + 4, (int)((float)dDur * rMod), dDam, dType, rType)).setItemName("rDiamondSword");
        oDiamondSword = (new InfiToolSword(diamondSwordID + 5, (int)((float)dDur * oMod), dDam, dType, oType)).setItemName("oDiamondSword");
        bDiamondSword = (new InfiToolSword(diamondSwordID + 6, (int)((float)dDur * bMod), dDam, dType, bType)).setItemName("bDiamondSword");
        mDiamondSword = (new InfiToolSword(diamondSwordID + 7, (int)((float)dDur * mMod), dDam, dType, mType)).setItemName("mDiamondSword");
        nDiamondSword = (new InfiToolSword(diamondSwordID + 8, (int)((float)dDur * nMod), dDam, dType, nType)).setItemName("nDiamondSword");
        glDiamondSword = (new InfiToolSword(diamondSwordID + 9, (int)((float)dDur * glMod), dDam, dType, glType)).setItemName("glDiamondSword");
        blDiamondSword = (new InfiToolSword(diamondSwordID + 10, (int)((float)dDur * blMod), dDam, dType, blType)).setItemName("blDiamondSword");
        stGoldSword = (new InfiToolSword(goldSwordID + 0, (int)((float)gDur * stMod), gDam, gType, stType)).setItemName("stGoldSword");
        gGoldSword = (new InfiToolSword(goldSwordID + 1, (int)((float)gDur * gMod), gDam, gType, gType)).setItemName("gGoldSword");
        oGoldSword = (new InfiToolSword(goldSwordID + 2, (int)((float)gDur * oMod), gDam, gType, oType)).setItemName("oGoldSword");
        saGoldSword = (new InfiToolSword(goldSwordID + 3, (int)((float)gDur * saMod), gDam, gType, saType)).setItemName("saGoldSword");
        bGoldSword = (new InfiToolSword(goldSwordID + 4, (int)((float)gDur * bMod), gDam, gType, bType)).setItemName("bGoldSword");
        mGoldSword = (new InfiToolSword(goldSwordID + 5, (int)((float)gDur * mMod), gDam, gType, mType)).setItemName("mGoldSword");
        nGoldSword = (new InfiToolSword(goldSwordID + 6, (int)((float)gDur * nMod), gDam, gType, nType)).setItemName("nGoldSword");
        glGoldSword = (new InfiToolSword(goldSwordID + 7, (int)((float)gDur * glMod), gDam, gType, glType)).setItemName("glGoldSword");
        iceGoldSword = (new InfiToolSword(goldSwordID + 8, (int)((float)gDur * iceMod), gDam, gType, iceType)).setItemName("iceGoldSword");
        sGoldSword = (new InfiToolSword(goldSwordID + 9, (int)((float)gDur * sMod), gDam, gType, sType)).setItemName("sGoldSword");
        fGoldSword = (new InfiToolSword(goldSwordID + 10, (int)((float)gDur * fMod), gDam, gType, fType)).setItemName("fGoldSword");
        wRedstoneSword = (new InfiToolSword(redstoneSwordID + 0, (int)((float)rDur * wMod), rDam, rType, wType)).setItemName("wRedstoneSword");
        stRedstoneSword = (new InfiToolSword(redstoneSwordID + 1, (int)((float)rDur * stMod), rDam, rType, stType)).setItemName("stRedstoneSword");
        iRedstoneSword = (new InfiToolSword(redstoneSwordID + 2, (int)((float)rDur * iMod), rDam, rType, iType)).setItemName("iRedstoneSword");
        dRedstoneSword = (new InfiToolSword(redstoneSwordID + 3, (int)((float)rDur * dMod), rDam, rType, dType)).setItemName("dRedstoneSword");
        rRedstoneSword = (new InfiToolSword(redstoneSwordID + 4, (int)((float)rDur * rMod), rDam, rType, rType)).setItemName("rRedstoneSword");
        oRedstoneSword = (new InfiToolSword(redstoneSwordID + 5, (int)((float)rDur * oMod), rDam, rType, oType)).setItemName("oRedstoneSword");
        bRedstoneSword = (new InfiToolSword(redstoneSwordID + 6, (int)((float)rDur * bMod), rDam, rType, bType)).setItemName("bRedstoneSword");
        mRedstoneSword = (new InfiToolSword(redstoneSwordID + 7, (int)((float)rDur * mMod), rDam, rType, mType)).setItemName("mRedstoneSword");
        glRedstoneSword = (new InfiToolSword(redstoneSwordID + 8, (int)((float)rDur * glMod), rDam, rType, glType)).setItemName("glRedstoneSword");
        sRedstoneSword = (new InfiToolSword(redstoneSwordID + 9, (int)((float)rDur * sMod), rDam, rType, sType)).setItemName("sRedstoneSword");
        blRedstoneSword = (new InfiToolSword(redstoneSwordID + 10, (int)((float)rDur * blMod), rDam, rType, blType)).setItemName("blRedstoneSword");
        wObsidianSword = (new InfiToolSword(obsidianSwordID + 0, (int)((float)oDur * wMod), oDam, oType, wType)).setItemName("wObsidianSword");
        stObsidianSword = (new InfiToolSword(obsidianSwordID + 1, (int)((float)oDur * stMod), oDam, oType, stType)).setItemName("stObsidianSword");
        iObsidianSword = (new InfiToolSword(obsidianSwordID + 2, (int)((float)oDur * iMod), oDam, oType, iType)).setItemName("iObsidianSword");
        dObsidianSword = (new InfiToolSword(obsidianSwordID + 3, (int)((float)oDur * dMod), oDam, oType, dType)).setItemName("dObsidianSword");
        gObsidianSword = (new InfiToolSword(obsidianSwordID + 4, (int)((float)oDur * gMod), oDam, oType, gType)).setItemName("gObsidianSword");
        rObsidianSword = (new InfiToolSword(obsidianSwordID + 5, (int)((float)oDur * rMod), oDam, oType, rType)).setItemName("rObsidianSword");
        oObsidianSword = (new InfiToolSword(obsidianSwordID + 6, (int)((float)oDur * oMod), oDam, oType, oType)).setItemName("oObsidianSword");
        bObsidianSword = (new InfiToolSword(obsidianSwordID + 7, (int)((float)oDur * bMod), oDam, oType, bType)).setItemName("bObsidianSword");
        nObsidianSword = (new InfiToolSword(obsidianSwordID + 8, (int)((float)oDur * nMod), oDam, oType, nType)).setItemName("nObsidianSword");
        glObsidianSword = (new InfiToolSword(obsidianSwordID + 9, (int)((float)oDur * glMod), oDam, oType, glType)).setItemName("glObsidianSword");
        sObsidianSword = (new InfiToolSword(obsidianSwordID + 10, (int)((float)oDur * sMod), oDam, oType, sType)).setItemName("sObsidianSword");
        fObsidianSword = (new InfiToolSword(obsidianSwordID + 11, (int)((float)oDur * fMod), oDam, oType, fType)).setItemName("fObsidianSword");
        blObsidianSword = (new InfiToolSword(obsidianSwordID + 12, (int)((float)oDur * blMod), oDam, oType, blType)).setItemName("blObsidianSword");
        wSandstoneSword = (new InfiToolSword(sandstoneSwordID + 0, (int)((float)saDur * wMod), saDam, saType, wType)).setItemName("wSandstoneSword");
        stSandstoneSword = (new InfiToolSword(sandstoneSwordID + 1, (int)((float)saDur * stMod), saDam, saType, stType)).setItemName("stSandstoneSword");
        saSandstoneSword = (new InfiToolSword(sandstoneSwordID + 2, (int)((float)saDur * saMod), saDam, saType, saType)).setItemName("saSandstoneSword");
        bSandstoneSword = (new InfiToolSword(sandstoneSwordID + 3, (int)((float)saDur * bMod), saDam, saType, bType)).setItemName("bSandstoneSword");
        pSandstoneSword = (new InfiToolSword(sandstoneSwordID + 4, (int)((float)saDur * pMod), saDam, saType, pType)).setItemName("pSandstoneSword");
        nSandstoneSword = (new InfiToolSword(sandstoneSwordID + 5, (int)((float)saDur * nMod), saDam, saType, nType)).setItemName("nSandstoneSword");
        iceSandstoneSword = (new InfiToolSword(sandstoneSwordID + 6, (int)((float)saDur * iceMod), saDam, saType, iceType)).setItemName("iceSandstoneSword");
        sSandstoneSword = (new InfiToolSword(sandstoneSwordID + 7, (int)((float)saDur * sMod), saDam, saType, sType)).setItemName("sSandstoneSword");
        cSandstoneSword = (new InfiToolSword(sandstoneSwordID + 8, (int)((float)saDur * cMod), saDam, saType, cType)).setItemName("cSandstoneSword");
        fSandstoneSword = (new InfiToolSword(sandstoneSwordID + 9, (int)((float)saDur * fMod), saDam, saType, fType)).setItemName("fSandstoneSword");
        brSandstoneSword = (new InfiToolSword(sandstoneSwordID + 10, (int)((float)saDur * brMod), saDam, saType, brType)).setItemName("brSandstoneSword");
        wBoneSword = (new InfiToolSword(boneSwordID + 0, (int)((float)bDur * wMod), bDam, bType, wType)).setItemName("wBoneSword");
        stBoneSword = (new InfiToolSword(boneSwordID + 1, (int)((float)bDur * stMod), bDam, bType, stType)).setItemName("stBoneSword");
        iBoneSword = (new InfiToolSword(boneSwordID + 2, (int)((float)bDur * iMod), bDam, bType, iType)).setItemName("iBoneSword");
        dBoneSword = (new InfiToolSword(boneSwordID + 3, (int)((float)bDur * iMod), bDam, bType, dType)).setItemName("dBoneSword");
        rBoneSword = (new InfiToolSword(boneSwordID + 4, (int)((float)bDur * rMod), bDam, bType, rType)).setItemName("rBoneSword");
        oBoneSword = (new InfiToolSword(boneSwordID + 5, (int)((float)bDur * oMod), bDam, bType, oType)).setItemName("oBoneSword");
        bBoneSword = (new InfiToolSword(boneSwordID + 6, (int)((float)bDur * bMod), bDam, bType, bType)).setItemName("bBoneSword");
        mBoneSword = (new InfiToolSword(boneSwordID + 7, (int)((float)bDur * mMod), bDam, bType, mType)).setItemName("mBoneSword");
        nBoneSword = (new InfiToolSword(boneSwordID + 8, (int)((float)bDur * nMod), bDam, bType, nType)).setItemName("nBoneSword");
        glBoneSword = (new InfiToolSword(boneSwordID + 9, (int)((float)bDur * gMod), bDam, bType, glType)).setItemName("glBoneSword");
        sBoneSword = (new InfiToolSword(boneSwordID + 10, (int)((float)bDur * sMod), bDam, bType, sType)).setItemName("sBoneSword");
        cBoneSword = (new InfiToolSword(boneSwordID + 11, (int)((float)bDur * cMod), bDam, bType, cType)).setItemName("cBoneSword");
        fBoneSword = (new InfiToolSword(boneSwordID + 12, (int)((float)bDur * fMod), bDam, bType, fType)).setItemName("fBoneSword");
        brBoneSword = (new InfiToolSword(boneSwordID + 13, (int)((float)bDur * brMod), bDam, bType, brType)).setItemName("brBoneSword");
        blBoneSword = (new InfiToolSword(boneSwordID + 14, (int)((float)bDur * blMod), bDam, bType, blType)).setItemName("blBoneSword");
        wPaperSword = (new InfiToolSword(paperSwordID + 0, (int)((float)pDur * wMod), pDam, pType, wType)).setItemName("wPaperSword");
        saPaperSword = (new InfiToolSword(paperSwordID + 1, (int)((float)pDur * saMod), pDam, pType, saType)).setItemName("saPaperSword");
        bPaperSword = (new InfiToolSword(paperSwordID + 2, (int)((float)pDur * bMod), pDam, pType, bType)).setItemName("bPaperSword");
        pPaperSword = (new InfiToolSword(paperSwordID + 3, (int)((float)pDur * pMod), pDam, pType, pType)).setItemName("pPaperSword");
        sPaperSword = (new InfiToolSword(paperSwordID + 4, (int)((float)pDur * sMod), pDam, pType, sType)).setItemName("sPaperSword");
        cPaperSword = (new InfiToolSword(paperSwordID + 5, (int)((float)pDur * cMod), pDam, pType, cType)).setItemName("cPaperSword");
        brPaperSword = (new InfiToolSword(paperSwordID + 6, (int)((float)pDur * brMod), pDam, pType, brType)).setItemName("brPaperSword");
        stMossySword = (new InfiToolSword(mossySwordID + 0, (int)((float)mDur * stMod), mDam, mType, stType)).setItemName("stMossySword");
        dMossySword = (new InfiToolSword(mossySwordID + 1, (int)((float)mDur * iMod), mDam, mType, dType)).setItemName("dMossySword");
        rMossySword = (new InfiToolSword(mossySwordID + 2, (int)((float)mDur * rMod), mDam, mType, rType)).setItemName("rMossySword");
        bMossySword = (new InfiToolSword(mossySwordID + 3, (int)((float)mDur * bMod), mDam, mType, bType)).setItemName("bMossySword");
        mMossySword = (new InfiToolSword(mossySwordID + 4, (int)((float)mDur * mMod), mDam, mType, mType)).setItemName("mMossySword");
        glMossySword = (new InfiToolSword(mossySwordID + 5, (int)((float)mDur * gMod), mDam, mType, glType)).setItemName("glMossySword");
        wNetherrackSword = (new InfiToolSword(netherrackSwordID + 0, (int)((float)nDur * wMod), nDam, nType, wType)).setItemName("wNetherrackSword");
        stNetherrackSword = (new InfiToolSword(netherrackSwordID + 1, (int)((float)nDur * stMod), nDam, nType, stType)).setItemName("stNetherrackSword");
        iNetherrackSword = (new InfiToolSword(netherrackSwordID + 2, (int)((float)nDur * iMod), nDam, nType, iType)).setItemName("iNetherrackSword");
        rNetherrackSword = (new InfiToolSword(netherrackSwordID + 3, (int)((float)nDur * rMod), nDam, nType, rType)).setItemName("rNetherrackSword");
        oNetherrackSword = (new InfiToolSword(netherrackSwordID + 4, (int)((float)nDur * oMod), nDam, nType, oType)).setItemName("oNetherrackSword");
        saNetherrackSword = (new InfiToolSword(netherrackSwordID + 5, (int)((float)nDur * saMod), nDam, nType, saType)).setItemName("saNetherrackSword");
        bNetherrackSword = (new InfiToolSword(netherrackSwordID + 6, (int)((float)nDur * bMod), nDam, nType, bType)).setItemName("bNetherrackSword");
        mNetherrackSword = (new InfiToolSword(netherrackSwordID + 7, (int)((float)nDur * mMod), nDam, nType, mType)).setItemName("mNetherrackSword");
        nNetherrackSword = (new InfiToolSword(netherrackSwordID + 8, (int)((float)nDur * nMod), nDam, nType, nType)).setItemName("nNetherrackSword");
        glNetherrackSword = (new InfiToolSword(netherrackSwordID + 9, (int)((float)nDur * glMod), nDam, nType, glType)).setItemName("glNetherrackSword");
        iceNetherrackSword = (new InfiToolSword(netherrackSwordID + 10, (int)((float)nDur * iceMod), nDam, nType, iceType)).setItemName("iceNetherrackSword");
        sNetherrackSword = (new InfiToolSword(netherrackSwordID + 11, (int)((float)nDur * sMod), nDam, nType, sType)).setItemName("sNetherrackSword");
        cNetherrackSword = (new InfiToolSword(netherrackSwordID + 12, (int)((float)nDur * cMod), nDam, nType, cType)).setItemName("cNetherrackSword");
        fNetherrackSword = (new InfiToolSword(netherrackSwordID + 13, (int)((float)nDur * fMod), nDam, nType, fType)).setItemName("fNetherrackSword");
        brNetherrackSword = (new InfiToolSword(netherrackSwordID + 14, (int)((float)nDur * brMod), nDam, nType, brType)).setItemName("brNetherrackSword");
        blNetherrackSword = (new InfiToolSword(netherrackSwordID + 15, (int)((float)nDur * blMod), nDam, nType, blType)).setItemName("blNetherrackSword");
        wGlowstoneSword = (new InfiToolSword(glowstoneSwordID + 0, (int)((float)glDur * wMod), glDam, glType, wType)).setItemName("wGlowstoneSword");
        stGlowstoneSword = (new InfiToolSword(glowstoneSwordID + 1, (int)((float)glDur * stMod), glDam, glType, stType)).setItemName("stGlowstoneSword");
        iGlowstoneSword = (new InfiToolSword(glowstoneSwordID + 2, (int)((float)glDur * iMod), glDam, glType, iType)).setItemName("iGlowstoneSword");
        dGlowstoneSword = (new InfiToolSword(glowstoneSwordID + 3, (int)((float)glDur * iMod), glDam, glType, dType)).setItemName("dGlowstoneSword");
        rGlowstoneSword = (new InfiToolSword(glowstoneSwordID + 4, (int)((float)glDur * rMod), glDam, glType, rType)).setItemName("rGlowstoneSword");
        oGlowstoneSword = (new InfiToolSword(glowstoneSwordID + 5, (int)((float)glDur * oMod), glDam, glType, oType)).setItemName("oGlowstoneSword");
        bGlowstoneSword = (new InfiToolSword(glowstoneSwordID + 6, (int)((float)glDur * bMod), glDam, glType, bType)).setItemName("bGlowstoneSword");
        mGlowstoneSword = (new InfiToolSword(glowstoneSwordID + 7, (int)((float)glDur * mMod), glDam, glType, mType)).setItemName("mGlowstoneSword");
        nGlowstoneSword = (new InfiToolSword(glowstoneSwordID + 8, (int)((float)glDur * nMod), glDam, glType, nType)).setItemName("nGlowstoneSword");
        glGlowstoneSword = (new InfiToolSword(glowstoneSwordID + 9, (int)((float)glDur * gMod), glDam, glType, glType)).setItemName("glGlowstoneSword");
        iceGlowstoneSword = (new InfiToolSword(glowstoneSwordID + 10, (int)((float)glDur * iceMod), glDam, glType, iceType)).setItemName("iceGlowstoneSword");
        lGlowstoneSword = (new InfiToolSword(glowstoneSwordID + 11, (int)((float)glDur * gMod), glDam, glType, lType)).setItemName("lGlowstoneSword");
        sGlowstoneSword = (new InfiToolSword(glowstoneSwordID + 12, (int)((float)glDur * sMod), glDam, glType, sType)).setItemName("sGlowstoneSword");
        blGlowstoneSword = (new InfiToolSword(glowstoneSwordID + 13, (int)((float)glDur * blMod), glDam, glType, blType)).setItemName("blGlowstoneSword");
        wIceSword = (new InfiToolSword(iceSwordID + 0, (int)((float)iceDur * wMod), iceDam, iceType, wType)).setItemName("wIceSword");
        stIceSword = (new InfiToolSword(iceSwordID + 1, (int)((float)iceDur * stMod), iceDam, iceType, stType)).setItemName("stIceSword");
        iIceSword = (new InfiToolSword(iceSwordID + 2, (int)((float)iceDur * iMod), iceDam, iceType, iType)).setItemName("iIceSword");
        dIceSword = (new InfiToolSword(iceSwordID + 3, (int)((float)iceDur * dMod), iceDam, iceType, dType)).setItemName("dIceSword");
        gIceSword = (new InfiToolSword(iceSwordID + 4, (int)((float)iceDur * gMod), iceDam, iceType, gType)).setItemName("gIceSword");
        rIceSword = (new InfiToolSword(iceSwordID + 5, (int)((float)iceDur * rMod), iceDam, iceType, rType)).setItemName("rIceSword");
        oIceSword = (new InfiToolSword(iceSwordID + 6, (int)((float)iceDur * oMod), iceDam, iceType, oType)).setItemName("oIceSword");
        saIceSword = (new InfiToolSword(iceSwordID + 7, (int)((float)iceDur * saMod), iceDam, iceType, saType)).setItemName("saIceSword");
        bIceSword = (new InfiToolSword(iceSwordID + 8, (int)((float)iceDur * bMod), iceDam, iceType, bType)).setItemName("bIceSword");
        glIceSword = (new InfiToolSword(iceSwordID + 9, (int)((float)iceDur * gMod), iceDam, iceType, glType)).setItemName("glIceSword");
        iceIceSword = (new InfiToolSword(iceSwordID + 10, (int)((float)iceDur * iMod), iceDam, iceType, iceType)).setItemName("iceIceSword");
        sIceSword = (new InfiToolSword(iceSwordID + 11, (int)((float)iceDur * sMod), iceDam, iceType, sType)).setItemName("sIceSword");
        cIceSword = (new InfiToolSword(iceSwordID + 12, (int)((float)iceDur * cMod), iceDam, iceType, cType)).setItemName("cIceSword");
        fIceSword = (new InfiToolSword(iceSwordID + 13, (int)((float)iceDur * fMod), iceDam, iceType, fType)).setItemName("fIceSword");
        brIceSword = (new InfiToolSword(iceSwordID + 14, (int)((float)iceDur * brMod), iceDam, iceType, brType)).setItemName("brIceSword");
        dLavaSword = (new InfiToolSword(lavaSwordID + 0, (int)((float)lDur * iMod), lDam, lType, dType)).setItemName("dLavaSword");
        rLavaSword = (new InfiToolSword(lavaSwordID + 1, (int)((float)lDur * rMod), lDam, lType, rType)).setItemName("rLavaSword");
        bLavaSword = (new InfiToolSword(lavaSwordID + 2, (int)((float)lDur * bMod), lDam, lType, bType)).setItemName("bLavaSword");
        nLavaSword = (new InfiToolSword(lavaSwordID + 3, (int)((float)lDur * nMod), lDam, lType, nType)).setItemName("nLavaSword");
        glLavaSword = (new InfiToolSword(lavaSwordID + 4, (int)((float)lDur * gMod), lDam, lType, glType)).setItemName("glLavaSword");
        lLavaSword = (new InfiToolSword(lavaSwordID + 5, (int)((float)lDur * lMod), lDam, lType, lType)).setItemName("lLavaSword");
        blLavaSword = (new InfiToolSword(lavaSwordID + 6, (int)((float)lDur * blMod), lDam, lType, blType)).setItemName("blLavaSword");
        wSlimeSword = (new InfiToolSword(slimeSwordID + 0, (int)((float)sDur * wMod), sDam, sType, wType)).setItemName("wSlimeSword");
        stSlimeSword = (new InfiToolSword(slimeSwordID + 1, (int)((float)sDur * stMod), sDam, sType, stType)).setItemName("stSlimeSword");
        iSlimeSword = (new InfiToolSword(slimeSwordID + 2, (int)((float)sDur * iMod), sDam, sType, iType)).setItemName("iSlimeSword");
        dSlimeSword = (new InfiToolSword(slimeSwordID + 3, (int)((float)sDur * iMod), sDam, sType, dType)).setItemName("dSlimeSword");
        gSlimeSword = (new InfiToolSword(slimeSwordID + 4, (int)((float)sDur * gMod), sDam, sType, gType)).setItemName("gSlimeSword");
        rSlimeSword = (new InfiToolSword(slimeSwordID + 5, (int)((float)sDur * rMod), sDam, sType, rType)).setItemName("rSlimeSword");
        oSlimeSword = (new InfiToolSword(slimeSwordID + 6, (int)((float)sDur * oMod), sDam, sType, oType)).setItemName("oSlimeSword");
        saSlimeSword = (new InfiToolSword(slimeSwordID + 7, (int)((float)sDur * saMod), sDam, sType, saType)).setItemName("saSlimeSword");
        bSlimeSword = (new InfiToolSword(slimeSwordID + 8, (int)((float)sDur * bMod), sDam, sType, bType)).setItemName("bSlimeSword");
        pSlimeSword = (new InfiToolSword(slimeSwordID + 9, (int)((float)sDur * pMod), sDam, sType, pType)).setItemName("pSlimeSword");
        mSlimeSword = (new InfiToolSword(slimeSwordID + 10, (int)((float)sDur * mMod), sDam, sType, mType)).setItemName("mSlimeSword");
        nSlimeSword = (new InfiToolSword(slimeSwordID + 11, (int)((float)sDur * nMod), sDam, sType, nType)).setItemName("nSlimeSword");
        glSlimeSword = (new InfiToolSword(slimeSwordID + 12, (int)((float)sDur * gMod), sDam, sType, glType)).setItemName("glSlimeSword");
        iceSlimeSword = (new InfiToolSword(slimeSwordID + 13, (int)((float)sDur * iMod), sDam, sType, iceType)).setItemName("iceSlimeSword");
        lSlimeSword = (new InfiToolSword(slimeSwordID + 14, (int)((float)sDur * lMod), sDam, sType, lType)).setItemName("lSlimeSword");
        sSlimeSword = (new InfiToolSword(slimeSwordID + 15, (int)((float)sDur * sMod), sDam, sType, sType)).setItemName("sSlimeSword");
        cSlimeSword = (new InfiToolSword(slimeSwordID + 16, (int)((float)sDur * cMod), sDam, sType, cType)).setItemName("cSlimeSword");
        fSlimeSword = (new InfiToolSword(slimeSwordID + 17, (int)((float)sDur * fMod), sDam, sType, fType)).setItemName("fSlimeSword");
        brSlimeSword = (new InfiToolSword(slimeSwordID + 18, (int)((float)sDur * brMod), sDam, sType, brType)).setItemName("brSlimeSword");
        blSlimeSword = (new InfiToolSword(slimeSwordID + 19, (int)((float)sDur * blMod), sDam, sType, blType)).setItemName("blSlimeSword");
        wCactusSword = (new InfiToolSword(cactusSwordID + 0, (int)((float)cDur * wMod), cDam, cType, wType)).setItemName("wCactusSword");
        stCactusSword = (new InfiToolSword(cactusSwordID + 1, (int)((float)cDur * stMod), cDam, cType, stType)).setItemName("stCactusSword");
        saCactusSword = (new InfiToolSword(cactusSwordID + 2, (int)((float)cDur * saMod), cDam, cType, saType)).setItemName("saCactusSword");
        bCactusSword = (new InfiToolSword(cactusSwordID + 3, (int)((float)cDur * bMod), cDam, cType, bType)).setItemName("bCactusSword");
        pCactusSword = (new InfiToolSword(cactusSwordID + 4, (int)((float)cDur * pMod), cDam, cType, pType)).setItemName("pCactusSword");
        nCactusSword = (new InfiToolSword(cactusSwordID + 5, (int)((float)cDur * nMod), cDam, cType, nType)).setItemName("nCactusSword");
        sCactusSword = (new InfiToolSword(cactusSwordID + 6, (int)((float)cDur * sMod), cDam, cType, sType)).setItemName("sCactusSword");
        cCactusSword = (new InfiToolSword(cactusSwordID + 7, (int)((float)cDur * cMod), cDam, cType, cType)).setItemName("cCactusSword");
        fCactusSword = (new InfiToolSword(cactusSwordID + 8, (int)((float)cDur * fMod), cDam, cType, fType)).setItemName("fCactusSword");
        brCactusSword = (new InfiToolSword(cactusSwordID + 9, (int)((float)cDur * brMod), cDam, cType, brType)).setItemName("brCactusSword");
        wFlintSword = (new InfiToolSword(flintSwordID + 0, (int)((float)fDur * wMod), fDam, fType, wType)).setItemName("wFlintSword");
        stFlintSword = (new InfiToolSword(flintSwordID + 1, (int)((float)fDur * stMod), fDam, fType, stType)).setItemName("stFlintSword");
        iFlintSword = (new InfiToolSword(flintSwordID + 2, (int)((float)fDur * iMod), fDam, fType, iType)).setItemName("iFlintSword");
        gFlintSword = (new InfiToolSword(flintSwordID + 3, (int)((float)fDur * gMod), fDam, fType, gType)).setItemName("gFlintSword");
        oFlintSword = (new InfiToolSword(flintSwordID + 4, (int)((float)fDur * oMod), fDam, fType, oType)).setItemName("oFlintSword");
        saFlintSword = (new InfiToolSword(flintSwordID + 5, (int)((float)fDur * saMod), fDam, fType, saType)).setItemName("saFlintSword");
        bFlintSword = (new InfiToolSword(flintSwordID + 6, (int)((float)fDur * bMod), fDam, fType, bType)).setItemName("bFlintSword");
        nFlintSword = (new InfiToolSword(flintSwordID + 7, (int)((float)fDur * nMod), fDam, fType, nType)).setItemName("nFlintSword");
        iceFlintSword = (new InfiToolSword(flintSwordID + 8, (int)((float)fDur * wMod), fDam, fType, iceType)).setItemName("iceFlintSword");
        sFlintSword = (new InfiToolSword(flintSwordID + 9, (int)((float)fDur * sMod), fDam, fType, sType)).setItemName("sFlintSword");
        cFlintSword = (new InfiToolSword(flintSwordID + 10, (int)((float)fDur * cMod), fDam, fType, cType)).setItemName("cFlintSword");
        fFlintSword = (new InfiToolSword(flintSwordID + 11, (int)((float)fDur * fMod), fDam, fType, fType)).setItemName("fFlintSword");
        brFlintSword = (new InfiToolSword(flintSwordID + 12, (int)((float)fDur * brMod), fDam, fType, brType)).setItemName("brFlintSword");
        blFlintSword = (new InfiToolSword(flintSwordID + 13, (int)((float)fDur * blMod), fDam, fType, blType)).setItemName("blFlintSword");
        wBrickSword = (new InfiToolSword(brickSwordID + 0, (int)((float)brDur * wMod), brDam, brType, wType)).setItemName("wBrickSword");
        stBrickSword = (new InfiToolSword(brickSwordID + 1, (int)((float)brDur * stMod), brDam, brType, stType)).setItemName("stBrickSword");
        saBrickSword = (new InfiToolSword(brickSwordID + 2, (int)((float)brDur * saMod), brDam, brType, saType)).setItemName("saBrickSword");
        bBrickSword = (new InfiToolSword(brickSwordID + 3, (int)((float)brDur * bMod), brDam, brType, bType)).setItemName("bBrickSword");
        pBrickSword = (new InfiToolSword(brickSwordID + 4, (int)((float)brDur * pMod), brDam, brType, pType)).setItemName("pBrickSword");
        nBrickSword = (new InfiToolSword(brickSwordID + 5, (int)((float)brDur * nMod), brDam, brType, nType)).setItemName("nBrickSword");
        iceBrickSword = (new InfiToolSword(brickSwordID + 6, (int)((float)brDur * iceMod), brDam, brType, iceType)).setItemName("iceBrickSword");
        sBrickSword = (new InfiToolSword(brickSwordID + 7, (int)((float)brDur * sMod), brDam, brType, sType)).setItemName("sBrickSword");
        cBrickSword = (new InfiToolSword(brickSwordID + 8, (int)((float)brDur * cMod), brDam, brType, cType)).setItemName("cBrickSword");
        fBrickSword = (new InfiToolSword(brickSwordID + 9, (int)((float)brDur * fMod), brDam, brType, fType)).setItemName("fBrickSword");
        brBrickSword = (new InfiToolSword(brickSwordID + 10, (int)((float)brDur * brMod), brDam, brType, brType)).setItemName("brBrickSword");
        dBlazeSword = (new InfiToolSword(blazeSwordID + 0, (int)((float)blDur * dMod), blDam, blType, dType)).setItemName("wBlazeSword");
        rBlazeSword = (new InfiToolSword(blazeSwordID + 1, (int)((float)blDur * rMod), blDam, blType, rType)).setItemName("rBlazeSword");
        bBlazeSword = (new InfiToolSword(blazeSwordID + 2, (int)((float)blDur * bMod), blDam, blType, bType)).setItemName("bBlazeSword");
        nBlazeSword = (new InfiToolSword(blazeSwordID + 3, (int)((float)blDur * nMod), blDam, blType, nType)).setItemName("nBlazeSword");
        glBlazeSword = (new InfiToolSword(blazeSwordID + 4, (int)((float)blDur * glMod), blDam, blType, glType)).setItemName("glBlazeSword");
        lBlazeSword = (new InfiToolSword(blazeSwordID + 5, (int)((float)blDur * lMod), blDam, blType, lType)).setItemName("lBlazeSword");
        fBlazeSword = (new InfiToolSword(blazeSwordID + 6, (int)((float)blDur * fMod), blDam, blType, fType)).setItemName("fBlazeSword");
        blBlazeSword = (new InfiToolSword(blazeSwordID + 7, (int)((float)blDur * bMod), blDam, blType, bType)).setItemName("blBlazeSword");
        stWoodPickaxe = (new InfiToolPickaxe(woodPickaxeID + 0, wLevel, (int)((float)wDur * stMod), wSpeed, wDam, wType, stType)).setItemName("stWoodPickaxe");
        saWoodPickaxe = (new InfiToolPickaxe(woodPickaxeID + 1, wLevel, (int)((float)wDur * saMod), wSpeed, wDam, wType, saType)).setItemName("saWoodPickaxe");
        bWoodPickaxe = (new InfiToolPickaxe(woodPickaxeID + 2, wLevel, (int)((float)wDur * bMod), wSpeed, wDam, wType, bType)).setItemName("bWoodPickaxe");
        pWoodPickaxe = (new InfiToolPickaxe(woodPickaxeID + 3, wLevel, (int)((float)wDur * pMod), wSpeed, wDam, wType, pType)).setItemName("pWoodPickaxe");
        nWoodPickaxe = (new InfiToolPickaxe(woodPickaxeID + 4, wLevel, (int)((float)wDur * nMod), wSpeed, wDam, wType, nType)).setItemName("nWoodPickaxe");
        sWoodPickaxe = (new InfiToolPickaxe(woodPickaxeID + 5, wLevel, (int)((float)wDur * sMod), wSpeed, wDam, wType, sType)).setItemName("sWoodPickaxe");
        cWoodPickaxe = (new InfiToolPickaxe(woodPickaxeID + 6, wLevel, (int)((float)wDur * cMod), wSpeed, wDam, wType, cType)).setItemName("cWoodPickaxe");
        fWoodPickaxe = (new InfiToolPickaxe(woodPickaxeID + 7, wLevel, (int)((float)wDur * fMod), wSpeed, wDam, wType, fType)).setItemName("fWoodPickaxe");
        brWoodPickaxe = (new InfiToolPickaxe(woodPickaxeID + 8, wLevel, (int)((float)wDur * brMod), wSpeed, wDam, wType, brType)).setItemName("brWoodPickaxe");
        stStonePickaxe = (new InfiToolPickaxe(stonePickaxeID + 0, stLevel, (int)((float)stDur * stMod), stSpeed, stDam, stType, stType)).setItemName("stStonePickaxe");
        saStonePickaxe = (new InfiToolPickaxe(stonePickaxeID + 1, stLevel, (int)((float)stDur * saMod), stSpeed, stDam, stType, saType)).setItemName("saStonePickaxe");
        bStonePickaxe = (new InfiToolPickaxe(stonePickaxeID + 2, stLevel, (int)((float)stDur * bMod), stSpeed, stDam, stType, bType)).setItemName("bStonePickaxe");
        pStonePickaxe = (new InfiToolPickaxe(stonePickaxeID + 3, stLevel, (int)((float)stDur * pMod), stSpeed, stDam, stType, pType)).setItemName("pStonePickaxe");
        mStonePickaxe = (new InfiToolPickaxe(stonePickaxeID + 4, stLevel, (int)((float)stDur * mMod), stSpeed, stDam, stType, mType)).setItemName("mStonePickaxe");
        nStonePickaxe = (new InfiToolPickaxe(stonePickaxeID + 5, stLevel, (int)((float)stDur * nMod), stSpeed, stDam, stType, nType)).setItemName("nStonePickaxe");
        iceStonePickaxe = (new InfiToolPickaxe(stonePickaxeID + 6, stLevel, (int)((float)stDur * iceMod), stSpeed, stDam, stType, iceType)).setItemName("iceStonePickaxe");
        sStonePickaxe = (new InfiToolPickaxe(stonePickaxeID + 7, stLevel, (int)((float)stDur * sMod), stSpeed, stDam, stType, sType)).setItemName("sStonePickaxe");
        cStonePickaxe = (new InfiToolPickaxe(stonePickaxeID + 8, stLevel, (int)((float)stDur * cMod), stSpeed, stDam, stType, cType)).setItemName("cStonePickaxe");
        fStonePickaxe = (new InfiToolPickaxe(stonePickaxeID + 9, stLevel, (int)((float)stDur * fMod), stSpeed, stDam, stType, fType)).setItemName("fStonePickaxe");
        brStonePickaxe = (new InfiToolPickaxe(stonePickaxeID + 10, stLevel, (int)((float)stDur * brMod), stSpeed, stDam, stType, brType)).setItemName("brStonePickaxe");
        stIronPickaxe = (new InfiToolPickaxe(ironPickaxeID + 0, iLevel, (int)((float)iDur * stMod), iSpeed, iDam, iType, stType)).setItemName("stIronPickaxe");
        iIronPickaxe = (new InfiToolPickaxe(ironPickaxeID + 1, iLevel, (int)((float)iDur * iMod), iSpeed, iDam, iType, iType)).setItemName("iIronPickaxe");
        dIronPickaxe = (new InfiToolPickaxe(ironPickaxeID + 2, iLevel, (int)((float)iDur * dMod), iSpeed, iDam, iType, dType)).setItemName("dIronPickaxe");
        gIronPickaxe = (new InfiToolPickaxe(ironPickaxeID + 3, iLevel, (int)((float)iDur * gMod), iSpeed, iDam, iType, gType)).setItemName("gIronPickaxe");
        rIronPickaxe = (new InfiToolPickaxe(ironPickaxeID + 4, iLevel, (int)((float)iDur * rMod), iSpeed, iDam, iType, rType)).setItemName("rIronPickaxe");
        oIronPickaxe = (new InfiToolPickaxe(ironPickaxeID + 5, iLevel, (int)((float)iDur * oMod), iSpeed, iDam, iType, oType)).setItemName("oIronPickaxe");
        bIronPickaxe = (new InfiToolPickaxe(ironPickaxeID + 6, iLevel, (int)((float)iDur * bMod), iSpeed, iDam, iType, bType)).setItemName("bIronPickaxe");
        nIronPickaxe = (new InfiToolPickaxe(ironPickaxeID + 7, iLevel, (int)((float)iDur * nMod), iSpeed, iDam, iType, nType)).setItemName("nIronPickaxe");
        glIronPickaxe = (new InfiToolPickaxe(ironPickaxeID + 8, iLevel, (int)((float)iDur * glMod), iSpeed, iDam, iType, glType)).setItemName("glIronPickaxe");
        iceIronPickaxe = (new InfiToolPickaxe(ironPickaxeID + 9, iLevel, (int)((float)iDur * iceMod), iSpeed, iDam, iType, iceType)).setItemName("iceIronPickaxe");
        sIronPickaxe = (new InfiToolPickaxe(ironPickaxeID + 10, iLevel, (int)((float)iDur * sMod), iSpeed, iDam, iType, sType)).setItemName("sIronPickaxe");
        blIronPickaxe = (new InfiToolPickaxe(ironPickaxeID + 11, iLevel, (int)((float)iDur * blMod), iSpeed, iDam, iType, blType)).setItemName("blIronPickaxe");
        stDiamondPickaxe = (new InfiToolPickaxe(diamondPickaxeID + 0, dLevel, (int)((float)dDur * stMod), dSpeed, dDam, dType, stType)).setItemName("stDiamondPickaxe");
        iDiamondPickaxe = (new InfiToolPickaxe(diamondPickaxeID + 1, dLevel, (int)((float)dDur * iMod), dSpeed, dDam, dType, iType)).setItemName("iDiamondPickaxe");
        dDiamondPickaxe = (new InfiToolPickaxe(diamondPickaxeID + 2, dLevel, (int)((float)dDur * dMod), dSpeed, dDam, dType, dType)).setItemName("dDiamondPickaxe");
        gDiamondPickaxe = (new InfiToolPickaxe(diamondPickaxeID + 3, dLevel, (int)((float)dDur * gMod), dSpeed, dDam, dType, gType)).setItemName("gDiamondPickaxe");
        rDiamondPickaxe = (new InfiToolPickaxe(diamondPickaxeID + 4, dLevel, (int)((float)dDur * rMod), dSpeed, dDam, dType, rType)).setItemName("rDiamondPickaxe");
        oDiamondPickaxe = (new InfiToolPickaxe(diamondPickaxeID + 5, dLevel, (int)((float)dDur * oMod), dSpeed, dDam, dType, oType)).setItemName("oDiamondPickaxe");
        bDiamondPickaxe = (new InfiToolPickaxe(diamondPickaxeID + 6, dLevel, (int)((float)dDur * bMod), dSpeed, dDam, dType, bType)).setItemName("bDiamondPickaxe");
        mDiamondPickaxe = (new InfiToolPickaxe(diamondPickaxeID + 7, dLevel, (int)((float)dDur * mMod), dSpeed, dDam, dType, mType)).setItemName("mDiamondPickaxe");
        nDiamondPickaxe = (new InfiToolPickaxe(diamondPickaxeID + 8, dLevel, (int)((float)dDur * nMod), dSpeed, dDam, dType, nType)).setItemName("nDiamondPickaxe");
        glDiamondPickaxe = (new InfiToolPickaxe(diamondPickaxeID + 9, dLevel, (int)((float)dDur * glMod), dSpeed, dDam, dType, glType)).setItemName("glDiamondPickaxe");
        blDiamondPickaxe = (new InfiToolPickaxe(diamondPickaxeID + 10, dLevel, (int)((float)dDur * blMod), dSpeed, dDam, dType, blType)).setItemName("blDiamondPickaxe");
        stGoldPickaxe = (new InfiToolPickaxe(goldPickaxeID + 0, gLevel, (int)((float)gDur * stMod), gSpeed, gDam, gType, stType)).setItemName("stGoldPickaxe");
        gGoldPickaxe = (new InfiToolPickaxe(goldPickaxeID + 1, gLevel, (int)((float)gDur * gMod), gSpeed, gDam, gType, gType)).setItemName("gGoldPickaxe");
        oGoldPickaxe = (new InfiToolPickaxe(goldPickaxeID + 2, gLevel, (int)((float)gDur * oMod), gSpeed, gDam, gType, oType)).setItemName("oGoldPickaxe");
        saGoldPickaxe = (new InfiToolPickaxe(goldPickaxeID + 3, gLevel, (int)((float)gDur * saMod), gSpeed, gDam, gType, saType)).setItemName("saGoldPickaxe");
        bGoldPickaxe = (new InfiToolPickaxe(goldPickaxeID + 4, gLevel, (int)((float)gDur * bMod), gSpeed, gDam, gType, bType)).setItemName("bGoldPickaxe");
        mGoldPickaxe = (new InfiToolPickaxe(goldPickaxeID + 5, gLevel, (int)((float)gDur * mMod), gSpeed, gDam, gType, mType)).setItemName("mGoldPickaxe");
        nGoldPickaxe = (new InfiToolPickaxe(goldPickaxeID + 6, gLevel, (int)((float)gDur * nMod), gSpeed, gDam, gType, nType)).setItemName("nGoldPickaxe");
        glGoldPickaxe = (new InfiToolPickaxe(goldPickaxeID + 7, gLevel, (int)((float)gDur * glMod), gSpeed, gDam, gType, glType)).setItemName("glGoldPickaxe");
        iceGoldPickaxe = (new InfiToolPickaxe(goldPickaxeID + 8, gLevel, (int)((float)gDur * iceMod), gSpeed, gDam, gType, iceType)).setItemName("iceGoldPickaxe");
        sGoldPickaxe = (new InfiToolPickaxe(goldPickaxeID + 9, gLevel, (int)((float)gDur * sMod), gSpeed, gDam, gType, sType)).setItemName("sGoldPickaxe");
        fGoldPickaxe = (new InfiToolPickaxe(goldPickaxeID + 10, gLevel, (int)((float)gDur * fMod), gSpeed, gDam, gType, fType)).setItemName("fGoldPickaxe");
        wRedstonePickaxe = (new InfiToolPickaxe(redstonePickaxeID + 0, rLevel, (int)((float)rDur * wMod), rSpeed, rDam, rType, wType)).setItemName("wRedstonePickaxe");
        stRedstonePickaxe = (new InfiToolPickaxe(redstonePickaxeID + 1, rLevel, (int)((float)rDur * stMod), rSpeed, rDam, rType, stType)).setItemName("stRedstonePickaxe");
        iRedstonePickaxe = (new InfiToolPickaxe(redstonePickaxeID + 2, rLevel, (int)((float)rDur * iMod), rSpeed, rDam, rType, iType)).setItemName("iRedstonePickaxe");
        dRedstonePickaxe = (new InfiToolPickaxe(redstonePickaxeID + 3, rLevel, (int)((float)rDur * dMod), rSpeed, rDam, rType, dType)).setItemName("dRedstonePickaxe");
        rRedstonePickaxe = (new InfiToolPickaxe(redstonePickaxeID + 4, rLevel, (int)((float)rDur * rMod), rSpeed, rDam, rType, rType)).setItemName("rRedstonePickaxe");
        oRedstonePickaxe = (new InfiToolPickaxe(redstonePickaxeID + 5, rLevel, (int)((float)rDur * oMod), rSpeed, rDam, rType, oType)).setItemName("oRedstonePickaxe");
        bRedstonePickaxe = (new InfiToolPickaxe(redstonePickaxeID + 6, rLevel, (int)((float)rDur * bMod), rSpeed, rDam, rType, bType)).setItemName("bRedstonePickaxe");
        mRedstonePickaxe = (new InfiToolPickaxe(redstonePickaxeID + 7, rLevel, (int)((float)rDur * mMod), rSpeed, rDam, rType, mType)).setItemName("mRedstonePickaxe");
        glRedstonePickaxe = (new InfiToolPickaxe(redstonePickaxeID + 8, rLevel, (int)((float)rDur * glMod), rSpeed, rDam, rType, glType)).setItemName("glRedstonePickaxe");
        sRedstonePickaxe = (new InfiToolPickaxe(redstonePickaxeID + 9, rLevel, (int)((float)rDur * sMod), rSpeed, rDam, rType, sType)).setItemName("sRedstonePickaxe");
        blRedstonePickaxe = (new InfiToolPickaxe(redstonePickaxeID + 10, rLevel, (int)((float)rDur * blMod), rSpeed, rDam, rType, blType)).setItemName("blRedstonePickaxe");
        wObsidianPickaxe = (new InfiToolPickaxe(obsidianPickaxeID + 0, oLevel, (int)((float)oDur * wMod), oSpeed, oDam, oType, wType)).setItemName("wObsidianPickaxe");
        stObsidianPickaxe = (new InfiToolPickaxe(obsidianPickaxeID + 1, oLevel, (int)((float)oDur * stMod), oSpeed, oDam, oType, stType)).setItemName("stObsidianPickaxe");
        iObsidianPickaxe = (new InfiToolPickaxe(obsidianPickaxeID + 2, oLevel, (int)((float)oDur * iMod), oSpeed, oDam, oType, iType)).setItemName("iObsidianPickaxe");
        dObsidianPickaxe = (new InfiToolPickaxe(obsidianPickaxeID + 3, oLevel, (int)((float)oDur * dMod), oSpeed, oDam, oType, dType)).setItemName("dObsidianPickaxe");
        gObsidianPickaxe = (new InfiToolPickaxe(obsidianPickaxeID + 4, oLevel, (int)((float)oDur * gMod), oSpeed, oDam, oType, gType)).setItemName("gObsidianPickaxe");
        rObsidianPickaxe = (new InfiToolPickaxe(obsidianPickaxeID + 5, oLevel, (int)((float)oDur * rMod), oSpeed, oDam, oType, rType)).setItemName("rObsidianPickaxe");
        oObsidianPickaxe = (new InfiToolPickaxe(obsidianPickaxeID + 6, oLevel, (int)((float)oDur * oMod), oSpeed, oDam, oType, oType)).setItemName("oObsidianPickaxe");
        bObsidianPickaxe = (new InfiToolPickaxe(obsidianPickaxeID + 7, oLevel, (int)((float)oDur * bMod), oSpeed, oDam, oType, bType)).setItemName("bObsidianPickaxe");
        nObsidianPickaxe = (new InfiToolPickaxe(obsidianPickaxeID + 8, oLevel, (int)((float)oDur * nMod), oSpeed, oDam, oType, nType)).setItemName("nObsidianPickaxe");
        glObsidianPickaxe = (new InfiToolPickaxe(obsidianPickaxeID + 9, oLevel, (int)((float)oDur * glMod), oSpeed, oDam, oType, glType)).setItemName("glObsidianPickaxe");
        sObsidianPickaxe = (new InfiToolPickaxe(obsidianPickaxeID + 10, oLevel, (int)((float)oDur * sMod), oSpeed, oDam, oType, sType)).setItemName("sObsidianPickaxe");
        fObsidianPickaxe = (new InfiToolPickaxe(obsidianPickaxeID + 11, oLevel, (int)((float)oDur * fMod), oSpeed, oDam, oType, fType)).setItemName("fObsidianPickaxe");
        blObsidianPickaxe = (new InfiToolPickaxe(obsidianPickaxeID + 12, oLevel, (int)((float)oDur * blMod), oSpeed, oDam, oType, blType)).setItemName("blObsidianPickaxe");
        wSandstonePickaxe = (new InfiToolPickaxe(sandstonePickaxeID + 0, saLevel, (int)((float)saDur * wMod), saSpeed, saDam, saType, wType)).setItemName("wSandstonePickaxe");
        stSandstonePickaxe = (new InfiToolPickaxe(sandstonePickaxeID + 1, saLevel, (int)((float)saDur * stMod), saSpeed, saDam, saType, stType)).setItemName("stSandstonePickaxe");
        saSandstonePickaxe = (new InfiToolPickaxe(sandstonePickaxeID + 2, saLevel, (int)((float)saDur * saMod), saSpeed, saDam, saType, saType)).setItemName("saSandstonePickaxe");
        bSandstonePickaxe = (new InfiToolPickaxe(sandstonePickaxeID + 3, saLevel, (int)((float)saDur * bMod), saSpeed, saDam, saType, bType)).setItemName("bSandstonePickaxe");
        pSandstonePickaxe = (new InfiToolPickaxe(sandstonePickaxeID + 4, saLevel, (int)((float)saDur * pMod), saSpeed, saDam, saType, pType)).setItemName("pSandstonePickaxe");
        nSandstonePickaxe = (new InfiToolPickaxe(sandstonePickaxeID + 5, saLevel, (int)((float)saDur * nMod), saSpeed, saDam, saType, nType)).setItemName("nSandstonePickaxe");
        iceSandstonePickaxe = (new InfiToolPickaxe(sandstonePickaxeID + 6, saLevel, (int)((float)saDur * iceMod), saSpeed, saDam, saType, iceType)).setItemName("iceSandstonePickaxe");
        sSandstonePickaxe = (new InfiToolPickaxe(sandstonePickaxeID + 7, saLevel, (int)((float)saDur * sMod), saSpeed, saDam, saType, sType)).setItemName("sSandstonePickaxe");
        cSandstonePickaxe = (new InfiToolPickaxe(sandstonePickaxeID + 8, saLevel, (int)((float)saDur * cMod), saSpeed, saDam, saType, cType)).setItemName("cSandstonePickaxe");
        fSandstonePickaxe = (new InfiToolPickaxe(sandstonePickaxeID + 9, saLevel, (int)((float)saDur * fMod), saSpeed, saDam, saType, fType)).setItemName("fSandstonePickaxe");
        brSandstonePickaxe = (new InfiToolPickaxe(sandstonePickaxeID + 10, saLevel, (int)((float)saDur * brMod), saSpeed, saDam, saType, brType)).setItemName("brSandstonePickaxe");
        wBonePickaxe = (new InfiToolPickaxe(bonePickaxeID + 0, bLevel, (int)((float)bDur * wMod), bSpeed, bDam, bType, wType)).setItemName("wBonePickaxe");
        stBonePickaxe = (new InfiToolPickaxe(bonePickaxeID + 1, bLevel, (int)((float)bDur * stMod), bSpeed, bDam, bType, stType)).setItemName("stBonePickaxe");
        iBonePickaxe = (new InfiToolPickaxe(bonePickaxeID + 2, bLevel, (int)((float)bDur * iMod), bSpeed, bDam, bType, iType)).setItemName("iBonePickaxe");
        dBonePickaxe = (new InfiToolPickaxe(bonePickaxeID + 3, bLevel, (int)((float)bDur * iMod), bSpeed, bDam, bType, dType)).setItemName("dBonePickaxe");
        rBonePickaxe = (new InfiToolPickaxe(bonePickaxeID + 4, bLevel, (int)((float)bDur * rMod), bSpeed, bDam, bType, rType)).setItemName("rBonePickaxe");
        oBonePickaxe = (new InfiToolPickaxe(bonePickaxeID + 5, bLevel, (int)((float)bDur * oMod), bSpeed, bDam, bType, oType)).setItemName("oBonePickaxe");
        bBonePickaxe = (new InfiToolPickaxe(bonePickaxeID + 6, bLevel, (int)((float)bDur * bMod), bSpeed, bDam, bType, bType)).setItemName("bBonePickaxe");
        mBonePickaxe = (new InfiToolPickaxe(bonePickaxeID + 7, bLevel, (int)((float)bDur * mMod), bSpeed, bDam, bType, mType)).setItemName("mBonePickaxe");
        nBonePickaxe = (new InfiToolPickaxe(bonePickaxeID + 8, bLevel, (int)((float)bDur * nMod), bSpeed, bDam, bType, nType)).setItemName("nBonePickaxe");
        glBonePickaxe = (new InfiToolPickaxe(bonePickaxeID + 9, bLevel, (int)((float)bDur * gMod), bSpeed, bDam, bType, glType)).setItemName("glBonePickaxe");
        sBonePickaxe = (new InfiToolPickaxe(bonePickaxeID + 10, bLevel, (int)((float)bDur * sMod), bSpeed, bDam, bType, sType)).setItemName("sBonePickaxe");
        cBonePickaxe = (new InfiToolPickaxe(bonePickaxeID + 11, bLevel, (int)((float)bDur * cMod), bSpeed, bDam, bType, cType)).setItemName("cBonePickaxe");
        fBonePickaxe = (new InfiToolPickaxe(bonePickaxeID + 12, bLevel, (int)((float)bDur * fMod), bSpeed, bDam, bType, fType)).setItemName("fBonePickaxe");
        brBonePickaxe = (new InfiToolPickaxe(bonePickaxeID + 13, bLevel, (int)((float)bDur * brMod), bSpeed, bDam, bType, brType)).setItemName("brBonePickaxe");
        blBonePickaxe = (new InfiToolPickaxe(bonePickaxeID + 14, bLevel, (int)((float)bDur * blMod), bSpeed, bDam, bType, blType)).setItemName("blBonePickaxe");
        wPaperPickaxe = (new InfiToolPickaxe(paperPickaxeID + 0, pLevel, (int)((float)pDur * wMod), pSpeed, pDam, pType, wType)).setItemName("wPaperPickaxe");
        saPaperPickaxe = (new InfiToolPickaxe(paperPickaxeID + 1, pLevel, (int)((float)pDur * saMod), pSpeed, pDam, pType, saType)).setItemName("saPaperPickaxe");
        bPaperPickaxe = (new InfiToolPickaxe(paperPickaxeID + 2, pLevel, (int)((float)pDur * bMod), pSpeed, pDam, pType, bType)).setItemName("bPaperPickaxe");
        pPaperPickaxe = (new InfiToolPickaxe(paperPickaxeID + 3, pLevel, (int)((float)pDur * pMod), pSpeed, pDam, pType, pType)).setItemName("pPaperPickaxe");
        sPaperPickaxe = (new InfiToolPickaxe(paperPickaxeID + 4, pLevel, (int)((float)pDur * sMod), pSpeed, pDam, pType, sType)).setItemName("sPaperPickaxe");
        cPaperPickaxe = (new InfiToolPickaxe(paperPickaxeID + 5, pLevel, (int)((float)pDur * cMod), pSpeed, pDam, pType, cType)).setItemName("cPaperPickaxe");
        brPaperPickaxe = (new InfiToolPickaxe(paperPickaxeID + 6, pLevel, (int)((float)pDur * brMod), pSpeed, pDam, pType, brType)).setItemName("brPaperPickaxe");
        stMossyPickaxe = (new InfiToolPickaxe(mossyPickaxeID + 0, mLevel, (int)((float)mDur * stMod), mSpeed, mDam, mType, stType)).setItemName("stMossyPickaxe");
        dMossyPickaxe = (new InfiToolPickaxe(mossyPickaxeID + 1, mLevel, (int)((float)mDur * iMod), mSpeed, mDam, mType, dType)).setItemName("dMossyPickaxe");
        rMossyPickaxe = (new InfiToolPickaxe(mossyPickaxeID + 2, mLevel, (int)((float)mDur * rMod), mSpeed, mDam, mType, rType)).setItemName("rMossyPickaxe");
        bMossyPickaxe = (new InfiToolPickaxe(mossyPickaxeID + 3, mLevel, (int)((float)mDur * bMod), mSpeed, mDam, mType, bType)).setItemName("bMossyPickaxe");
        mMossyPickaxe = (new InfiToolPickaxe(mossyPickaxeID + 4, mLevel, (int)((float)mDur * mMod), mSpeed, mDam, mType, mType)).setItemName("mMossyPickaxe");
        glMossyPickaxe = (new InfiToolPickaxe(mossyPickaxeID + 5, mLevel, (int)((float)mDur * gMod), mSpeed, mDam, mType, glType)).setItemName("glMossyPickaxe");
        wNetherrackPickaxe = (new InfiToolPickaxe(netherrackPickaxeID + 0, nLevel, (int)((float)nDur * wMod), nSpeed, nDam, nType, wType)).setItemName("wNetherrackPickaxe");
        stNetherrackPickaxe = (new InfiToolPickaxe(netherrackPickaxeID + 1, nLevel, (int)((float)nDur * stMod), nSpeed, nDam, nType, stType)).setItemName("stNetherrackPickaxe");
        iNetherrackPickaxe = (new InfiToolPickaxe(netherrackPickaxeID + 2, nLevel, (int)((float)nDur * iMod), nSpeed, nDam, nType, iType)).setItemName("iNetherrackPickaxe");
        rNetherrackPickaxe = (new InfiToolPickaxe(netherrackPickaxeID + 3, nLevel, (int)((float)nDur * rMod), nSpeed, nDam, nType, rType)).setItemName("rNetherrackPickaxe");
        oNetherrackPickaxe = (new InfiToolPickaxe(netherrackPickaxeID + 4, nLevel, (int)((float)nDur * oMod), nSpeed, nDam, nType, oType)).setItemName("oNetherrackPickaxe");
        saNetherrackPickaxe = (new InfiToolPickaxe(netherrackPickaxeID + 5, nLevel, (int)((float)nDur * saMod), nSpeed, nDam, nType, saType)).setItemName("saNetherrackPickaxe");
        bNetherrackPickaxe = (new InfiToolPickaxe(netherrackPickaxeID + 6, nLevel, (int)((float)nDur * bMod), nSpeed, nDam, nType, bType)).setItemName("bNetherrackPickaxe");
        mNetherrackPickaxe = (new InfiToolPickaxe(netherrackPickaxeID + 7, nLevel, (int)((float)nDur * mMod), nSpeed, nDam, nType, mType)).setItemName("mNetherrackPickaxe");
        nNetherrackPickaxe = (new InfiToolPickaxe(netherrackPickaxeID + 8, nLevel, (int)((float)nDur * nMod), nSpeed, nDam, nType, nType)).setItemName("nNetherrackPickaxe");
        glNetherrackPickaxe = (new InfiToolPickaxe(netherrackPickaxeID + 9, nLevel, (int)((float)nDur * glMod), nSpeed, nDam, nType, glType)).setItemName("glNetherrackPickaxe");
        iceNetherrackPickaxe = (new InfiToolPickaxe(netherrackPickaxeID + 10, nLevel, (int)((float)nDur * iceMod), nSpeed, nDam, nType, iceType)).setItemName("iceNetherrackPickaxe");
        sNetherrackPickaxe = (new InfiToolPickaxe(netherrackPickaxeID + 11, nLevel, (int)((float)nDur * sMod), nSpeed, nDam, nType, sType)).setItemName("sNetherrackPickaxe");
        cNetherrackPickaxe = (new InfiToolPickaxe(netherrackPickaxeID + 12, nLevel, (int)((float)nDur * cMod), nSpeed, nDam, nType, cType)).setItemName("cNetherrackPickaxe");
        fNetherrackPickaxe = (new InfiToolPickaxe(netherrackPickaxeID + 13, nLevel, (int)((float)nDur * fMod), nSpeed, nDam, nType, fType)).setItemName("fNetherrackPickaxe");
        brNetherrackPickaxe = (new InfiToolPickaxe(netherrackPickaxeID + 14, nLevel, (int)((float)nDur * brMod), nSpeed, nDam, nType, brType)).setItemName("brNetherrackPickaxe");
        blNetherrackPickaxe = (new InfiToolPickaxe(netherrackPickaxeID + 15, nLevel, (int)((float)nDur * blMod), nSpeed, nDam, nType, blType)).setItemName("blNetherrackPickaxe");
        wGlowstonePickaxe = (new InfiToolPickaxe(glowstonePickaxeID + 0, glLevel, (int)((float)glDur * wMod), glSpeed, glDam, glType, wType)).setItemName("wGlowstonePickaxe");
        stGlowstonePickaxe = (new InfiToolPickaxe(glowstonePickaxeID + 1, glLevel, (int)((float)glDur * stMod), glSpeed, glDam, glType, stType)).setItemName("stGlowstonePickaxe");
        iGlowstonePickaxe = (new InfiToolPickaxe(glowstonePickaxeID + 2, glLevel, (int)((float)glDur * iMod), glSpeed, glDam, glType, iType)).setItemName("iGlowstonePickaxe");
        dGlowstonePickaxe = (new InfiToolPickaxe(glowstonePickaxeID + 3, glLevel, (int)((float)glDur * iMod), glSpeed, glDam, glType, dType)).setItemName("dGlowstonePickaxe");
        rGlowstonePickaxe = (new InfiToolPickaxe(glowstonePickaxeID + 4, glLevel, (int)((float)glDur * rMod), glSpeed, glDam, glType, rType)).setItemName("rGlowstonePickaxe");
        oGlowstonePickaxe = (new InfiToolPickaxe(glowstonePickaxeID + 5, glLevel, (int)((float)glDur * oMod), glSpeed, glDam, glType, oType)).setItemName("oGlowstonePickaxe");
        bGlowstonePickaxe = (new InfiToolPickaxe(glowstonePickaxeID + 6, glLevel, (int)((float)glDur * bMod), glSpeed, glDam, glType, bType)).setItemName("bGlowstonePickaxe");
        mGlowstonePickaxe = (new InfiToolPickaxe(glowstonePickaxeID + 7, glLevel, (int)((float)glDur * mMod), glSpeed, glDam, glType, mType)).setItemName("mGlowstonePickaxe");
        nGlowstonePickaxe = (new InfiToolPickaxe(glowstonePickaxeID + 8, glLevel, (int)((float)glDur * nMod), glSpeed, glDam, glType, nType)).setItemName("nGlowstonePickaxe");
        glGlowstonePickaxe = (new InfiToolPickaxe(glowstonePickaxeID + 9, glLevel, (int)((float)glDur * gMod), glSpeed, glDam, glType, glType)).setItemName("glGlowstonePickaxe");
        iceGlowstonePickaxe = (new InfiToolPickaxe(glowstonePickaxeID + 10, glLevel, (int)((float)glDur * iceMod), glSpeed, glDam, glType, iceType)).setItemName("iceGlowstonePickaxe");
        lGlowstonePickaxe = (new InfiToolPickaxe(glowstonePickaxeID + 11, glLevel, (int)((float)glDur * gMod), glSpeed, glDam, glType, lType)).setItemName("lGlowstonePickaxe");
        sGlowstonePickaxe = (new InfiToolPickaxe(glowstonePickaxeID + 12, glLevel, (int)((float)glDur * sMod), glSpeed, glDam, glType, sType)).setItemName("sGlowstonePickaxe");
        blGlowstonePickaxe = (new InfiToolPickaxe(glowstonePickaxeID + 13, glLevel, (int)((float)glDur * blMod), glSpeed, glDam, glType, blType)).setItemName("blGlowstonePickaxe");
        wIcePickaxe = (new InfiToolPickaxe(icePickaxeID + 0, iceLevel, (int)((float)iceDur * wMod), iceSpeed, iceDam, iceType, wType)).setItemName("wIcePickaxe");
        stIcePickaxe = (new InfiToolPickaxe(icePickaxeID + 1, iceLevel, (int)((float)iceDur * stMod), iceSpeed, iceDam, iceType, stType)).setItemName("stIcePickaxe");
        iIcePickaxe = (new InfiToolPickaxe(icePickaxeID + 2, iceLevel, (int)((float)iceDur * iMod), iceSpeed, iceDam, iceType, iType)).setItemName("iIcePickaxe");
        dIcePickaxe = (new InfiToolPickaxe(icePickaxeID + 3, iceLevel, (int)((float)iceDur * dMod), iceSpeed, iceDam, iceType, dType)).setItemName("dIcePickaxe");
        gIcePickaxe = (new InfiToolPickaxe(icePickaxeID + 4, iceLevel, (int)((float)iceDur * gMod), iceSpeed, iceDam, iceType, gType)).setItemName("gIcePickaxe");
        rIcePickaxe = (new InfiToolPickaxe(icePickaxeID + 5, iceLevel, (int)((float)iceDur * rMod), iceSpeed, iceDam, iceType, rType)).setItemName("rIcePickaxe");
        oIcePickaxe = (new InfiToolPickaxe(icePickaxeID + 6, iceLevel, (int)((float)iceDur * oMod), iceSpeed, iceDam, iceType, oType)).setItemName("oIcePickaxe");
        saIcePickaxe = (new InfiToolPickaxe(icePickaxeID + 7, iceLevel, (int)((float)iceDur * saMod), iceSpeed, iceDam, iceType, saType)).setItemName("saIcePickaxe");
        bIcePickaxe = (new InfiToolPickaxe(icePickaxeID + 8, iceLevel, (int)((float)iceDur * bMod), iceSpeed, iceDam, iceType, bType)).setItemName("bIcePickaxe");
        glIcePickaxe = (new InfiToolPickaxe(icePickaxeID + 9, iceLevel, (int)((float)iceDur * gMod), iceSpeed, iceDam, iceType, glType)).setItemName("glIcePickaxe");
        iceIcePickaxe = (new InfiToolPickaxe(icePickaxeID + 10, iceLevel, (int)((float)iceDur * iMod), iceSpeed, iceDam, iceType, iceType)).setItemName("iceIcePickaxe");
        sIcePickaxe = (new InfiToolPickaxe(icePickaxeID + 11, iceLevel, (int)((float)iceDur * sMod), iceSpeed, iceDam, iceType, sType)).setItemName("sIcePickaxe");
        cIcePickaxe = (new InfiToolPickaxe(icePickaxeID + 12, iceLevel, (int)((float)iceDur * cMod), iceSpeed, iceDam, iceType, cType)).setItemName("cIcePickaxe");
        fIcePickaxe = (new InfiToolPickaxe(icePickaxeID + 13, iceLevel, (int)((float)iceDur * fMod), iceSpeed, iceDam, iceType, fType)).setItemName("fIcePickaxe");
        brIcePickaxe = (new InfiToolPickaxe(icePickaxeID + 14, iceLevel, (int)((float)iceDur * brMod), iceSpeed, iceDam, iceType, brType)).setItemName("brIcePickaxe");
        dLavaPickaxe = (new InfiToolPickaxe(lavaPickaxeID + 0, lLevel, (int)((float)lDur * iMod), lSpeed, lDam, lType, dType)).setItemName("dLavaPickaxe");
        rLavaPickaxe = (new InfiToolPickaxe(lavaPickaxeID + 1, lLevel, (int)((float)lDur * rMod), lSpeed, lDam, lType, rType)).setItemName("rLavaPickaxe");
        bLavaPickaxe = (new InfiToolPickaxe(lavaPickaxeID + 2, lLevel, (int)((float)lDur * bMod), lSpeed, lDam, lType, bType)).setItemName("bLavaPickaxe");
        nLavaPickaxe = (new InfiToolPickaxe(lavaPickaxeID + 3, lLevel, (int)((float)lDur * nMod), lSpeed, lDam, lType, nType)).setItemName("nLavaPickaxe");
        glLavaPickaxe = (new InfiToolPickaxe(lavaPickaxeID + 4, lLevel, (int)((float)lDur * gMod), lSpeed, lDam, lType, glType)).setItemName("glLavaPickaxe");
        lLavaPickaxe = (new InfiToolPickaxe(lavaPickaxeID + 5, lLevel, (int)((float)lDur * lMod), lSpeed, lDam, lType, lType)).setItemName("lLavaPickaxe");
        blLavaPickaxe = (new InfiToolPickaxe(lavaPickaxeID + 6, lLevel, (int)((float)lDur * blMod), lSpeed, lDam, lType, blType)).setItemName("blLavaPickaxe");
        wSlimePickaxe = (new InfiToolPickaxe(slimePickaxeID + 0, sLevel, (int)((float)sDur * wMod), sSpeed, sDam, sType, wType)).setItemName("wSlimePickaxe");
        stSlimePickaxe = (new InfiToolPickaxe(slimePickaxeID + 1, sLevel, (int)((float)sDur * stMod), sSpeed, sDam, sType, stType)).setItemName("stSlimePickaxe");
        iSlimePickaxe = (new InfiToolPickaxe(slimePickaxeID + 2, sLevel, (int)((float)sDur * iMod), sSpeed, sDam, sType, iType)).setItemName("iSlimePickaxe");
        dSlimePickaxe = (new InfiToolPickaxe(slimePickaxeID + 3, sLevel, (int)((float)sDur * iMod), sSpeed, sDam, sType, dType)).setItemName("dSlimePickaxe");
        gSlimePickaxe = (new InfiToolPickaxe(slimePickaxeID + 4, sLevel, (int)((float)sDur * gMod), sSpeed, sDam, sType, gType)).setItemName("gSlimePickaxe");
        rSlimePickaxe = (new InfiToolPickaxe(slimePickaxeID + 5, sLevel, (int)((float)sDur * rMod), sSpeed, sDam, sType, rType)).setItemName("rSlimePickaxe");
        oSlimePickaxe = (new InfiToolPickaxe(slimePickaxeID + 6, sLevel, (int)((float)sDur * oMod), sSpeed, sDam, sType, oType)).setItemName("oSlimePickaxe");
        saSlimePickaxe = (new InfiToolPickaxe(slimePickaxeID + 7, sLevel, (int)((float)sDur * saMod), sSpeed, sDam, sType, saType)).setItemName("saSlimePickaxe");
        bSlimePickaxe = (new InfiToolPickaxe(slimePickaxeID + 8, sLevel, (int)((float)sDur * bMod), sSpeed, sDam, sType, bType)).setItemName("bSlimePickaxe");
        pSlimePickaxe = (new InfiToolPickaxe(slimePickaxeID + 9, sLevel, (int)((float)sDur * pMod), sSpeed, sDam, sType, pType)).setItemName("pSlimePickaxe");
        mSlimePickaxe = (new InfiToolPickaxe(slimePickaxeID + 10, sLevel, (int)((float)sDur * mMod), sSpeed, sDam, sType, mType)).setItemName("mSlimePickaxe");
        nSlimePickaxe = (new InfiToolPickaxe(slimePickaxeID + 11, sLevel, (int)((float)sDur * nMod), sSpeed, sDam, sType, nType)).setItemName("nSlimePickaxe");
        glSlimePickaxe = (new InfiToolPickaxe(slimePickaxeID + 12, sLevel, (int)((float)sDur * gMod), sSpeed, sDam, sType, glType)).setItemName("glSlimePickaxe");
        iceSlimePickaxe = (new InfiToolPickaxe(slimePickaxeID + 13, sLevel, (int)((float)sDur * iMod), sSpeed, sDam, sType, iceType)).setItemName("iceSlimePickaxe");
        lSlimePickaxe = (new InfiToolPickaxe(slimePickaxeID + 14, sLevel, (int)((float)sDur * lMod), sSpeed, sDam, sType, lType)).setItemName("lSlimePickaxe");
        sSlimePickaxe = (new InfiToolPickaxe(slimePickaxeID + 15, sLevel, (int)((float)sDur * sMod), sSpeed, sDam, sType, sType)).setItemName("sSlimePickaxe");
        cSlimePickaxe = (new InfiToolPickaxe(slimePickaxeID + 16, sLevel, (int)((float)sDur * cMod), sSpeed, sDam, sType, cType)).setItemName("cSlimePickaxe");
        fSlimePickaxe = (new InfiToolPickaxe(slimePickaxeID + 17, sLevel, (int)((float)sDur * fMod), sSpeed, sDam, sType, fType)).setItemName("fSlimePickaxe");
        brSlimePickaxe = (new InfiToolPickaxe(slimePickaxeID + 18, sLevel, (int)((float)sDur * brMod), sSpeed, sDam, sType, brType)).setItemName("brSlimePickaxe");
        blSlimePickaxe = (new InfiToolPickaxe(slimePickaxeID + 19, sLevel, (int)((float)sDur * blMod), sSpeed, sDam, sType, blType)).setItemName("blSlimePickaxe");
        wCactusPickaxe = (new InfiToolPickaxe(cactusPickaxeID + 0, cLevel, (int)((float)cDur * wMod), cSpeed, cDam, cType, wType)).setItemName("wCactusPickaxe");
        stCactusPickaxe = (new InfiToolPickaxe(cactusPickaxeID + 1, cLevel, (int)((float)cDur * stMod), cSpeed, cDam, cType, stType)).setItemName("stCactusPickaxe");
        saCactusPickaxe = (new InfiToolPickaxe(cactusPickaxeID + 2, cLevel, (int)((float)cDur * saMod), cSpeed, cDam, cType, saType)).setItemName("saCactusPickaxe");
        bCactusPickaxe = (new InfiToolPickaxe(cactusPickaxeID + 3, cLevel, (int)((float)cDur * bMod), cSpeed, cDam, cType, bType)).setItemName("bCactusPickaxe");
        pCactusPickaxe = (new InfiToolPickaxe(cactusPickaxeID + 4, cLevel, (int)((float)cDur * pMod), cSpeed, cDam, cType, pType)).setItemName("pCactusPickaxe");
        nCactusPickaxe = (new InfiToolPickaxe(cactusPickaxeID + 5, cLevel, (int)((float)cDur * nMod), cSpeed, cDam, cType, nType)).setItemName("nCactusPickaxe");
        sCactusPickaxe = (new InfiToolPickaxe(cactusPickaxeID + 6, cLevel, (int)((float)cDur * sMod), cSpeed, cDam, cType, sType)).setItemName("sCactusPickaxe");
        cCactusPickaxe = (new InfiToolPickaxe(cactusPickaxeID + 7, cLevel, (int)((float)cDur * cMod), cSpeed, cDam, cType, cType)).setItemName("cCactusPickaxe");
        fCactusPickaxe = (new InfiToolPickaxe(cactusPickaxeID + 8, cLevel, (int)((float)cDur * fMod), cSpeed, cDam, cType, fType)).setItemName("fCactusPickaxe");
        brCactusPickaxe = (new InfiToolPickaxe(cactusPickaxeID + 9, cLevel, (int)((float)cDur * brMod), cSpeed, cDam, cType, brType)).setItemName("brCactusPickaxe");
        wFlintPickaxe = (new InfiToolPickaxe(flintPickaxeID + 0, fLevel, (int)((float)fDur * wMod), fSpeed, fDam, fType, wType)).setItemName("wFlintPickaxe");
        stFlintPickaxe = (new InfiToolPickaxe(flintPickaxeID + 1, fLevel, (int)((float)fDur * stMod), fSpeed, fDam, fType, stType)).setItemName("stFlintPickaxe");
        iFlintPickaxe = (new InfiToolPickaxe(flintPickaxeID + 2, fLevel, (int)((float)fDur * iMod), fSpeed, fDam, fType, iType)).setItemName("iFlintPickaxe");
        gFlintPickaxe = (new InfiToolPickaxe(flintPickaxeID + 3, fLevel, (int)((float)fDur * gMod), fSpeed, fDam, fType, gType)).setItemName("gFlintPickaxe");
        oFlintPickaxe = (new InfiToolPickaxe(flintPickaxeID + 4, fLevel, (int)((float)fDur * oMod), fSpeed, fDam, fType, oType)).setItemName("oFlintPickaxe");
        saFlintPickaxe = (new InfiToolPickaxe(flintPickaxeID + 5, fLevel, (int)((float)fDur * saMod), fSpeed, fDam, fType, saType)).setItemName("saFlintPickaxe");
        bFlintPickaxe = (new InfiToolPickaxe(flintPickaxeID + 6, fLevel, (int)((float)fDur * bMod), fSpeed, fDam, fType, bType)).setItemName("bFlintPickaxe");
        nFlintPickaxe = (new InfiToolPickaxe(flintPickaxeID + 7, fLevel, (int)((float)fDur * nMod), fSpeed, fDam, fType, nType)).setItemName("nFlintPickaxe");
        iceFlintPickaxe = (new InfiToolPickaxe(flintPickaxeID + 8, fLevel, (int)((float)fDur * wMod), fSpeed, fDam, fType, iceType)).setItemName("iceFlintPickaxe");
        sFlintPickaxe = (new InfiToolPickaxe(flintPickaxeID + 9, fLevel, (int)((float)fDur * sMod), fSpeed, fDam, fType, sType)).setItemName("sFlintPickaxe");
        cFlintPickaxe = (new InfiToolPickaxe(flintPickaxeID + 10, fLevel, (int)((float)fDur * cMod), fSpeed, fDam, fType, cType)).setItemName("cFlintPickaxe");
        fFlintPickaxe = (new InfiToolPickaxe(flintPickaxeID + 11, fLevel, (int)((float)fDur * fMod), fSpeed, fDam, fType, fType)).setItemName("fFlintPickaxe");
        brFlintPickaxe = (new InfiToolPickaxe(flintPickaxeID + 12, fLevel, (int)((float)fDur * brMod), fSpeed, fDam, fType, brType)).setItemName("brFlintPickaxe");
        blFlintPickaxe = (new InfiToolPickaxe(flintPickaxeID + 13, fLevel, (int)((float)fDur * blMod), fSpeed, fDam, fType, blType)).setItemName("blFlintPickaxe");
        wBrickPickaxe = (new InfiToolPickaxe(brickPickaxeID + 0, brLevel, (int)((float)brDur * wMod), brSpeed, brDam, brType, wType)).setItemName("wBrickPickaxe");
        stBrickPickaxe = (new InfiToolPickaxe(brickPickaxeID + 1, brLevel, (int)((float)brDur * stMod), brSpeed, brDam, brType, stType)).setItemName("stBrickPickaxe");
        saBrickPickaxe = (new InfiToolPickaxe(brickPickaxeID + 2, brLevel, (int)((float)brDur * saMod), brSpeed, brDam, brType, saType)).setItemName("saBrickPickaxe");
        bBrickPickaxe = (new InfiToolPickaxe(brickPickaxeID + 3, brLevel, (int)((float)brDur * bMod), brSpeed, brDam, brType, bType)).setItemName("bBrickPickaxe");
        pBrickPickaxe = (new InfiToolPickaxe(brickPickaxeID + 4, brLevel, (int)((float)brDur * pMod), brSpeed, brDam, brType, pType)).setItemName("pBrickPickaxe");
        nBrickPickaxe = (new InfiToolPickaxe(brickPickaxeID + 5, brLevel, (int)((float)brDur * nMod), brSpeed, brDam, brType, nType)).setItemName("nBrickPickaxe");
        iceBrickPickaxe = (new InfiToolPickaxe(brickPickaxeID + 6, brLevel, (int)((float)brDur * iceMod), brSpeed, brDam, brType, iceType)).setItemName("iceBrickPickaxe");
        sBrickPickaxe = (new InfiToolPickaxe(brickPickaxeID + 7, brLevel, (int)((float)brDur * sMod), brSpeed, brDam, brType, sType)).setItemName("sBrickPickaxe");
        cBrickPickaxe = (new InfiToolPickaxe(brickPickaxeID + 8, brLevel, (int)((float)brDur * cMod), brSpeed, brDam, brType, cType)).setItemName("cBrickPickaxe");
        fBrickPickaxe = (new InfiToolPickaxe(brickPickaxeID + 9, brLevel, (int)((float)brDur * fMod), brSpeed, brDam, brType, fType)).setItemName("fBrickPickaxe");
        brBrickPickaxe = (new InfiToolPickaxe(brickPickaxeID + 10, brLevel, (int)((float)brDur * brMod), brSpeed, brDam, brType, brType)).setItemName("brBrickPickaxe");
        dBlazePickaxe = (new InfiToolPickaxe(blazePickaxeID + 0, blLevel, (int)((float)blDur * dMod), blSpeed, blDam, blType, dType)).setItemName("wBlazePickaxe");
        rBlazePickaxe = (new InfiToolPickaxe(blazePickaxeID + 1, blLevel, (int)((float)blDur * rMod), blSpeed, blDam, blType, rType)).setItemName("rBlazePickaxe");
        bBlazePickaxe = (new InfiToolPickaxe(blazePickaxeID + 2, blLevel, (int)((float)blDur * bMod), blSpeed, blDam, blType, bType)).setItemName("bBlazePickaxe");
        nBlazePickaxe = (new InfiToolPickaxe(blazePickaxeID + 3, blLevel, (int)((float)blDur * nMod), blSpeed, blDam, blType, nType)).setItemName("nBlazePickaxe");
        glBlazePickaxe = (new InfiToolPickaxe(blazePickaxeID + 4, blLevel, (int)((float)blDur * glMod), blSpeed, blDam, blType, glType)).setItemName("glBlazePickaxe");
        lBlazePickaxe = (new InfiToolPickaxe(blazePickaxeID + 5, blLevel, (int)((float)blDur * lMod), blSpeed, blDam, blType, lType)).setItemName("lBlazePickaxe");
        fBlazePickaxe = (new InfiToolPickaxe(blazePickaxeID + 6, blLevel, (int)((float)blDur * fMod), blSpeed, blDam, blType, fType)).setItemName("fBlazePickaxe");
        blBlazePickaxe = (new InfiToolPickaxe(blazePickaxeID + 7, blLevel, (int)((float)blDur * bMod), blSpeed, blDam, blType, bType)).setItemName("blBlazePickaxe");
        stWoodShovel = (new InfiToolShovel(woodShovelID + 0, wLevel, (int)((float)wDur * stMod), wSpeed, wDam, wType, stType)).setItemName("stWoodShovel");
        saWoodShovel = (new InfiToolShovel(woodShovelID + 1, wLevel, (int)((float)wDur * saMod), wSpeed, wDam, wType, saType)).setItemName("saWoodShovel");
        bWoodShovel = (new InfiToolShovel(woodShovelID + 2, wLevel, (int)((float)wDur * bMod), wSpeed, wDam, wType, bType)).setItemName("bWoodShovel");
        pWoodShovel = (new InfiToolShovel(woodShovelID + 3, wLevel, (int)((float)wDur * pMod), wSpeed, wDam, wType, pType)).setItemName("pWoodShovel");
        nWoodShovel = (new InfiToolShovel(woodShovelID + 4, wLevel, (int)((float)wDur * nMod), wSpeed, wDam, wType, nType)).setItemName("nWoodShovel");
        sWoodShovel = (new InfiToolShovel(woodShovelID + 5, wLevel, (int)((float)wDur * sMod), wSpeed, wDam, wType, sType)).setItemName("sWoodShovel");
        cWoodShovel = (new InfiToolShovel(woodShovelID + 6, wLevel, (int)((float)wDur * cMod), wSpeed, wDam, wType, cType)).setItemName("cWoodShovel");
        fWoodShovel = (new InfiToolShovel(woodShovelID + 7, wLevel, (int)((float)wDur * fMod), wSpeed, wDam, wType, fType)).setItemName("fWoodShovel");
        brWoodShovel = (new InfiToolShovel(woodShovelID + 8, wLevel, (int)((float)wDur * brMod), wSpeed, wDam, wType, brType)).setItemName("brWoodShovel");
        stStoneShovel = (new InfiToolShovel(stoneShovelID + 0, stLevel, (int)((float)stDur * stMod), stSpeed, stDam, stType, stType)).setItemName("stStoneShovel");
        saStoneShovel = (new InfiToolShovel(stoneShovelID + 1, stLevel, (int)((float)stDur * saMod), stSpeed, stDam, stType, saType)).setItemName("saStoneShovel");
        bStoneShovel = (new InfiToolShovel(stoneShovelID + 2, stLevel, (int)((float)stDur * bMod), stSpeed, stDam, stType, bType)).setItemName("bStoneShovel");
        pStoneShovel = (new InfiToolShovel(stoneShovelID + 3, stLevel, (int)((float)stDur * pMod), stSpeed, stDam, stType, pType)).setItemName("pStoneShovel");
        mStoneShovel = (new InfiToolShovel(stoneShovelID + 4, stLevel, (int)((float)stDur * mMod), stSpeed, stDam, stType, mType)).setItemName("mStoneShovel");
        nStoneShovel = (new InfiToolShovel(stoneShovelID + 5, stLevel, (int)((float)stDur * nMod), stSpeed, stDam, stType, nType)).setItemName("nStoneShovel");
        iceStoneShovel = (new InfiToolShovel(stoneShovelID + 6, stLevel, (int)((float)stDur * iceMod), stSpeed, stDam, stType, iceType)).setItemName("iceStoneShovel");
        sStoneShovel = (new InfiToolShovel(stoneShovelID + 7, stLevel, (int)((float)stDur * sMod), stSpeed, stDam, stType, sType)).setItemName("sStoneShovel");
        cStoneShovel = (new InfiToolShovel(stoneShovelID + 8, stLevel, (int)((float)stDur * cMod), stSpeed, stDam, stType, cType)).setItemName("cStoneShovel");
        fStoneShovel = (new InfiToolShovel(stoneShovelID + 9, stLevel, (int)((float)stDur * fMod), stSpeed, stDam, stType, fType)).setItemName("fStoneShovel");
        brStoneShovel = (new InfiToolShovel(stoneShovelID + 10, stLevel, (int)((float)stDur * brMod), stSpeed, stDam, stType, brType)).setItemName("brStoneShovel");
        stIronShovel = (new InfiToolShovel(ironShovelID + 0, iLevel, (int)((float)iDur * stMod), iSpeed, iDam, iType, stType)).setItemName("stIronShovel");
        iIronShovel = (new InfiToolShovel(ironShovelID + 1, iLevel, (int)((float)iDur * iMod), iSpeed, iDam, iType, iType)).setItemName("iIronShovel");
        dIronShovel = (new InfiToolShovel(ironShovelID + 2, iLevel, (int)((float)iDur * dMod), iSpeed, iDam, iType, dType)).setItemName("dIronShovel");
        gIronShovel = (new InfiToolShovel(ironShovelID + 3, iLevel, (int)((float)iDur * gMod), iSpeed, iDam, iType, gType)).setItemName("gIronShovel");
        rIronShovel = (new InfiToolShovel(ironShovelID + 4, iLevel, (int)((float)iDur * rMod), iSpeed, iDam, iType, rType)).setItemName("rIronShovel");
        oIronShovel = (new InfiToolShovel(ironShovelID + 5, iLevel, (int)((float)iDur * oMod), iSpeed, iDam, iType, oType)).setItemName("oIronShovel");
        bIronShovel = (new InfiToolShovel(ironShovelID + 6, iLevel, (int)((float)iDur * bMod), iSpeed, iDam, iType, bType)).setItemName("bIronShovel");
        nIronShovel = (new InfiToolShovel(ironShovelID + 7, iLevel, (int)((float)iDur * nMod), iSpeed, iDam, iType, nType)).setItemName("nIronShovel");
        glIronShovel = (new InfiToolShovel(ironShovelID + 8, iLevel, (int)((float)iDur * glMod), iSpeed, iDam, iType, glType)).setItemName("glIronShovel");
        iceIronShovel = (new InfiToolShovel(ironShovelID + 9, iLevel, (int)((float)iDur * iceMod), iSpeed, iDam, iType, iceType)).setItemName("iceIronShovel");
        sIronShovel = (new InfiToolShovel(ironShovelID + 10, iLevel, (int)((float)iDur * sMod), iSpeed, iDam, iType, sType)).setItemName("sIronShovel");
        blIronShovel = (new InfiToolShovel(ironShovelID + 11, iLevel, (int)((float)iDur * blMod), iSpeed, iDam, iType, blType)).setItemName("blIronShovel");
        stDiamondShovel = (new InfiToolShovel(diamondShovelID + 0, dLevel, (int)((float)dDur * stMod), dSpeed, dDam, dType, stType)).setItemName("stDiamondShovel");
        iDiamondShovel = (new InfiToolShovel(diamondShovelID + 1, dLevel, (int)((float)dDur * iMod), dSpeed, dDam, dType, iType)).setItemName("iDiamondShovel");
        dDiamondShovel = (new InfiToolShovel(diamondShovelID + 2, dLevel, (int)((float)dDur * dMod), dSpeed, dDam, dType, dType)).setItemName("dDiamondShovel");
        gDiamondShovel = (new InfiToolShovel(diamondShovelID + 3, dLevel, (int)((float)dDur * gMod), dSpeed, dDam, dType, gType)).setItemName("gDiamondShovel");
        rDiamondShovel = (new InfiToolShovel(diamondShovelID + 4, dLevel, (int)((float)dDur * rMod), dSpeed, dDam, dType, rType)).setItemName("rDiamondShovel");
        oDiamondShovel = (new InfiToolShovel(diamondShovelID + 5, dLevel, (int)((float)dDur * oMod), dSpeed, dDam, dType, oType)).setItemName("oDiamondShovel");
        bDiamondShovel = (new InfiToolShovel(diamondShovelID + 6, dLevel, (int)((float)dDur * bMod), dSpeed, dDam, dType, bType)).setItemName("bDiamondShovel");
        mDiamondShovel = (new InfiToolShovel(diamondShovelID + 7, dLevel, (int)((float)dDur * mMod), dSpeed, dDam, dType, mType)).setItemName("mDiamondShovel");
        nDiamondShovel = (new InfiToolShovel(diamondShovelID + 8, dLevel, (int)((float)dDur * nMod), dSpeed, dDam, dType, nType)).setItemName("nDiamondShovel");
        glDiamondShovel = (new InfiToolShovel(diamondShovelID + 9, dLevel, (int)((float)dDur * glMod), dSpeed, dDam, dType, glType)).setItemName("glDiamondShovel");
        blDiamondShovel = (new InfiToolShovel(diamondShovelID + 10, dLevel, (int)((float)dDur * blMod), dSpeed, dDam, dType, blType)).setItemName("blDiamondShovel");
        stGoldShovel = (new InfiToolShovel(goldShovelID + 0, gLevel, (int)((float)gDur * stMod), gSpeed, gDam, gType, stType)).setItemName("stGoldShovel");
        gGoldShovel = (new InfiToolShovel(goldShovelID + 1, gLevel, (int)((float)gDur * gMod), gSpeed, gDam, gType, gType)).setItemName("gGoldShovel");
        oGoldShovel = (new InfiToolShovel(goldShovelID + 2, gLevel, (int)((float)gDur * oMod), gSpeed, gDam, gType, oType)).setItemName("oGoldShovel");
        saGoldShovel = (new InfiToolShovel(goldShovelID + 3, gLevel, (int)((float)gDur * saMod), gSpeed, gDam, gType, saType)).setItemName("saGoldShovel");
        bGoldShovel = (new InfiToolShovel(goldShovelID + 4, gLevel, (int)((float)gDur * bMod), gSpeed, gDam, gType, bType)).setItemName("bGoldShovel");
        mGoldShovel = (new InfiToolShovel(goldShovelID + 5, gLevel, (int)((float)gDur * mMod), gSpeed, gDam, gType, mType)).setItemName("mGoldShovel");
        nGoldShovel = (new InfiToolShovel(goldShovelID + 6, gLevel, (int)((float)gDur * nMod), gSpeed, gDam, gType, nType)).setItemName("nGoldShovel");
        glGoldShovel = (new InfiToolShovel(goldShovelID + 7, gLevel, (int)((float)gDur * glMod), gSpeed, gDam, gType, glType)).setItemName("glGoldShovel");
        iceGoldShovel = (new InfiToolShovel(goldShovelID + 8, gLevel, (int)((float)gDur * iceMod), gSpeed, gDam, gType, iceType)).setItemName("iceGoldShovel");
        sGoldShovel = (new InfiToolShovel(goldShovelID + 9, gLevel, (int)((float)gDur * sMod), gSpeed, gDam, gType, sType)).setItemName("sGoldShovel");
        fGoldShovel = (new InfiToolShovel(goldShovelID + 10, gLevel, (int)((float)gDur * fMod), gSpeed, gDam, gType, fType)).setItemName("fGoldShovel");
        wRedstoneShovel = (new InfiToolShovel(redstoneShovelID + 0, rLevel, (int)((float)rDur * wMod), rSpeed, rDam, rType, wType)).setItemName("wRedstoneShovel");
        stRedstoneShovel = (new InfiToolShovel(redstoneShovelID + 1, rLevel, (int)((float)rDur * stMod), rSpeed, rDam, rType, stType)).setItemName("stRedstoneShovel");
        iRedstoneShovel = (new InfiToolShovel(redstoneShovelID + 2, rLevel, (int)((float)rDur * iMod), rSpeed, rDam, rType, iType)).setItemName("iRedstoneShovel");
        dRedstoneShovel = (new InfiToolShovel(redstoneShovelID + 3, rLevel, (int)((float)rDur * dMod), rSpeed, rDam, rType, dType)).setItemName("dRedstoneShovel");
        rRedstoneShovel = (new InfiToolShovel(redstoneShovelID + 4, rLevel, (int)((float)rDur * rMod), rSpeed, rDam, rType, rType)).setItemName("rRedstoneShovel");
        oRedstoneShovel = (new InfiToolShovel(redstoneShovelID + 5, rLevel, (int)((float)rDur * oMod), rSpeed, rDam, rType, oType)).setItemName("oRedstoneShovel");
        bRedstoneShovel = (new InfiToolShovel(redstoneShovelID + 6, rLevel, (int)((float)rDur * bMod), rSpeed, rDam, rType, bType)).setItemName("bRedstoneShovel");
        mRedstoneShovel = (new InfiToolShovel(redstoneShovelID + 7, rLevel, (int)((float)rDur * mMod), rSpeed, rDam, rType, mType)).setItemName("mRedstoneShovel");
        glRedstoneShovel = (new InfiToolShovel(redstoneShovelID + 8, rLevel, (int)((float)rDur * glMod), rSpeed, rDam, rType, glType)).setItemName("glRedstoneShovel");
        sRedstoneShovel = (new InfiToolShovel(redstoneShovelID + 9, rLevel, (int)((float)rDur * sMod), rSpeed, rDam, rType, sType)).setItemName("sRedstoneShovel");
        blRedstoneShovel = (new InfiToolShovel(redstoneShovelID + 10, rLevel, (int)((float)rDur * blMod), rSpeed, rDam, rType, blType)).setItemName("blRedstoneShovel");
        wObsidianShovel = (new InfiToolShovel(obsidianShovelID + 0, oLevel, (int)((float)oDur * wMod), oSpeed, oDam, oType, wType)).setItemName("wObsidianShovel");
        stObsidianShovel = (new InfiToolShovel(obsidianShovelID + 1, oLevel, (int)((float)oDur * stMod), oSpeed, oDam, oType, stType)).setItemName("stObsidianShovel");
        iObsidianShovel = (new InfiToolShovel(obsidianShovelID + 2, oLevel, (int)((float)oDur * iMod), oSpeed, oDam, oType, iType)).setItemName("iObsidianShovel");
        dObsidianShovel = (new InfiToolShovel(obsidianShovelID + 3, oLevel, (int)((float)oDur * dMod), oSpeed, oDam, oType, dType)).setItemName("dObsidianShovel");
        gObsidianShovel = (new InfiToolShovel(obsidianShovelID + 4, oLevel, (int)((float)oDur * gMod), oSpeed, oDam, oType, gType)).setItemName("gObsidianShovel");
        rObsidianShovel = (new InfiToolShovel(obsidianShovelID + 5, oLevel, (int)((float)oDur * rMod), oSpeed, oDam, oType, rType)).setItemName("rObsidianShovel");
        oObsidianShovel = (new InfiToolShovel(obsidianShovelID + 6, oLevel, (int)((float)oDur * oMod), oSpeed, oDam, oType, oType)).setItemName("oObsidianShovel");
        bObsidianShovel = (new InfiToolShovel(obsidianShovelID + 7, oLevel, (int)((float)oDur * bMod), oSpeed, oDam, oType, bType)).setItemName("bObsidianShovel");
        nObsidianShovel = (new InfiToolShovel(obsidianShovelID + 8, oLevel, (int)((float)oDur * nMod), oSpeed, oDam, oType, nType)).setItemName("nObsidianShovel");
        glObsidianShovel = (new InfiToolShovel(obsidianShovelID + 9, oLevel, (int)((float)oDur * glMod), oSpeed, oDam, oType, glType)).setItemName("glObsidianShovel");
        sObsidianShovel = (new InfiToolShovel(obsidianShovelID + 10, oLevel, (int)((float)oDur * sMod), oSpeed, oDam, oType, sType)).setItemName("sObsidianShovel");
        fObsidianShovel = (new InfiToolShovel(obsidianShovelID + 11, oLevel, (int)((float)oDur * fMod), oSpeed, oDam, oType, fType)).setItemName("fObsidianShovel");
        blObsidianShovel = (new InfiToolShovel(obsidianShovelID + 12, oLevel, (int)((float)oDur * blMod), oSpeed, oDam, oType, blType)).setItemName("blObsidianShovel");
        wSandstoneShovel = (new InfiToolShovel(sandstoneShovelID + 0, saLevel, (int)((float)saDur * wMod), saSpeed, saDam, saType, wType)).setItemName("wSandstoneShovel");
        stSandstoneShovel = (new InfiToolShovel(sandstoneShovelID + 1, saLevel, (int)((float)saDur * stMod), saSpeed, saDam, saType, stType)).setItemName("stSandstoneShovel");
        saSandstoneShovel = (new InfiToolShovel(sandstoneShovelID + 2, saLevel, (int)((float)saDur * saMod), saSpeed, saDam, saType, saType)).setItemName("saSandstoneShovel");
        bSandstoneShovel = (new InfiToolShovel(sandstoneShovelID + 3, saLevel, (int)((float)saDur * bMod), saSpeed, saDam, saType, bType)).setItemName("bSandstoneShovel");
        pSandstoneShovel = (new InfiToolShovel(sandstoneShovelID + 4, saLevel, (int)((float)saDur * pMod), saSpeed, saDam, saType, pType)).setItemName("pSandstoneShovel");
        nSandstoneShovel = (new InfiToolShovel(sandstoneShovelID + 5, saLevel, (int)((float)saDur * nMod), saSpeed, saDam, saType, nType)).setItemName("nSandstoneShovel");
        iceSandstoneShovel = (new InfiToolShovel(sandstoneShovelID + 6, saLevel, (int)((float)saDur * iceMod), saSpeed, saDam, saType, iceType)).setItemName("iceSandstoneShovel");
        sSandstoneShovel = (new InfiToolShovel(sandstoneShovelID + 7, saLevel, (int)((float)saDur * sMod), saSpeed, saDam, saType, sType)).setItemName("sSandstoneShovel");
        cSandstoneShovel = (new InfiToolShovel(sandstoneShovelID + 8, saLevel, (int)((float)saDur * cMod), saSpeed, saDam, saType, cType)).setItemName("cSandstoneShovel");
        fSandstoneShovel = (new InfiToolShovel(sandstoneShovelID + 9, saLevel, (int)((float)saDur * fMod), saSpeed, saDam, saType, fType)).setItemName("fSandstoneShovel");
        brSandstoneShovel = (new InfiToolShovel(sandstoneShovelID + 10, saLevel, (int)((float)saDur * brMod), saSpeed, saDam, saType, brType)).setItemName("brSandstoneShovel");
        wBoneShovel = (new InfiToolShovel(boneShovelID + 0, bLevel, (int)((float)bDur * wMod), bSpeed, bDam, bType, wType)).setItemName("wBoneShovel");
        stBoneShovel = (new InfiToolShovel(boneShovelID + 1, bLevel, (int)((float)bDur * stMod), bSpeed, bDam, bType, stType)).setItemName("stBoneShovel");
        iBoneShovel = (new InfiToolShovel(boneShovelID + 2, bLevel, (int)((float)bDur * iMod), bSpeed, bDam, bType, iType)).setItemName("iBoneShovel");
        dBoneShovel = (new InfiToolShovel(boneShovelID + 3, bLevel, (int)((float)bDur * iMod), bSpeed, bDam, bType, dType)).setItemName("dBoneShovel");
        rBoneShovel = (new InfiToolShovel(boneShovelID + 4, bLevel, (int)((float)bDur * rMod), bSpeed, bDam, bType, rType)).setItemName("rBoneShovel");
        oBoneShovel = (new InfiToolShovel(boneShovelID + 5, bLevel, (int)((float)bDur * oMod), bSpeed, bDam, bType, oType)).setItemName("oBoneShovel");
        bBoneShovel = (new InfiToolShovel(boneShovelID + 6, bLevel, (int)((float)bDur * bMod), bSpeed, bDam, bType, bType)).setItemName("bBoneShovel");
        mBoneShovel = (new InfiToolShovel(boneShovelID + 7, bLevel, (int)((float)bDur * mMod), bSpeed, bDam, bType, mType)).setItemName("mBoneShovel");
        nBoneShovel = (new InfiToolShovel(boneShovelID + 8, bLevel, (int)((float)bDur * nMod), bSpeed, bDam, bType, nType)).setItemName("nBoneShovel");
        glBoneShovel = (new InfiToolShovel(boneShovelID + 9, bLevel, (int)((float)bDur * gMod), bSpeed, bDam, bType, glType)).setItemName("glBoneShovel");
        sBoneShovel = (new InfiToolShovel(boneShovelID + 10, bLevel, (int)((float)bDur * sMod), bSpeed, bDam, bType, sType)).setItemName("sBoneShovel");
        cBoneShovel = (new InfiToolShovel(boneShovelID + 11, bLevel, (int)((float)bDur * cMod), bSpeed, bDam, bType, cType)).setItemName("cBoneShovel");
        fBoneShovel = (new InfiToolShovel(boneShovelID + 12, bLevel, (int)((float)bDur * fMod), bSpeed, bDam, bType, fType)).setItemName("fBoneShovel");
        brBoneShovel = (new InfiToolShovel(boneShovelID + 13, bLevel, (int)((float)bDur * brMod), bSpeed, bDam, bType, brType)).setItemName("brBoneShovel");
        blBoneShovel = (new InfiToolShovel(boneShovelID + 14, bLevel, (int)((float)bDur * blMod), bSpeed, bDam, bType, blType)).setItemName("blBoneShovel");
        wPaperShovel = (new InfiToolShovel(paperShovelID + 0, pLevel, (int)((float)pDur * wMod), pSpeed, pDam, pType, wType)).setItemName("wPaperShovel");
        saPaperShovel = (new InfiToolShovel(paperShovelID + 1, pLevel, (int)((float)pDur * saMod), pSpeed, pDam, pType, saType)).setItemName("saPaperShovel");
        bPaperShovel = (new InfiToolShovel(paperShovelID + 2, pLevel, (int)((float)pDur * bMod), pSpeed, pDam, pType, bType)).setItemName("bPaperShovel");
        pPaperShovel = (new InfiToolShovel(paperShovelID + 3, pLevel, (int)((float)pDur * pMod), pSpeed, pDam, pType, pType)).setItemName("pPaperShovel");
        sPaperShovel = (new InfiToolShovel(paperShovelID + 4, pLevel, (int)((float)pDur * sMod), pSpeed, pDam, pType, sType)).setItemName("sPaperShovel");
        cPaperShovel = (new InfiToolShovel(paperShovelID + 5, pLevel, (int)((float)pDur * cMod), pSpeed, pDam, pType, cType)).setItemName("cPaperShovel");
        brPaperShovel = (new InfiToolShovel(paperShovelID + 6, pLevel, (int)((float)pDur * brMod), pSpeed, pDam, pType, brType)).setItemName("brPaperShovel");
        stMossyShovel = (new InfiToolShovel(mossyShovelID + 0, mLevel, (int)((float)mDur * stMod), mSpeed, mDam, mType, stType)).setItemName("stMossyShovel");
        dMossyShovel = (new InfiToolShovel(mossyShovelID + 1, mLevel, (int)((float)mDur * iMod), mSpeed, mDam, mType, dType)).setItemName("dMossyShovel");
        rMossyShovel = (new InfiToolShovel(mossyShovelID + 2, mLevel, (int)((float)mDur * rMod), mSpeed, mDam, mType, rType)).setItemName("rMossyShovel");
        bMossyShovel = (new InfiToolShovel(mossyShovelID + 3, mLevel, (int)((float)mDur * bMod), mSpeed, mDam, mType, bType)).setItemName("bMossyShovel");
        mMossyShovel = (new InfiToolShovel(mossyShovelID + 4, mLevel, (int)((float)mDur * mMod), mSpeed, mDam, mType, mType)).setItemName("mMossyShovel");
        glMossyShovel = (new InfiToolShovel(mossyShovelID + 5, mLevel, (int)((float)mDur * gMod), mSpeed, mDam, mType, glType)).setItemName("glMossyShovel");
        wNetherrackShovel = (new InfiToolShovel(netherrackShovelID + 0, nLevel, (int)((float)nDur * wMod), nSpeed, nDam, nType, wType)).setItemName("wNetherrackShovel");
        stNetherrackShovel = (new InfiToolShovel(netherrackShovelID + 1, nLevel, (int)((float)nDur * stMod), nSpeed, nDam, nType, stType)).setItemName("stNetherrackShovel");
        iNetherrackShovel = (new InfiToolShovel(netherrackShovelID + 2, nLevel, (int)((float)nDur * iMod), nSpeed, nDam, nType, iType)).setItemName("iNetherrackShovel");
        rNetherrackShovel = (new InfiToolShovel(netherrackShovelID + 3, nLevel, (int)((float)nDur * rMod), nSpeed, nDam, nType, rType)).setItemName("rNetherrackShovel");
        oNetherrackShovel = (new InfiToolShovel(netherrackShovelID + 4, nLevel, (int)((float)nDur * oMod), nSpeed, nDam, nType, oType)).setItemName("oNetherrackShovel");
        saNetherrackShovel = (new InfiToolShovel(netherrackShovelID + 5, nLevel, (int)((float)nDur * saMod), nSpeed, nDam, nType, saType)).setItemName("saNetherrackShovel");
        bNetherrackShovel = (new InfiToolShovel(netherrackShovelID + 6, nLevel, (int)((float)nDur * bMod), nSpeed, nDam, nType, bType)).setItemName("bNetherrackShovel");
        mNetherrackShovel = (new InfiToolShovel(netherrackShovelID + 7, nLevel, (int)((float)nDur * mMod), nSpeed, nDam, nType, mType)).setItemName("mNetherrackShovel");
        nNetherrackShovel = (new InfiToolShovel(netherrackShovelID + 8, nLevel, (int)((float)nDur * nMod), nSpeed, nDam, nType, nType)).setItemName("nNetherrackShovel");
        glNetherrackShovel = (new InfiToolShovel(netherrackShovelID + 9, nLevel, (int)((float)nDur * glMod), nSpeed, nDam, nType, glType)).setItemName("glNetherrackShovel");
        iceNetherrackShovel = (new InfiToolShovel(netherrackShovelID + 10, nLevel, (int)((float)nDur * iceMod), nSpeed, nDam, nType, iceType)).setItemName("iceNetherrackShovel");
        sNetherrackShovel = (new InfiToolShovel(netherrackShovelID + 11, nLevel, (int)((float)nDur * sMod), nSpeed, nDam, nType, sType)).setItemName("sNetherrackShovel");
        cNetherrackShovel = (new InfiToolShovel(netherrackShovelID + 12, nLevel, (int)((float)nDur * cMod), nSpeed, nDam, nType, cType)).setItemName("cNetherrackShovel");
        fNetherrackShovel = (new InfiToolShovel(netherrackShovelID + 13, nLevel, (int)((float)nDur * fMod), nSpeed, nDam, nType, fType)).setItemName("fNetherrackShovel");
        brNetherrackShovel = (new InfiToolShovel(netherrackShovelID + 14, nLevel, (int)((float)nDur * brMod), nSpeed, nDam, nType, brType)).setItemName("brNetherrackShovel");
        blNetherrackShovel = (new InfiToolShovel(netherrackShovelID + 15, nLevel, (int)((float)nDur * blMod), nSpeed, nDam, nType, blType)).setItemName("blNetherrackShovel");
        wGlowstoneShovel = (new InfiToolShovel(glowstoneShovelID + 0, glLevel, (int)((float)glDur * wMod), glSpeed, glDam, glType, wType)).setItemName("wGlowstoneShovel");
        stGlowstoneShovel = (new InfiToolShovel(glowstoneShovelID + 1, glLevel, (int)((float)glDur * stMod), glSpeed, glDam, glType, stType)).setItemName("stGlowstoneShovel");
        iGlowstoneShovel = (new InfiToolShovel(glowstoneShovelID + 2, glLevel, (int)((float)glDur * iMod), glSpeed, glDam, glType, iType)).setItemName("iGlowstoneShovel");
        dGlowstoneShovel = (new InfiToolShovel(glowstoneShovelID + 3, glLevel, (int)((float)glDur * iMod), glSpeed, glDam, glType, dType)).setItemName("dGlowstoneShovel");
        rGlowstoneShovel = (new InfiToolShovel(glowstoneShovelID + 4, glLevel, (int)((float)glDur * rMod), glSpeed, glDam, glType, rType)).setItemName("rGlowstoneShovel");
        oGlowstoneShovel = (new InfiToolShovel(glowstoneShovelID + 5, glLevel, (int)((float)glDur * oMod), glSpeed, glDam, glType, oType)).setItemName("oGlowstoneShovel");
        bGlowstoneShovel = (new InfiToolShovel(glowstoneShovelID + 6, glLevel, (int)((float)glDur * bMod), glSpeed, glDam, glType, bType)).setItemName("bGlowstoneShovel");
        mGlowstoneShovel = (new InfiToolShovel(glowstoneShovelID + 7, glLevel, (int)((float)glDur * mMod), glSpeed, glDam, glType, mType)).setItemName("mGlowstoneShovel");
        nGlowstoneShovel = (new InfiToolShovel(glowstoneShovelID + 8, glLevel, (int)((float)glDur * nMod), glSpeed, glDam, glType, nType)).setItemName("nGlowstoneShovel");
        glGlowstoneShovel = (new InfiToolShovel(glowstoneShovelID + 9, glLevel, (int)((float)glDur * gMod), glSpeed, glDam, glType, glType)).setItemName("glGlowstoneShovel");
        iceGlowstoneShovel = (new InfiToolShovel(glowstoneShovelID + 10, glLevel, (int)((float)glDur * iceMod), glSpeed, glDam, glType, iceType)).setItemName("iceGlowstoneShovel");
        lGlowstoneShovel = (new InfiToolShovel(glowstoneShovelID + 11, glLevel, (int)((float)glDur * gMod), glSpeed, glDam, glType, lType)).setItemName("lGlowstoneShovel");
        sGlowstoneShovel = (new InfiToolShovel(glowstoneShovelID + 12, glLevel, (int)((float)glDur * sMod), glSpeed, glDam, glType, sType)).setItemName("sGlowstoneShovel");
        blGlowstoneShovel = (new InfiToolShovel(glowstoneShovelID + 13, glLevel, (int)((float)glDur * blMod), glSpeed, glDam, glType, blType)).setItemName("blGlowstoneShovel");
        wIceShovel = (new InfiToolShovel(iceShovelID + 0, iceLevel, (int)((float)iceDur * wMod), iceSpeed, iceDam, iceType, wType)).setItemName("wIceShovel");
        stIceShovel = (new InfiToolShovel(iceShovelID + 1, iceLevel, (int)((float)iceDur * stMod), iceSpeed, iceDam, iceType, stType)).setItemName("stIceShovel");
        iIceShovel = (new InfiToolShovel(iceShovelID + 2, iceLevel, (int)((float)iceDur * iMod), iceSpeed, iceDam, iceType, iType)).setItemName("iIceShovel");
        dIceShovel = (new InfiToolShovel(iceShovelID + 3, iceLevel, (int)((float)iceDur * dMod), iceSpeed, iceDam, iceType, dType)).setItemName("dIceShovel");
        gIceShovel = (new InfiToolShovel(iceShovelID + 4, iceLevel, (int)((float)iceDur * gMod), iceSpeed, iceDam, iceType, gType)).setItemName("gIceShovel");
        rIceShovel = (new InfiToolShovel(iceShovelID + 5, iceLevel, (int)((float)iceDur * rMod), iceSpeed, iceDam, iceType, rType)).setItemName("rIceShovel");
        oIceShovel = (new InfiToolShovel(iceShovelID + 6, iceLevel, (int)((float)iceDur * oMod), iceSpeed, iceDam, iceType, oType)).setItemName("oIceShovel");
        saIceShovel = (new InfiToolShovel(iceShovelID + 7, iceLevel, (int)((float)iceDur * saMod), iceSpeed, iceDam, iceType, saType)).setItemName("saIceShovel");
        bIceShovel = (new InfiToolShovel(iceShovelID + 8, iceLevel, (int)((float)iceDur * bMod), iceSpeed, iceDam, iceType, bType)).setItemName("bIceShovel");
        glIceShovel = (new InfiToolShovel(iceShovelID + 9, iceLevel, (int)((float)iceDur * gMod), iceSpeed, iceDam, iceType, glType)).setItemName("glIceShovel");
        iceIceShovel = (new InfiToolShovel(iceShovelID + 10, iceLevel, (int)((float)iceDur * iMod), iceSpeed, iceDam, iceType, iceType)).setItemName("iceIceShovel");
        sIceShovel = (new InfiToolShovel(iceShovelID + 11, iceLevel, (int)((float)iceDur * sMod), iceSpeed, iceDam, iceType, sType)).setItemName("sIceShovel");
        cIceShovel = (new InfiToolShovel(iceShovelID + 12, iceLevel, (int)((float)iceDur * cMod), iceSpeed, iceDam, iceType, cType)).setItemName("cIceShovel");
        fIceShovel = (new InfiToolShovel(iceShovelID + 13, iceLevel, (int)((float)iceDur * fMod), iceSpeed, iceDam, iceType, fType)).setItemName("fIceShovel");
        brIceShovel = (new InfiToolShovel(iceShovelID + 14, iceLevel, (int)((float)iceDur * brMod), iceSpeed, iceDam, iceType, brType)).setItemName("brIceShovel");
        dLavaShovel = (new InfiToolShovel(lavaShovelID + 0, lLevel, (int)((float)lDur * iMod), lSpeed, lDam, lType, dType)).setItemName("dLavaShovel");
        rLavaShovel = (new InfiToolShovel(lavaShovelID + 1, lLevel, (int)((float)lDur * rMod), lSpeed, lDam, lType, rType)).setItemName("rLavaShovel");
        bLavaShovel = (new InfiToolShovel(lavaShovelID + 2, lLevel, (int)((float)lDur * bMod), lSpeed, lDam, lType, bType)).setItemName("bLavaShovel");
        nLavaShovel = (new InfiToolShovel(lavaShovelID + 3, lLevel, (int)((float)lDur * nMod), lSpeed, lDam, lType, nType)).setItemName("nLavaShovel");
        glLavaShovel = (new InfiToolShovel(lavaShovelID + 4, lLevel, (int)((float)lDur * gMod), lSpeed, lDam, lType, glType)).setItemName("glLavaShovel");
        lLavaShovel = (new InfiToolShovel(lavaShovelID + 5, lLevel, (int)((float)lDur * lMod), lSpeed, lDam, lType, lType)).setItemName("lLavaShovel");
        blLavaShovel = (new InfiToolShovel(lavaShovelID + 6, lLevel, (int)((float)lDur * blMod), lSpeed, lDam, lType, blType)).setItemName("blLavaShovel");
        wSlimeShovel = (new InfiToolShovel(slimeShovelID + 0, sLevel, (int)((float)sDur * wMod), sSpeed, sDam, sType, wType)).setItemName("wSlimeShovel");
        stSlimeShovel = (new InfiToolShovel(slimeShovelID + 1, sLevel, (int)((float)sDur * stMod), sSpeed, sDam, sType, stType)).setItemName("stSlimeShovel");
        iSlimeShovel = (new InfiToolShovel(slimeShovelID + 2, sLevel, (int)((float)sDur * iMod), sSpeed, sDam, sType, iType)).setItemName("iSlimeShovel");
        dSlimeShovel = (new InfiToolShovel(slimeShovelID + 3, sLevel, (int)((float)sDur * iMod), sSpeed, sDam, sType, dType)).setItemName("dSlimeShovel");
        gSlimeShovel = (new InfiToolShovel(slimeShovelID + 4, sLevel, (int)((float)sDur * gMod), sSpeed, sDam, sType, gType)).setItemName("gSlimeShovel");
        rSlimeShovel = (new InfiToolShovel(slimeShovelID + 5, sLevel, (int)((float)sDur * rMod), sSpeed, sDam, sType, rType)).setItemName("rSlimeShovel");
        oSlimeShovel = (new InfiToolShovel(slimeShovelID + 6, sLevel, (int)((float)sDur * oMod), sSpeed, sDam, sType, oType)).setItemName("oSlimeShovel");
        saSlimeShovel = (new InfiToolShovel(slimeShovelID + 7, sLevel, (int)((float)sDur * saMod), sSpeed, sDam, sType, saType)).setItemName("saSlimeShovel");
        bSlimeShovel = (new InfiToolShovel(slimeShovelID + 8, sLevel, (int)((float)sDur * bMod), sSpeed, sDam, sType, bType)).setItemName("bSlimeShovel");
        pSlimeShovel = (new InfiToolShovel(slimeShovelID + 9, sLevel, (int)((float)sDur * pMod), sSpeed, sDam, sType, pType)).setItemName("pSlimeShovel");
        mSlimeShovel = (new InfiToolShovel(slimeShovelID + 10, sLevel, (int)((float)sDur * mMod), sSpeed, sDam, sType, mType)).setItemName("mSlimeShovel");
        nSlimeShovel = (new InfiToolShovel(slimeShovelID + 11, sLevel, (int)((float)sDur * nMod), sSpeed, sDam, sType, nType)).setItemName("nSlimeShovel");
        glSlimeShovel = (new InfiToolShovel(slimeShovelID + 12, sLevel, (int)((float)sDur * gMod), sSpeed, sDam, sType, glType)).setItemName("glSlimeShovel");
        iceSlimeShovel = (new InfiToolShovel(slimeShovelID + 13, sLevel, (int)((float)sDur * iMod), sSpeed, sDam, sType, iceType)).setItemName("iceSlimeShovel");
        lSlimeShovel = (new InfiToolShovel(slimeShovelID + 14, sLevel, (int)((float)sDur * lMod), sSpeed, sDam, sType, lType)).setItemName("lSlimeShovel");
        sSlimeShovel = (new InfiToolShovel(slimeShovelID + 15, sLevel, (int)((float)sDur * sMod), sSpeed, sDam, sType, sType)).setItemName("sSlimeShovel");
        cSlimeShovel = (new InfiToolShovel(slimeShovelID + 16, sLevel, (int)((float)sDur * cMod), sSpeed, sDam, sType, cType)).setItemName("cSlimeShovel");
        fSlimeShovel = (new InfiToolShovel(slimeShovelID + 17, sLevel, (int)((float)sDur * fMod), sSpeed, sDam, sType, fType)).setItemName("fSlimeShovel");
        brSlimeShovel = (new InfiToolShovel(slimeShovelID + 18, sLevel, (int)((float)sDur * brMod), sSpeed, sDam, sType, brType)).setItemName("brSlimeShovel");
        blSlimeShovel = (new InfiToolShovel(slimeShovelID + 19, sLevel, (int)((float)sDur * blMod), sSpeed, sDam, sType, blType)).setItemName("blSlimeShovel");
        wCactusShovel = (new InfiToolShovel(cactusShovelID + 0, cLevel, (int)((float)cDur * wMod), cSpeed, cDam, cType, wType)).setItemName("wCactusShovel");
        stCactusShovel = (new InfiToolShovel(cactusShovelID + 1, cLevel, (int)((float)cDur * stMod), cSpeed, cDam, cType, stType)).setItemName("stCactusShovel");
        saCactusShovel = (new InfiToolShovel(cactusShovelID + 2, cLevel, (int)((float)cDur * saMod), cSpeed, cDam, cType, saType)).setItemName("saCactusShovel");
        bCactusShovel = (new InfiToolShovel(cactusShovelID + 3, cLevel, (int)((float)cDur * bMod), cSpeed, cDam, cType, bType)).setItemName("bCactusShovel");
        pCactusShovel = (new InfiToolShovel(cactusShovelID + 4, cLevel, (int)((float)cDur * pMod), cSpeed, cDam, cType, pType)).setItemName("pCactusShovel");
        nCactusShovel = (new InfiToolShovel(cactusShovelID + 5, cLevel, (int)((float)cDur * nMod), cSpeed, cDam, cType, nType)).setItemName("nCactusShovel");
        sCactusShovel = (new InfiToolShovel(cactusShovelID + 6, cLevel, (int)((float)cDur * sMod), cSpeed, cDam, cType, sType)).setItemName("sCactusShovel");
        cCactusShovel = (new InfiToolShovel(cactusShovelID + 7, cLevel, (int)((float)cDur * cMod), cSpeed, cDam, cType, cType)).setItemName("cCactusShovel");
        fCactusShovel = (new InfiToolShovel(cactusShovelID + 8, cLevel, (int)((float)cDur * fMod), cSpeed, cDam, cType, fType)).setItemName("fCactusShovel");
        brCactusShovel = (new InfiToolShovel(cactusShovelID + 9, cLevel, (int)((float)cDur * brMod), cSpeed, cDam, cType, brType)).setItemName("brCactusShovel");
        wFlintShovel = (new InfiToolShovel(flintShovelID + 0, fLevel, (int)((float)fDur * wMod), fSpeed, fDam, fType, wType)).setItemName("wFlintShovel");
        stFlintShovel = (new InfiToolShovel(flintShovelID + 1, fLevel, (int)((float)fDur * stMod), fSpeed, fDam, fType, stType)).setItemName("stFlintShovel");
        iFlintShovel = (new InfiToolShovel(flintShovelID + 2, fLevel, (int)((float)fDur * iMod), fSpeed, fDam, fType, iType)).setItemName("iFlintShovel");
        gFlintShovel = (new InfiToolShovel(flintShovelID + 3, fLevel, (int)((float)fDur * gMod), fSpeed, fDam, fType, gType)).setItemName("gFlintShovel");
        oFlintShovel = (new InfiToolShovel(flintShovelID + 4, fLevel, (int)((float)fDur * oMod), fSpeed, fDam, fType, oType)).setItemName("oFlintShovel");
        saFlintShovel = (new InfiToolShovel(flintShovelID + 5, fLevel, (int)((float)fDur * saMod), fSpeed, fDam, fType, saType)).setItemName("saFlintShovel");
        bFlintShovel = (new InfiToolShovel(flintShovelID + 6, fLevel, (int)((float)fDur * bMod), fSpeed, fDam, fType, bType)).setItemName("bFlintShovel");
        nFlintShovel = (new InfiToolShovel(flintShovelID + 7, fLevel, (int)((float)fDur * nMod), fSpeed, fDam, fType, nType)).setItemName("nFlintShovel");
        iceFlintShovel = (new InfiToolShovel(flintShovelID + 8, fLevel, (int)((float)fDur * wMod), fSpeed, fDam, fType, iceType)).setItemName("iceFlintShovel");
        sFlintShovel = (new InfiToolShovel(flintShovelID + 9, fLevel, (int)((float)fDur * sMod), fSpeed, fDam, fType, sType)).setItemName("sFlintShovel");
        cFlintShovel = (new InfiToolShovel(flintShovelID + 10, fLevel, (int)((float)fDur * cMod), fSpeed, fDam, fType, cType)).setItemName("cFlintShovel");
        fFlintShovel = (new InfiToolShovel(flintShovelID + 11, fLevel, (int)((float)fDur * fMod), fSpeed, fDam, fType, fType)).setItemName("fFlintShovel");
        brFlintShovel = (new InfiToolShovel(flintShovelID + 12, fLevel, (int)((float)fDur * brMod), fSpeed, fDam, fType, brType)).setItemName("brFlintShovel");
        blFlintShovel = (new InfiToolShovel(flintShovelID + 13, fLevel, (int)((float)fDur * blMod), fSpeed, fDam, fType, blType)).setItemName("blFlintShovel");
        wBrickShovel = (new InfiToolShovel(brickShovelID + 0, brLevel, (int)((float)brDur * wMod), brSpeed, brDam, brType, wType)).setItemName("wBrickShovel");
        stBrickShovel = (new InfiToolShovel(brickShovelID + 1, brLevel, (int)((float)brDur * stMod), brSpeed, brDam, brType, stType)).setItemName("stBrickShovel");
        saBrickShovel = (new InfiToolShovel(brickShovelID + 2, brLevel, (int)((float)brDur * saMod), brSpeed, brDam, brType, saType)).setItemName("saBrickShovel");
        bBrickShovel = (new InfiToolShovel(brickShovelID + 3, brLevel, (int)((float)brDur * bMod), brSpeed, brDam, brType, bType)).setItemName("bBrickShovel");
        pBrickShovel = (new InfiToolShovel(brickShovelID + 4, brLevel, (int)((float)brDur * pMod), brSpeed, brDam, brType, pType)).setItemName("pBrickShovel");
        nBrickShovel = (new InfiToolShovel(brickShovelID + 5, brLevel, (int)((float)brDur * nMod), brSpeed, brDam, brType, nType)).setItemName("nBrickShovel");
        iceBrickShovel = (new InfiToolShovel(brickShovelID + 6, brLevel, (int)((float)brDur * iceMod), brSpeed, brDam, brType, iceType)).setItemName("iceBrickShovel");
        sBrickShovel = (new InfiToolShovel(brickShovelID + 7, brLevel, (int)((float)brDur * sMod), brSpeed, brDam, brType, sType)).setItemName("sBrickShovel");
        cBrickShovel = (new InfiToolShovel(brickShovelID + 8, brLevel, (int)((float)brDur * cMod), brSpeed, brDam, brType, cType)).setItemName("cBrickShovel");
        fBrickShovel = (new InfiToolShovel(brickShovelID + 9, brLevel, (int)((float)brDur * fMod), brSpeed, brDam, brType, fType)).setItemName("fBrickShovel");
        brBrickShovel = (new InfiToolShovel(brickShovelID + 10, brLevel, (int)((float)brDur * brMod), brSpeed, brDam, brType, brType)).setItemName("brBrickShovel");
        dBlazeShovel = (new InfiToolShovel(blazeShovelID + 0, blLevel, (int)((float)blDur * dMod), blSpeed, blDam, blType, dType)).setItemName("wBlazeShovel");
        rBlazeShovel = (new InfiToolShovel(blazeShovelID + 1, blLevel, (int)((float)blDur * rMod), blSpeed, blDam, blType, rType)).setItemName("rBlazeShovel");
        bBlazeShovel = (new InfiToolShovel(blazeShovelID + 2, blLevel, (int)((float)blDur * bMod), blSpeed, blDam, blType, bType)).setItemName("bBlazeShovel");
        nBlazeShovel = (new InfiToolShovel(blazeShovelID + 3, blLevel, (int)((float)blDur * nMod), blSpeed, blDam, blType, nType)).setItemName("nBlazeShovel");
        glBlazeShovel = (new InfiToolShovel(blazeShovelID + 4, blLevel, (int)((float)blDur * glMod), blSpeed, blDam, blType, glType)).setItemName("glBlazeShovel");
        lBlazeShovel = (new InfiToolShovel(blazeShovelID + 5, blLevel, (int)((float)blDur * lMod), blSpeed, blDam, blType, lType)).setItemName("lBlazeShovel");
        fBlazeShovel = (new InfiToolShovel(blazeShovelID + 6, blLevel, (int)((float)blDur * fMod), blSpeed, blDam, blType, fType)).setItemName("fBlazeShovel");
        blBlazeShovel = (new InfiToolShovel(blazeShovelID + 7, blLevel, (int)((float)blDur * bMod), blSpeed, blDam, blType, bType)).setItemName("blBlazeShovel");
        stWoodAxe = (new InfiToolAxe(woodAxeID + 0, wLevel, (int)((float)wDur * stMod), wSpeed, wDam, wType, stType)).setItemName("stWoodAxe");
        saWoodAxe = (new InfiToolAxe(woodAxeID + 1, wLevel, (int)((float)wDur * saMod), wSpeed, wDam, wType, saType)).setItemName("saWoodAxe");
        bWoodAxe = (new InfiToolAxe(woodAxeID + 2, wLevel, (int)((float)wDur * bMod), wSpeed, wDam, wType, bType)).setItemName("bWoodAxe");
        pWoodAxe = (new InfiToolAxe(woodAxeID + 3, wLevel, (int)((float)wDur * pMod), wSpeed, wDam, wType, pType)).setItemName("pWoodAxe");
        nWoodAxe = (new InfiToolAxe(woodAxeID + 4, wLevel, (int)((float)wDur * nMod), wSpeed, wDam, wType, nType)).setItemName("nWoodAxe");
        sWoodAxe = (new InfiToolAxe(woodAxeID + 5, wLevel, (int)((float)wDur * sMod), wSpeed, wDam, wType, sType)).setItemName("sWoodAxe");
        cWoodAxe = (new InfiToolAxe(woodAxeID + 6, wLevel, (int)((float)wDur * cMod), wSpeed, wDam, wType, cType)).setItemName("cWoodAxe");
        fWoodAxe = (new InfiToolAxe(woodAxeID + 7, wLevel, (int)((float)wDur * fMod), wSpeed, wDam, wType, fType)).setItemName("fWoodAxe");
        brWoodAxe = (new InfiToolAxe(woodAxeID + 8, wLevel, (int)((float)wDur * brMod), wSpeed, wDam, wType, brType)).setItemName("brWoodAxe");
        stStoneAxe = (new InfiToolAxe(stoneAxeID + 0, stLevel, (int)((float)stDur * stMod), stSpeed, stDam, stType, stType)).setItemName("stStoneAxe");
        saStoneAxe = (new InfiToolAxe(stoneAxeID + 1, stLevel, (int)((float)stDur * saMod), stSpeed, stDam, stType, saType)).setItemName("saStoneAxe");
        bStoneAxe = (new InfiToolAxe(stoneAxeID + 2, stLevel, (int)((float)stDur * bMod), stSpeed, stDam, stType, bType)).setItemName("bStoneAxe");
        pStoneAxe = (new InfiToolAxe(stoneAxeID + 3, stLevel, (int)((float)stDur * pMod), stSpeed, stDam, stType, pType)).setItemName("pStoneAxe");
        mStoneAxe = (new InfiToolAxe(stoneAxeID + 4, stLevel, (int)((float)stDur * mMod), stSpeed, stDam, stType, mType)).setItemName("mStoneAxe");
        nStoneAxe = (new InfiToolAxe(stoneAxeID + 5, stLevel, (int)((float)stDur * nMod), stSpeed, stDam, stType, nType)).setItemName("nStoneAxe");
        iceStoneAxe = (new InfiToolAxe(stoneAxeID + 6, stLevel, (int)((float)stDur * iceMod), stSpeed, stDam, stType, iceType)).setItemName("iceStoneAxe");
        sStoneAxe = (new InfiToolAxe(stoneAxeID + 7, stLevel, (int)((float)stDur * sMod), stSpeed, stDam, stType, sType)).setItemName("sStoneAxe");
        cStoneAxe = (new InfiToolAxe(stoneAxeID + 8, stLevel, (int)((float)stDur * cMod), stSpeed, stDam, stType, cType)).setItemName("cStoneAxe");
        fStoneAxe = (new InfiToolAxe(stoneAxeID + 9, stLevel, (int)((float)stDur * fMod), stSpeed, stDam, stType, fType)).setItemName("fStoneAxe");
        brStoneAxe = (new InfiToolAxe(stoneAxeID + 10, stLevel, (int)((float)stDur * brMod), stSpeed, stDam, stType, brType)).setItemName("brStoneAxe");
        stIronAxe = (new InfiToolAxe(ironAxeID + 0, iLevel, (int)((float)iDur * stMod), iSpeed, iDam, iType, stType)).setItemName("stIronAxe");
        iIronAxe = (new InfiToolAxe(ironAxeID + 1, iLevel, (int)((float)iDur * iMod), iSpeed, iDam, iType, iType)).setItemName("iIronAxe");
        dIronAxe = (new InfiToolAxe(ironAxeID + 2, iLevel, (int)((float)iDur * dMod), iSpeed, iDam, iType, dType)).setItemName("dIronAxe");
        gIronAxe = (new InfiToolAxe(ironAxeID + 3, iLevel, (int)((float)iDur * gMod), iSpeed, iDam, iType, gType)).setItemName("gIronAxe");
        rIronAxe = (new InfiToolAxe(ironAxeID + 4, iLevel, (int)((float)iDur * rMod), iSpeed, iDam, iType, rType)).setItemName("rIronAxe");
        oIronAxe = (new InfiToolAxe(ironAxeID + 5, iLevel, (int)((float)iDur * oMod), iSpeed, iDam, iType, oType)).setItemName("oIronAxe");
        bIronAxe = (new InfiToolAxe(ironAxeID + 6, iLevel, (int)((float)iDur * bMod), iSpeed, iDam, iType, bType)).setItemName("bIronAxe");
        nIronAxe = (new InfiToolAxe(ironAxeID + 7, iLevel, (int)((float)iDur * nMod), iSpeed, iDam, iType, nType)).setItemName("nIronAxe");
        glIronAxe = (new InfiToolAxe(ironAxeID + 8, iLevel, (int)((float)iDur * glMod), iSpeed, iDam, iType, glType)).setItemName("glIronAxe");
        iceIronAxe = (new InfiToolAxe(ironAxeID + 9, iLevel, (int)((float)iDur * iceMod), iSpeed, iDam, iType, iceType)).setItemName("iceIronAxe");
        sIronAxe = (new InfiToolAxe(ironAxeID + 10, iLevel, (int)((float)iDur * sMod), iSpeed, iDam, iType, sType)).setItemName("sIronAxe");
        blIronAxe = (new InfiToolAxe(ironAxeID + 11, iLevel, (int)((float)iDur * blMod), iSpeed, iDam, iType, blType)).setItemName("blIronAxe");
        stDiamondAxe = (new InfiToolAxe(diamondAxeID + 0, dLevel, (int)((float)dDur * stMod), dSpeed, dDam, dType, stType)).setItemName("stDiamondAxe");
        iDiamondAxe = (new InfiToolAxe(diamondAxeID + 1, dLevel, (int)((float)dDur * iMod), dSpeed, dDam, dType, iType)).setItemName("iDiamondAxe");
        dDiamondAxe = (new InfiToolAxe(diamondAxeID + 2, dLevel, (int)((float)dDur * dMod), dSpeed, dDam, dType, dType)).setItemName("dDiamondAxe");
        gDiamondAxe = (new InfiToolAxe(diamondAxeID + 3, dLevel, (int)((float)dDur * gMod), dSpeed, dDam, dType, gType)).setItemName("gDiamondAxe");
        rDiamondAxe = (new InfiToolAxe(diamondAxeID + 4, dLevel, (int)((float)dDur * rMod), dSpeed, dDam, dType, rType)).setItemName("rDiamondAxe");
        oDiamondAxe = (new InfiToolAxe(diamondAxeID + 5, dLevel, (int)((float)dDur * oMod), dSpeed, dDam, dType, oType)).setItemName("oDiamondAxe");
        bDiamondAxe = (new InfiToolAxe(diamondAxeID + 6, dLevel, (int)((float)dDur * bMod), dSpeed, dDam, dType, bType)).setItemName("bDiamondAxe");
        mDiamondAxe = (new InfiToolAxe(diamondAxeID + 7, dLevel, (int)((float)dDur * mMod), dSpeed, dDam, dType, mType)).setItemName("mDiamondAxe");
        nDiamondAxe = (new InfiToolAxe(diamondAxeID + 8, dLevel, (int)((float)dDur * nMod), dSpeed, dDam, dType, nType)).setItemName("nDiamondAxe");
        glDiamondAxe = (new InfiToolAxe(diamondAxeID + 9, dLevel, (int)((float)dDur * glMod), dSpeed, dDam, dType, glType)).setItemName("glDiamondAxe");
        blDiamondAxe = (new InfiToolAxe(diamondAxeID + 10, dLevel, (int)((float)dDur * blMod), dSpeed, dDam, dType, blType)).setItemName("blDiamondAxe");
        stGoldAxe = (new InfiToolAxe(goldAxeID + 0, gLevel, (int)((float)gDur * stMod), gSpeed, gDam, gType, stType)).setItemName("stGoldAxe");
        gGoldAxe = (new InfiToolAxe(goldAxeID + 1, gLevel, (int)((float)gDur * gMod), gSpeed, gDam, gType, gType)).setItemName("gGoldAxe");
        oGoldAxe = (new InfiToolAxe(goldAxeID + 2, gLevel, (int)((float)gDur * oMod), gSpeed, gDam, gType, oType)).setItemName("oGoldAxe");
        saGoldAxe = (new InfiToolAxe(goldAxeID + 3, gLevel, (int)((float)gDur * saMod), gSpeed, gDam, gType, saType)).setItemName("saGoldAxe");
        bGoldAxe = (new InfiToolAxe(goldAxeID + 4, gLevel, (int)((float)gDur * bMod), gSpeed, gDam, gType, bType)).setItemName("bGoldAxe");
        mGoldAxe = (new InfiToolAxe(goldAxeID + 5, gLevel, (int)((float)gDur * mMod), gSpeed, gDam, gType, mType)).setItemName("mGoldAxe");
        nGoldAxe = (new InfiToolAxe(goldAxeID + 6, gLevel, (int)((float)gDur * nMod), gSpeed, gDam, gType, nType)).setItemName("nGoldAxe");
        glGoldAxe = (new InfiToolAxe(goldAxeID + 7, gLevel, (int)((float)gDur * glMod), gSpeed, gDam, gType, glType)).setItemName("glGoldAxe");
        iceGoldAxe = (new InfiToolAxe(goldAxeID + 8, gLevel, (int)((float)gDur * iceMod), gSpeed, gDam, gType, iceType)).setItemName("iceGoldAxe");
        sGoldAxe = (new InfiToolAxe(goldAxeID + 9, gLevel, (int)((float)gDur * sMod), gSpeed, gDam, gType, sType)).setItemName("sGoldAxe");
        fGoldAxe = (new InfiToolAxe(goldAxeID + 10, gLevel, (int)((float)gDur * fMod), gSpeed, gDam, gType, fType)).setItemName("fGoldAxe");
        wRedstoneAxe = (new InfiToolAxe(redstoneAxeID + 0, rLevel, (int)((float)rDur * wMod), rSpeed, rDam, rType, wType)).setItemName("wRedstoneAxe");
        stRedstoneAxe = (new InfiToolAxe(redstoneAxeID + 1, rLevel, (int)((float)rDur * stMod), rSpeed, rDam, rType, stType)).setItemName("stRedstoneAxe");
        iRedstoneAxe = (new InfiToolAxe(redstoneAxeID + 2, rLevel, (int)((float)rDur * iMod), rSpeed, rDam, rType, iType)).setItemName("iRedstoneAxe");
        dRedstoneAxe = (new InfiToolAxe(redstoneAxeID + 3, rLevel, (int)((float)rDur * dMod), rSpeed, rDam, rType, dType)).setItemName("dRedstoneAxe");
        rRedstoneAxe = (new InfiToolAxe(redstoneAxeID + 4, rLevel, (int)((float)rDur * rMod), rSpeed, rDam, rType, rType)).setItemName("rRedstoneAxe");
        oRedstoneAxe = (new InfiToolAxe(redstoneAxeID + 5, rLevel, (int)((float)rDur * oMod), rSpeed, rDam, rType, oType)).setItemName("oRedstoneAxe");
        bRedstoneAxe = (new InfiToolAxe(redstoneAxeID + 6, rLevel, (int)((float)rDur * bMod), rSpeed, rDam, rType, bType)).setItemName("bRedstoneAxe");
        mRedstoneAxe = (new InfiToolAxe(redstoneAxeID + 7, rLevel, (int)((float)rDur * mMod), rSpeed, rDam, rType, mType)).setItemName("mRedstoneAxe");
        glRedstoneAxe = (new InfiToolAxe(redstoneAxeID + 8, rLevel, (int)((float)rDur * glMod), rSpeed, rDam, rType, glType)).setItemName("glRedstoneAxe");
        sRedstoneAxe = (new InfiToolAxe(redstoneAxeID + 9, rLevel, (int)((float)rDur * sMod), rSpeed, rDam, rType, sType)).setItemName("sRedstoneAxe");
        blRedstoneAxe = (new InfiToolAxe(redstoneAxeID + 10, rLevel, (int)((float)rDur * blMod), rSpeed, rDam, rType, blType)).setItemName("blRedstoneAxe");
        wObsidianAxe = (new InfiToolAxe(obsidianAxeID + 0, oLevel, (int)((float)oDur * wMod), oSpeed, oDam, oType, wType)).setItemName("wObsidianAxe");
        stObsidianAxe = (new InfiToolAxe(obsidianAxeID + 1, oLevel, (int)((float)oDur * stMod), oSpeed, oDam, oType, stType)).setItemName("stObsidianAxe");
        iObsidianAxe = (new InfiToolAxe(obsidianAxeID + 2, oLevel, (int)((float)oDur * iMod), oSpeed, oDam, oType, iType)).setItemName("iObsidianAxe");
        dObsidianAxe = (new InfiToolAxe(obsidianAxeID + 3, oLevel, (int)((float)oDur * dMod), oSpeed, oDam, oType, dType)).setItemName("dObsidianAxe");
        gObsidianAxe = (new InfiToolAxe(obsidianAxeID + 4, oLevel, (int)((float)oDur * gMod), oSpeed, oDam, oType, gType)).setItemName("gObsidianAxe");
        rObsidianAxe = (new InfiToolAxe(obsidianAxeID + 5, oLevel, (int)((float)oDur * rMod), oSpeed, oDam, oType, rType)).setItemName("rObsidianAxe");
        oObsidianAxe = (new InfiToolAxe(obsidianAxeID + 6, oLevel, (int)((float)oDur * oMod), oSpeed, oDam, oType, oType)).setItemName("oObsidianAxe");
        bObsidianAxe = (new InfiToolAxe(obsidianAxeID + 7, oLevel, (int)((float)oDur * bMod), oSpeed, oDam, oType, bType)).setItemName("bObsidianAxe");
        nObsidianAxe = (new InfiToolAxe(obsidianAxeID + 8, oLevel, (int)((float)oDur * nMod), oSpeed, oDam, oType, nType)).setItemName("nObsidianAxe");
        glObsidianAxe = (new InfiToolAxe(obsidianAxeID + 9, oLevel, (int)((float)oDur * glMod), oSpeed, oDam, oType, glType)).setItemName("glObsidianAxe");
        sObsidianAxe = (new InfiToolAxe(obsidianAxeID + 10, oLevel, (int)((float)oDur * sMod), oSpeed, oDam, oType, sType)).setItemName("sObsidianAxe");
        fObsidianAxe = (new InfiToolAxe(obsidianAxeID + 11, oLevel, (int)((float)oDur * fMod), oSpeed, oDam, oType, fType)).setItemName("fObsidianAxe");
        blObsidianAxe = (new InfiToolAxe(obsidianAxeID + 12, oLevel, (int)((float)oDur * blMod), oSpeed, oDam, oType, blType)).setItemName("blObsidianAxe");
        wSandstoneAxe = (new InfiToolAxe(sandstoneAxeID + 0, saLevel, (int)((float)saDur * wMod), saSpeed, saDam, saType, wType)).setItemName("wSandstoneAxe");
        stSandstoneAxe = (new InfiToolAxe(sandstoneAxeID + 1, saLevel, (int)((float)saDur * stMod), saSpeed, saDam, saType, stType)).setItemName("stSandstoneAxe");
        saSandstoneAxe = (new InfiToolAxe(sandstoneAxeID + 2, saLevel, (int)((float)saDur * saMod), saSpeed, saDam, saType, saType)).setItemName("saSandstoneAxe");
        bSandstoneAxe = (new InfiToolAxe(sandstoneAxeID + 3, saLevel, (int)((float)saDur * bMod), saSpeed, saDam, saType, bType)).setItemName("bSandstoneAxe");
        pSandstoneAxe = (new InfiToolAxe(sandstoneAxeID + 4, saLevel, (int)((float)saDur * pMod), saSpeed, saDam, saType, pType)).setItemName("pSandstoneAxe");
        nSandstoneAxe = (new InfiToolAxe(sandstoneAxeID + 5, saLevel, (int)((float)saDur * nMod), saSpeed, saDam, saType, nType)).setItemName("nSandstoneAxe");
        iceSandstoneAxe = (new InfiToolAxe(sandstoneAxeID + 6, saLevel, (int)((float)saDur * iceMod), saSpeed, saDam, saType, iceType)).setItemName("iceSandstoneAxe");
        sSandstoneAxe = (new InfiToolAxe(sandstoneAxeID + 7, saLevel, (int)((float)saDur * sMod), saSpeed, saDam, saType, sType)).setItemName("sSandstoneAxe");
        cSandstoneAxe = (new InfiToolAxe(sandstoneAxeID + 8, saLevel, (int)((float)saDur * cMod), saSpeed, saDam, saType, cType)).setItemName("cSandstoneAxe");
        fSandstoneAxe = (new InfiToolAxe(sandstoneAxeID + 9, saLevel, (int)((float)saDur * fMod), saSpeed, saDam, saType, fType)).setItemName("fSandstoneAxe");
        brSandstoneAxe = (new InfiToolAxe(sandstoneAxeID + 10, saLevel, (int)((float)saDur * brMod), saSpeed, saDam, saType, brType)).setItemName("brSandstoneAxe");
        wBoneAxe = (new InfiToolAxe(boneAxeID + 0, bLevel, (int)((float)bDur * wMod), bSpeed, bDam, bType, wType)).setItemName("wBoneAxe");
        stBoneAxe = (new InfiToolAxe(boneAxeID + 1, bLevel, (int)((float)bDur * stMod), bSpeed, bDam, bType, stType)).setItemName("stBoneAxe");
        iBoneAxe = (new InfiToolAxe(boneAxeID + 2, bLevel, (int)((float)bDur * iMod), bSpeed, bDam, bType, iType)).setItemName("iBoneAxe");
        dBoneAxe = (new InfiToolAxe(boneAxeID + 3, bLevel, (int)((float)bDur * iMod), bSpeed, bDam, bType, dType)).setItemName("dBoneAxe");
        rBoneAxe = (new InfiToolAxe(boneAxeID + 4, bLevel, (int)((float)bDur * rMod), bSpeed, bDam, bType, rType)).setItemName("rBoneAxe");
        oBoneAxe = (new InfiToolAxe(boneAxeID + 5, bLevel, (int)((float)bDur * oMod), bSpeed, bDam, bType, oType)).setItemName("oBoneAxe");
        bBoneAxe = (new InfiToolAxe(boneAxeID + 6, bLevel, (int)((float)bDur * bMod), bSpeed, bDam, bType, bType)).setItemName("bBoneAxe");
        mBoneAxe = (new InfiToolAxe(boneAxeID + 7, bLevel, (int)((float)bDur * mMod), bSpeed, bDam, bType, mType)).setItemName("mBoneAxe");
        nBoneAxe = (new InfiToolAxe(boneAxeID + 8, bLevel, (int)((float)bDur * nMod), bSpeed, bDam, bType, nType)).setItemName("nBoneAxe");
        glBoneAxe = (new InfiToolAxe(boneAxeID + 9, bLevel, (int)((float)bDur * gMod), bSpeed, bDam, bType, glType)).setItemName("glBoneAxe");
        sBoneAxe = (new InfiToolAxe(boneAxeID + 10, bLevel, (int)((float)bDur * sMod), bSpeed, bDam, bType, sType)).setItemName("sBoneAxe");
        cBoneAxe = (new InfiToolAxe(boneAxeID + 11, bLevel, (int)((float)bDur * cMod), bSpeed, bDam, bType, cType)).setItemName("cBoneAxe");
        fBoneAxe = (new InfiToolAxe(boneAxeID + 12, bLevel, (int)((float)bDur * fMod), bSpeed, bDam, bType, fType)).setItemName("fBoneAxe");
        brBoneAxe = (new InfiToolAxe(boneAxeID + 13, bLevel, (int)((float)bDur * brMod), bSpeed, bDam, bType, brType)).setItemName("brBoneAxe");
        blBoneAxe = (new InfiToolAxe(boneAxeID + 14, bLevel, (int)((float)bDur * blMod), bSpeed, bDam, bType, blType)).setItemName("blBoneAxe");
        wPaperAxe = (new InfiToolAxe(paperAxeID + 0, pLevel, (int)((float)pDur * wMod), pSpeed, pDam, pType, wType)).setItemName("wPaperAxe");
        saPaperAxe = (new InfiToolAxe(paperAxeID + 1, pLevel, (int)((float)pDur * saMod), pSpeed, pDam, pType, saType)).setItemName("saPaperAxe");
        bPaperAxe = (new InfiToolAxe(paperAxeID + 2, pLevel, (int)((float)pDur * bMod), pSpeed, pDam, pType, bType)).setItemName("bPaperAxe");
        pPaperAxe = (new InfiToolAxe(paperAxeID + 3, pLevel, (int)((float)pDur * pMod), pSpeed, pDam, pType, pType)).setItemName("pPaperAxe");
        sPaperAxe = (new InfiToolAxe(paperAxeID + 4, pLevel, (int)((float)pDur * sMod), pSpeed, pDam, pType, sType)).setItemName("sPaperAxe");
        cPaperAxe = (new InfiToolAxe(paperAxeID + 5, pLevel, (int)((float)pDur * cMod), pSpeed, pDam, pType, cType)).setItemName("cPaperAxe");
        brPaperAxe = (new InfiToolAxe(paperAxeID + 6, pLevel, (int)((float)pDur * brMod), pSpeed, pDam, pType, brType)).setItemName("brPaperAxe");
        stMossyAxe = (new InfiToolAxe(mossyAxeID + 0, mLevel, (int)((float)mDur * stMod), mSpeed, mDam, mType, stType)).setItemName("stMossyAxe");
        dMossyAxe = (new InfiToolAxe(mossyAxeID + 1, mLevel, (int)((float)mDur * iMod), mSpeed, mDam, mType, dType)).setItemName("dMossyAxe");
        rMossyAxe = (new InfiToolAxe(mossyAxeID + 2, mLevel, (int)((float)mDur * rMod), mSpeed, mDam, mType, rType)).setItemName("rMossyAxe");
        bMossyAxe = (new InfiToolAxe(mossyAxeID + 3, mLevel, (int)((float)mDur * bMod), mSpeed, mDam, mType, bType)).setItemName("bMossyAxe");
        mMossyAxe = (new InfiToolAxe(mossyAxeID + 4, mLevel, (int)((float)mDur * mMod), mSpeed, mDam, mType, mType)).setItemName("mMossyAxe");
        glMossyAxe = (new InfiToolAxe(mossyAxeID + 5, mLevel, (int)((float)mDur * gMod), mSpeed, mDam, mType, glType)).setItemName("glMossyAxe");
        wNetherrackAxe = (new InfiToolAxe(netherrackAxeID + 0, nLevel, (int)((float)nDur * wMod), nSpeed, nDam, nType, wType)).setItemName("wNetherrackAxe");
        stNetherrackAxe = (new InfiToolAxe(netherrackAxeID + 1, nLevel, (int)((float)nDur * stMod), nSpeed, nDam, nType, stType)).setItemName("stNetherrackAxe");
        iNetherrackAxe = (new InfiToolAxe(netherrackAxeID + 2, nLevel, (int)((float)nDur * iMod), nSpeed, nDam, nType, iType)).setItemName("iNetherrackAxe");
        rNetherrackAxe = (new InfiToolAxe(netherrackAxeID + 3, nLevel, (int)((float)nDur * rMod), nSpeed, nDam, nType, rType)).setItemName("rNetherrackAxe");
        oNetherrackAxe = (new InfiToolAxe(netherrackAxeID + 4, nLevel, (int)((float)nDur * oMod), nSpeed, nDam, nType, oType)).setItemName("oNetherrackAxe");
        saNetherrackAxe = (new InfiToolAxe(netherrackAxeID + 5, nLevel, (int)((float)nDur * saMod), nSpeed, nDam, nType, saType)).setItemName("saNetherrackAxe");
        bNetherrackAxe = (new InfiToolAxe(netherrackAxeID + 6, nLevel, (int)((float)nDur * bMod), nSpeed, nDam, nType, bType)).setItemName("bNetherrackAxe");
        mNetherrackAxe = (new InfiToolAxe(netherrackAxeID + 7, nLevel, (int)((float)nDur * mMod), nSpeed, nDam, nType, mType)).setItemName("mNetherrackAxe");
        nNetherrackAxe = (new InfiToolAxe(netherrackAxeID + 8, nLevel, (int)((float)nDur * nMod), nSpeed, nDam, nType, nType)).setItemName("nNetherrackAxe");
        glNetherrackAxe = (new InfiToolAxe(netherrackAxeID + 9, nLevel, (int)((float)nDur * glMod), nSpeed, nDam, nType, glType)).setItemName("glNetherrackAxe");
        iceNetherrackAxe = (new InfiToolAxe(netherrackAxeID + 10, nLevel, (int)((float)nDur * iceMod), nSpeed, nDam, nType, iceType)).setItemName("iceNetherrackAxe");
        sNetherrackAxe = (new InfiToolAxe(netherrackAxeID + 11, nLevel, (int)((float)nDur * sMod), nSpeed, nDam, nType, sType)).setItemName("sNetherrackAxe");
        cNetherrackAxe = (new InfiToolAxe(netherrackAxeID + 12, nLevel, (int)((float)nDur * cMod), nSpeed, nDam, nType, cType)).setItemName("cNetherrackAxe");
        fNetherrackAxe = (new InfiToolAxe(netherrackAxeID + 13, nLevel, (int)((float)nDur * fMod), nSpeed, nDam, nType, fType)).setItemName("fNetherrackAxe");
        brNetherrackAxe = (new InfiToolAxe(netherrackAxeID + 14, nLevel, (int)((float)nDur * brMod), nSpeed, nDam, nType, brType)).setItemName("brNetherrackAxe");
        blNetherrackAxe = (new InfiToolAxe(netherrackAxeID + 15, nLevel, (int)((float)nDur * blMod), nSpeed, nDam, nType, blType)).setItemName("blNetherrackAxe");
        wGlowstoneAxe = (new InfiToolAxe(glowstoneAxeID + 0, glLevel, (int)((float)glDur * wMod), glSpeed, glDam, glType, wType)).setItemName("wGlowstoneAxe");
        stGlowstoneAxe = (new InfiToolAxe(glowstoneAxeID + 1, glLevel, (int)((float)glDur * stMod), glSpeed, glDam, glType, stType)).setItemName("stGlowstoneAxe");
        iGlowstoneAxe = (new InfiToolAxe(glowstoneAxeID + 2, glLevel, (int)((float)glDur * iMod), glSpeed, glDam, glType, iType)).setItemName("iGlowstoneAxe");
        dGlowstoneAxe = (new InfiToolAxe(glowstoneAxeID + 3, glLevel, (int)((float)glDur * iMod), glSpeed, glDam, glType, dType)).setItemName("dGlowstoneAxe");
        rGlowstoneAxe = (new InfiToolAxe(glowstoneAxeID + 4, glLevel, (int)((float)glDur * rMod), glSpeed, glDam, glType, rType)).setItemName("rGlowstoneAxe");
        oGlowstoneAxe = (new InfiToolAxe(glowstoneAxeID + 5, glLevel, (int)((float)glDur * oMod), glSpeed, glDam, glType, oType)).setItemName("oGlowstoneAxe");
        bGlowstoneAxe = (new InfiToolAxe(glowstoneAxeID + 6, glLevel, (int)((float)glDur * bMod), glSpeed, glDam, glType, bType)).setItemName("bGlowstoneAxe");
        mGlowstoneAxe = (new InfiToolAxe(glowstoneAxeID + 7, glLevel, (int)((float)glDur * mMod), glSpeed, glDam, glType, mType)).setItemName("mGlowstoneAxe");
        nGlowstoneAxe = (new InfiToolAxe(glowstoneAxeID + 8, glLevel, (int)((float)glDur * nMod), glSpeed, glDam, glType, nType)).setItemName("nGlowstoneAxe");
        glGlowstoneAxe = (new InfiToolAxe(glowstoneAxeID + 9, glLevel, (int)((float)glDur * gMod), glSpeed, glDam, glType, glType)).setItemName("glGlowstoneAxe");
        iceGlowstoneAxe = (new InfiToolAxe(glowstoneAxeID + 10, glLevel, (int)((float)glDur * iceMod), glSpeed, glDam, glType, iceType)).setItemName("iceGlowstoneAxe");
        lGlowstoneAxe = (new InfiToolAxe(glowstoneAxeID + 11, glLevel, (int)((float)glDur * gMod), glSpeed, glDam, glType, lType)).setItemName("lGlowstoneAxe");
        sGlowstoneAxe = (new InfiToolAxe(glowstoneAxeID + 12, glLevel, (int)((float)glDur * sMod), glSpeed, glDam, glType, sType)).setItemName("sGlowstoneAxe");
        blGlowstoneAxe = (new InfiToolAxe(glowstoneAxeID + 13, glLevel, (int)((float)glDur * blMod), glSpeed, glDam, glType, blType)).setItemName("blGlowstoneAxe");
        wIceAxe = (new InfiToolAxe(iceAxeID + 0, iceLevel, (int)((float)iceDur * wMod), iceSpeed, iceDam, iceType, wType)).setItemName("wIceAxe");
        stIceAxe = (new InfiToolAxe(iceAxeID + 1, iceLevel, (int)((float)iceDur * stMod), iceSpeed, iceDam, iceType, stType)).setItemName("stIceAxe");
        iIceAxe = (new InfiToolAxe(iceAxeID + 2, iceLevel, (int)((float)iceDur * iMod), iceSpeed, iceDam, iceType, iType)).setItemName("iIceAxe");
        dIceAxe = (new InfiToolAxe(iceAxeID + 3, iceLevel, (int)((float)iceDur * dMod), iceSpeed, iceDam, iceType, dType)).setItemName("dIceAxe");
        gIceAxe = (new InfiToolAxe(iceAxeID + 4, iceLevel, (int)((float)iceDur * gMod), iceSpeed, iceDam, iceType, gType)).setItemName("gIceAxe");
        rIceAxe = (new InfiToolAxe(iceAxeID + 5, iceLevel, (int)((float)iceDur * rMod), iceSpeed, iceDam, iceType, rType)).setItemName("rIceAxe");
        oIceAxe = (new InfiToolAxe(iceAxeID + 6, iceLevel, (int)((float)iceDur * oMod), iceSpeed, iceDam, iceType, oType)).setItemName("oIceAxe");
        saIceAxe = (new InfiToolAxe(iceAxeID + 7, iceLevel, (int)((float)iceDur * saMod), iceSpeed, iceDam, iceType, saType)).setItemName("saIceAxe");
        bIceAxe = (new InfiToolAxe(iceAxeID + 8, iceLevel, (int)((float)iceDur * bMod), iceSpeed, iceDam, iceType, bType)).setItemName("bIceAxe");
        glIceAxe = (new InfiToolAxe(iceAxeID + 9, iceLevel, (int)((float)iceDur * gMod), iceSpeed, iceDam, iceType, glType)).setItemName("glIceAxe");
        iceIceAxe = (new InfiToolAxe(iceAxeID + 10, iceLevel, (int)((float)iceDur * iMod), iceSpeed, iceDam, iceType, iceType)).setItemName("iceIceAxe");
        sIceAxe = (new InfiToolAxe(iceAxeID + 11, iceLevel, (int)((float)iceDur * sMod), iceSpeed, iceDam, iceType, sType)).setItemName("sIceAxe");
        cIceAxe = (new InfiToolAxe(iceAxeID + 12, iceLevel, (int)((float)iceDur * cMod), iceSpeed, iceDam, iceType, cType)).setItemName("cIceAxe");
        fIceAxe = (new InfiToolAxe(iceAxeID + 13, iceLevel, (int)((float)iceDur * fMod), iceSpeed, iceDam, iceType, fType)).setItemName("fIceAxe");
        brIceAxe = (new InfiToolAxe(iceAxeID + 14, iceLevel, (int)((float)iceDur * brMod), iceSpeed, iceDam, iceType, brType)).setItemName("brIceAxe");
        dLavaAxe = (new InfiToolAxe(lavaAxeID + 0, lLevel, (int)((float)lDur * iMod), lSpeed, lDam, lType, dType)).setItemName("dLavaAxe");
        rLavaAxe = (new InfiToolAxe(lavaAxeID + 1, lLevel, (int)((float)lDur * rMod), lSpeed, lDam, lType, rType)).setItemName("rLavaAxe");
        bLavaAxe = (new InfiToolAxe(lavaAxeID + 2, lLevel, (int)((float)lDur * bMod), lSpeed, lDam, lType, bType)).setItemName("bLavaAxe");
        nLavaAxe = (new InfiToolAxe(lavaAxeID + 3, lLevel, (int)((float)lDur * nMod), lSpeed, lDam, lType, nType)).setItemName("nLavaAxe");
        glLavaAxe = (new InfiToolAxe(lavaAxeID + 4, lLevel, (int)((float)lDur * gMod), lSpeed, lDam, lType, glType)).setItemName("glLavaAxe");
        lLavaAxe = (new InfiToolAxe(lavaAxeID + 5, lLevel, (int)((float)lDur * lMod), lSpeed, lDam, lType, lType)).setItemName("lLavaAxe");
        blLavaAxe = (new InfiToolAxe(lavaAxeID + 6, lLevel, (int)((float)lDur * blMod), lSpeed, lDam, lType, blType)).setItemName("blLavaAxe");
        wSlimeAxe = (new InfiToolAxe(slimeAxeID + 0, sLevel, (int)((float)sDur * wMod), sSpeed, sDam, sType, wType)).setItemName("wSlimeAxe");
        stSlimeAxe = (new InfiToolAxe(slimeAxeID + 1, sLevel, (int)((float)sDur * stMod), sSpeed, sDam, sType, stType)).setItemName("stSlimeAxe");
        iSlimeAxe = (new InfiToolAxe(slimeAxeID + 2, sLevel, (int)((float)sDur * iMod), sSpeed, sDam, sType, iType)).setItemName("iSlimeAxe");
        dSlimeAxe = (new InfiToolAxe(slimeAxeID + 3, sLevel, (int)((float)sDur * iMod), sSpeed, sDam, sType, dType)).setItemName("dSlimeAxe");
        gSlimeAxe = (new InfiToolAxe(slimeAxeID + 4, sLevel, (int)((float)sDur * gMod), sSpeed, sDam, sType, gType)).setItemName("gSlimeAxe");
        rSlimeAxe = (new InfiToolAxe(slimeAxeID + 5, sLevel, (int)((float)sDur * rMod), sSpeed, sDam, sType, rType)).setItemName("rSlimeAxe");
        oSlimeAxe = (new InfiToolAxe(slimeAxeID + 6, sLevel, (int)((float)sDur * oMod), sSpeed, sDam, sType, oType)).setItemName("oSlimeAxe");
        saSlimeAxe = (new InfiToolAxe(slimeAxeID + 7, sLevel, (int)((float)sDur * saMod), sSpeed, sDam, sType, saType)).setItemName("saSlimeAxe");
        bSlimeAxe = (new InfiToolAxe(slimeAxeID + 8, sLevel, (int)((float)sDur * bMod), sSpeed, sDam, sType, bType)).setItemName("bSlimeAxe");
        pSlimeAxe = (new InfiToolAxe(slimeAxeID + 9, sLevel, (int)((float)sDur * pMod), sSpeed, sDam, sType, pType)).setItemName("pSlimeAxe");
        mSlimeAxe = (new InfiToolAxe(slimeAxeID + 10, sLevel, (int)((float)sDur * mMod), sSpeed, sDam, sType, mType)).setItemName("mSlimeAxe");
        nSlimeAxe = (new InfiToolAxe(slimeAxeID + 11, sLevel, (int)((float)sDur * nMod), sSpeed, sDam, sType, nType)).setItemName("nSlimeAxe");
        glSlimeAxe = (new InfiToolAxe(slimeAxeID + 12, sLevel, (int)((float)sDur * gMod), sSpeed, sDam, sType, glType)).setItemName("glSlimeAxe");
        iceSlimeAxe = (new InfiToolAxe(slimeAxeID + 13, sLevel, (int)((float)sDur * iMod), sSpeed, sDam, sType, iceType)).setItemName("iceSlimeAxe");
        lSlimeAxe = (new InfiToolAxe(slimeAxeID + 14, sLevel, (int)((float)sDur * lMod), sSpeed, sDam, sType, lType)).setItemName("lSlimeAxe");
        sSlimeAxe = (new InfiToolAxe(slimeAxeID + 15, sLevel, (int)((float)sDur * sMod), sSpeed, sDam, sType, sType)).setItemName("sSlimeAxe");
        cSlimeAxe = (new InfiToolAxe(slimeAxeID + 16, sLevel, (int)((float)sDur * cMod), sSpeed, sDam, sType, cType)).setItemName("cSlimeAxe");
        fSlimeAxe = (new InfiToolAxe(slimeAxeID + 17, sLevel, (int)((float)sDur * fMod), sSpeed, sDam, sType, fType)).setItemName("fSlimeAxe");
        brSlimeAxe = (new InfiToolAxe(slimeAxeID + 18, sLevel, (int)((float)sDur * brMod), sSpeed, sDam, sType, brType)).setItemName("brSlimeAxe");
        blSlimeAxe = (new InfiToolAxe(slimeAxeID + 19, sLevel, (int)((float)sDur * blMod), sSpeed, sDam, sType, blType)).setItemName("blSlimeAxe");
        wCactusAxe = (new InfiToolAxe(cactusAxeID + 0, cLevel, (int)((float)cDur * wMod), cSpeed, cDam, cType, wType)).setItemName("wCactusAxe");
        stCactusAxe = (new InfiToolAxe(cactusAxeID + 1, cLevel, (int)((float)cDur * stMod), cSpeed, cDam, cType, stType)).setItemName("stCactusAxe");
        saCactusAxe = (new InfiToolAxe(cactusAxeID + 2, cLevel, (int)((float)cDur * saMod), cSpeed, cDam, cType, saType)).setItemName("saCactusAxe");
        bCactusAxe = (new InfiToolAxe(cactusAxeID + 3, cLevel, (int)((float)cDur * bMod), cSpeed, cDam, cType, bType)).setItemName("bCactusAxe");
        pCactusAxe = (new InfiToolAxe(cactusAxeID + 4, cLevel, (int)((float)cDur * pMod), cSpeed, cDam, cType, pType)).setItemName("pCactusAxe");
        nCactusAxe = (new InfiToolAxe(cactusAxeID + 5, cLevel, (int)((float)cDur * nMod), cSpeed, cDam, cType, nType)).setItemName("nCactusAxe");
        sCactusAxe = (new InfiToolAxe(cactusAxeID + 6, cLevel, (int)((float)cDur * sMod), cSpeed, cDam, cType, sType)).setItemName("sCactusAxe");
        cCactusAxe = (new InfiToolAxe(cactusAxeID + 7, cLevel, (int)((float)cDur * cMod), cSpeed, cDam, cType, cType)).setItemName("cCactusAxe");
        fCactusAxe = (new InfiToolAxe(cactusAxeID + 8, cLevel, (int)((float)cDur * fMod), cSpeed, cDam, cType, fType)).setItemName("fCactusAxe");
        brCactusAxe = (new InfiToolAxe(cactusAxeID + 9, cLevel, (int)((float)cDur * brMod), cSpeed, cDam, cType, brType)).setItemName("brCactusAxe");
        wFlintAxe = (new InfiToolAxe(flintAxeID + 0, fLevel, (int)((float)fDur * wMod), fSpeed, fDam, fType, wType)).setItemName("wFlintAxe");
        stFlintAxe = (new InfiToolAxe(flintAxeID + 1, fLevel, (int)((float)fDur * stMod), fSpeed, fDam, fType, stType)).setItemName("stFlintAxe");
        iFlintAxe = (new InfiToolAxe(flintAxeID + 2, fLevel, (int)((float)fDur * iMod), fSpeed, fDam, fType, iType)).setItemName("iFlintAxe");
        gFlintAxe = (new InfiToolAxe(flintAxeID + 3, fLevel, (int)((float)fDur * gMod), fSpeed, fDam, fType, gType)).setItemName("gFlintAxe");
        oFlintAxe = (new InfiToolAxe(flintAxeID + 4, fLevel, (int)((float)fDur * oMod), fSpeed, fDam, fType, oType)).setItemName("oFlintAxe");
        saFlintAxe = (new InfiToolAxe(flintAxeID + 5, fLevel, (int)((float)fDur * saMod), fSpeed, fDam, fType, saType)).setItemName("saFlintAxe");
        bFlintAxe = (new InfiToolAxe(flintAxeID + 6, fLevel, (int)((float)fDur * bMod), fSpeed, fDam, fType, bType)).setItemName("bFlintAxe");
        nFlintAxe = (new InfiToolAxe(flintAxeID + 7, fLevel, (int)((float)fDur * nMod), fSpeed, fDam, fType, nType)).setItemName("nFlintAxe");
        iceFlintAxe = (new InfiToolAxe(flintAxeID + 8, fLevel, (int)((float)fDur * wMod), fSpeed, fDam, fType, iceType)).setItemName("iceFlintAxe");
        sFlintAxe = (new InfiToolAxe(flintAxeID + 9, fLevel, (int)((float)fDur * sMod), fSpeed, fDam, fType, sType)).setItemName("sFlintAxe");
        cFlintAxe = (new InfiToolAxe(flintAxeID + 10, fLevel, (int)((float)fDur * cMod), fSpeed, fDam, fType, cType)).setItemName("cFlintAxe");
        fFlintAxe = (new InfiToolAxe(flintAxeID + 11, fLevel, (int)((float)fDur * fMod), fSpeed, fDam, fType, fType)).setItemName("fFlintAxe");
        brFlintAxe = (new InfiToolAxe(flintAxeID + 12, fLevel, (int)((float)fDur * brMod), fSpeed, fDam, fType, brType)).setItemName("brFlintAxe");
        blFlintAxe = (new InfiToolAxe(flintAxeID + 13, fLevel, (int)((float)fDur * blMod), fSpeed, fDam, fType, blType)).setItemName("blFlintAxe");
        wBrickAxe = (new InfiToolAxe(brickAxeID + 0, brLevel, (int)((float)brDur * wMod), brSpeed, brDam, brType, wType)).setItemName("wBrickAxe");
        stBrickAxe = (new InfiToolAxe(brickAxeID + 1, brLevel, (int)((float)brDur * stMod), brSpeed, brDam, brType, stType)).setItemName("stBrickAxe");
        saBrickAxe = (new InfiToolAxe(brickAxeID + 2, brLevel, (int)((float)brDur * saMod), brSpeed, brDam, brType, saType)).setItemName("saBrickAxe");
        bBrickAxe = (new InfiToolAxe(brickAxeID + 3, brLevel, (int)((float)brDur * bMod), brSpeed, brDam, brType, bType)).setItemName("bBrickAxe");
        pBrickAxe = (new InfiToolAxe(brickAxeID + 4, brLevel, (int)((float)brDur * pMod), brSpeed, brDam, brType, pType)).setItemName("pBrickAxe");
        nBrickAxe = (new InfiToolAxe(brickAxeID + 5, brLevel, (int)((float)brDur * nMod), brSpeed, brDam, brType, nType)).setItemName("nBrickAxe");
        iceBrickAxe = (new InfiToolAxe(brickAxeID + 6, brLevel, (int)((float)brDur * iceMod), brSpeed, brDam, brType, iceType)).setItemName("iceBrickAxe");
        sBrickAxe = (new InfiToolAxe(brickAxeID + 7, brLevel, (int)((float)brDur * sMod), brSpeed, brDam, brType, sType)).setItemName("sBrickAxe");
        cBrickAxe = (new InfiToolAxe(brickAxeID + 8, brLevel, (int)((float)brDur * cMod), brSpeed, brDam, brType, cType)).setItemName("cBrickAxe");
        fBrickAxe = (new InfiToolAxe(brickAxeID + 9, brLevel, (int)((float)brDur * fMod), brSpeed, brDam, brType, fType)).setItemName("fBrickAxe");
        brBrickAxe = (new InfiToolAxe(brickAxeID + 10, brLevel, (int)((float)brDur * brMod), brSpeed, brDam, brType, brType)).setItemName("brBrickAxe");
        dBlazeAxe = (new InfiToolAxe(blazeAxeID + 0, blLevel, (int)((float)blDur * dMod), blSpeed, blDam, blType, dType)).setItemName("wBlazeAxe");
        rBlazeAxe = (new InfiToolAxe(blazeAxeID + 1, blLevel, (int)((float)blDur * rMod), blSpeed, blDam, blType, rType)).setItemName("rBlazeAxe");
        bBlazeAxe = (new InfiToolAxe(blazeAxeID + 2, blLevel, (int)((float)blDur * bMod), blSpeed, blDam, blType, bType)).setItemName("bBlazeAxe");
        nBlazeAxe = (new InfiToolAxe(blazeAxeID + 3, blLevel, (int)((float)blDur * nMod), blSpeed, blDam, blType, nType)).setItemName("nBlazeAxe");
        glBlazeAxe = (new InfiToolAxe(blazeAxeID + 4, blLevel, (int)((float)blDur * glMod), blSpeed, blDam, blType, glType)).setItemName("glBlazeAxe");
        lBlazeAxe = (new InfiToolAxe(blazeAxeID + 5, blLevel, (int)((float)blDur * lMod), blSpeed, blDam, blType, lType)).setItemName("lBlazeAxe");
        fBlazeAxe = (new InfiToolAxe(blazeAxeID + 6, blLevel, (int)((float)blDur * fMod), blSpeed, blDam, blType, fType)).setItemName("fBlazeAxe");
        blBlazeAxe = (new InfiToolAxe(blazeAxeID + 7, blLevel, (int)((float)blDur * bMod), blSpeed, blDam, blType, bType)).setItemName("blBlazeAxe");
        stWoodHoe = (new InfiToolHoe(woodHoeID + 0, (int)((float)wDur * stMod), wDam, wType, stType)).setItemName("stWoodHoe");
        saWoodHoe = (new InfiToolHoe(woodHoeID + 1, (int)((float)wDur * saMod), wDam, wType, saType)).setItemName("saWoodHoe");
        bWoodHoe = (new InfiToolHoe(woodHoeID + 2, (int)((float)wDur * bMod), wDam, wType, bType)).setItemName("bWoodHoe");
        pWoodHoe = (new InfiToolHoe(woodHoeID + 3, (int)((float)wDur * pMod), wDam, wType, pType)).setItemName("pWoodHoe");
        nWoodHoe = (new InfiToolHoe(woodHoeID + 4, (int)((float)wDur * nMod), wDam, wType, nType)).setItemName("nWoodHoe");
        sWoodHoe = (new InfiToolHoe(woodHoeID + 5, (int)((float)wDur * sMod), wDam, wType, sType)).setItemName("sWoodHoe");
        cWoodHoe = (new InfiToolHoe(woodHoeID + 6, (int)((float)wDur * cMod), wDam, wType, cType)).setItemName("cWoodHoe");
        fWoodHoe = (new InfiToolHoe(woodHoeID + 7, (int)((float)wDur * fMod), wDam, wType, fType)).setItemName("fWoodHoe");
        brWoodHoe = (new InfiToolHoe(woodHoeID + 8, (int)((float)wDur * brMod), wDam, wType, brType)).setItemName("brWoodHoe");
        stStoneHoe = (new InfiToolHoe(stoneHoeID + 0, (int)((float)stDur * stMod), stDam, stType, stType)).setItemName("stStoneHoe");
        saStoneHoe = (new InfiToolHoe(stoneHoeID + 1, (int)((float)stDur * saMod), stDam, stType, saType)).setItemName("saStoneHoe");
        bStoneHoe = (new InfiToolHoe(stoneHoeID + 2, (int)((float)stDur * bMod), stDam, stType, bType)).setItemName("bStoneHoe");
        pStoneHoe = (new InfiToolHoe(stoneHoeID + 3, (int)((float)stDur * pMod), stDam, stType, pType)).setItemName("pStoneHoe");
        mStoneHoe = (new InfiToolHoe(stoneHoeID + 4, (int)((float)stDur * mMod), stDam, stType, mType)).setItemName("mStoneHoe");
        nStoneHoe = (new InfiToolHoe(stoneHoeID + 5, (int)((float)stDur * nMod), stDam, stType, nType)).setItemName("nStoneHoe");
        iceStoneHoe = (new InfiToolHoe(stoneHoeID + 6, (int)((float)stDur * iceMod), stDam, stType, iceType)).setItemName("iceStoneHoe");
        sStoneHoe = (new InfiToolHoe(stoneHoeID + 7, (int)((float)stDur * sMod), stDam, stType, sType)).setItemName("sStoneHoe");
        cStoneHoe = (new InfiToolHoe(stoneHoeID + 8, (int)((float)stDur * cMod), stDam, stType, cType)).setItemName("cStoneHoe");
        fStoneHoe = (new InfiToolHoe(stoneHoeID + 9, (int)((float)stDur * fMod), stDam, stType, fType)).setItemName("fStoneHoe");
        brStoneHoe = (new InfiToolHoe(stoneHoeID + 10, (int)((float)stDur * brMod), stDam, stType, brType)).setItemName("brStoneHoe");
        stIronHoe = (new InfiToolHoe(ironHoeID + 0, (int)((float)iDur * stMod), iDam, iType, stType)).setItemName("stIronHoe");
        iIronHoe = (new InfiToolHoe(ironHoeID + 1, (int)((float)iDur * iMod), iDam, iType, iType)).setItemName("iIronHoe");
        dIronHoe = (new InfiToolHoe(ironHoeID + 2, (int)((float)iDur * dMod), iDam, iType, dType)).setItemName("dIronHoe");
        gIronHoe = (new InfiToolHoe(ironHoeID + 3, (int)((float)iDur * gMod), iDam, iType, gType)).setItemName("gIronHoe");
        rIronHoe = (new InfiToolHoe(ironHoeID + 4, (int)((float)iDur * rMod), iDam, iType, rType)).setItemName("rIronHoe");
        oIronHoe = (new InfiToolHoe(ironHoeID + 5, (int)((float)iDur * oMod), iDam, iType, oType)).setItemName("oIronHoe");
        bIronHoe = (new InfiToolHoe(ironHoeID + 6, (int)((float)iDur * bMod), iDam, iType, bType)).setItemName("bIronHoe");
        nIronHoe = (new InfiToolHoe(ironHoeID + 7, (int)((float)iDur * nMod), iDam, iType, nType)).setItemName("nIronHoe");
        glIronHoe = (new InfiToolHoe(ironHoeID + 8, (int)((float)iDur * glMod), iDam, iType, glType)).setItemName("glIronHoe");
        iceIronHoe = (new InfiToolHoe(ironHoeID + 9, (int)((float)iDur * iceMod), iDam, iType, iceType)).setItemName("iceIronHoe");
        sIronHoe = (new InfiToolHoe(ironHoeID + 10, (int)((float)iDur * sMod), iDam, iType, sType)).setItemName("sIronHoe");
        blIronHoe = (new InfiToolHoe(ironHoeID + 11, (int)((float)iDur * blMod), iDam, iType, blType)).setItemName("blIronHoe");
        stDiamondHoe = (new InfiToolHoe(diamondHoeID + 0, (int)((float)dDur * stMod), dDam, dType, stType)).setItemName("stDiamondHoe");
        iDiamondHoe = (new InfiToolHoe(diamondHoeID + 1, (int)((float)dDur * iMod), dDam, dType, iType)).setItemName("iDiamondHoe");
        dDiamondHoe = (new InfiToolHoe(diamondHoeID + 2, (int)((float)dDur * dMod), dDam, dType, dType)).setItemName("dDiamondHoe");
        gDiamondHoe = (new InfiToolHoe(diamondHoeID + 3, (int)((float)dDur * gMod), dDam, dType, gType)).setItemName("gDiamondHoe");
        rDiamondHoe = (new InfiToolHoe(diamondHoeID + 4, (int)((float)dDur * rMod), dDam, dType, rType)).setItemName("rDiamondHoe");
        oDiamondHoe = (new InfiToolHoe(diamondHoeID + 5, (int)((float)dDur * oMod), dDam, dType, oType)).setItemName("oDiamondHoe");
        bDiamondHoe = (new InfiToolHoe(diamondHoeID + 6, (int)((float)dDur * bMod), dDam, dType, bType)).setItemName("bDiamondHoe");
        mDiamondHoe = (new InfiToolHoe(diamondHoeID + 7, (int)((float)dDur * mMod), dDam, dType, mType)).setItemName("mDiamondHoe");
        nDiamondHoe = (new InfiToolHoe(diamondHoeID + 8, (int)((float)dDur * nMod), dDam, dType, nType)).setItemName("nDiamondHoe");
        glDiamondHoe = (new InfiToolHoe(diamondHoeID + 9, (int)((float)dDur * glMod), dDam, dType, glType)).setItemName("glDiamondHoe");
        blDiamondHoe = (new InfiToolHoe(diamondHoeID + 10, (int)((float)dDur * blMod), dDam, dType, blType)).setItemName("blDiamondHoe");
        stGoldHoe = (new InfiToolHoe(goldHoeID + 0, (int)((float)gDur * stMod), gDam, gType, stType)).setItemName("stGoldHoe");
        gGoldHoe = (new InfiToolHoe(goldHoeID + 1, (int)((float)gDur * gMod), gDam, gType, gType)).setItemName("gGoldHoe");
        oGoldHoe = (new InfiToolHoe(goldHoeID + 2, (int)((float)gDur * oMod), gDam, gType, oType)).setItemName("oGoldHoe");
        saGoldHoe = (new InfiToolHoe(goldHoeID + 3, (int)((float)gDur * saMod), gDam, gType, saType)).setItemName("saGoldHoe");
        bGoldHoe = (new InfiToolHoe(goldHoeID + 4, (int)((float)gDur * bMod), gDam, gType, bType)).setItemName("bGoldHoe");
        mGoldHoe = (new InfiToolHoe(goldHoeID + 5, (int)((float)gDur * mMod), gDam, gType, mType)).setItemName("mGoldHoe");
        nGoldHoe = (new InfiToolHoe(goldHoeID + 6, (int)((float)gDur * nMod), gDam, gType, nType)).setItemName("nGoldHoe");
        glGoldHoe = (new InfiToolHoe(goldHoeID + 7, (int)((float)gDur * glMod), gDam, gType, glType)).setItemName("glGoldHoe");
        iceGoldHoe = (new InfiToolHoe(goldHoeID + 8, (int)((float)gDur * iceMod), gDam, gType, iceType)).setItemName("iceGoldHoe");
        sGoldHoe = (new InfiToolHoe(goldHoeID + 9, (int)((float)gDur * sMod), gDam, gType, sType)).setItemName("sGoldHoe");
        fGoldHoe = (new InfiToolHoe(goldHoeID + 10, (int)((float)gDur * fMod), gDam, gType, fType)).setItemName("fGoldHoe");
        wRedstoneHoe = (new InfiToolHoe(redstoneHoeID + 0, (int)((float)rDur * wMod), rDam, rType, wType)).setItemName("wRedstoneHoe");
        stRedstoneHoe = (new InfiToolHoe(redstoneHoeID + 1, (int)((float)rDur * stMod), rDam, rType, stType)).setItemName("stRedstoneHoe");
        iRedstoneHoe = (new InfiToolHoe(redstoneHoeID + 2, (int)((float)rDur * iMod), rDam, rType, iType)).setItemName("iRedstoneHoe");
        dRedstoneHoe = (new InfiToolHoe(redstoneHoeID + 3, (int)((float)rDur * dMod), rDam, rType, dType)).setItemName("dRedstoneHoe");
        rRedstoneHoe = (new InfiToolHoe(redstoneHoeID + 4, (int)((float)rDur * rMod), rDam, rType, rType)).setItemName("rRedstoneHoe");
        oRedstoneHoe = (new InfiToolHoe(redstoneHoeID + 5, (int)((float)rDur * oMod), rDam, rType, oType)).setItemName("oRedstoneHoe");
        bRedstoneHoe = (new InfiToolHoe(redstoneHoeID + 6, (int)((float)rDur * bMod), rDam, rType, bType)).setItemName("bRedstoneHoe");
        mRedstoneHoe = (new InfiToolHoe(redstoneHoeID + 7, (int)((float)rDur * mMod), rDam, rType, mType)).setItemName("mRedstoneHoe");
        glRedstoneHoe = (new InfiToolHoe(redstoneHoeID + 8, (int)((float)rDur * glMod), rDam, rType, glType)).setItemName("glRedstoneHoe");
        sRedstoneHoe = (new InfiToolHoe(redstoneHoeID + 9, (int)((float)rDur * sMod), rDam, rType, sType)).setItemName("sRedstoneHoe");
        blRedstoneHoe = (new InfiToolHoe(redstoneHoeID + 10, (int)((float)rDur * blMod), rDam, rType, blType)).setItemName("blRedstoneHoe");
        wObsidianHoe = (new InfiToolHoe(obsidianHoeID + 0, (int)((float)oDur * wMod), oDam, oType, wType)).setItemName("wObsidianHoe");
        stObsidianHoe = (new InfiToolHoe(obsidianHoeID + 1, (int)((float)oDur * stMod), oDam, oType, stType)).setItemName("stObsidianHoe");
        iObsidianHoe = (new InfiToolHoe(obsidianHoeID + 2, (int)((float)oDur * iMod), oDam, oType, iType)).setItemName("iObsidianHoe");
        dObsidianHoe = (new InfiToolHoe(obsidianHoeID + 3, (int)((float)oDur * dMod), oDam, oType, dType)).setItemName("dObsidianHoe");
        gObsidianHoe = (new InfiToolHoe(obsidianHoeID + 4, (int)((float)oDur * gMod), oDam, oType, gType)).setItemName("gObsidianHoe");
        rObsidianHoe = (new InfiToolHoe(obsidianHoeID + 5, (int)((float)oDur * rMod), oDam, oType, rType)).setItemName("rObsidianHoe");
        oObsidianHoe = (new InfiToolHoe(obsidianHoeID + 6, (int)((float)oDur * oMod), oDam, oType, oType)).setItemName("oObsidianHoe");
        bObsidianHoe = (new InfiToolHoe(obsidianHoeID + 7, (int)((float)oDur * bMod), oDam, oType, bType)).setItemName("bObsidianHoe");
        nObsidianHoe = (new InfiToolHoe(obsidianHoeID + 8, (int)((float)oDur * nMod), oDam, oType, nType)).setItemName("nObsidianHoe");
        glObsidianHoe = (new InfiToolHoe(obsidianHoeID + 9, (int)((float)oDur * glMod), oDam, oType, glType)).setItemName("glObsidianHoe");
        sObsidianHoe = (new InfiToolHoe(obsidianHoeID + 10, (int)((float)oDur * sMod), oDam, oType, sType)).setItemName("sObsidianHoe");
        fObsidianHoe = (new InfiToolHoe(obsidianHoeID + 11, (int)((float)oDur * fMod), oDam, oType, fType)).setItemName("fObsidianHoe");
        blObsidianHoe = (new InfiToolHoe(obsidianHoeID + 12, (int)((float)oDur * blMod), oDam, oType, blType)).setItemName("blObsidianHoe");
        wSandstoneHoe = (new InfiToolHoe(sandstoneHoeID + 0, (int)((float)saDur * wMod), saDam, saType, wType)).setItemName("wSandstoneHoe");
        stSandstoneHoe = (new InfiToolHoe(sandstoneHoeID + 1, (int)((float)saDur * stMod), saDam, saType, stType)).setItemName("stSandstoneHoe");
        saSandstoneHoe = (new InfiToolHoe(sandstoneHoeID + 2, (int)((float)saDur * saMod), saDam, saType, saType)).setItemName("saSandstoneHoe");
        bSandstoneHoe = (new InfiToolHoe(sandstoneHoeID + 3, (int)((float)saDur * bMod), saDam, saType, bType)).setItemName("bSandstoneHoe");
        pSandstoneHoe = (new InfiToolHoe(sandstoneHoeID + 4, (int)((float)saDur * pMod), saDam, saType, pType)).setItemName("pSandstoneHoe");
        nSandstoneHoe = (new InfiToolHoe(sandstoneHoeID + 5, (int)((float)saDur * nMod), saDam, saType, nType)).setItemName("nSandstoneHoe");
        iceSandstoneHoe = (new InfiToolHoe(sandstoneHoeID + 6, (int)((float)saDur * iceMod), saDam, saType, iceType)).setItemName("iceSandstoneHoe");
        sSandstoneHoe = (new InfiToolHoe(sandstoneHoeID + 7, (int)((float)saDur * sMod), saDam, saType, sType)).setItemName("sSandstoneHoe");
        cSandstoneHoe = (new InfiToolHoe(sandstoneHoeID + 8, (int)((float)saDur * cMod), saDam, saType, cType)).setItemName("cSandstoneHoe");
        fSandstoneHoe = (new InfiToolHoe(sandstoneHoeID + 9, (int)((float)saDur * fMod), saDam, saType, fType)).setItemName("fSandstoneHoe");
        brSandstoneHoe = (new InfiToolHoe(sandstoneHoeID + 10, (int)((float)saDur * brMod), saDam, saType, brType)).setItemName("brSandstoneHoe");
        wBoneHoe = (new InfiToolHoe(boneHoeID + 0, (int)((float)bDur * wMod), bDam, bType, wType)).setItemName("wBoneHoe");
        stBoneHoe = (new InfiToolHoe(boneHoeID + 1, (int)((float)bDur * stMod), bDam, bType, stType)).setItemName("stBoneHoe");
        iBoneHoe = (new InfiToolHoe(boneHoeID + 2, (int)((float)bDur * iMod), bDam, bType, iType)).setItemName("iBoneHoe");
        dBoneHoe = (new InfiToolHoe(boneHoeID + 3, (int)((float)bDur * iMod), bDam, bType, dType)).setItemName("dBoneHoe");
        rBoneHoe = (new InfiToolHoe(boneHoeID + 4, (int)((float)bDur * rMod), bDam, bType, rType)).setItemName("rBoneHoe");
        oBoneHoe = (new InfiToolHoe(boneHoeID + 5, (int)((float)bDur * oMod), bDam, bType, oType)).setItemName("oBoneHoe");
        bBoneHoe = (new InfiToolHoe(boneHoeID + 6, (int)((float)bDur * bMod), bDam, bType, bType)).setItemName("bBoneHoe");
        mBoneHoe = (new InfiToolHoe(boneHoeID + 7, (int)((float)bDur * mMod), bDam, bType, mType)).setItemName("mBoneHoe");
        nBoneHoe = (new InfiToolHoe(boneHoeID + 8, (int)((float)bDur * nMod), bDam, bType, nType)).setItemName("nBoneHoe");
        glBoneHoe = (new InfiToolHoe(boneHoeID + 9, (int)((float)bDur * gMod), bDam, bType, glType)).setItemName("glBoneHoe");
        sBoneHoe = (new InfiToolHoe(boneHoeID + 10, (int)((float)bDur * sMod), bDam, bType, sType)).setItemName("sBoneHoe");
        cBoneHoe = (new InfiToolHoe(boneHoeID + 11, (int)((float)bDur * cMod), bDam, bType, cType)).setItemName("cBoneHoe");
        fBoneHoe = (new InfiToolHoe(boneHoeID + 12, (int)((float)bDur * fMod), bDam, bType, fType)).setItemName("fBoneHoe");
        brBoneHoe = (new InfiToolHoe(boneHoeID + 13, (int)((float)bDur * brMod), bDam, bType, brType)).setItemName("brBoneHoe");
        blBoneHoe = (new InfiToolHoe(boneHoeID + 14, (int)((float)bDur * blMod), bDam, bType, blType)).setItemName("blBoneHoe");
        wPaperHoe = (new InfiToolHoe(paperHoeID + 0, (int)((float)pDur * wMod), pDam, pType, wType)).setItemName("wPaperHoe");
        saPaperHoe = (new InfiToolHoe(paperHoeID + 1, (int)((float)pDur * saMod), pDam, pType, saType)).setItemName("saPaperHoe");
        bPaperHoe = (new InfiToolHoe(paperHoeID + 2, (int)((float)pDur * bMod), pDam, pType, bType)).setItemName("bPaperHoe");
        pPaperHoe = (new InfiToolHoe(paperHoeID + 3, (int)((float)pDur * pMod), pDam, pType, pType)).setItemName("pPaperHoe");
        sPaperHoe = (new InfiToolHoe(paperHoeID + 4, (int)((float)pDur * sMod), pDam, pType, sType)).setItemName("sPaperHoe");
        cPaperHoe = (new InfiToolHoe(paperHoeID + 5, (int)((float)pDur * cMod), pDam, pType, cType)).setItemName("cPaperHoe");
        brPaperHoe = (new InfiToolHoe(paperHoeID + 6, (int)((float)pDur * brMod), pDam, pType, brType)).setItemName("brPaperHoe");
        stMossyHoe = (new InfiToolHoe(mossyHoeID + 0, (int)((float)mDur * stMod), mDam, mType, stType)).setItemName("stMossyHoe");
        dMossyHoe = (new InfiToolHoe(mossyHoeID + 1, (int)((float)mDur * iMod), mDam, mType, dType)).setItemName("dMossyHoe");
        rMossyHoe = (new InfiToolHoe(mossyHoeID + 2, (int)((float)mDur * rMod), mDam, mType, rType)).setItemName("rMossyHoe");
        bMossyHoe = (new InfiToolHoe(mossyHoeID + 3, (int)((float)mDur * bMod), mDam, mType, bType)).setItemName("bMossyHoe");
        mMossyHoe = (new InfiToolHoe(mossyHoeID + 4, (int)((float)mDur * mMod), mDam, mType, mType)).setItemName("mMossyHoe");
        glMossyHoe = (new InfiToolHoe(mossyHoeID + 5, (int)((float)mDur * gMod), mDam, mType, glType)).setItemName("glMossyHoe");
        wNetherrackHoe = (new InfiToolHoe(netherrackHoeID + 0, (int)((float)nDur * wMod), nDam, nType, wType)).setItemName("wNetherrackHoe");
        stNetherrackHoe = (new InfiToolHoe(netherrackHoeID + 1, (int)((float)nDur * stMod), nDam, nType, stType)).setItemName("stNetherrackHoe");
        iNetherrackHoe = (new InfiToolHoe(netherrackHoeID + 2, (int)((float)nDur * iMod), nDam, nType, iType)).setItemName("iNetherrackHoe");
        rNetherrackHoe = (new InfiToolHoe(netherrackHoeID + 3, (int)((float)nDur * rMod), nDam, nType, rType)).setItemName("rNetherrackHoe");
        oNetherrackHoe = (new InfiToolHoe(netherrackHoeID + 4, (int)((float)nDur * oMod), nDam, nType, oType)).setItemName("oNetherrackHoe");
        saNetherrackHoe = (new InfiToolHoe(netherrackHoeID + 5, (int)((float)nDur * saMod), nDam, nType, saType)).setItemName("saNetherrackHoe");
        bNetherrackHoe = (new InfiToolHoe(netherrackHoeID + 6, (int)((float)nDur * bMod), nDam, nType, bType)).setItemName("bNetherrackHoe");
        mNetherrackHoe = (new InfiToolHoe(netherrackHoeID + 7, (int)((float)nDur * mMod), nDam, nType, mType)).setItemName("mNetherrackHoe");
        nNetherrackHoe = (new InfiToolHoe(netherrackHoeID + 8, (int)((float)nDur * nMod), nDam, nType, nType)).setItemName("nNetherrackHoe");
        glNetherrackHoe = (new InfiToolHoe(netherrackHoeID + 9, (int)((float)nDur * glMod), nDam, nType, glType)).setItemName("glNetherrackHoe");
        iceNetherrackHoe = (new InfiToolHoe(netherrackHoeID + 10, (int)((float)nDur * iceMod), nDam, nType, iceType)).setItemName("iceNetherrackHoe");
        sNetherrackHoe = (new InfiToolHoe(netherrackHoeID + 11, (int)((float)nDur * sMod), nDam, nType, sType)).setItemName("sNetherrackHoe");
        cNetherrackHoe = (new InfiToolHoe(netherrackHoeID + 12, (int)((float)nDur * cMod), nDam, nType, cType)).setItemName("cNetherrackHoe");
        fNetherrackHoe = (new InfiToolHoe(netherrackHoeID + 13, (int)((float)nDur * fMod), nDam, nType, fType)).setItemName("fNetherrackHoe");
        brNetherrackHoe = (new InfiToolHoe(netherrackHoeID + 14, (int)((float)nDur * brMod), nDam, nType, brType)).setItemName("brNetherrackHoe");
        blNetherrackHoe = (new InfiToolHoe(netherrackHoeID + 15, (int)((float)nDur * blMod), nDam, nType, blType)).setItemName("blNetherrackHoe");
        wGlowstoneHoe = (new InfiToolHoe(glowstoneHoeID + 0, (int)((float)glDur * wMod), glDam, glType, wType)).setItemName("wGlowstoneHoe");
        stGlowstoneHoe = (new InfiToolHoe(glowstoneHoeID + 1, (int)((float)glDur * stMod), glDam, glType, stType)).setItemName("stGlowstoneHoe");
        iGlowstoneHoe = (new InfiToolHoe(glowstoneHoeID + 2, (int)((float)glDur * iMod), glDam, glType, iType)).setItemName("iGlowstoneHoe");
        dGlowstoneHoe = (new InfiToolHoe(glowstoneHoeID + 3, (int)((float)glDur * iMod), glDam, glType, dType)).setItemName("dGlowstoneHoe");
        rGlowstoneHoe = (new InfiToolHoe(glowstoneHoeID + 4, (int)((float)glDur * rMod), glDam, glType, rType)).setItemName("rGlowstoneHoe");
        oGlowstoneHoe = (new InfiToolHoe(glowstoneHoeID + 5, (int)((float)glDur * oMod), glDam, glType, oType)).setItemName("oGlowstoneHoe");
        bGlowstoneHoe = (new InfiToolHoe(glowstoneHoeID + 6, (int)((float)glDur * bMod), glDam, glType, bType)).setItemName("bGlowstoneHoe");
        mGlowstoneHoe = (new InfiToolHoe(glowstoneHoeID + 7, (int)((float)glDur * mMod), glDam, glType, mType)).setItemName("mGlowstoneHoe");
        nGlowstoneHoe = (new InfiToolHoe(glowstoneHoeID + 8, (int)((float)glDur * nMod), glDam, glType, nType)).setItemName("nGlowstoneHoe");
        glGlowstoneHoe = (new InfiToolHoe(glowstoneHoeID + 9, (int)((float)glDur * gMod), glDam, glType, glType)).setItemName("glGlowstoneHoe");
        iceGlowstoneHoe = (new InfiToolHoe(glowstoneHoeID + 10, (int)((float)glDur * iceMod), glDam, glType, iceType)).setItemName("iceGlowstoneHoe");
        lGlowstoneHoe = (new InfiToolHoe(glowstoneHoeID + 11, (int)((float)glDur * gMod), glDam, glType, lType)).setItemName("lGlowstoneHoe");
        sGlowstoneHoe = (new InfiToolHoe(glowstoneHoeID + 12, (int)((float)glDur * sMod), glDam, glType, sType)).setItemName("sGlowstoneHoe");
        blGlowstoneHoe = (new InfiToolHoe(glowstoneHoeID + 13, (int)((float)glDur * blMod), glDam, glType, blType)).setItemName("blGlowstoneHoe");
        wIceHoe = (new InfiToolHoe(iceHoeID + 0, (int)((float)iceDur * wMod), iceDam, iceType, wType)).setItemName("wIceHoe");
        stIceHoe = (new InfiToolHoe(iceHoeID + 1, (int)((float)iceDur * stMod), iceDam, iceType, stType)).setItemName("stIceHoe");
        iIceHoe = (new InfiToolHoe(iceHoeID + 2, (int)((float)iceDur * iMod), iceDam, iceType, iType)).setItemName("iIceHoe");
        dIceHoe = (new InfiToolHoe(iceHoeID + 3, (int)((float)iceDur * dMod), iceDam, iceType, dType)).setItemName("dIceHoe");
        gIceHoe = (new InfiToolHoe(iceHoeID + 4, (int)((float)iceDur * gMod), iceDam, iceType, gType)).setItemName("gIceHoe");
        rIceHoe = (new InfiToolHoe(iceHoeID + 5, (int)((float)iceDur * rMod), iceDam, iceType, rType)).setItemName("rIceHoe");
        oIceHoe = (new InfiToolHoe(iceHoeID + 6, (int)((float)iceDur * oMod), iceDam, iceType, oType)).setItemName("oIceHoe");
        saIceHoe = (new InfiToolHoe(iceHoeID + 7, (int)((float)iceDur * saMod), iceDam, iceType, saType)).setItemName("saIceHoe");
        bIceHoe = (new InfiToolHoe(iceHoeID + 8, (int)((float)iceDur * bMod), iceDam, iceType, bType)).setItemName("bIceHoe");
        glIceHoe = (new InfiToolHoe(iceHoeID + 9, (int)((float)iceDur * gMod), iceDam, iceType, glType)).setItemName("glIceHoe");
        iceIceHoe = (new InfiToolHoe(iceHoeID + 10, (int)((float)iceDur * iMod), iceDam, iceType, iceType)).setItemName("iceIceHoe");
        sIceHoe = (new InfiToolHoe(iceHoeID + 11, (int)((float)iceDur * sMod), iceDam, iceType, sType)).setItemName("sIceHoe");
        cIceHoe = (new InfiToolHoe(iceHoeID + 12, (int)((float)iceDur * cMod), iceDam, iceType, cType)).setItemName("cIceHoe");
        fIceHoe = (new InfiToolHoe(iceHoeID + 13, (int)((float)iceDur * fMod), iceDam, iceType, fType)).setItemName("fIceHoe");
        brIceHoe = (new InfiToolHoe(iceHoeID + 14, (int)((float)iceDur * brMod), iceDam, iceType, brType)).setItemName("brIceHoe");
        dLavaHoe = (new InfiToolHoe(lavaHoeID + 0, (int)((float)lDur * iMod), lDam, lType, dType)).setItemName("dLavaHoe");
        rLavaHoe = (new InfiToolHoe(lavaHoeID + 1, (int)((float)lDur * rMod), lDam, lType, rType)).setItemName("rLavaHoe");
        bLavaHoe = (new InfiToolHoe(lavaHoeID + 2, (int)((float)lDur * bMod), lDam, lType, bType)).setItemName("bLavaHoe");
        nLavaHoe = (new InfiToolHoe(lavaHoeID + 3, (int)((float)lDur * nMod), lDam, lType, nType)).setItemName("nLavaHoe");
        glLavaHoe = (new InfiToolHoe(lavaHoeID + 4, (int)((float)lDur * gMod), lDam, lType, glType)).setItemName("glLavaHoe");
        lLavaHoe = (new InfiToolHoe(lavaHoeID + 5, (int)((float)lDur * lMod), lDam, lType, lType)).setItemName("lLavaHoe");
        blLavaHoe = (new InfiToolHoe(lavaHoeID + 6, (int)((float)lDur * blMod), lDam, lType, blType)).setItemName("blLavaHoe");
        wSlimeHoe = (new InfiToolHoe(slimeHoeID + 0, (int)((float)sDur * wMod), sDam, sType, wType)).setItemName("wSlimeHoe");
        stSlimeHoe = (new InfiToolHoe(slimeHoeID + 1, (int)((float)sDur * stMod), sDam, sType, stType)).setItemName("stSlimeHoe");
        iSlimeHoe = (new InfiToolHoe(slimeHoeID + 2, (int)((float)sDur * iMod), sDam, sType, iType)).setItemName("iSlimeHoe");
        dSlimeHoe = (new InfiToolHoe(slimeHoeID + 3, (int)((float)sDur * iMod), sDam, sType, dType)).setItemName("dSlimeHoe");
        gSlimeHoe = (new InfiToolHoe(slimeHoeID + 4, (int)((float)sDur * gMod), sDam, sType, gType)).setItemName("gSlimeHoe");
        rSlimeHoe = (new InfiToolHoe(slimeHoeID + 5, (int)((float)sDur * rMod), sDam, sType, rType)).setItemName("rSlimeHoe");
        oSlimeHoe = (new InfiToolHoe(slimeHoeID + 6, (int)((float)sDur * oMod), sDam, sType, oType)).setItemName("oSlimeHoe");
        saSlimeHoe = (new InfiToolHoe(slimeHoeID + 7, (int)((float)sDur * saMod), sDam, sType, saType)).setItemName("saSlimeHoe");
        bSlimeHoe = (new InfiToolHoe(slimeHoeID + 8, (int)((float)sDur * bMod), sDam, sType, bType)).setItemName("bSlimeHoe");
        pSlimeHoe = (new InfiToolHoe(slimeHoeID + 9, (int)((float)sDur * pMod), sDam, sType, pType)).setItemName("pSlimeHoe");
        mSlimeHoe = (new InfiToolHoe(slimeHoeID + 10, (int)((float)sDur * mMod), sDam, sType, mType)).setItemName("mSlimeHoe");
        nSlimeHoe = (new InfiToolHoe(slimeHoeID + 11, (int)((float)sDur * nMod), sDam, sType, nType)).setItemName("nSlimeHoe");
        glSlimeHoe = (new InfiToolHoe(slimeHoeID + 12, (int)((float)sDur * gMod), sDam, sType, glType)).setItemName("glSlimeHoe");
        iceSlimeHoe = (new InfiToolHoe(slimeHoeID + 13, (int)((float)sDur * iMod), sDam, sType, iceType)).setItemName("iceSlimeHoe");
        lSlimeHoe = (new InfiToolHoe(slimeHoeID + 14, (int)((float)sDur * lMod), sDam, sType, lType)).setItemName("lSlimeHoe");
        sSlimeHoe = (new InfiToolHoe(slimeHoeID + 15, (int)((float)sDur * sMod), sDam, sType, sType)).setItemName("sSlimeHoe");
        cSlimeHoe = (new InfiToolHoe(slimeHoeID + 16, (int)((float)sDur * cMod), sDam, sType, cType)).setItemName("cSlimeHoe");
        fSlimeHoe = (new InfiToolHoe(slimeHoeID + 17, (int)((float)sDur * fMod), sDam, sType, fType)).setItemName("fSlimeHoe");
        brSlimeHoe = (new InfiToolHoe(slimeHoeID + 18, (int)((float)sDur * brMod), sDam, sType, brType)).setItemName("brSlimeHoe");
        blSlimeHoe = (new InfiToolHoe(slimeHoeID + 19, (int)((float)sDur * blMod), sDam, sType, blType)).setItemName("blSlimeHoe");
        wCactusHoe = (new InfiToolHoe(cactusHoeID + 0, (int)((float)cDur * wMod), cDam, cType, wType)).setItemName("wCactusHoe");
        stCactusHoe = (new InfiToolHoe(cactusHoeID + 1, (int)((float)cDur * stMod), cDam, cType, stType)).setItemName("stCactusHoe");
        saCactusHoe = (new InfiToolHoe(cactusHoeID + 2, (int)((float)cDur * saMod), cDam, cType, saType)).setItemName("saCactusHoe");
        bCactusHoe = (new InfiToolHoe(cactusHoeID + 3, (int)((float)cDur * bMod), cDam, cType, bType)).setItemName("bCactusHoe");
        pCactusHoe = (new InfiToolHoe(cactusHoeID + 4, (int)((float)cDur * pMod), cDam, cType, pType)).setItemName("pCactusHoe");
        nCactusHoe = (new InfiToolHoe(cactusHoeID + 5, (int)((float)cDur * nMod), cDam, cType, nType)).setItemName("nCactusHoe");
        sCactusHoe = (new InfiToolHoe(cactusHoeID + 6, (int)((float)cDur * sMod), cDam, cType, sType)).setItemName("sCactusHoe");
        cCactusHoe = (new InfiToolHoe(cactusHoeID + 7, (int)((float)cDur * cMod), cDam, cType, cType)).setItemName("cCactusHoe");
        fCactusHoe = (new InfiToolHoe(cactusHoeID + 8, (int)((float)cDur * fMod), cDam, cType, fType)).setItemName("fCactusHoe");
        brCactusHoe = (new InfiToolHoe(cactusHoeID + 9, (int)((float)cDur * brMod), cDam, cType, brType)).setItemName("brCactusHoe");
        wFlintHoe = (new InfiToolHoe(flintHoeID + 0, (int)((float)fDur * wMod), fDam, fType, wType)).setItemName("wFlintHoe");
        stFlintHoe = (new InfiToolHoe(flintHoeID + 1, (int)((float)fDur * stMod), fDam, fType, stType)).setItemName("stFlintHoe");
        iFlintHoe = (new InfiToolHoe(flintHoeID + 2, (int)((float)fDur * iMod), fDam, fType, iType)).setItemName("iFlintHoe");
        gFlintHoe = (new InfiToolHoe(flintHoeID + 3, (int)((float)fDur * gMod), fDam, fType, gType)).setItemName("gFlintHoe");
        oFlintHoe = (new InfiToolHoe(flintHoeID + 4, (int)((float)fDur * oMod), fDam, fType, oType)).setItemName("oFlintHoe");
        saFlintHoe = (new InfiToolHoe(flintHoeID + 5, (int)((float)fDur * saMod), fDam, fType, saType)).setItemName("saFlintHoe");
        bFlintHoe = (new InfiToolHoe(flintHoeID + 6, (int)((float)fDur * bMod), fDam, fType, bType)).setItemName("bFlintHoe");
        nFlintHoe = (new InfiToolHoe(flintHoeID + 7, (int)((float)fDur * nMod), fDam, fType, nType)).setItemName("nFlintHoe");
        iceFlintHoe = (new InfiToolHoe(flintHoeID + 8, (int)((float)fDur * wMod), fDam, fType, iceType)).setItemName("iceFlintHoe");
        sFlintHoe = (new InfiToolHoe(flintHoeID + 9, (int)((float)fDur * sMod), fDam, fType, sType)).setItemName("sFlintHoe");
        cFlintHoe = (new InfiToolHoe(flintHoeID + 10, (int)((float)fDur * cMod), fDam, fType, cType)).setItemName("cFlintHoe");
        fFlintHoe = (new InfiToolHoe(flintHoeID + 11, (int)((float)fDur * fMod), fDam, fType, fType)).setItemName("fFlintHoe");
        brFlintHoe = (new InfiToolHoe(flintHoeID + 12, (int)((float)fDur * brMod), fDam, fType, brType)).setItemName("brFlintHoe");
        blFlintHoe = (new InfiToolHoe(flintHoeID + 13, (int)((float)fDur * blMod), fDam, fType, blType)).setItemName("blFlintHoe");
        wBrickHoe = (new InfiToolHoe(brickHoeID + 0, (int)((float)brDur * wMod), brDam, brType, wType)).setItemName("wBrickHoe");
        stBrickHoe = (new InfiToolHoe(brickHoeID + 1, (int)((float)brDur * stMod), brDam, brType, stType)).setItemName("stBrickHoe");
        saBrickHoe = (new InfiToolHoe(brickHoeID + 2, (int)((float)brDur * saMod), brDam, brType, saType)).setItemName("saBrickHoe");
        bBrickHoe = (new InfiToolHoe(brickHoeID + 3, (int)((float)brDur * bMod), brDam, brType, bType)).setItemName("bBrickHoe");
        pBrickHoe = (new InfiToolHoe(brickHoeID + 4, (int)((float)brDur * pMod), brDam, brType, pType)).setItemName("pBrickHoe");
        nBrickHoe = (new InfiToolHoe(brickHoeID + 5, (int)((float)brDur * nMod), brDam, brType, nType)).setItemName("nBrickHoe");
        iceBrickHoe = (new InfiToolHoe(brickHoeID + 6, (int)((float)brDur * iceMod), brDam, brType, iceType)).setItemName("iceBrickHoe");
        sBrickHoe = (new InfiToolHoe(brickHoeID + 7, (int)((float)brDur * sMod), brDam, brType, sType)).setItemName("sBrickHoe");
        cBrickHoe = (new InfiToolHoe(brickHoeID + 8, (int)((float)brDur * cMod), brDam, brType, cType)).setItemName("cBrickHoe");
        fBrickHoe = (new InfiToolHoe(brickHoeID + 9, (int)((float)brDur * fMod), brDam, brType, fType)).setItemName("fBrickHoe");
        brBrickHoe = (new InfiToolHoe(brickHoeID + 10, (int)((float)brDur * brMod), brDam, brType, brType)).setItemName("brBrickHoe");
        dBlazeHoe = (new InfiToolHoe(blazeHoeID + 0, (int)((float)blDur * dMod), blDam, blType, dType)).setItemName("wBlazeHoe");
        rBlazeHoe = (new InfiToolHoe(blazeHoeID + 1, (int)((float)blDur * rMod), blDam, blType, rType)).setItemName("rBlazeHoe");
        bBlazeHoe = (new InfiToolHoe(blazeHoeID + 2, (int)((float)blDur * bMod), blDam, blType, bType)).setItemName("bBlazeHoe");
        nBlazeHoe = (new InfiToolHoe(blazeHoeID + 3, (int)((float)blDur * nMod), blDam, blType, nType)).setItemName("nBlazeHoe");
        glBlazeHoe = (new InfiToolHoe(blazeHoeID + 4, (int)((float)blDur * glMod), blDam, blType, glType)).setItemName("glBlazeHoe");
        lBlazeHoe = (new InfiToolHoe(blazeHoeID + 5, (int)((float)blDur * lMod), blDam, blType, lType)).setItemName("lBlazeHoe");
        fBlazeHoe = (new InfiToolHoe(blazeHoeID + 6, (int)((float)blDur * fMod), blDam, blType, fType)).setItemName("fBlazeHoe");
        blBlazeHoe = (new InfiToolHoe(blazeHoeID + 7, (int)((float)blDur * bMod), blDam, blType, bType)).setItemName("blBlazeHoe");
        /*wWoodFryingPan = (new InfiToolFryingPan(woodFryingPanID + 0, (int)((float)wDur * wMod), wDam, wType, wType)).setItemName("wWoodFryingPan");
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
        blBlazeFryingPan = (new InfiToolFryingPan(blazeFryingPanID + 7, (int)((float)blDur * bMod), blDam, blType, bType)).setItemName("blBlazeFryingPan");*/
    }
}
