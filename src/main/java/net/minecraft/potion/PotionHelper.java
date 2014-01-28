package net.minecraft.potion;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class PotionHelper
{
    public static final String field_77924_a = null;
    public static final String sugarEffect;
    public static final String ghastTearEffect = "+0-1-2-3&4-4+13";
    public static final String spiderEyeEffect;
    public static final String fermentedSpiderEyeEffect;
    public static final String speckledMelonEffect;
    public static final String blazePowderEffect;
    public static final String magmaCreamEffect;
    public static final String redstoneEffect;
    public static final String glowstoneEffect;
    public static final String gunpowderEffect;
    public static final String goldenCarrotEffect;
    public static final String field_151423_m;
    private static final HashMap potionRequirements = new HashMap();
    // JAVADOC FIELD $$ field_77928_m
    private static final HashMap potionAmplifiers = new HashMap();
    private static final HashMap field_77925_n;
    // JAVADOC FIELD $$ field_77926_o
    private static final String[] potionPrefixes;
    private static final String __OBFID = "CL_00000078";

    // JAVADOC METHOD $$ func_77914_a
    public static boolean checkFlag(int par0, int par1)
    {
        return (par0 & 1 << par1) != 0;
    }

    // JAVADOC METHOD $$ func_77910_c
    private static int isFlagSet(int par0, int par1)
    {
        // JAVADOC METHOD $$ func_77914_a
        return checkFlag(par0, par1) ? 1 : 0;
    }

    // JAVADOC METHOD $$ func_77916_d
    private static int isFlagUnset(int par0, int par1)
    {
        // JAVADOC METHOD $$ func_77914_a
        return checkFlag(par0, par1) ? 0 : 1;
    }

    public static int func_77909_a(int par0)
    {
        return func_77908_a(par0, 5, 4, 3, 2, 1);
    }

    // JAVADOC METHOD $$ func_77911_a
    public static int calcPotionLiquidColor(Collection par0Collection)
    {
        int i = 3694022;

        if (par0Collection != null && !par0Collection.isEmpty())
        {
            float f = 0.0F;
            float f1 = 0.0F;
            float f2 = 0.0F;
            float f3 = 0.0F;
            Iterator iterator = par0Collection.iterator();

            while (iterator.hasNext())
            {
                PotionEffect potioneffect = (PotionEffect)iterator.next();
                int j = Potion.potionTypes[potioneffect.getPotionID()].getLiquidColor();

                for (int k = 0; k <= potioneffect.getAmplifier(); ++k)
                {
                    f += (float)(j >> 16 & 255) / 255.0F;
                    f1 += (float)(j >> 8 & 255) / 255.0F;
                    f2 += (float)(j >> 0 & 255) / 255.0F;
                    ++f3;
                }
            }

            f = f / f3 * 255.0F;
            f1 = f1 / f3 * 255.0F;
            f2 = f2 / f3 * 255.0F;
            return (int)f << 16 | (int)f1 << 8 | (int)f2;
        }
        else
        {
            return i;
        }
    }

    public static boolean func_82817_b(Collection par0Collection)
    {
        Iterator iterator = par0Collection.iterator();
        PotionEffect potioneffect;

        do
        {
            if (!iterator.hasNext())
            {
                return true;
            }

            potioneffect = (PotionEffect)iterator.next();
        }
        while (potioneffect.getIsAmbient());

        return false;
    }

    @SideOnly(Side.CLIENT)
    public static int func_77915_a(int par0, boolean par1)
    {
        if (!par1)
        {
            if (field_77925_n.containsKey(Integer.valueOf(par0)))
            {
                return ((Integer)field_77925_n.get(Integer.valueOf(par0))).intValue();
            }
            else
            {
                int j = calcPotionLiquidColor(getPotionEffects(par0, false));
                field_77925_n.put(Integer.valueOf(par0), Integer.valueOf(j));
                return j;
            }
        }
        else
        {
            // JAVADOC METHOD $$ func_77911_a
            return calcPotionLiquidColor(getPotionEffects(par0, par1));
        }
    }

    public static String func_77905_c(int par0)
    {
        int j = func_77909_a(par0);
        return potionPrefixes[j];
    }

    private static int func_77904_a(boolean par0, boolean par1, boolean par2, int par3, int par4, int par5, int par6)
    {
        int i1 = 0;

        if (par0)
        {
            i1 = isFlagUnset(par6, par4);
        }
        else if (par3 != -1)
        {
            if (par3 == 0 && countSetFlags(par6) == par4)
            {
                i1 = 1;
            }
            else if (par3 == 1 && countSetFlags(par6) > par4)
            {
                i1 = 1;
            }
            else if (par3 == 2 && countSetFlags(par6) < par4)
            {
                i1 = 1;
            }
        }
        else
        {
            i1 = isFlagSet(par6, par4);
        }

        if (par1)
        {
            i1 *= par5;
        }

        if (par2)
        {
            i1 *= -1;
        }

        return i1;
    }

    // JAVADOC METHOD $$ func_77907_h
    private static int countSetFlags(int par0)
    {
        int j;

        for (j = 0; par0 > 0; ++j)
        {
            par0 &= par0 - 1;
        }

        return j;
    }

    private static int parsePotionEffects(String par0Str, int par1, int par2, int par3)
    {
        if (par1 < par0Str.length() && par2 >= 0 && par1 < par2)
        {
            int l = par0Str.indexOf(124, par1);
            int i1;
            int j2;

            if (l >= 0 && l < par2)
            {
                i1 = parsePotionEffects(par0Str, par1, l - 1, par3);

                if (i1 > 0)
                {
                    return i1;
                }
                else
                {
                    j2 = parsePotionEffects(par0Str, l + 1, par2, par3);
                    return j2 > 0 ? j2 : 0;
                }
            }
            else
            {
                i1 = par0Str.indexOf(38, par1);

                if (i1 >= 0 && i1 < par2)
                {
                    j2 = parsePotionEffects(par0Str, par1, i1 - 1, par3);

                    if (j2 <= 0)
                    {
                        return 0;
                    }
                    else
                    {
                        int k2 = parsePotionEffects(par0Str, i1 + 1, par2, par3);
                        return k2 <= 0 ? 0 : (j2 > k2 ? j2 : k2);
                    }
                }
                else
                {
                    boolean flag = false;
                    boolean flag1 = false;
                    boolean flag2 = false;
                    boolean flag3 = false;
                    boolean flag4 = false;
                    byte b0 = -1;
                    int j1 = 0;
                    int k1 = 0;
                    int l1 = 0;

                    for (int i2 = par1; i2 < par2; ++i2)
                    {
                        char c0 = par0Str.charAt(i2);

                        if (c0 >= 48 && c0 <= 57)
                        {
                            if (flag)
                            {
                                k1 = c0 - 48;
                                flag1 = true;
                            }
                            else
                            {
                                j1 *= 10;
                                j1 += c0 - 48;
                                flag2 = true;
                            }
                        }
                        else if (c0 == 42)
                        {
                            flag = true;
                        }
                        else if (c0 == 33)
                        {
                            if (flag2)
                            {
                                l1 += func_77904_a(flag3, flag1, flag4, b0, j1, k1, par3);
                                flag3 = false;
                                flag4 = false;
                                flag = false;
                                flag1 = false;
                                flag2 = false;
                                k1 = 0;
                                j1 = 0;
                                b0 = -1;
                            }

                            flag3 = true;
                        }
                        else if (c0 == 45)
                        {
                            if (flag2)
                            {
                                l1 += func_77904_a(flag3, flag1, flag4, b0, j1, k1, par3);
                                flag3 = false;
                                flag4 = false;
                                flag = false;
                                flag1 = false;
                                flag2 = false;
                                k1 = 0;
                                j1 = 0;
                                b0 = -1;
                            }

                            flag4 = true;
                        }
                        else if (c0 != 61 && c0 != 60 && c0 != 62)
                        {
                            if (c0 == 43 && flag2)
                            {
                                l1 += func_77904_a(flag3, flag1, flag4, b0, j1, k1, par3);
                                flag3 = false;
                                flag4 = false;
                                flag = false;
                                flag1 = false;
                                flag2 = false;
                                k1 = 0;
                                j1 = 0;
                                b0 = -1;
                            }
                        }
                        else
                        {
                            if (flag2)
                            {
                                l1 += func_77904_a(flag3, flag1, flag4, b0, j1, k1, par3);
                                flag3 = false;
                                flag4 = false;
                                flag = false;
                                flag1 = false;
                                flag2 = false;
                                k1 = 0;
                                j1 = 0;
                                b0 = -1;
                            }

                            if (c0 == 61)
                            {
                                b0 = 0;
                            }
                            else if (c0 == 60)
                            {
                                b0 = 2;
                            }
                            else if (c0 == 62)
                            {
                                b0 = 1;
                            }
                        }
                    }

                    if (flag2)
                    {
                        l1 += func_77904_a(flag3, flag1, flag4, b0, j1, k1, par3);
                    }

                    return l1;
                }
            }
        }
        else
        {
            return 0;
        }
    }

    // JAVADOC METHOD $$ func_77917_b
    public static List getPotionEffects(int par0, boolean par1)
    {
        ArrayList arraylist = null;
        Potion[] apotion = Potion.potionTypes;
        int j = apotion.length;

        for (int k = 0; k < j; ++k)
        {
            Potion potion = apotion[k];

            if (potion != null && (!potion.isUsable() || par1))
            {
                String s = (String)potionRequirements.get(Integer.valueOf(potion.getId()));

                if (s != null)
                {
                    int l = parsePotionEffects(s, 0, s.length(), par0);

                    if (l > 0)
                    {
                        int i1 = 0;
                        String s1 = (String)potionAmplifiers.get(Integer.valueOf(potion.getId()));

                        if (s1 != null)
                        {
                            i1 = parsePotionEffects(s1, 0, s1.length(), par0);

                            if (i1 < 0)
                            {
                                i1 = 0;
                            }
                        }

                        if (potion.isInstant())
                        {
                            l = 1;
                        }
                        else
                        {
                            l = 1200 * (l * 3 + (l - 1) * 2);
                            l >>= i1;
                            l = (int)Math.round((double)l * potion.getEffectiveness());

                            if ((par0 & 16384) != 0)
                            {
                                l = (int)Math.round((double)l * 0.75D + 0.5D);
                            }
                        }

                        if (arraylist == null)
                        {
                            arraylist = new ArrayList();
                        }

                        PotionEffect potioneffect = new PotionEffect(potion.getId(), l, i1);

                        if ((par0 & 16384) != 0)
                        {
                            potioneffect.setSplashPotion(true);
                        }

                        arraylist.add(potioneffect);
                    }
                }
            }
        }

        return arraylist;
    }

    // JAVADOC METHOD $$ func_77906_a
    private static int brewBitOperations(int par0, int par1, boolean par2, boolean par3, boolean par4)
    {
        if (par4)
        {
            if (!checkFlag(par0, par1))
            {
                return 0;
            }
        }
        else if (par2)
        {
            par0 &= ~(1 << par1);
        }
        else if (par3)
        {
            if ((par0 & 1 << par1) == 0)
            {
                par0 |= 1 << par1;
            }
            else
            {
                par0 &= ~(1 << par1);
            }
        }
        else
        {
            par0 |= 1 << par1;
        }

        return par0;
    }

    // JAVADOC METHOD $$ func_77913_a
    public static int applyIngredient(int par0, String par1Str)
    {
        byte b0 = 0;
        int j = par1Str.length();
        boolean flag = false;
        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag3 = false;
        int k = 0;

        for (int l = b0; l < j; ++l)
        {
            char c0 = par1Str.charAt(l);

            if (c0 >= 48 && c0 <= 57)
            {
                k *= 10;
                k += c0 - 48;
                flag = true;
            }
            else if (c0 == 33)
            {
                if (flag)
                {
                    par0 = brewBitOperations(par0, k, flag2, flag1, flag3);
                    flag3 = false;
                    flag1 = false;
                    flag2 = false;
                    flag = false;
                    k = 0;
                }

                flag1 = true;
            }
            else if (c0 == 45)
            {
                if (flag)
                {
                    par0 = brewBitOperations(par0, k, flag2, flag1, flag3);
                    flag3 = false;
                    flag1 = false;
                    flag2 = false;
                    flag = false;
                    k = 0;
                }

                flag2 = true;
            }
            else if (c0 == 43)
            {
                if (flag)
                {
                    par0 = brewBitOperations(par0, k, flag2, flag1, flag3);
                    flag3 = false;
                    flag1 = false;
                    flag2 = false;
                    flag = false;
                    k = 0;
                }
            }
            else if (c0 == 38)
            {
                if (flag)
                {
                    par0 = brewBitOperations(par0, k, flag2, flag1, flag3);
                    flag3 = false;
                    flag1 = false;
                    flag2 = false;
                    flag = false;
                    k = 0;
                }

                flag3 = true;
            }
        }

        if (flag)
        {
            par0 = brewBitOperations(par0, k, flag2, flag1, flag3);
        }

        return par0 & 32767;
    }

    public static int func_77908_a(int par0, int par1, int par2, int par3, int par4, int par5)
    {
        return (checkFlag(par0, par1) ? 16 : 0) | (checkFlag(par0, par2) ? 8 : 0) | (checkFlag(par0, par3) ? 4 : 0) | (checkFlag(par0, par4) ? 2 : 0) | (checkFlag(par0, par5) ? 1 : 0);
    }

    static
    {
        potionRequirements.put(Integer.valueOf(Potion.regeneration.getId()), "0 & !1 & !2 & !3 & 0+6");
        sugarEffect = "-0+1-2-3&4-4+13";
        potionRequirements.put(Integer.valueOf(Potion.moveSpeed.getId()), "!0 & 1 & !2 & !3 & 1+6");
        magmaCreamEffect = "+0+1-2-3&4-4+13";
        potionRequirements.put(Integer.valueOf(Potion.fireResistance.getId()), "0 & 1 & !2 & !3 & 0+6");
        speckledMelonEffect = "+0-1+2-3&4-4+13";
        potionRequirements.put(Integer.valueOf(Potion.heal.getId()), "0 & !1 & 2 & !3");
        spiderEyeEffect = "-0-1+2-3&4-4+13";
        potionRequirements.put(Integer.valueOf(Potion.poison.getId()), "!0 & !1 & 2 & !3 & 2+6");
        fermentedSpiderEyeEffect = "-0+3-4+13";
        potionRequirements.put(Integer.valueOf(Potion.weakness.getId()), "!0 & !1 & !2 & 3 & 3+6");
        potionRequirements.put(Integer.valueOf(Potion.harm.getId()), "!0 & !1 & 2 & 3");
        potionRequirements.put(Integer.valueOf(Potion.moveSlowdown.getId()), "!0 & 1 & !2 & 3 & 3+6");
        blazePowderEffect = "+0-1-2+3&4-4+13";
        potionRequirements.put(Integer.valueOf(Potion.damageBoost.getId()), "0 & !1 & !2 & 3 & 3+6");
        goldenCarrotEffect = "-0+1+2-3+13&4-4";
        potionRequirements.put(Integer.valueOf(Potion.nightVision.getId()), "!0 & 1 & 2 & !3 & 2+6");
        potionRequirements.put(Integer.valueOf(Potion.invisibility.getId()), "!0 & 1 & 2 & 3 & 2+6");
        field_151423_m = "+0-1+2+3+13&4-4";
        potionRequirements.put(Integer.valueOf(Potion.waterBreathing.getId()), "0 & !1 & 2 & 3 & 2+6");
        glowstoneEffect = "+5-6-7";
        potionAmplifiers.put(Integer.valueOf(Potion.moveSpeed.getId()), "5");
        potionAmplifiers.put(Integer.valueOf(Potion.digSpeed.getId()), "5");
        potionAmplifiers.put(Integer.valueOf(Potion.damageBoost.getId()), "5");
        potionAmplifiers.put(Integer.valueOf(Potion.regeneration.getId()), "5");
        potionAmplifiers.put(Integer.valueOf(Potion.harm.getId()), "5");
        potionAmplifiers.put(Integer.valueOf(Potion.heal.getId()), "5");
        potionAmplifiers.put(Integer.valueOf(Potion.resistance.getId()), "5");
        potionAmplifiers.put(Integer.valueOf(Potion.poison.getId()), "5");
        redstoneEffect = "-5+6-7";
        gunpowderEffect = "+14&13-13";
        field_77925_n = new HashMap();
        potionPrefixes = new String[] {"potion.prefix.mundane", "potion.prefix.uninteresting", "potion.prefix.bland", "potion.prefix.clear", "potion.prefix.milky", "potion.prefix.diffuse", "potion.prefix.artless", "potion.prefix.thin", "potion.prefix.awkward", "potion.prefix.flat", "potion.prefix.bulky", "potion.prefix.bungling", "potion.prefix.buttered", "potion.prefix.smooth", "potion.prefix.suave", "potion.prefix.debonair", "potion.prefix.thick", "potion.prefix.elegant", "potion.prefix.fancy", "potion.prefix.charming", "potion.prefix.dashing", "potion.prefix.refined", "potion.prefix.cordial", "potion.prefix.sparkling", "potion.prefix.potent", "potion.prefix.foul", "potion.prefix.odorless", "potion.prefix.rank", "potion.prefix.harsh", "potion.prefix.acrid", "potion.prefix.gross", "potion.prefix.stinky"};
    }
}