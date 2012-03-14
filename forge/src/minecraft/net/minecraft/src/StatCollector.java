package net.minecraft.src;

public class StatCollector
{
    private static StringTranslate localizedName = StringTranslate.getInstance();

    /**
     * Translates a Stat name
     */
    public static String translateToLocal(String par0Str)
    {
        return localizedName.translateKey(par0Str);
    }

    /**
     * Translates a Stat name with format args
     */
    public static String translateToLocalFormatted(String par0Str, Object ... par1ArrayOfObj)
    {
        return localizedName.translateKeyFormat(par0Str, par1ArrayOfObj);
    }
}
