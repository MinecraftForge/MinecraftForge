package net.minecraft.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StatList
{
    protected static Map oneShotStats = new HashMap();
    public static List allStats = new ArrayList();
    public static List generalStats = new ArrayList();
    public static List itemStats = new ArrayList();
    public static List objectMineStats = new ArrayList();

    /** times the game has been started */
    public static StatBase startGameStat = (new StatBasic(1000, "stat.startGame")).initIndependentStat().registerStat();

    /** times a world has been created */
    public static StatBase createWorldStat = (new StatBasic(1001, "stat.createWorld")).initIndependentStat().registerStat();

    /** the number of times you have loaded a world */
    public static StatBase loadWorldStat = (new StatBasic(1002, "stat.loadWorld")).initIndependentStat().registerStat();

    /** number of times you've joined a multiplayer world */
    public static StatBase joinMultiplayerStat = (new StatBasic(1003, "stat.joinMultiplayer")).initIndependentStat().registerStat();

    /** number of times you've left a game */
    public static StatBase leaveGameStat = (new StatBasic(1004, "stat.leaveGame")).initIndependentStat().registerStat();

    /** number of minutes you have played */
    public static StatBase minutesPlayedStat = (new StatBasic(1100, "stat.playOneMinute", StatBase.timeStatType)).initIndependentStat().registerStat();

    /** distance you've walked */
    public static StatBase distanceWalkedStat = (new StatBasic(2000, "stat.walkOneCm", StatBase.distanceStatType)).initIndependentStat().registerStat();

    /** distance you have swam */
    public static StatBase distanceSwumStat = (new StatBasic(2001, "stat.swimOneCm", StatBase.distanceStatType)).initIndependentStat().registerStat();

    /** the distance you have fallen */
    public static StatBase distanceFallenStat = (new StatBasic(2002, "stat.fallOneCm", StatBase.distanceStatType)).initIndependentStat().registerStat();

    /** the distance you've climbed */
    public static StatBase distanceClimbedStat = (new StatBasic(2003, "stat.climbOneCm", StatBase.distanceStatType)).initIndependentStat().registerStat();

    /** the distance you've flown */
    public static StatBase distanceFlownStat = (new StatBasic(2004, "stat.flyOneCm", StatBase.distanceStatType)).initIndependentStat().registerStat();

    /** the distance you've dived */
    public static StatBase distanceDoveStat = (new StatBasic(2005, "stat.diveOneCm", StatBase.distanceStatType)).initIndependentStat().registerStat();

    /** the distance you've traveled by minecart */
    public static StatBase distanceByMinecartStat = (new StatBasic(2006, "stat.minecartOneCm", StatBase.distanceStatType)).initIndependentStat().registerStat();

    /** the distance you've traveled by boat */
    public static StatBase distanceByBoatStat = (new StatBasic(2007, "stat.boatOneCm", StatBase.distanceStatType)).initIndependentStat().registerStat();

    /** the distance you've traveled by pig */
    public static StatBase distanceByPigStat = (new StatBasic(2008, "stat.pigOneCm", StatBase.distanceStatType)).initIndependentStat().registerStat();

    /** the times you've jumped */
    public static StatBase jumpStat = (new StatBasic(2010, "stat.jump")).initIndependentStat().registerStat();

    /** the distance you've dropped (or times you've fallen?) */
    public static StatBase dropStat = (new StatBasic(2011, "stat.drop")).initIndependentStat().registerStat();

    /** the amount of damage you've dealt */
    public static StatBase damageDealtStat = (new StatBasic(2020, "stat.damageDealt")).registerStat();

    /** the amount of damage you have taken */
    public static StatBase damageTakenStat = (new StatBasic(2021, "stat.damageTaken")).registerStat();

    /** the number of times you have died */
    public static StatBase deathsStat = (new StatBasic(2022, "stat.deaths")).registerStat();

    /** the number of mobs you have killed */
    public static StatBase mobKillsStat = (new StatBasic(2023, "stat.mobKills")).registerStat();

    /** counts the number of times you've killed a player */
    public static StatBase playerKillsStat = (new StatBasic(2024, "stat.playerKills")).registerStat();
    public static StatBase fishCaughtStat = (new StatBasic(2025, "stat.fishCaught")).registerStat();
    public static StatBase[] mineBlockStatArray = initMinableStats("stat.mineBlock", 16777216);
    public static StatBase[] objectCraftStats;
    public static StatBase[] objectUseStats;
    public static StatBase[] objectBreakStats;
    private static boolean blockStatsInitialized;
    private static boolean itemStatsInitialized;

    public static void func_27092_a() {}

    public static void initBreakableStats()
    {
        objectUseStats = initUsableStats(objectUseStats, "stat.useItem", 16908288, 0, 256);
        objectBreakStats = initBreakStats(objectBreakStats, "stat.breakItem", 16973824, 0, 256);
        blockStatsInitialized = true;
        initCraftableStats();
    }

    public static void initStats()
    {
        objectUseStats = initUsableStats(objectUseStats, "stat.useItem", 16908288, 256, 32000);
        objectBreakStats = initBreakStats(objectBreakStats, "stat.breakItem", 16973824, 256, 32000);
        itemStatsInitialized = true;
        initCraftableStats();
    }

    public static void initCraftableStats()
    {
        if (blockStatsInitialized && itemStatsInitialized)
        {
            HashSet var0 = new HashSet();
            Iterator var1 = CraftingManager.getInstance().getRecipeList().iterator();

            while (var1.hasNext())
            {
                IRecipe var2 = (IRecipe)var1.next();
                var0.add(Integer.valueOf(var2.getRecipeOutput().itemID));
            }

            var1 = FurnaceRecipes.smelting().getSmeltingList().values().iterator();

            while (var1.hasNext())
            {
                ItemStack var4 = (ItemStack)var1.next();
                var0.add(Integer.valueOf(var4.itemID));
            }

            objectCraftStats = new StatBase[32000];
            var1 = var0.iterator();

            while (var1.hasNext())
            {
                Integer var5 = (Integer)var1.next();

                if (Item.itemsList[var5.intValue()] != null)
                {
                    String var3 = StatCollector.translateToLocalFormatted("stat.craftItem", new Object[] {Item.itemsList[var5.intValue()].getStatName()});
                    objectCraftStats[var5.intValue()] = (new StatCrafting(16842752 + var5.intValue(), var3, var5.intValue())).registerStat();
                }
            }

            replaceAllSimilarBlocks(objectCraftStats);
        }
    }

    private static StatBase[] initMinableStats(String par0Str, int par1)
    {
        StatBase[] var2 = new StatBase[256];

        for (int var3 = 0; var3 < 256; ++var3)
        {
            if (Block.blocksList[var3] != null && Block.blocksList[var3].getEnableStats())
            {
                String var4 = StatCollector.translateToLocalFormatted(par0Str, new Object[] {Block.blocksList[var3].translateBlockName()});
                var2[var3] = (new StatCrafting(par1 + var3, var4, var3)).registerStat();
                objectMineStats.add((StatCrafting)var2[var3]);
            }
        }

        replaceAllSimilarBlocks(var2);
        return var2;
    }

    private static StatBase[] initUsableStats(StatBase[] par0ArrayOfStatBase, String par1Str, int par2, int par3, int par4)
    {
        if (par0ArrayOfStatBase == null)
        {
            par0ArrayOfStatBase = new StatBase[32000];
        }

        for (int var5 = par3; var5 < par4; ++var5)
        {
            if (Item.itemsList[var5] != null)
            {
                String var6 = StatCollector.translateToLocalFormatted(par1Str, new Object[] {Item.itemsList[var5].getStatName()});
                par0ArrayOfStatBase[var5] = (new StatCrafting(par2 + var5, var6, var5)).registerStat();

                if (var5 >= 256)
                {
                    itemStats.add((StatCrafting)par0ArrayOfStatBase[var5]);
                }
            }
        }

        replaceAllSimilarBlocks(par0ArrayOfStatBase);
        return par0ArrayOfStatBase;
    }

    private static StatBase[] initBreakStats(StatBase[] par0ArrayOfStatBase, String par1Str, int par2, int par3, int par4)
    {
        if (par0ArrayOfStatBase == null)
        {
            par0ArrayOfStatBase = new StatBase[32000];
        }

        for (int var5 = par3; var5 < par4; ++var5)
        {
            if (Item.itemsList[var5] != null && Item.itemsList[var5].isDamageable())
            {
                String var6 = StatCollector.translateToLocalFormatted(par1Str, new Object[] {Item.itemsList[var5].getStatName()});
                par0ArrayOfStatBase[var5] = (new StatCrafting(par2 + var5, var6, var5)).registerStat();
            }
        }

        replaceAllSimilarBlocks(par0ArrayOfStatBase);
        return par0ArrayOfStatBase;
    }

    private static void replaceAllSimilarBlocks(StatBase[] par0ArrayOfStatBase)
    {
        replaceSimilarBlocks(par0ArrayOfStatBase, Block.waterStill.blockID, Block.waterMoving.blockID);
        replaceSimilarBlocks(par0ArrayOfStatBase, Block.lavaStill.blockID, Block.lavaStill.blockID);
        replaceSimilarBlocks(par0ArrayOfStatBase, Block.pumpkinLantern.blockID, Block.pumpkin.blockID);
        replaceSimilarBlocks(par0ArrayOfStatBase, Block.stoneOvenActive.blockID, Block.stoneOvenIdle.blockID);
        replaceSimilarBlocks(par0ArrayOfStatBase, Block.oreRedstoneGlowing.blockID, Block.oreRedstone.blockID);
        replaceSimilarBlocks(par0ArrayOfStatBase, Block.redstoneRepeaterActive.blockID, Block.redstoneRepeaterIdle.blockID);
        replaceSimilarBlocks(par0ArrayOfStatBase, Block.torchRedstoneActive.blockID, Block.torchRedstoneIdle.blockID);
        replaceSimilarBlocks(par0ArrayOfStatBase, Block.mushroomRed.blockID, Block.mushroomBrown.blockID);
        replaceSimilarBlocks(par0ArrayOfStatBase, Block.stairDouble.blockID, Block.stairSingle.blockID);
        replaceSimilarBlocks(par0ArrayOfStatBase, Block.grass.blockID, Block.dirt.blockID);
        replaceSimilarBlocks(par0ArrayOfStatBase, Block.tilledField.blockID, Block.dirt.blockID);
    }

    private static void replaceSimilarBlocks(StatBase[] par0ArrayOfStatBase, int par1, int par2)
    {
        if (par0ArrayOfStatBase[par1] != null && par0ArrayOfStatBase[par2] == null)
        {
            par0ArrayOfStatBase[par2] = par0ArrayOfStatBase[par1];
        }
        else
        {
            allStats.remove(par0ArrayOfStatBase[par1]);
            objectMineStats.remove(par0ArrayOfStatBase[par1]);
            generalStats.remove(par0ArrayOfStatBase[par1]);
            par0ArrayOfStatBase[par1] = par0ArrayOfStatBase[par2];
        }
    }

    static
    {
        AchievementList.init();
        blockStatsInitialized = false;
        itemStatsInitialized = false;
    }
}
