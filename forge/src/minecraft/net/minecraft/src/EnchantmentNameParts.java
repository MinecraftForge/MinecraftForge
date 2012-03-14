package net.minecraft.src;

import java.util.Random;

public class EnchantmentNameParts
{
    public static final EnchantmentNameParts field_40253_a = new EnchantmentNameParts();
    private Random field_40251_b = new Random();
    private String[] field_40252_c = "the elder scrolls klaatu berata niktu xyzzy bless curse light darkness fire air earth water hot dry cold wet ignite snuff embiggen twist shorten stretch fiddle destroy imbue galvanize enchant free limited range of towards inside sphere cube self other ball mental physical grow shrink demon elemental spirit animal creature beast humanoid undead fresh stale ".split(" ");

    public String func_40249_a()
    {
        int var1 = this.field_40251_b.nextInt(2) + 3;
        String var2 = "";

        for (int var3 = 0; var3 < var1; ++var3)
        {
            if (var3 > 0)
            {
                var2 = var2 + " ";
            }

            var2 = var2 + this.field_40252_c[this.field_40251_b.nextInt(this.field_40252_c.length)];
        }

        return var2;
    }

    public void func_40250_a(long par1)
    {
        this.field_40251_b.setSeed(par1);
    }
}
