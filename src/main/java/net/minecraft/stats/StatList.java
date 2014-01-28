package net.minecraft.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ChatComponentTranslation;

public class StatList
{
    // JAVADOC FIELD $$ field_75942_a
    protected static Map oneShotStats = new HashMap();
    public static List allStats = new ArrayList();
    public static List generalStats = new ArrayList();
    public static List itemStats = new ArrayList();
    // JAVADOC FIELD $$ field_75939_e
    public static List objectMineStats = new ArrayList();
    // JAVADOC FIELD $$ field_75947_j
    public static StatBase leaveGameStat = (new StatBasic("stat.leaveGame", new ChatComponentTranslation("stat.leaveGame", new Object[0]))).initIndependentStat().registerStat();
    // JAVADOC FIELD $$ field_75948_k
    public static StatBase minutesPlayedStat = (new StatBasic("stat.playOneMinute", new ChatComponentTranslation("stat.playOneMinute", new Object[0]), StatBase.timeStatType)).initIndependentStat().registerStat();
    // JAVADOC FIELD $$ field_75945_l
    public static StatBase distanceWalkedStat = (new StatBasic("stat.walkOneCm", new ChatComponentTranslation("stat.walkOneCm", new Object[0]), StatBase.distanceStatType)).initIndependentStat().registerStat();
    // JAVADOC FIELD $$ field_75946_m
    public static StatBase distanceSwumStat = (new StatBasic("stat.swimOneCm", new ChatComponentTranslation("stat.swimOneCm", new Object[0]), StatBase.distanceStatType)).initIndependentStat().registerStat();
    // JAVADOC FIELD $$ field_75943_n
    public static StatBase distanceFallenStat = (new StatBasic("stat.fallOneCm", new ChatComponentTranslation("stat.fallOneCm", new Object[0]), StatBase.distanceStatType)).initIndependentStat().registerStat();
    // JAVADOC FIELD $$ field_75944_o
    public static StatBase distanceClimbedStat = (new StatBasic("stat.climbOneCm", new ChatComponentTranslation("stat.climbOneCm", new Object[0]), StatBase.distanceStatType)).initIndependentStat().registerStat();
    // JAVADOC FIELD $$ field_75958_p
    public static StatBase distanceFlownStat = (new StatBasic("stat.flyOneCm", new ChatComponentTranslation("stat.flyOneCm", new Object[0]), StatBase.distanceStatType)).initIndependentStat().registerStat();
    // JAVADOC FIELD $$ field_75957_q
    public static StatBase distanceDoveStat = (new StatBasic("stat.diveOneCm", new ChatComponentTranslation("stat.diveOneCm", new Object[0]), StatBase.distanceStatType)).initIndependentStat().registerStat();
    // JAVADOC FIELD $$ field_75956_r
    public static StatBase distanceByMinecartStat = (new StatBasic("stat.minecartOneCm", new ChatComponentTranslation("stat.minecartOneCm", new Object[0]), StatBase.distanceStatType)).initIndependentStat().registerStat();
    // JAVADOC FIELD $$ field_75955_s
    public static StatBase distanceByBoatStat = (new StatBasic("stat.boatOneCm", new ChatComponentTranslation("stat.boatOneCm", new Object[0]), StatBase.distanceStatType)).initIndependentStat().registerStat();
    // JAVADOC FIELD $$ field_75954_t
    public static StatBase distanceByPigStat = (new StatBasic("stat.pigOneCm", new ChatComponentTranslation("stat.pigOneCm", new Object[0]), StatBase.distanceStatType)).initIndependentStat().registerStat();
    public static StatBase field_151185_q = (new StatBasic("stat.horseOneCm", new ChatComponentTranslation("stat.horseOneCm", new Object[0]), StatBase.distanceStatType)).initIndependentStat().registerStat();
    // JAVADOC FIELD $$ field_75953_u
    public static StatBase jumpStat = (new StatBasic("stat.jump", new ChatComponentTranslation("stat.jump", new Object[0]))).initIndependentStat().registerStat();
    // JAVADOC FIELD $$ field_75952_v
    public static StatBase dropStat = (new StatBasic("stat.drop", new ChatComponentTranslation("stat.drop", new Object[0]))).initIndependentStat().registerStat();
    // JAVADOC FIELD $$ field_75951_w
    public static StatBase damageDealtStat = (new StatBasic("stat.damageDealt", new ChatComponentTranslation("stat.damageDealt", new Object[0]), StatBase.field_111202_k)).registerStat();
    // JAVADOC FIELD $$ field_75961_x
    public static StatBase damageTakenStat = (new StatBasic("stat.damageTaken", new ChatComponentTranslation("stat.damageTaken", new Object[0]), StatBase.field_111202_k)).registerStat();
    // JAVADOC FIELD $$ field_75960_y
    public static StatBase deathsStat = (new StatBasic("stat.deaths", new ChatComponentTranslation("stat.deaths", new Object[0]))).registerStat();
    // JAVADOC FIELD $$ field_75959_z
    public static StatBase mobKillsStat = (new StatBasic("stat.mobKills", new ChatComponentTranslation("stat.mobKills", new Object[0]))).registerStat();
    public static StatBase field_151186_x = (new StatBasic("stat.animalsBred", new ChatComponentTranslation("stat.animalsBred", new Object[0]))).registerStat();
    // JAVADOC FIELD $$ field_75932_A
    public static StatBase playerKillsStat = (new StatBasic("stat.playerKills", new ChatComponentTranslation("stat.playerKills", new Object[0]))).registerStat();
    public static StatBase fishCaughtStat = (new StatBasic("stat.fishCaught", new ChatComponentTranslation("stat.fishCaught", new Object[0]))).registerStat();
    public static StatBase field_151183_A = (new StatBasic("stat.junkFished", new ChatComponentTranslation("stat.junkFished", new Object[0]))).registerStat();
    public static StatBase field_151184_B = (new StatBasic("stat.treasureFished", new ChatComponentTranslation("stat.treasureFished", new Object[0]))).registerStat();
    public static final StatBase[] mineBlockStatArray = new StatBase[4096];
    // JAVADOC FIELD $$ field_75928_D
    public static final StatBase[] objectCraftStats = new StatBase[32000];
    // JAVADOC FIELD $$ field_75929_E
    public static final StatBase[] objectUseStats = new StatBase[32000];
    // JAVADOC FIELD $$ field_75930_F
    public static final StatBase[] objectBreakStats = new StatBase[32000];
    private static final String __OBFID = "CL_00001480";

