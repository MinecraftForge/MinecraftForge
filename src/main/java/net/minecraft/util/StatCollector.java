package net.minecraft.util;

public class StatCollector
{
    private static StringTranslate localizedName = StringTranslate.getInstance();
    private static StringTranslate field_150828_b = new StringTranslate();
    private static final String __OBFID = "CL_00001211";

    // JAVADOC METHOD $$ func_74838_a
    public static String translateToLocal(String par0Str)
    {
        return localizedName.translateKey(par0Str);
    }

    // JAVADOC METHOD $$ func_74837_a
    public static String translateToLocalFormatted(String par0Str, Object ... par1ArrayOfObj)
    {
        return localizedName.translateKeyFormat(par0Str, par1ArrayOfObj);
    }

    public static String func_150826_b(String p_150826_0_)
    {
        return field_150828_b.translateKey(p_150826_0_);
    }

    public static boolean func_94522_b(String par0Str)
    {
        return localizedName.containsTranslateKey(par0Str);
    }

    public static long func_150827_a()
    {
        return localizedName.func_150510_c();
    }
}