package net.minecraft.src;

public class StatCollector
{
    private static StringTranslate localizedName = StringTranslate.getInstance();

    public static String translateToLocal(String par0Str)
    {
        return localizedName.translateKey(par0Str);
    }

    public static String translateToLocalFormatted(String par0Str, Object ... par1ArrayOfObj)
    {
        return localizedName.translateKeyFormat(par0Str, par1ArrayOfObj);
    }
}
