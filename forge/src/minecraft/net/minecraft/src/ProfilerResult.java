package net.minecraft.src;

public final class ProfilerResult implements Comparable
{
    /**
     * Percentage of time spent in this ProfilerResult relative to its parent ProfilerResult
     */
    public double sectionPercentage;

    /**
     * Percentage of time spent in this ProfilerResult relative to the entire game
     */
    public double globalPercentage;

    /** The name of this ProfilerResult */
    public String name;

    public ProfilerResult(String par1Str, double par2, double par4)
    {
        this.name = par1Str;
        this.sectionPercentage = par2;
        this.globalPercentage = par4;
    }

    /**
     * Called from compareTo()
     */
    public int compareProfilerResult(ProfilerResult par1ProfilerResult)
    {
        return par1ProfilerResult.sectionPercentage < this.sectionPercentage ? -1 : (par1ProfilerResult.sectionPercentage > this.sectionPercentage ? 1 : par1ProfilerResult.name.compareTo(this.name));
    }

    /**
     * Compute the color used to display this ProfilerResult on the debug screen
     */
    public int getDisplayColor()
    {
        return (this.name.hashCode() & 11184810) + 4473924;
    }

    public int compareTo(Object par1Obj)
    {
        return this.compareProfilerResult((ProfilerResult)par1Obj);
    }
}
