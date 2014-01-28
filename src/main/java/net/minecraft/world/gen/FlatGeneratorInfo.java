package net.minecraft.world.gen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;

public class FlatGeneratorInfo
{
    // JAVADOC FIELD $$ field_82655_a
    private final List flatLayers = new ArrayList();
    // JAVADOC FIELD $$ field_82653_b
    private final Map worldFeatures = new HashMap();
    private int biomeToUse;
    private static final String __OBFID = "CL_00000440";

    // JAVADOC METHOD $$ func_82648_a
    public int getBiome()
    {
        return this.biomeToUse;
    }

    // JAVADOC METHOD $$ func_82647_a
    public void setBiome(int par1)
    {
        this.biomeToUse = par1;
    }

    // JAVADOC METHOD $$ func_82644_b
    public Map getWorldFeatures()
    {
        return this.worldFeatures;
    }

    // JAVADOC METHOD $$ func_82650_c
    public List getFlatLayers()
    {
        return this.flatLayers;
    }

    public void func_82645_d()
    {
        int i = 0;
        FlatLayerInfo flatlayerinfo;

        for (Iterator iterator = this.flatLayers.iterator(); iterator.hasNext(); i += flatlayerinfo.getLayerCount())
        {
            flatlayerinfo = (FlatLayerInfo)iterator.next();
            flatlayerinfo.setMinY(i);
        }
    }

    public String toString()
    {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append(2);
        stringbuilder.append(";");
        int i;

        for (i = 0; i < this.flatLayers.size(); ++i)
        {
            if (i > 0)
            {
                stringbuilder.append(",");
            }

            stringbuilder.append(((FlatLayerInfo)this.flatLayers.get(i)).toString());
        }

        stringbuilder.append(";");
        stringbuilder.append(this.biomeToUse);

        if (!this.worldFeatures.isEmpty())
        {
            stringbuilder.append(";");
            i = 0;
            Iterator iterator = this.worldFeatures.entrySet().iterator();

            while (iterator.hasNext())
            {
                Entry entry = (Entry)iterator.next();

                if (i++ > 0)
                {
                    stringbuilder.append(",");
                }

                stringbuilder.append(((String)entry.getKey()).toLowerCase());
                Map map = (Map)entry.getValue();

                if (!map.isEmpty())
                {
                    stringbuilder.append("(");
                    int j = 0;
                    Iterator iterator1 = map.entrySet().iterator();

                    while (iterator1.hasNext())
                    {
                        Entry entry1 = (Entry)iterator1.next();

                        if (j++ > 0)
                        {
                            stringbuilder.append(" ");
                        }

                        stringbuilder.append((String)entry1.getKey());
                        stringbuilder.append("=");
                        stringbuilder.append((String)entry1.getValue());
                    }

                    stringbuilder.append(")");
                }
            }
        }
        else
        {
            stringbuilder.append(";");
        }

        return stringbuilder.toString();
    }

    private static FlatLayerInfo func_82646_a(String par0Str, int par1)
    {
        String[] astring = par0Str.split("x", 2);
        int j = 1;
        int l = 0;

        if (astring.length == 2)
        {
            try
            {
                j = Integer.parseInt(astring[0]);

                if (par1 + j >= 256)
                {
                    j = 256 - par1;
                }

                if (j < 0)
                {
                    j = 0;
                }
            }
            catch (Throwable throwable)
            {
                return null;
            }
        }

        int k;

        try
        {
            String s1 = astring[astring.length - 1];
            astring = s1.split(":", 2);
            k = Integer.parseInt(astring[0]);

            if (astring.length > 1)
            {
                l = Integer.parseInt(astring[1]);
            }

            if (Block.func_149729_e(k) == Blocks.air)
            {
                k = 0;
                l = 0;
            }

            if (l < 0 || l > 15)
            {
                l = 0;
            }
        }
        catch (Throwable throwable1)
        {
            return null;
        }

        FlatLayerInfo flatlayerinfo = new FlatLayerInfo(j, Block.func_149729_e(k), l);
        flatlayerinfo.setMinY(par1);
        return flatlayerinfo;
    }

