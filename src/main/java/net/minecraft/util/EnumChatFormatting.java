package net.minecraft.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public enum EnumChatFormatting
{
    BLACK('0'),
    DARK_BLUE('1'),
    DARK_GREEN('2'),
    DARK_AQUA('3'),
    DARK_RED('4'),
    DARK_PURPLE('5'),
    GOLD('6'),
    GRAY('7'),
    DARK_GRAY('8'),
    BLUE('9'),
    GREEN('a'),
    AQUA('b'),
    RED('c'),
    LIGHT_PURPLE('d'),
    YELLOW('e'),
    WHITE('f'),
    OBFUSCATED('k', true),
    BOLD('l', true),
    STRIKETHROUGH('m', true),
    UNDERLINE('n', true),
    ITALIC('o', true),
    RESET('r');
    private static final Map field_96321_w = new HashMap();
    private static final Map field_96331_x = new HashMap();
    private static final Pattern field_96330_y = Pattern.compile("(?i)" + String.valueOf('\u00a7') + "[0-9A-FK-OR]");
    private final char field_96329_z;
    private final boolean field_96303_A;
    private final String field_96304_B;

    private static final String __OBFID = "CL_00000342";

    private EnumChatFormatting(char par3)
    {
        this(par3, false);
    }

    private EnumChatFormatting(char par3, boolean par4)
    {
        this.field_96329_z = par3;
        this.field_96303_A = par4;
        this.field_96304_B = "\u00a7" + par3;
    }

    public char func_96298_a()
    {
        return this.field_96329_z;
    }

    public boolean func_96301_b()
    {
        return this.field_96303_A;
    }

    // JAVADOC METHOD $$ func_96302_c
    public boolean isColor()
    {
        return !this.field_96303_A && this != RESET;
    }

    public String func_96297_d()
    {
        return this.name().toLowerCase();
    }

    public String toString()
    {
        return this.field_96304_B;
    }

    @SideOnly(Side.CLIENT)
    public static String func_110646_a(String par0Str)
    {
        return par0Str == null ? null : field_96330_y.matcher(par0Str).replaceAll("");
    }

    public static EnumChatFormatting func_96300_b(String par0Str)
    {
        return par0Str == null ? null : (EnumChatFormatting)field_96331_x.get(par0Str.toLowerCase());
    }

    public static Collection func_96296_a(boolean par0, boolean par1)
    {
        ArrayList arraylist = new ArrayList();
        EnumChatFormatting[] aenumchatformatting = values();
        int i = aenumchatformatting.length;

        for (int j = 0; j < i; ++j)
        {
            EnumChatFormatting enumchatformatting = aenumchatformatting[j];

            if ((!enumchatformatting.isColor() || par0) && (!enumchatformatting.func_96301_b() || par1))
            {
                arraylist.add(enumchatformatting.func_96297_d());
            }
        }

        return arraylist;
    }

    static
    {
        EnumChatFormatting[] var0 = values();
        int var1 = var0.length;

        for (int var2 = 0; var2 < var1; ++var2)
        {
            EnumChatFormatting var3 = var0[var2];
            field_96321_w.put(Character.valueOf(var3.func_96298_a()), var3);
            field_96331_x.put(var3.func_96297_d(), var3);
        }
    }
}