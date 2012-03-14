package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;

public class AchievementList
{
    /** Is the smallest column used to display a achievement on the GUI. */
    public static int minDisplayColumn;

    /** Is the smallest row used to display a achievement on the GUI. */
    public static int minDisplayRow;

    /** Is the biggest column used to display a achievement on the GUI. */
    public static int maxDisplayColumn;

    /** Is the biggest row used to display a achievement on the GUI. */
    public static int maxDisplayRow;

    /** Holds a list of all registered achievements. */
    public static List achievementList = new ArrayList();

    /** Is the 'open inventory' achievement. */
    public static Achievement openInventory = (new Achievement(0, "openInventory", 0, 0, Item.book, (Achievement)null)).setIndependent().registerAchievement();

    /** Is the 'getting wood' achievement. */
    public static Achievement mineWood = (new Achievement(1, "mineWood", 2, 1, Block.wood, openInventory)).registerAchievement();

    /** Is the 'benchmarking' achievement. */
    public static Achievement buildWorkBench = (new Achievement(2, "buildWorkBench", 4, -1, Block.workbench, mineWood)).registerAchievement();

    /** Is the 'time to mine' achievement. */
    public static Achievement buildPickaxe = (new Achievement(3, "buildPickaxe", 4, 2, Item.pickaxeWood, buildWorkBench)).registerAchievement();

    /** Is the 'hot topic' achievement. */
    public static Achievement buildFurnace = (new Achievement(4, "buildFurnace", 3, 4, Block.stoneOvenActive, buildPickaxe)).registerAchievement();

    /** Is the 'acquire hardware' achievement. */
    public static Achievement acquireIron = (new Achievement(5, "acquireIron", 1, 4, Item.ingotIron, buildFurnace)).registerAchievement();

    /** Is the 'time to farm' achievement. */
    public static Achievement buildHoe = (new Achievement(6, "buildHoe", 2, -3, Item.hoeWood, buildWorkBench)).registerAchievement();

    /** Is the 'bake bread' achievement. */
    public static Achievement makeBread = (new Achievement(7, "makeBread", -1, -3, Item.bread, buildHoe)).registerAchievement();

    /** Is the 'the lie' achievement. */
    public static Achievement bakeCake = (new Achievement(8, "bakeCake", 0, -5, Item.cake, buildHoe)).registerAchievement();

    /** Is the 'getting a upgrade' achievement. */
    public static Achievement buildBetterPickaxe = (new Achievement(9, "buildBetterPickaxe", 6, 2, Item.pickaxeStone, buildPickaxe)).registerAchievement();

    /** Is the 'delicious fish' achievement. */
    public static Achievement cookFish = (new Achievement(10, "cookFish", 2, 6, Item.fishCooked, buildFurnace)).registerAchievement();

    /** Is the 'on a rail' achievement */
    public static Achievement onARail = (new Achievement(11, "onARail", 2, 3, Block.rail, acquireIron)).setSpecial().registerAchievement();

    /** Is the 'time to strike' achievement. */
    public static Achievement buildSword = (new Achievement(12, "buildSword", 6, -1, Item.swordWood, buildWorkBench)).registerAchievement();

    /** Is the 'monster hunter' achievement. */
    public static Achievement killEnemy = (new Achievement(13, "killEnemy", 8, -1, Item.bone, buildSword)).registerAchievement();

    /** is the 'cow tipper' achievement. */
    public static Achievement killCow = (new Achievement(14, "killCow", 7, -3, Item.leather, buildSword)).registerAchievement();

    /** Is the 'when pig fly' achievement. */
    public static Achievement flyPig = (new Achievement(15, "flyPig", 8, -4, Item.saddle, killCow)).setSpecial().registerAchievement();
    public static Achievement snipeSkeleton = (new Achievement(16, "snipeSkeleton", 7, 0, Item.bow, killEnemy)).setSpecial().registerAchievement();

    /** Is the 'DIAMONDS!' achievement */
    public static Achievement diamonds = (new Achievement(17, "diamonds", -1, 5, Item.diamond, acquireIron)).registerAchievement();

    /** Is the 'We Need to Go Deeper' achievement */
    public static Achievement portal = (new Achievement(18, "portal", -1, 7, Block.obsidian, diamonds)).registerAchievement();

    /** Is the 'Return to Sender' achievement */
    public static Achievement ghast = (new Achievement(19, "ghast", -4, 8, Item.ghastTear, portal)).setSpecial().registerAchievement();

    /** Is the 'Into Fire' achievement */
    public static Achievement blazeRod = (new Achievement(20, "blazeRod", 0, 9, Item.blazeRod, portal)).registerAchievement();

    /** Is the 'Local Brewery' achievement */
    public static Achievement potion = (new Achievement(21, "potion", 2, 8, Item.potion, blazeRod)).registerAchievement();

    /** Is the 'The End?' achievement */
    public static Achievement theEnd = (new Achievement(22, "theEnd", 3, 10, Item.eyeOfEnder, blazeRod)).setSpecial().registerAchievement();

    /** Is the 'The End.' achievement */
    public static Achievement theEnd2 = (new Achievement(23, "theEnd2", 4, 13, Block.dragonEgg, theEnd)).setSpecial().registerAchievement();

    /** Is the 'Enchanter' achievement */
    public static Achievement enchantments = (new Achievement(24, "enchantments", -4, 4, Block.enchantmentTable, diamonds)).registerAchievement();
    public static Achievement overkill = (new Achievement(25, "overkill", -4, 1, Item.swordDiamond, enchantments)).setSpecial().registerAchievement();

    /** Is the 'Librarian' achievement */
    public static Achievement bookcase = (new Achievement(26, "bookcase", -3, 6, Block.bookShelf, enchantments)).registerAchievement();

    /**
     * A stub functions called to make the static initializer for this class run.
     */
    public static void init() {}

    static
    {
        System.out.println(achievementList.size() + " achievements");
    }
}
