package net.minecraft.src;

final class StatTypeDistance implements IStatType
{
    /**
     * Formats a given stat for human consumption.
     */
    public String format(int par1)
    {
        double var3 = (double)par1 / 100.0D;
        double var5 = var3 / 1000.0D;
        return var5 > 0.5D ? StatBase.getDecimalFormat().format(var5) + " km" : (var3 > 0.5D ? StatBase.getDecimalFormat().format(var3) + " m" : par1 + " cm");
    }
}
