package net.minecraft.src;

final class StatTypeTime implements IStatType
{
    /**
     * Formats a given stat for human consumption.
     */
    public String format(int par1)
    {
        double var2 = (double)par1 / 20.0D;
        double var4 = var2 / 60.0D;
        double var6 = var4 / 60.0D;
        double var8 = var6 / 24.0D;
        double var10 = var8 / 365.0D;
        return var10 > 0.5D ? StatBase.getDecimalFormat().format(var10) + " y" : (var8 > 0.5D ? StatBase.getDecimalFormat().format(var8) + " d" : (var6 > 0.5D ? StatBase.getDecimalFormat().format(var6) + " h" : (var4 > 0.5D ? StatBase.getDecimalFormat().format(var4) + " m" : var2 + " s")));
    }
}
