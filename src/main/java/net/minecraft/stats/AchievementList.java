package net.minecraft.stats;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonSerializableSet;

public class AchievementList
{
    // JAVADOC FIELD $$ field_76010_a
    public static int minDisplayColumn;
    // JAVADOC FIELD $$ field_76008_b
    public static int minDisplayRow;
    // JAVADOC FIELD $$ field_76009_c
    public static int maxDisplayColumn;
    // JAVADOC FIELD $$ field_76006_d
    public static int maxDisplayRow;
    // JAVADOC FIELD $$ field_76007_e
    public static List achievementList = new ArrayList();
    // JAVADOC FIELD $$ field_76004_f
    public static Achievement openInventory = (new Achievement("achievement.openInventory", "openInventory", 0, 0, Items.book, (Achievement)null)).initIndependentStat().registerStat();
    // JAVADOC FIELD $$ field_76005_g
    public static Achievement mineWood = (new Achievement("achievement.mineWood", "mineWood", 2, 1, Blocks.log, openInventory)).registerStat();
    // JAVADOC FIELD $$ field_76017_h
    public static Achievement buildWorkBench = (new Achievement("achievement.buildWorkBench", "buildWorkBench", 4, -1, Blocks.crafting_table, mineWood)).registerStat();
    // JAVADOC FIELD $$ field_76018_i
    public static Achievement buildPickaxe = (new Achievement("achievement.buildPickaxe", "buildPickaxe", 4, 2, Items.wooden_pickaxe, buildWorkBench)).registerStat();
    // JAVADOC FIELD $$ field_76015_j
    public static Achievement buildFurnace = (new Achievement("achievement.buildFurnace", "buildFurnace", 3, 4, Blocks.furnace, buildPickaxe)).registerStat();
    // JAVADOC FIELD $$ field_76016_k
    public static Achievement acquireIron = (new Achievement("achievement.acquireIron", "acquireIron", 1, 4, Items.iron_ingot, buildFurnace)).registerStat();
    // JAVADOC FIELD $$ field_76013_l
    public static Achievement buildHoe = (new Achievement("achievement.buildHoe", "buildHoe", 2, -3, Items.wooden_hoe, buildWorkBench)).registerStat();
    // JAVADOC FIELD $$ field_76014_m
    public static Achievement makeBread = (new Achievement("achievement.makeBread", "makeBread", -1, -3, Items.bread, buildHoe)).registerStat();
    // JAVADOC FIELD $$ field_76011_n
    public static Achievement bakeCake = (new Achievement("achievement.bakeCake", "bakeCake", 0, -5, Items.cake, buildHoe)).registerStat();
    // JAVADOC FIELD $$ field_76012_o
    public static Achievement buildBetterPickaxe = (new Achievement("achievement.buildBetterPickaxe", "buildBetterPickaxe", 6, 2, Items.stone_pickaxe, buildPickaxe)).registerStat();
    // JAVADOC FIELD $$ field_76026_p
    public static Achievement cookFish = (new Achievement("achievement.cookFish", "cookFish", 2, 6, Items.cooked_fished, buildFurnace)).registerStat();
    // JAVADOC FIELD $$ field_76025_q
    public static Achievement onARail = (new Achievement("achievement.onARail", "onARail", 2, 3, Blocks.rail, acquireIron)).setSpecial().registerStat();
    // JAVADOC FIELD $$ field_76024_r
    public static Achievement buildSword = (new Achievement("achievement.buildSword", "buildSword", 6, -1, Items.wooden_sword, buildWorkBench)).registerStat();
    // JAVADOC FIELD $$ field_76023_s
    public static Achievement killEnemy = (new Achievement("achievement.killEnemy", "killEnemy", 8, -1, Items.bone, buildSword)).registerStat();
    // JAVADOC FIELD $$ field_76022_t
    public static Achievement killCow = (new Achievement("achievement.killCow", "killCow", 7, -3, Items.leather, buildSword)).registerStat();
    // JAVADOC FIELD $$ field_76021_u
    public static Achievement flyPig = (new Achievement("achievement.flyPig", "flyPig", 9, -3, Items.saddle, killCow)).setSpecial().registerStat();
    // JAVADOC FIELD $$ field_76020_v
    public static Achievement snipeSkeleton = (new Achievement("achievement.snipeSkeleton", "snipeSkeleton", 7, 0, Items.bow, killEnemy)).setSpecial().registerStat();
    // JAVADOC FIELD $$ field_76019_w
    public static Achievement diamonds = (new Achievement("achievement.diamonds", "diamonds", -1, 5, Blocks.diamond_ore, acquireIron)).registerStat();
    public static Achievement field_150966_x = (new Achievement("achievement.diamondsToYou", "diamondsToYou", -1, 2, Items.diamond, diamonds)).registerStat();
    // JAVADOC FIELD $$ field_76029_x
    public static Achievement portal = (new Achievement("achievement.portal", "portal", -1, 7, Blocks.obsidian, diamonds)).registerStat();
    // JAVADOC FIELD $$ field_76028_y
    public static Achievement ghast = (new Achievement("achievement.ghast", "ghast", -4, 8, Items.ghast_tear, portal)).setSpecial().registerStat();
    // JAVADOC FIELD $$ field_76027_z
    public static Achievement blazeRod = (new Achievement("achievement.blazeRod", "blazeRod", 0, 9, Items.blaze_rod, portal)).registerStat();
    // JAVADOC FIELD $$ field_76001_A
    public static Achievement potion = (new Achievement("achievement.potion", "potion", 2, 8, Items.potionitem, blazeRod)).registerStat();
    // JAVADOC FIELD $$ field_76002_B
    public static Achievement theEnd = (new Achievement("achievement.theEnd", "theEnd", 3, 10, Items.ender_eye, blazeRod)).setSpecial().registerStat();
    // JAVADOC FIELD $$ field_76003_C
    public static Achievement theEnd2 = (new Achievement("achievement.theEnd2", "theEnd2", 4, 13, Blocks.dragon_egg, theEnd)).setSpecial().registerStat();
    // JAVADOC FIELD $$ field_75998_D
    public static Achievement enchantments = (new Achievement("achievement.enchantments", "enchantments", -4, 4, Blocks.enchanting_table, diamonds)).registerStat();
    public static Achievement overkill = (new Achievement("achievement.overkill", "overkill", -4, 1, Items.diamond_sword, enchantments)).setSpecial().registerStat();
    // JAVADOC FIELD $$ field_76000_F
    public static Achievement bookcase = (new Achievement("achievement.bookcase", "bookcase", -3, 6, Blocks.bookshelf, enchantments)).registerStat();
    public static Achievement field_150962_H = (new Achievement("achievement.breedCow", "breedCow", 7, -5, Items.wheat, killCow)).registerStat();
    public static Achievement field_150963_I = (new Achievement("achievement.spawnWither", "spawnWither", 7, 12, new ItemStack(Items.skull, 1, 1), theEnd2)).registerStat();
    public static Achievement field_150964_J = (new Achievement("achievement.killWither", "killWither", 7, 10, Items.nether_star, field_150963_I)).registerStat();
    public static Achievement field_150965_K = (new Achievement("achievement.fullBeacon", "fullBeacon", 7, 8, Blocks.beacon, field_150964_J)).setSpecial().registerStat();
    public static Achievement field_150961_L = (new Achievement("achievement.exploreAllBiomes", "exploreAllBiomes", 4, 8, Items.diamond_boots, theEnd)).func_150953_b(JsonSerializableSet.class).setSpecial().registerStat();
    private static final String __OBFID = "CL_00001467";

    // JAVADOC METHOD $$ func_75997_a
    public static void init() {}
}