    private static List func_82652_b(String par0Str)
    {
        if (par0Str != null && par0Str.length() >= 1)
        {
            ArrayList arraylist = new ArrayList();
            String[] astring = par0Str.split(",");
            int i = 0;
            String[] astring1 = astring;
            int j = astring.length;

            for (int k = 0; k < j; ++k)
            {
                String s1 = astring1[k];
                FlatLayerInfo flatlayerinfo = func_82646_a(s1, i);

                if (flatlayerinfo == null)
                {
                    return null;
                }

                arraylist.add(flatlayerinfo);
                i += flatlayerinfo.getLayerCount();
            }

            return arraylist;
        }
        else
        {
            return null;
        }
    }

    public static FlatGeneratorInfo createFlatGeneratorFromString(String par0Str)
    {
        if (par0Str == null)
        {
            return getDefaultFlatGenerator();
        }
        else
        {
            String[] astring = par0Str.split(";", -1);
            int i = astring.length == 1 ? 0 : MathHelper.parseIntWithDefault(astring[0], 0);

            if (i >= 0 && i <= 2)
            {
                FlatGeneratorInfo flatgeneratorinfo = new FlatGeneratorInfo();
                int j = astring.length == 1 ? 0 : 1;
                List list = func_82652_b(astring[j++]);

                if (list != null && !list.isEmpty())
                {
                    flatgeneratorinfo.getFlatLayers().addAll(list);
                    flatgeneratorinfo.func_82645_d();
                    int k = BiomeGenBase.plains.biomeID;

                    if (i > 0 && astring.length > j)
                    {
                        k = MathHelper.parseIntWithDefault(astring[j++], k);
                    }

                    flatgeneratorinfo.setBiome(k);

                    if (i > 0 && astring.length > j)
                    {
                        String[] astring1 = astring[j++].toLowerCase().split(",");
                        String[] astring2 = astring1;
                        int l = astring1.length;

                        for (int i1 = 0; i1 < l; ++i1)
                        {
                            String s1 = astring2[i1];
                            String[] astring3 = s1.split("\\(", 2);
                            HashMap hashmap = new HashMap();

                            if (astring3[0].length() > 0)
                            {
                                flatgeneratorinfo.getWorldFeatures().put(astring3[0], hashmap);

                                if (astring3.length > 1 && astring3[1].endsWith(")") && astring3[1].length() > 1)
                                {
                                    String[] astring4 = astring3[1].substring(0, astring3[1].length() - 1).split(" ");

                                    for (int j1 = 0; j1 < astring4.length; ++j1)
                                    {
                                        String[] astring5 = astring4[j1].split("=", 2);

                                        if (astring5.length == 2)
                                        {
                                            hashmap.put(astring5[0], astring5[1]);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else
                    {
                        flatgeneratorinfo.getWorldFeatures().put("village", new HashMap());
                    }

                    return flatgeneratorinfo;
                }
                else
                {
                    return getDefaultFlatGenerator();
                }
            }
            else
            {
                return getDefaultFlatGenerator();
            }
        }
    }

    public static FlatGeneratorInfo getDefaultFlatGenerator()
    {
        FlatGeneratorInfo flatgeneratorinfo = new FlatGeneratorInfo();
        flatgeneratorinfo.setBiome(BiomeGenBase.plains.biomeID);
        flatgeneratorinfo.getFlatLayers().add(new FlatLayerInfo(1, Blocks.bedrock));
        flatgeneratorinfo.getFlatLayers().add(new FlatLayerInfo(2, Blocks.dirt));
        flatgeneratorinfo.getFlatLayers().add(new FlatLayerInfo(1, Blocks.grass));
        flatgeneratorinfo.func_82645_d();
        flatgeneratorinfo.getWorldFeatures().put("village", new HashMap());
        return flatgeneratorinfo;
    }
}