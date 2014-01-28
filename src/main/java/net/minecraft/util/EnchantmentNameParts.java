package net.minecraft.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class EnchantmentNameParts
{
    public static final EnchantmentNameParts field_148338_a = new EnchantmentNameParts();
    private Random field_148336_b = new Random();
    private String[] field_148337_c = "the elder scrolls klaatu berata niktu xyzzy bless curse light darkness fire air earth water hot dry cold wet ignite snuff embiggen twist shorten stretch fiddle destroy imbue galvanize enchant free limited range of towards inside sphere cube self other ball mental physical grow shrink demon elemental spirit animal creature beast humanoid undead fresh stale ".split(" ");
    private static final String __OBFID = "CL_00000756";

    public String func_148334_a()
    {
        int i = this.field_148336_b.nextInt(2) + 3;
        String s = "";

        for (int j = 0; j < i; ++j)
        {
            if (j > 0)
            {
                s = s + " ";
            }

            s = s + this.field_148337_c[this.field_148336_b.nextInt(this.field_148337_c.length)];
        }

        return s;
    }

    public void func_148335_a(long p_148335_1_)
    {
        this.field_148336_b.setSeed(p_148335_1_);
    }
}