    public static void func_151178_a()
    {
        func_151181_c();
        initStats();
        func_151179_e();
        initCraftableStats();
        AchievementList.init();
        EntityList.func_151514_a();
    }

    // JAVADOC METHOD $$ func_75918_d
    private static void initCraftableStats()
    {
        HashSet hashset = new HashSet();
        Iterator iterator = CraftingManager.getInstance().getRecipeList().iterator();

        while (iterator.hasNext())
        {
            IRecipe irecipe = (IRecipe)iterator.next();

            if (irecipe.getRecipeOutput() != null)
            {
                hashset.add(irecipe.getRecipeOutput().getItem());
            }
        }

        iterator = FurnaceRecipes.smelting().getSmeltingList().values().iterator();

        while (iterator.hasNext())
        {
            ItemStack itemstack = (ItemStack)iterator.next();
            hashset.add(itemstack.getItem());
        }

        iterator = hashset.iterator();

        while (iterator.hasNext())
        {
            Item item = (Item)iterator.next();

            if (item != null)
            {
                int i = Item.func_150891_b(item); //TODO: Hook FML's stat change event and re-assign these
                objectCraftStats[i] = (new StatCrafting("stat.craftItem." + i, new ChatComponentTranslation("stat.craftItem", new Object[] {(new ItemStack(item)).func_151000_E()}), item)).registerStat();
            }
        }

        replaceAllSimilarBlocks(objectCraftStats);
    }

    private static void func_151181_c()
    {
        Iterator iterator = Block.field_149771_c.iterator();

        while (iterator.hasNext())
        {
            Block block = (Block)iterator.next();

            if (Item.func_150898_a(block) != null)
            {
                int i = Block.func_149682_b(block); //TODO: Hook FML's stat change event and re-assign these

                if (block.func_149652_G())
                {
                    mineBlockStatArray[i] = (new StatCrafting("stat.mineBlock." + i, new ChatComponentTranslation("stat.mineBlock", new Object[] {(new ItemStack(block)).func_151000_E()}), Item.func_150898_a(block))).registerStat();
                    objectMineStats.add((StatCrafting)mineBlockStatArray[i]);
                }
            }
        }

        replaceAllSimilarBlocks(mineBlockStatArray);
    }

