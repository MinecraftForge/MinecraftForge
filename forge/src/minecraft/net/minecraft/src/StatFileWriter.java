package net.minecraft.src;

import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import argo.jdom.JsonStringNode;
import argo.saj.InvalidSyntaxException;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class StatFileWriter
{
    private Map field_25102_a = new HashMap();
    private Map field_25101_b = new HashMap();
    private boolean field_27189_c = false;
    private StatsSyncher statsSyncher;

    public StatFileWriter(Session par1Session, File par2File)
    {
        File var3 = new File(par2File, "stats");

        if (!var3.exists())
        {
            var3.mkdir();
        }

        File[] var4 = par2File.listFiles();
        int var5 = var4.length;

        for (int var6 = 0; var6 < var5; ++var6)
        {
            File var7 = var4[var6];

            if (var7.getName().startsWith("stats_") && var7.getName().endsWith(".dat"))
            {
                File var8 = new File(var3, var7.getName());

                if (!var8.exists())
                {
                    System.out.println("Relocating " + var7.getName());
                    var7.renameTo(var8);
                }
            }
        }

        this.statsSyncher = new StatsSyncher(par1Session, this, var3);
    }

    public void readStat(StatBase par1StatBase, int par2)
    {
        this.writeStatToMap(this.field_25101_b, par1StatBase, par2);
        this.writeStatToMap(this.field_25102_a, par1StatBase, par2);
        this.field_27189_c = true;
    }

    private void writeStatToMap(Map par1Map, StatBase par2StatBase, int par3)
    {
        Integer var4 = (Integer)par1Map.get(par2StatBase);
        int var5 = var4 == null ? 0 : var4.intValue();
        par1Map.put(par2StatBase, Integer.valueOf(var5 + par3));
    }

    public Map func_27176_a()
    {
        return new HashMap(this.field_25101_b);
    }

    public void func_27179_a(Map par1Map)
    {
        if (par1Map != null)
        {
            this.field_27189_c = true;
            Iterator var2 = par1Map.keySet().iterator();

            while (var2.hasNext())
            {
                StatBase var3 = (StatBase)var2.next();
                this.writeStatToMap(this.field_25101_b, var3, ((Integer)par1Map.get(var3)).intValue());
                this.writeStatToMap(this.field_25102_a, var3, ((Integer)par1Map.get(var3)).intValue());
            }
        }
    }

    public void func_27180_b(Map par1Map)
    {
        if (par1Map != null)
        {
            Iterator var2 = par1Map.keySet().iterator();

            while (var2.hasNext())
            {
                StatBase var3 = (StatBase)var2.next();
                Integer var4 = (Integer)this.field_25101_b.get(var3);
                int var5 = var4 == null ? 0 : var4.intValue();
                this.field_25102_a.put(var3, Integer.valueOf(((Integer)par1Map.get(var3)).intValue() + var5));
            }
        }
    }

    public void func_27187_c(Map par1Map)
    {
        if (par1Map != null)
        {
            this.field_27189_c = true;
            Iterator var2 = par1Map.keySet().iterator();

            while (var2.hasNext())
            {
                StatBase var3 = (StatBase)var2.next();
                this.writeStatToMap(this.field_25101_b, var3, ((Integer)par1Map.get(var3)).intValue());
            }
        }
    }

    public static Map func_27177_a(String par0Str)
    {
        HashMap var1 = new HashMap();

        try
        {
            String var2 = "local";
            StringBuilder var3 = new StringBuilder();
            JsonRootNode var4 = (new JdomParser()).parse(par0Str);
            List var5 = var4.getArrayNode(new Object[] {"stats-change"});
            Iterator var6 = var5.iterator();

            while (var6.hasNext())
            {
                JsonNode var7 = (JsonNode)var6.next();
                Map var8 = var7.getFields();
                Entry var9 = (Entry)var8.entrySet().iterator().next();
                int var10 = Integer.parseInt(((JsonStringNode)var9.getKey()).getText());
                int var11 = Integer.parseInt(((JsonNode)var9.getValue()).getText());
                StatBase var12 = StatList.getOneShotStat(var10);

                if (var12 == null)
                {
                    System.out.println(var10 + " is not a valid stat");
                }
                else
                {
                    var3.append(StatList.getOneShotStat(var10).statGuid).append(",");
                    var3.append(var11).append(",");
                    var1.put(var12, Integer.valueOf(var11));
                }
            }

            MD5String var14 = new MD5String(var2);
            String var15 = var14.getMD5String(var3.toString());

            if (!var15.equals(var4.getStringValue(new Object[] {"checksum"})))
            {
                System.out.println("CHECKSUM MISMATCH");
                return null;
            }
        }
        catch (InvalidSyntaxException var13)
        {
            var13.printStackTrace();
        }

        return var1;
    }

    public static String func_27185_a(String par0Str, String par1Str, Map par2Map)
    {
        StringBuilder var3 = new StringBuilder();
        StringBuilder var4 = new StringBuilder();
        boolean var5 = true;
        var3.append("{\r\n");

        if (par0Str != null && par1Str != null)
        {
            var3.append("  \"user\":{\r\n");
            var3.append("    \"name\":\"").append(par0Str).append("\",\r\n");
            var3.append("    \"sessionid\":\"").append(par1Str).append("\"\r\n");
            var3.append("  },\r\n");
        }

        var3.append("  \"stats-change\":[");
        Iterator var6 = par2Map.keySet().iterator();

        while (var6.hasNext())
        {
            StatBase var7 = (StatBase)var6.next();

            if (!var5)
            {
                var3.append("},");
            }
            else
            {
                var5 = false;
            }

            var3.append("\r\n    {\"").append(var7.statId).append("\":").append(par2Map.get(var7));
            var4.append(var7.statGuid).append(",");
            var4.append(par2Map.get(var7)).append(",");
        }

        if (!var5)
        {
            var3.append("}");
        }

        MD5String var8 = new MD5String(par1Str);
        var3.append("\r\n  ],\r\n");
        var3.append("  \"checksum\":\"").append(var8.getMD5String(var4.toString())).append("\"\r\n");
        var3.append("}");
        return var3.toString();
    }

    /**
     * Returns true if the achievement has been unlocked.
     */
    public boolean hasAchievementUnlocked(Achievement par1Achievement)
    {
        return this.field_25102_a.containsKey(par1Achievement);
    }

    /**
     * Returns true if the parent has been unlocked, or there is no parent
     */
    public boolean canUnlockAchievement(Achievement par1Achievement)
    {
        return par1Achievement.parentAchievement == null || this.hasAchievementUnlocked(par1Achievement.parentAchievement);
    }

    public int writeStat(StatBase par1StatBase)
    {
        Integer var2 = (Integer)this.field_25102_a.get(par1StatBase);
        return var2 == null ? 0 : var2.intValue();
    }

    public void func_27175_b() {}

    public void syncStats()
    {
        this.statsSyncher.syncStatsFileWithMap(this.func_27176_a());
    }

    public void func_27178_d()
    {
        if (this.field_27189_c && this.statsSyncher.func_27420_b())
        {
            this.statsSyncher.beginSendStats(this.func_27176_a());
        }

        this.statsSyncher.func_27425_c();
    }
}
