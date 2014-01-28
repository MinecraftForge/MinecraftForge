package net.minecraft.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.regex.Pattern;

public class StringUtils
{
    private static final Pattern patternControlCode = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");
    private static final String __OBFID = "CL_00001501";

    // JAVADOC METHOD $$ func_76337_a
    @SideOnly(Side.CLIENT)
    public static String ticksToElapsedTime(int par0)
    {
        int j = par0 / 20;
        int k = j / 60;
        j %= 60;
        return j < 10 ? k + ":0" + j : k + ":" + j;
    }

    @SideOnly(Side.CLIENT)
    public static String stripControlCodes(String par0Str)
    {
        return patternControlCode.matcher(par0Str).replaceAll("");
    }

    public static boolean func_151246_b(String p_151246_0_)
    {
        return p_151246_0_ == null || "".equals(p_151246_0_);
    }
}