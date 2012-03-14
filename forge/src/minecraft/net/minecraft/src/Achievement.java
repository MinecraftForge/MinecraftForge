package net.minecraft.src;

public class Achievement extends StatBase
{
    /**
     * Is the column (related to center of achievement gui, in 24 pixels unit) that the achievement will be displayed.
     */
    public final int displayColumn;

    /**
     * Is the row (related to center of achievement gui, in 24 pixels unit) that the achievement will be displayed.
     */
    public final int displayRow;

    /**
     * Holds the parent achievement, that must be taken before this achievement is avaiable.
     */
    public final Achievement parentAchievement;

    /**
     * Holds the description of the achievement, ready to be formatted and/or displayed.
     */
    private final String achievementDescription;

    /**
     * Holds a string formatter for the achievement, some of then needs extra dynamic info - like the key used to open
     * the inventory.
     */
    private IStatStringFormat statStringFormatter;

    /**
     * Holds the ItemStack that will be used to draw the achievement into the GUI.
     */
    public final ItemStack theItemStack;

    /**
     * Special achievements have a 'spiked' (on normal texture pack) frame, special achievements are the hardest ones to
     * achieve.
     */
    private boolean isSpecial;

    public Achievement(int par1, String par2Str, int par3, int par4, Item par5Item, Achievement par6Achievement)
    {
        this(par1, par2Str, par3, par4, new ItemStack(par5Item), par6Achievement);
    }

    public Achievement(int par1, String par2Str, int par3, int par4, Block par5Block, Achievement par6Achievement)
    {
        this(par1, par2Str, par3, par4, new ItemStack(par5Block), par6Achievement);
    }

    public Achievement(int par1, String par2Str, int par3, int par4, ItemStack par5ItemStack, Achievement par6Achievement)
    {
        super(5242880 + par1, "achievement." + par2Str);
        this.theItemStack = par5ItemStack;
        this.achievementDescription = "achievement." + par2Str + ".desc";
        this.displayColumn = par3;
        this.displayRow = par4;

        if (par3 < AchievementList.minDisplayColumn)
        {
            AchievementList.minDisplayColumn = par3;
        }

        if (par4 < AchievementList.minDisplayRow)
        {
            AchievementList.minDisplayRow = par4;
        }

        if (par3 > AchievementList.maxDisplayColumn)
        {
            AchievementList.maxDisplayColumn = par3;
        }

        if (par4 > AchievementList.maxDisplayRow)
        {
            AchievementList.maxDisplayRow = par4;
        }

        this.parentAchievement = par6Achievement;
    }

    /**
     * Indicates whether or not the given achievement or statistic is independent (i.e., lacks prerequisites for being
     * update).
     */
    public Achievement setIndependent()
    {
        this.isIndependent = true;
        return this;
    }

    /**
     * Special achievements have a 'spiked' (on normal texture pack) frame, special achievements are the hardest ones to
     * achieve.
     */
    public Achievement setSpecial()
    {
        this.isSpecial = true;
        return this;
    }

    /**
     * Adds the achievement on the internal list of registered achievements, also, it's check for duplicated id's.
     */
    public Achievement registerAchievement()
    {
        super.registerStat();
        AchievementList.achievementList.add(this);
        return this;
    }

    /**
     * Returns whether or not the StatBase-derived class is a statistic (running counter) or an achievement (one-shot).
     */
    public boolean isAchievement()
    {
        return true;
    }

    /**
     * Returns the fully description of the achievement - ready to be displayed on screen.
     */
    public String getDescription()
    {
        return this.statStringFormatter != null ? this.statStringFormatter.formatString(StatCollector.translateToLocal(this.achievementDescription)) : StatCollector.translateToLocal(this.achievementDescription);
    }

    /**
     * Defines a string formatter for the achievement.
     */
    public Achievement setStatStringFormatter(IStatStringFormat par1IStatStringFormat)
    {
        this.statStringFormatter = par1IStatStringFormat;
        return this;
    }

    /**
     * Special achievements have a 'spiked' (on normal texture pack) frame, special achievements are the hardest ones to
     * achieve.
     */
    public boolean getSpecial()
    {
        return this.isSpecial;
    }

    /**
     * Register the stat into StatList.
     */
    public StatBase registerStat()
    {
        return this.registerAchievement();
    }

    /**
     * Initializes the current stat as independent (i.e., lacking prerequisites for being updated) and returns the
     * current instance.
     */
    public StatBase initIndependentStat()
    {
        return this.setIndependent();
    }
}