    private static void initStats()
    {
        Iterator iterator = Item.field_150901_e.iterator();

        while (iterator.hasNext())
        {
            Item item = (Item)iterator.next();

            if (item != null)
            {
                int i = Item.func_150891_b(item); //TODO: Hook FML's stat change event and re-assign these
                objectUseStats[i] = (new StatCrafting("stat.useItem." + i, new ChatComponentTranslation("stat.useItem", new Object[] {(new ItemStack(item)).func_151000_E()}), item)).registerStat();

                if (!(item instanceof ItemBlock))
                {
                    itemStats.add((StatCrafting)objectUseStats[i]);
                }
            }
        }

        replaceAllSimilarBlocks(objectUseStats);
    }

    private static void func_151179_e()
    {
        Iterator iterator = Item.field_150901_e.iterator();

        while (iterator.hasNext())
        {
            Item item = (Item)iterator.next();

            if (item != null)
            {
                int i = Item.func_150891_b(item); //TODO: Hook FML's stat change event and re-assign these

                if (item.isDamageable())
                {
                    objectBreakStats[i] = (new StatCrafting("stat.breakItem." + i, new ChatComponentTranslation("stat.breakItem", new Object[] {(new ItemStack(item)).func_151000_E()}), item)).registerStat();
                }
            }
        }

        replaceAllSimilarBlocks(objectBreakStats);
    }

    // JAVADOC METHOD $$ func_75924_a
    private static void replaceAllSimilarBlocks(StatBase[] par0ArrayOfStatBase)
    {
        func_151180_a(par0ArrayOfStatBase, Blocks.water, Blocks.flowing_water);
        func_151180_a(par0ArrayOfStatBase, Blocks.lava, Blocks.flowing_lava);
        func_151180_a(par0ArrayOfStatBase, Blocks.lit_pumpkin, Blocks.pumpkin);
        func_151180_a(par0ArrayOfStatBase, Blocks.lit_furnace, Blocks.furnace);
        func_151180_a(par0ArrayOfStatBase, Blocks.lit_redstone_ore, Blocks.redstone_ore);
        func_151180_a(par0ArrayOfStatBase, Blocks.powered_repeater, Blocks.unpowered_repeater);
        func_151180_a(par0ArrayOfStatBase, Blocks.powered_comparator, Blocks.unpowered_comparator);
        func_151180_a(par0ArrayOfStatBase, Blocks.redstone_torch, Blocks.unlit_redstone_torch);
        func_151180_a(par0ArrayOfStatBase, Blocks.lit_redstone_lamp, Blocks.redstone_lamp);
        func_151180_a(par0ArrayOfStatBase, Blocks.red_mushroom, Blocks.brown_mushroom);
        func_151180_a(par0ArrayOfStatBase, Blocks.double_stone_slab, Blocks.stone_slab);
        func_151180_a(par0ArrayOfStatBase, Blocks.double_wooden_slab, Blocks.wooden_slab);
        func_151180_a(par0ArrayOfStatBase, Blocks.grass, Blocks.dirt);
        func_151180_a(par0ArrayOfStatBase, Blocks.farmland, Blocks.dirt);
    }

    private static void func_151180_a(StatBase[] p_151180_0_, Block p_151180_1_, Block p_151180_2_)
    {
        int i = Block.func_149682_b(p_151180_1_);
        int j = Block.func_149682_b(p_151180_2_);

        if (p_151180_0_[i] != null && p_151180_0_[j] == null)
        {
            p_151180_0_[j] = p_151180_0_[i];
        }
        else
        {
            allStats.remove(p_151180_0_[i]);
            objectMineStats.remove(p_151180_0_[i]);
            generalStats.remove(p_151180_0_[i]);
            p_151180_0_[i] = p_151180_0_[j];
        }
    }

    public static StatBase func_151182_a(EntityList.EntityEggInfo p_151182_0_)
    {
        String s = EntityList.getStringFromID(p_151182_0_.spawnedID);
        return s == null ? null : (new StatBase("stat.killEntity." + s, new ChatComponentTranslation("stat.entityKill", new Object[] {new ChatComponentTranslation("entity." + s + ".name", new Object[0])}))).registerStat();
    }

    public static StatBase func_151176_b(EntityList.EntityEggInfo p_151176_0_)
    {
        String s = EntityList.getStringFromID(p_151176_0_.spawnedID);
        return s == null ? null : (new StatBase("stat.entityKilledBy." + s, new ChatComponentTranslation("stat.entityKilledBy", new Object[] {new ChatComponentTranslation("entity." + s + ".name", new Object[0])}))).registerStat();
    }

    public static StatBase func_151177_a(String p_151177_0_)
    {
        return (StatBase)oneShotStats.get(p_151177_0_);
    }
}