package net.minecraft.src;

public class SaveFormatComparator implements Comparable
{
    /** the file name of this save */
    private final String fileName;

    /** the displayed name of this save file */
    private final String displayName;
    private final long lastTimePlayed;
    private final long sizeOnDisk;
    private final boolean requiresConversion;
    private final int gameType;
    private final boolean hardcore;

    public SaveFormatComparator(String par1Str, String par2Str, long par3, long par5, int par7, boolean par8, boolean par9)
    {
        this.fileName = par1Str;
        this.displayName = par2Str;
        this.lastTimePlayed = par3;
        this.sizeOnDisk = par5;
        this.gameType = par7;
        this.requiresConversion = par8;
        this.hardcore = par9;
    }

    /**
     * return the file name
     */
    public String getFileName()
    {
        return this.fileName;
    }

    /**
     * return the display name of the save
     */
    public String getDisplayName()
    {
        return this.displayName;
    }

    public boolean requiresConversion()
    {
        return this.requiresConversion;
    }

    public long getLastTimePlayed()
    {
        return this.lastTimePlayed;
    }

    public int compareTo(SaveFormatComparator par1SaveFormatComparator)
    {
        return this.lastTimePlayed < par1SaveFormatComparator.lastTimePlayed ? 1 : (this.lastTimePlayed > par1SaveFormatComparator.lastTimePlayed ? -1 : this.fileName.compareTo(par1SaveFormatComparator.fileName));
    }

    public int getGameType()
    {
        return this.gameType;
    }

    public boolean isHardcoreModeEnabled()
    {
        return this.hardcore;
    }

    public int compareTo(Object par1Obj)
    {
        return this.compareTo((SaveFormatComparator)par1Obj);
    }
}
